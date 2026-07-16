Banner = function(timeDelay) {
	Banner.timerID = null;
	Banner.timeDelay = timeDelay;
	Banner.bannerIndex = 0;
};

Banner.prototype = {
	start : function() {
		$('.indicator a').each(function(i,e){
			$(this).click(function(e){
				clearInterval(Banner.timerID);
				Banner.bannerIndex=i;
				Banner.prototype.selectNode($(this),Banner.bannerIndex, e);
				var selectBannerFunction = Banner.prototype.selectBanner;
				var timeDelay = Banner.timeDelay;
				Banner.timerID = setInterval(function() {
					selectBannerFunction();
				}, timeDelay);
			});
		});
		this.autoRoll();
		this.initTouch();
	},
	autoRoll : function() {
		var selectBannerFunction = this.selectBanner;
		var timeDelay = Banner.timeDelay;
		Banner.timerID = setInterval(function() {
			selectBannerFunction();
		}, timeDelay);
	},
	selectBanner : function(direct) {
		var selectNodeFunction = Banner.prototype.selectNode;
		var count = $('.first_view .banner').length;
		$('.first_view .banner').each(function(j) {
			if ($(this).css("display") != 'none') {
				$('.indicator a').each(function(i, e) {
					if (direct==undefined || direct=='left'){
						if (i == Banner.bannerIndex) {
							var dom;
							if (Banner.bannerIndex == count - 1) {
								Banner.bannerIndex = 0;
								dom = $('.indicator a:first');
							} else {
								Banner.bannerIndex++;
								dom = $(this).next();
							}
							selectNodeFunction(dom, Banner.bannerIndex, e);

							return false;
						}
					}else{
						if (i == Banner.bannerIndex) {
							var dom;
							if (Banner.bannerIndex == 0) {
								Banner.bannerIndex = count - 1;
								dom = $('.indicator a:last');
							} else {
								Banner.bannerIndex--;
								dom = $(this).prev();
							}
							selectNodeFunction(dom, Banner.bannerIndex, e);
							
							return false;
						}
						
					}
				});
				return false;
			}
		});
	},
	selectNode : function(dom, i, e) {
		// e.stopPropagation();
		dom.addClass('current').siblings().removeClass('current');
		$('.first_view .banner').each(function(j) {
			if (i == j) {
				$(this).show().siblings().hide();
			}
		});
	},
	initTouch: function(){
		var first_view= document.getElementById("first_view");
		touch.on(first_view, 'touchstart', function(ev){
			ev.preventDefault();
		});
		
		touch.on(first_view, 'swipeend', function(ev){
//			console.log(ev.direction);
			var selectBannerFunction = Banner.prototype.selectBanner;
			if (ev.direction=='left'){
				selectBannerFunction();
			}else if (ev.direction=='right'){
				selectBannerFunction('right');
			}
		});
	}
};
