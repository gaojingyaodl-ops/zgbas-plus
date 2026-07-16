/**
 * 常用工具JS
 * */

/**拓展日期格式化方法*/
Date.prototype.format = function(format)
{
	 var o = {
	 "M+" : this.getMonth()+1, //month
	 "d+" : this.getDate(),    //day
	 "h+" : this.getHours(),   //hour
	 "m+" : this.getMinutes(), //minute
	 "s+" : this.getSeconds(), //second
	 "q+" : Math.floor((this.getMonth()+3)/3),  //quarter
	 "S" : this.getMilliseconds() //millisecond
	 };
	 
	 if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
		(this.getFullYear()+"").substr(4 - RegExp.$1.length));
	 		for(var k in o)
	 			if(new RegExp("("+ k +")").test(format))
	 				format = format.replace(RegExp.$1,
	 				RegExp.$1.length==1 ? o[k] :
	 				("00"+ o[k]).substr((""+ o[k]).length));
	 		return format;
};
// 只显示年月，不显示日
var intiMonthBox = function(id){
	var db = $('#'+id);
	db.datebox({
		onShowPanel: function () {//显示日趋选择对象后再触发弹出月份层的事件，初始化时没有生成月份层
			span.trigger('click'); //触发click事件弹出月份层
			if (!tds) setTimeout(function () {//延时触发获取月份对象，因为上面的事件触发和对象生成有时间间隔
				tds = p.find('div.calendar-menu-month-inner td');
				tds.click(function (e) {
					e.stopPropagation(); //禁止冒泡执行easyui给月份绑定的事件
					var year = /\d{4}/.exec(span.html())[0]//得到年份
						, month = parseInt($(this).attr('abbr'), 10); //月份，这里不需要+1
					db.datebox('hidePanel')//隐藏日期对象
						.datebox('setValue', year + '-' + month); //设置日期的值
				});
			}, 0);
			yearIpt.unbind();//解绑年份输入框中任何事件
		},
		parser: function (s) {
			if (!s) return new Date();
			var arr = s.split('-');
			return new Date(parseInt(arr[0], 10), parseInt(arr[1], 10) - 1, 1);
		},
		formatter: function (d) {
			var month = d.getMonth() + 1;
			if(month<10){
				month = '0'+month;
			}
			return d.getFullYear() + '-' + month;
		}
	});
	var p = db.datebox('panel'), //日期选择对象
		tds = false, //日期选择对象中月份
		yearIpt = p.find('input.calendar-menu-year'),//年份输入框
		span = p.find('span.calendar-text'); //显示月份层的触发控件
}
/**
 * 获取当天日期
 * */
Date.prototype.getWeek = function()
{
	if(new Date().getDay()==0)          
		return "周日";
	if(new Date().getDay()==1)          
		return "周一";
	if(new Date().getDay()==2)          
		return "周二";
	if(new Date().getDay()==3)          
		return "周三";
	if(new Date().getDay()==4)          
		return "周四";
	if(new Date().getDay()==5)          
		return "周五";
	if(new Date().getDay()==6)          
		return "周六";
};

/**
	传入毫秒为单位的时间差
*/
Date.prototype.getHms = function(ms)
{
	var vd,vh,vm,vs,leave1,leave2,leave3;
	var vd = Math.floor(ms/(24*3600*1000));

	var leave1 = ms%(24*3600*1000);    
	var vh = Math.floor(leave1/(3600*1000));
	
	var leave2 = leave1%(3600*1000);        
	var vm = Math.floor(leave2/(60*1000));
	
	var leave3 = leave2%(60*1000);     
	var vs = Math.round(leave3/1000);

	return {day:vd,hour:vh,minute:vm,second:vs};
}

/**
 * 冒泡排序
 * */
function bubbleSort(secArray)
{
        var len = secArray.length,empty;
        for(var k=0; k<len-1; k++) {
            for(var i=0; i<len-k-1; i++) {
                if(secArray[i]>secArray[i+1]) {
                    empty = secArray[i+1];
                    secArray[i+1] = secArray[i];
                    secArray[i] = empty;
                }
            }
        }
}


/**
 * 字符串判空
 * @param str
 * @returns
 */
function strIsNull(str)
{
	return (str == null || str == "" || str.length <= 0);
}

function getServerDate()
{
	var xmlHttp = false;
	try {
	xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
	try {
	    xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	} catch (e2) {
	    xmlHttp = false;
	}
	}

	if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
	xmlHttp = new XMLHttpRequest();
	}

	xmlHttp.open("GET", "resources/klinePages.jsp", false);
	xmlHttp.setRequestHeader("Range", "bytes=-1");
	xmlHttp.send(null);

	severtime = new Date(xmlHttp.getResponseHeader("Date"));
	return 	severtime;
}
function getProvinceName(code) {
	return provinces[code] || '';
}
const provinces = {
	'110000': '北京市',
	'120000': '天津市',
	'130000': '河北省',
	'140000': '山西省',
	'150000': '内蒙古自治区',
	'210000': '辽宁省',
	'220000': '吉林省',
	'230000': '黑龙江省',
	'310000': '上海市',
	'320000': '江苏省',
	'330000': '浙江省',
	'340000': '安徽省',
	'350000': '福建省',
	'360000': '江西省',
	'370000': '山东省',
	'410000': '河南省',
	'420000': '湖北省',
	'430000': '湖南省',
	'440000': '广东省',
	'450000': '广西壮族自治区',
	'460000': '海南省',
	'500000': '重庆市',
	'510000': '四川省',
	'520000': '贵州省',
	'530000': '云南省',
	'540000': '西藏自治区',
	'610000': '陕西省',
	'620000': '甘肃省',
	'630000': '青海省',
	'640000': '宁夏回族自治区',
	'650000': '新疆维吾尔自治区',
	'710000': '台湾省',
	'810000': '香港特别行政区',
	'820000': '澳门特别行政区'
};
