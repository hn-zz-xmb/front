package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Store;
import com.meishi.model.StoreCatagory;
import com.meishi.util.ToolUtil;

import java.util.HashMap;
import java.util.Map;
//@Controller(controllerKey = "/store/admin/foodType")
@Before(StoreInteceptor.class)
public class StoreCategoryController extends BaseController {
	private static Logger log = Logger.getLogger(StoreCategoryController.class);
	// 店内分类管理
	public void storecatagory() {
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
		Page<StoreCatagory> page=StoreCatagory.dao.pageBystore(pageNum, pageSize, params);
		setAttr("page", page);
		setAttr("params",params);
		setAttr("page_params",paramsToStr(params));
		render("/store/admin/storecategory/categoryList.html");
	}

	// 添加店内分类(前往页面)
	public void toAddCategory() {
		render("/store/admin/storecategory/categoryForm.html");
	}

	// 添加分类
	public void addCategory() {
		Store store = getSessionAttr("store");
		StoreCatagory sto=new StoreCatagory();
		sto.set("px", getPara("px")).set("name", getPara("name")).set("is_show",getPara("isShow")).set("store_id", store.getStr("id")).set("id",ToolUtil.getUuidByJdk(true))
				.save();
		redirect("/salerManage/category/storecatagory");
	}

	// 更新店内分类(前往更新页面)
	public void toupdateCategory() {
		StoreCatagory storeCategory = StoreCatagory.dao.findById(getPara("id"));
		setAttr("storeCategory", storeCategory);
		render("/store/admin/storecategory/categoryEdit.html");
	}

	// 更新店内分类
	public void updateCategory() {
		StoreCatagory sto=StoreCatagory.dao.findById(getPara("id"));
		sto.set("px", getPara("px")).set("name", getPara("name")).set("is_show", getPara("isShow")).update();
		redirect("/salerManage/category/storecatagory");
	}

	// 删除店内分类
	public void deleteStoreCategory() {
		StoreCatagory.dao.deleteById(getPara("id"));
		redirect("/salerManage/category/storecatagory");
	}

	//删除选中的分类
	public void delCategories(){
		String ids=getPara("ids");
		String list[]=ids.split(",");
		for(int i=0; i<list.length;i++){
			if(list[i]!=null&& !"".equals(list[i]))
				StoreCatagory.dao.deleteById(list[i]);
		}
		renderText("success");
	}
	//更换是否显示
	public void updateshow(){
		StoreCatagory storeCategory = StoreCatagory.dao.findById(getPara("id"));
		if(storeCategory.getInt("is_show")==1){
			storeCategory.set("is_show",0).update();		
		}else{
			storeCategory.set("is_show",1).update();
		}
		redirect("/salerManage/category/storecatagory");
	}
}
