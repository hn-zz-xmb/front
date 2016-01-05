package com.meishi.front.controller.goods;

import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.model.CartItem;
import com.meishi.model.Food;
import com.meishi.model.FoodPackage;
import com.meishi.model.Goods;
import com.meishi.model.Store;
import com.meishi.util.DateUtil;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;
import java.util.*;

//@Controller(controllerKey = "/cart")
public class CartController extends BaseController {

	private static Logger log = Logger.getLogger(CartController.class);

	/************************ 单店铺购物车 *******************************/
	/**
	 * 显示菜单
	 */
	public void index() {
		String storeId = getPara("storeId");
		if (getUserIds() != null && !"".equals(getUserIds())) {
			Map<String, CartItem> cartItemMap = CartItem.dao
					.findByMemberAndStoreToMap(getUserIds(), storeId);
			setSessionAttr("cartItems_", cartItemMap);
		}

		setAttr("storeId", storeId);
		render("/store/_cart.html");
	}

	/**
	 * 改數量
	 */
	public void changeCount() {
		JSONObject result = new JSONObject();
		String cartItemId = getPara("cartItemId");
		String storeId = getPara("storeId");
		// 是否登录
		if (getUserIds() != null && !"".equals(getUserIds())) {
			CartItem cartItem = CartItem.dao.findById(cartItemId);
			// 添加或者删除
			boolean isAdd = getParaToBoolean("isAdd");
			if (isAdd) {
				int count = cartItem.getInt("item_num") + 1;
				boolean isChange = cartItem.set("item_num", count).update();
				if (isChange) {
					result.put("isChange", true);
					result.put("count", count);
				} else {
					result.put("isChange", false);
				}
			} else {
				// 数量不足直接删除
				if (cartItem.getInt("item_num") <= 1) {
					cartItem.delete();
					result.put("isChange", true);
					result.put("count", 0);
				} else {
					int count = cartItem.getInt("item_num") - 1;
					boolean isChange = cartItem.set("item_num", count).update();
					if (isChange) {
						result.put("isChange", true);
						result.put("count", count);
					} else {
						result.put("isChange", false);
					}
				}
			}
			result.put("total", calTotalMoney(getUserIds(), storeId));
			result.put("totalCount", calTotalCount(getUserIds(), storeId));
		} else {
			Map<String, CartItem> cartItems = getSessionAttr("cartItems_");

			boolean isAdd = getParaToBoolean("isAdd");
			if (isAdd) {
				int count = cartItems.get(cartItemId).getInt("item_num") + 1;
				cartItems.get(cartItemId).set("item_num", count);
				result.put("isChange", true);
				result.put("count", count);
			} else {
				// 数量不足直接删除
				if (cartItems.get(cartItemId).getInt("item_num") <= 1) {
					cartItems.remove(cartItemId);
					result.put("isChange", true);
					result.put("count", 0);
				} else {
					int count = cartItems.get(cartItemId).getInt("item_num") - 1;
					cartItems.get(cartItemId).set("item_num", count);
					result.put("isChange", true);
					result.put("count", count);
				}
			}

			setSessionAttr("cartItems_", cartItems);
			result.put("total", calTotalMoney(null, storeId));
			result.put("totalCount", calTotalCount(getUserIds(), storeId));
		}

		renderJson(result.toString());
	}

	/**
	 * 删除购物车某一个商品
	 */
	public void remove() {
		JSONObject result = new JSONObject();
		String cartItemId = getPara("cartItemId");
		String storeId = getPara("storeId");
		// 是否登录
		if (getUserIds() != null && !"".equals(getUserIds())) {
			boolean isDelete = CartItem.dao.findById(cartItemId).delete();
			if (isDelete) {
				result.put("isDelete", true);
			} else {
				result.put("isDelete", false);
			}
			result.put("total", calTotalMoney(getUserIds(), storeId));
			result.put("totalCount", calTotalCount(getUserIds(), storeId));
		} else {
			Map<String, CartItem> cartItems = getSessionAttr("cartItems_");

			cartItems.remove(cartItemId);
			result.put("isDelete", true);
			result.put("total", calTotalMoney(null, storeId));
			result.put("totalCount", calTotalCount(getUserIds(), storeId));
		}

		renderJson(result.toString());
	}

	/**
	 * 清空我的购物车
	 */
	public void clear() {
		JSONObject result = new JSONObject();
		String storeId = getPara("storeId");

		// 是否登录
		if (getUserIds() != null && !"".equals(getUserIds())) {
			CartItem.dao.delete(getUserIds(), storeId);
			result.put("isClear", true);
		} else {
			removeSessionAttr("cartItems_");
			result.put("isClear", true);
		}
		result.put("total", 0.00);
		result.put("totalCount", 0);
		renderJson(result.toString());
	}

