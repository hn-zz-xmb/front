function MenuManager(){
	var ctx=$("#ctx").val();
	var countLv1=0;
	var tb;
	var tmpId=1;
	var addBtn;
//	var countMap={};
//	var map={};
	var $topMenu;
	var $subMenu;
	var menus=[];
//	var api={};
	var tuwenOptionStr="";
	var matters=[];
//	var selectStr="<select class='select'><option value='url'>链接</option><option value='tuwen'>单图文</option></select>";
//	var inputStr="<input class='input2' type='url'/>";
	function init(){
		tb=document.getElementById('testTabLv1');
		addBtn=document.getElementById('addItemLv1');
		addBtn.disabled=false;
		$topMenu=$("#yulan").find(".top-menu");
		$subMenu=$("#yulan").find(".sub-menu");
		initTuwen();
		initMenu();
	}
	function initMenu(){
		//通过ajax获取menu数据
		/*var data=[{"id":"001","menu_name":"test","click":"sub_menu","operate_info":"1","pid":"0","m_level":1},
		          {"id":"002","menu_name":"test","click":"picture","operate_info":"1","pid":"001","m_level":2}];
		setData(data);
		var topMenus=getTopMenus();
		countLv1=topMenus.length;
		initTable();
		initPhone();*/
		$.ajax({
			url:ctx+"/salerManage/weixin/menu/list",
			type:"get",
			dataType:"json",
			success:function(result){
				var data=result.menuList;
				setData(data);
				var topMenus=getTopMenus();
				countLv1=topMenus.length;
				if(countLv1>=3){
					addBtn.disabled=true;
				}
				initTable();
				initPhone();
			}
		})
	}
	function initTuwen(){
		//通过ajax获取单图文数据
		//tuwenOptionStr="<option>test</option><option>test1</option>";
		$.ajax({
			url:ctx+"/salerManage/weixin/matter/list",
			type:"get",
			async:false,
			dataType:"json",
			success:function(result){
				tuwenOptionStr="";
				matters=result.matterList;
				for(var i=0;i<matters.length;i++){
					tuwenOptionStr+="<option value='"+matters[i].id+"'>"+matters[i].title+"</option>";
				}
			}
		})
	}
	function getTuwenStr(id){
		return "<select class='tuwenList' onchange='changeTuwenId(&apos;"+id+"&apos;,this)'>"+tuwenOptionStr+"</select>";
	}
	function getTuwenOptionStr(key){
		var str="";
		for(var i=0;i<matters.length;i++){
			if(matters[i].id==key){
				str+="<option value='"+matters[i].id+"' selected>"+matters[i].title+"</option>";
			}else{
				str+="<option value='"+matters[i].id+"'>"+matters[i].title+"</option>";
			}
		}
		return str;
	}
	function setData(data){
		for(var i=0;i<data.length;i++){
			menus.push(new Menu(data[i]));
		}
	}
	function initTable(){
		var tableStr="";
		var topMenus = getTopMenus();
		for(var i=0;i<topMenus.length;i++){
			tableStr+="<tr id='tr"+topMenus[i].id+"'><td>";
			if(topMenus[i].editable==1){
				tableStr+="<input class='input1 menu_name' type='text' onblur='setTopMenuName(&apos;"+topMenus[i].id+"&apos;,this)' placeholder='请输入菜单名称' value='"+topMenus[i].name+"'/>" +
				"<a class='RightLv1Img' onclick='insertTrLv2(&apos;"+topMenus[i].id+"&apos;)' ></a>";
			}else{
				tableStr+="<input class='input1 menu_name' type='text' disabled value='"+topMenus[i].name+"'/>";
			}
			tableStr+="</td><td>";
				
			if(topMenus[i].click=="link"){
				tableStr+="<select class='select' onchange='changeClickEvent(&apos;"+topMenus[i].id+"&apos;,this)' ><option value='url' selected>链接</option><option value='tuwen'>单图文</option></select>"+
					"<input type='url' class='input2' onchange='changeUrlValue(&apos;"+topMenus[i].id+"&apos;,this)' value='"+topMenus[i].key+"'/>";
			}else if(topMenus[i].click=="picture"){
				tableStr+="<select class='select' onchange='changeClickEvent(&apos;"+topMenus[i].id+"&apos;,this)' ><option value='tuwen'>链接</option><option value='tuwen' selected>单图文</option></select>"+
				"<select class='tuwenList' onchange='changeTuwenId(&apos;"+topMenus[i].id+"&apos;,this)' value='"+topMenus[i].key+"'>"+tuwenOptionStr+"</select>";
			}
			if(topMenus[i].editable==1){
				tableStr+="<button class='delBtn' onclick='removeTrLv1(&apos;"+topMenus[i].id+"&apos;)'>移   出</button>";
			}
			tableStr+="</td></tr>";
			
			var subMenus = getSubMenus(topMenus[i].id);
			for(var j=0;j<subMenus.length;j++){
				tableStr+="<tr id='tr"+subMenus[j].id+"'><td>-----";
				if(subMenus[j].editable==1){
					tableStr+="<input class='input1 menu_name' type='text' onblur='setSubMenuName(&apos;"+subMenus[j].id+"&apos;)' placeholder='请输入菜单名称' value='"+subMenus[j].name+"'/>";
				}else{
					tableStr+="<input class='input1 menu_name' type='text' disabled value='"+subMenus[j].name+"'/>";
				}
				tableStr+="</td><td>";
				if(subMenus[j].editable==1){
					if(subMenus[j].click=="link"){
						tableStr+="<select class='select' onchange='changeClickEvent(&apos;"+subMenus[j].id+"&apos;,this)' ><option value='url' selected>链接</option><option value='tuwen'>单图文</option></select>"+
							"<input type='url' class='input2' onchange='changeUrlValue(&apos;"+subMenus[j].id+"&apos;,this)' value='"+subMenus[j].key+"'/>";
					}else if(subMenus[j].click=="picture"){
						tableStr+="<select class='select' onchange='changeClickEvent(&apos;"+subMenus[j].id+"&apos;,this)' ><option value='url'>链接</option><option value='tuwen' selected>单图文</option></select>"+
							"<select class='tuwenList' onchange='changeTuwenId(&apos;"+subMenus[j].id+"&apos;,this)' value='"+subMenus[j].key+"'>"+getTuwenOptionStr(subMenus[j].key)+"</select>";
					}
					tableStr+="<button class='delBtn' onclick='removeTrLv2(&apos;"+subMenus[j].id+"&apos;)'>移   出</button>";
				}
				tableStr+="</td></tr>";
			}
		}
		$(tb).append(tableStr);
	}
	function initPhone(){
		var topMenuStr="";
		var subMenuStr="";
		var topMenus = getTopMenus();
		for(var i=0;i<topMenus.length;i++){
			topMenuStr+="<li id='m_"+topMenus[i].id+"' data-index='"+(i+1)+"'><a onclick='showSubMenu(&apos;"+topMenus[i].id+"&apos;)'>"+topMenus[i].name+"</a></li>";
			var subMenus = getSubMenus(topMenus[i].id);
			if(subMenus.length>0){
				subMenuStr+="<div id='p_"+topMenus[i].id+"' class='ui-popover' style='display:none;'><div class='arrow'></div><div class='ui-popover-content'>";
				for(var j=0;j<subMenus.length;j++){
					subMenuStr+="<a id='m_"+subMenus[j].id+"'>"+subMenus[j].name+"</a>";
				}
				subMenuStr+="</div></div>";
			}
		}
		$topMenu.html(topMenuStr);
		$subMenu.html(subMenuStr);
		if(topMenus.length>0){
			$topMenu.addClass("has-menu-"+topMenus.length);
		}
	}
	function Menu(data){
		var obj = new Object();
		if(data==undefined){
			obj.id="";
			obj.name="";
			obj.click="";
			obj.key="";
			obj.pid="";
			obj.level=0;
			obj.editable=0;
			obj.px=0;
			return obj;
		}
		obj.id=data.id;//菜单id
		obj.name=data.menu_name;//菜单名称
		obj.click=data.click;//菜单点击事件(picture,link,sub_menu)
		obj.key=data.operate_info;//url链接或者图文id
		obj.pid=data.pid;//父级菜单id
		obj.level=data.m_level;//1:一级菜单，2：二级菜单
		obj.editable=data.is_delete;//1:可编辑，0:不可编辑
		obj.px=data.px;//排序
		return obj;
	}
	function getTopMenus(){
		var a = [];
		for(var i=0;i<menus.length;i++){
			if(menus[i].level==1){
				a.push(menus[i]);
			}
		}
		return a;
	}
	function getSubMenus(pid){
		var a=[];
		for(var i=0;i<menus.length;i++){
			if(menus[i].pid==pid){
				a.push(menus[i]);
			}
		}
		return a;
	}
	function getMenu(id){
		for(var i=0;i<menus.length;i++){
			if(menus[i].id==id){
				return menus[i];
			}
		}
	}
	function delMenu(id){
		for(var i=0;i<menus.length;i++){
			if(menus[i].id==id){
				menus.splice(i,1);
				break;
			}
		}
	}
	function addTrLv1(){
		var newTr = tb.insertRow(-1);
		newTr.id='tr'+tmpId;
//		countMap[newTr.id]=0;
		var newTd0 = newTr.insertCell();
		var newTd1 = newTr.insertCell();
		newTd0.innerHTML = "<input type=\'text\' placeholder=\'请输入菜单名称\' class=\'input1 menu_name\' onblur='setTopMenuName(&apos;"+tmpId+"&apos;,this)'></input><a class=\'RightLv1Img\' onClick=\'insertTrLv2(&apos;"+tmpId+"&apos;)\' ></a>"; 
		newTd1.innerHTML= "<select class='select' onchange='changeClickEvent(&apos;"+tmpId+"&apos;,this)'><option value='url'>链接</option><option value='tuwen'>单图文</option></select>" +
				getInputStr(tmpId)+
				"<button onclick=\"removeTrLv1('"+tmpId+"');\" class='delBtn'>移   出</button>";
		
		var menu = new Menu();
		menu.id=tmpId+"";
		menu.level=1;
		menu.editable=1;
		menu.click="link";
		menus.push(menu);
		
		hideSubMenu();
		countLv1++;
		if(countLv1>=3){
			addBtn.disabled=true;
		}
		addTopMenu(tmpId);
		tmpId++;
	}
	function delTrLv1(id){
		var tr = document.getElementById("tr"+id);
		tb.deleteRow(tr.rowIndex);
		
		/*
		for(var key in map){
			if(map[key]==tr.id){
				var trLv2 = document.getElementById(key);
				tb.deleteRow(trLv2.rowIndex);
			}
		}*/
		
		var subMenus = getSubMenus(id);
		for(var i=0;i<subMenus.length;i++){
			delTrLv2(subMenus[i].id);
		}
		delMenu(id);
		countLv1--;
		if(countLv1<3){
			addBtn.disabled=false;
		}
//		countMap[tr.id]=0;
		delTopMenu(id);
		hideSubMenu();
	}
	function addTrLv2(id){
		var topMenu = getMenu(id);
		topMenu.click="sub_menu";
		var tr = document.getElementById("tr"+id);
		var subMenus=getSubMenus(id);
		if(subMenus.length>=5){
			return;
		}
		$(tr).find(".select").remove();
		$(tr).find(".input2").remove();
		var newTr = tb.insertRow(tr.rowIndex+1);
		newTr.id='tr'+tmpId;
//		newTr["data-ref"]=id;
//		map[newTr.id]=tr.id;
		var newTd0 = newTr.insertCell();
		var newTd1 = newTr.insertCell();
		newTd0.innerHTML = "-----<input type=\'text\' placeholder=\'请输入菜单名称\' class=\'input1 menu_name\' onblur='setSubMenuName(&apos;"+tmpId+"&apos;,this)'></input>"; 
		newTd1.innerHTML= "<select class='select' onchange='changeClickEvent(&apos;"+tmpId+"&apos;,this)'><option value='url'>链接</option><option value='tuwen'>单图文</option></select>" +
				getInputStr(tmpId)+
				"<button onclick=\"removeTrLv2('"+tmpId+"');\" class='delBtn'>移   出</button>";
//		countMap[tr.id]++;
		if(subMenus.length==0){
			preAddSubMenu(id);
		}
		addSubMenu(tmpId,id);
		var menu = new Menu();
		menu.id=tmpId;
		menu.pid=id;
		menu.level=2;
		menu.editable=1;
		menu.click="link";
		menus.push(menu);
		tmpId++;
		hideSubMenu();
	}
	function delTrLv2(id){
		var tr = document.getElementById("tr"+id);
		tb.deleteRow(tr.rowIndex);
//		var index = map[tr.id];
//		var index="tr"+tr['data-ref'];
//		countMap[index]--;
//		delete map[tr.id];
		var menu = getMenu(id);
		
		delMenu(id);
		var subMenus = getSubMenus(menu.pid);
		if(subMenus.length==0){
			var htmlStr="<select class='select' onchange='changeClickEvent(&apos;"+menu.pid+"&apos;,this)'><option value='url'>链接</option><option value='tuwen'>单图文</option></select>"+
						getInputStr(menu.pid);
			$("#tr"+menu.pid).find(".delBtn").before(htmlStr);
		}
		if(subMenus.length==0){
//			var i = index.substr(2);
			afterDelSubMenu(menu.pid);
		}
		delSubMenu(id);
		hideSubMenu();
	}
	function addTopMenu(id){
		var htmlStr = "<li id='m_"+id+"' data-index='"+countLv1+"'><a onclick='showSubMenu(&apos;"+id+"&apos;)'>[未命名]</a></li>";
		$topMenu.append(htmlStr);
		if(countLv1==1){
			$topMenu.addClass("has-menu-1");
		}else if(countLv1==2){
			$topMenu.removeClass("has-menu-1");
			$topMenu.addClass("has-menu-2");
		}else if(countLv1==3){
			$topMenu.removeClass("has-menu-2");
			$topMenu.addClass("has-menu-3");
		}
	}
	function delTopMenu(id){
		$("#m_"+id).remove();
		if(countLv1==1){
			$topMenu.removeClass("has-menu-2");
			$topMenu.addClass("has-menu-1");
		}else if(countLv1==2){
			$topMenu.removeClass("has-menu-3");
			$topMenu.addClass("has-menu-2");
		}
	}
	function preAddSubMenu(id){
		var htmlStr = "<div class='ui-popover' id='p_"+id+"' style='display:none;'>" 
			+"<div class='arrow'></div>"
			+"<div class='ui-popover-content'></div>" 
			+"</div>";
		$subMenu.append(htmlStr);
	}
	function addSubMenu(id,pid){
		var htmlStr="<a id='m_"+id+"'>[未命名]</a>";
		$("#p_"+pid).find(".ui-popover-content").append(htmlStr);
	}
	function delSubMenu(id){
		$("#m_"+id).remove();
	}
	function afterDelSubMenu(id){
		$("#p_"+id).remove();
	}
	function hideSubMenu(){
		$(".ui-popover").hide();
	}
	function getSelectStr(id){
		return "<select class='select' onchange='changeClickEvent(&apos;"+id+"&apos;,this)'><option value='url'>链接</option><option value='tuwen'>单图文</option></select>";
	}
	function getInputStr(id){
		return "<input type='url' class='input2' onchange='changeUrlValue(&apos;"+id+"&apos;,this)'/>";
	}
	init();
	/*
	$("#testTabLv1").on("change",".select",function(){
		$(this).next().remove();
		if(this.value=="tuwen"){
			$(this).after(tuwenStr);
		}else if(this.value=="url"){
			var htmlStr="<input type='url' class='input2'/>";
			$(this).after(htmlStr);
		}
	})*/
	window.insertTrLv1=function(){
		addTrLv1();
	}
	window.removeTrLv1=function(id){
		delTrLv1(id);
	}
	window.insertTrLv2=function(id){
		addTrLv2(id);
	}
	window.removeTrLv2=function(id){
		delTrLv2(id);
	}
	window.showSubMenu=function(id){
		var index = $("#m_"+id).attr("data-index");
		if(countLv1==1){
			$("#p_"+id).css("left","87px").css("right","auto").show();
		}else if(countLv1==2){
			if(index==1){
				$("#p_"+id).css("left","27px").css("right","auto").show();
			}else if(index==2){
				$("#p_"+id).css("right","27px").css("left","auto").show();
			}
		}else if(countLv1==3){
			if(index==1){
				$("#p_"+id).css("left","7px").css("right","auto").show();
			}else if(index==2){
				$("#p_"+id).css("left","87px").css("right","auto").show();
			}else if(index==3){
				$("#p_"+id).css("right","7px").css("left","auto").show();
			}
		}
	}
	window.setTopMenuName=function(id,el){
		$("#m_"+id).find("a").text(el.value);
		var menu = getMenu(id);
		menu.name=el.value;
	}
	window.setSubMenuName=function(id,el){
		$("#m_"+id).text(el.value);
		var menu = getMenu(id);
		menu.name=el.value;
	}
	window.changeClickEvent=function(id,el){
		var menu = getMenu(id);
		$(el).next().remove();
		if(el.value=="tuwen"){
			//$(el).after(tuwenStr);
			$(el).after(getTuwenStr(id));
			menu.click="picture";
			menu.key=matters[0].id;
		}else if(el.value=="url"){
			var htmlStr=getInputStr(id);
			$(el).after(htmlStr);
			menu.click="link";
		}
	}
	window.changeUrlValue=function(id,el){
		var menu = getMenu(id);
		menu.key=el.value;
	}
	window.changeTuwenId=function(id,el){
		var menu = getMenu(id);
		menu.key=el.value;
	}
	window.publishMenus=function(){
		//通过ajax发布菜单
		var param={
				"menus": JSON.stringify(menus)
		}
		$.ajax({
			url:ctx+"/salerManage/weixin/menu/publish",
			type:"post",
			data:param,
			dataType:"json",
			success:function(result){
				if(result.isPublish){
					alert("菜单发布成功！");
				}else{
					alert(result.error);
				}
			}
		})
	}
//	api.getData=function(){
//		return menus;
//	}
//	return api;
}
new MenuManager();
