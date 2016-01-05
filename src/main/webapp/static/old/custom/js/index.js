require.config({
	paths : {
		jquery : "../jquery/jquery-1.9.1.min"
	}
});

require([ 'jquery' ], function($) {
//	// 加载特色美食
//	$("#tsms").load("./index/tsms");
//	// 加载美食天地
//	$("#mstd").load("./index/mstd");
	//加载招聘信息
	$("#inviteinfo").load("./index/inviteinfo");
})