package com.meishi.front.controller.store.admin.material;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;
import com.meishi.util.ToolUtil;

import net.sf.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 原料商品
 * */
@Before(StoreInteceptor.class)
public class MaterialGoodsManageController extends BaseController {
	private static Logger log = Logger
			.getLogger(MaterialGoodsManageController.class);
//店铺中心原料商品列表
	public void index() {
		Store store = getSessionAttr("store");
		String status = getPara("status");
		setAttr("foodType",
				StoreCatagory.dao.findByStoreId(store.getStr("id")));
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
		params.put("materialname", getPara("materialname"));
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
		Page<Material> result = Material.dao.findMaterialByStore(pageNum,
				pageSize, params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/material/goods/goodsList.html");
	}

	// 跳转到原料新增页面
	public void addMaterial() {
		List<MaterialType> list = MaterialType.dao.findyi();// 所有原料一级分类
		Store store = getSessionAttr("store");
		List<StoreCatagory> catagories = StoreCatagory.dao.findByStoreId(store
				.getStr("id"));
		List<MaterialUnit> units = MaterialUnit.dao.findall();
		setAttr("units", units);
		setAttr("catagories", catagories);
		setAttr("materialtypeList", list);
		render("/store/admin/material/goods/goodsForm.html");
	}

	// 添加原料商品
	public void saveOrupdateMaterial() {
		Material material = getModel(Material.class);
		Store store = getSessionAttr("store");
		Object[] images = getParaValues("images");
		Object[] imagesId = getParaValues("imagesId");
		if (material.getStr("id") == "" || material.getStr("id") == null) {
			material.set("id", ToolUtil.getUuidByJdk(true))
					.set("store_id", store.getStr("id"))
					.set("upload_time",
							DateUtil.format(new Date(), "yyyyMMddHHmmss"))
					.set("region_id", store.getStr("region_id")).set("is_show", 1)
					.set("is_recommend", 0);
			new MaterialStatistics().set("id", ToolUtil.getUuidByJdk(true))
					.set("material_id", material.get("id")).set("collects", 0)
					.set("sale_num", 0).set("visits", 0).save();
			if (images != null) {
				for (int i = 0; i < images.length; i++) {
					UploadImg uploadImg = new UploadImg();
					uploadImg
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("item_id", material.getStr("id"))
							.set("store_id", store.getStr("id"))
							.set("img_type", "material")
							.set("create_time",
									DateUtil.format(new Date(),
											DateUtil.pattern_ymd_hms));

					// 生成图片
//					String[] path = GoodsImageDll.getAllImage(images[i]
//							.toString());
//					uploadImg.set("small_url", path[0])
//							.set("middle_url", path[1])
//							.set("large_url", path[2]).save();
//					material.set("material_image", path[1]);				
					
				}
			}
			material.save();
		} else {
			List<UploadImg> goodsImgs = UploadImg.dao.findBygoodId(material
					.getStr("id"));
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
								.set("item_id", material.getStr("id"))
								.set("store_id", store.getStr("id"))
								.set("img_type", "goods")
								.set("create_time",
										DateUtil.format(new Date(),
												DateUtil.pattern_ymd_hms));

						// 生成图片
//						String[] path = GoodsImageDll.getAllImage(images[i]
//								.toString());
//						uploadImg.set("small_url", path[0])
//								.set("middle_url", path[1])
//								.set("large_url", path[2]).save();
//						material.set("material_image", path[1]);
					}
				}
			}
			material.update();
			
		}
		redirect("/salerManage/materialgoodsmanage");
	}

	// 前往修改原料页面
	public void toupdateMaterial() {
		List<MaterialType> list = MaterialType.dao.findyi();// 所有原料一级分类
		Material material = Material.dao.findById(getPara("materialId"));
		List<MaterialImg> materialImgs = MaterialImg.dao
				.getMaterialImg(material.getStr("id"));
		Store store = getSessionAttr("store");
		List<StoreCatagory> storeCatagories = StoreCatagory.dao
				.findByStoreId(store.getStr("id"));
		List<MaterialUnit> units = MaterialUnit.dao.findall();
		MaterialType typeyi=MaterialType.dao.findyitype(material.getStr("material_type_id"),3);
		List<MaterialType> typeer=MaterialType.dao.findfertype(material.getStr("material_type_id"),3);
		List<MaterialType> typesan=MaterialType.dao.findfsantype(material.getStr("material_type_id"),3);
		MaterialType type=MaterialType.dao.findById(material.getStr("material_type_id"));
		setAttr("units", units);
		setAttr("material", material);
		setAttr("materialImgs", materialImgs);
		setAttr("catagories", storeCatagories);
		setAttr("materialtypeList", list);
		setAttr("type",type);
		setAttr("typeyi",typeyi);
		setAttr("typeer",typeer);
		setAttr("typesan",typesan);
		render("/store/admin/material/goods/goodsForm.html");
	}

	//查找二三级分类
	public void findersan(){
		String pid=getPara("pid");
		List<MaterialType> list = MaterialType.dao.findByPid(pid);
		renderJson(list);
	}
	
	// 商品上架
	public void shangjia() {
		Material material = Material.dao.findById(getPara("materialId"),
				"id,is_show");
		if (material.getInt("is_show") == 1) {
			renderText("fail");
		} else {
			material.set("is_show", 1).update();
			renderText("success");
		}
	}

	// 商品下架
	public void xiajia() {
		Material material = Material.dao.findById(getPara("materialId"),
				"id,is_show");
		if (material.getInt("is_show") == 0) {
			renderText("fail");
		} else {
			material.set("is_show", 0).update();
			renderText("success");
		}
	}

	/*// 商品推荐
	public void recommendMaterial() {
		List<MaterialType> materialTypes = MaterialType.dao.finds();
		setAttr("materialId", getPara("materialId"));
		setAttr("materialTypes", materialTypes);
		render("/store/admin/material/goods/recommendDialog.html");
	}

	// 商品推荐
	public void recommend() {
		JSONObject result = new JSONObject();
		Material material = Material.dao.findById(getPara("materialId"),
				"id,is_recommend");
		Store store = getSessionAttr("store");
		if (material.getInt("is_recommend") == 1) {
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
			material.set("is_recommend", 1)
					.set("material_type_id", getPara("materialType")).update();
			store.set("has_recommend", store.getInt("has_recommend") + 1)
					.update();
			result.put("status", true);
			renderJson(result.toString());
		}
	}

	// 取消商品推荐
	public void cancelRecommend() {
		Material material = Material.dao.findById(getPara("materialId"),
				"id,is_recommend");
		if (material.getInt("is_recommend") == 0) {
			renderText("fail");
		} else {
			material.set("is_recommend", 0).update();
			Store store = getSessionAttr("store");
			if (store.getInt("has_recommend") <= 0) {
				renderText("fail");
			}
			store.set("has_recommend", store.getInt("has_recommend") - 1)
					.update();
			renderText("success");
		}
	}*/

	// 商品删除
	public void deleteMaterial() {
		JSONObject result = new JSONObject();
		Material material = Material.dao.findById(getPara("materialId"));
		Material.dao.deleteMaterial(material.getStr("id"));
		result.put("status", true);
		renderJson(result.toString());
	}

}
