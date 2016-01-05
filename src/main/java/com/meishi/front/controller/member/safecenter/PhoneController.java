package com.meishi.front.controller.member.safecenter;

import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.common.rabbit.SendRabbitMQMsg;
import com.meishi.front.common.util.GenerateLinkUtils;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.Member;
import com.meishi.model.MemberLink;
import com.meishi.util.GenerateUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import com.meishi.util.msg.SendMsgTemplate;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rsp on 14-11-22.
 */
@Before(LoginInteceptor.class)
public class PhoneController extends BaseController {
    private static Logger log = Logger.getLogger(PhoneController.class);

    /***********************************绑定手机号码**********************************************/
    /**
     * 绑定手机号码
     * 判断是否已經绑定手机号码
     */
    public void setUI(){
        Member member=Member.dao.findById(getUserIds(),"username,tel");
        if(StringUtil.isNotBlank(member.getStr("tel"))){
            redirect("/safecenter/phone");//不为空则跳转修改手机号码页面
        }else {
            setAttr("member", member);
            render("/oldfront/account/security/mobile/setMobile.html");
        }
    }

    /**
     * 更改手机号码
     */
    public void save(){
        JSONObject result=new JSONObject();
        Member members=Member.dao.findByPhone(getPara("phone"));
        if(members!=null && !"".equals(members)){
        	 result.put("status","n");
             result.put("error","手机号码已存在");
             renderJson(result.toString());
             return;
        }
        MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1, getPara("phoneCode"),
                SendMsgTemplate.USE_CODE,getPara("phone"));

        if (!MemberLink.dao.checkMemberLink(memberLink)) {
            result.put("status","n");
            result.put("error","激活码不存在或已失效");
            renderJson(result.toString());
            return;
        }

