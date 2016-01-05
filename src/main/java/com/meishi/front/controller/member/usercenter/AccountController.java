package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;

import java.math.BigDecimal;
import java.util.Date;
@Before(LoginInteceptor.class)
public class AccountController extends BaseController {
	private static Logger log = Logger.getLogger(AccountController.class);

	// 个人中心需要查找当前用户登录时间、ip、积分、账户余额、订单、代金券、是否签到、中奖信息、消息
	public void accountcenter() {
		// 查询到当前登录的用户
		Member member = Member.dao.findById(getUserIds());
		// 查询当前用户余额
		Coin coin = Coin.dao.findById(member.getStr("coin_id"));
		// 查询当前用户积分
		Points points = Points.dao.findById(member.getStr("points_id"));
		// 判断该用户是否签到
		SignIn signin = SignIn.dao.findByMIdAndLoginTime(member.getStr("id"),
				DateUtil.format(new Date(), "yyyyMMdd"));
		// 当a==0时已经签过到 a==1时未签到
		int a = 0;
		if (signin == null) {
			a = 1;
		} else {
			a = 0;
		}
		// 统计订单
		Integer nopayOrder = Orders.dao.findCount(member.getStr("id"),
				AppContextData.SUBMITTED);
		Integer finishedOrder = Orders.dao.findCount(member.getStr("id"),
				AppContextData.FINISHED);
		Integer nouseOrder = Orders.dao.findCount(member.getStr("id"),
				AppContextData.PAYED);
		// 查询当前用户的中奖记录
		// 当前用户所有中奖记录
		Integer prizeInfoWin = ActivePrizeInfo.dao.countPrizeWin(member
				.getStr("id"));
		// 当前用户未兑奖记录
		Integer prizeInfoNoUse = ActivePrizeInfo.dao.countPrizeinfoNouse(member
				.getStr("id"));
		// 查询当前用户的优惠券
		// 当前用户所有优惠券
		Integer countCoupon = MemberCoupon.dao.findCountCoupon(member
				.getStr("id"));
		// 当前用户所有未使用优惠券
		Integer countCouponNoUse = MemberCoupon.dao.findCountCouponNoUse(member
				.getStr("id"));
		
		//当前用户的推荐人申请记录
		RecommendApply recommendApply=RecommendApply.dao.findBymemberId(member.getStr("id"));
		setAttr("recommendApply", recommendApply);
		//推荐人表是否有当前用户
		RecommendMember recommendMember=RecommendMember.dao.findBymemberId(member.getStr("id"));
		
		//查询用户的消息数
		Integer countMessage=Message.dao.findCountMessage(member.getStr("id"));
		setAttr("countMessage", countMessage);
		
		setAttr("recommendMember", recommendMember);
		setAttr("member", member);
		setAttr("coin", coin);
		setAttr("points", points);
		setAttr("nopayOrder", nopayOrder);
		setAttr("finishedOrder", finishedOrder);
		setAttr("nouseOrder", nouseOrder);
		setAttr("a", a);
		setAttr("prizeInfoWin", prizeInfoWin);
		setAttr("prizeInfoNoUse", prizeInfoNoUse);
		setAttr("countCoupon", countCoupon);
		setAttr("countCouponNoUse", countCouponNoUse);
		//推荐中心
		Integer count1 = RecommendRelation.dao
				.findFirstRecommendCount(member.getStr("id"));
		Integer count2 = RecommendRelation.dao
				.findSecondRecommendCount(member.getStr("id"));
		Integer count3 = RecommendRelation.dao
				.findThirdRecommendCount(member.getStr("id"));
		Integer storecount = RecommendRelation.dao
				.findRecommendStoreCount(member.getStr("id"));
		setAttr("count1", count1);
		setAttr("count2", count2);
		setAttr("count3", count3);
		setAttr("storecount", storecount);
		render("/usercenter/personalCenter.html");
	}

	public void Signin() {
		// 查询到当前登录的用户
		Member member = Member.dao.findById(getUserIds());
		if (member == null) {
			renderText("fail");
		} else {
			// 查询当前用户积分
			Points points = Points.dao.findById(member.getStr("points_id"));
			new SignIn()
					.set("member_id", member.getStr("id"))
					.set("login_time",
							DateUtil.format(new Date(), "yyyyMMddHHmmss"))
					.save();
			points.set(
					"account_points",
					points.getBigDecimal("account_points").add(
							new BigDecimal(5))).update();
			renderText("success");
		}

	}
}
