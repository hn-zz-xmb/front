$(function(){
	$("#form3").hide();
	$("#form2").hide();
	$("#form1").hide();

	$("#menu_event_dropdown").on("click",".selected",function(){
		$("#menu_event_dropdown ul").show();
	});
	$("#menu_event_dropdown").on("mouseleave","ul",function(){
		$("#menu_event_dropdown ul").hide();
	});
	$("#menu_event_dropdown").on("click","ul li a",function(){
		$("#menu_event_dropdown .selected").text($(this).text());
		$("#menu_event_dropdown ul").hide();
	});

	// init click
	$("#menuListUl li").each(function(i,item){
		$(this).find("a").click(function(){
			var code=$(this).attr("code");
			if(code=="link"){
				$(".form").hide();
				$("#form3").show();
			}else if(code=="picture"){
				$(".form").hide();
				$("#form2").show();
			}else if(code=="sub_menu"){
				$(".form").hide();
				$("#form1").show();
			}
			$("#menuClick").val(code);
		});
	})
	//init
	loadMenus();

	clearStatus();
});


var lastStatusId;

function clearStatus(){
	if(lastStatusId){
		$("#"+lastStatusId).removeClass("active");
	}
	//清空
	$("#menuName").val("");
	$("#menu_event_dropdown .selected").text("请选择");

	$("input[name='tuwen']").each(function(){
		this.checked=false;
	})

	$("#url").val("");

	$("input[name='sub_menu']").each(function(i){
		$(this).val("");
	})
	$("input[name='sub_menuId']").each(function(i){
		$(this).val("");
	})

	//获取级别
	$("#menuLevel").val("1");
	//获取ID
	$("#menuId").val("");
	$("#menuClick").val("");
}

/**
 * 加载微信菜单
 */
function loadMenus(){
	var url=MSConfig.BaseURL+"/salerManage/weixin/menu/getWeixinMenuList";
	$(".menus").load(url);
}

function validMenuForm(menuName,menuClick){
	if(menuName.val()==""){
		layer.tips('请填写菜单名称', menuName , {guide: 1, time: 2});
		return false;
	}
	$("#menuNameSpan").html("");

	if(menuClick.val()==""){
		layer.tips('请选择菜单点击事件', menuClick , {guide: 1, time: 2});
		return false;
	}
	$("#menuClickSpan").html("");
	return true;
}

/**
 * 保存菜单
 */
function saveMenu(type){
	//菜单名称
	var menuName=$("#menuName").val();
	//获取菜单类型
	var menuClick=$("#menuClick").val();

	if(!validMenuForm($("#menuName"),$("#menuClick"))){
		return;
	}
	//获取级别
	var level=$("#menuLevel").val();
	//获取ID
	var id=$("#menuId").val();

	var url=MSConfig.BaseURL+"/salerManage/weixin/menu/save";
	//菜单类型内容
	if(type==1){	//显示二级菜单
		var secondMenuArray=$("input[name='sub_menu']");
		var subMenus="";
		secondMenuArray.each(function(i,item){
			subMenus+=$(this).val()+"-";
		})

		var secondMenuIdArray=$("input[name='sub_menuId']");
		var subMenuIds="";
		secondMenuIdArray.each(function(){
			subMenuIds+=$(this).val()+"-";
		})
		var params={"weixinStoreMenu.id":id,"weixinStoreMenu.menu_name":menuName,
			"weixinStoreMenu.click":menuClick,"subMenus":subMenus,"subMenuIds":subMenuIds,
		"weixinStoreMenu.m_level":level};
		var result=callJson(url,params,false);
		if(result.isSave){
			alert("save");
			loadMenus();
		}
	}else if(type==2){//单图文
		var tuwenId=$("input[name='tuwen']:checked").val();
		if(tuwenId!=""){
			var params={"weixinStoreMenu.id":id,"weixinStoreMenu.menu_name":menuName,
				"weixinStoreMenu.click":menuClick,"weixinStoreMenu.operate_info":tuwenId,
				"weixinStoreMenu.m_level":level};
			var result=callJson(url,params,false);
			if(result.isSave){
				alert("save");
				loadMenus();
			}
		}
	}else if(type==3){//链接地址
		var link_url=$("input[name='url']").val();
		if(link_url!=""){
			var params={"weixinStoreMenu.id":id,"weixinStoreMenu.menu_name":menuName,
				"weixinStoreMenu.click":menuClick,"weixinStoreMenu.operate_info":link_url,
				"weixinStoreMenu.m_level":level};
			var result=callJson(url,params,false);
			if(result.isSave){
				alert("save");
				loadMenus();
			}
		}
	}

}

//发布菜单
function fabu() {
	if (window.confirm('您确定要发布菜单吗?')) {
		var url = MSConfig.BaseURL+"/salerManage/weixin/menu/publish";
		var loading=layer.load('加载中…');
		var data = callJson(url, {}, false);
		layer.close(loading);
		if (data.isPublish) {
			alert("菜单发布成功!");
		} else {
			alert(data.error);
		}
	}
}

// 取消菜单
function cancel() {
	if (window.confirm('您确定要停用菜单吗?')) {
		var url = SConfig.BaseURL+"/salermanage/menumanage/delete";
		var data = callJson(url, {}, false);
		if (data.isFalg) {
			alert("菜单取消成功!");
		} else {
			alert(data.error);
		}
	}
}


