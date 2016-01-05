package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.meishi.front.common.dll.UserInfoImgDll;
import com.meishi.front.config.PropertyConfig;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.Datadic;
import com.meishi.model.Member;
import com.meishi.model.UploadImg;
import com.meishi.util.DateUtil;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

@Before(LoginInteceptor.class)
// @Controller(controllerKey = "/account/userinfo")
public class UserInfoController extends BaseController {
    private static Logger log = Logger.getLogger(UserInfoController.class);

    // 获得用户个人信息
    public void index() {
        Member member = Member.dao.findById(getUserIds());
        List<Datadic> datadics = Datadic.dao.findByGroup("job");
        List<Datadic> datadic = Datadic.dao.findByGroup("selfinfo");
        List<Datadic> hobbygroup = Datadic.dao.findByGroup("hobby");
        String hobby = member.getStr("hobby");
        if (!StringUtil.isBlank(hobby)) {
            String[] hobby1 = hobby.split(",");
            setAttr("hobby1", hobby1);
        }
        setAttr("hobbygroup", hobbygroup);
        setAttr("datadic", datadic);
        setAttr("datadics", datadics);
        setAttr("member", member);
        render("/usercenter/personalInfo.html");
    }

    // 更新用户信息
    @Before(Tx.class)
    public void updateuserinfo() {
        Member member = getModel(Member.class);
        String[] s = getParaValues("hobby");
//    	Object[] images=getParaValues("images");
//		Object[] imagesId=getParaValues("imageId");
        String images = member.getStr("head_image");

        String hobby = "";
        for (int i = 0; i < s.length; i++) {
            if (i < s.length - 1) {
                hobby = hobby + (s[i] + ",");
            } else {
                hobby = hobby + (s[i]);
            }
        }
        if (images != null) {
            UploadImg userInfoId = UploadImg.dao.findById(member.getStr("id"));
            //当有用户id的时候先删除原有图片在添加新图片
            if (userInfoId!=null) {
                String root = PropertyConfig.me().getProperty("config.image");

                File f=new File(root+userInfoId.getStr("small_url"));
                if(f.exists())f.delete();

                f=new File(root+userInfoId.getStr("middle_url"));
                if(f.exists())f.delete();

                f=new File(root+userInfoId.getStr("large_url"));
                if(f.exists())f.delete();

                //删除后的添加
                //生成图片
//                String[] path = UserInfoImageDll.getAllImage(images);
//                userInfoId.set("small_url", path[0])
//                        .set("middle_url", path[1])
//                        .set("large_url", path[2]).update();
                String path = UserInfoImgDll.getImage(images);
                member.set("head_image", path);
            } else
            //没有直接添加新图片
            {
                UploadImg uploadImg = new UploadImg();
                uploadImg
                        .set("id", ToolUtil.getUuidByJdk(true))
                        .set("item_id", member.getStr("id"))
                        .set("img_type", "member")
                        .set("create_time",
                                DateUtil.format(new Date(),
                                        DateUtil.pattern_ymd_hms));
                //生成图片
//                String[] path = UserInfoImageDll.getAllImage(images);
//                uploadImg.set("small_url", path[0])
//                        .set("middle_url", path[1])
//                        .set("large_url", path[2]).save();
                String path = UserInfoImgDll.getImage(images);
                member.set("head_image", path);
            }
        }
        member.set("hobby", hobby);
        member.update();
        redirect("/account/accountcenter");
    }
}
