/**
 * 
 */
require.config({
    paths: {
        jquery: '../jquery/jquery-1.9.1.min'
    }
});

require(['../artDialog/src/dialog'],function(dialog){
	$('#addressBtn').click( function () {
		var d = dialog({
			title: '添加地址',
			content: $('#dialogContent').html(),
			okValue: '确 定',
			ok: function () {
				var that = this;
				setTimeout(function () {
					that.title('提交中..');
				}, 2000);
				return false;
			},
			cancelValue: '取消',
			cancel: function () {}
		});

		d.showModal();
	});
});