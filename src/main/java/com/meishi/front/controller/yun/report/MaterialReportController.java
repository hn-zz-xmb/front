package com.meishi.front.controller.yun.report;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.front.vo.ComItemVo;
import com.meishi.model.Store;
import com.meishi.model.yun.*;
import com.meishi.util.DateUtil;
import com.meishi.util.StringUtil;

import java.util.*;

/**
 * Created by niao on 15-8-24.
 */
@Before(StoreInteceptor.class)
public class MaterialReportController extends BaseController {

    /**
     * 原料报表
     */
//    public void material_warn(){
//        String store_id = getSessionAttr("storeId");
//        //查询原料库存(序号,原料名称,分类,数量)
//        List<MaterialYun> list=MaterialYun.dao.findWarnList(store_id);
//        setAttr("list",list);
//        render("/store/admin/yun/report/material/material_warn.html");
//    }


    public void instore(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime= DateUtil.format(yesterday, DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);
        params.put("oprType","in_store");

        Page<MaterialOperatorYun> page= MaterialOperatorYun.dao.findPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/material/instore_report.html");
    }

    public void inventory(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);
        params.put("oprType","inventory");

        Page<MaterialOperatorYun> page= MaterialOperatorYun.dao.findPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/material/inventory_report.html");
    }

    public void loss(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime", endTime);
        params.put("oprType","loss");

        Page<MaterialOperatorYun> page= MaterialOperatorYun.dao.findPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/material/loss_report.html");
    }

    /**
     * 原料流水表
     */
    public void material_info(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);
        params.put("materialTypeId",getPara("materialTypeId"));

        List<MaterialTypeYun> typeYunList=MaterialTypeYun.dao.findByStore(store.getStr("id"));
        List<ComItemVo> newTypeList=initType(typeYunList);
        setAttr("typeList",newTypeList);

        Page<MaterialOperatorYun> page= MaterialOperatorYun.dao.findPageByStore_info(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/material/material_info_report.html");
    }

    public void return_(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);
        params.put("oprType", "return");

        Page<MaterialOperatorYun> page= MaterialOperatorYun.dao.findPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/material/return_report.html");
    }

    /**
     * 库存一览表
     */
    public void stock_info(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
//        String date=getPara("date");
//        String beginTime=getPara("beginTime");
//        String endTime=getPara("endTime");
//
//        if(StringUtil.isBlank(date)){
//            date="0";
//        }
//
//        Date now=new Date();
//
//        switch (date){
//            case "1":
//                Calendar date_temp = Calendar.getInstance();
//                date_temp.setTime(now);
//                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);
//
//                Date yesterday=date_temp.getTime();
//                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
//                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
//                break;
//            case "2":
//                if(StringUtil.isBlank(beginTime)){
//                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
//                }
//
//                if(StringUtil.isBlank(endTime)){
//                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
//                }
//                break;
//            default:
//                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
//                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
//                break;
//        }
//
//        params.put("date",date);
//        params.put("beginTime",beginTime);
//        params.put("endTime",endTime);
        params.put("materialTypeId",getPara("materialTypeId"));

        List<MaterialTypeYun> typeYunList=MaterialTypeYun.dao.findByStore(store.getStr("id"));
        List<ComItemVo> newTypeList=initType(typeYunList);
        setAttr("typeList",newTypeList);

        Page<MaterialStockYun> page=MaterialStockYun.dao.findPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/material/stockinfo_report.html");
    }

    private List<ComItemVo> initType(List<MaterialTypeYun> typeYunList){
        List<ComItemVo> newAll=new ArrayList<>();
        handleType(typeYunList,null,null,1,newAll);
        return newAll;
    }

    private void handleType(List<MaterialTypeYun> child,MaterialTypeYun parent,String parentId,int level,List<ComItemVo> newAll){
        List<MaterialTypeYun> selectedList=new ArrayList<>();
        if(child!=null && child.size()>0) {
            for (MaterialTypeYun item : child) {
                if (parentId ==null || parentId.equals(item.getStr("pid"))) {
                    selectedList.add(item);
                }
            }
        }

        if(selectedList!=null && selectedList.size()>0) {
            for (MaterialTypeYun type_ : selectedList) {
                String str = "L";
                for (int i = 1; i <= level; i++) {
                    str = "  " + str;
                }
                int s = level + 1;
                newAll.add(new ComItemVo(str + type_.getStr("name"), type_.getStr("id")));
                handleType(child, type_, type_.getStr("id"), s, newAll);
            }
        }
    }
}
