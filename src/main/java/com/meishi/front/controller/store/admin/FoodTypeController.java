package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Food;
import com.meishi.model.FoodType;
import com.meishi.model.Store;
import com.meishi.model.StoreCatagory;
import com.meishi.util.DateUtil;
import com.meishi.util.ToolUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
//@Controller(controllerKey = "/store/admin/FoodType")
@Before(StoreInteceptor.class)
public class FoodTypeController extends BaseController {
	private static Logger log = Logger.getLogger(FoodTypeController.class);
	// 店内分类管理
	public void foodtype() {
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
			pageSize = 10;// 最大100条
		}
		Page<FoodType> page=FoodType.dao.pageBystore(pageNum, pageSize, params);
		setAttr("page", page);
		setAttr("params",params);
		setAttr("page_params",paramsToStr(params));
		render("/store/admin/foodtype/categoryList.html");
	}

	// 添加店内分类(前往页面)
	public void toAddCategory() {
		render("/store/admin/foodtype/categoryForm.html");
	}

	// 添加分类
	public void addCategory() {
		Store store = getSessionAttr("store");
		FoodType sto=new FoodType();
		sto.set("px", getPara("px")).set("name", getPara("name")).set("is_show",getPara("isShow")).set("store_id", store.getStr("id")).set("id",ToolUtil.getUuidByJdk(true))
		.set("create_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))		
		.save();
		redirect("/salerManage/foodtype/foodtype");
	}

	// 更新店内分类(前往更新页面)
	public void toupdateCategory() {
		FoodType foodType = FoodType.dao.findById(getPara("id"));
		setAttr("foodType", foodType);
		render("/store/admin/foodtype/categoryEdit.html");
	}

	// 更新店内分类
	public void updateCategory() {
		FoodType sto=FoodType.dao.findById(getPara("id"));
		sto.set("px", getPara("px")).set("name", getPara("name")).set("is_show", getPara("isShow"))
		.set("update_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms)).update();
		redirect("/salerManage/foodtype/foodtype");
	}

	// 删除店内分类
	public void deleteFoodType() {
		JSONObject result = new JSONObject();
		FoodType foodtype=FoodType.dao.findById(getPara("id"));
		List<Food> food=Food.dao.findByfoodPackageId(foodtype.getStr("id"));
		if(food.size()>0){
			result.put("status", false);
			result.put("error", "该分类中有商品，请先删除商品");
			renderJson(result.toString());
			return;
		}
		foodtype.set("update_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms)).set("is_delete", "y").update();
		//FoodType.dao.deleteById(getPara("id"));
		//redirect("/salerManage/foodtype/foodtype");
		result.put("status", true);
		renderJson(result.toString());
	}

	//删除选中的分类
	public void delCategories(){
		String ids=getPara("ids");
		String list[]=ids.split(",");
		for(int i=0; i<list.length;i++){
			if(list[i]!=null&& !"".equals(list[i]))
				FoodType.dao.deleteById(list[i]);
		}
		renderText("success");
	}
	//更换是否显示
	public void updateshow(){
		FoodType foodtype = FoodType.dao.findById(getPara("id"));
		if("0".equals(foodtype.getStr("is_show"))){
			foodtype.set("is_show","1").update();		
		}else {
			foodtype.set("is_show","0").update();
		}
		redirect("/salerManage/foodtype/foodtype");
	}
}
