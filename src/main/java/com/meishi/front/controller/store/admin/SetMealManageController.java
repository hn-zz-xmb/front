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

//@Controller(controllerKey = "/store/admin/setmeal")
@Before(StoreInteceptor.class)
public class SetMealManageController extends BaseController {
	private static Logger log = Logger.getLogger(SetMealManageController.class);

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
		Page<SetMeal> result = SetMeal.dao.setmealmanage(pageNum, pageSize,
				params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/setmeal/setMealList.html");
	}

	/**
	 * 新增套餐页面
	 */
	public void saveUI() {
		Store store = getSessionAttr("store");
		setAttr("store", store);
		setAttr("action", "save");
		removeSessionAttr("setMealItemMap");
		render("/store/admin/setmeal/addsetmeal.html");
	}

	public void showItem() {
		render("/store/admin/setmeal/_showItem.html");
	}

	/**
	 * 选择商品dialog
	 */
	public void dialog() {
		Store store = Store.dao.findById(getPara("storeId"));
		List<StoreCatagory> storeCategories = StoreCatagory.dao
				.findByStoreId(store.getStr("id"));
		//
		setAttr("foodTypes", storeCategories);
		render("/store/admin/setmeal/setMealDialog.html");
	}

	/**
	 * 分页加载商品
	 */
	public void loadGoodsList() {
		Store store = getSessionAttr("store");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.get("id"));
		params.put("foodType", getPara("foodType"));
		params.put("goodsName", getPara("goodsName"));

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
		Page<Goods> result = Goods.dao.findSetMealGoods(pageNum, pageSize,
				params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		keepPara("div");
		keepPara("url");
		keepPara("toPage");
		render("/store/admin/setmeal/_goodsList.html");
	}

	/**
	 * update choosed goods insert or delete
	 */
	public void updateChooseGoods() {
		JSONObject result = new JSONObject();
		String goodsId = getPara("goodsId");
		Goods goods = Goods.dao.findById(goodsId, "id,name,sales_price");
		// key:goodsId,value:setMealItem
		Map<String, SetMealItem> setMealItemMap = getSessionAttr("setMealItemMap");
		// init
		if (setMealItemMap == null) {
			setMealItemMap = new HashMap<String, SetMealItem>();
		}

		if (setMealItemMap.containsKey(goodsId)) {
			// update count
			SetMealItem setMealItem = setMealItemMap.get(goodsId);
			if (setMealItem.getBoolean("isDelete")) {
				setMealItem.set("count", 1);
				setMealItem.put("isDelete", false);
			} else {
				setMealItem.set("count", setMealItem.getInt("count") + 1);
			}
		} else {
			// insert
			SetMealItem setMealItem = new SetMealItem();
			setMealItem.put("goodsName", goods.getStr("name")).put(
					"goodsPrice", goods.getBigDecimal("sales_price"));
			setMealItem.set("goods_id", goodsId).set("count", 1);
			setMealItemMap.put(goodsId, setMealItem);
		}

		setSessionAttr("setMealItemMap", setMealItemMap);
		result.put("isUpdate", true);
		renderJson(result);
	}

	/**
	 * 已经选择的商品
	 */
	public void chooseGoodsUI() {
		render("/store/admin/setmeal/_setMealItem.html");
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
		Map<String, SetMealItem> setMealItemMap = getSessionAttr("setMealItemMap");
		if (setMealItemMap == null) {
			result.put("isDelete", false);
			renderJson(result);
			return;
		}
		// setMealItemMap.remove(goodsId);
		// 不删除标识删除
		setMealItemMap.get(goodsId).put("isDelete", true);
		result.put("isDelete", true);
		renderJson(result);
	}

