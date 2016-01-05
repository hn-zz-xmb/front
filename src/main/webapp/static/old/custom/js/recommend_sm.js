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
		url: ctx+"/salerManage/setmeal/recommendDialog",
		type: "get",
		success: function(result){
			content=result;
		}
	});
	window.showDialog= function (setMealId) {
		var d = dialog({
			title: '推荐套餐',
			content: content,
			okValue: '确 定',
			ok: function () {
				var that = this;
				setTimeout(function () {
					that.title('提交中..');
				}, 2000);
				
				var categoryId=$("#smCategory").val();
				if(categoryId=="")return true;
				var url=ctx+"/salermanage/setmealmanage/recommendSetMeal";
				var param={
						"setMealId": setMealId,
						"categoryId": categoryId
				}
				var result=callText(url,param,true);
				if(result=="fail"){
					alert("对不起您的推荐栏位已满！");
					return true;
				}
				window.location.href=ctx+"/salermanage/setmealmanage";
				return true;
			},
			cancelValue: '取消',
			cancel: function () {}
		});

		d.showModal();
	}
});