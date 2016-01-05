package com.meishi.front.common.util;

import com.meishi.model.Member;
import com.meishi.util.Digests;
import com.meishi.util.Encodes;


public class PwdUtils {

	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	private static final int SALT_SIZE = 8;

	/**
     * 验证密码
     *
     * @return
     */
    public static boolean checkPwd(String plainPwd, Member member) {
        byte[] salt = Encodes.decodeHex(member.getStr("salt"));
        byte[] hashPassword = Digests.sha1(plainPwd.getBytes(), salt,
                HASH_INTERATIONS);
        if (Encodes.encodeHex(hashPassword).equals(member.get("password"))) {
            return true;
        }
        return false;
    }

    /**
     * 验证支付密码
     *
     * @return
     */
    public static boolean checkPayPwd(String plainPwd, Member member) {
        byte[] salt = Encodes.decodeHex(member.getStr("pay_salt"));
        byte[] hashPassword = Digests.sha1(plainPwd.getBytes(), salt,
                HASH_INTERATIONS);
        if (Encodes.encodeHex(hashPassword).equals(member.get("pay_pwd"))) {
            return true;
        }
        return false;
    }

	/**
	 * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
	 */
	public static void entryptPayPassword(String plainPwd, Member member) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		member.set("salt", Encodes.encodeHex(salt));

		byte[] hashPassword = Digests.sha1(plainPwd.getBytes(), salt,
				HASH_INTERATIONS);
		member.set("password", Encodes.encodeHex(hashPassword));
	}

	public static void main(String[] gg){
		byte[] salt = Encodes.decodeHex("54a6cb88d5380c58");
		byte[] hashPassword = Digests.sha1("123456".getBytes(), salt,
				HASH_INTERATIONS);
		System.out.print(Encodes.encodeHex(hashPassword));
	}
}
