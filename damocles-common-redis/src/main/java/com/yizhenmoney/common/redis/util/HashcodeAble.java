package com.yizhenmoney.common.redis.util;

public interface HashcodeAble {

	/**
	 * 获取用于判重的数组
	 * 
	 * @return
	 */
	Object[] genEffectArray();
}