	/**
	 * 加入购物车 storeId,itemId,isFoodPackage
	 */
	public void add() {
		JSONObject result = new JSONObject();
		String storeId = getPara("storeId");
		String itemId = getPara("itemId");
		String isFoodPackage = getPara("isSetMeal");// 是否为套餐
		// 是否登录
		if (getUserIds() != null && !"".equals(getUserIds())) {
			// 查询是否存在cart
			CartItem cartItem = CartItem.dao.findByStoreAndItem(storeId,
					itemId, getUserIds());
			if (cartItem == null) {
				CartItem tmp = new CartItem()
						.set("id", ToolUtil.getUuidByJdk(true))
						.set("member_id", getUserIds())
						.set("store_id", storeId)
						.set("item_id", itemId)
						.set("create_date",
								DateUtil.format(new Date(), "yyyyMMddHHmmss"))
						.set("item_num", 1)
						.set("is_set_meal", Integer.parseInt(isFoodPackage));// 是否为套餐

				if ("1".equals(isFoodPackage)) {
					FoodPackage foodPackage = FoodPackage.dao
							.findById(itemId, "name,price,image");
					tmp.set("item_name", foodPackage.getStr("name"))
							.set("item_price", foodPackage.getBigDecimal("price"))
							.set("item_img", foodPackage.getBigDecimal("image"));

				} else if ("0".equals(isFoodPackage)) {
					Food goods = Food.dao
							.findById(itemId, "name,price");
					tmp.set("item_name", goods.getStr("name"))
							.set("item_price", goods.getBigDecimal("price"))
							.set("item_img", goods.getBigDecimal("image"));
				}
				tmp.save();
			} else {
				cartItem.set("item_num", cartItem.getInt("item_num") + 1)
						.update();
			}
			result.put("isAdd", true);
			result.put("total", calTotalMoney(getUserIds(), storeId));
			result.put("totalCount", calTotalCount(getUserIds(), storeId));
		} else {
			Map<String, CartItem> cartItems = getSessionAttr("cartItems_");
			// 不存在项目
			if (cartItems == null) {
				cartItems = new HashMap<String, CartItem>();
			}

			// 如果已经存在，则添加数量
			String cartItemId = checkGoodsIsExist(cartItems, itemId);
			if (!"".equals(cartItemId)) {
				int count = cartItems.get(cartItemId).getInt("item_num") + 1;
				cartItems.get(cartItemId).set("item_num", count);
			} else {
				cartItemId = ToolUtil.getUuidByJdk(true);

				CartItem cartItem = new CartItem()
						.set("id", cartItemId)
						.set("store_id", storeId)
						.set("item_id", itemId)
						.set("item_num", 1)
						.set("create_date",
								DateUtil.format(new Date(), "yyyyMMddHHmmss"))
						.set("is_set_meal", Integer.parseInt(isFoodPackage));// 是否为套餐
				// 查询店铺信息
				Store store = Store.dao.findById(storeId, "id,name");
				cartItem.put("storename", store.getStr("name"));

				if ("1".equals(isFoodPackage)) {
					FoodPackage foodPackage = FoodPackage.dao
							.findById(itemId, "name,price,image");
					cartItem.put("itemname", foodPackage.getStr("name")).put(
							"itemprice", foodPackage.getBigDecimal("price"));
					cartItems.put(cartItemId, cartItem);
				} else if ("0".equals(isFoodPackage)) {
					Food goods = Food.dao
							.findById(itemId, "name,price");
					cartItem.put("itemname", goods.getStr("name")).put(
							"itemprice", goods.getBigDecimal("price"));
					cartItems.put(cartItemId, cartItem);
				}
			}
			setSessionAttr("cartItems_", cartItems);
			result.put("isAdd", true);
			result.put("total", calTotalMoney(null, storeId));
			result.put("totalCount", calTotalCount(getUserIds(), storeId));
		}

		renderJson(result.toString());
	}

	/**
	 * 计算总价
	 * 
	 * @return
	 */
	private BigDecimal calTotalMoney(String memberId, String storeId) {
		BigDecimal total = new BigDecimal(0.00);
		// 是否登录
		if (memberId != null && !"".equals(memberId)) {
			total = CartItem.dao.getTotalMoney(memberId, storeId);
		} else {
			Map<String, CartItem> cartItems = getSessionAttr("cartItems_");
			for (CartItem t : cartItems.values()) {
				if (t.getStr("store_id").equals(storeId)) {
					total = total.add(t.getBigDecimal("itemprice").multiply(
							new BigDecimal(t.getInt("item_num"))));
				}
			}
		}

		if (total == null) {
			total = new BigDecimal(0.00);
		}
		return total;
	}

	/**
	 * 计算数量
	 * 
	 * @return
	 */
	private int calTotalCount(String memberId, String storeId) {
		Integer totalCount = 0;
		// 是否登录
		if (memberId != null && !"".equals(memberId)) {
			totalCount = CartItem.dao.getTotalCount(memberId, storeId);
		} else {
			Map<String, CartItem> cartItems = getSessionAttr("cartItems_");
			if (cartItems == null) {
				cartItems = new HashMap<String, CartItem>();
			}
			for (CartItem t : cartItems.values()) {
				if (t.getStr("store_id").equals(storeId)) {
					totalCount += t.getInt("item_num");
				}
			}
		}
		return totalCount == null ? 0 : totalCount;
	}

