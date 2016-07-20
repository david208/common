package com.yizhenmoney.common.redis.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 计算hashcode
 * 
 * @author sm
 */
public class HashCodeUtil {

//	public static int getHashCode(Object... params) {
//		HashCodeBuilder builder = new HashCodeBuilder(17, 37);
//		for (Object param : params) {
//			builder.append(param);
//		}
//		return builder.toHashCode();
//	}

	public static int getHashCode(List<? extends Object> params) {
		HashCodeBuilder builder = new HashCodeBuilder(17, 37);
		for (Object param : params) {
			if (param instanceof HashcodeAble) {
				builder.append(getHashCode(Arrays.asList(((HashcodeAble) param).genEffectArray())));
			} else {
				builder.append(param);
			}
		}
		return builder.toHashCode();
	}

	public static String getToken32(String... strings) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append("+");
			sb.append(s);
		}
		return DESCoder.md5ToHex(sb.toString());

	}

}
