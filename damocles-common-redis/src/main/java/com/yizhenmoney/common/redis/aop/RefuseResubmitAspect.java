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

import com.yizhenmoney.common.redis.service.RedisLockService;

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
	 * @description 用户操作日志通知，围绕方法会记录结果
	 * @param jp
	 *            连接点
	 * @author sm
	 * @throws Throwable
	 */
	@Before("refuseResubmit()")
	public void refuseResubmitOperate(JoinPoint jp) {
		execFilter(jp);
	}

	/**
	 * 
	 * @description 获取备注
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
		checkRoundTrips(hashRequestBody(effectValues), jp);
	}

	private int hashRequestBody(List<Object> effectValues) {
		return HashCodeUtil.getHashCode(effectValues);
	}

	private void checkRoundTrips(int jopHashcode, JoinPoint jp) {
		String hashcodeString = jopHashcode + "";
		boolean result = redisLockService.lock("ResubmitToekn", hashcodeString, 60, TimeUnit.SECONDS);
		if (!result) {
			LOGGER.warn("时间周期内重复提交" + getSignature(jp));
			throw new RuntimeException("时间周期内重复提交");
		}
	}
}
