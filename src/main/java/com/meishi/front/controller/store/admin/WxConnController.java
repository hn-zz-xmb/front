package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.common.dll.WeixinStoreImgDll;
import com.meishi.front.config.PropertyConfig;
import com.meishi.front.controller.BaseController;
import com.meishi.front.ext.weixin.api.AccessToken;
import com.meishi.front.ext.weixin.api.AccessTokenApi;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Store;
import com.meishi.model.WeixinStoreConn;
import com.meishi.model.WeixinStoreMatter;
import com.meishi.model.WeixinStoreMenu;
import com.meishi.util.DateUtil;
import com.meishi.util.FileUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;

import net.sf.json.JSONObject;

import java.util.Date;

/**
 * Created by rsp on 14-11-20.
 * 对接微信
 */
@Before(StoreInteceptor.class)
public class WxConnController extends BaseController {
    private static Logger log = Logger.getLogger(WxConnController.class);

    public void index() {
        Store store = getSessionAttr("store");
        setAttr("weixinStoreConn", WeixinStoreConn.dao.findByStore(store.getStr("id")));
        render("/store/admin/weixin/weixin.html");
    }

    /**
     * 保存
     */
    public void save() {
        JSONObject result=new JSONObject();
        Store store = getSessionAttr("store");
        WeixinStoreConn conn=getModel(WeixinStoreConn.class);
//        String images = conn.getStr("wechat_logo_img");
        AccessToken at = AccessTokenApi.getAccessToken(conn.getStr("app_id"),conn.getStr("app_secret"));
        if (!at.isAvailable()) {
            result.put("status", "n");
            result.put("error", "请检查你的appid和appSecret");
        }
        else {
            if(conn.getStr("id")!=null && !"".equals(conn.getStr("id"))){//更改
            	WeixinStoreConn weixinStoreConn = WeixinStoreConn.dao.findById(conn.getStr("id"));
            	String wechatLogoImg = conn.getStr("wechat_logo_img");
            	if(wechatLogoImg!=null && !wechatLogoImg.equals(weixinStoreConn.getStr("wechat_logo_img"))){
            		String path=WeixinStoreImgDll.getImage(wechatLogoImg);
                	conn.set("wechat_logo_img", path);
                	store.set("wechat_logo_img", path);
            	}
            	
            	String codeImage = conn.getStr("code_image");
            	if(codeImage!=null && !codeImage.equals(weixinStoreConn.getStr("code_image"))){
            		String old_root = PropertyConfig.me().getProperty("config.temp");
            		String oldPath = old_root + conn.getStr("code_image");

            		String root = PropertyConfig.me().getProperty("config.pc");
            		String loadPath = FileUtils.generateDateDir(root) + "/"
            				+ ToolUtil.getUuidByJdk(true) + ".jpg";
            		String newPath = root + loadPath;
            		
            		FileUtils.Copy(oldPath, newPath);
            		store.set("code_image",loadPath);
            		conn.set("code_image", loadPath);
            	}
            	store.update();
                conn.update();
            }else{//保存
                conn.set("id",ToolUtil.getUuidByJdk(true))
                        .set("store_id", store.getStr("id"))
                        .set("url", PropertyConfig.me().getProperty("config.wxConn")+"/"+store.getStr("id"))
                        .set("token",ToolUtil.getUuidByJdk(true));
                
                String old_root = PropertyConfig.me().getProperty("config.temp");
        		String oldPath = old_root + conn.getStr("code_image");

        		String root = PropertyConfig.me().getProperty("config.pc");
        		String loadPath = FileUtils.generateDateDir(root) + "/"
        				+ ToolUtil.getUuidByJdk(true) + ".jpg";
        		String newPath = root + loadPath;
        		
        		FileUtils.Copy(oldPath, newPath);
        		store.set("code_image",loadPath);
        		conn.set("code_image", loadPath);
        		
        		String path=WeixinStoreImgDll.getImage(conn.getStr("wechat_logo_img"));
                store.set("wechat_logo_img", path).update();
                conn.set("wechat_logo_img", path);

                conn.save();
                initWeixinMenu(conn, PropertyConfig.me().getProperty("config.wxDomain"));
            }
            result.put("status","y");
        }
        
        renderJson(result.toString());
    }

    private JSONObject checkIsRight(WeixinStoreConn conn){
        JSONObject result=new JSONObject();

        return result;
    }