	/**
	 * 初始化店铺出世诵读佛经联赛等级分克利斯朵夫角色看到了解放类似的科技离开 散大夫init count
	 */
	public void getTotalCount() {
		JSONObject result = new JSONObject();
		String storeId = getPara("storeId");
		int count = calTotalCount(getUserIds(), storeId);
		result.put("count", count);
		renderJson(result);
	}

	/**
	 * 验证购物车里面的商品是否存在
	 * 
	 * @param cartItemMap
	 * @return 商品Id
	 */
	private String checkGoodsIsExist(Map<String, CartItem> cartItemMap,
			String itemId) {
		String temp = "";
		for (CartItem cartItem : cartItemMap.values()) {
			if (cartItem.getStr("item_id").equals(itemId)) {
				temp = cartItem.getStr("id");
				break;
			}
		}
		return temp;
	}

	/**
	 * 点击结算
	 */
	public void toWriteOrder() {
		JSONObject result = new JSONObject();
		// 验证购物车是否有商品
		Map<String, CartItem> cartItemMap = getSessionAttr("cartItems_");
		if (cartItemMap == null || cartItemMap.size() <= 0) {
			result.put("isCart", false);
			result.put("isLogin", false);
			result.put("error", "请先确定购物车有商品");
			renderJson(result);
			return;
		}

		// 验证是否登陆
		if (StringUtil.isBlank(getUserIds())) {
			result.put("isCart", true);
			result.put("isLogin", false);
			result.put("error", "请先登录");
			renderJson(result);
			return;
		}

		result.put("isCart", true);
		result.put("isLogin", true);
		renderJson(result.toString());
	}

	/************************ 单店铺购物车 *******************************/

	/************************ 购物车所有商品列表 *******************************/

	/**
	 * 显示购物车列表
	 */
	public void list() {
		// 按店铺读取购物车商品
		if (StringUtil.isNotBlank(getUserIds())) {
			List<CartItem> cartItemList = CartItem.dao
					.findListByMember(getUserIds());
			// 处理店铺列表
			Map<String, Store> storeMap = new HashMap<>();
			for (CartItem item : cartItemList) {
				if (storeMap.containsKey(item.getStr("store_id"))) {
					continue;
				}
				Store store = new Store();
				store.set("name", item.getStr("storename"));
				store.set("id", item.getStr("store_id"));
				store.put("storeaddress", item.getStr("storeaddress"));
				store.put("storedesc", StringEscapeUtils.unescapeHtml4(item
						.getStr("storedesc")));
				store.put("storetel", item.getStr("storetel"));
				store.put("storeimg", item.getStr("storeimg"));
				storeMap.put(item.getStr("store_id"), store);
			}
			setAttr("storeMap_", storeMap);
			setAttr("cartItemList_", cartItemList);
		} else {
			// 处理店铺列表
			Map<String, Store> storeMap = new HashMap<>();
			List<CartItem> cartItemList = new ArrayList<>();

			Map<String, CartItem> cartItemMap = getSessionAttr("cartItems_");
			if (cartItemMap == null) {
				cartItemMap = new HashMap<>();
			}
			for (CartItem item : cartItemMap.values()) {
				cartItemList.add(item);
				if (storeMap.containsKey(item.getStr("store_id"))) {
					continue;
				}
				Store store = new Store();
				store.set("name", item.getStr("storename"));
				store.set("id", item.getStr("store_id"));
				store.put("storeaddress", item.getStr("storeaddress"));
				store.put("storedesc", StringEscapeUtils.unescapeHtml4(item
						.getStr("storedesc")));
				store.put("storetel", item.getStr("storetel"));
				store.put("storeimg", item.getStr("storeimg"));
				storeMap.put(item.getStr("store_id"), store);
			}
			setAttr("storeMap_", storeMap);
			setAttr("cartItemList_", cartItemList);
		}
		render("/goods/cartlist.html");
	}

	/**
	 * 删除本店铺所有购物车信息
	 */
	public void deleteByStore() {
		JSONObject result = new JSONObject();
		String storeId = getPara("store_id");
		if (StringUtil.isNotBlank(getUserIds())) {
			CartItem.dao.delete(getUserIds(), storeId);
			result.put("isDelete", true);
		} else {
			Map<String, CartItem> cartItemMap = getSessionAttr("cartItems_");
			for (CartItem item : cartItemMap.values()) {
				if (item.getStr("store_id").equals(storeId)) {
					cartItemMap.remove(item.getStr("id"));
				}
			}
			setSessionAttr("cartItems_", cartItemMap);
			result.put("isDelete", true);
		}
		renderJson(result);
	}

}
