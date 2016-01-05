function checkAll(el){
	var cbs = $(".checkitem");
	for(var i=0; i<cbs.length; i++){
		cbs[i].checked=el.checked;
	}
	
	var cbs2 = $(".checkall");
	for(var i=0; i<cbs2.length; i++){
		cbs2[i].checked=el.checked;
	}
}

function unCheck(el){
	if(!el.checked){
		var cbs = $(".checkall");
		for(var i=0; i<cbs.length; i++){
			cbs[i].checked=false;
		}
	}
}