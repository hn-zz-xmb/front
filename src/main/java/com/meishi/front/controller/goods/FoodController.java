package com.meishi.front.controller.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.model.Advert;
import com.meishi.model.City;
import com.meishi.model.Food;
import com.meishi.model.FoodImage;
import com.meishi.model.GoodsStatistics;
import com.meishi.model.OrderAppraise;
import com.meishi.model.Orders;
import com.meishi.model.Store;
import com.meishi.util.StringUtil;

public class FoodController extends BaseController{

	/**
	 * 查看商品详情
	 */
	public void view() {
		String id = getPara("id");
		if (StringUtil.isNotBlank(id)) {
			City city = getSessionAttr("cur_city");
			if (city == null) {
				city = City.dao.findByName("郑州市");
			}
			List<Advert> advertAd = Advert.dao.findBy("main_pro",
					"goodsdetail_r", city.getStr("id"));
			Food food = Food.dao.findById(id);

			if (food != null) {
                String description=food.getStr("description");
                description= StringUtil.isBlank(description)?"":description;
                food.set("description", StringEscapeUtils.unescapeHtml4(description));

                List<FoodImage> goodsImgs=FoodImage.dao.findBygoodId(food.getStr("id"));
				
				GoodsStatistics goodsStatistics = GoodsStatistics.dao
						.findByGoodsId(food.getStr("id"));
				Store store = Store.dao.findOpenStoreById(food
						.getStr("store_id"));
				setAttr("food", food);
				setAttr("advertAd", advertAd);
				setAttr("goodsImgs", goodsImgs);
				setAttr("goodsStatistics", goodsStatistics);
				setAttr("store", store);
				render("/goods/商品详情.html");
			}
		}
	}
	
	 /**
     * 加载用户评价信息
     * Ajax load
     *
     */
    public void loadOrderAppraise(){
        String storeId=getPara("storeId");
        Map params=new HashMap<>();
        params.put("storeId",storeId);
        //1.分页查询
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
        Page<OrderAppraise> orderAppraisePage=OrderAppraise.dao.findPageByStore(storeId,pageNum,pageSize);
        setAttr("orderAppraisePage",orderAppraisePage);
        setAttr("url","goods/loadOrderAppraise");
        setAttr("div","tab2");
        setAttr("page_params", paramsToStr(params));
        render("/goods/_detail_appraise.html");
    }

    /**
     * 加载购买记录
     * Ajax load
     */
    public void loadGoodsBugHistory(){
        String goodsId=getPara("goodsId");
        Map params=new HashMap<>();
        params.put("goodsId",goodsId);
        //1.分页查询
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
        Page<Orders> ordersPage=Orders.dao.findGoodsBuyHistory(goodsId,pageNum,pageSize);
        setAttr("goodsBugHistoryPage",ordersPage);
        setAttr("url","goods/loadGoodsBugHistory");
        setAttr("div","tab3");
        setAttr("page_params", paramsToStr(params));
        render("/goods/_detail_buy_history.html");
    }
}
