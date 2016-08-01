package com.yizhenmoney.common.redis.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yizhenmoney.common.redis.exception.ResubmitException;
import com.yizhenmoney.common.redis.service.RedisLockService;
import com.yizhenmoney.common.redis.util.HashCodeUtil;

/**
 * 
 * @description 重复提交过滤切面
 * @author sm
 */
@Aspect
public class RefuseResubmitAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefuseResubmitAspect.class);

	private RedisLockService redisLockService;
	

	public RefuseResubmitAspect(RedisLockService redisLockService) {
		this.redisLockService = redisLockService;
	}

	/**
	 * 
	 * @description 切点
	 * @author sm
	 */
	@Pointcut("@annotation(com.yizhenmoney.common.redis.aop.RefuseResubmit)")
	public void refuseResubmit() {

	}

	/**
	 * 
	 * @description 拒绝重复提交
	 * @param jp
	 *            连接点
	 * @author sm
	 */
	@Before("refuseResubmit()")
	public void refuseResubmitOperate(JoinPoint jp) {
		execFilter(jp);
	}

	/**
	 * 
	 * @description 获取判重点
	 * @param jp
	 *            连接点
	 * @return 备注
	 * @author sm
	 */
	public String[] getFactors(JoinPoint jp) {
		RefuseResubmit refuseResubmit = getAnnotation(jp);
		if (refuseResubmit != null) {
			return refuseResubmit.factors();
		}
		return new String[] {};
	}
	
	public String[] getExcludes(JoinPoint jp) {
		RefuseResubmit refuseResubmit = getAnnotation(jp);
		if (refuseResubmit != null) {
			return refuseResubmit.excludes();
		}
		return new String[] {};
	}
	
	public int getInterval(JoinPoint jp) {
		RefuseResubmit refuseResubmit = getAnnotation(jp);
		if (refuseResubmit != null) {
			return refuseResubmit.interval();
		}
		return 1;
	}

	private RefuseResubmit getAnnotation(JoinPoint jp) {
		MethodSignature signature = (MethodSignature) jp.getSignature();
		Method method = signature.getMethod();
		RefuseResubmit refuseResubmit = method.getAnnotation(RefuseResubmit.class);
		return refuseResubmit;
	}

	private String getSignature(JoinPoint jp) {
		return jp.getSignature().toLongString();
	}

	/**
	 * 检查
	 * 
	 * @param jp
	 */
	public void execFilter(JoinPoint jp) {
		String[] factors = getFactors(jp);
		Object[] argValues = jp.getArgs();
		MethodSignature signature = (MethodSignature) jp.getSignature();
		String[] argNames = signature.getParameterNames();
		List<String> argNameList = Arrays.asList(argNames);
		List<Object> effectValues = new ArrayList<Object>();
		effectValues.add(signature.toShortString());
		for (int i = 0; i < factors.length; i++) {
			int j = argNameList.indexOf(factors[i]);
			if (j != -1 && null != argValues[j]) {
				effectValues.add(argValues[j]);
			}
		}
		String[] excludes = getExcludes(jp);
		checkRoundTrips(hashRequestBody(effectValues,excludes), jp);
	}

	private int hashRequestBody(List<Object> effectValues,String[] excludes) {
		return HashCodeUtil.getHashCode(effectValues,excludes);
	}

	private void checkRoundTrips(int jopHashcode, JoinPoint jp) {
		String hashcodeString = jopHashcode + "";
		int interval = getInterval(jp);
		boolean result = redisLockService.lock("ResubmitToekn", hashcodeString,interval, TimeUnit.SECONDS);
		if (!result) {
			LOGGER.warn(interval+"秒内重复提交" + getSignature(jp));
			throw new ResubmitException(interval+"秒内重复提交");
		}
	}
	
	
}
