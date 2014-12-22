$(function() {
	String.prototype.decodeHTML = function() {
	return $("<div>", {html: "" + this}).html();
};
var $main = $(".mainContent"),

init = function() {	
	jQuery(document).ready(function() {
			jQuery('.preview').addClass("hidden").viewportChecker({
					classToAdd: 'visible animated fadeIn',
					offset: 100
				 });
	});

	// Do this when a page loads.
	$('.mainNavigation li a').click(function() {
		$('.mainNavigation li.current').removeClass('current');
		$(this).closest('li').addClass('current');
	});	
	if(window.location.pathname === "/openthings/tutorial.php" || window.location.pathname === "/openthings/project.php" || window.location.pathname === "/openthings/user.php"){
		UserRating();
	}
	else{}		
	if(window.location.pathname === "/openthings/tutorials.php" || window.location.pathname === "/openthings/index.php"  || window.location.pathname === "/openthings/designers.php"){
		$( ".mainContent" ).addClass( "overviewPage" );
	}
	else{
		$( ".mainContent" ).removeClass( "overviewPage" );
	}
	if(window.location.pathname === "/openthings/standard.php"){
		$( ".mainContent" ).addClass( "standardPage" );
	}
	else{
		$( ".mainContent" ).removeClass( "standardPage" );
	}
},

ajaxLoad = function(html) {
	document.title = html
		.match(/<title>(.*?)<\/title>/)[1]
		.trim()
		.decodeHTML();		

	init();
},

loadPage = function(href) {
	$main.load(href + " .mainContent>*", ajaxLoad);
};

init();

	$(window).on("popstate", function(e) {
		if (e.originalEvent.state !== null) {
			loadPage(location.href);
		}
	});

	$(document).on("click", "a, area", function() {
		var href = $(this).attr("href");

		if (href.indexOf(document.domain) > -1
			|| href.indexOf(':') === -1)
		{
			history.pushState({}, '', href);
			loadPage(href);
			return false;
		}
	});
});