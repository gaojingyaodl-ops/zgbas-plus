var applyMatch={
		//整个js初始化区域
		init:function(){
			this.initNode();
			this.bindEven();
			//重写验证方法
			$.extend($.fn.validatebox.defaults.rules, {
			    phoneNum: { //验证手机号
			        validator: function(value, param){
			         return /^1[3-8]+\d{9}$/.test(value);
			        },
			        message: '请输入正确的手机号码。'
			    },

			    telNum:{ //既验证手机号，又验证座机号
			      validator: function(value, param){
			          return /(^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$)|(^((\d3)|(\d{3}\-))?(1[358]\d{9})$)/.test(value);
			         },
			         message: '请输入正确的电话号码。'
			    }
			});

			//geiArr增加in_array属性方法
			Array.prototype.in_array=function(e){
				　//　var r=new RegExp(','+e+',');
				　//　return (r.test(','+this.join(this.S)+','));
					var testStr=','+arr.join(",")+",";
				　　return testStr.indexOf(","+val+",")!=-1;
			};
		},
		//节点区域
		initNode:function(){

		},
		//绑定区域
		bindEven:function(){

		},
		//增加 采购/销售 输入域
		addBuyOrSellInput:function(obj){
			var _type=$(obj).attr("type");
			var _tag=$(obj).attr("tag");
			var _curNumber = 0;
			var url="";
			if(_tag=="R"){
				url=_ctx+"/apply/import/addProductDetail";
			}else{
				url=_ctx+"/apply/match/addProductDetail";
			}
			if(_type=="B"){
				_curNumber = number1;
				number1 = number1+2;
			}else{
				_curNumber = number2;
				number2 = number2+2;
			}
			$.post(url,{contractType:_type,curNumber:_curNumber},function(html){
				//获得随机码
				//var randomNumber=$(html).find("#randomNumber").val();
				$("#s_parenDiv").find("fieldset").eq(0).after(html);
				//加载html的组件
				applyMatch.initLoadEasyui(_curNumber,_type);
				$('.easyui-linkbutton').linkbutton();
			});
		},
		//增加行
		insert:function(obj){
			var tableName=$(obj).attr("table");
			var changeRows = $('#'+tableName).datagrid('getRows');
			if(changeRows.length>9){
				$.messager.alert('提示','最多只能添加10条商品记录！','info');
				return;
			}
			var lastIndex;
			if(indexlist[tableName]!==undefined){
				lastIndex=indexlist[tableName];
			}else{
				lastIndex=indexlist[tableName]=0;
			}
			$('#'+tableName).datagrid('endEdit', lastIndex);
			var field={id:0,point:this.getRandom(3)};
			$('#'+tableName).datagrid('appendRow',field);
			lastIndex = $('#'+tableName).datagrid('getRows').length-1;
			$('#'+tableName).datagrid('selectRow', lastIndex);
			$('#'+tableName).datagrid('beginEdit', lastIndex);
			indexlist[tableName]=lastIndex;
		},
		//删除行
		remove:function(obj,id){
			//获得datagird id
			var _table=$(obj).attr("table");
			var _datagrid=$("#"+_table).datagrid('getRows');
			if(_datagrid.length<=1){
				return false;
			}
			var row , index;
			index = getRowIndex(obj);
			if(id=='0' ||id =='' || id == undefined){
				//var row = $('#'+_table).datagrid('getSelected');
				//var index = $('#'+_table).datagrid('getRowIndex', row);
				$('#'+_table).datagrid('deleteRow',index);
			}else{
				//$('#'+_table).datagrid('selectRecord',id);
				//row = $('#'+_table).datagrid('getSelected');
				//index = $('#'+_table).datagrid('getRowIndex', row);
				$.messager.confirm('提示','确认要删除该记录吗?',function(t){
					if(t){
						$('#'+_table).datagrid('deleteRow', index);
						var nextSelect=index>0?index-1:0;
						$('#'+_table).datagrid('selectRow', nextSelect);
						indexlist[_table]=nextSelect;
					}
				});
			}
			//查询销售的数据，若有数据，则需要同步
			var _sellDataGrid = $('#relaTable'+number2).datagrid('getRows');
			if(_sellDataGrid.length>1){
				applyMatch.deleteSell(row);
			}
			//选中
			/*$('#'+_table).datagrid('selectRecord',id);
			var row = $('#'+_table).datagrid('getSelected');
				if(row){
					var index = $('#'+_table).datagrid('getRowIndex', row);

					if (id=='0' ||id=='' || id=="undefined" || id==undefined){
						$('#'+_table).datagrid('deleteRow',index);
					}else{
						$.messager.confirm('提示','确认要删除该记录吗?',function(t){
							if(t){
								$('#'+_table).datagrid('deleteRow', index);
								var nextSelect=index>0?index-1:0;
								$('#'+_table).datagrid('selectRow', nextSelect);
								indexlist[_table]=nextSelect;
							}
						});
					}
					//查询销售的数据，若有数据，则需要同步
					var _sellDataGrid = $('#relaTable'+number2).datagrid('getRows');
					if(_sellDataGrid.length>1){
						applyMatch.deleteSell(row);
					}

				}*/
		},
		//生成指定随机数
		getRandom:function(n){
			var t='';
			for(var i=0;i<n;i++){
			t+=Math.floor(Math.random()*10);
			}
			return t;
		},
		deleteSell:function(row){
			var num = number2-2;
			var _sellDataGrid = $('#relaTable'+num).datagrid('getRows');
			var _productCd = row.productCd;
			var _dealNumber = row.dealNumber;
			var _factoryId = row.factoryId;
			var _brandNumber = row.brandNumber;
			var _warehouseName = row.warehouseName;
			for(var i=0;i<_sellDataGrid.length;i++){
				var _curProductCd = _sellDataGrid[i].productCd;
				var _curDealNumber = _sellDataGrid[i].dealNumber;
				var _curFactoryId = _sellDataGrid[i].factoryId;
				var _curBrandNumber = _sellDataGrid[i].brandNumber;
				var _curWarehouseName = _sellDataGrid[i].warehouseName;
				if(_productCd==_curProductCd&&_dealNumber==_curDealNumber&&_factoryId==_curFactoryId
						&&_brandNumber==_curBrandNumber&&_warehouseName==_curWarehouseName){
					var index = $('#relaTable'+num).datagrid('getRowIndex', _sellDataGrid[i]);
					$('#relaTable'+num).datagrid('deleteRow',index);
					break;
				}
			}


		},
		//计算总价
		curOper:function(curIndex,row,table){
			var _datagrid = $('#'+table);
			var _ed_dealPrice = _datagrid.datagrid('getEditor', {index:curIndex,field:'dealPrice'});
			var _ed_dealNumber = _datagrid.datagrid('getEditor', {index:curIndex,field:'dealNumber'});
			var _ed_dealAmount = _datagrid.datagrid('getEditor', {index:curIndex,field:'totalPrice'});
			var sumAmount=function(){
				var _dealNumber=$(_ed_dealNumber.target).numberbox('textbox').val();
				var _dealPrice= $(_ed_dealPrice.target).numberbox('textbox').val();
				if(_dealPrice &&_dealNumber){
					$(_ed_dealAmount.target).numberbox('setValue', _dealPrice*_dealNumber);
				}
				//计算差价 和毛利润
				applyMatch.curGrossProfitAndDiffPrice();
			}
			$(_ed_dealPrice.target).numberbox('textbox').on('keyup',sumAmount);
			$(_ed_dealNumber.target).numberbox('textbox').on('keyup',sumAmount);
			$(_ed_dealPrice.target).numberbox('textbox').on('blur',sumAmount);
			$(_ed_dealNumber.target).numberbox('textbox').on('blur',sumAmount);

			$(_ed_dealAmount.target).numberbox('textbox').on("keyup",function(){
				var _dealAmount = $(this).val();
				$(_ed_dealAmount.target).numberbox('setValue', _dealAmount);
			});
		},
		removeNode:function(obj){
			var _obj=$(obj);
			var _tbl=$(".relaTable");
			var _len=_tbl.length;
			var flag=$(obj).hasClass("detail");
			if(_len>2){
				var id=_obj.attr("detailId");
				if(id){
					$.messager.confirm('提示','确认要删除该记录吗?',function(t){
						if(t){
							var tableName=$("#relaTable"+id);
							//先同步销售数据
							applyMatch.syncBuyData(tableName);
							if(flag){	//追加到删arr
								removelist.push(id);
//								console.log("removeArr:"+removelist);
							}
							$(obj).parent().parent().remove();
							//重新计算 数量 计算差价 和毛利润
							applyMatch.curGrossProfitAndDiffPrice();
						}
						return ;
					});
				}else{
					$(obj).parent().parent().remove();
				}
			}
		},//当删除整个采购时 需要同步前去掉 销售中该该采购的数据
		syncBuyData:function(table){
			var _row=$(table).datagrid('getRows');
			for(var i=0;i<_row.length;i++){
				//比较销售数据 去除删掉的数据
				applyMatch.deleteSell(_row[i]);
			}
		},
		//比较货品名称
		eqSet:function(as, bs) {
		    for (var a in as) if (!bs.in_array(a)) return false;
		    for (var a in bs) if (!as.in_array(a)) return false;
		    return true;
		},
		//统计所有datagrid 数据
		curGrossProfitAndDiffPrice:function(){
			//获得所有的datagrid
			var _tab=$(".relaTable");
			var _len=_tab.length;
			var buyNumber=0,sellNumber=0,buyAmount=0,sellAmount=0,buyPrice=0,sellPrice=0;
			for(var j=0;j<_len;j++){
				//获取table id
				var _datagridId = $(_tab[j]).attr("id");
				//获取table atr
				var _atr = $(_tab[j]).attr("atr");
				//获取table pointer
				var _pointer = $(_tab[j]).attr("pointer");
				//获取该datagrid所有行
				var _datagirData = $("#"+_datagridId).datagrid('getRows');
				var length = _datagirData.length;
				var $datagrid=$("#"+_datagridId);
				//循环遍历
				var amount = 0;
				for(var i=0;i<length;i++){
					var deal_price = _datagirData[i].dealPrice;
					var deal_number = _datagirData[i].dealNumber;
					var total_price = _datagirData[i].totalPrice;

					//采购
					if ("B"==_atr){
						buyNumber += Number(deal_number);
						buyAmount += Number(total_price);
						buyPrice += Number(deal_price);
					//销售
					}else{
						//单价
						var price = $datagrid.datagrid("getEditor",{index:i,field:"dealPrice"});
						//总价
						var toPrice =$datagrid.datagrid("getEditor",{index:i,field:"totalPrice"});
						//数量
						var number =$datagrid.datagrid("getEditor",{index:i,field:"dealNumber"});

						if (price){
							deal_price=$(price.target).numberbox('getValue');
							total_price=$(toPrice.target).numberbox('getValue');
							deal_number=$(number.target).numberbox('getValue');
						}

						sellNumber += Number(deal_number);
						sellAmount += Number(total_price);
						sellPrice += Number(deal_price);
					}
				}
			}
			$("#buyTotalNumber").val(buyNumber);
			$("#sellTotalNumber").val(sellNumber);
			var _grossProfit = Number(sellAmount) - Number(buyAmount);
			var _differPrice = Number(sellPrice) - Number(buyPrice);
			$("#grossProfit").val(Number(_grossProfit)>0?_grossProfit:0);
			$("#differPrice").val(Number(_differPrice)>0?_differPrice:0);
			getCostProfit();
		},
		batchInsertSell:function(dom){
			var _tableName = "relaTable";
			var _allTable = $(".relaTable");
			var _nexTable = 1;
			var sellArr = [];
			var flg;
			for(var k=0;k<_allTable.length;k++){
				var atr=$(_allTable[k]).attr("atr");
				var id =$(_allTable[k]).attr("id");
				if("S"==atr){
					sellArr.push(id);
				}
			}
			if (sellArr.length>1){
				flg = true;
			}else{
				flg = false;
				var _sellIndex = 0;
			}
			for(var j=0;j<_allTable.length-1;j++){
				var atr=$(_allTable[j]).attr("atr");
				if("B"!=atr){
					break;
				}
				var dataGrid=$(_allTable[j]).attr("id");

				var _curTable =$("#"+dataGrid);
					//获取当前table的所有行
					var curAllRows = $("#"+dataGrid).datagrid('getRows');
					for(var i=0;i<curAllRows.length;i++){
						var rowIndex=$("#"+dataGrid).datagrid('getRowIndex',curAllRows[i]);
						flag=$("#"+dataGrid).datagrid('validateRow',rowIndex);
						if (!flag){
							$("#"+dataGrid).datagrid('selectRow',rowIndex);
							$("#"+dataGrid).datagrid('beginEdit', rowIndex);
							break;
						}else{
							var ed_warehouseName = $("#"+dataGrid).datagrid('getEditor', {index:i,field:'warehouseName'});
							if(ed_warehouseName){
								var warehouseName = $(ed_warehouseName.target).combobox('textbox').val();
								$(ed_warehouseName.target).combobox('setValue',warehouseName);
							}
							$("#"+dataGrid).datagrid('endEdit', rowIndex);
						}
					}
					for(var h=0;h<sellArr.length;h++){
						if (flg){
							var _sellIndex = 0;
						}
						var rela_table = "#"+sellArr[h];
						var _nexAllRows = $(rela_table).datagrid('getRows');
						var _nextLength = _nexAllRows.length;
						for(var i=0;i<curAllRows.length;i++){
							var _productCd = curAllRows[i].productCd;
							var _productName = curAllRows[i].productName;
							var _brandNumber = curAllRows[i].brandNumber;
							var _factoryId = curAllRows[i].factoryId;
							var _factoryName = curAllRows[i].factoryName;
							var _warehouseName = curAllRows[i].warehouseName;
							var _dealNumber = curAllRows[i].dealNumber;
							var _warehousePos = curAllRows[i].warehousePos;
							var _wrapSpecs = curAllRows[i].wrapSpecs;
							var _warehousePrice = curAllRows[i].warehousePrice;
							var jg=true;
							var _point=curAllRows[i].point;
							var _oper = 'updateRow';
							if(_sellIndex>=_nextLength){
								//如果无值，则新增
								_oper = 'insertRow';
							}else{
								//修改时 需要记录
								var buyRows=$(rela_table).datagrid("getRows");
								var buyRow=buyRows[_sellIndex];
								var buyPoint=buyRow.point;
								var _dealPrice,_totalPrice;
								var dealPrice = $(rela_table).datagrid("getEditor",{index:_sellIndex,field:"dealPrice"});
								var totalPrice =$(rela_table).datagrid("getEditor",{index:_sellIndex,field:"totalPrice"});
								if(dealPrice){
									  _dealPrice=$(dealPrice.target).numberbox('getValue');
									  _totalPrice=$(totalPrice.target).numberbox('getValue');
								}else{
									  _dealPrice=buyRow.dealPrice;
									  _totalPrice=buyRow.totalPrice;
								 }

								//不是当前行时 记录编辑的单价跟总价
								if(_point!=buyPoint&&_dealPrice!=""&&_totalPrice!=""){
									var row={dealPrice:_dealPrice,totalPrice:_totalPrice};
									buyPricelist[buyPoint]=row;
									jg=false;
								}
							}

							var row={
								productCd: _productCd,
								productName:_productName,
								brandNumber: _brandNumber,
								factoryId: _factoryId,
								factoryName:_factoryName,
								warehouseName:_warehouseName,
								dealNumber:_dealNumber,
								warehousePos:_warehousePos,
								wrapSpecs:_wrapSpecs,
								warehousePrice:_warehousePrice,
								point:_point,
								dealPrice:'',
								totalPrice:''
							};
							//设置单价 总价
							if(buyPricelist[_point]!==undefined){
								var pointRow=buyPricelist[_point];
								row.dealPrice=pointRow.dealPrice;
								row.totalPrice=pointRow.totalPrice;
							}

							$(rela_table).datagrid(_oper,{
								index: _sellIndex,
								row:row
							});
							//打开编辑器
							$(rela_table).datagrid("beginEdit",_sellIndex);

							_sellIndex++;
					}

				}

			}
			//clear arr
			buyPricelist.splice(0,buyPricelist.length);

		},
		//统一加载 easyui 组件
		initLoadEasyui:function(randomNum,type,tag,applyType){
			//加载 datagrid
			var _url="";
			if(tag=="S"){
				 _url = _ctx+"/apply/productDetail/productList?search_EQL_applyId="+randomNum+"&search_EQS_applyType="+applyType;
			}
			var _curColumns;
			if("B"==type){
				_curColumns = [ [
					{field:'productCd',title:'货品名称',width:120,editor:{type:'combotree',options:{required:'true',editable:'false',panelWidth:150,panelHeight:300,data:_productAllJson}},
						formatter:function(value,row,index){
							return Convert.findValueByKey(_productChildrenJson,"typeCode",value,"typeName");
					}},
					{field:'brandNumber',title:'牌号',width:100,editor:{type:'combobox',options:{required:'true',panelWidth:180,textField:'brandNumber',valueField:'brandNumber'}}},
					{field:'factoryId',title:'厂商',width:120,editor:{type:'combobox',options:{required:'true',panelWidth:180,data:_factoryJson,textField:'factoryName',valueField:'id',onHidePanel:function(){BasCombobox.onHidePanel(this);}}},
						formatter:function(value,row,index){
							return Convert.findValueByKey(_factoryJson,"id",value,"factoryName");
					}},
					{field:'warehouseName',title:'仓库',width:150,editor:{type:'combobox',options:{required:true,valueField:'warehouseName',textField:'warehouseName'}}},
					{field:'warehousePos',title:'仓库所在地',align:'center',width:160,editor:{ type:'combotree',options:{required:'true',panelWidth:160,panelHeight:300,data:_bsAreaJson,onBeforeExpand:function(node){}}},
						formatter:function(value,row,index){
							return Convert.findTreeValueByKey(_bsAreaJson,"id",value,"text");
					}},
					{field:'warehousePrice',title:'仓储费/地区单价(元)', width:100,align:'right',hidden:true,editor:{type:'numberbox',options:{validType:'length[1,20]',precision:3}}},
					{field:'wrapSpecs',title:'包装规格',align:'center',width:120,editor:{type:'combobox',options:{required:true,editable:false,panelHeight:'auto',data:packingSpecificaJson,textField:'dictName',valueField:'dictCd',onHidePanel:function(){BasCombobox.onHidePanel(this);}}},
						formatter:function(value,row,index){
							return Convert.findValueByKey(packingSpecificaJson,"dictCd",value,"dictName");
					}},
					{field:'dealNumber',title:'数量(吨)', width:120,editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:4}},
						formatter: function (value, row, index) {
		                    if (row != null) {
		                        return parseFloat(value).toFixed(3);
		                    }
					}},
					{field:'dealPrice',title:'单价(元)', width:120,editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:2}}},
					{field:'totalPrice',title:'总价(元)', width:120,editor:{type:'numberbox',options:{required:'true',readonly:'true',validType:'length[1,20]',precision:2}}},
					{field:'option',title:'操作', width:120, hidden:true, align:'center',formatter:function(value, row, index){
						var str ="";
						/*
						if(randomNum%2==0){
						 str = str + "<img onclick=applyMatch.updateSellGrid('"+index+"',"+randomNum+") table='relaTable"+randomNum+"' style='cursor:pointer;width:16px;hight:18px' src='/static/images/icon_xiangqing.png'/>&nbsp;&nbsp;";
						}*/
						str = str + "<img onclick=applyMatch.remove(this,\""+row.id+"\") table='relaTable"+randomNum+"' style='cursor:pointer;width:16px;hight:18px' src='/static/images/icon_delete.png'/>";
				        return str;
					}}
				]]
			}else{
				_curColumns = [ [
					{field:'productCd',title:'货品名称',width:120,
						formatter:function(value,row,index){
							return Convert.findValueByKey(_productChildrenJson,"typeCode",value,"typeName");
					}},
					{field:'brandNumber',title:'牌号',width:100},
					{field:'factoryId',title:'厂商',width:120,
						formatter:function(value,row,index){
							return Convert.findValueByKey(_factoryJson,"id",value,"factoryName");
					}},
					{field:'warehouseName',title:'仓库',width:150},
					{field:'warehousePos',title:'仓库所在地', width:160,align:'left',
						formatter:function(value,row,index){
							return Convert.findTreeValueByKey(_bsAreaJson,"id",value,"text");
					}},
					{field:'warehousePrice',title:'仓储费/地区单价(元)', width:100,align:'right',hidden:true,editor:{type:'numberbox',options:{validType:'length[1,20]',precision:3}}},
					{field:'wrapSpecs',title:'包装规格',align:'center',width:120,
						formatter:function(value,row,index){
							return Convert.findTreeValueByKey(packingSpecificaJson,"dictCd",value,"dictName");
					}},
					{field:'dealNumber',title:'数量(吨)', width:120,editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:4}},
						formatter: function (value, row, index) {
		                    if (value != null) {
		                        return parseFloat(value).toFixed(3);
		                    }
					}},
					{field:'dealPrice',title:'单价(元)', width:120,editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:2}}},
					{field:'totalPrice',title:'总价(元)', width:120,editor:{type:'numberbox',options:{required:'true',readonly:'true',validType:'length[1,20]',precision:2}}},
					{field:'option',title:'操作', width:120, hidden:true, align:'center',formatter:function(value, row, index){
						var str ="";
						/*str = str + "<img onclick=applyMatch.remove(this,\""+row.id+"\") table='relaTable"+randomNum+"' style='cursor:pointer;width:16px;hight:18px' src='/static/images/icon_delete.png'/>";
				       */
						return str;
					}}
				]];
			}
			if (_industry.indexOf('SL')==-1){
				for(var i in _curColumns[0]){
					var col =_curColumns[0][i];
					if (col.field=='brandNumber' || col.field=='factoryId'){
						if (col.editor){
							col.editor.options.required = false;
						}
						col.hidden = true;
					}

				}
			}

			$('#relaTable'+randomNum).datagrid({
				pageSize:50,
				rownumbers:true,
				url:_url,
				pagination:false,
				columns:_curColumns,
				//toolbar:'#btn',
				onBeginEdit:function(index,row){
					if("B"==type){
						var ed_warehouseName = $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'warehouseName'});
						var ed_productCd = $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'productCd'});
						var ed_brandNumber = $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'brandNumber'});
						var ed_warehousePos = $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'warehousePos'});
							$(ed_productCd.target).combotree('textbox').on('blur',function(){
								var t = $(ed_productCd.target).combotree('tree');
								var _productSelect = t.tree('getSelected');
								if(!_productSelect) $(ed_productCd.target).combotree('clear');
							});

							//点击货品时 添加该货品名称
							$(ed_productCd.target).combotree('options').onClick = function(record){

								if(record.children.length!=0){
									$(ed_productCd.target).combotree('clear');
								}else{
									var productName = record.text;
									row.productName = productName;
									var productCode = record.id;

									$(ed_warehouseName.target).combobox('reload',_ctx+'/bas/warehouse/findWarehoseList/'+productCode);
									if (ed_brandNumber){

										$.post(_ctx+"/bas/brand/findBrand",{"productCode":productCode},function(data){

											//当为塑料产品时 显示 下拉数据  否则 默认第一个牌号
											if(productCode.indexOf("SL")>=0){
												//显示牌号
//												$('#relaTable'+randomNum).datagrid('showColumn', 'brandNumber');
												//点击之后清空
												$(ed_brandNumber.target).combobox("setValue","");
												//重新加载combobox
												$(ed_brandNumber.target).combobox('loadData',data);
											}else{
												//隐藏牌号
//												$('#relaTable'+randomNum).datagrid('hideColumn', 'brandNumber');
//												if (data.length>0){
//													$(ed_brandNumber.target).combobox('setValue',data[0].brandNumber);
//												}
											}


										});

									}
								}
							};
							var productCode = row.productCd;
							if (productCode){
								$(ed_warehouseName.target).combobox('reload',_ctx+'/bas/warehouse/findWarehoseList/'+productCode);
							}
							//点击厂商时 添加厂商名称
							var ed_factoryId = $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'factoryId'});
							$(ed_factoryId.target).combobox('options').onClick = function(record){
								var factoryName =record.factoryName;
								row.factoryName = factoryName;
							};
							$(ed_warehousePos.target).combotree('options').onClick = function(record){
								if(record.children.length!=0){
									$(ed_warehousePos.target).combotree('clear');
								}else{
									$(ed_warehousePos.target).combotree('setValue',record.id);
									//根据市区areacode获取单价
									$.post(_ctx+"/bs/areaCost/acquirePriceVo",{"areaCode":record.id},function(data){
										var rObj = eval(data);
										var _housePrice=rObj;
										var _area_housePrice =  $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'warehousePrice'});
										$(_area_housePrice.target).numberbox('setValue',rObj.warehouseUnitCost);
							 		});
								}
							}
					}

					//点击仓库时 添加改行 仓库名称
					/*var ed_warehouseId = $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'warehouseId'});
					$(ed_warehouseId.target).combobox('options').onClick = function(record){
						var warehouseName =record.warehouseName;
						row.factoryName = warehouseName;
					}; */
					//计算总价
					applyMatch.curOper(index,row,'relaTable'+randomNum);

				},onDblClickCell:function(index,field){
					var entityStatus=$("#entityStatus").val();
					if(entityStatus=="N"|| entityStatus=="B" || entityStatus=="C"){
						$('#relaTable'+randomNum).datagrid('endEdit', lastIndex);
						$('#relaTable'+randomNum).datagrid('beginEdit', index);
					}
				},onLoadSuccess:function(index,field){
					//非塑料隐藏牌号
					//获得所有行
//					var rows=$('#relaTable'+randomNum).datagrid("getRows");
//					var _len=rows.length;
//					for(var i=0;i<_len;i++){
//						var productCd=rows[i].productCd;
//						if(productCd){
//							if(productCd.indexOf("HG")>=0){
//								$('#relaTable'+randomNum).datagrid('hideColumn', 'brandNumber');
//							}
//						}
//					}
				}
			});
			var entityStatus=$("#entityStatus").val();
			if(entityStatus=="N"|| entityStatus=="B"|| entityStatus=="C"){
				//显示删除按钮
				$('#relaTable'+randomNum).datagrid("showColumn","option");
				//
				var rows=$('#relaTable'+randomNum).datagrid("getRows");
				if(rows.length==0){
					var lastIndex;
					if(indexlist["relaTable"+randomNum]!==undefined){
						lastIndex=indexlist["relaTable"+randomNum];
					}else{
						lastIndex=indexlist["relaTable"+randomNum]=0;
					}
					$('#relaTable'+randomNum).datagrid('endEdit', lastIndex);
					var field={id:0,point:this.getRandom(3)};
					$('#relaTable'+randomNum).datagrid('appendRow',field);
					lastIndex = $('#relaTable'+randomNum).datagrid('getRows').length-1;
					$('#relaTable'+randomNum).datagrid('selectRow', lastIndex);
					$('#relaTable'+randomNum).datagrid('beginEdit', lastIndex);
					indexlist["relaTable"+randomNum]=lastIndex;
				}

				//船期
				/*$("#shippingDate"+randomNum).datebox({
					height:30,
					required:false
				});
				//收款时间
				$("#arrivalTime"+randomNum).datebox({
					height:30,
					editable:false,
					onSelect:function(record){
					 	var mDate=$("#arrivalTime"+randomNum).datebox('getValue');
						var curr_time = new Date();
						var sDate=myformatter(curr_time);
						if(mDate>sDate){
							$("#contractAttr"+randomNum).combobox('setValue', "F");
						}else{
							$("#contractAttr"+randomNum).combobox('setValue',"N");
						}
					}
				});
				//付款时间
				$("#payFullTime"+randomNum+","+"#receiveBondTime"+randomNum+","+"#receiveFullTime"+randomNum).datebox({
					height:30,
					required:true,
					editable:false
				});
				$("#payBondTime"+randomNum).datebox({
					height:30,
					required:true,
					editable:false,
					onChange : function(newValue,oldValue){
						var dateFullTime = strDateForDate($('#payFullTime'+randomNum).datebox('getValue')).getTime();
						var dateFullTimeValue = $('#payFullTime'+randomNum).datebox('getValue');
						var dateBondTime = strDateForDate(newValue).getTime();
						$('#payFullTime'+randomNum).datebox().datebox('calendar').calendar({
							validator: function(date){
				                return strDateForDate(newValue) <=date;//<=
				            }
						 });
						 if(dateBondTime > dateFullTime){
							 $('#payFullTime'+randomNum).datebox('setValue', newValue);
						}  else {
							$('#payFullTime'+randomNum).datebox('setValue', dateFullTimeValue);
						}
					}

				});
				$("#receiveBondTime"+randomNum).datebox({
					height:30,
					required:true,
					editable:false,
					onChange : function(newValue,oldValue){
						var dateFullTime = strDateForDate($('#receiveFullTime'+randomNum).datebox('getValue')).getTime();
						var dateFullTimeValue = $('#receiveFullTime'+randomNum).datebox('getValue');
						var dateBondTime = strDateForDate(newValue).getTime();
						 $('#receiveFullTime'+randomNum).datebox().datebox('calendar').calendar({
								validator: function(date){
					                return strDateForDate(newValue)<=date;//<=
					            }
							 });
						 if(dateBondTime>dateFullTime){
							 $('#receiveFullTime'+randomNum).datebox('setValue', newValue);
						}  else {
							$('#receiveFullTime'+randomNum).datebox('setValue',dateFullTimeValue);
						}
					}
				});

				var _payTime ;
				if($("#payBondTime"+randomNum).length>0){
					_payTime = $("#payBondTime"+randomNum).datebox('getValue');
					var beginBondTime = strDateForDate(_payTime);
					$("#payFullTime"+randomNum).datebox('calendar').calendar({
						validator:function(date){
							return beginBondTime <= date;
						}
					});
				}else{
					_payTime = $("#receiveBondTime"+randomNum).datebox('getValue');
					var beginBondTime = strDateForDate(_payTime);
					$("#receiveFullTime"+randomNum).datebox('calendar').calendar({
						validator:function(date){
							return beginBondTime <= date;
						}
					});
				}
				if(!_payTime){
					var curr_time = new Date();
					$("#payBondTime"+randomNum+","+"#payFullTime"+randomNum+","+"#receiveFullTime"+randomNum+","+"#receiveBondTime"+randomNum).datebox('setValue',myformatter(curr_time));
				}*/
				//收款金额
				$("#receiveBondAmount"+randomNum).numberbox({
					 min:0,
					 precision:2,
					 height:30
				});
				//付款金额
				$("#payBondAmount"+randomNum).numberbox({
					height:30,
					 min:0,
					 precision:2
				});

				$("#payRate"+randomNum).numberbox({
					min:0,
					precision:4,
					height:30
				});
				//合同类型
				$("#contractType"+randomNum).combobox({
					data:_contractTypeJson,
					textField:'dictName',
					valueField:'dictCd',
					height:30,
					panelHeight:"auto",
					editable :false,
					hasDownArrow:false
				});
				//合同属性
				$("#contractAttr"+randomNum).combobox({
					data:_contractAttrJson,
					valueField:'dictCd',
					textField:'dictName',
					panelHeight:"auto",
					required: true,
					onLoadSuccess:function(){
						var _this = $(this);
						if(!_this.combobox('getValue')){
							_this.combobox("setValue","N");
							var curr_time = new Date();
							$("#arrivalTime"+randomNum).datebox('setValue',myformatter(curr_time));
						}
						var entityStatus=$("#entityStatus").val();
						if(entityStatus=="N"|| entityStatus=="B"|| entityStatus=="C"){
							_this.combo('readonly' ,false);
						}
					}
				});
				//付款方式
				$("#payType"+randomNum).combobox({
					data:_payTypeJson,
					textField:'dictName',
					valueField:'dictCd',
					panelHeight:"auto",
					onHidePanel:function(){
						BasCombobox.onHidePanel(this);
					}
				});
				//付款方式
				$("#receiveTypeAdd"+randomNum).combobox({
					data:_payTypeJson,
					textField:'dictName',
					valueField:'dictCd',
					panelHeight:"auto",
					editable:false,
					onHidePanel:function(){
						BasCombobox.onHidePanel(this);
					}
				});
				//供货商
				var _companyId = $("#companyIdAdd"+randomNum).val();
		        if(!_companyId){
		          _companyId = 0
		        }
				//供货商
				$("#companyIdAdd"+randomNum).combobox({
					url:_ctx+"/bs/company/listMyCompany/"+_companyId,
					mode:'remote',
					limitToList:true,
					textField:'text',
					valueField:'id',
					panelHeight:300,
					formatter: function(row){
						var text = row.text;
						var myFlag = row.myFlag;
						if (myFlag!=true){
							text = '<span style="color:#ccc">'+text+'</span>';
						}
						return text;
					},
					onSelect:function(record){
					    var _contactPhone = record.contactPhone;
					    var _address = record.address;
					    var _number = randomNum;
					    var _contactPhoneDom = $("#contactPhone"+_number);
					    if(_contactPhoneDom){
					    	_contactPhoneDom.val(_contactPhone);
					    }
					    var _contactAddrDom = $("#contactAddr"+_number);
					    if(_contactAddrDom){
					    	_contactAddrDom.val(_address);
					    }
					    var myFlag = record.myFlag;
						if (myFlag!=true){
							setTimeout(function () { $("#companyName").combobox("clear"); }, 1)
						}
					}
				});
				//销售-结算方式
				$('#deliveryMode'+randomNum).combobox({
					data:_deliveryModeJson,
					valueField:'dictCd',
					textField:'dictName',
					required: true,
					panelHeight:"auto",
					readonly:true,
					hasDownArrow:false,
					onLoadSuccess:function(){
						var _this = $(this);
						var _val = _this.val();
						if(""==_val){
							_this.combobox("setValue","XKHH");
						}else{
							_this.combobox("setValue",_val);
						}
					},
					onHidePanel:function(){
						BasCombobox.onHidePanel(this);
					}
				});
				//交货方式
				$("#deliveryType"+randomNum).combobox({
					data:_deliveryTypeJson,
					valueField:'dictCd',
					textField:'dictName',
					panelHeight:"auto",
					editable:false,
					onLoadSuccess:function(){
						var _this = $(this);
						var _val = _this.val();
						if(""==_val){
							_this.combobox("setValue","Z");
						}else{
							_this.combobox("setValue",_val);
						}
					}
				});
				//销售方式
				$("#deliveryModeAdd"+randomNum).combobox({
					data:_deliveryModeJson,
					valueField:'dictCd',
					textField:'dictName',
					panelHeight:"auto",
					onLoadSuccess:function(){
						var _this = $(this);
						var _val = _this.val();
						if(""==_val){
							_this.combobox("setValue","XKHH");
						}else{
							_this.combobox("setValue",_val);
						}
					}
				});
				//销售-结算方式
				$('#deliveryModeS'+randomNum).combobox({
					data:_deliveryModeJson,
					valueField:'dictCd',
					textField:'dictName',
					required: true,
					panelHeight:"auto",
					readonly:true,
					hasDownArrow:false,
					onLoadSuccess:function(){
						var _this = $(this);
						var _val = _this.val();
						if(""==_val){
							_this.combobox("setValue","XKHH");
						}else{
							_this.combobox("setValue",_val);
						}
					},
					onHidePanel:function(){
						BasCombobox.onHidePanel(this);
					}
				});
				//收款方式
				$("#receiveType"+randomNum).combobox({
					data:_payTypeJson,
					textField:'dictName',
					valueField:'dictCd',
					panelHeight:"auto"
				});
				//港口
				$("#port"+randomNum).textbox({
					height:30,
					required:false
				});

				$("#objectivePort"+randomNum).textbox({
					height:30,
					required:false
				});
				//合同编号
				$("#contractNo"+randomNum).textbox({
					height:30
				});
				$('#warehouseCost'+randomNum+',#transportCost'+randomNum+',#qingguanFee'+randomNum+',#kaizhengFee'+randomNum+',#chengduiFee'+randomNum).numberbox({
					min:0,
					precision:2,
					required:true,
					height:30,
					onChange:function(newValue,oldValue){
						getCostProfit();
					}
				});
				/*//仓储费
				$("#warehouseCost"+randomNum).numberbox({
					 min:0,
					 precision:3,
					 height:30,
					 required:true
				});
				//运输费
				$("#transportCost"+randomNum).numberbox({
					 min:0,
					 precision:3,
					 height:30
				});
				//清关费
				$("#qingguanFee"+randomNum).numberbox({
					min:0,
					precision:2,
					required:true,
					height:30
				});
				//开证手续费
				$("#kaizhengFee"+randomNum).numberbox({
					min:0,
					precision:2,
					required:true,
					height:30
				});
				//承兑费
				$("#chengduiFee"+randomNum).numberbox({
					min:0,
					precision:2,
					required:true,
					height:30
				});*/
				//代理费
				$("#dailiFee"+randomNum).numberbox({
					min:0,
					precision:2,
					required:true,
					height:30
				});
				/*if("S"==type){
					//运输费仓储费
					$('#warehouseCost'+randomNum).numberbox({
						min:0,
					    precision:3,
					    required: true,
					    onChange:function(newValue,oldValue){
							var costProfit = $('#grossProfit').val();
							var warehouseCost = getTotalTransportCost();
							var num = Number(costProfit) - Number(newValue) - Number(warehouseCost);
							$('#costProfit').val(num);
						}
					});
					$('#transportCost'+randomNum).numberbox({
						min:0,
					    precision:3,
					    required: true,
					    onChange:function(newValue,oldValue){
							var costProfit = $('#grossProfit').val();
							var transportCost = getTotalWarehouseCost();
							var num = Number(costProfit) - Number(newValue) - Number(transportCost);
							$('#costProfit').val(num);
						}
					});
				}*/
				//联系电话
				/*$("#contactPhone"+randomNum).textbox({
					validType:'telNum',			//验证输入格式
					height:30
				});*/
			}else{
				//禁用所有输入框
				$(".control").attr("readOnly","true");
				//支付方式
				var receiveTypeDom=$("#receiveType"+randomNum);
				var receiveType=Convert.findValueByKey(_payTypeJson,"dictCd",receiveTypeDom.val(),"dictName");
				receiveTypeDom.val(receiveType);

				//合同类型
				var contractTypeDom=$("#contractType"+randomNum);
				var contractType=Convert.findValueByKey(_contractTypeJson,"dictCd",contractTypeDom.val(),"dictName");
				contractTypeDom.val(contractType);

				//合同属性
				var contractAttrDom=$("#contractAttr"+randomNum);
				var contractAttr=Convert.findValueByKey(_contractAttrJson,"dictCd",contractAttrDom.val(),"dictName");
				contractAttrDom.val(contractAttr);

				//付款方式
				var payTypeDom=$("#payType"+randomNum);
				var payType=Convert.findValueByKey(_payTypeJson,"dictCd",payTypeDom.val(),"dictName");
				payTypeDom.val(payType);

				//供货商
				var companyIdDom=$("#companyId"+randomNum);
				var companyName=Convert.findValueByKey(_companyJson,"id",companyIdDom.val(),"companyName");
				companyIdDom.val(companyName);

				//销售方式
				var deliveryModeDom=$("#deliveryMode"+randomNum);
				var delivery = Convert.findValueByKey(_deliveryModeJson,"dictCd",deliveryModeDom.val(),"dictName");
				deliveryModeDom.val(delivery);

				//结算方式
				var deliveryTypeDom=$("#deliveryType"+randomNum);
				var deliveryType = Convert.findValueByKey(_deliveryTypeJson,"dictCd",deliveryTypeDom.val(),"dictName");
				deliveryTypeDom.val(deliveryType);

			}

		}

}
function getTotalWarehouseCost(){
	var totalWarehouseCost = 0;
	$(".sell_warehouse").each(function(){
		var value = $(this).val();
		if (value){
			totalWarehouseCost = Number(totalWarehouseCost) + Number(value);
		}
	});
	return totalWarehouseCost;
}

