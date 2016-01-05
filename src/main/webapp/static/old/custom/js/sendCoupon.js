/**
 * 
 */
require.config({
    paths: {
        jquery: '../../jquery/jquery-1.9.1.min'
    }
});

require(['../../artDialog/src/dialog'],function(dialog){
	var ctxPath = $('#ctxPath').val();
	var dialogContent = "";
	
	$.ajax({
		url: ctxPath+"/salermanage/ordermanage/dialog",
		type: "get",
		success: function(result){
			dialogContent=result;
		}
	});
	
	window.showDialog= function(userid, username){
		var d = dialog({
			title: '发放代金券号码',
			content: dialogContent,
			okValue: '确 定',
			ok: function () {
				var that = this;
				setTimeout(function () {
					that.title('提交中..');
				}, 2000);
				
				var cbs = $(".cb:checked");
//				var items = [];
//				for(var i=0; i<cbs.length; i++){
//					items[i]={};
//					items[i].couponId=cbs[i].value;
//					items[i].quantity=$("#"+cbs[i].value).find(".qty").val();
//				}
				if(cbs.length==0)return true;
				var param = "";
				for(var i=0; i<cbs.length; i++){
					if(i==0){
						param+="couponItemVOList[0].couponId="+cbs[i].value
							+"&couponItemVOList[0].quantity="+$("#"+cbs[i].value).find(".qty").val();
					}else{
						param+="&couponItemVOList["+i+"].couponId="+cbs[i].value
							+"&couponItemVOList["+i+"].quantity="+$("#"+cbs[i].value).find(".qty").val();
					}
				}
				var url=ctxPath+"/salermanage/ordermanage/sendCoupon";
//				var param={
//						"userid": userid,
//						"couponItemVOList": items
//				}
//				param.isAjax=1;
				param+="&userid="+userid;
				callText(url,param,true);
//				param+="&isAjax=1";
//				var headers = {};
//				headers['__RequestVerificationToken'] = $("#CSRFToken").val();
//				if (url.indexOf("?") == -1) {
//					url = url + "?rid=" + Math.random();
//				} else {
//					url = url + "&rid=" + Math.random();
//				}
				
//				$.ajax({
//					url: url,
//					type: "post",
////					contentType : "application/json", 
//					data: param,
//					headers : headers
//				});
				return true;
			},
			cancelValue: '取消',
			cancel: function () {}
		});

		d.showModal();
		
		$("#username").text(username);
	}
	
});