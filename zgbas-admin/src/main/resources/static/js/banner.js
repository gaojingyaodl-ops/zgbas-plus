Banner = function(timeDelay) {
	Banner.timerID = null;
	Banner.timeDelay = timeDelay;
	Banner.bannerIndex = 0;
};

Banner.prototype = {
	start : function() {
		$('.slide-number a').each(function(i,e){
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
	},
	autoRoll : function() {
		var selectBannerFunction = this.selectBanner;
		var timeDelay = Banner.timeDelay;
		Banner.timerID = setInterval(function() {
			selectBannerFunction();
		}, timeDelay);
	},
	selectBanner : function() {
		var selectNodeFunction = Banner.prototype.selectNode;
		var count = $('.slide-wrap .slide').length;
		$('.slide-wrap .slide').each(function(j) {
			if ($(this).css("display") != 'none') {
				$('.slide-number a').each(function(i, e) {
					if (i == Banner.bannerIndex) {
						var dom;
						if (Banner.bannerIndex == count - 1) {
							Banner.bannerIndex = 0;
							dom = $('.slide-number a:first');
						} else {
							Banner.bannerIndex++;
							dom = $(this).next();
						}
						selectNodeFunction(dom, Banner.bannerIndex, e);

						return false;
					}
				});
				return false;
			}
		});
	},
	selectNode : function(dom, i, e) {
		// e.stopPropagation();
		dom.addClass('current').siblings().removeClass('current');
		$('.slide-wrap .slide').each(function(j) {
			if (i == j) {
				$(this).addClass('current').siblings().removeClass('current');
			}
		});
	}
};
