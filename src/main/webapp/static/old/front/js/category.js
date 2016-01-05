var storeId = "";
var ctxPath = "";

$(function(){
	storeId = $("#storeId").val();
	ctxPath = $("#ctxPath").val();
});

function filterGoods(categoryId){
	cateId=categoryId;
	var data = {
			"storeId": storeId,
			"categoryId": categoryId
	}
	$("#middle").load(ctxPath+"/store/filterGoods", data);
}
function getSetMeals(){
	var data = {
			"storeId": storeId
	}
	$("#middle").load(ctxPath+"/store/setmeal", data);
}