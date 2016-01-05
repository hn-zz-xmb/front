package com.meishi.front.controller.goods;

import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.model.StoreCatagory;
import com.meishi.util.JSONUtils;

import java.util.List;

//@Controller(controllerKey = "/catagory")
public class CatagoryController extends BaseController {

	private static Logger log = Logger.getLogger(CatagoryController.class);

	// 查询店内商品分类
	public void findcatagory() {
		List<StoreCatagory> storeCategories = StoreCatagory.dao
				.findByStoreId(getPara("store_id"));
		renderJson(JSONUtils.toJSONString(storeCategories));
	}
}
