package com.meishi.front.controller.store;

import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.model.*;
import com.meishi.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller(controllerKey = "/store")
public class StoreController extends BaseController {
	private static Logger log = Logger.getLogger(StoreController.class);

	public void index() {
		Store store = Store.dao.findOpenStoreById(getPara("storeId"));
		if (store == null) {
			render("该店铺已经被冻结");
		} else {
			List<FoodType> storeCategories = FoodType.dao
					.findByStoreId(store.getStr("id"));
			setAttr("store", store);
			setAttr("foodType", storeCategories);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("storeId", getPara("storeId"));
			params.put("foodTypeId", getPara("foodTypeId"));
			params.put("sort", getPara("sort"));
			if (StringUtil.isBlank(getPara("nav"))) {
				params.put("nav", 0);
			} else {
				params.put("nav", getParaToInt("nav"));
			}
			Integer pageNum = getParaToInt("pageNum");
			Integer pageSize = getParaToInt("pageSize");
			if (pageNum == null || pageNum.intValue() < 1) {
				pageNum = 1;// 默认第一页
			}
			if (pageSize == null || pageSize.intValue() < 0) {
				pageSize = 24;// 默认每页显示10条
			} else if (pageSize.intValue() > 100) {
				pageSize = 24;// 最大100条
			}
			Page<Food> goods = Food.dao.findStoreGoods(pageNum, pageSize,
					params);
			setAttr("goods", goods);
			setAttr("params", params);
			setAttr("page_params", paramsToStr(params));
			render("/store/store.html");
		}
	}

	// 店铺子站套餐查询
	public void searcheSetMeal() {
		Store store = Store.dao.findOpenStoreById(getPara("storeId"));
		if (store == null) {
			render("该店铺已经被冻结");
		} else {
			List<FoodType> storeCategories = FoodType.dao
					.findByStoreId(store.getStr("id"));
			setAttr("store", store);
			setAttr("foodType", storeCategories);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("storeId", getPara("storeId"));
			params.put("sort", getPara("sort"));
			if (StringUtil.isBlank(getPara("nav"))) {
				params.put("nav", 0);
			} else {
				params.put("nav", getParaToInt("nav"));
			}
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
			Page<FoodPackage> foodPackage = FoodPackage.dao.listsetmeal(pageNum, pageSize,
					params);
			setAttr("foodPackage", foodPackage);
			setAttr("params", params);
			setAttr("page_params", paramsToStr(params));
			render("/store/storetaocan.html");
		}
	}

	// 查询店内分类
	public void getCatagory() {
		List<FoodType> categories = FoodType.dao
				.findByStoreId(getPara("storeId"));
		Store store = Store.dao.findOpenStoreById(getPara("storeId"));
		setAttr("category", categories);
		setAttr("store", store);
		render("/store/category.html");
	}

	// 查询店内导航
	public void getNavigetion() {
		List<StoreNavigate> storeNavigates = StoreNavigate.dao
				.findByStoreId(getPara("storeId"));
		setAttr("navigate", storeNavigates);
		renderJson(storeNavigates);
	}

	// 进入导航页面
	public void navigetion() {
		StoreNavigate storeNavigate = StoreNavigate.dao
				.findByNavigateId(getPara("id"));
		if (storeNavigate == null) {
			renderError(404);
		}
		Store store = Store.dao.findBystoreId(storeNavigate.getStr("store_id"));
		setAttr("store", store);
		setAttr("navigate", storeNavigate);
		render("/store/navigation.html");
	}

    /**
     * 加载活动列表页面
     */
    public void findStoreActive(){
        String storeId=getPara("storeId");
        List<ActiveProgram> programList=ActiveProgram.dao.findByStoreId(storeId);
        setAttr("activeProgramList",programList);
        renderJson();
    }
}
