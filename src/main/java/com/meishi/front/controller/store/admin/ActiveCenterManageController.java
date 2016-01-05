package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;

@Before(StoreInteceptor.class)
/**
 * Created by rsp on 14-11-18.
 */
public class ActiveCenterManageController extends BaseController {
	private static Logger log = Logger
			.getLogger(ActiveCenterManageController.class);

	/**
	 * 查询店铺活动中心
	 */
	public void index() {
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
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/active/activityList.html");
	}

	public void saveUI() {
		Store store = getSessionAttr("store");
		setAttr("action", "save");
		setAttr("activeTypes", ActiveType.dao.findAllInUse());
		setAttr("prizeItemList",
				ActivePrizeItem.dao.findByStore(store.getStr("id")));
		render("/store/admin/active/addactive.html");
	}

	public void updateUI() {
		Store store = getSessionAttr("store");
		setAttr("action", "update");
		setAttr("activeProgram", ActiveProgram.dao.findById(getPara("id")));
		setAttr("activeTypes", ActiveType.dao.findAllInUse());
		setAttr("prizeItemList",
				ActivePrizeItem.dao.findByStore(store.getStr("id")));
		render("/oldfront/saler/activeCenter/active_form.html");
	}

	public void add() {
		Integer id = getParaToInt("id");
		String type = getPara("type");
		setAttr("id", id);
		setAttr("type", type);
		if (type.equals("points")) {
			render("/store/admin/active/zidingyi.html");
		} else if (type.equals("self")) {
			render("/store/admin/active/zidingyi.html");
		} else {
			render("/store/admin/active/coupon.html");
		}
	}

	// 添加代金券奖项
	public void couponprizeitem() {
		JSONObject result = new JSONObject();
		Store store = getSessionAttr("store");
		Integer i = getParaToInt("id");
		Integer day = getParaToInt("endday");
		Coupon coupon = new Coupon();
		coupon.set("id", ToolUtil.getUuidByJdk(true))
				.set("name", getPara("couponname"))
				.set("coupon_money",
						new BigDecimal(getParaToInt("couponmoney")))
				.set("begin_time",
						DateUtil.format(new Date(), "yyyyMMddHHmmss"))
				.set("count", 0).set("issure", 1)
				.set("store_id", store.get("id"))
				.set("limit_money", new BigDecimal(getParaToInt("limitmoney")))
				.save();
		ActivePrizeItem item = new ActivePrizeItem();
		item.set("id", ToolUtil.getUuidByJdk(true))
				.set("item_name", getPara("couponname"))
				.set("item_desc", getPara("couponname"))
				.set("store_id", store.get("id")).set("item_type", 2)
				.set("coupon_id", coupon.get("id")).set("endday", day).save();
		result.put("status", true);
		result.put("i", i);
		result.put("activePrizeItemId", item.getStr("id"));
		result.put("activePrizeItemName", item.getStr("item_name"));
		renderJson(result.toString());
	}

	public void selfprizeitem() {
		JSONObject result = new JSONObject();
		Store store = getSessionAttr("store");
		Integer i = getParaToInt("id");
		ActivePrizeItem item = new ActivePrizeItem();
		item.set("id", ToolUtil.getUuidByJdk(true))
				.set("item_name", getPara("prizename"))
				.set("item_desc", getPara("descrpition"))
				.set("store_id", store.get("id")).set("item_type", 1).save();
		result.put("status", true);
		result.put("i", i);
		result.put("activePrizeItemId", item.getStr("id"));
		result.put("activePrizeItemName", item.getStr("item_name"));
		renderJson(result.toString());
	}

	// test
	public void test() {
		JSONObject result = new JSONObject();
		Map<Integer, ActivePrizeItemVo> aa = getSessionAttr("map");
		if (aa == null) {
			aa = new HashMap<Integer, ActivePrizeItemVo>();
		}
		String type = getPara("type");
		Integer i = getParaToInt("id");
		ActivePrizeItemVo item = new ActivePrizeItemVo();
		if (type.equals("coupon")) {
			item.setItem_name(getPara("item_name"));
			item.setType(type);
			item.setCouponmoney(new BigDecimal(getParaToInt("couponmoney")));
			item.setEndday(getParaToInt("endday"));
			item.setLimitmoney(new BigDecimal(getParaToInt("limitmoney")));
			item.setItem_desc(getPara("item_desc"));
			aa.put(i, item);
		} else if (type.equals("self")) {
			item.setItem_name(getPara("item_name"));
			item.setType(type);
			item.setItem_desc(getPara("item_desc"));
			aa.put(i, item);
		}
		setSessionAttr("map", aa);
		result.put("status", true);
		result.put("i", i);
		result.put("itemname", getPara("item_name"));
		renderJson(result.toString());
	}

