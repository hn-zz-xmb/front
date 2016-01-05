$(function() {
	$("#more").click(function() {
		// $("#filter_more").toggle();
		if ($(this).hasClass("down")) {
			$(this).removeClass("down").addClass("up");
			$("#filter_more").slideDown();
		} else {
			$(this).removeClass("up").addClass("down");
			$("#filter_more").slideUp();
		}
	});

	$("#tab").on("click", "a", function() {
		$("#tab li a").removeClass("active");
		$(this).addClass("active");
	});
});

function showTab(i) {
	$(".tab_content").hide();
	$("#tab" + i).show();
}