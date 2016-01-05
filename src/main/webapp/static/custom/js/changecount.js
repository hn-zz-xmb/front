	function chooseGoods(id) {
		if($("#goods_" + id).attr("tag")=="1") {
			var params = {"goodsId": id};
			var url = MSConfig.BaseURL + "/salerManage/setmeal/updateChooseGoods";
			var result = callJson(url, params, false);
			if (result.isUpdate) {
				$("#goods_" + id).find("img").attr("src", MSConfig.BaseURL + "/static/custom/images/storeManage/right_green.png");
				$("#goods_" + id).attr("tag","2");
				loadChooseGoods();
			}
		}
	}

	function loadChooseGoods(){
		var url=MSConfig.BaseURL+"/salerManage/setmeal/chooseGoodsUI";
		$(".selected_goods_table").load(url);
	}

	function loadGoodsList(foodType,obj){
		$(".category").find('li a').removeClass("active");
		$(obj).attr("class","active");
		var url=MSConfig.BaseURL+"/salerManage/setmeal/loadGoodsList?foodType="+foodType;
		$("#goodsList").load(url+"&div=goodsList&url=salerManage/setmeal/loadGoodsList");
	}

	function searchGoods(){
		var goodsName=$("#goodsName").val();
		var url=MSConfig.BaseURL+"/salerManage/setmeal/loadGoodsList?goodsName="+goodsName;
		$("#goodsList").load(url+"&div=goodsList&url=salerManage/setmeal/loadGoodsList");
	}

	function delSelectGoods(id){
		var params={"goodsId":id}
		var result=callJson("${ctxPath}/salerManage/setmeal/delChooseGoods",params,false);
		if(result.isDelete){
			$("#goods_"+id).find("img").attr("src",MSConfig.BaseURL+"/static/custom/images/storeManage/right_grey.png");
			loadChooseGoods();
			$("#goods_" + id).attr("tag","1");
		}
	}
	function delGoods() {
		var ok = confirm("确定要删除这些商品吗？");
		if (!ok)
			return;

		var cbs = $(".checkitem:checked");
		var ids = "";
		for (var i = 0; i < cbs.length; i++) {
			ids = ids+","+cbs[i].value;
		}
		var url = "${ctxPath}/salerManage/setmeal/bathDelete";
		var param = {
			"ids" : ids
		}
		var result = callJson(url, param, false);
		if (result.isDelete) {
			loadChooseGoods();
			for (var i = 0; i < cbs.length; i++) {
				$("#goods_"+cbs[i].value).find("img").attr("src",MSConfig.BaseURL+"/static/custom/images/storeManage/right_grey.png");
				$("#goods_"+cbs[i].value).attr("tag","1");
			}

		}
	}
	function changeSelectGoodsCount(id,isAdd){
		var params={"goodsId":id,"isAdd":isAdd}
		var result=callJson("${ctxPath}/salerManage/setmeal/changeChooseGoodsCount",params,false);
		if(result.isChange){
			if(result.isRemove){
				$("#goods_"+id).find("img").attr("src",MSConfig.BaseURL+"/static/custom/images/storeManage/right_grey.png");
				$("#goods_" + id).attr("tag","1");
			}
			loadChooseGoods();
		}
	}

	$(function(){
		loadGoodsList('');
		loadChooseGoods();
		$(".category").find('li a:first').addClass("active");
		$("#btnOk").click(function(){
			parent.setMealLoad();
			var index = parent.layer.getFrameIndex(window.name);
			parent.layer.close(index);
		});

		$(".btn_cancel").click(function(){
			var index = parent.layer.getFrameIndex(window.name);
			parent.layer.close(index);
		})
	})
