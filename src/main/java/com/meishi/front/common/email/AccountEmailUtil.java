package com.meishi.front.common.email;


import com.meishi.front.common.AppContextData;
import com.meishi.front.common.util.GenerateLinkUtils;
import com.meishi.front.ext.email.SendEmail;
import com.meishi.util.WebUtil;

import java.util.HashMap;
import java.util.Map;

public class AccountEmailUtil {
	public static final AccountEmailUtil service = new AccountEmailUtil();

	private static final String EMAIL_MANAGER = "meishi_37@163.com";

	/**
	 * 激活帐号
	 *
	 * @param map
	 */
	public void sendAccountActivateEmail(Map map) {
		SendEmail email = new SendEmail("smtp.163.com", 25, 0, true,
				"meishi_37@163.com", "meishi", true);

		try {
			Map<String, Object> content = new HashMap<String, Object>();
			content.put("userName", map.get("username"));
			content.put("register_url", GenerateLinkUtils.generateActivateLink(
					AppContextData.DOMAIN,
					(String) map.get("email"), (String) map.get("operateCode")));
			
			String content_html = WebUtil.generateContent(
					"/oldfront/user/email/activeEmail.html", content);

			email.sendEmail(EMAIL_MANAGER, "37美食", (String) map.get("email"),
					"账户激活", content_html);
			System.out.println("send out successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("send fail");
		}
	}
//
	/**
	 * 重置密码
	 *
	 * @param map
	 *            用户对象
	 */
	public void resetPwdEmail(Map map) {
		SendEmail email = new SendEmail("smtp.163.com", 25, 0, true,
				"meishi_37@163.com", "meishi", true);

		try {
			Map<String, Object> content = new HashMap<String, Object>();
			content.put("userName", map.get("username"));
			content.put("resetPwd_url", GenerateLinkUtils.generateResetPwdLink(
					AppContextData.DOMAIN,
					(String) map.get("email"), (String) map.get("operateCode")));
			String content_html = WebUtil.generateContent(
					"/oldfront/user/email/resetPwd.html", content);

			email.sendEmail(EMAIL_MANAGER, "37美食", (String) map.get("email"),
					"重置密码", content_html);
			System.out.println("send out successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("send fail");
		}
	}
//
	/**
	 * 修改密码
	 *
	 * @param map        用户对象
	 */
	public void modifyPwdEmail(Map map) {
		SendEmail email = new SendEmail("smtp.163.com", 25, 0, true,
				"meishi_37@163.com", "meishi", false);

		try {
			Map<String, Object> content = new HashMap<String, Object>();
			content.put("userName", map.get("username"));
			content.put("verify_url", GenerateLinkUtils.generateChangePwdLink(
					AppContextData.DOMAIN,
					(String) map.get("email"), (String) map.get("operateCode")));
			String content_html = WebUtil.generateContent(
					"/oldfront/user/email/modifyPhone.html", content);

			email.sendEmail(EMAIL_MANAGER, "37美食", (String) map.get("email"),
					"修改密码", content_html);
			System.out.println("send out successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("send fail");
		}
	}
//
	/**
	 * 绑定邮箱
	 *
	 * @param map
	 *            用户对象
	 */
	public void verifyEmail(Map map) {
		SendEmail email = new SendEmail("smtp.163.com", 25, 0, true,
				"meishi_37@163.com", "meishi", true);

		try {
			HashMap<String, Object> content = new HashMap<String, Object>();
			content.put("verify_url", GenerateLinkUtils
					.generateVerifyEmailLink(
							AppContextData.DOMAIN,
							(String) map.get("operateCode")));
			String content_html = WebUtil.generateContent(
					"/oldfront/user/email/verifyEmail.html", content);

			email.sendEmail(EMAIL_MANAGER, "37美食", (String) map.get("email"),
					"绑定邮箱", content_html);
			System.out.println("send out successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("send fail");
		}
	}
//
	/**
	 * 修改手机号码
	 *
	 * @param map
	 *            用户对象
	 */
	public void modifyPhone(Map map) {
		SendEmail email = new SendEmail("smtp.163.com", 25, 0, true,
				"meishi_37@163.com", "meishi", false);

		try {
			Map<String, Object> content = new HashMap<String, Object>();
			content.put("userName", map.get("username"));
			content.put("verify_url", GenerateLinkUtils
					.generateModifyPhoneLink(
							AppContextData.DOMAIN,
							(String) map.get("operateCode")));
			String content_html = WebUtil.generateContent(
					"/oldfront/user/email/modifyPhone.html", content);

			email.sendEmail(EMAIL_MANAGER, "37美食", (String) map.get("email"),
					"修改手机号码", content_html);
			System.out.println("send out successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("send fail");
		}
	}
//
	/**
	 * 修改密码
	 *
	 * @param map
	 *            用户对象
	 */
	public void modifyPayPwdEmail(Map map) {
		SendEmail email = new SendEmail("smtp.163.com", 25, 0, true,
				"meishi_37@163.com", "meishi", false);

		try {
			Map<String, Object> content = new HashMap<String, Object>();
			content.put("userName", map.get("username"));
			content.put("verify_url", GenerateLinkUtils
					.generateChangePayPwdLink(
							AppContextData.DOMAIN,
							(String) map.get("email"),
							(String) map.get("operateCode")));
			String content_html = WebUtil.generateContent(
					"/oldfront/user/email/modifyPhone.html", content);

			email.sendEmail(EMAIL_MANAGER, "37美食", (String) map.get("email"),
					"修改支付密码", content_html);
			System.out.println("send out successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("send fail");
		}
	}

}
