package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Delivery;
import com.meishi.model.Store;
import com.meishi.util.ToolUtil;

import java.util.HashMap;
import java.util.Map;
//@Controller(controllerKey = "/store/admin/delivery")
@Before(StoreInteceptor.class)
public class DeliveryController extends BaseController {
	private static Logger log = Logger.getLogger(DeliveryController.class);
	// 根据店铺id查找该店铺的配送方式
	public void deliveryList() {
		Map<String, Object> params = new HashMap<String, Object>();
		Store store = getSessionAttr("store");
		params.put("store_id", store.getStr("id"));
		Integer pageNum = getParaToInt("pageNum");
		Integer pageSize = getParaToInt("pageSize");
		if (pageNum == null || pageNum.intValue() < 1) {
			pageNum = 1;// 默认第一页
		}
		if (pageSize == null || pageSize.intValue() < 0) {
			pageSize = 10;// 默认每页显示10条
		} else if (pageSize.intValue() > 100) {
			pageSize = 100;// 最大100条
		}
		Page<Delivery> page=Delivery.dao.pageByStoreId(pageNum, pageSize, params);		
		setAttr("page", page);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/material/delivery/deliveryList.html");
	}

	// 删除已添加的配送方式
	public void deleteDelivery() {
		Delivery.dao.deleteById(getPara("id"));
		redirect("/salerManage/materialManage/delivery/deliveryList");
	}
	
	//多条数据同时删除
	public void deleteAllDelivery(){
		String ids = getPara("ids");
		String list[] = ids.split(",");
		for (int i = 0; i < list.length; i++) {
			if (list[i] != null && !"".equals(list[i])) {
				Delivery.dao.deleteById(list[i]);
			}
		}
		renderText("success");
	}
	
	// 添加配送方式(前往添加页面)
	public void toAddDelivery() {
		render("/store/admin/material/delivery/deliveryForm.html");
	}

	// 添加配送方式(添加)
	public void addDelivery() {
		Store store = getSessionAttr("store");
		getModel(Delivery.class).set("store_id", store.getStr("id")).set("id",ToolUtil.getUuidByJdk(true)).save();
		redirect("/salerManage/materialManage/delivery/deliveryList");
	}

	// 更新配送方式(前往更新页面)
	public void toupdateDelivery() {
		setAttr("delivery", Delivery.dao.findById(getPara("id")));
		render("/store/admin/material/delivery/deliveryEdit.html");
	}

	// 更新配送方式(更新)
	public void updateDelivery() {
		getModel(Delivery.class).update();
		redirect("/salerManage/materialManage/delivery/deliveryList");
	}
	
	//是否启用
	public void updateopen(){
		Delivery delivery=Delivery.dao.findById(getPara("id"));
		if(delivery.getInt("is_open")==0){
			delivery.set("is_open", 1).update();
		}else{
			delivery.set("is_open", 0).update();
		}
		redirect("/salerManage/materialManage/delivery/deliveryList");
	}
}
