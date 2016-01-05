package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.ActivePrizeInfo;
import com.meishi.model.Member;
import com.meishi.model.Store;
import com.meishi.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

@Before(LoginInteceptor.class)
/**
 * Created by rsp on 14-11-18.
 */
public class ActivePrizeInfoController extends BaseController {
	private static Logger log = Logger
			.getLogger(ActivePrizeInfoController.class);

	public void index() {
		Store store = getSessionAttr("store");
		Map<String, Object> params = new HashMap<String, Object>();

		String userName=getPara("username");
		if(StringUtil.isNotBlank((userName))) {
			params.put("username", userName);
			Member member = Member.dao.findByLogin(getPara("username"));
			if (member != null) {
				params.put("userId", member.getStr("id"));
			}
		}
		params.put("status", getPara("status"));
		params.put("storeId", store.get("id"));
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
		Page<ActivePrizeInfo> result = ActivePrizeInfo.dao
				.StoreActivePrizeInfo(pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/active/prizeCenter.html");
	}

	// 查看中奖详情
	public void infoDetail() {
		ActivePrizeInfo activePrizeInfo = ActivePrizeInfo.dao
				.findDetail(getPara("infoId"));
		setAttr("activePrizeInfo", activePrizeInfo);
		render("/store/admin/active/prizeDetail.html");
	}

	// 确认兑奖
	public void sureinfo() {
		ActivePrizeInfo activePrizeInfo = ActivePrizeInfo.dao
				.findById(getPara("infoId"));
		if (activePrizeInfo == null) {
			renderError(404);
		}
		activePrizeInfo.set("status", 0).update();
		redirect("/salerManage/prizeInfo");
	}
}
