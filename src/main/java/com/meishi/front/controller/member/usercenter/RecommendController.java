package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.Member;
import com.meishi.model.RecommendMember;
import com.meishi.model.RecommendMoney;
import com.meishi.model.RecommendRelation;
import com.meishi.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

//@Controller(controllerKey = "/account/buyer")
@Before(LoginInteceptor.class)
public class RecommendController extends BaseController {
	private static Logger log = Logger.getLogger(RecommendController.class);

	// 我推荐的人
	public void index() {
		// 根据Id查询用户
		Member member = Member.dao.findById(getUserIds());
		// 查询该推荐人
		RecommendMember recommendMember = RecommendMember.dao
				.findBymemberId(member.getStr("id"));
		// 查询该推荐人推荐的一级
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", member.get("id"));
		String pageNum = getPara("pageNum");
		String pageSize = getPara("pageSize");
		if (StringUtil.isBlank(pageNum)) {
			pageNum = "1";
			if (StringUtil.isBlank(pageSize)) {
				pageSize = "10";
			}
			Page<RecommendRelation> result = RecommendRelation.dao.findPage(
					Integer.parseInt(pageNum), Integer.parseInt(pageSize),
					params);
			// 查询获利
			RecommendMoney recommendMoney = RecommendMoney.dao
					.findByUserId(member.getStr("id"));

			Integer count1 = RecommendRelation.dao
					.findFirstRecommendCount(member.getStr("id"));
			Integer count2 = RecommendRelation.dao
					.findSecondRecommendCount(member.getStr("id"));
			Integer count3 = RecommendRelation.dao
					.findThirdRecommendCount(member.getStr("id"));
			Integer storecount = RecommendRelation.dao
					.findRecommendStoreCount(member.getStr("id"));
			setAttr("recommendMember", recommendMember);
			setAttr("recommendMoney", recommendMoney);
			setAttr("count1", count1);
			setAttr("count2", count2);
			setAttr("count3", count3);
			setAttr("storecount", storecount);
			setAttr("page", result);
			setAttr("params", params);
			setAttr("page_params", paramsToStr(params));
			render("/usercenter/tuijianzhongxin.html");
		}
	}

	public void findRecommendStore() {
		// 根据Id查询用户
		Member member = Member.dao.findById(getPara("userId"));
		// 查询该推荐人
		RecommendMember recommendMember = RecommendMember.dao
				.findBymemberId(member.getStr("id"));
		// 查询该推荐人推荐的一级
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", member.get("id"));
		String pageNum = getPara("pageNum");
		String pageSize = getPara("pageSize");
		if (StringUtil.isBlank(pageNum)) {
			pageNum = "1";
		}
		if (StringUtil.isBlank(pageSize)) {
			pageSize = "10";
		}
		// 查询推荐的店铺
		Page<RecommendRelation> store = RecommendRelation.dao.findReStore(
				Integer.parseInt(pageNum), Integer.parseInt(pageSize), params);
		// 查询获利
		RecommendMoney recommendMoney = RecommendMoney.dao.findByUserId(member
				.getStr("id"));
		Integer count1 = RecommendRelation.dao.findFirstRecommendCount(member
				.getStr("id"));
		Integer count2 = RecommendRelation.dao.findSecondRecommendCount(member
				.getStr("id"));
		Integer count3 = RecommendRelation.dao.findThirdRecommendCount(member
				.getStr("id"));
		Integer storecount = RecommendRelation.dao
				.findRecommendStoreCount(member.getStr("id"));
		setAttr("recommendMember", recommendMember);
		setAttr("recommendMoney", recommendMoney);
		setAttr("count1", count1);
		setAttr("count2", count2);
		setAttr("count3", count3);
		setAttr("storecount", storecount);
		setAttr("page", store);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/usercenter/storerecommend1.html");
	}
	public void findSecondRecommend() {
		// 根据Id查询用户
		Member member = Member.dao.findById(getPara("userId"));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", member.get("id"));
		String pageNum = getPara("pageNum");
		String pageSize = getPara("pageSize");
		if (StringUtil.isBlank(pageNum)) {
			pageNum = "1";
		}
		if (StringUtil.isBlank(pageSize)) {
			pageSize = "10";
		}
		Page<RecommendRelation> result = RecommendRelation.dao.findPage(
				Integer.parseInt(pageNum), Integer.parseInt(pageSize), params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/usercenter/secondrecommend1.html");
	}
	
	public void findThirdRecommend() {
		// 根据Id查询用户
		Member member = Member.dao.findById(getPara("userId"));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", member.get("id"));
		String pageNum = getPara("pageNum");
		String pageSize = getPara("pageSize");
		if (StringUtil.isBlank(pageNum)) {
			pageNum = "1";
		}
		if (StringUtil.isBlank(pageSize)) {
			pageSize = "10";
		}
		Page<RecommendRelation> result = RecommendRelation.dao.findPage(
				Integer.parseInt(pageNum), Integer.parseInt(pageSize), params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/usercenter/thirdrecommend1.html");
	}
	// 查询用户的获利纪录
	public void findRecommendMoney() {
		Member member = Member.dao.findById(getUserIds(), "id");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", member.getStr("id"));
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
		Page<RecommendMoney> result = RecommendMoney.dao.findListByUserId(
				pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/usercenter/recommendmoney.html");
	}

}
