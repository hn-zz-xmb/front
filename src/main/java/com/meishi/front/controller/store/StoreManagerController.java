package com.meishi.front.controller.store;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Business;
import com.meishi.model.Member;
import com.meishi.model.Province;
import com.meishi.model.Store;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller(controllerKey = "/storemanage")
@Before(LoginInteceptor.class)
public class StoreManagerController extends BaseController {
	private static Logger log = Logger.getLogger(StoreManagerController.class);

	// 店家进入店铺管理时查询该店家开的所有店铺
	public void index() {
		Member member = Member.dao.findById(getUserIds());
		if (member != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", member.getStr("id"));
			Integer pageNum = getParaToInt("pageNum");
			Integer pageSize = getParaToInt("pageSize");
			if (pageNum == null || pageNum.intValue() < 1) {
				pageNum = 1;// 默认第一页
			}
			if (pageSize == null || pageSize.intValue() < 0) {
				pageSize = 50;// 默认每页显示10条
			} else if (pageSize.intValue() > 100) {
				pageSize = 50;// 最大100条
			}
			Page<Store> store = Store.dao.findStoreManage(pageNum, pageSize,
					params);
			setAttr("page", store);
			setAttr("param", paramsToStr(params));
			render("/store/admin/storeList.html");
		} else {
			render("/");
		}
	}

	// 店家进入自己店铺管理中心
	public void inStore() {
		Store store = Store.dao.findById(getPara("storeId"));
		if (store != null && !"".equals(store)) {
			setSessionAttr("store", store);
			if (store.getStr("store_type_id").equals(AppContextData.NORMAL)) {
				List<Store> storeList = Store.dao
						.findListByMember(getUserIds());
				setSessionAttr("storeList", storeList);
				redirect("/salerManage/ordermanage");
			} else {
				List<Store> storeList = Store.dao
						.findListByMember(getUserIds());
				setSessionAttr("storeList", storeList);
				redirect("/salerManage/materialManage/ordermanage");
			}
		}
	}

	// 店家进入自己店铺管理中心
	public void consumeCode() {
		Store store = Store.dao.findById(getPara("storeId"));
		if (store != null && !"".equals(store)) {
			setSessionAttr("store", store);
			if (store.getStr("store_type_id").equals(AppContextData.NORMAL)) {
				List<Store> storeList = Store.dao
						.findListByMember(getUserIds());
				setSessionAttr("storeList", storeList);
				redirect("/salerManage/sureOrder/makesure");
			}
		}
	}

	/**
	 * 切换店铺
	 */
	@Before(StoreInteceptor.class)
	public void changeStore() {
		JSONObject result = new JSONObject();
		Store store = Store.dao.findById(getPara("storeId"));
		if (store != null) {
			setSessionAttr("store", store);
			result.put("isChange", true);
		} else {
			result.put("isChange", false);
		}
		renderJson(result);
	}

	// 店家查看店铺申请被拒绝原因
	public void showrefuse() {
		Store store = Store.dao.findById(getPara("storeId"));
		setAttr("store", store);
		render("/store/admin/storerefuse.html");
	}

	// 前往重新申请页面
	public void toagainapply() {
		Store store = Store.dao.findById(getPara("storeId"));
		List<Business> businesses = Business.dao.findAll();
		List<Province> provinces = Province.dao.findAll();
		setAttr("businessList", businesses);
		setAttr("provinceList", provinces);
		setAttr("store", store);
		render("/store/admin/againapply.html");
	}
}
