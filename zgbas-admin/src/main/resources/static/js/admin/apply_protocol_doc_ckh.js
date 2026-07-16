var applyProtocolDoc_ckh={
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
					applyProtocolDoc_ckh.initLoadEasyui(_curNumber);
					number1 = number1+1;
				} else {
					for (var i = 0; i < _detailSize; i++) {
						applyProtocolDoc_ckh.initLoadEasyui(i);
						applyProtocolDoc_ckh.initLoadEasyui(number1);
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
			$("#contractNo"+randomNum).textbox({
				required:true
			});
			$("#brandNumber"+randomNum).combobox({
				data:"",
				required:true,
				limitToList:true,
				editable:true,
				textField:'brandNumber',
				valueField:'brandNumber'
			});
			$("#productCd"+randomNum).combotree({
				data:_productTypeJson,
				required:true,
				editable:true,
				panelWidth:150,
				panelHeight:300,
				onSelect:function(node){
					if (node.children.length!=0){
						$("#productCd"+randomNum).combotree('clear');
						$("#productName"+randomNum).val('');
						return;
					}
					var text = node.text;
					$("#productName"+randomNum).val(text);
					var new_brandJson = _brandJson.filter(function (e) {
						return e.productCd == node.id && e.enterpriseId == 44;
					})
					//初始化区域
					$("#brandNumber"+randomNum).combobox({
						data:new_brandJson,
						required:true,
						limitToList:true,
						editable:true,
						textField:'brandNumber',
						valueField:'brandNumber'
					});
				},
				onLoadSuccess: function (node) {
					// 获取当前选中的节点
					var selectedNode = $("#productCd" + randomNum).combotree('tree').tree('getSelected');
					if (selectedNode) {
						// 手动触发 onSelect 方法
						$("#productCd" + randomNum).combotree('options').onSelect.call(this, selectedNode);
					}

				}
			});
			$("#payFullDate"+randomNum).datebox({
				height:30,
				required:true,
				editable:false,
			});
			$("#totalNumber"+randomNum).numberbox({
				required:true,
				precision: 3,
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
						val = parseFloat(val).toFixed(3).toString().split(".");
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
			$("#breachDays"+randomNum).numberbox({
				required:true,
				precision: 0,
			})
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
			$("#dealedAmount"+randomNum).numberbox({
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
			$("#unPayOverdueAmount"+randomNum).numberbox({
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
			$("#breachAmount"+randomNum).numberbox({
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
			if(entityStatus=="N"|| entityStatus=="B"|| entityStatus=="C"){


			}else{
				//禁用所有输入框
				$(".control").attr("readOnly","true");
				//合同类型
				$("#contractNo"+randomNum).textbox('readonly', true);
				$("#brandNumber"+randomNum).combobox('readonly', true);
				$("#productCd"+randomNum).combotree('readonly', true);
				$("#payFullDate"+randomNum).datebox('readonly', true);
				$("#totalAmount"+randomNum).numberbox('readonly', true);
				$("#totalNumber"+randomNum).numberbox('readonly', true);
				$("#breachDays"+randomNum).numberbox('readonly', true);
				$("#dealedAmount"+randomNum).numberbox('readonly', true);
				$("#unPayOverdueAmount"+randomNum).numberbox('readonly', true);
				$("#breachAmount"+randomNum).numberbox('readonly', true);

			}
			
		},
	syncContractDetail(number) {
		var _contractNo = $("#contractNo"+number).val();
		if (!_contractNo) {
			$.messager.alert('提示','合同编号不可为空!','warning');
			return;
		}
		$.ajax({
			url: _ctx + "/apply/protocolDocument/findDetailByContractNo?contractNo=" + _contractNo + "&endDate=",
			type: "post",
			async: false,
			success: function (obj) {
				if (obj != null && obj != undefined) {
					$("#productCd"+number).combotree({
						data:_productTypeJson,
						required:true,
						editable:true,
						panelWidth:150,
						panelHeight:300,
						onSelect:function(node){
							if (node.children.length!=0){
								$("#productCd"+number).combotree('clear');
								$("#productName"+number).val('');
								return;
							}
							var text = node.text;
							$("#productName"+number).val(text);
							var new_brandJson = _brandJson.filter(function (e) {
								return e.productCd == node.id && e.enterpriseId == 44;
							})
							//初始化区域
							$("#brandNumber"+number).combobox({
								data:new_brandJson,
								required:true,
								limitToList:true,
								editable:true,
								textField:'brandNumber',
								valueField:'brandNumber'
							});
						},
						onLoadSuccess: function (node) {
							$("#productCd"+number).combotree("setValue", obj.productCd);
							// 获取当前选中的节点
							var selectedNode = $("#productCd" + number).combotree('tree').tree('getSelected');
							if (selectedNode) {
								// 手动触发 onSelect 方法
								$("#productCd" + number).combotree('options').onSelect.call(this, selectedNode);
							}

						}
					});

					$("#productName"+number).val(obj.productName);
					$("#brandNumber"+number).combobox("setValue",obj.brandNumber);
					$("#totalNumber"+number).numberbox("setValue", obj.totalNumber);
					$("#payFullDate"+number).datebox("setValue", obj.appointPayFullTime);
					$("#totalAmount"+number).numberbox("setValue", obj.totalAmount);
					$("#dealedAmount"+number).numberbox("setValue", obj.dealedAmount);
					$("#unPayOverdueAmount"+number).numberbox("setValue", obj.unPayOverdueAmount);
					$("#breachAmount"+number).numberbox("setValue", obj.breachAmount);
					$("#breachDays"+number).numberbox("setValue", obj.breachDays);

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
