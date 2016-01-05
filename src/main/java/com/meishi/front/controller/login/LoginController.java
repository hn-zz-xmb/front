package com.meishi.front.controller.login;

import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.model.CartItem;
import com.meishi.model.Member;
import com.meishi.model.MemberLink;
import com.meishi.util.DateUtil;
import com.meishi.util.WebUtil;
import com.meishi.util.msg.SendMsgTemplate;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.Map;

//@Controller(controllerKey = "/login")
public class LoginController extends BaseController {
	private static Logger log = Logger.getLogger(LoginController.class);

	// 前往登录页面
	public void index() {
		setAttr("ret_url", getPara("ret_url"));
		render("/login/login.html");
	}

	// 会员登录
	public void ajaxLogin() {
		JSONObject result = new JSONObject();
		String username = getPara("username");
		String password = getPara("password");
		Member member = Member.dao.findByLogin(username);
		// }
		if (member == null) {
			result.put("error", "用户名或密码不正确");
			result.put("isLogin", false);
		} else {
			if (!Member.dao.checkPwd(password, member)) {
				result.put("error", "用户名或密码不正确");
				result.put("isLogin", false);
			} else if (member.getInt("status") != 1) {
				result.put("error", "账户没有激活");
				result.put("isLogin", false);
			} else {
				result.put("isLogin", true);
				setSessionAttr("user_", member);
				member.set("last_login",
						DateUtil.format(new Date(), "yyyyMMddHHmmss"))
						.set("last_ip", WebUtil.getClientIpAddr(getRequest()))
						.update();
				handleCart(member.getStr("id"));
			}
		}
		renderJson(result.toString());
	}

	// 手机动态码登录
	public void ajaxPhoneLogin() {
		JSONObject result = new JSONObject();
		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1,
				getPara("dynamicCode"), SendMsgTemplate.LOGIN_CODE,
				getPara("phone"));

		if (memberLink != null && MemberLink.dao.checkMemberLink(memberLink)) {
			Member member = Member.dao.findByPhone(getPara("phone"));
			if (member.getInt("status") != 1) {
				result.put("error", "账户没有激活");
				result.put("isLogin", false);
			} else {
				result.put("isLogin", true);
				setSessionAttr("user_", member);
				member.set("last_login",
						DateUtil.format(new Date(), "yyyyMMddHHmmss"))
						.set("last_ip", WebUtil.getClientIpAddr(getRequest()))
						.update();
				handleCart(member.getStr("id"));
			}
		} else {
			result.put("error", "动态码不正确或者已过期");
			result.put("isLogin", false);
		}
		renderJson(result.toString());
	}

	// 验证当前用户是否存在
	public void ajaxValid() {
		JSONObject result = new JSONObject();
		String username = getPara("username");
		Member member = Member.dao.findByUsername(username);
		if (member == null) {
			result.put("isRegist", false);
			renderJson(result.toString());
			return;
		}
		result.put("isRegist", true);
		renderJson(result.toString());
	}

	// 登录时验证输入的手机号码是否正确
	public void ajaxPhone() {
		Member member = Member.dao.findByPhone(getPara("phone"));
		if (member == null) {
			renderText("false");
			return;
		}
		renderText("true");
	}

	/**
	 * 打开ajax登录窗口
	 */
	public void ajaxUI() {
		render("/login/ajaxLogin.html");
	}

	/**
	 * session中的购物车存放的数据库里面
	 */
	private void handleCart(String memberId) {
		Map<String, CartItem> cartItemMap = getSessionAttr("cartItems_");
		if (cartItemMap != null && cartItemMap.size() > 0) {
			CartItem.dao.saveByMap(cartItemMap, memberId);
		}
	}
}
