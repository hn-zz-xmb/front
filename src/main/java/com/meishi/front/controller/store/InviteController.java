package com.meishi.front.controller.store;

import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.model.InviteInfo;
import com.meishi.model.Store;

import java.util.List;

//@Controller(controllerKey = "/store/invite")
public class InviteController extends BaseController {
	private static Logger log = Logger.getLogger(InviteController.class);

	// /查询店铺发布的招聘信息
	public void getJob() {
		Store store = Store.dao.findBystoreId(getPara("storeId"));
		List<InviteInfo> inviteInfos = InviteInfo.dao.findInviteInfo(store
				.getStr("id"));
		setAttr("store", store);
		setAttr("inviteInfo", inviteInfos);
		render("/store/zhaopin.html");
	}

	// 招聘信息详情
	public void inviteInfoDetail() {
		InviteInfo info = InviteInfo.dao.findInviteInfoDetail(getPara("id"));
		Store store = Store.dao.findById(info.getStr("store_id"));
		setAttr("info", info);
		setAttr("store", store);
		render("/store/jobinfo.html");
	}
}
