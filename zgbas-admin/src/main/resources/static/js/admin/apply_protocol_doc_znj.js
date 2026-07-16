var applyProtocolDoc_znj={
		//增加 明细 输入域
		addProtocolInput:function(obj,docType,num){
			var entityId=$("#entityId").val();
			if (num == 0) {
				entityId = 0;
			}
			var _curNumber = 0;
			var url= _ctx + "/apply/protocolDocument/addProtocolDocCkhDetail";
			_curNumber = number1;

			var _tab=$(".detail_div");
			var _len=_tab.length;
			if(_len >= 3) {
				$.messager.alert('提示','最多允许添加3条明细!','warning');
				return
			}
			$.post(url,{docType:docType,curNumber:_curNumber,id:entityId},function(html){
				//获得随机码
				$("#b_parenDiv").find("#endDiv").before(html);
				//加载html的组件
				if(num == 0) {
					applyProtocolDoc_znj.initLoadEasyui(_curNumber);
					number1 = number1+1;
				} else {
					for (var i = 0; i < _detailSize; i++) {
						applyProtocolDoc_znj.initLoadEasyui(i);
						applyProtocolDoc_znj.initLoadEasyui(number1);
						number1 = number1+1;
					}
				}
				$('.easyui-linkbutton').linkbutton();
			});
		},
		removeNode:function(obj){
			var _obj=$(obj);
			var _tbl=$(".detail_div");
			var _len=_tbl.length;
			var flag=$(obj).hasClass("detail");
			if(_len>1){
				var id=_obj.attr("detailId");
				if(id){
					$.messager.confirm('提示','确认要删除该记录吗?',function(t){
						if(t){
							$(obj).parent().parent().remove();
						}
						return ;
					});			
				}else{
					$(obj).parent().parent().remove();
				}
				if (_len-1==2){
					$('#insertB,#insertS,#removeBuy,#removeSell').removeClass('hide');
				}
			}
		},
		//统一加载 easyui 组件
		initLoadEasyui:function(randomNum){
			var entityStatus=$("#entityStatus").val();

			$("#totalAmount"+randomNum).numberbox({
				required:true,
				precision: 2,
				onChange: function () {
				},
				parser: function(val) {
					// 解析时去除千分位分隔符
					if(val) {
						return parseFloat(String(val).replace(/,/g, ''));
					} else {
						if(isNaN(val)) {
							return '';
						} else {
							return val;
						}
					}
				},
				formatter: function(val) {
					if (val || val === 0) {
						val = parseFloat(val).toFixed(2).toString().split(".");
						val[0] = val[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");
						return val.join(".");
					} else {
						if(isNaN(val)){
							return ""
						} else {
							return val;
						}
					}
				}
			})
			$("#contractNo"+randomNum).textbox({
				required:true
			});
			$("#deliveryDate"+randomNum).datebox({
				height:30,
				required:true,
				editable:false,
			});
			$("#creditDays"+randomNum).numberbox({
				required:true,
				precision: 0,
			});
			$("#payDateStr"+randomNum).datebox({
				height:30,
				required:true,
				editable:false,
			});


			if(entityStatus=="N"|| entityStatus=="B"|| entityStatus=="C"){
				$("#realPayDateStr"+randomNum).validatebox({
					required:true
				});
				$("#overdueLateFees"+randomNum).validatebox({
					required:true
				});

			}else{
				//禁用所有输入框
				$(".detail-content").attr("readOnly","true");
				//合同类型
				$("#contractNo"+randomNum).textbox('readonly', true);
				$("#totalAmount"+randomNum).numberbox('readonly', true);
				$("#deliveryDate"+randomNum).datebox('readonly', true);
				$("#payDateStr"+randomNum).datebox('readonly', true);
				$("#creditDays"+randomNum).numberbox('readonly', true);
				$("#realPayDateStr"+randomNum).validatebox({
					required:false
				});
				$("#overdueLateFees"+randomNum).validatebox({
					required:false
				});
				$("#realPayDateStr"+randomNum).validatebox('readonly', true);
				$("#overdueLateFees"+randomNum).validatebox('readonly', true);

			}
			
		},
	syncContractDetail(number) {
		var _contractNo = $("#contractNo"+number).val();
		var _endDate = $("#endDate").val();
		if (!_endDate) {
			$.messager.alert('提示','截止日期不可为空!','warning');
			return;
		}
		if (!_contractNo) {
			$.messager.alert('提示','合同编号不可为空!','warning');
			return;
		}
		$.ajax({
			url: _ctx + "/apply/protocolDocument/findDetailByContractNo?contractNo=" + _contractNo + "&endDate=" + _endDate,
			type: "post",
			async: false,
			success: function (obj) {
				if (obj != null && obj != undefined) {
					$("#totalAmount"+number).numberbox("setValue", obj.totalAmount);
					$("#deliveryDate"+number).datebox("setValue", obj.confirmDate);
					$("#creditDays"+number).numberbox("setValue", obj.creditCycle);
					$("#payDateStr"+number).datebox("setValue", obj.appointPayFullTime);
					$("#realPayDateStr"+number).text(obj.realPayDateStr);
					// 手动触发 validatebox 校验
					$("#realPayDateStr"+number).validatebox('validate');
					$("#overdueLateFees"+number).text(obj.overdueLateFees);
					$("#overdueLateFees"+number).validatebox('validate');
					$("#overdueLateFeeSum"+number).val(obj.overdueLateFeeSum);


					var overdueLateFeesSumStr = '';
					var totalSum = 0;
					var arr=new Array();
					var num = 0;
					for(var i=0;i<number1;i++){
						var overdueLateFeesSum = $("#overdueLateFeeSum"+i).val();
						if(overdueLateFeesSum) {
							totalSum = Number(totalSum) + Number(overdueLateFeesSum);
							if (num == 0) {
								overdueLateFeesSumStr = overdueLateFeesSum + '';
							} else {
								overdueLateFeesSumStr = overdueLateFeesSumStr + '+' + overdueLateFeesSum;
							}
							num += 1;
						}
					}
					if (num > 1) {
						if (overdueLateFeesSumStr) {
							overdueLateFeesSumStr = overdueLateFeesSumStr + '=' +totalSum + '元'
						}
					} else {
						if (overdueLateFeesSumStr) {
							overdueLateFeesSumStr = totalSum + '元'
						}
					}


					$("#overdueLateFeesSumHidden").text(overdueLateFeesSumStr)
					$("#overdueLateFeesSum").text(overdueLateFeesSumStr)
					$("#overdueLateFeesSum").validatebox('validate');

					$("#companyName").textbox("setValue", obj.companyName);
					$("#accountName").combobox("setValue", obj.ourCompanyName);
					$("#signCompanyName").combobox("setValue", obj.ourCompanyName);
					$("#bankName").textbox("setValue", obj.bankName);
					$("#bankAccount").textbox("setValue", obj.bankAccount);

				} else {
					$.messager.alert('提示','未获取到合同信息!','warning');
					return;
				}
			}
		});
	},
	splitString(value) {
		try {
			// 以斜杠为分隔符将字符串分割成数组
			const parts = value.split('/');
			// 最后一个元素作为单独的字符串
			const lastPart = parts.pop();
			// 其余部分重新合并为一个字符串
			const remainingParts = parts.join('/');

			return {
				remainingParts: remainingParts,
				lastPart: lastPart
			};
		}catch (e){
			return {
				remainingParts: "",
				lastPart: ""
			};
		}
	}
		
}

//获取当前时间
function myformatter(date){  
    var y = date.getFullYear();  
    var m = date.getMonth()+1;  
    var d = date.getDate();  
    return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);  
}

function CommissionRate(businessCommissionRate,
						buyCommissionRate,
						sellCommissionRate,
						marketingRetentionRate,
						companyCommissionRate,
						productCd) {
	this.businessCommissionRate = businessCommissionRate;
	this.buyCommissionRate = buyCommissionRate;
	this.sellCommissionRate = sellCommissionRate;
	this.marketingRetentionRate = marketingRetentionRate;
	this.companyCommissionRate = companyCommissionRate;
	this.productCd = productCd;
}
