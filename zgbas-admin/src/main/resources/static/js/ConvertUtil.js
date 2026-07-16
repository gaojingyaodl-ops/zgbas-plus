var Convert = {
	/*把 JSON 对象转化为字符串 */
    ToJSONString: function(obj) {
        switch(typeof(obj))
        {
            case 'object':
                var ret = [];
                if (obj instanceof Array)  {
                    for (var i = 0, len = obj.length; i < len; i++) {
                        ret.push(Convert.ToJSONString(obj[i]));
                    }
                    return '[' + ret.join(',') + ']';
                } else if (obj instanceof RegExp) {
                    return obj.toString();
                }else if (obj ==null){
                	return null;
                }
                else {
                    for (var a in obj){
                    	ret.push(a + ':' + Convert.ToJSONString(obj[a]));
                    }
                    return '{' + ret.join(',') + '}';
                }
            case 'function':
                return 'function() {}';
            case 'number':
                return obj.toString();
            case 'string':
                return "\"" + obj.replace(/(\\|\")/g, "\\$1").replace(/\n|\r|\t/g, function(a) {return ("\n"==a)?"\\n":("\r"==a)?"\\r":("\t"==a)?"\\t":"";}) + "\"";
            case 'boolean':
                return obj.toString();
            default:
            	if (isEmpty(obj)){
            		return "''";
            	}
                return obj.toString();

        }
    },
    /*获取可编辑表格所有更改的数据,返回json对象*/
    ToSaveJson:function(gridId){
    	var inserted = $('#'+gridId).datagrid('getChanges','inserted');
		var updated = $('#'+gridId).datagrid('getChanges','updated');
		var deleted = $('#'+gridId).datagrid('getChanges','deleted');
		var result ={'_inserted':inserted,'_updated':updated,'_deleted':deleted};
		return result;
    },
    /*获取可编辑表格所有更改的数据,返回格式为后台接收需要的格式*/
    ToSaveParam:function(gridId,parentId){
		var result = Convert.ToSaveJson(gridId);
		result = Convert.ToJSONString(result);
		var param = {'_easy_grid':result};
		if(parentId){
			param = {'_easy_grid':result,'parentId':parentId};
		}
		return param;
    },
    /*将子表(可编辑表格)更改的数据集中到主表表单中,以便一次性保存*/
    setChildren2Form:function(formId,gridId){
    	var result = Convert.ToSaveJson(gridId);
    	var childrenData = Convert.ToJSONString(result);
    	if($('#_easy_grid').length == 0){
    		$('#'+formId).append('<input type="hidden" id="_easy_grid" name="_easy_grid">');
    	}
    	$('#_easy_grid').val(childrenData);
    },
    /*将表单中的对象序列化*/
    getJson4Form:function(formId){
    	var array = $('#'+formId).serializeArray();
    	var obj = {};
    	$.each(array,function(i,n){
    		var name = n.name;
    		var key = n.value;
    		var oldVal= obj[name];
    		if(oldVal==undefined || oldVal=="")
    		{
    			obj[name] = key;
    		}
    		else
    		{
    			obj[name] = obj[name]+","+key;
    		}

    	});
    	return obj;
    },
    codeValidate:function(url,data){
		var value = $.ajax({
			  url: url,
			  data : data,
			  async: false
			 }).responseText;
		if('true' == value){
			return true;
		}else{
			return false;
		}
    },
    search : function(searchFormId,gridId){
    	var param = Convert.getJson4Form(searchFormId);
		console.log("search", param);
		// console.log("search", param);
		$('#'+gridId).datagrid('options').queryParams = param;
		$('#'+gridId).datagrid('reload');
    },
    getCheckedIds : function(treeId,type,perfix){
    	var nodes = $('#'+treeId).tree('getChecked');
    	var m = '';
    	for(var i=0; i<nodes.length; i++){
    		if (m != '') m += ',';
    		var node = nodes[i];
    		var isLeaf = $('#'+treeId).tree('isLeaf',node.target);
    		if ((node.attributes.type==type || isEmpty(type)) && isLeaf ){
    			var nodeId= node.id;
    			if (!isEmpty(perfix)){
    				nodeId=nodeId.replace(perfix,'');
    			}
    			m += nodeId;
    		}
    	}
    	return m;
    },
    findValueByKey : function(jsonObject,keyField,keyValue,valueField){
		for(var i in jsonObject){
			if (jsonObject[i][keyField]==keyValue){
				return jsonObject[i][valueField];
			}
		}
	},
	focusEditor:function(tableId,field,editIndex){
			var editor = $('#'+tableId).datagrid('getEditor', {index:editIndex,field:field});
			if (editor){
				editor.target.focus();
			} else {
				var editors = $('#'+tableId).datagrid('getEditors', editIndex);
				if (editors.length){
					editors[0].target.focus();
				}
			}
	},
	findTreeValueByKey :function(jsonObject,keyField,keyValue,valueField){
		var rtnValue='';
		for(var i in jsonObject){
			var _dom=jsonObject[i];
			if (_dom[keyField]==keyValue){
				rtnValue = _dom[valueField];
				break;
			}
			var children = _dom.children;
			if(children!=null && children.length>0){
				rtnValue = Convert.findTreeValueByKey(children,keyField,keyValue,valueField);
				if (rtnValue!=undefined && rtnValue!=''){
					break;
				}
			}
		}
		return rtnValue;
	},
	numFormat :function(num) {
		return (num.toString().indexOf('.') !== -1) ? num.toLocaleString() : num.toString().replace(/(\d)(?=(?:\d{3})+$)/g, '$1,');
	},
	numFormatZero :function(num){
		return isEmpty(num) ? Number.parseFloat(0) : Number.parseFloat(num);
	},
	getRandomNum :function(leg){
		var num = '';
		if (leg){
			for (var i = 0; i < leg; i++) {
				num += Math.floor(Math.random() * 10);
			}
		}
		return num;
	},
	getNowDateStrWithRandom :function(leg){
		return this.getNowDateStr() + "" + this.getRandomNum(leg);
	},
	getNowDateStr :function(){
		var date = new Date();
		var month = date.getMonth() + 1;
		var strDate = date.getDate();
		if (month >= 1 && month <= 9) {
			month = "0" + month;
		}
		if (strDate >= 0 && strDate <= 9) {
			strDate = "0" + strDate;
		}
		var hours = date.getHours();
		if(hours >=0 && hours <=9){
			hours = "0" + hours;
		}
		var minutes = date.getMinutes();
		if(minutes >=0 && minutes <=9){
			minutes = "0" + minutes;
		}
		var seconds = date.getSeconds();
		if(seconds >=0 && seconds <=9){
			seconds = "0" + seconds;
		}
		return date.getFullYear() + "" + month + "" + strDate + hours + "" + minutes + "" + seconds;
	},
	basToFixed(value, decimal = 2) {
		// 解决ToFixed四舍五入精度损失问题
		const n = Math.pow(10, decimal);
		return divideFloat(Math.round(multiplyFloat(value, n)), n)
	},
	accMul(arg1, arg2) {
		// 乘法函数，用来得到精确的乘法结果
		var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
		try {
			m += s1.split(".")[1].length;
		} catch (e) {
		}
		try {
			m += s2.split(".")[1].length;
		} catch (e) {
		}
		return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
	},
	getFileId(fileId){
		const value = String(fileId);
		if (value){
			return String(value.replace(",",""));
		}
		return fileId;
	},
	browserRedirect() {
		var sUserAgent = navigator.userAgent.toLowerCase();
		var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
		var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
		var bIsMidp = sUserAgent.match(/midp/i) == "midp";
		var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
		var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
		var bIsAndroid = sUserAgent.match(/android/i) == "android";
		var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
		var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
		if (bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM){
			return false;
		}
		return true;
	},
	convertSkin() {
		var _skinClass = "";
		var _skin = window.localStorage.getItem("skin");
		if (_skin) {
			_skinClass = _skin.split('|')[0];
		}
		if (_skinClass == "skin-blue") {
			return "#1BA3FF"
		} else if (_skinClass == "skin-green") {
			return "#45b681";
		} else if (_skinClass == "skin-purple") {
			return "#605ca8";
		} else if (_skinClass == "skin-red") {
			return "#dd4b39";
		} else if (_skinClass == "skin-yellow") {
			return "#f39c12";
		} else {
			return "#43b77f";
		}
	},
	buildCompanyText(text, node) {
		text += '<br/>'
		if (node.piccAmount > 0){
			text += "<span style='color: #005977;'>【人保：" + this.formatterNum1(node.piccAmount) + "】</span>"
		}
		if (node.daDiAmount > 0){
			text += "<span style='color: #5e00b3;'>【大地：" + this.formatterNum1(node.daDiAmount) + "】</span>"
		}
		if (node.zyAmount > 0){
			text += "<span style='color: #c1a32e;'>【中银：" + this.formatterNum1(node.zyAmount) + "】</span>"
		}
		if (node.ourAmount > 0){
			text += "<span style='color: #e3142c;'>【自主：" + this.formatterNum1(node.ourAmount) + "】</span>"
		}
		return text;
	},
	formatterNum(num, row, index) {
	if (num) {
		num = parseFloat(num).toFixed(2).toString().split(".");
		num[0] = num[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");
		return num.join(".");
	} else {
		return num;
	}
},
	formatterNum1(num){
		if (num) {
			num = Math.round(parseFloat(num)).toString().split(".");
			num[0] = num[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");
			return num.join(".");
		} else {
			return num;
		}
	}
};

/**
 * 除法运算
 * @param {Number} arg1 - 参数1除数
 * @param {Number} arg2 - 参数2被除数
 */
function divideFloat(arg1, arg2) {
	const arg1Str = arg1 + '';
	const arg2Str = arg2 + '';
	const arg1StrFloat = arg1Str.split('.')[1] || '';
	const arg2StrFloat = arg2Str.split('.')[1] || '';
	const m = arg2StrFloat.length - arg1StrFloat.length;
	const transferResult = +(arg1Str.replace('.', '')) / +(arg2Str.replace('.', ''));
	return transferResult * Math.pow(10, m);
}

// 乘法运算
function multiplyFloat(arg1, arg2) {
	let m = 0;
	const arg1Str = arg1 + ''; const arg2Str = arg2 + '';
	const arg1StrFloat = arg1Str.split('.')[1];
	const arg2StrFloat = arg2Str.split('.')[1];
	arg1StrFloat && (m += arg1StrFloat.length);
	arg2StrFloat && (m += arg2StrFloat.length);
	const transferResult = +(arg1Str.replace('.', '')) * +(arg2Str.replace('.', ''));
	return transferResult / Math.pow(10, m)
}

function isEmpty(str) {
	return (typeof (str) === "undefined" || str === null || (str.length === 0));
};

function formatFloat(src, pos)
{
    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);
}
function fmoney(s, n) {
	n = n >= 0 && n <= 20 ? n : 2;
	s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";
	var l = s.split(".")[0].split("").reverse();
	var r = n > 0 ? s.split(".")[1] : '';
	t = "";
	var len =l.length;
	for (var i = 0; i < len; i++) {
		t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != len ? "," : "");
	}
	var t_arr= t.split("").reverse();
	if(t_arr[0]=='-' && t_arr[1]==','){
		t_arr[1]='';
	}
	var value = t_arr.join("");
	if (n > 0) {
		value += "." + r;
	}
	return value;
}

function moneyrate(s,n){
	var rate=parseFloat(s/n).toFixed(4);
	return  Math.round(rate*10000)/100+'%';
}

function rmoney(s) {
	return parseFloat(s.replace(/[^\d\.-]/g, ""));
}
function mobileHid(s){
	 if($('#mobileViewFlag').val() ){
		 return s;
	 }else{
		 var start = Math.floor((s.length/2))-Math.floor((s.length/4));
		 var end = Math.floor((s.length/2))+Math.floor((s.length/4));
		 var s2=s.substr(0,start);
		 for(var i =start; i<end;i++){
			 s2+='*';
		 }
		 s2+=s.substr(end);
		 return s2;
	 }
};

$.extend($.fn.validatebox.defaults.rules, {
	equals: {
        validator: function(value, param){
        	console.log('equals',value,param[0], $(param[0]).val());
        	return value == $(param[0]).val();
        },
        message: '两次输入不一致'
    },
    number: {
        validator: function(value, param){
        	var reg_Float = /^\d+(\d|(\.[0-9]{0,3}))?$/;
        	 return reg_Float.test(value);
        },
        message: '只能输入数字'
    }
});
