package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.common.dll.GoodsImgDll;
import com.meishi.front.config.PropertyConfig;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;

import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

//@Controller(controllerKey = "/store/admin/foodpackage")
@Before(StoreInteceptor.class)
public class FoodPackageManageController extends BaseController {
	private static Logger log = Logger.getLogger(FoodPackageManageController.class);

	/**
	 * 套餐列表
	 */
	public void index() {
		Store store = getSessionAttr("store");
		String status = getPara("status");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.get("id"));
		if (status == null) {
			params.put("showstatus", "");
		} else {
			if (status.equals("show")) {
				params.put("showstatus", "1");
			} else if (status.equals("hide")) {
				params.put("showstatus", "0");
			}
		}
		params.put("status", status);
		params.put("mealname", getPara("mealname"));
		
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
		Page<FoodPackage> result = FoodPackage.dao.setmealmanage(pageNum, pageSize,
				params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/foodpackage/setMealList.html");
	}

	/**
	 * 新增套餐页面
	 */
	public void saveUI() {
		Store store = getSessionAttr("store");
		setAttr("store", store);
		setAttr("action", "save");
		removeSessionAttr("foodPackageItemMap");
		render("/store/admin/foodpackage/addsetmeal.html");
	}

	public void showItem() {
		render("/store/admin/foodpackage/_showItem.html");
	}

	/**
	 * 选择商品dialog
	 */
	public void dialog() {
		Store store = Store.dao.findById(getPara("storeId"));
		List<FoodType> storeCategories = FoodType.dao
				.findByStoreId(store.getStr("id"));
		//
		setAttr("foodTypes", storeCategories);
		render("/store/admin/foodpackage/setMealDialog.html");
	}

	/**
	 * 分页加载商品
	 */
	public void loadGoodsList() {
		Store store = getSessionAttr("store");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.get("id"));
		params.put("foodType", getPara("foodType"));
		params.put("foodName", getPara("foodName"));

		Integer pageNum = getParaToInt("pageNum");
		Integer pageSize = getParaToInt("pageSize");
		if (pageNum == null || pageNum.intValue() < 1) {
			pageNum = 1;// 默认第一页
		}
		if (pageSize == null || pageSize.intValue() < 0) {
			pageSize = 6;// 默认每页显示10条
		} else if (pageSize.intValue() > 100) {
			pageSize = 6;// 最大100条
		}
		Page<Food> result = Food.dao.findSetMealGoods(pageNum, pageSize,
				params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		keepPara("div");
		keepPara("url");
		keepPara("toPage");
		render("/store/admin/foodpackage/_goodsList.html");
	}

	/**
	 * update choosed goods insert or delete
	 * 
	 */
	public void updateChooseGoods() {
		JSONObject result = new JSONObject();
		String goodsId = getPara("goodsId");
		Food food = Food.dao.findById(goodsId, "id,name,price");
		// key:goodsId,value:setMealItem
		Map<String, FoodPackageItem> foodPackageItemMap = getSessionAttr("foodPackageItemMap");
		// init
		if (foodPackageItemMap == null) {
			foodPackageItemMap = new HashMap<String, FoodPackageItem>();
		}

		if (foodPackageItemMap.containsKey(goodsId)) {
			// update count
			FoodPackageItem foodPackageItem = foodPackageItemMap.get(goodsId);
			if (foodPackageItem.getBoolean("isDelete")) {
				foodPackageItem.set("count", 1);
				foodPackageItem.put("isDelete", false);
			} else {
				foodPackageItem.set("count", foodPackageItem.getInt("count") + 1);
			}
		} else {
			// insert
			FoodPackageItem foodPackageItem = new FoodPackageItem();
			foodPackageItem.put("foodName", food.getStr("name")).put(
					"foodPrice", food.getBigDecimal("price"));
			foodPackageItem.set("food_id", goodsId).set("count", 1);
			foodPackageItemMap.put(goodsId, foodPackageItem);
		}

		setSessionAttr("foodPackageItemMap", foodPackageItemMap);
		result.put("isUpdate", true);
		renderJson(result);
	}

	/**
	 * 已经选择的商品
	 */
	public void chooseGoodsUI() {
		render("/store/admin/foodpackage/_setMealItem.html");
	}

	/**
	 * 删除已经选中的商品
	 */
	public void delChooseGoods() {
		JSONObject result = new JSONObject();
		String goodsId = getPara("goodsId");
		if (StringUtil.isBlank(goodsId)) {
			result.put("isDelete", false);
			renderJson(result);
			return;
		}
		Map<String, FoodPackageItem> foodPackageItemMap = getSessionAttr("foodPackageItemMap");
		if (foodPackageItemMap == null) {
			result.put("isDelete", false);
			renderJson(result);
			return;
		}
		// setMealItemMap.remove(goodsId);
		// 不删除标识删除
		foodPackageItemMap.get(goodsId).put("isDelete", true);
		result.put("isDelete", true);
		renderJson(result);
	}

