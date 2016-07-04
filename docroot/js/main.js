(function($) {
	$("#assets-column h2").on("click", function() {
		var ul = $(this).parent().children("ul");
		var isUlVisible = ul.is(":visible");
		if(isUlVisible){	
			$(this).parent().children("ul").hide();
			$(this).parent().children("ul").addClass("hidden");
		}else{
			$(this).parent().children("ul").show();
			$(this).parent().children("ul").removeClass("hidden");
		}	
	});
})(jQuery);