package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Coupon;
import com.meishi.model.Store;
import com.meishi.util.ToolUtil;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;

//@Controller(controllerKey = "/store/admin/coupon")
@Before(StoreInteceptor.class)
public class CouponManageController extends BaseController {
	private static Logger log = Logger.getLogger(CouponManageController.class);

	/**
	 * 查询当前店铺发布的代金券
	 */
	public void index() {
		Store store = getSessionAttr("store");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.get("id"));
		params.put("startTime", getPara("startTime"));
		params.put("endTime", getPara("endTime"));
		params.put("issue", getPara("issue"));
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
		Page<Coupon> result = Coupon.dao.findCouponByStoreId(pageNum, pageSize,
				params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/coupon/storecoupon1.html");
	}

	// 查询单个代金券发放记录
	public void couponSendRecode() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("couponId", getPara("couponId"));
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
		Page<Coupon> result = Coupon.dao.findCouponRecode(pageNum, pageSize,
				params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/coupon/sendrecord.html");
	}

	// 前往新增代金券页面
	public void saveUI() {
		render("/store/admin/coupon/addcoupon.html");
	}

	// 新增代金券
	public void saveorupdate() {
		Store store = getSessionAttr("store");
		Coupon coupon = getModel(Coupon.class);
		if (coupon.getStr("id") == "" || coupon.getStr("id") == null) {
			coupon.set("id", ToolUtil.getUuidByJdk(true))
					.set("count", 0)
					.set("store_id", store.getStr("id"))
					.set("begin_time",
							coupon.getStr("begin_time").replace("-", "")
									+ "000000")
					.set("end_time",
							coupon.getStr("end_time").replace("-", "")
									+ "235959");
			//if (coupon.getInt("issue") == null) {
			coupon.set("issue", 1);	//新增代金券 默认发布
			//}
			coupon.save();
		} else {
			coupon.set("begin_time",
					coupon.getStr("begin_time").replace("-", "") + "000000")
					.set("end_time",
							coupon.getStr("end_time").replace("-", "")
									+ "235959").update();
		}
		redirect("/salerManage/coupon");
	}

	// 编辑代金券(前往编辑页面)
	public void updateUI() {
		Coupon coupon=Coupon.dao.findById(getPara("id"));
		coupon.set("coupondetail", StringEscapeUtils.unescapeHtml4(coupon.getStr("coupondetail")));
		setAttr("coupon", coupon);
		render("/store/admin/coupon/addcoupon.html");
	}

	// 删除代金券
	public void delete() {
		Coupon coupon = Coupon.dao.findById(getPara("id"));
		coupon.delete();
		redirect("/salerManage/coupon");
	}

}