function getTotalTransportCost(){
	var totalTransportCost = 0;
	$(".sell_transport").each(function(){
		var value = $(this).val();
		if (value){
			totalTransportCost = Number(totalTransportCost) + Number(value);
		}
	});
	return totalTransportCost;
}

function getCostProfit(){
	var grossProfit = $('#grossProfit').val();
	if(!grossProfit){
		grossProfit = Number(0);
	}
	//var totalWarehouseCost = getTotalWarehouseCost();
	//var totalTransportCost = getTotalTransportCost();
	var buy_warehouse_cost = $('.buy_warehouse_cost').val()=='undefined'?0:$('.buy_warehouse_cost').val();//采购仓储费
	var buy_transport_cost = $('.buy_transport_cost').val()=='undefined'?0:$('.buy_transport_cost').val();//采购运输费
	var buy_qingguan_fee = $('.buy_qingguan_fee').val()=='undefined'?0:$('.buy_qingguan_fee').val();//采购清关费
	var buy_kaizheng_fee = $('.buy_kaizheng_fee').val()=='undefined'?0:$('.buy_kaizheng_fee').val();//采购开证手续费
	var buy_chengdui_fee = $('.buy_chengdui_fee').val()=='undefined'?0:$('.buy_chengdui_fee').val();//采购承兑费
	if(grossProfit){
		$('#costProfit').val(Number(grossProfit));
	}
	if(grossProfit != '' && grossProfit != undefined && grossProfit > 0){
		//var cost_value = Number(grossProfit) - Number(totalWarehouseCost) - Number(totalTransportCost);
		var cost_value = Number(grossProfit) - Number(buy_warehouse_cost) - Number(buy_transport_cost) - Number(buy_qingguan_fee) - Number(buy_kaizheng_fee) - Number(buy_chengdui_fee);
		$('#costProfit').val(Number(cost_value).toFixed(2));
	}

}
//获取当前时间
function myformatter(date){
    var y = date.getFullYear();
    var m = date.getMonth()+1;
    var d = date.getDate();
    return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
}
function strDateForDate(str){
	var strDate = str.split(" ");
	var strDatepart = strDate[0].split("-");
	var dtDate = new Date(strDatepart[0],strDatepart[1]-1,strDatepart[2]);
	return dtDate;
}
