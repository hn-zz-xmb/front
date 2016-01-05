package com.meishi.front.controller.yun;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.controller.store.admin.ActiveCenterManageController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Store;
import com.meishi.model.yun.*;

@Before(StoreInteceptor.class)
public class OrderPayYunController extends BaseController{

	private static Logger log = Logger
			.getLogger(ActiveCenterManageController.class);
	
	/**
	 * 查询营业报表（订单详单列表）
	 */
	public void index() {
		Store store = getSessionAttr("store");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.getStr("id"));

		//验证begin_time
		String begin_time= getPara("begin_time");
		String end_time= getPara("end_time");

		//验证end_time
		params.put("begin_time",begin_time);
		params.put("end_time",end_time);

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
		//付款方式统计
		setAttr("orderPayList",
				OrderPayYun.dao.orderPayByStore(params));

		//订单数量统计
		setAttr("orderCount",
				OrderYun.dao.orderCountByStore(params));
		//菜品数量
		setAttr("itemCount",
				OrderItemYun.dao.CountByStore(params));
		//充值统计
		setAttr("rechargeCount",
				CardRecordYun.dao.CountbyStore(params));

		Page<OrderYun> result = OrderYun.dao.findPageByStore(pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/yun/activityList.html");
	}
	
	/**
	 * 商品统计报表（菜品统计报表）
	 */
	public void AllFood() {
		Store store = getSessionAttr("store");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.getStr("id"));

		//验证begin_time
		String begin_time= getPara("begin_time");
		String end_time= getPara("end_time");

		//验证end_time
		params.put("begin_time",begin_time);
		params.put("end_time",end_time);

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
		Page<OrderItemYun> result = OrderItemYun.dao.findItemPageByStore(pageNum, pageSize,params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/yun/AllFoodList.html");
	}

	/**
	 * 退菜报表
	 */
	public void Retreatfood() {
		Store store = getSessionAttr("store");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.getStr("id"));

		//验证begin_time
		String begin_time= getPara("begin_time");
		String end_time= getPara("end_time");

		//验证end_time
		params.put("begin_time",begin_time);
		params.put("end_time",end_time);
		
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
		Page<OrderItemReturnYun> result = OrderItemReturnYun.dao.findPageByStore(pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/yun/RetreatfoodList.html");
	}
	
	
	/**
	 * 撤单报表
	 */
	public void CancleOrder() {
		Store store = getSessionAttr("store");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.getStr("id"));

		//验证begin_time
		String begin_time= getPara("begin_time");
		String end_time= getPara("end_time");

		//验证end_time
		params.put("begin_time",begin_time);
		params.put("end_time",end_time);
		
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
		Page<OrderYun> result = OrderYun.dao.findCancelPageByStore(pageNum, pageSize,params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/yun/CancleOrderList.html");
	}

	public void AntiOrder() {
		Store store = getSessionAttr("store");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId",store.getStr("id"));

		//验证begin_time
		String begin_time= getPara("begin_time");
		String end_time= getPara("end_time");

		//验证end_time
		params.put("begin_time",begin_time);
		params.put("end_time",end_time);

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
		Page<AntiOrderYun> result = AntiOrderYun.dao.findPageByStore(pageNum, pageSize,params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/yun/AntiOrderList.html");
	}

	/**
	 * 会员充值报表
	 */
	public void CardRecord() {
		Store store = getSessionAttr("store");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.getStr("id"));

		//验证begin_time
		String begin_time= getPara("begin_time");
		String end_time= getPara("end_time");

		//验证end_time
		params.put("begin_time",begin_time);
		params.put("end_time",end_time);

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
		//分类统计金额
		setAttr("type",
				CardRecordYun.dao.typeByStore(params));
		//赠送金额
		setAttr("give_money",
				CardRecordYun.dao.giveByStore(params));
		//剩余金额
		setAttr("all_money",
				CardRecordYun.dao.allByStore(params));
		Page<CardRecordYun> result = CardRecordYun.dao.findPageByStore(pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/yun/CardList.html");
	}
}
