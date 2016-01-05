package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.OrderAppraise;
import com.meishi.model.Store;

/**
 * 店铺查询所有评价
 * Created by Develop_RSP on 2015/2/10.
 */
@Before(StoreInteceptor.class)
public class OrderAppraiseManageController extends BaseController {

    /**
     *列表
     */
    public void index(){
        Store store=getSessionAttr("store");
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
        //2.
        Page<OrderAppraise> list=OrderAppraise.dao.findPageByStore(store.getStr("id"),pageNum,pageSize);
        setAttr("orderAppraiseList",list);
        render("/store/admin/appraise/list.html");
    }
}
