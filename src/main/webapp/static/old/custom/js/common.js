/**
 * 判断是否是空
 * 
 * @param value
 */
function isEmpty(value) {
	if (value == null || value == "" || value == "undefined"
			|| value == undefined || value == "null") {
		return true;
	} else {
		value = value.replace(/\s/g, "");
		if (value == "") {
			return true;
		}
		return false;
	}
}
//正则
function trimTxt(txt) {
	return txt.replace(/(^\s*)|(\s*$)/g, "");
}
/**
 * 检查是否含有非法字符
 * 
 * @param temp_str
 * @returns {Boolean}
 */
function is_forbid(temp_str) {
	temp_str = trimTxt(temp_str);
	temp_str = temp_str.replace('*', "@");
	temp_str = temp_str.replace('--', "@");
	temp_str = temp_str.replace('/', "@");
	temp_str = temp_str.replace('+', "@");
	temp_str = temp_str.replace('\'', "@");
	temp_str = temp_str.replace('\\', "@");
	temp_str = temp_str.replace('$', "@");
	temp_str = temp_str.replace('^', "@");
	temp_str = temp_str.replace('.', "@");
	temp_str = temp_str.replace(';', "@");
	temp_str = temp_str.replace('<', "@");
	temp_str = temp_str.replace('>', "@");
	temp_str = temp_str.replace('"', "@");
	temp_str = temp_str.replace('=', "@");
	temp_str = temp_str.replace('{', "@");
	temp_str = temp_str.replace('}', "@");
	var forbid_str = new String('@,%,~,&');
	var forbid_array = new Array();
	forbid_array = forbid_str.split(',');
	for (i = 0; i < forbid_array.length; i++) {
		if (temp_str.search(new RegExp(forbid_array[i])) != -1)
			return false;
	}
	return true;
}

/**
 * 不让浏览器返回
 */
function listenBrowerBack() {
	if (window.history && window.history.pushState) {
		$(window).on('popstate', function() {
			var hashLocation = location.hash;
			var hashSplit = hashLocation.split("#!/");
			var hashName = hashSplit[1];
			if (hashName !== '') {
				var hash = window.location.hash;
				if (hash === '') {
					window.location.href = window.location.href;
				}
			}
		});
		var url = window.location.href + "&#forward";
		var state = {
			title : document.title,
			url : url
		}
		window.history.pushState(state, document.title, url);
	}
}
