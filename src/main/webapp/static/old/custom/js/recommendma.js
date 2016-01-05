/**
 * 
 */
require.config({
    paths: {
        jquery: '../../jquery/jquery-1.9.1.min'
    }
});

require(['../../artDialog/src/dialog'],function(dialog){
	var ctx=$("#ctx").val();
	var content ="";
	$.ajax({
		url: ctx+"/salermanage/materialgoodsmanage/dialog",
		type: "get",
		success: function(result){
			content=result;
		}
	});
	window.showDialog= function (materialId) {
		var d = dialog({
			title: '推荐商品',
			content: content,
			okValue: '确 定',
			ok: function () {
				var that = this;
				setTimeout(function () {
					that.title('提交中..');
				}, 2000);
				
				var materialTypeId=$("#materialType").val();
				if(materialTypeId=="")return true;
				var url=ctx+"/salermanage/materialgoodsmanage/recommendMaterial";
				var param={
						"materialId": materialId,
						"materialTypeId": materialTypeId
				}
				var result=callText(url,param,true);
				if(result=="fail"){
					alert("对不起您的推荐栏位已满，请购买！");
					window.open(ctx+"/salermanage/materialgoodsmanage", "", "", "");
					//window.location.href=ctx+"/salermanage/goodsmanage";
					return true;
				}
				window.location.href=ctx+"/salermanage/materialgoodsmanage";
				return true;
			},
			cancelValue: '取消',
			cancel: function () {}
		});

		d.showModal();
	}
});