package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.ActivePrizeItem;
import com.meishi.model.Coupon;
import com.meishi.model.Store;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Before(StoreInteceptor.class)
/**
 * Created by rsp on 14-11-18.
 */
public class ActivePrizeItemManageController extends BaseController {
	private static Logger log = Logger
			.getLogger(ActivePrizeItemManageController.class);

	/**
	 * 查询店铺奖项
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
		Page<ActivePrizeItem> result = ActivePrizeItem.dao.findByStoreId(
				pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/active/prizeList_s.html");
	}

	/**
	 * 添加奖项页面
	 */
	public void saveUI() {
		Store store = getSessionAttr("store");
		// 加载优惠券
		setAttr("coupons", Coupon.dao.findByStoreId(store.getStr("id")));
		setAttr("action", "save");
		render("/store/admin/active/prizeForm.html");
	}

	/**
	 * 添加操作
	 */
	// 保存代金券奖项
	public void saveCouponItem() {
		Store store = getSessionAttr("store");
		Coupon coupon = Coupon.dao.findById(getPara("couponid"));
		getModel(ActivePrizeItem.class).set("id", ToolUtil.getUuidByJdk(true))
				.set("store_id", store.getStr("id"))
				.set("coupon_id", coupon.get("id"))
				.set("item_name", coupon.get("name"))
				.set("item_desc", coupon.get("name")).set("item_type", 2)
				.save();
		redirect("/salerManage/prizeItem");
	}

	// 保存自定义奖项
	public void saveCustomItem() {
		Store store = getSessionAttr("store");
		getModel(ActivePrizeItem.class).set("id", ToolUtil.getUuidByJdk(true))
				.set("store_id", store.get("id")).set("item_type", 1).save();
		redirect("/salerManage/prizeItem");
	}

	// 保存积分奖项
	public void savePointsItem() {
		Store store = getSessionAttr("store");
		getModel(ActivePrizeItem.class).set("id", ToolUtil.getUuidByJdk(true))
				.set("store_id", store.get("id")).set("item_type", 3).save();
		redirect("/salerManage/prizeItem");
	}

	/**
	 * 更新奖项页面
	 */
	public void updateUI() {
		setAttr("action", "update");
		setAttr("activePrizeItem", ActivePrizeItem.dao.findById(getPara("id")));
		render("/oldfront/saler/activeCenter/prizeItem_form.html");
	}

	/**
	 * 更新操作
	 */
	public void update() {
		getModel(ActivePrizeItem.class).update();
		redirect("/salerManage/prizeItem");
	}

	/**
	 * 删除
	 */
	public void delete() {
		JSONObject result = new JSONObject();
		result.put("isDelete", ActivePrizeItem.dao.deleteById(getPara("id")));
		renderJson(result.toString());
	}

}