	// 批量删除已选中的商品
	public void bathDelete() {
		JSONObject result = new JSONObject();
		Map<String, FoodPackageItem> foodPackageItemMap = getSessionAttr("foodPackageItemMap");
		if (foodPackageItemMap == null) {
			result.put("isDelete", false);
			renderJson(result);
			return;
		}
		String ids_ = getPara("ids");
		String[] goodsIds = ids_.split(",");
		for (int i = 0; i < goodsIds.length; i++) {
			String goodsId = (String) goodsIds[i];
			if (StringUtil.isNotBlank(goodsId)) {
				foodPackageItemMap.get(goodsId).put("isDelete", true);
			}
		}
		result.put("isDelete", true);
		renderJson(result);
	}

	/**
	 * 修改数量
	 */
	public void changeChooseGoodsCount() {
		JSONObject result = new JSONObject();
		String goodsId = getPara("goodsId");
		boolean isAdd = getParaToBoolean("isAdd");

		if (StringUtil.isBlank(goodsId)) {
			result.put("isChange", false);
			renderJson(result);
			return;
		}

		Map<String, FoodPackageItem> foodPackageItemMap = getSessionAttr("foodPackageItemMap");
		if (foodPackageItemMap == null) {
			result.put("isChange", false);
			renderJson(result);
			return;
		}

		if (foodPackageItemMap.containsKey(goodsId)) {
			FoodPackageItem foodPackageItem = foodPackageItemMap.get(goodsId);
			if (isAdd) {
				foodPackageItem.set("count", foodPackageItem.getInt("count") + 1);
			} else {
				if (foodPackageItem.getInt("count") <= 1) {
					// setMealItemMap.remove(goodsId);
					foodPackageItemMap.get(goodsId).put("isDelete", true);
					result.put("isRemove", true);
				} else {
					foodPackageItem.set("count", foodPackageItem.getInt("count") - 1);
				}
			}
		}
		result.put("isChange", true);
		renderJson(result);
	}

