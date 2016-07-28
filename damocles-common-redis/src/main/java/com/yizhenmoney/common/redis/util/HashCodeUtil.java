package com.yizhenmoney.common.redis.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 计算hashcode
 * 
 * @author sm
 */
public class HashCodeUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashCodeUtil.class);

	private static Random random = new Random(10000L);

	// public static int getHashCode(Object... params) {
	// HashCodeBuilder builder = new HashCodeBuilder(17, 37);
	// for (Object param : params) {
	// builder.append(param);
	// }
	// return builder.toHashCode();
	// }

	public static int getHashCode(List<? extends Object> params,String[] excludes) {
		HashCodeBuilder builder = new HashCodeBuilder(17, 37);
		try {
			for (Object param : params) {
				if (param instanceof String || param instanceof Number || param instanceof Boolean || param instanceof Character) {
					builder.append(param);
				} else {
					builder.append(getHashCode(Arrays.asList(getFieldValues(param,excludes)),excludes));
				}
			}
			return builder.toHashCode();
		} catch (Exception e) {
			LOGGER.warn("hashError", e);
		}
		return random.nextInt();
	}

	public static String getToken32(String... strings) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append("+");
			sb.append(s);
		}
		return DESCoder.md5ToHex(sb.toString());

	}

	public static Object[] getFieldValues(Object object,String[] excludes) throws Exception {
		if (null == object)
			return null;
		Method[] methods = object.getClass().getMethods();
		List<Object> fieldValueList = new ArrayList<Object>();
		
		for (Method f : methods) {
			if (( f.getName().startsWith("get") || f.getName().startsWith("is")) && !f.getName().equals("getClass") && !ArrayUtils.contains(excludes, f.getName())) {
				Object object2 = f.invoke(object);
				if (null != object2 && object2.hashCode() !=  0)
					fieldValueList.add(object2);
			}
		}

		return fieldValueList.toArray();
	}

}
