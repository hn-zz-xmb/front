var ctxPath = "";// 全局路径
var storeId_ = "";// 全局店铺ID
var divObj_;// 全局DIV

/**
 * @param ctxPath
 *            //上下文路径
 * @param storeId
 *            //店铺Id
 * @param divObj
 *            要操作的div
 */
function initCart(ctxPath, storeId, divObj) {
	this.ctxPath = ctxPath;
	this.storeId_ = storeId;
	this.divObj_ = divObj;

	$("#" + divObj).load(ctxPath + "/cart", {
		storeId : storeId
	},init);
}
/**
 * 添加菜单
 * 
 * @param item_id 套餐或者单件商品ID
 * @param type 类型:套餐:单件美食
 */
function addToMenu(item_id,type) {
	var param = {
		"itemId" : item_id,
		"type":type,
		"storeId" : storeId_
	}
	$.ajax({
		url : ctxPath + "/cart/add",
		type : "post",
		data : param,
		success : function(result) {
			initCart(ctxPath, storeId_, divObj_);
		},
		error : function() {

		}
	});
}
/**
 * 删除菜单
 * 
 * @param id
 * @param itemId
 */
function removeGoods(id, itemId) {
	var param = {
		"id" : id,
		"itemId" : itemId,
		"storeId" : storeId_
	}
	$.ajax({
		url : ctxPath + "/cart/delete",
		type : "post",
		data : param,
		success : function() {
			initCart(ctxPath, storeId_, divObj_);
		},
		error : function() {

		}
	});
}
/**
 * 更新数量
 * 
 * @param id
 * @param goodsId
 * @param type
 */
function changeQuantity(id, goodsId, quantity_type) {
	var quantityObj = $("#quantity_" + goodsId);
	var moneyObj = $("#money_" + goodsId);
	var totalMoneyObj = $("#totalMoney");

	var quantityCount = parseInt(quantityObj.html());
	var money = Number(moneyObj.html());
	var totalMoney = Number(totalMoneyObj.html());
	if (quantity_type == "0" && quantityCount <= 0) {
		return;
	}
	var param = {
		"id" : id,
		"goodsId" : goodsId,
		"quantity_type" : quantity_type,
		"storeId":storeId_
	}
	$.ajax({
		url : ctxPath + "/cart/change",
		type : "post",
		data : param,
		success : function() {
			if (quantity_type == "0") {
				quantityObj.html(quantityCount - 1);
				totalMoney = totalMoney - money;
				totalMoneyObj.html(totalMoney.toFixed(2));
			} else if (quantity_type == "1") {
				quantityObj.html(quantityCount + 1);
				totalMoney = totalMoney + money;
				totalMoneyObj.html(totalMoney.toFixed(2));
			}

		},
		error : function() {

		}
	});
}
var dialog;

function openAjaxLogin() {
	dialog = art.dialog.open(ctxPath + "/ajaxLoginUI", {
		id : "loginForm",
		title : "登录",
		lock : true,
		background : '#FFFFFF', // 背景色
		opacity : 0.56 // 透明度
	});
}

function closeDialog() {
	dialog.close();
}

// 跳转页面
function toWriteOrder(totalCount) {
	if(totalCount<=0){
		alert("请先添加商品");
		return;
	}
	location.href = ctxPath + "/order/writeorder?store_id="+storeId_;
}
