package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.*;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
@Before(StoreInteceptor.class)
//@Controller(controllerKey = "/store/admin/activeProgram")
public class ActiveProgramController extends BaseController {

	private static Logger log = Logger.getLogger(ActiveProgramController.class);


	// 前往添加活动页面
	public void saveUI() {
		Store store = Store.dao.findById(getSessionAttr("storeId"));
		setAttr("prizeItemList",
				ActivePrizeItem.dao.findByStore(store.getStr("id")));
		setAttr("activeTypeList", ActiveType.dao.findAllInUse());
		render("");
	}

	// 添加活动
	public void saveActive() {
		JSONObject result = new JSONObject();
		List<ActiveProgramItem> itemList = new LinkedList<ActiveProgramItem>();
		Store store = getSessionAttr("store");
		Member member = Member.dao.findById(store.getStr("user_id"));
		// 查询用户积分
		Points points = Points.dao.findById(member.getStr("points_id"));
		BigDecimal sum = new BigDecimal(0);
		for (int i = 1; i <= 5; i++) {
			String prizeItem = getPara("prizeItem" + i);
			String count = getPara("count" + i);
			ActivePrizeItem activePrizeItem = ActivePrizeItem.dao
					.findById(prizeItem);
			if (activePrizeItem != null
					&& activePrizeItem.getStr("item_type") != null
					&& activePrizeItem.getInt("item_type").intValue() == 3) {
				sum = sum.add(activePrizeItem.getBigDecimal("points").multiply(
						new BigDecimal(count)));
			}

		}
		if (points.getBigDecimal("account_points").compareTo(sum) < 0) {
			result.put("status", "n");
			result.put("error", "请确认你的积分充足");
			renderJson(result.toString());
		}
		ActiveProgram activeProgram = getModel(ActiveProgram.class).set(
				"store_id", store.getStr("id"));
		for (int i = 1; i <= 5; i++) {
			String prizeItem = getPara("prizeItem" + i);
			String count = getPara("count" + i);
			String id = getPara("programId" + i);
			if (id == null) {
				// 新增
				itemList.add(new ActiveProgramItem().set("name", "奖项" + i)
						.set("active_prize_item_id", prizeItem).set("px", i)
						.set("item_count", Integer.parseInt(count)));
			} else {
				// 编辑
				ActiveProgramItem programItem = ActiveProgramItem.dao
						.findById(id);
				programItem.set("active_prize_item_id", prizeItem)
						.set("item_count", Integer.parseInt(count)).update();
			}
		}
		activeProgram.save();
		for (ActiveProgramItem activeProgramItem : itemList) {
			activeProgramItem.set("program_id", activeProgram.getStr("id"));
			activeProgramItem.save();
		}
		result.put("status", "y");
		renderJson(result.toString());
	}

	// 添加奖项
	// 自定义奖项
	public void addPrizeItem() {
		Store store = getSessionAttr("store");
		getModel(ActivePrizeItem.class).set("store_id", store.getStr("id"))
				.save();
		render("");
	}

	// 添加积分奖项
	public void addPrizeItemJifen() {
		Store store = getSessionAttr("store");
		getModel(ActivePrizeItem.class).set("store_id", store.getStr("id"))
				.save();
		render("");
	}

	// 添加代金券奖项
	public void addPrizeItemCoupon() {
		ActivePrizeItem activePrizeItem = new ActivePrizeItem();
		Coupon coupon = Coupon.dao.findById(getPara("couponId"));
		Store store = getSessionAttr("store");
		activePrizeItem.set("store_id", store.getStr("id"))
				.set("item_name", coupon.getStr("name")).set("item_type", 2)
				.set("item_desc", coupon.getStr("name"))
				.set("coupon_id", coupon.getStr("id")).save();
		render("");

	}

	// 查询店铺已添加的所有奖项列表
	public void findAllPrizeItem() {
		Store store = getSessionAttr("store");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.getStr("id"));

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
		setAttr("params", paramsToStr(params));
		render("");
	}

	// 删除奖项
	public void deleteprizeItem() {
		ActivePrizeItem.dao.deleteById(getPara("itemId"));
		render("");
	}

	// 兑奖中心查看
	public void showActivePrizeInfo() {
		Store store = getSessionAttr("store");
		List<ActivePrizeInfo> activePrizeInfos = ActivePrizeInfo.dao
				.findBystoreId(store.getStr("id"));
		setAttr("activeProzeInfo", activePrizeInfos);
		render("");
	}

	// 查询店铺开启的活动
	public void findstoreActive() {
		Store store = getSessionAttr("store");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.getStr("id"));

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
		Page<ActiveProgram> result = ActiveProgram.dao.findByStoreId(pageNum,
				pageSize, params);
		setAttr("page", result);
		setAttr("params", paramsToStr(params));

		render("");
	}

	// 店铺删除活动
	public void deleteActive() {
		ActiveProgram.dao.deleteById(getPara("id"));
		ActiveProgramItem.dao.deleteActive(getPara("id"));
		render("");
	}

	// 编辑店铺中开启的活动
	public void editActive() {
		ActiveProgram activeProgram = ActiveProgram.dao.findById(getPara("id"));
		ActiveType activeType = ActiveType.dao.findById(activeProgram
				.getStr("id"));
		List<ActiveProgramItem> activeProgramItems = ActiveProgramItem.dao
				.findActiveProgramItem(activeProgram.getStr("id"));
		Store store = getSessionAttr("store");
		setAttr("activeprogram", activeProgram);
		setAttr("activeType", activeType);
		setAttr("activeProgramItem", activeProgramItems);
		setAttr("store", store);
		setAttr("prizeItemList",
				ActivePrizeItem.dao.findByStore(store.getStr("id")));
		render("");
	}

}
