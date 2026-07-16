var protocolDoc_ckh={
	addDeliveryTab:function(title, deleteFlg, id, logisticsCount) {
		return new Promise(function(resolve, reject) {
			var url = _ctx + "/ctr/logistics/addLogisticsDeliveryDetail";
			var _curNumber = number;
			number = number + 1;
	
			$.post(url, { curNumber: _curNumber, logisticsId: id, logisticsCount: logisticsCount }, function(html) {
				$('#easyui-tabs').tabs('add', {
					title: title,
					content: html,
					closable: deleteFlg
				});
				var deliveryId = $("#deliveryId" + _curNumber).val();
				if (!id) {
					id = 0;
				}
				if (!deliveryId) {
					deliveryId = 0;
				}

				protocolDoc_ckh.initLoadEasyui(_curNumber, id, deliveryId);
				$('.easyui-linkbutton').linkbutton();
	
				resolve(); // 解析 Promise
			});
		});
	},
	 addDeliveryTabOld:async function(title,deleteFlg,id,logisticsCount){
		var _curNumber = 1;
		var url=_ctx+"/ctr/logistics/addLogisticsDeliveryDetail";
		_curNumber = number;
		number = number+1;
		$.post(url,{curNumber:_curNumber,logisticsId:id,logisticsCount:logisticsCount},function(html){
			$('#easyui-tabs').tabs('add',{
				title:title,
				content:html,
				closable:deleteFlg
			});
			var deliveryId = $("#deliveryId"+_curNumber).val();
			if(!id) {
				id = 0;
			}
			if(!deliveryId){
				deliveryId = 0;
			}
			
			//加载html的组件
			protocolDoc_ckh.initLoadEasyui(_curNumber,id,deliveryId);
			// protocolDoc_ckh.initLoadEasyui(_curNumber);
			$('.easyui-linkbutton').linkbutton();
			
			
			
		});
	},
	//统一加载 easyui 组件
	initLoadEasyui:async function(randomNum,logisticsId,logisticsDeliveryId){
		//加载 datagrid
		var _url= _ctx+"/ctr/logistics/findAllLogisticsDriverLoading?logisticsId="+logisticsId+"&logisticsDeliveryId="+logisticsDeliveryId;
		var operatorClass = 'operator' + randomNum;
		var _curColumns = [ [
			{field:'id',title:'id',  width:100,hidden:'true'},
			{field:'logisticsNumber',title:'数量(吨)',  width:100,
				editor: { 
				type: 'numberbox', 
					options: { 
						required: true,
						validType: 'length[1,20]',
						precision: 4
					}
				},
				formatter:formateNum
			},
			{field:'plateNumber',title:'车牌号', width:100,editor: { type: 'textbox', options: { required: true}}},
			{field:'contactPhone',title:'联系电话', width:100,editor: { type: 'textbox', options: { required: true}}},
			{field:'driverName',title:'司机姓名', width:100,editor: { type: 'textbox', options: { required: true}}},
			{field:'driverCardNo',title:'司机身份证', width:100,editor: { type: 'textbox', options: { required: true}}},
			{
				field: 'option' + randomNum,
				title: '',
				width: 80,
				hidden: false,
				align: 'center',
				formatter: function (value, row, index) {
					var str = '';
					if (index == 0) {
						str = '<i class="icon-add icon cursor '+operatorClass+' mt4" style="margin-right: 26px;" onclick="protocolDoc_ckh.insertLogisticsDeliveryRow(\'' + randomNum + '\')"></i>'
					} else {
						str = '<i class="icon-add icon cursor '+operatorClass+' mt4" onclick="protocolDoc_ckh.insertLogisticsDeliveryRow(\'' + randomNum + '\')"></i>' +
							'<i class="icon-remove ml10 icon cursor mt4" onclick="protocolDoc_ckh.removeLogisticsDeliveryRow(\'' + row.id + '\',\'' + index + '\',event,\'' + randomNum + '\')"></i>'
					}
					return str;
				}
			}
		]]
		
		await $('#relaTable'+randomNum).datagrid({ 
			pageSize:10,
			rownumbers:true, 
			url:_url,
			pagination:false,
			columns:_curColumns,
			onDblClickCell:function(index,field){
				$('#relaTable'+randomNum).datagrid('beginEdit', index);
				if(id){
					var logisticsNumberEditor = $("#relaTable" + randomNum).datagrid("getEditor", { index: index, field: 'logisticsNumber' });
					if (logisticsNumberEditor) {
						logisticsNumberEditor.target.numberbox({
							onChange: function(newValue, oldValue) {
								var row = $("#relaTable" + randomNum).datagrid("getRows")[index];
								row.logisticsNumber = newValue;
								$("#relaTable" + randomNum).datagrid("updateRow", {
									index: index,
									row: row
								});
							}
						});
					}
					var plateNumberEditor = $("#relaTable" + randomNum).datagrid("getEditor", { index: index, field: 'plateNumber' });
					if (plateNumberEditor) {
						plateNumberEditor.target.textbox({
							onChange: function(newValue, oldValue) {
								var row = $("#relaTable" + randomNum).datagrid("getRows")[index];
								row.plateNumber = newValue;
								$("#relaTable" + randomNum).datagrid("updateRow", {
									index: index,
									row: row
								});
							}
						});
					}
					var contactPhoneEditor = $("#relaTable" + randomNum).datagrid("getEditor", { index: index, field: 'contactPhone' });
					if (contactPhoneEditor) {
						contactPhoneEditor.target.textbox({
							onChange: function(newValue, oldValue) {
								var row = $("#relaTable" + randomNum).datagrid("getRows")[index];
								row.contactPhone = newValue;
								$("#relaTable" + randomNum).datagrid("updateRow", {
									index: index,
									row: row
								});
							}
						});
					}
					var driverNameEditor = $("#relaTable" + randomNum).datagrid("getEditor", { index: index, field: 'driverName' });
					if (driverNameEditor) {
						driverNameEditor.target.textbox({
							onChange: function(newValue, oldValue) {
								var row = $("#relaTable" + randomNum).datagrid("getRows")[index];
								row.driverName = newValue;
								$("#relaTable" + randomNum).datagrid("updateRow", {
									index: index,
									row: row
								});
							}
						});
					}
					var driverCardNoEditor = $("#relaTable" + randomNum).datagrid("getEditor", { index: index, field: 'driverCardNo' });
					if (driverCardNoEditor) {
						driverCardNoEditor.target.textbox({
							onChange: function(newValue, oldValue) {
								var row = $("#relaTable" + randomNum).datagrid("getRows")[index];
								row.driverCardNo = newValue;
								$("#relaTable" + randomNum).datagrid("updateRow", {
									index: index,
									row: row
								});
							}
						});
					}
					
				}
			},
			// onClickCell:function(index,field){
			// 	debugger;
			// 	_logistics_last_index = index;
			// 	$(this).datagrid('beginEdit', index);
			// 	if(field === '') {
			//		
			// 	}
			// },
			onLoadSuccess: function(data) {
				// protocolDoc_ckh.insertLogisticsDeliveryRow(randomNum)
				// 数据加载完成后的处理逻辑
				if(data.rows.length===0) {
					protocolDoc_ckh.insertLogisticsDeliveryRow(randomNum)
				} else {
					var operator = '.operator' + randomNum;
					var operators = $(operator)
					for (var i = 0; i < operators.length; i++) {
						if (i !== operators.length - 1) {
							$(operators[i]).removeClass('icon-add').addClass('icon-add-no').attr('onClick', '');
						}
					}
				}
			},
			onAfterEdit: function(index, row, changes) {
				$('#relaTable' + randomNum).datagrid('endEdit', index);
			},
			// onBeginEdit: function (index, row) {
			// 	var fieldName = 'columnName'; // 将 'columnName' 替换为你要监听的列的字段名
			// 	var editor = $(this).datagrid('getEditor', { index: index, field: fieldName });
			//
			// 	if (editor) {
			// 		var target = editor.target;
			// 		$(target).on('input propertychange', function () {
			// 			var updatedValue = $(this).val();
			//
			// 			// 处理实时变化的值
			// 			// 这里可以执行你需要的操作，比如更新其他字段或调用其他函数
			// 			console.log(updatedValue);
			// 			// 其他操作...
			// 		});
			// 	}
			// }
		});

		$("#logisticsNumber"+randomNum).numberbox({
			required: true,
			min:0,
			precision:4
		});
		$("#transportAmount"+randomNum).numberbox({
			required: requiredFlg,
			min:0,
			precision:2
		});
		$("#stevedorage"+randomNum).numberbox({
			required: requiredFlg,
			min:0,
			precision:2
		});
		$("#deliveryOutFee"+randomNum).numberbox({
			required: requiredFlg,
			min:0,
			precision:2
		});
		$("#otherFee"+randomNum).numberbox({
			required: requiredFlg,
			min:0,
			precision:2
		});
		$("#trainNum"+randomNum).numberbox({
			required: true,
			min:0,
			precision:0
		});
		$("#phoneProtect"+randomNum).textbox({
			required: true,
		});
		$("#masterPorter"+randomNum).textbox({
			required: requiredFlg,
		});
		$("#masterPhone"+randomNum).textbox({
			required: requiredFlg,
		});
		//日期
		$("#logisticsDate"+randomNum).datebox({
			height:30,
			editable:false,
			required: true
		})
		//实际提货时间:
		$("#realDeliveryDate"+randomNum).datebox({
			height:30,
			editable:false,
			required: true
		})
		//实际到货时间
		$("#realArrivalDate"+randomNum).datebox({
			height:30,
			editable:false,
			required: true
		})


		$('#easyui-tabs').tabs({
			onClose: function(title, index) {
				var elements = $(".logisticsCount");
				elements.each(function(index) {
					var elementInput = $(this);
					var elementValue = elementInput.val()
					var indexValue = index+1;
					if(indexValue < elementValue ) {
						elementInput.val(elementValue-1);
					}
					
				});
			}
		});
		// 选中最新添加的选项卡项
		var tabs = $('#easyui-tabs').tabs('tabs');
		var lastIndex = tabs.length - 1;
		$('#easyui-tabs').tabs('select', lastIndex);

		// 重新设置提货次数
		var elements = $(".logisticsCount");
		elements.each(function(index) {
			var elementInput = $(this);
			if(lastIndex === index ) {
				elementInput.val(index+1);
			}

		});
	},
	insertLogisticsDeliveryRow: function (randomNum) {
		var _$table = $('#relaTable'+ randomNum);
		var changeRows = _$table.datagrid('getRows');
		if (changeRows.length > 9) {
			$.messager.alert('提示', '最多只能添加10条数据！', 'info');
			return;
		}
		if (!_logistics_last_index) {
			_logistics_last_index = 1;
		}else{
			_logistics_last_index = _logistics_last_index + 1;
		}
		var operator = '.operator' + randomNum;
		var operators = $(operator)
		for (var i = 0; i < operators.length; i++) {
			$(operators[i]).removeClass('icon-add').addClass('icon-add-no').attr('onClick', '');
			if ($(operators[i]).next()) {
				$(operators[i]).next().removeClass('icon-remove').attr('onClick', '');
			}
		}
		_$table.datagrid('endEdit', _logistics_last_index );
		_$table.datagrid('selectRow', _logistics_last_index);
		var field = {
			id: 0, 
			serialNumber: changeRows.length + 1,
			logisticsNumber: '',
			plateNumber: '',
			contactPhone: '',
			driverName: '',
			driverCardNo: '',
		};
		_$table.datagrid('appendRow', field);
		_logistics_last_index = _$table.datagrid('getRows').length - 1;
		_$table.datagrid('beginEdit', _logistics_last_index);
		Convert.focusEditor('relaTable'+ randomNum, field, _logistics_last_index);
	},
	removeLogisticsDeliveryRow: function (id, _index, event, randomNum) {
		var _$table = $('#relaTable'+ randomNum);
		var lastIndex = _$table.datagrid('getRows').length - 1;
		if (_index != lastIndex)
			return;
		var row, index;
		if (id == '0' || id == '' || id == undefined) {
			_$table.datagrid('deleteRow', _index);
		} else {
			_$table.datagrid('selectRecord', id);
			row = _$table.datagrid('getSelected');
			index = _$table.datagrid('getRowIndex', row);
			$.messager.confirm('提示', '确认要删除该记录吗?', function (t) {
				if (t) {
					_$table.datagrid('deleteRow', index);
					var nextSelect = index > 0 ? index - 1 : 0;
					_$table.datagrid('selectRow', nextSelect);
				}
			});
		}
		var operators = $('.operator'+randomNum);
		$(operators[operators.length - 1]).removeClass('icon-add-no').addClass('icon-add').attr('onClick', 'protocolDoc_ckh.insertLogisticsDeliveryRow(\''+randomNum+'\')');
		if (operators.length > 1) {
			$(operators[operators.length - 1]).next().addClass('icon-remove').attr('onClick', 'protocolDoc_ckh.removeLogisticsDeliveryRow(\'' + 0 + '\',\'' + (_index - 1) + '\',event,\''+randomNum+'\')');
		}
		event.stopPropagation();
	}
	
}
//获取当前时间
function myformatter(date){  
    var y = date.getFullYear();  
    var m = date.getMonth()+1;  
    var d = date.getDate();  
    return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);  
}

function logisticsInfo(randomNumber){
	debugger;
	$('.logisticsTapA').css("background","#cccccc");
	$('.logisticsTapA').css("box-shadow","0 2px 6px 0 #cccccc");

	$('#logisticsA'+randomNumber).css("background","#43b77f");
	$('#logisticsA'+randomNumber).css("box-shadow","0 2px 6px 0 #43b77f");
	$('.deliveryDivShowFlg').hide();
	$('#deliveryDivId'+randomNumber).show();
	nowNumber = randomNumber;
	// $('#relaTable'+ randomNumber).datagrid('reload');
}
function formateNum(num,row,index) {
	if (num){
		num = parseFloat(num).toFixed(4).toString().split(".");
		num[0] = num[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)','ig'),"$1,");
		return num.join(".");
	}else{
		return num;
	}
}