        Member member=Member.dao.findById(memberLink.getStr("member_id"));
        member.set("tel", memberLink.getStr("type_no"));
        Member.dao.updatePhone(member, memberLink);
        result.put("status", "y");
        renderJson(result.toString());
    }

    /***********************************更改手机号方式**********************************************/

    /**
     * 更改手机号码
     */
    public void index(){
        Member member=Member.dao.findById(getUserIds(),"tel,email");
        setAttr("member", member);
        render("/oldfront/account/security/mobile/choose_style.html");
    }

    /***********************************1>通过邮箱更改**********************************************/

    /**
     * 通过邮箱
     */
    public void updateByEmailUI(){
        Member member=Member.dao.findById(getUserIds(),"email");
        if(StringUtil.isNotBlank(member.getStr("email"))){
            // 发送邮件
            HashMap map = new HashMap();
            map.put("email", member.getStr("email"));
            map.put("id", getUserIds());
            map.put("random_code", ToolUtil.getUuidByJdk(true));
            map.put("operate", AppContextData.MEMBER_OPERATE_MODIFYPHONE);
            SendRabbitMQMsg.service.sendEmailMsg(map);

            setAttr("member",member);
            render("/oldfront/account/security/mobile/verify_by_e.html");
        }else{
            redirect("/safecenter/email");//绑定邮箱链接
        }
    }

    /**
     * 验证修改手机号码邮箱链接
     */
    @ClearInterceptor
    public void validCode(){
        MemberLink memberLink = MemberLink.dao.findMemberLink(2,getPara(GenerateLinkUtils.CHECK_CODE),"1");

        if (!MemberLink.dao.checkMemberLink(memberLink)) {
            setAttr("message", "激活码不存在或已失效");
            render("/oldfront/user/active_result.html");
        }else {
            render("/oldfront/account/security/mobile/modifyByE.html");
        }
    }


    /**
     * 通过邮箱更改手机号码
     */
    public void updateByEmail(){
        //验证手机动态码是否正确
        JSONObject result=new JSONObject();
        MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1, getPara("phoneCode"),
                SendMsgTemplate.USE_CODE,getPara("newPhone"));

        if (!MemberLink.dao.checkMemberLink(memberLink)) {
            result.put("status","n");
            result.put("error","激活码不存在或已失效");
            renderJson(result.toString());
            return;
        }

        Member member=Member.dao.findById(memberLink.getStr("member_id"));
        member.set("tel", memberLink.getStr("type_no"));
        Member.dao.updatePhone(member, memberLink);
        result.put("status", "y");
        renderJson(result.toString());
    }

    /***********************************2>通过旧手机验证**********************************************/

    /**
     * 通过新的手机号
     */
    public void updateByNewPhoneUI(){
        setAttr("member",Member.dao.findById(getUserIds(),"tel"));
        render("/oldfront/account/security/mobile/verify_by_m.html");
    }

    /**
     * 验证已经绑定的手机号
     */
    public void checkPhone(){
        JSONObject result=new JSONObject();
        Member member=Member.dao.findById(getUserIds(),"tel");

        if(!member.getStr("tel").equals(getPara("phone"))){
            result.put("status", "n");
            result.put("error", "原手机号码不正确");
            renderJson(result.toString());
            return;
        }
        result.put("status", "y");
        renderJson(result.toString());
    }


    /***********************************3>通过旧手机验证**********************************************/

    /**
     * 通过已经绑定的手机号
     */
    public void updateByPhoneUI(){
        Member member=Member.dao.findById(getUserIds(),"tel");

        String msgType = SendMsgTemplate.USE_CODE;// 短信类型
        String phone = member.getStr("tel");

        List<String> params = new LinkedList<String>();// 短信参数
        String phone_code = GenerateUtils.generateIntRandom(6);
        params.add(phone_code);
        // 生成随机码

        HashMap map = new HashMap();
        map.put("msgType", msgType);
        map.put("phone", phone);
        map.put("phone_code", phone_code);
        map.put("params", params);
        map.put("memberId",getUserIds());
        SendRabbitMQMsg.service.sendPhoneMsg(map);

        setAttr("member",member);
        render("/oldfront/account/security/mobile/verify_by_old_m.html");
    }

    /**
     * 验证手机号码
     */
    public void validPhoneCode(){
        JSONObject result=new JSONObject();
        MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1, getPara("phoneCode"),
                SendMsgTemplate.USE_CODE,getPara("newPhone"));

        if (!MemberLink.dao.checkMemberLink(memberLink)) {
            result.put("status","n");
            result.put("error","激活码不存在或已失效");
            renderJson(result.toString());
            return;
        }

        result.put("status","y");
        renderJson(result.toString());
    }

    /**********************************这里是修改手机号码****************************************/

    /**
     * 更改手机号码页面
     */
    public void updateUI(){
        Member member=Member.dao.findById(getUserIds(),"username,tel");
        if(StringUtil.isNotBlank(member.getStr("tel"))){
            setAttr("member", member);
            render("/oldfront/account/security/mobile/setMobile.html"); //修改手机号码页面
        }else {
            redirect("/safecenter/phone/setUI");//为空则跳转设置手机号码
        }
    }


    /**
     * 更改手机号码
     */
    public void update(){
        JSONObject result=new JSONObject();

        MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1, getPara("phoneCode"),
                SendMsgTemplate.USE_CODE, getPara("phone"));

        if (!MemberLink.dao.checkMemberLink(memberLink)) {
            result.put("status","n");
            result.put("error","激活码不存在或已失效");
            renderJson(result.toString());
            return;
        }

        Member member=Member.dao.findById(memberLink.getStr("member_id"));

        if(member.getStr("tel").equals(getPara("phone"))){
            result.put("status","n");
            result.put("error","手机号码已被注册");
            renderJson(result.toString());
            return;
        }

        member.set("tel", memberLink.getStr("type_no"));
        Member.dao.updatePhone(member, memberLink);
        result.put("status", "y");
        renderJson(result.toString());
    }
}
