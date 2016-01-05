package com.meishi.front.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class GenerateLinkUtils {

//	/**
//	 * 生成帐户激活、重新设置密码的链接
//	 */
//
	public static final String CHECK_CODE = "checkCode";
	private static final String EMAIL_URL="/safecenter/email/validCode";
	private static final String ModifyPhone_URL="/safecenter/phone/validCode";
	private static final String REGIST_URL="/register/validCode";
//
	/**
	 * 生成帐户激活链接
	 */
	public static String generateActivateLink(String domain, String email,
			String generateCode) {
		StringBuffer sb = new StringBuffer(domain);
		return sb.append(REGIST_URL).append("?").append(CHECK_CODE).append("=")
				.append(generateCode).toString();
	}
//
	/**
	 * 生成重设密码的链接
	 */
	public static String generateResetPwdLink(String domain, String email,
			String generateCode) {
		StringBuffer sb = new StringBuffer(domain);
		return sb.append("/member/resetPwd?").append(CHECK_CODE)
				.append("=").append(generateCode).toString();
	}
//
	/**
	 * 生成修改密码的链接
	 *
	 * @param domain
	 * @param generateCode
	 * @return
	 */
	public static String generateChangePwdLink(String domain, String email,
			String generateCode) {
		StringBuffer sb = new StringBuffer(domain);
		return sb.append("/common/account/security/checkPwdByE?email=")
				.append(email).append("&").append(CHECK_CODE).append("=")
				.append(generateCode).toString();
	}
//
//	/**
//	 * 生成绑定邮箱的链接
//	 *
//	 * @param domain
//	 * @param generateCode
//	 * @return
//	 */
//	public static String generateVerifyEmailLink(String domain,
//			String generateCode) {
//		StringBuffer sb = new StringBuffer(domain);
//		return sb.append("/account/email/verifyEmail")
//				.append(generateCode).toString();
//	}
//
	/**
	 * 生成修改手机号码的链接
	 *
	 * @param domain
	 * @param generateCode
	 * @return
	 */
	public static String generateModifyPhoneLink(String domain,
			String generateCode) {
		StringBuffer sb = new StringBuffer(domain);
		return sb.append(ModifyPhone_URL).append("?").append(CHECK_CODE)
				.append("=").append(generateCode).toString();
	}
//
	/**
	 * 生成修改支付密码密码的链接
	 *
	 * @param domain
	 * @param target
	 * @param generateCode
	 * @return
	 */
	public static String generateChangePayPwdLink(String domain, String email,
			String generateCode) {
		StringBuffer sb = new StringBuffer(domain);
		return sb.append("/account/security/checkPayPwdByE?email=")
				.append(email).append("&").append(CHECK_CODE).append("=")
				.append(generateCode).toString();
	}
//

//
//	/**
//	 * 验证校验码是否和注册时发送的验证码一致
//	 *
//	 * @param Member
//	 *            要激活的帐户
//	 * @param checkcode
//	 *            注册时发送的校验码
//	 * @return 如果一致返回true，否则返回false
//	 */
//	public static boolean verifyCheckcode(Map map, ServletRequest request) {
//		String checkCode = request.getParameter(CHECK_CODE);
//		return generateCheckcode(map).equals(checkCode);
//	}

	/**
	 * 生成绑定邮箱的链接
	 *
	 * @param domain
	 * @param generateCode
	 * @return
	 */
	public static String generateVerifyEmailLink(String domain,
			String generateCode) {
		StringBuffer sb = new StringBuffer(domain);
		return sb.append(EMAIL_URL).append("?").append(CHECK_CODE)
				.append("=").append(generateCode).toString();
	}


	/**
	 * 生成验证帐户的MD5校验码
	 *
	 * @param map 要激活的帐户
	 * @return 将id和随即码组合后，通过md5加密后的16进制格式的字符串
	 */
	public static String generateCheckcode(Map map) {
		String memberName = (String) map.get("id");
		String randomCode = (String) map.get("random_code");
		return md5(memberName + ":" + randomCode);
	}

	private static String md5(String string) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("md5");
			md.update(string.getBytes());
			byte[] md5Bytes = md.digest();
			return bytes2Hex(md5Bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String bytes2Hex(byte[] byteArray) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] >= 0 && byteArray[i] < 16) {
				strBuf.append("0");
			}
			strBuf.append(Integer.toHexString(byteArray[i] & 0xFF));
		}
		return strBuf.toString();
	}
}
