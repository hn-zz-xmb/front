package com.meishi.front.controller.store.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringEscapeUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.meishi.front.common.dll.GoodsImgDll;
import com.meishi.front.config.PropertyConfig;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Food;
import com.meishi.model.FoodImage;
import com.meishi.model.FoodPackageItem;
import com.meishi.model.FoodType;
import com.meishi.model.Goods;
import com.meishi.model.GoodsStatistics;
import com.meishi.model.GoodsType;
import com.meishi.model.SetMealItem;
import com.meishi.model.Store;
import com.meishi.model.StoreCatagory;
import com.meishi.model.UploadImg;
import com.meishi.util.DateUtil;
import com.meishi.util.ToolUtil;

@Before(StoreInteceptor.class)
public class FoodManageController extends BaseController{

	/**
	 * 店铺管理中商品管理
	 */
	public void index() {
		Store store = getSessionAttr("store");
		String status = getPara("status");
		setAttr("foodType",
				FoodType.dao.findByStoreId(store.getStr("id")));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.get("id"));
		if (status == null) {
			params.put("showstatus", "");
			params.put("rstatus", "");
		} else {
			if (status.equals("show")) {
				params.put("showstatus", "1");
			} else if (status.equals("hide")) {
				params.put("showstatus", "0");
			} else if (status.equals("recommended")) {
				params.put("rstatus", "1");
			}
		}
		params.put("status", status);
		params.put("goodsname", getPara("goodsname"));
		params.put("foodType", getPara("foodType"));
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
		Page<Food> result = Food.dao.findGoodsByStore(pageNum, pageSize,
				params);
		setAttr("pageNum", pageNum);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/goods/goodslis.html");
	}
	
	/**
	 * 新增商品
	 */
	public void addProduct() {
		Store store = getSessionAttr("store");
		List<FoodType> catagories = FoodType.dao.findByStoreId(store
				.getStr("id"));
		List<GoodsType> goodsTypes = GoodsType.dao.findAllType();
		setAttr("pageNum", 1);
		setAttr("goodsTypes", goodsTypes);
		setAttr("categorys", catagories);
		setAttr("action","save");
		render("/store/admin/goods/addproduct.html");
	}
	
	public void save(){
		Food food = getModel(Food.class);
		Store store = getSessionAttr("store");
		Object[] images = getParaValues("images");
		Object[] imagesId = getParaValues("imagesId");
		
		food.set("id", ToolUtil.getUuidByJdk(true))
			.set("store_id", store.getStr("id"))
			.set("create_time", DateUtil.format(new Date(), "yyyyMMddHHmmss"))
			.set("show", 1)
			.set("recommend", 0);

		//TODO
		new GoodsStatistics().set("id", ToolUtil.getUuidByJdk(true))
				.set("goods_id", food.get("id")).set("collects", 0)
				.set("sale_num", 0).set("visits", 0).save();
//
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				FoodImage foodImage = new FoodImage();
				foodImage
						.set("id", ToolUtil.getUuidByJdk(true))
						.set("item_id", food.getStr("id"))
						.set("store_id", store.getStr("id"))
						.set("img_type", "goods")
						.set("create_time",
								DateUtil.format(new Date(),
										DateUtil.pattern_ymd_hms));

				String path = GoodsImgDll.getMiniImage(images[i].toString());
				String path1=GoodsImgDll.getSmallImage(images[i].toString());
				String path2=GoodsImgDll.getLargeImage(images[i].toString());
				foodImage.set("small_image", path)
				.set("large_image", path2)
					.save();
				food.set("image", path1);
			}
		}
		food.save();
		redirect("/salerManage/goodsmanage?pageNum=" + getParaToInt("pageNum"));
	}
	
	public void update(){
		Food food = getModel(Food.class);
		Store store = getSessionAttr("store");
		Object[] images = getParaValues("images");
		Object[] imagesId = getParaValues("imagesId");
		List<FoodImage> goodsImgs = FoodImage.dao.findBygoodId(food
				.getStr("id"));
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				int count = 0;
				for (FoodImage upimg : goodsImgs) {
					if (upimg.getStr("id").equals(imagesId[i].toString())) {
						count++;
						break;
					}
				}
				if (count == 0) {
					FoodImage foodImage = new FoodImage();
					foodImage
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("item_id", food.getStr("id"))
							.set("store_id", store.getStr("id"))
							.set("img_type", "goods")
							.set("create_time",
									DateUtil.format(new Date(),
											DateUtil.pattern_ymd_hms));
					
					String path = GoodsImgDll.getMiniImage(images[i].toString());
					String path1=GoodsImgDll.getSmallImage(images[i].toString());
					String path2=GoodsImgDll.getLargeImage(images[i].toString());
					foodImage.set("small_image", path)
						.set("large_image", path2)
						.save();
					food.set("image", path1);
				}
			}
		}
		food.set("update_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms));
		food.update();
		redirect("/salerManage/goodsmanage?pageNum=" + getParaToInt("pageNum"));
	}
	
	/**
	 * 前往编辑商品页面
	 */
	public void toUpdateGoods() {
		Food goods = Food.dao.findById(getPara("goodsId"));
		List<FoodImage> goodsImgs = FoodImage.dao.findBygoodId(goods
				.getStr("id"));
		Store store = getSessionAttr("store");
		List<FoodType> catagories = FoodType.dao.findByStoreId(store
				.getStr("id"));
		List<GoodsType> goodsTypes = GoodsType.dao.findAllType();
		setAttr("goodsTypes", goodsTypes);
		setAttr("pageNum", getParaToInt("pageNum"));
		setAttr("categorys", catagories);
		setAttr("goodsImgs", goodsImgs);
		setAttr("goods", goods);
		String description = StringEscapeUtils.unescapeHtml4(goods.getStr("description"));
		setAttr("description",description);
		setAttr("action","update");
		render("/store/admin/goods/addproduct.html");
	}
	
	/**
	 * 商品上架
	 */
	public void shangjia() {
		Food goods = Food.dao.findById(getPara("goodsId"), "id,show");
		if ("1".equals(goods.getStr("show"))) {
			renderText("fail");
		} else {
			goods.set("show", "1").update();
			renderText("success");
		}
	}
	
	/**
	 * 商品下架
	 */
	public void xiajia() {
		Food goods = Food.dao.findById(getPara("goodsId"), "id,show");
		if ("0".equals(goods.getStr("show"))) {
			goods.set("show", "1").update();
			renderText("fail");
		} else {
			goods.set("show", "0").update();
			renderText("success");
		}
	}
	
	/**
	 * 商品推荐
	 */
	public void recommendGoods() {
		List<GoodsType> goodsTypes = GoodsType.dao.findAllType();
		setAttr("goodsId", getPara("goodsId"));
		setAttr("goodsType", goodsTypes);
		render("/store/admin/goods/recommendDialog.html");
	}
	
	/**
	 * 商品推荐
	 */
	public void recommend() {
		JSONObject result = new JSONObject();
		Food food = Food.dao.findById(getPara("goodsId"), "id,recommend");
		Store store = getSessionAttr("store");
		if ("1".equals(food.getStr("recommend"))) {
			result.put("status", false);
			result.put("error", "操作有误");
			renderJson(result.toString());
			return;
		}
		if (store.getInt("may_recommend") - store.getInt("has_recommend") <= 0) {
			result.put("status", false);
			result.put("error", "对不起您的推荐栏位已满");
			renderJson(result.toString());
			return;
		} else {
			food.set("recommend", 1)
					.set("pt_type", getPara("goodsType")).update();
			store.set("has_recommend", store.getInt("has_recommend") + 1)
					.update();
			result.put("status", true);
			renderJson(result.toString());
		}
	}
	
	/**
	 * 取消商品推荐
	 */
	public void cancelRecommend() {
		Food food = Food.dao.findById(getPara("goodsId"), "id,recommend");
		if ("0".equals(food.getStr("recommend"))) {
			renderText("fail");
		} else {
			food.set("recommend", "0").update();
			Store store = getSessionAttr("store");
			if (store.getInt("has_recommend") <= 0) {
				renderText("fail");
			}
			store.set("has_recommend", store.getInt("has_recommend") - 1)
					.update();
			renderText("success");
		}
	}
	
	/**
	 * 商品删除
	 */
	public void deleteGoods() {
		JSONObject result = new JSONObject();
		Food goods = Food.dao.findById(getPara("goodsId"));
		//判断，当该商品在套餐中的时候需要先删除套餐在删除商品
		FoodPackageItem mealItem = FoodPackageItem.dao
				.findByGoodsId(goods.getStr("id"));
		if (mealItem != null) {
			result.put("status", false);
			result.put("error", "该商品在套餐中，请先删除套餐再删除商品");
			renderJson(result.toString());
			return;
		}
		//判断，当该商品是已经推荐的商品，需要先取消推荐然后在删除商品
		Food food=Food.dao.findFoodByFoodId(goods.getStr("id"));
		if (food != null) {
			result.put("status", false);
			result.put("error", "该商品已推荐，请先取消商品推荐");
			renderJson(result.toString());
			return;
		}
		
		String root = PropertyConfig.me().getProperty("config.pc");
//		FoodImage.dao.deleteByItemId(goods.getStr("id"), root);// 删除商品的图片
		Food.dao.deleteGoods(goods.getStr("id"));
		result.put("status", true);
		renderJson(result.toString());
	}
}
