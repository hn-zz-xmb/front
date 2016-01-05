jQuery.validator.addMethod("phone", function(value, element) {   
    var tel = /^[1][3578][0-9]{9}$/;
    return this.optional(element) || (tel.test(value));
}, "请正确填写您的手机号码");