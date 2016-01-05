package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.OrderAppraise;
import com.meishi.model.Store;
import com.meishi.model.StoreBackAppraise;
import com.meishi.util.DateUtil;
import com.meishi.util.ToolUtil;

import java.util.Date;

/**
 * 店家回评
 * Created by Develop_RSP on 2015/2/9.
 */
@Before(StoreInteceptor.class)
public class StoreBackAppraiseController extends BaseController {

    /**
     * 店家回评页面
     */
    public void index(){
        String id= getPara("id");
        OrderAppraise orderAppraise=OrderAppraise.dao.findAppraiseById(id);

        Store store = getSessionAttr("store");
        //获取订单
        String storeId=store.getStr("id");

        //检验是否已经回复评论
        boolean isBack= StoreBackAppraise.dao.isBack(orderAppraise.getStr("order_no"),getUserIds(),storeId);
        if(isBack){
            //已经回复
        }else{
           setAttr("orderAppraise",orderAppraise);
           render("/store/admin/appraise/back.html");
        }
    }

    /**
     * 店家回评操作
     */
    public void back(){
        Store store = getSessionAttr("store");
        //获取订单
        OrderAppraise orderAppraise=OrderAppraise.dao.findById(getPara("id"),"order_no");
        String orderNo = orderAppraise.getStr("order_no");
        String storeId=store.getStr("id");
        String desc=getPara("desc");

        StoreBackAppraise appraise= new StoreBackAppraise().set("id", ToolUtil.getUuidByJdk(true));
        appraise.set("store_id",storeId);
        appraise.set("order_no",orderNo);
        appraise.set("member_id",getUserIds());
        appraise.set("appraise_desc",desc);
        appraise.set("create_time", DateUtil.format(new Date(),DateUtil.pattern_ymd_hms)).save();

        setAttr("isSuccess",true);//添加成功
        renderJson();
    }
}
