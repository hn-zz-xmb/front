package com.meishi.front.controller.store;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.ActivePrizeInfo;
import com.meishi.model.ActiveProgram;
import com.meishi.model.ActiveProgramItem;
import com.meishi.model.ActiveType;
import com.meishi.util.DateUtil;

import java.util.Date;
import java.util.List;

//@Controller(controllerKey = "/store/game")
@Before(LoginInteceptor.class)
public class GameController extends BaseController {
	private static Logger log = Logger.getLogger(GameController.class);

	// 参加活动
	public void joinActive() {
		ActiveProgram activeProgram = ActiveProgram.dao
				.findById(getPara("activeId"));
		if (activeProgram == null) {
			renderError(404);
		}
		// 验证活动是否过期
		if (DateUtil.parse(activeProgram.getStr("end_time"),
				DateUtil.pattern_ymd_hms).compareTo(new Date()) < 0) {
			render("/store/admin/active/expired.html");
			return;
		}
		//查询活动的奖项
		List<ActiveProgramItem> activeProgramItem=ActiveProgramItem.dao.findByProgramId(activeProgram.getStr("id"));
		//查询活动的获奖名单
		List<ActivePrizeInfo> activePrizeInfos=ActivePrizeInfo.dao.findByProgramId(activeProgram.getStr("id"));
		setAttr("activePrizeInfos", activePrizeInfos);
		setAttr("activeProgramItem", activeProgramItem);
		setAttr("activeProgram", activeProgram);
		render("/store/admin/game/activity.html");
	}

	// 判断活动类型跳转到某个页面
	public void checkActive() {
		ActiveProgram activeProgram = ActiveProgram.dao
				.findById(getPara("activeId"));
		if (activeProgram == null) {
			renderError(404);
		}
		// 验证活动是否过期
		if (DateUtil.parse(activeProgram.getStr("end_time"), "yyyyMMdd")
				.compareTo(new Date()) < 0) {
			render("/store/admin/active/expired.html");
		}
		setAttr("activeProgram", activeProgram);
		setAttr("storeId", activeProgram.get("store_id"));
		ActiveType activeType = ActiveType.dao.findById(activeProgram
				.get("activetype_id"));
		if (AppContextData.StoreActiveType.eggs.name().equals(
				activeType.get("code"))) {
			render("/store/admin/game/eggs.html");
		} else if (AppContextData.StoreActiveType.bigwheel.name().equals(
				activeType.get("code"))) {
			render("/store/admin/game/bigwheel.html");
		}

	}
}
