/**
 * 返回text文本数据
 * 
 * @param url
 * @param param
 * @return
 */
function callText(url, param, isCsrf) {
	if (!isCsrf) {
		return call("text", url, param);
	} else {
		return callByIsCsrf("text", url, param);
	}
}

/**
 * 返回json数据
 * 
 * @param url
 * @param param
 * @return
 */
function callJson(url, param, isCsrf) {
	if (!isCsrf) {
		return call("json", url, param);
	} else {
		return callByIsCsrf("json", url, param);
	}
}
/**
 * 异步操作请求服务器Call
 * 
 * @param actionType
 *            请求参数类型
 * @param url
 * @param param
 */
function call(callType, url, param) {
	var isReturn = true;
	var result = null;
	if (url.indexOf("?") == -1) {
		url = url + "?rid=" + Math.random();
	} else {
		url = url + "&rid=" + Math.random();
	}
	$.ajax({
		type : "POST",
		dataType : callType,
		url : url,
		data : param,
		async : false,
		success : function(dataResult) {
			// 服务器返回异常处理
			result = dataResult;
		},
		error : function(XMLHttpResponse) {
			alert("系统繁忙，请稍后再试！");
			return false;
		}
	});
	if (isReturn) {
		return result;
	}

	return false;
}

/**
 * 异步操作请求服务器Call
 * 
 * @param actionType
 *            请求参数类型
 * @param url
 * @param param
 */
function callByIsCsrf(callType, url, param) {
	if(typeof param=="object"){
		param.isAjax=1;
	}else if(typeof param=="string"){
		if(param==""){
			param="isAjax=1";
		}else{
			param+="&isAjax=1";
		}
	}

	var headers = {};
	headers['__RequestVerificationToken'] = $("#CSRFToken").val();

	var isReturn = true;
	var result = null;
	if (url.indexOf("?") == -1) {
		url = url + "?rid=" + Math.random();
	} else {
		url = url + "&rid=" + Math.random();
	}
	$.ajax({
		type : "POST",
		dataType : callType,
		url : url,
		data : param,
		headers : headers,
		async : false,
		success : function(dataResult) {
			// 服务器返回异常处理,如果有消息div则放入,没有则弹出
			result = dataResult;
		},
		error : function(XMLHttpResponse) {
			alert("系统繁忙，请稍后再试！");
			return false;
		}
	});
	if (isReturn) {
		return result;
	}

	return false;
}