    @Before(Tx.class)
	public void saveactive() {
		JSONObject result = new JSONObject();
		Map<Integer, ActivePrizeItemVo> aa = getSessionAttr("map");
		Store store = getSessionAttr("store");
		Member member = Member.dao.findById(store.getStr("user_id"));
		// 查询用户积分
//		Points points = Points.dao.findById(member.getStr("points_id"));
//		if (points == null) {
//            result.put("status", "n");
//            result.put("error", "请确认你的积分账户是否存在!");
//            renderJson(result.toString());
//            return;
//		}
		if (aa != null) {
			List<ActiveProgramItem> itemList = new LinkedList<ActiveProgramItem>();
			for (Integer i : aa.keySet()) {
				ActivePrizeItemVo activePrizeItemVo = aa.get(i);
				Coupon coupon = new Coupon();
				ActivePrizeItem item = new ActivePrizeItem();
				item.set("id", ToolUtil.getUuidByJdk(true)).set("store_id",
						store.get("id"));
				if (activePrizeItemVo.getType().equals("coupon")) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.DATE, activePrizeItemVo.getEndday());
					coupon.set("id", ToolUtil.getUuidByJdk(true))
							.set("name", activePrizeItemVo.getItem_name())
							.set("coupon_money",
									activePrizeItemVo.getCouponmoney())
							.set("begin_time",
									DateUtil.format(new Date(),
											"yyyyMMddHHmmss"))
							.set("end_time",
									DateUtil.format(calendar.getTime(),
											"yyyyMMddHHmmss"))
							.set("count", 0)
							.set("issue", 1)
							.set("store_id", store.get("id"))
							.set("limit_money",
									activePrizeItemVo.getLimitmoney()).save();
					item.set("coupon_id", coupon.get("id")).set("item_type", 2);
//				} else if (activePrizeItemVo.getType().equals("points")) {
//					item.set("item_type", 3);
//					// 积分奖项未完成
				} else {
					item.set("item_type", 1);
				}
				item.set("item_name", activePrizeItemVo.getItem_name())
						.set("item_desc", activePrizeItemVo.getItem_desc())
						.save();
				itemList.add(new ActiveProgramItem()
						.set("id", ToolUtil.getUuidByJdk(true))
						.set("name", "奖项" + i)
						.set("active_prize_item_id", item.get("id"))
						.set("px", i)
						.set("item_count", getParaToInt("count" + i)));
			}
			ActiveProgram activeProgram = getModel(ActiveProgram.class);
			if (getPara("issure") == null) {
				activeProgram.set("issure", 0);
			} else {
				activeProgram.set("issure", 1);
			}
			activeProgram
					.set("id", ToolUtil.getUuidByJdk(true))
					.set("end_time",
							activeProgram.getStr("end_time").replace("-", "")
									+ "000000")
					.set("start_time",
							activeProgram.getStr("start_time").replace("-", "")
									+ "000000")
					.set("store_id", store.getStr("id")).save();
			for (ActiveProgramItem activeProgramItem : itemList) {
				activeProgramItem.set("program_id", activeProgram.getStr("id"));
				activeProgramItem.save();
			}

		}
		result.put("status", "y");
		renderJson(result.toString());
	}

	/**
	 * ? 未完成
	 */
//	public void save() {
//		JSONObject result = new JSONObject();
//		List<ActiveProgramItem> itemList = new LinkedList<ActiveProgramItem>();
//		Store store = getSessionAttr("store");
//		Member member = Member.dao.findById(store.getStr("user_id"));
//
//		BigDecimal sum = new BigDecimal(0);
//		for (int i = 1; i <= 5; i++) {
//			String prizeItem = getPara("itemid_" + i);
//			String count = getPara("count" + i);
//			ActivePrizeItem activePrizeItem = ActivePrizeItem.dao
//					.findById(prizeItem);
//			if (activePrizeItem != null
//					&& activePrizeItem.getInt("item_type") != null
//					&& activePrizeItem.getInt("item_type").intValue() == 3) {
//				sum = sum.add(activePrizeItem.getBigDecimal("points").multiply(
//						new BigDecimal(count)));
//			}
//			if (activePrizeItem != null
//					&& activePrizeItem.getInt("item_type") == 2) {
//				Coupon coupon = Coupon.dao.findById(activePrizeItem
//						.get("coupon_id"));
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(new Date());
//				calendar.add(Calendar.DATE, activePrizeItem.getInt("endday"));
//				coupon.set("end_time",
//						DateUtil.format(calendar.getTime(), "yyyyMMddHHmmss"))
//						.update();
//			}
//
//		}
//        // 查询用户积分
//        Points points = Points.dao.findById(member.getStr("points_id"));
//        if (points == null) {
//            result.put("status", "n");
//            result.put("error", "请确认你有积分账户");
//            renderJson(result.toString());
//            return;
//        }
//
//		if (points.getBigDecimal("account_points").compareTo(sum) < 0) {
//			result.put("status", "n");
//			result.put("error", "请确认你的积分充足");
//			renderJson(result.toString());
//			return;
//		}
//		ActiveProgram activeProgram = getModel(ActiveProgram.class);
//		Integer issure = getParaToInt("issure");
//		if (issure != 1) {
//			activeProgram.set("issure", 0);
//		}
//		activeProgram
//				.set("id", ToolUtil.getUuidByJdk(true))
//				.set("end_time",
//						activeProgram.getStr("end_time").replace("-", "")
//								+ "000000")
//				.set("start_time",
//						activeProgram.getStr("start_time").replace("-", "")
//								+ "000000").set("store_id", store.getStr("id"));
//		for (int i = 1; i <= 5; i++) {
//			String prizeItem = getPara("itemid_" + i);
//			String count = getPara("count" + i);
//			// 新增
//			itemList.add(new ActiveProgramItem()
//					.set("id", ToolUtil.getUuidByJdk(true))
//					.set("name", "奖项" + i)
//					.set("active_prize_item_id", prizeItem).set("px", i)
//					.set("item_count", Integer.parseInt(count)));
//		}
//		activeProgram.save();
//		for (ActiveProgramItem activeProgramItem : itemList) {
//			activeProgramItem.set("program_id", activeProgram.getStr("id"));
//			activeProgramItem.save();
//		}
//		result.put("status", "y");
//		renderJson(result.toString());
//	}

	public void update() {
		redirect("/salerManage/activeCenter");
	}

	public void delete() {
		ActiveProgram activeProgram = ActiveProgram.dao
				.findById(getPara("programId"));
		if (activeProgram == null) {
			renderError(404);
		} else {
			ActiveProgramItem.dao.deleteActive(activeProgram.getStr("id"));
			activeProgram.delete();
		}
		redirect("/salerManage/activeCenter");
	}

}
