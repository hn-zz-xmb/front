package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.config.PropertyConfig;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Store;
import com.meishi.model.WeixinStoreCarereply;
import com.meishi.model.WeixinStoreMatter;
import com.meishi.util.DateUtil;
import com.meishi.util.FileUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Date;
import java.util.List;
/**
 * Created by rsp on 14-11-19.
 */
@Before(StoreInteceptor.class)
public class WxMatterController extends BaseController {
    private static Logger log = Logger.getLogger(WxMatterController.class);
    public void index() {
        Store store = getSessionAttr("store");
        List<WeixinStoreMatter> mattersNotChange=WeixinStoreMatter.dao.findListByStore(store.getStr("id"),0);
        List<WeixinStoreMatter> matters=WeixinStoreMatter.dao.findListByStore(store.getStr("id"),1);
        setAttr("mattersNotChange",mattersNotChange);
        setAttr("matters",matters);
        render("/store/admin/weixin/tuwenList.html");
    }

    public void saveUI() {
        setAttr("action","save");
        render("/store/admin/weixin/tuwenForm.html");
    }

    public void save() {
        Store store = getSessionAttr("store");
        String id=ToolUtil.getUuidByJdk(true);
        WeixinStoreMatter weixinStoreMatter=getModel(WeixinStoreMatter.class).set("id",id )
                .set("store_id",store.getStr("id"))
                .set("create_time", DateUtil.format(new Date(),DateUtil.pattern_ymd_hms))
                .set("url",PropertyConfig.me().getProperty("config.wxDomain")+"/saler/matter/detail?matterId="+id)
                .set("is_change", 1);
        String url = weixinStoreMatter.getStr("pic_url");
        String temp = PropertyConfig.me().getProperty("config.temp");
        String pc = PropertyConfig.me().getProperty("config.pc");
        String wx = PropertyConfig.me().getProperty("config.wx");
        String path = FileUtils.generateDateDir(pc) + "/"+ ToolUtil.getUuidByJdk(true) + ".jpg";
        FileUtils.Copy(temp+url, pc+path);
        FileUtils.generateDateDir(wx);
        FileUtils.Copy(temp + url, wx + path);
        weixinStoreMatter.set("pic_url", path);
        weixinStoreMatter.save();
        redirect("/salerManage/weixin/matter");
    }

    public void updateUI() {
        setAttr("action", "update");
        WeixinStoreMatter matter=WeixinStoreMatter.dao.findById(getPara("id"));
        matter.set("text", StringEscapeUtils.unescapeHtml4(matter.getStr("text")));
        setAttr("matter",matter);
        render("/store/admin/weixin/tuwenForm.html");
    }


    public void update() {
        WeixinStoreMatter weixinStoreMatter = getModel(WeixinStoreMatter.class);
        String url = weixinStoreMatter.getStr("pic_url");

        WeixinStoreMatter temp_matter = WeixinStoreMatter.dao.findById(weixinStoreMatter.getStr("id"));
        if (StringUtil.isNotBlank(weixinStoreMatter.getStr("pic_url"))
                && !temp_matter.getStr("pic_url").equals(weixinStoreMatter.getStr("pic_url"))) {
            String temp = PropertyConfig.me().getProperty("config.temp");
            String pc = PropertyConfig.me().getProperty("config.pc");
            String wx = PropertyConfig.me().getProperty("config.wx");
            String path = FileUtils.generateDateDir(pc) + "/"+ ToolUtil.getUuidByJdk(true) + ".jpg";
            FileUtils.Copy(temp + url, pc + path);
            FileUtils.generateDateDir(wx);
            FileUtils.Copy(temp + url, wx + path);
            weixinStoreMatter.set("pic_url", path);
        }
        //weixinStoreMatter.set("url", PropertyConfig.me().getProperty("config.wxDomain") + "/saler/matter/detail?matterId=" + temp_matter.getStr("id"));
        weixinStoreMatter.update();
        redirect("/salerManage/weixin/matter");
    }

    public void delete() {
        JSONObject result=new JSONObject();
        boolean isDelete=WeixinStoreMatter.dao.deleteById(getPara("id"));
        if(isDelete) {
            result.put("isDelete",true);
            renderJson(result);
            return;
        }
        result.put("isDelete",false);
        renderJson(result);
    }

    /**
     * 设置为关注回复
     */
    public void setCare(){
        JSONObject result=new JSONObject();
        Store store =getSessionAttr("store");
        WeixinStoreCarereply carereply=WeixinStoreCarereply.dao.findByStoreAndMatter(store.getStr("id"),getPara("id"));

        if(carereply==null) {
            carereply=new WeixinStoreCarereply();
            carereply.set("id", ToolUtil.getUuidByJdk(true))
                    .set("store_id", store.getStr("id"))
                    .set("matter_id", getPara("id")).save();
            result.put("isCare",true);
        }else {
            carereply.set("matter_id",getPara("id")).update();
            result.put("isCare",true);
        }
        renderJson(result);
    }
    
    public void list(){
    	Store store = getSessionAttr("store");
    	List<WeixinStoreMatter> matters=WeixinStoreMatter.dao.findListByStore(store.getStr("id"),1);
    	setAttr("matterList",matters);
    	renderJson();
    }
}
