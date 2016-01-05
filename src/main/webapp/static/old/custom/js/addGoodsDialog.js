require.config({
    paths: {
        jquery: '../../jquery/jquery-1.9.1.min'
    }
});

require(['../../artDialog/src/dialog'],function(dialog){
	var ctxPath = $('#ctxPath').val();
	var storeId = $("#storeId").val();
	var dialogContent = "";
	var categoryId = "";
	var goodsMap = {};
	 
	var showDialog=function(content){
		var d = dialog({
			title: '添加商品',
			content: content,
			okValue: '确 定',
			ok: function () {
				var that = this;
				setTimeout(function () {
					that.title('提交中..');
				}, 2000);
				
				var rows = $('.selected_goods_tb tr[id]');
				var sel_rows = $("#sel_goods_tb tr[id]");
				var index = $("#sel_goods tr").length;
				var ids = [];
				var qtys = {};
				for(var i=0; i<rows.length; i++){
					var mark= false;
					for(var j=0; j<sel_rows.length; j++){
						if(rows[i].id==sel_rows[j].id){
							mark=true;
							break;
						}
					}
					if(mark){continue;}
					var id = rows[i].id;
					ids[i]=id;
					qtys[id]=$(rows[i]).find(".quantity").val();
				}
				
				var htmlStr = "";
				var htmlStr2= "";
				for(var i=0; i<ids.length; i++){
					htmlStr += '<tr id="'+ids[i]
						+'"><td><input type="checkbox" class="sel_goods_cb" value="'
						+ids[i]+'"></td><td>'
						+goodsMap[ids[i]].name+'</td><td>'+goodsMap[ids[i]].salesPrice
						+'</td><td><input type="text" class="quantity" value="'
						+qtys[ids[i]]+'"></td></tr>';
					htmlStr2+= '<tr id="sel_goods_'+ids[i]+'"><td><input type="hidden" value="'
						+ids[i]+'" name="setMealItemList['
						+(index+i)+'].goods.id" class="goodsId"></td><td><input type="hidden" value="'
						+qtys[ids[i]]+'" name="setMealItemList['
						+(index+i)+'].count" class="quantity"></td></tr>';
				}
				$("#sel_goods_tb").append(htmlStr);
				$("#sel_goods").append(htmlStr2);
				$("#hasGoods").val("Y");
				init();
				return true;
			},
			cancelValue: '取消',
			cancel: function () {}
		});
		d.showModal();
		
		var param={
				"storeId": storeId
		}
		findGoods(param);
	}
	
	var initGoodsMap=function(data){
		for(var i=0; i<data.length; i++){
			goodsMap[data[i].id]=data[i];
		}
	}

	var initPagination=function(page){
		var html = "";
		for(var i=1; i<=page.totalPages; i++){
			if(page.number==i-1){
				html += "<a class='active' onclick='getPage("+i+")'>"+i+"</a>";
			}else{
				html += "<a onclick='getPage("+i+")'>"+i+"</a>";
			}
		}
		if(page.number+2<=page.totalPages){
			html += "<a onclick='getPage("+(page.number+2)+")'>下页</a>";
		}
		html += "<label>共"+page.totalPages+"页</label>";
		$("#pagination").html(html);
	}
	
	var findGoods=function(param){
		
		var url=ctxPath+"/salermanage/setmealmanage/goods";
		var obj=callJson(url,param,true);
		var data = obj.content;
		
		initGoodsMap(data);
		
		var htmlStr = "<tr><th></th><th>名称</th><th>价格</th></tr>";
		for(var i=0; i<data.length; i++){
			htmlStr += '<tr><td><input type="checkbox" class="all_goods_cb" value="'+data[i].id+'">'
				+ '<td>'+data[i].name+'</td><td>'+data[i].salesPrice+'</td></tr>';
		}
		$("#all_goods_tb").html(htmlStr);
		
		initPagination(obj);
	}
	
	
	//get dialog
	var param={
			"storeId": storeId
	}
	
	var url=ctxPath+"/salermanage/setmealmanage/dialog";
	var result=callText(url,param,true);
	dialogContent = "<div id='dialogContent'>"+result+"</div>";
	
	$('#addProductBtn').click( function () {
		showDialog(dialogContent);
	});
	
	
	window.getPage = function(n){
		var param ={
				"storeId": storeId,
				"pageNumber": n,
				"categoryId": categoryId
		}
		
		var url=ctxPath+"/salermanage/setmealmanage/goods";
		var obj=callJson(url,param,true);
		var data = obj.content;
		var page = obj;
		
		initGoodsMap(data);
		
		var htmlStr = "<tr><th></th><th>名称</th><th>价格</th></tr>";
		for(var i=0; i<data.length; i++){
			htmlStr += '<tr><td><input type="checkbox" class="all_goods_cb" value="'+data[i].id+'">'
				+ '<td>'+data[i].name+'</td><td>'+data[i].salesPrice+'</td></tr>';
		}
		$("#all_goods_tb").html(htmlStr);
		
//		$("#pagination a").removeClass("active");
//		$($("#pagination a")[page.number]).addClass("active");
		initPagination(page);
	}
	
	window.filterGoods = function(cateId){
		categoryId = cateId;
		
		$("#categoryList a").removeClass("on");
		if(cateId==""){
			$("#all").addClass("on");
		}else{
			$("#"+cateId).addClass("on");
		}
		
		var param ={
				"storeId": storeId,
				"categoryId": cateId
		}
		findGoods(param);
	}
	
	window.searchGoods = function(){
		categoryId="";
		
		$("#categoryList a").removeClass("on");
		$("#all").addClass("on");
		
		var param ={
				"storeId": storeId,
				"goodsName": $("#goods_name").val()
		}
		findGoods(param);
	}
	
	$('body').on('click', '#add_btn', function(){
		var cbs = $(".all_goods_cb:checked");
		var rows = $(".selected_goods_tb tr[id]");
		var ids = [];
		var k = 0;
		for(var i=0; i<cbs.length; i++){
			var mark = false;
			for(var j=0; j<rows.length; j++){
				if(cbs[i].value==rows[j].id){
					mark=true;
					break;
				}
			}
			if(!mark){
				ids[k]=cbs[i].value;
				k++;
			}
		}
		
		var htmlStr = "";
		for(var i=0; i<ids.length; i++){
			htmlStr += '<tr id="'+ids[i]
				+'"><td><input type="checkbox" class="selected_goods_cb" value="'
				+ids[i]+'"></td><td>'+goodsMap[ids[i]].name+'</td><td>'+goodsMap[ids[i]].salesPrice
				+'</td><td><input type="text" value="1" class="quantity"></td></tr>';
		}
		$(".selected_goods_tb").append(htmlStr);
		
	});
	
	$("body").on("click", "#remove_btn", function(){
		var cbs = $(".selected_goods_cb:checked");
		for(var i=0; i<cbs.length; i++){
			$("#"+cbs[i].value).remove();
		}
	});
});