    $(document).ready(function() {
        //$("#ss").addClass("sear");		
		$(".seac ul li").click(function(){
		$(this).addClass("sear").children(".h").slideToggle().end().siblings().removeClass("sear").children(".h").slideUp(300);
		})
    }); 