	/**
	 * 保存套餐
	 */
	public void save() {
		Store store = getSessionAttr("store");
		//Store newStore = store.findById(store.getStr("id"), "region_id");
		Object[] images = getParaValues("images");
		Map<String, FoodPackageItem> foodPackageItemMap = getSessionAttr("foodPackageItemMap");
		FoodPackage foodPackage = getModel(FoodPackage.class);
		foodPackage.set("id", ToolUtil.getUuidByJdk(true)).set("sales_count", "0")
				.set("store_id", store.get("id"))
				//.set("street_id", newStore.getStr("region_id"))
				.set("recommend", "0");

		// 计算价格
		BigDecimal totalPrice = new BigDecimal(0);
		for (FoodPackageItem item : foodPackageItemMap.values()) {
			totalPrice = totalPrice.add(item.getBigDecimal("foodPrice")
					.multiply(new BigDecimal(item.getInt("count"))));
		}
		foodPackage.set("total_price", totalPrice);
		// 图片
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				FoodImage foodImg = new FoodImage();
				foodImg
						.set("id", ToolUtil.getUuidByJdk(true))
						.set("item_id", foodPackage.getStr("id"))
						.set("store_id", store.getStr("id"))
						.set("img_type", "setmeal")
						.set("create_time",
								DateUtil.format(new Date(),
										DateUtil.pattern_ymd_hms));

				// 生成图片
//				String[] path = GoodsImageDll.getAllImage(images[i].toString());
//				foodImg.set("small_url", path[0]).set("middle_url", path[1])
//						.set("large_url", path[2]).save();
				
				String path = GoodsImgDll.getMiniImage(images[i].toString());
				String path1=GoodsImgDll.getSmallImage(images[i].toString());
				//String path2=GoodsImgDll.getLargeImage(images[i].toString());
				foodImg.set("small_image", path)
					.save();
				foodPackage.set("image", path1);
					//.set("image_big", path2);
			}
		}

		foodPackage.set("create_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms));
		FoodPackage.dao.save(foodPackage, foodPackageItemMap);
		removeSessionAttr("foodPackageItemMap");
		redirect("/salerManage/foodpackage");
	}

	/**
	 * 跳转到编辑套餐页面
	 */
	public void updateUI() {
		Store store = getSessionAttr("store");
		setAttr("store", store);
		FoodPackage foodPackage = FoodPackage.dao.findById(getPara("id"));
		List<FoodImage> foodImgs = FoodImage.dao.findBygoodId(foodPackage
				.getStr("id"));
		/**
		 * 查询编辑套餐
		 */
		Map<String, FoodPackageItem> itemsMap = FoodPackageItem.dao
				.findBySetmeal(foodPackage.getStr("id"));
		setSessionAttr("foodPackageItemMap", itemsMap);
		String description = StringEscapeUtils.unescapeHtml4(foodPackage.getStr("description"));
		setAttr("description",description);
		setAttr("goodsImgs", foodImgs);
		setAttr("foodPackage", foodPackage);
		setAttr("action", "update");
		render("/store/admin/foodpackage/addsetmeal.html");
	}

	/**
	 * 更新套餐
	 */
	public void update() {
		FoodPackage foodPackage = getModel(FoodPackage.class);
		Object[] images = getParaValues("images");
		Object[] imagesId = getParaValues("imagesId");
		Map<String, FoodPackageItem> foodPackageItemMap = getSessionAttr("foodPackageItemMap");
		BigDecimal totalPrice = new BigDecimal(0);
		for (FoodPackageItem item : foodPackageItemMap.values()) {
			totalPrice = totalPrice.add(item.getBigDecimal("foodPrice")
					.multiply(new BigDecimal(item.getInt("count"))));
		}
		foodPackage.set("total_price", totalPrice);
		// 图片
		/*
		 * String root = PropertyConfig.me().getProperty("config.image");
		 * UploadImg.dao.deleteById(setMeal.getStr("id"), root);
		 */
		List<FoodImage> foodImgs = FoodImage.dao.findBygoodId(foodPackage
				.getStr("id"));
		Store store = getSessionAttr("store");
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				int count = 0;
				for (FoodImage upimg : foodImgs) {
					if (upimg.getStr("id").equals(imagesId[i].toString())) {
						count++;
						break;
					}
				}
				if (count == 0) {
					FoodImage foodImg = new FoodImage();
					foodImg
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("item_id", foodPackage.getStr("id"))
							.set("store_id", store.getStr("id"))
							.set("img_type", "foodPackage")
							.set("create_time",
									DateUtil.format(new Date(),
											DateUtil.pattern_ymd_hms));

					// 生成图片
//					String[] path = GoodsImageDll.getAllImage(images[i]
//							.toString());
//					uploadImg.set("small_url", path[0])
//							.set("middle_url", path[1])
//							.set("large_url", path[2]).save();
					String path = GoodsImgDll.getMiniImage(images[i].toString());
					String path1=GoodsImgDll.getSmallImage(images[i].toString());
					String path2=GoodsImgDll.getLargeImage(images[i].toString());
					foodImg.set("small_image", path)
					.set("large_image", path2)
						.save();
					foodPackage.set("image", path1);
						//.set("image_big", path2);
				}
			}
		}

		foodPackage.set("update_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms));
		FoodPackage.dao.update(foodPackage, foodPackageItemMap);
		removeSessionAttr("foodPackageItemMap");
		redirect("/salerManage/foodpackage");
	}

	// 删除套餐
	public void deleteSetMeal() {
		String root = PropertyConfig.me().getProperty("config.pc");
//		new FoodImage().deleteByItemId(getPara("setMealId"), root);// 删除商品的图片
		
		//判断，当该商品是已经推荐的商品，需要先取消推荐然后在删除商品
		JSONObject result = new JSONObject();
		FoodPackage foodP = FoodPackage.dao.findById(getPara("setMealId"));
		FoodPackage foodPackage=FoodPackage.dao.findFoodPackageByFoodId(foodP.getStr("id"));
		if (foodPackage != null) {
			result.put("status", false);
			result.put("error", "该商品已推荐，请先取消商品推荐");
			renderJson(result.toString());
			return;
		}
		
		FoodPackage.dao.deleteSetMeal(getPara("setMealId"));
		result.put("status", true);
		renderJson(result.toString());
		//redirect("/salerManage/foodpackage");
	}

	// 推荐套餐
	public void recommendSetMeal() {
		List<SetMealCategory> categories = SetMealCategory.dao
				.findallSetMealCategory();
		setAttr("category", categories);
		setAttr("foodpackageId", getPara("foodpackageId"));
		render("/store/admin/foodpackage/recommendDialog.html");
	}

	public void recommend() {
		JSONObject result = new JSONObject();
		FoodPackage foodPackage = FoodPackage.dao.findById(getPara("foodpackageId"),
				"id,recommend");
		Store store = getSessionAttr("store");
		if (foodPackage.getStr("recommend") == "1") {
			result.put("status", false);
			result.put("error", "操作有误");
			renderJson(result.toString());
			return;
		}
		if (store.getInt("may_recommend_sm") - store.getInt("has_recommend_sm") <= 0) {
			result.put("status", false);
			result.put("error", "对不起您的推荐栏位已满");
			renderJson(result.toString());
			return;
		} else {
			foodPackage.set("recommend", "1")
					.set("category", getPara("setmealType")).update();
			store.set("has_recommend_sm", store.getInt("has_recommend_sm") + 1)
					.update();
			result.put("status", true);
			renderJson(result.toString());
		}
	}

	// 取消套餐推荐
	public void cancelRecommend() {
		FoodPackage foodPackage = FoodPackage.dao.findById(getPara("foodpackageId"),
				"id,recommend");
		if (foodPackage.getStr("recommend") == "0") {
			renderText("fail");
		} else {
			foodPackage.set("recommend", "0").update();
			Store store = getSessionAttr("store");
			if (store.getInt("has_recommend_sm") <= 0) {
				renderText("fail");
			}
			store.set("has_recommend_sm", store.getInt("has_recommend_sm") - 1)
					.update();
			renderText("success");
		}
	}
}
