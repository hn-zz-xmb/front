package com.meishi.front.controller.store.admin;

import java.util.Iterator;
import java.util.List;

import com.jfinal.log.Logger;
import org.apache.commons.lang3.StringEscapeUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.meishi.front.ext.weixin.api.AccessToken;
import com.meishi.front.ext.weixin.api.AccessTokenApi;
import com.meishi.front.ext.weixin.api.ApiResult;
import com.meishi.front.ext.weixin.api.MenuApi;
import com.meishi.model.Store;
import com.meishi.model.WeixinStoreConn;
import com.meishi.model.WeixinStoreMenu;
import com.meishi.util.ToolUtil;

public class WeixinMenuController extends Controller{
    private static Logger log = Logger.getLogger(WeixinMenuController.class);

	public void index(){
		render("/store/admin/weixin/weixin_menu.html");
	}
	
	public void list(){
		Store store=getSessionAttr("store");
        List<WeixinStoreMenu> menuList=WeixinStoreMenu.dao.findListByStore(store.getStr("id"));
        setAttr("menuList",menuList);
        renderJson();
	}
	
	/**
	 * 发布菜单
	 */
	@Before(Tx.class)
	public void publish(){
		String menus = StringEscapeUtils.unescapeHtml4(getPara("menus"));
		Store store=getSessionAttr("store");
		
		//删除旧数据
		WeixinStoreMenu.dao.deleteEditableMenus(store.getStr("id"));
		
		//保存菜单
		JSONArray jsonArr = JSONArray.fromObject(menus);
    	Iterator<JSONObject> it = jsonArr.iterator();
    	while(it.hasNext()){
    		JSONObject jsonObj = it.next();
    		if(jsonObj.optInt("editable")== 0)continue;
    		if(jsonObj.optInt("level")==1){//一级菜单
    			String id = ToolUtil.getUuidByJdk(true);
    			WeixinStoreMenu menu = new WeixinStoreMenu();
    			menu.set("id", id)
    			.set("store_id", store.getStr("id"))
    			.set("menu_name", jsonObj.getString("name"))
    			.set("click", jsonObj.getString("click"))
    			.set("operate_info", jsonObj.getString("key"))
    			.set("is_delete", 1)
    			.set("m_level", 1)
    			.set("px", jsonObj.optInt("px"))
    			.save();
    			
    			if("sub_menu".equals(jsonObj.getString("click"))){
    				String pid = jsonObj.getString("id");
        			Iterator<JSONObject> it1 = jsonArr.iterator();
        			while(it1.hasNext()){
        				JSONObject obj = it1.next();
        				if(pid.equals(obj.getString("pid"))){
        					WeixinStoreMenu weixinStoreMenu = new WeixinStoreMenu();
        					weixinStoreMenu.set("id", ToolUtil.getUuidByJdk(true))
        	    			.set("store_id", store.getStr("id"))
        	    			.set("menu_name", obj.getString("name"))
        	    			.set("click", obj.getString("click"))
        	    			.set("operate_info", obj.getString("key"))
        	    			.set("is_delete", 1)
        	    			.set("m_level", 2)
        	    			.set("pid", id)
        	    			.set("px", obj.optInt("px"))
        	    			.save();
        				}
        			}
    			}
    		}
    	}
    	
    	//发布菜单
    	JSONObject status=new JSONObject();
    	WeixinStoreConn conn=WeixinStoreConn.dao.findByStore(store.getStr("id"));
        AccessToken at = AccessTokenApi.getAccessToken(conn.getStr("app_id"),conn.getStr("app_secret"));
        if(!at.isAvailable()){
            status.put("isPublish",false);
            status.put("error","菜单发布失败!"+at.getErrorMsg());
        }else {
            List<WeixinStoreMenu> menuList = WeixinStoreMenu.dao.findListByStore(store.getStr("id"));

            String btnStr=handleWxMenu(menuList);

            log.debug("微信发布菜单JSON:"+btnStr);
            System.out.print(btnStr);
            ApiResult apiResult = MenuApi.createMenu(btnStr,at);
            if(apiResult.isSucceed()){
                status.put("isPublish",true);
            }else{
                status.put("isPublish",false);
                status.put("error","菜单发布失败!!");
            }
        }
        renderJson(status.toString());
	}
	
	/**
     * 处理微信菜单
     * @param menus
     * @return
     * {
        "button":[
            {
            "type":"click",
            "name":"今日歌曲",
            "key":"V1001_TODAY_MUSIC"
            },
            {
            "name":"菜单",
            "sub_button":[
                {
                "type":"view",
                "name":"搜索",
                "url":"http://www.soso.com/"
                },
                {
                "type":"view",
                "name":"视频",
                "url":"http://v.qq.com/"
                },
                {
                "type":"click",
                "name":"赞一下我们",
                "key":"V1001_GOOD"
                }]
         }]
        }
     */
	private String handleWxMenu(List<WeixinStoreMenu> menus){
        JSONObject result=new JSONObject();

        if(menus==null ||menus.size()<1){
            return "{}";
        }

        //一级菜单
        JSONArray buttons=new JSONArray();
        for(WeixinStoreMenu menu :menus){
            if(menu.getInt("m_level")!=null && menu.getInt("m_level")==1) {
                JSONObject click = new JSONObject();
                if (menu.getStr("click").equals("picture")) {
                    click.put("type", "click");
                    click.put("name", menu.getStr("menu_name"));
                    click.put("key", menu.getStr("operate_info"));
                    buttons.add(click);
                }else if(menu.getStr("click").equals("link")){
                    click.put("type", "view");
                    click.put("name", menu.getStr("menu_name"));
                    click.put("url", menu.getStr("operate_info"));
                    buttons.add(click);
                }else if(menu.getStr("click").equals("sub_menu")){
                    click.put("type","sub_button");
                    click.put("name",menu.getStr("menu_name"));
                    click.put("key",menu.getStr("operate_info"));

                    JSONArray subBtns=new JSONArray();
                    for(WeixinStoreMenu subMenu :menus){
                        JSONObject subClick=new JSONObject();
                        if(subMenu.getStr("pid")!=null && subMenu.getStr("pid").equals(menu.getStr("id"))){
                            if(subMenu.getStr("click")!=null){
                                subClick.put("type","click");
                                subClick.put("name",subMenu.getStr("menu_name"));
                                subClick.put("key",subMenu.getStr("operate_info"));
                                subBtns.add(subClick);
                            }
                        }
                    }

                    if(subBtns.size()>0) {
                        click.put("sub_button", subBtns);
                        buttons.add(click);
                    }
                }
            }
        }

        result.put("button",buttons);
        return result.toString();
    }
}
