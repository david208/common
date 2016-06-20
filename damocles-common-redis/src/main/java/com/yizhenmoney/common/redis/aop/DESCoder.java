package com.yizhenmoney.common.redis.aop;

import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public abstract class DESCoder extends Coder {
	/**
	 * ALGORITHM 算法 <br>
	 * 可替换为以下任意一种算法，同时key值的size相应改变。
	 * 
	 * <pre>
	 * DES                  key size must be equal to 56 
	 * DESede(TripleDES)    key size must be equal to 112 or 168 
	 * AES                  key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available 
	 * Blowfish             key size must be multiple of 8, and can only range from 32 to 448 (inclusive) 
	 * RC2                  key size must be between 40 and 1024 bits 
	 * RC4(ARCFOUR)         key size must be between 40 and 1024 bits
	 * </pre>
	 * 
	 * 在Key toKey(byte[] key)方法中使用下述代码
	 * <code>SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);</code> 替换
	 * <code> 
	 * DESKeySpec dks = new DESKeySpec(key); 
	 * SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM); 
	 * SecretKey secretKey = keyFactory.generateSecret(dks); 
	 * </code>
	 */
	public static final String ALGORITHM = "DES";

	/**
	 * 转换密钥<br>
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static Key toKey(byte[] key) throws Exception {
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey secretKey = keyFactory.generateSecret(dks);

		// 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码
		// SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);

		return secretKey;
	}

	/**
	 * 解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(String data, String key) throws Exception {
		Key k = toKey(key.getBytes(CharEncoding.UTF_8));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);
		return cipher.doFinal(DESCoder.decryptBASE64(data));
	}

	/**
	 * 加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(byte[] data, String key) throws Exception {
		Key k = toKey(key.getBytes(CharEncoding.UTF_8));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		return DESCoder.encryptBASE64(cipher.doFinal(data));
	}

	public static String encryptString(String data, String key) throws Exception {
		return encrypt(data.getBytes(CharEncoding.UTF_8), key);
	}

	public static String decryptString(String data, String key) throws Exception {
		return new String(decrypt(data, key), CharEncoding.UTF_8);
	}

	public static String md5(String s) throws Exception {
		MessageDigest mdInst = MessageDigest.getInstance("MD5");
		byte[] input = s.getBytes(CharEncoding.UTF_8);
		mdInst.update(input);
		return Base64.encodeBase64String(mdInst.digest());
	}
	
	public static String md5ToHex(String s) throws Exception {
		MessageDigest mdInst = MessageDigest.getInstance("MD5");
		byte[] input = s.getBytes(CharEncoding.UTF_8);
		mdInst.update(input);
		return Hex.encodeHexString(mdInst.digest());
	}
	
	public static String encryptToHex(String data, String key) throws Exception {
		Key k = toKey(key.getBytes(CharEncoding.UTF_8));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		return Hex.encodeHexString(cipher.doFinal(data.getBytes(CharEncoding.UTF_8)));
	}
	
	public static String decryptFromHex(String data, String key) throws Exception {
		
		Key k = toKey(key.getBytes(CharEncoding.UTF_8));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);
		
		return new String(cipher.doFinal(parseHexStr2Byte(data)),CharEncoding.UTF_8);
	}

	/**
	 * 生成密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String initKey() throws Exception {
		return initKey(null);
	}

	/**
	 * 生成密钥
	 * 
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	public static String initKey(String seed) throws Exception {
		SecureRandom secureRandom = null;

		if (seed != null) {
			secureRandom = new SecureRandom(decryptBASE64(seed));
		} else {
			secureRandom = new SecureRandom();
		}

		KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
		kg.init(secureRandom);

		SecretKey secretKey = kg.generateKey();

		return encryptBASE64(secretKey.getEncoded());
	}

}
