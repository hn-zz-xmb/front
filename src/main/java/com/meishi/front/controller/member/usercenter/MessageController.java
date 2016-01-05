package com.meishi.front.controller.member.usercenter;

import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.model.Member;
import com.meishi.model.Message;
import com.meishi.util.DateUtil;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 站内信Controller
 * Created by Develop_RSP on 2015/2/10.
 */
public class MessageController extends BaseController{

    /**
     * 查看我的站内信
     */
    public void list(){
        //我的站内信列表
    	Member member = Member.dao.findById(getUserIds());
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("memberId", member.getStr("id"));
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
        Page<Message> msgList=Message.dao.findPageByMember(pageNum,pageSize,params);
        setAttr("page", msgList);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
        render("/usercenter/message/messagelist.html");
    }

    /**
     * 站内信详情
     */
    public void detail(){
        String id=getPara("id");
        Message message=Message.dao.findById(id,getUserIds());
        setAttr("message",message);
        render("/usercenter/message/messageDetail.html");
    }

    /**
     * 发送站内信页面
     */
    public void sendUI(){
    	String id=getPara("storeId");
    	Member member=Member.dao.findByStoreId(id);
    	setAttr("member", member);
        render("/usercenter/message/messageForm.html");
    }

    /**
     * 发送站内信
     */
    public void send(){
    	JSONObject result = new JSONObject();
    	if(StringUtil.isBlank(getUserIds())){
    		result.put("isLogin", false);
    	}else{
    		Message message=getModel(Message.class);
            message.set("id", ToolUtil.getUuidByJdk(true)).set("from_member_id", getUserIds());
            message.set("create_time", DateUtil.format(new Date(),DateUtil.pattern_ymd_hms));
            message.save();
    		result.put("isLogin", true);    		
    	}
		renderJson(result.toString());
    }
    /**
     * 回复站内信页面
     */
    public void sendHF(){
    	String id=getPara("id");
    	Member member=Member.dao.findById(id);
    	setAttr("member", member);
        render("/usercenter/message/messageForm.html");
    }
    //删除
    public void delete(){
    	String id=getPara("id");
    	if(id==null){
    		renderText("fail");
    	}else{
    	Message.dao.deleteById(id);
    	renderText("success");
    	}
    }
}
