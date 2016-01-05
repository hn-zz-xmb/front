package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.Member;
import com.meishi.model.MemberCoupon;

import java.util.HashMap;
import java.util.Map;

//@Controller(controllerKey = "/account/buyer")
@Before(LoginInteceptor.class)
public class BuyerController extends BaseController {
	private static Logger log = Logger.getLogger(BuyerController.class);

	// 我的优惠券
	public void coupon() {
		Member member = Member.dao.findById(getUserIds());
		Map<String, Object> params = new HashMap<String, Object>();
		if (member != null) {
			params.put("userId", member.getStr("id"));
			params.put("startTime", getPara("startTime"));
			params.put("endTime", getPara("endTime"));
			params.put("status", getPara("status"));
			Integer pageNum = getParaToInt("pageNum");
			Integer pageSize = getParaToInt("pageSize");
			if (pageNum == null || pageNum.intValue() < 1) {
				pageNum = 1;// 默认第一页
			}
			if (pageSize == null || pageSize.intValue() < 0) {
				pageSize = 10;// 默认每页显示10条
			} else if (pageSize.intValue() > 100) {
				pageSize = 10;// 最大100条
			}
			Page<MemberCoupon> result = MemberCoupon.dao.findMemberCoupon(
					pageNum, pageSize, params);
			setAttr("page", result);
			setAttr("params", params);
			setAttr("page_params", paramsToStr(params));
			render("/usercenter/usercoupon1.html");
		} else {
			render("/login");
		}
	}

	// 查询优惠券详情
	public void couponDetail() {
		MemberCoupon coupon = MemberCoupon.dao
				.couponDetail(getPara("couponId"));
		setAttr("coupon", coupon);
		render("/usercenter/couponDetail.html");
	}
}