	// 批量删除已选中的商品
	public void bathDelete() {
		JSONObject result = new JSONObject();
		Map<String, SetMealItem> setMealItemMap = getSessionAttr("setMealItemMap");
		if (setMealItemMap == null) {
			result.put("isDelete", false);
			renderJson(result);
			return;
		}
		String ids_ = getPara("ids");
		String[] goodsIds = ids_.split(",");
		for (int i = 0; i < goodsIds.length; i++) {
			String goodsId = (String) goodsIds[i];
			if (StringUtil.isNotBlank(goodsId)) {
				setMealItemMap.get(goodsId).put("isDelete", true);
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

		Map<String, SetMealItem> setMealItemMap = getSessionAttr("setMealItemMap");
		if (setMealItemMap == null) {
			result.put("isChange", false);
			renderJson(result);
			return;
		}

		if (setMealItemMap.containsKey(goodsId)) {
			SetMealItem setMealItem = setMealItemMap.get(goodsId);
			if (isAdd) {
				setMealItem.set("count", setMealItem.getInt("count") + 1);
			} else {
				if (setMealItem.getInt("count") <= 1) {
					// setMealItemMap.remove(goodsId);
					setMealItemMap.get(goodsId).put("isDelete", true);
					result.put("isRemove", true);
				} else {
					setMealItem.set("count", setMealItem.getInt("count") - 1);
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
		Store newStore = store.findById(store.getStr("id"), "region_id");
		Object[] images = getParaValues("images");
		Map<String, SetMealItem> setMealItemMap = getSessionAttr("setMealItemMap");
		SetMeal setMeal = getModel(SetMeal.class);
		setMeal.set("id", ToolUtil.getUuidByJdk(true)).set("sales_volume", 0)
				.set("store_id", store.get("id"))
				.set("street_id", newStore.getStr("region_id"))
				.set("is_recommend", 0);

		// 计算价格
		BigDecimal totalPrice = new BigDecimal(0);
		for (SetMealItem item : setMealItemMap.values()) {
			totalPrice = totalPrice.add(item.getBigDecimal("goodsPrice")
					.multiply(new BigDecimal(item.getInt("count"))));
		}
		setMeal.set("original_price", totalPrice);
		// 图片
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				UploadImg uploadImg = new UploadImg();
				uploadImg
						.set("id", ToolUtil.getUuidByJdk(true))
						.set("item_id", setMeal.getStr("id"))
						.set("store_id", store.getStr("id"))
						.set("img_type", "setmeal")
						.set("create_time",
								DateUtil.format(new Date(),
										DateUtil.pattern_ymd_hms));

				// 生成图片
//				String[] path = GoodsImageDll.getAllImage(images[i].toString());
//				uploadImg.set("small_url", path[0]).set("middle_url", path[1])
//						.set("large_url", path[2]).save();
				
				String path = GoodsImgDll.getMiniImage(images[i].toString());
				String path1=GoodsImgDll.getSmallImage(images[i].toString());
				String path2=GoodsImgDll.getLargeImage(images[i].toString());
				uploadImg.set("small_url", path)
					.save();
				setMeal.set("image", path1)
					.set("image_big", path2);
			}
		}

		SetMeal.dao.save(setMeal, setMealItemMap);
		removeSessionAttr("setMealItemMap");
		redirect("/salerManage/setmeal");
	}

	/**
	 * 跳转到编辑套餐页面
	 */
	public void updateUI() {
		Store store = getSessionAttr("store");
		setAttr("store", store);
		SetMeal setMeal = SetMeal.dao.findById(getPara("id"));
		List<UploadImg> goodsImgs = UploadImg.dao.findBygoodId(setMeal
				.getStr("id"));
		/**
		 * 查询编辑套餐
		 */
		Map<String, SetMealItem> itemsMap = SetMealItem.dao
				.findBySetmeal(setMeal.getStr("id"));
		setSessionAttr("setMealItemMap", itemsMap);
		setAttr("goodsImgs", goodsImgs);
		setAttr("setMeal", setMeal);
		setAttr("action", "update");
		render("/store/admin/setmeal/addsetmeal.html");
	}

	/**
	 * 更新套餐
	 */
	public void update() {
		SetMeal setMeal = getModel(SetMeal.class);
		Object[] images = getParaValues("images");
		Object[] imagesId = getParaValues("imagesId");
		Map<String, SetMealItem> setMealItemMap = getSessionAttr("setMealItemMap");
		BigDecimal totalPrice = new BigDecimal(0);
		for (SetMealItem item : setMealItemMap.values()) {
			totalPrice = totalPrice.add(item.getBigDecimal("goodsPrice")
					.multiply(new BigDecimal(item.getInt("count"))));
		}
		setMeal.set("original_price", totalPrice);
		// 图片
		/*
		 * String root = PropertyConfig.me().getProperty("config.image");
		 * UploadImg.dao.deleteById(setMeal.getStr("id"), root);
		 */
		List<UploadImg> goodsImgs = UploadImg.dao.findBygoodId(setMeal
				.getStr("id"));
		Store store = getSessionAttr("store");
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				int count = 0;
				for (UploadImg upimg : goodsImgs) {
					if (upimg.getStr("id").equals(imagesId[i].toString())) {
						count++;
						break;
					}
				}
				if (count == 0) {
					UploadImg uploadImg = new UploadImg();
					uploadImg
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("item_id", setMeal.getStr("id"))
							.set("store_id", store.getStr("id"))
							.set("img_type", "setmeal")
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
					uploadImg.set("small_url", path)
						.save();
					setMeal.set("image", path1)
						.set("image_big", path2);
				}
			}
		}

		SetMeal.dao.update(setMeal, setMealItemMap);
		removeSessionAttr("setMealItemMap");
		redirect("/salerManage/setmeal");
	}

	// 删除套餐
	public void deleteSetMeal() {
		String root = PropertyConfig.me().getProperty("config.image");
		new UploadImg().deleteByItemId(getPara("setMealId"), root);// 删除商品的图片
		SetMeal.dao.deleteSetMeal(getPara("setMealId"));
		redirect("/salerManage/setmeal");
	}

	// 推荐套餐
	public void recommendSetMeal() {
		List<SetMealCategory> categories = SetMealCategory.dao
				.findallSetMealCategory();
		setAttr("category", categories);
		setAttr("setmealId", getPara("setmealId"));
		render("/store/admin/setmeal/recommendDialog.html");
	}

	public void recommend() {
		JSONObject result = new JSONObject();
		SetMeal setMeal = SetMeal.dao.findById(getPara("setmealId"),
				"id,is_recommend");
		Store store = getSessionAttr("store");
		if (setMeal.getInt("is_recommend") == 1) {
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
			setMeal.set("is_recommend", 1)
					.set("sm_category_id", getPara("setmealType")).update();
			store.set("has_recommend_sm", store.getInt("has_recommend_sm") + 1)
					.update();
			result.put("status", true);
			renderJson(result.toString());
		}
	}

	// 取消套餐推荐
	public void cancelRecommend() {
		SetMeal setMeal = SetMeal.dao.findById(getPara("setmealId"),
				"id,is_recommend");
		if (setMeal.getInt("is_recommend") == 0) {
			renderText("fail");
		} else {
			setMeal.set("is_recommend", 0).update();
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
