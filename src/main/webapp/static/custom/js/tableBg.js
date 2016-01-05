//表格背景色改变
window.onload = function (){
	var cNames = document.getElementById("grid").getElementsByClassName("tableBg")
	for(i=0;i<cNames.length;i++){
		if(i%2 == 0){
			cNames[i].style.backgroundColor = '#fff';
		}else{
			cNames[i].style.backgroundColor = '#f2f5fa';
		}	
	}
}











