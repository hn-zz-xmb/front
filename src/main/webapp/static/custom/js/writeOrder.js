$(function(){
	$("#daodian_radio").click(function(){
		$("#daodian").show();
		$("#waimai").hide();
	});
	$("#waimai_radio").click(function(){
		$("#waimai").show();
		$("#daodian").hide();
	});
	$("#new_address").click(function(){
		$("#base_info").show();
	});
	$(".address").click(function(){
		$("#base_info").hide();
	});
});

//填写订单
function writeOrder(){
	var consume_method=$("input[name=consume_method]:checked").val();
	if(consume_method==""){
		return;
	}
	if(consume_method=="1"){
		$("#arriveEatForm").submit();
	}else if(consume_method=="2"){
		$("#takeOutForm").submit();
	}

}
