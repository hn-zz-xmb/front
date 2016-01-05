package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.InviteInfo;
import com.meishi.model.Store;
import com.meishi.util.DateUtil;
import com.meishi.util.ToolUtil;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rsp on 14-11-18.
 */
@Before(StoreInteceptor.class)
public class InviteInfoManageController extends BaseController {
	private static Logger log = Logger
			.getLogger(InviteInfoManageController.class);

	/**
	 * 列表
	 */
	public void index() {
		Store store = getSessionAttr("store");
		Map<String, Object> params = new HashMap<String, Object>();
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
		Page<InviteInfo> result = InviteInfo.dao.findInviteInfoByStoreId(
				pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/storejob/zhaopinList.html");
	}

	/**
	 * 新增
	 */
	public void saveUI() {
		render("/store/admin/storejob/zhaopinForm.html");
	}

	/**
	 * 更新
	 */
	public void updateUI() {
		setAttr("inviteInfo", InviteInfo.dao.findById(getPara("id")));
		render("/store/admin/storejob/zhaopinForm.html");
	}

	/**
	 * 新增操作
	 */
	public void saveorupdate() {
		Store store = getSessionAttr("store");
		InviteInfo info = getModel(InviteInfo.class);
		if (info.getStr("id") == "" || info.getStr("id") == null) {
			info.set("id", ToolUtil.getUuidByJdk(true))
					.set("store_id", store.get("id"))
					.set("address", store.get("address"))
					.set("date", DateUtil.format(new Date(), "yyyyMMddHHmmss"))
					.set("description",
							StringEscapeUtils.unescapeHtml4(info
									.getStr("description"))).save();
		} else {
			info.set("description",
					StringEscapeUtils.unescapeHtml4(info.getStr("description")))
					.update();
		}

		redirect("/salerManage/inviteInfo");
	}

	/**
	 * 删除
	 */
	public void delete() {
		InviteInfo info = InviteInfo.dao.findById(getPara("id"));
		info.delete();
		redirect("/salerManage/inviteInfo");
	}

	// 是否显示快捷设置
	public void updateShow() {
		InviteInfo info = InviteInfo.dao.findById(getPara("id"));
		if (info.getInt("is_show") == 1) {
			info.set("is_show", 0).update();
		} else {
			info.set("is_show", 1).update();
		}
		redirect("/salerManage/inviteInfo");
	}

}
