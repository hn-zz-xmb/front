$(function(){
    $("#normal_login").click(function(){
        $(this).addClass("login_hover");
        $("#phone_login").removeClass("login_hover");
        $("#normal_form").show();
        $("#phone_form").hide();
    });
    $("#phone_login").click(function(){
        $(this).addClass("login_hover");
        $("#normal_login").removeClass("login_hover");
        $("#phone_form").show();
        $("#normal_form").hide();
    });


//用户名密码登陆
    $("#normal_form").validate({
        errorPlacement : function(error, element) {
            $(".errormessage").html(error);
        },
        rules : {
            username : "required",
            password : "required"
        },
        messages : {
            username : "请输入用户名",
            password :"请输入密码"
        },
        submitHandler : function(form) {
            $(form).ajaxSubmit({
                type : "post",
                url : MSConfig.BaseURL+"/login/ajaxLogin",
                dataType : "json",
                success : function(result) {
                    if(result.isLogin){
                        if($("#ajax").val()=="2") {
                            parent.location.reload();
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                        }else {
                            window.location.href = MSConfig.BaseURL + $("#return_url").val();
                        }
                    }else{
                        $(".errormessage").html(result.error);
                    }
                }
            });
        }

    });

    //手机号码动态码
    jQuery.validator.addMethod("isPhone", function(value, element) {
        var tel = /^(((13[0-9]{1})|(15[0-9]{1}))+\d{8})$/;
        return this.optional(element) || (tel.test(value));
    }, "请输入正确的手机号码");
    $("#phone_form").validate({
        errorPlacement : function(error, element) {
            $(".error").html(error);
        },
        rules : {
            phone :{
                required:true,
                isPhone:true,
                remote:{
                    type:"post",
                    url:MSConfig.BaseURL+"/login/ajaxPhone",
                    dataType: "json",
                    dataFilter: function(data, type) {
                        if (data == "true"){
                            isPhoneExist=true;
                            return true;
                        }else{
                            isPhoneExist=false;
                            return false;
                        }
                    }
                }
            },
            dynamicCode : "required"
        },
        messages : {
            phone : {
                required:"请输入手机号",
                isPhone:"请输入正确的手机号码",
                remote:"该手机号未注册请先注册"
            },
            dynamicCode :"请输入短信校验码"
        },
        submitHandler : function(form) {
            $(form).ajaxSubmit({
                type : "post",
                url : MSConfig.BaseURL+"/login/ajaxPhoneLogin",
                dataType : "json",
                success : function(result) {
                    if(result.isLogin){
                        if($("#ajax").val()=="2") {
                            parent.location.reload();
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                        }else {
                            window.location.href = MSConfig.BaseURL + $("#return_url").val();
                        }
                    }else{
                        $(".error").html(result.error);
                    }
                }
            });
        }
    });
});

var isPhoneExist=false;

var InterValObj; //timer变量，控制时间
var count = 60; //间隔函数，1秒执行
var curCount;//当前剩余秒数
function sendMessage() {
    curCount = count;
    var phone = $("#phone").val();
    if(phone==""||phone==null){
        $(".error").html("请输入手机号");
        return;
    }
    if(isPhoneExist==false){
        $(".error").html("手机号码不正确");
        return;
    }
    //设置button效果，开始计时
    $("#btnSendCode").attr("disabled", "true");
    $("#btnSendCode").val("请在" + curCount + "秒内输入验证码");
    InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
    var url = MSConfig.BaseURL+"/msg/send";
    var params = {
        "msgType" : "login",
        "phone" : $("#phone").val()
    };
    var result = callJson(url, params, false);
    if (result.isMsg) {
        alert("验证码发送成功");
    } else {
        alert(result.error);
    }
}
//timer处理函数
function SetRemainTime() {
    if (curCount == 0) {
        window.clearInterval(InterValObj);//停止计时器
        $("#btnSendCode").removeAttr("disabled");//启用按钮
        $("#btnSendCode").val("重新发送验证码");
        //code = ""; //清除验证码。如果不清除，过时间后，输入收到的验证码依然有效
    } else {
        curCount--;
        $("#btnSendCode").val("请在" + curCount + "秒内输入验证码");
    }
}