    /**
     * 初始化微信菜单
     */
    private void initWeixinMenu(WeixinStoreConn conn,String weixinDoMain){
        //init 图文信息
        //电子点餐
        WeixinStoreMatter orderFood =new WeixinStoreMatter()
                .set("id", ToolUtil.getUuidByJdk(true))
                .set("store_id", conn.getStr("store_id"))
                .set("create_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
                .set("title", "电子点餐")
                .set("pic_url", StringUtil.isNotBlank(conn.getStr("wechat_logo_img"))?
                        conn.getStr("wechat_logo_img"):"/static/front/images/store/default_store_logo.gif")
                .set("url",weixinDoMain+"/session/diancan/"+conn.getStr("store_id"))
                .set("is_change",0)
                ;
        orderFood.save();
        // 招聘中心
        WeixinStoreMatter advertiseCenter =new WeixinStoreMatter()
                .set("id", ToolUtil.getUuidByJdk(true))
                .set("store_id", conn.getStr("store_id"))
                .set("create_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
                .set("title", "招聘中心")
                .set("pic_url",StringUtil.isNotBlank(conn.getStr("wechat_logo_img"))?
                        conn.getStr("wechat_logo_img"):"/static/front/images/store/default_store_logo.gif")
                .set("url",weixinDoMain+"/session/invite/"+conn.getStr("store_id"))
                .set("is_change",0)
                ;
        advertiseCenter.save();
        // 评论中心
        WeixinStoreMatter commentCenter =new WeixinStoreMatter()
                .set("id", ToolUtil.getUuidByJdk(true))
                .set("store_id", conn.getStr("store_id"))
                .set("create_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
                .set("title", "评论中心")
                .set("pic_url",StringUtil.isNotBlank(conn.getStr("wechat_logo_img"))?
                        conn.getStr("wechat_logo_img"):"/static/front/images/store/default_store_logo.gif")
                .set("url",weixinDoMain+"/session/appraise/"+conn.getStr("store_id"))
                .set("is_change",0)
                ;
        commentCenter.save();
        // 活动中心
        WeixinStoreMatter activeCenter =new WeixinStoreMatter()
                .set("id", ToolUtil.getUuidByJdk(true))
                .set("store_id", conn.getStr("store_id"))
                .set("create_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
                .set("title", "活动中心")
                .set("pic_url",StringUtil.isNotBlank(conn.getStr("wechat_logo_img"))?
                        conn.getStr("wechat_logo_img"):"/static/front/images/store/default_store_logo.gif")
                .set("url",weixinDoMain+"/session/activity/"+conn.getStr("store_id"))
                .set("is_change",0)
                ;
        activeCenter.save();

        //店铺首页
        WeixinStoreMatter storeInfo = new WeixinStoreMatter()
                .set("id", ToolUtil.getUuidByJdk(true))
                .set("store_id", conn.getStr("store_id"))
                .set("create_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
                .set("title", "店铺首页")
                .set("pic_url",StringUtil.isNotBlank(conn.getStr("wechat_logo_img"))?
                        conn.getStr("wechat_logo_img"):"/static/front/images/store/default_store_logo.gif")
                .set("url",weixinDoMain+"/session/store/"+conn.getStr("store_id"))
                .set("is_change",0);
        storeInfo.save();

        // 初始化微信店铺菜单
        WeixinStoreMenu parent =new WeixinStoreMenu().set("id",ToolUtil.getUuidByJdk(true))
                .set("menu_name", "店家中心")
                .set("click","sub_menu")
                .set("m_level",1)
                .set("px", 1)
                .set("menu_type", "1")
                .set("is_delete", 0)
                .set("store_id", conn.getStr("store_id"));
        parent.save();
        //电子点餐
        new WeixinStoreMenu().set("id",ToolUtil.getUuidByJdk(true))
                .set("menu_name", "电子点餐")
                .set("click","picture")
                .set("m_level",2)
                .set("px", 1)
                .set("operate_info", orderFood.getStr("id"))
                .set("menu_type", "1")
                .set("store_id", conn.getStr("store_id"))
                .set("pid",parent.getStr("id"))
                .set("is_delete", 0)
                .save();
        //招聘中心
        new WeixinStoreMenu().set("id",ToolUtil.getUuidByJdk(true))
                .set("menu_name", "招聘中心")
                .set("click","picture")
                .set("m_level",2)
                .set("px", 1)
                .set("operate_info", advertiseCenter.getStr("id"))
                .set("menu_type", "1")
                .set("store_id", conn.getStr("store_id"))
                .set("pid", parent.getStr("id"))
                .set("is_delete", 0)
                .save();
        //评论中心
        new WeixinStoreMenu().set("id",ToolUtil.getUuidByJdk(true))
                .set("menu_name", "评论中心")
                .set("click","picture")
                .set("m_level",2)
                .set("px", 1)
                .set("operate_info", commentCenter.getStr("id"))
                .set("menu_type", "1")
                .set("store_id", conn.getStr("store_id"))
                .set("pid", parent.getStr("id"))
                .set("is_delete", 0)
                .save();
        //活动中心
        new WeixinStoreMenu().set("id",ToolUtil.getUuidByJdk(true))
                .set("menu_name", "活动中心")
                .set("click","picture")
                .set("m_level",2)
                .set("px", 1)
                .set("operate_info", activeCenter.getStr("id"))
                .set("menu_type","1")
                .set("is_delete", 0)
                .set("store_id", conn.getStr("store_id"))
                .set("pid", parent.getStr("id"))
                .save();
        //店铺首页
        new WeixinStoreMenu().set("id",ToolUtil.getUuidByJdk(true))
                .set("menu_name", "店铺首页")
                .set("is_delete", 0)
                .set("px",1)
                .set("click","picture")
                .set("m_level",2)
                .set("operate_info",storeInfo.getStr("id"))
                .set("menu_type","1")
                .set("store_id", conn.getStr("store_id"))
                .set("pid", parent.getStr("id"))
                .save();
    }
}
