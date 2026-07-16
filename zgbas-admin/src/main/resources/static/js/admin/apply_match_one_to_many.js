var applyMatchOneToMany={
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
		selectContract:function(obj){
			var _tag = $(obj).attr("tag");
			var _datagrid = $('#relaTable'+_tag);
			var url=_ctx+"/stock/stockContract/choose";
			$('#w_stock_choose').attr('callbackExec', 'chooseCallback');
			$('#w_stock_choose').window({href:url});
			$('#w_stock_choose').window("open");
		},
		//增加 采购/销售 输入域
		addBuyOrSellInput:function(obj){
			var _type=$(obj).attr("type");
			var _tag=$(obj).attr("tag");
			var _curNumber = 0;
			var url="";
			var target = ""
			var removeInsert = "";
			var removeTag = "";
			if(_tag=="R"){
				url=_ctx+"/apply/import/addProductDetail";
			}else{
				url=_ctx+"/apply/match/addProductDetail";
			}
			if(_type=="B"){
				_curNumber = number1;
				number1 = number1+2;
				target = "b_parenDiv";
				removeInsert = "#insertS";
				removeTag = "#removeSell";
			}else{
				_curNumber = number2;
				number2 = number2+2;
				target = "s_parenDiv";
				removeInsert = "#insertB";
				removeTag = "#removeBuy";
			}
			$.post(url,{contractType:_type,curNumber:_curNumber},function(html){
				//获得随机码
				//var randomNumber=$(html).find("#randomNumber").val();
				$("#"+target).find("fieldset").eq(0).after(html);
				//加载html的组件
				applyMatchOneToMany.initLoadEasyui(_curNumber,_type);
				$('.easyui-linkbutton').linkbutton();
			});
			$(removeInsert).addClass('hide');
			$(removeTag).addClass('hide');
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
			
			var lastIndex_row = changeRows[lastIndex];
			var ed_warehouseName = $('#'+tableName).datagrid('getEditor', {index:lastIndex,field:'warehouseName'});
			var ed_brandNumber = $('#'+tableName).datagrid('getEditor', {index:lastIndex,field:'brandNumber'});
			if(ed_warehouseName || ed_brandNumber){
				var warehouseName = $(ed_warehouseName.target).combobox('textbox').val();
				var brandNumber = $(ed_brandNumber.target).combobox('textbox').val();
				$(ed_warehouseName.target).combobox('setValue',warehouseName);
				$(ed_brandNumber.target).combobox('setValue',brandNumber);
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
			var num = number2;
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
			var _sellDataGrid = $('#relaTable'+num).datagrid('getRows');
			if(_sellDataGrid.length>1){
				applyMatchOneToMany.deleteSell(row);
			}
			//选中
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
				//var _warehousePos = _sellDataGrid[i].warehousePos;
				//var _wrapSpecs = _sellDataGrid[i].wrapSpecs;
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
				applyMatchOneToMany.curGrossProfitAndDiffPrice();
			}
			$(_ed_dealPrice.target).numberbox('textbox').on('keyup',sumAmount);
			$(_ed_dealNumber.target).numberbox('textbox').on('keyup',sumAmount);
			$(_ed_dealPrice.target).numberbox('textbox').on('blur',sumAmount);
			$(_ed_dealNumber.target).numberbox('textbox').on('blur',sumAmount);
			$(_ed_dealAmount.target).numberbox('textbox').on("keyup",function(){
				var _dealAmount = $(this).val();
				$(_ed_dealAmount.target).numberbox('setValue', _dealAmount);
			});
			$(_ed_dealPrice.target).numberbox('textbox').on("blur",function(){
				var _dealPrice= $(_ed_dealPrice.target).numberbox('textbox').val();
				var _ed_taxPrice = _datagrid.datagrid('getEditor', {index:curIndex,field:'taxPrice'});
				if(_dealPrice != '' && _dealPrice != undefined &&_dealPrice != null){
					$(_ed_taxPrice.target).numberbox('setValue',_dealPrice/1.13);
				}
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
							applyMatchOneToMany.syncBuyData(tableName);
							if(flag){	//追加到删arr
								removelist.push(id);
//								console.log("removeArr:"+removelist);
							}
							$(obj).parent().parent().remove();
							//重新计算 数量 计算差价 和毛利润
							applyMatchOneToMany.curGrossProfitAndDiffPrice();
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
		},//当删除整个采购时 需要同步前去掉 销售中该该采购的数据
		syncBuyData:function(table){
			var _row=$(table).datagrid('getRows');
			for(var i=0;i<_row.length;i++){
				//比较销售数据 去除删掉的数据
				applyMatchOneToMany.deleteSell(_row[i]);
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
				var amount = Number(0);
				for(var i=0;i<length;i++){
					var deal_price = _datagirData[i].dealPrice;
					var deal_number = _datagirData[i].dealNumber;
					var total_price = _datagirData[i].totalPrice;
					if (!total_price){
						var _ed_dealPrice = $datagrid.datagrid('getEditor', {index:i,field:'dealPrice'});
						var _ed_dealNumber = $datagrid.datagrid('getEditor', {index:i,field:'dealNumber'});
						var _ed_dealAmount = $datagrid.datagrid('getEditor', {index:i,field:'totalPrice'});
						deal_price = $(_ed_dealPrice.target).numberbox('textbox').val();
						deal_number = $(_ed_dealNumber.target).numberbox('textbox').val();
						total_price = $(_ed_dealAmount.target).numberbox('textbox').val();
					}
					
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
						var toPrice = $datagrid.datagrid("getEditor",{index:i,field:"totalPrice"});
						//数量
						var number = $datagrid.datagrid("getEditor",{index:i,field:"dealNumber"});
						
						if (price){
							deal_price=$(price.target).numberbox('getValue');
							total_price=$(toPrice.target).numberbox('getValue');
							deal_number=$(number.target).numberbox('getValue');
						}
						
						sellNumber += Number(deal_number);
						sellAmount += Number(total_price);
						sellPrice += Number(deal_price);
					}
					amount += Number(total_price);
					/*if (editableFlg){
						$("#totalAmount"+_pointer).val(amount);
					}else{
						$("#totalAmount"+_pointer).numberbox('setValue',amount);
					}*/
					$("#totalAmount"+_pointer).numberbox('setValue',amount);
					if ("B"==_atr){
						var pay_rate = $('#payRate'+_pointer).numberbox('getValue');
						var pay_bond_amount = Number(amount)*pay_rate;
						if (pay_bond_amount){
							$('#payBondAmount'+_pointer).numberbox('setValue',pay_bond_amount);
						}
					}else{
						var receive_rate = $('#receiveRate'+_pointer).numberbox('getValue');
						var receive_bond_amount = Number(amount)*receive_rate;
						if (receive_bond_amount){
							$('#receiveBondAmount'+_pointer).numberbox('setValue',receive_bond_amount);
						}
					}
				}
			}
			$("#buyTotalNumber").val(buyNumber);
			$("#sellTotalNumber").val(sellNumber);
			var _grossProfit = Number(sellAmount) - Number(buyAmount);
			var _differPrice = Number(sellPrice) - Number(buyPrice);
			_grossProfit = Number(_grossProfit) > 0 ? _grossProfit : 0;
			_grossProfit = Number(_grossProfit).toFixed(3);
			$("#grossProfit").val(_grossProfit);
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
							//var _warehousePos = curAllRows[i].warehousePos;
							//var _wrapSpecs = curAllRows[i].wrapSpecs;
							//var _warehousePrice = curAllRows[i].warehousePrice;
							var _otherDealPrice = curAllRows[i].dealPrice;
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
								/*warehousePos:_warehousePos,
								wrapSpecs:_wrapSpecs,
								warehousePrice:_warehousePrice,*/
								otherDealPrice:_otherDealPrice,
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
							getMinDealPrice(_sellIndex);
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
					{field:'productCd',title:'货品名称',align:'center',width:120,editor:{type:'combotree',options:{required:'true',editable:'false',panelWidth:150,panelHeight:300,data:_productAllJson}},
						formatter:function(value,row,index){
							return Convert.findValueByKey(_productChildrenJson,"typeCode",value,"typeName");
					}},
					{field:'brandNumber',title:'牌号',width:100,align:'center',editor:{type:'combobox',options:{required:'true',panelWidth:180,textField:'brandNumber',valueField:'brandNumber'}}},
					{field:'factoryId',title:'厂商',width:120,align:'center',editor:{type:'combobox',options:{required:'true',panelWidth:180,data:_factoryJson,textField:'factoryName',valueField:'id',onHidePanel:function(){BasCombobox.onHidePanel(this);}}},
						formatter:function(value,row,index){
							return Convert.findValueByKey(_factoryJson,"id",value,"factoryName");
					}},
					{field:'warehouseName',title:'仓库',width:150,align:'center',editor:{type:'combobox',options:{required:true,valueField:'warehouseName',textField:'warehouseName'}}},
					{field:'dealNumber',title:'数量(吨)',align:'center', width:120,editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:4}}},
					{field:'dealPrice',title:'单价(元)', align:'center',width:120,editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:2}}},
					{field:'taxPrice',title:'不含税单价(元)', width:120,align:'center',editor:{type:'numberbox',options:{required:'true' ,readonly:'true',validType:'length[1,20]',precision:2}},
						formatter: function (value, row, index) {
							if (row != null) {
								return parseFloat(row.dealPrice/1.13).toFixed(2);
							}
					}},
					{field:'totalPrice',title:'总价(元)', align:'center',width:110,editor:{type:'numberbox',options:{required:'true',readonly:'true',validType:'length[1,20]',precision:2}}},
					{field:'option',title:'操作', width:90, hidden:true, align:'center',formatter:function(value, row, index){
						var str ="";
						str = str + "<i onclick=applyMatchOneToMany.remove(this,\""+row.id+"\") table='relaTable"+randomNum+"' class='icon-remove icon cursor mt4'></i>";
				        return str; 
					}}
				]]
			}else{
				var processCode = $("#processCode").val();
				if (processCode == "APPLY_MATCH_IOUS"){
					_curColumns = [ [
						{field:'productCd',title:'货品名称',width:120,align:'center',
							formatter:function(value,row,index){
								return Convert.findValueByKey(_productChildrenJson,"typeCode",value,"typeName");
						}},
						{field:'brandNumber',title:'牌号',width:100,align:'center'},
						{field:'factoryId',title:'厂商',width:120,align:'center',
							formatter:function(value,row,index){
								return Convert.findValueByKey(_factoryJson,"id",value,"factoryName");
						}},
						{field:'warehouseName',title:'仓库',width:150,align:'center'},
						{field:'dealNumber',title:'数量(吨)', width:120,align:'center',editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:4}}},
						{field:'otherDealPrice',title:'采购单价(元)',align:'center', width:95,hidden:true},
						{field:'minDealPrice',title:'最低销售价(元)',align:'center', width:110,editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:2,readonly:'true'}}},
						{field:'premium',title:'加价(元)', width:110,align:'center',editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:2}}},
						{field:'dealPrice',title:'销售价(元)', width:110,align:'center',editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:2}}},
						{field:'taxPrice',title:'不含税单价(元)', width:110,align:'center',editor:{type:'numberbox',options:{required:'true' ,readonly:'true',validType:'length[1,20]',precision:2}},
							formatter: function (value, row, index) {
								if (row != null) {
									return parseFloat(row.dealPrice/1.13).toFixed(2);
								}
						}},
						{field:'totalPrice',title:'总价(元)', width:110,align:'center',editor:{type:'numberbox',options:{required:'true',readonly:'true',validType:'length[1,20]',precision:2}}},
						{field:'option',title:'操作', width:90, hidden:true, align:'center',formatter:function(value, row, index){
							var str ="";
							return str; 
						}}
					]];
				}else{
					_curColumns = [ [
						{field:'productCd',title:'货品名称',width:120,align:'center',
							formatter:function(value,row,index){
								return Convert.findValueByKey(_productChildrenJson,"typeCode",value,"typeName");
						}},
						{field:'brandNumber',title:'牌号',width:100,align:'center'},
						{field:'factoryId',title:'厂商',width:120,align:'center',
							formatter:function(value,row,index){
								return Convert.findValueByKey(_factoryJson,"id",value,"factoryName");
						}},
						{field:'warehouseName',title:'仓库',width:150,align:'center'},
						{field:'dealNumber',title:'数量(吨)', width:120,align:'center',editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:4}}},
						{field:'dealPrice',title:'单价(元)', width:120,align:'center',editor:{type:'numberbox',options:{required:'true',validType:'length[1,20]',precision:2}}},
						{field:'taxPrice',title:'不含税单价(元)', width:120,align:'center',editor:{type:'numberbox',options:{required:'true' ,readonly:'true',validType:'length[1,20]',precision:2}},
							formatter: function (value, row, index) {
								if (row != null) {
									return parseFloat(row.dealPrice/1.13).toFixed(2);
								}
						}},
						{field:'totalPrice',title:'总价(元)', width:110,align:'center',editor:{type:'numberbox',options:{required:'true',readonly:'true',validType:'length[1,20]',precision:2}}},
						{field:'option',title:'操作', width:90, hidden:true, align:'center',formatter:function(value, row, index){
							var str ="";
							return str; 
						}}
					]];
				}
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
						//var ed_warehousePos = $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'warehousePos'});		
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
									var warehouseName = $(ed_warehouseName.target).combobox('textbox').val();
									$(ed_warehouseName.target).combobox('reload',_ctx+'/bas/warehouse/findWarehoseList/'+productCode);
									$(ed_warehouseName.target).combobox('setValue',warehouseName);
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
//							$(ed_warehousePos.target).combotree('options').onClick = function(record){
//								$(ed_warehousePos.target).combotree('setValue',record.id);	
//								//根据市区areacode获取单价
//								$.post(_ctx+"/bs/areaCost/acquirePriceVo",{"areaCode":record.id},function(data){
//									var rObj = eval(data);
//									var _housePrice=rObj;
//									var _area_housePrice =  $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'warehousePrice'});
//									$(_area_housePrice.target).numberbox('setValue',rObj.warehouseUnitCost);
//						 		});
//							}
							if (_defaultDeliveryAddr){
								_defaultAddr = _defaultDeliveryAddr[0].dictName;
							}
							var _warehouse_name = $(ed_warehouseName.target).combobox('textbox').val();
							if (!_warehouse_name){
								$(ed_warehouseName.target).combobox('setValue',_defaultAddr);
							}
					}else{
						//加价
						var processCode = $("#processCode").val();
						if (processCode == "APPLY_MATCH_IOUS"){
							var ed_premium = $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'premium'});
							var ed_dealPrice = $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'dealPrice'});
							$(ed_premium.target).numberbox('textbox').on('blur',function(){
								getDealPrice(randomNum);
							});
							$(ed_dealPrice.target).numberbox('textbox').on('blur',function(){
								getPremium(randomNum);
							});
						}
					}
					//点击仓库时 添加改行 仓库名称
					/*var ed_warehouseId = $('#relaTable'+randomNum).datagrid('getEditor', {index:index,field:'warehouseId'});
					$(ed_warehouseId.target).combobox('options').onClick = function(record){
						var warehouseName =record.warehouseName;
						row.factoryName = warehouseName;
					}; */
					//计算总价
					applyMatchOneToMany.curOper(index,row,'relaTable'+randomNum);
				},onDblClickCell:function(index,field){
					var entityStatus=$("#entityStatus").val();					
					if(entityStatus=="N"|| entityStatus=="B" || entityStatus=="C"){
						$('#relaTable'+randomNum).datagrid('endEdit', lastIndex);						
						$('#relaTable'+randomNum).datagrid('beginEdit', index);	
					}							
				},onLoadSuccess:function(data){
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
				
				var processCode = $("#processCode").val();
				if (processCode != "APPLY_MATCH_IOUS"){
					$("#creditDays"+randomNum).attr("readonly",true);
				}
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
				
				if("B"==type){
					if (_defaultDeliveryAddr){
						_defaultAddr = _defaultDeliveryAddr[0].dictName;
					}
					var delivery_addr = $("#deliveryAddr"+randomNum).val();
					if (!delivery_addr){
						$("#deliveryAddr"+randomNum).val(_defaultAddr);
					}
				}
				
				$("#payBondAmount"+randomNum+",#receiveBondAmount"+randomNum).numberbox({
					 min:0,
					 precision:2,
					 editable:false
				});
				var buy_companyId = $("#buyCompanyId"+randomNum).val();
			    if(!buy_companyId){
			      buy_companyId = 0;
			    }
				$("#buyCompanyName"+randomNum).combobox({
					url:_ctx+"/bs/company/listMyCompany/"+0+"/"+1,
					mode:'remote',
					limitToList:true,
					textField:'text',
					valueField:'text',
					required:true,
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
						var myFlag = record.myFlag;
						var bankName = record.bankName;
						var bankAccount = record.bankAccount;
						if (myFlag!=true){
							setTimeout(function () { $("#buyCompanyName"+randomNum).combobox("clear"); }, 1)
						}else{
							$('#buyCompanyId'+randomNum).val(record.id);
						    var _contactPhone = record.contactPhone;
						    var _address = record.address;
						    var _number = randomNum;
						    var _contactPhoneDom = $("#contactPhone"+_number);
						    if(_contactPhone){
						    	_contactPhoneDom.val(_contactPhone);
						    }else{
						    	_contactPhoneDom.val("");
						    }
						    var _contactAddrDom = $("#contactAddr"+_number);
						    if(_address){
						    	_contactAddrDom.val(_address);
						    }else{
						    	_contactAddrDom.val("");
						    }
						}
					}
				});
				//船期
				$("#shippingDate"+randomNum).datebox({
					height:30
				});
				// //销售方式
				// $("#deliveryModeAdd"+randomNum).combobox({
				// 	data:_buyDeliveryModeJson, 
				// 	valueField:'dictCd',
				// 	textField:'dictName',
				// 	panelHeight:"auto",
				// 	editable:false,
				// 	onLoadSuccess:function(){
				// 		var value = $(this).val();
				// 		if (!value){
				// 			$(this).combobox('setValue','XKHH');
				// 		}
				// 	}
				// });
				//收货时间
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
						var processCode = $("#processCode").val();
						if (processCode == "APPLY_MATCH_IOUS"){
							var creditDays = $('#creditDays'+randomNum).val();
							if (creditDays){
								var max_full_time = addDate(mDate,creditDays);
								$('#receiveFullTime'+randomNum).datebox('setValue',max_full_time);
							}
						}
						getDealPrice(randomNum);
					}
				});
				//收货日期(补充)
				$("#arrivalTimeExt"+randomNum).combobox({
					data:_arrivalTimeExtJson, 
					valueField:'dictCd',
					textField:'dictName',
					panelHeight:"auto",
					required: true,
					editable: false,
					onLoadSuccess:function(){
						var value = $(this).combobox('getValue');
						if(!value){
							$(this).combobox("setValue","LR");
						}
					},
					onSelect:function(record){
					 	if(record.dictCd == 'K'){
					 		$("#arrivalTime"+randomNum).datebox({required:false});
					 		$("#arrivalTime"+randomNum).datebox('clear');
					 		var a = $(this).prev().addClass("hide");
					 	}else{
					 		var _value = $("#arrivalTime"+randomNum).datebox('getValue');
					 		$("#arrivalTime"+randomNum).datebox({required:true});
					 		$("#arrivalTime"+randomNum).datebox('setValue',_value);
					 		var a = $(this).prev().removeClass("hide");
					 	}
				 	}
				});
				
				//收货时间
				$("#arrivalTimeB"+randomNum).datebox({
					height:30,
					editable:false,
					onSelect:function(record){
					 	var mDate=$("#arrivalTimeB"+randomNum).datebox('getValue');
						var curr_time = new Date();
						var sDate=myformatter(curr_time);
						if(mDate>sDate){
							$("#contractAttr"+randomNum).combobox('setValue', "F");
						}else{
							$("#contractAttr"+randomNum).combobox('setValue',"N");
						}
					}
				});
				//收货日期(补充)
				$("#arrivalTimeExtB"+randomNum).combobox({
					data:_arrivalTimeExtJson, 
					valueField:'dictCd',
					textField:'dictName',
					panelHeight:"auto",
					required: true,
					editable: false,
					onLoadSuccess:function(){
						var value = $(this).combobox('getValue');
						if(!value){
							$(this).combobox("setValue","LR");
						}
					},
					onSelect:function(record){
					 	if(record.dictCd == 'K'){
					 		$("#arrivalTimeB"+randomNum).datebox({required:false});
					 		$("#arrivalTimeB"+randomNum).datebox('clear');
					 		var a = $(this).prev().addClass("hide");
					 		$("#deliveryMode"+randomNum).combobox({
								hasDownArrow:false,
								readonly:true,
								onLoadSuccess:function(){
									var value = $(this).val();
									if (!value){
										$(this).combobox('setValue','XKHH');
									}
								}
						 	});
					 		$("#deliveryModeAdd"+randomNum).combobox({
								hasDownArrow:false,
								readonly:true,
								onLoadSuccess:function(){
									var value = $(this).val();
									if (!value){
										$(this).combobox('setValue','XKHH');
									}
								}
						 	});
					 	}else{
					 		var _value = $("#arrivalTimeB"+randomNum).datebox('getValue');
					 		$("#arrivalTimeB"+randomNum).datebox({required:true});
					 		$("#arrivalTimeB"+randomNum).datebox('setValue',_value);
					 		$(this).prev().removeClass("hide");
					 		var delivery_mode = $('#deliveryMode'+randomNum).val();
					 		var delivery_mode_addr= $('#deliveryModeAdd'+randomNum).val();
					 		// $("#deliveryMode"+randomNum).combobox({
						 	// 	data:_buyDeliveryModeJson,
							// 	hasDownArrow:true,
							// 	readonly:false,
							// 	editable:false
						 	// });
					 		// $("#deliveryModeAdd"+randomNum).combobox({
						 	// 	data:_buyDeliveryModeJson,
							// 	hasDownArrow:true,
							// 	readonly:false,
							// 	editable:false
						 	// });
					 		if (delivery_mode){
					 			$("#deliveryMode"+randomNum).combobox('setValue',delivery_mode);
					 		}
					 		if (delivery_mode_addr){
					 			$("#deliveryModeAdd"+randomNum).combobox('setValue',delivery_mode_addr);
					 		}
					 	}
				 	}
				});
				
				//收货时间
				$("#deliveryTime"+randomNum).datebox({
					height:30,
					required:true,
					editable:false
				});
				//运输费
				$("#transportCost"+randomNum).numberbox({
					 min:0,
					 precision:3,
					 height:30
				});
				//合同总额金额
				$("#totalAmount"+randomNum).numberbox({
					 height:30,
					 min:0,
					 precision:2,
					 editable:false
				});
				
				$("#tax_warehouseCost"+randomNum+",#tax_transportCost"+randomNum).numberbox({
					min:0,
					precision:2,
					readonly:true
				});
				
				//仓储费
				$("#warehouseCost"+randomNum).numberbox({
					 min:0,
					 precision:2,
					 height:30,
					 required:true,
					 onChange:function(newValue){
						 var _tax_warehouseCost = parseFloat(newValue/1.09).toFixed(2);
						 $('#tax_warehouseCost'+randomNum).numberbox('setValue',_tax_warehouseCost);
					 }
				});
				//运输费
				$("#transportCost"+randomNum).numberbox({
					 min:0,
					 precision:2,
					 height:30,
					 required:true,
					 onChange:function(newValue){
						 var _tax_transportCost = parseFloat(newValue/1.09).toFixed(2);
						 $('#tax_transportCost'+randomNum).numberbox('setValue',_tax_transportCost);
					 }
				});
				//交货地点
				$("#deliveryAddr"+randomNum).textbox({
					required:true
				});
				//合同类型
				$("#contractType"+randomNum).combobox({
					data:_contractTypeJson,
					textField:'dictName',
					valueField:'dictCd',
					height:30,
					panelHeight:"auto",
					editable :false,
					hasDownArrow:false,
					readonly:true
				});
				//付全款时间
				$("#payFullTime"+randomNum).datebox({
					height:30,
					editable:false
				});
				
				//付定金时间
				$("#payBondTime"+randomNum).datebox({
					height:30,
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
							$('#payFullTime'+randomNum).datebox('setValue',dateFullTimeValue);
						}
					}
				});
				$('#payRate'+randomNum).combobox({
					data:_contractBondRateJson,
					valueField:'dictCd',
					textField:'dictName',
					panelHeight:'auto',
					editable:false,
					onLoadSuccess:function(data){
						var value = $(this).val();
						if (!value){
							$(this).combobox('setValue',data[0].dictCd);
						}
					},
					onSelect : function(node){
						var value = node.dictCd;
						var total_amount = $('#totalAmount'+randomNum).val();
						var bond_amount = Number(total_amount)*value;
						$('#payBondAmount'+randomNum).numberbox('setValue',bond_amount);
						if (value > 0){
							$("#payBondTime"+randomNum).parent().removeClass("hide");
							$("#payBondTime"+randomNum).datebox('options').required = true;
							$("#payBondTime"+randomNum).datebox('textbox').validatebox('options').required = true;
							$("#payBondTime"+randomNum).datebox('validate');
							//$("#deliveryMode"+randomNum).combobox('setValue','XKHH');
						}else{
							$("#payBondTime"+randomNum).parent().addClass("hide");
							$("#payBondTime"+randomNum).datebox({required:false});
							//$("#deliveryMode"+randomNum).combobox('setValue','XHHK');
						}
					}
				});
				//付款方式
				$("#payTypeAdd"+randomNum).combobox({
					data:_payTypeJson,
					textField:'dictName',
					valueField:'dictCd',
					panelHeight:"auto",
					editable:false,
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
				//业务员
				$('#matchUserName'+randomNum).combotree({
					data: _matchUserNameTree,
					multiple : false,
					editable : true,
					required:true,
					panelHeight:275,
					panelWidth:150,
					formatter:function(node){
						var enableFlg = node.attributes.enableFlg;
						if (enableFlg==false){
							return '<span style="color:red;">'+node.text+'(无效)</span>';
						}
						return node.text;
					},
					onLoadSuccess :function(data){
						var value = $(this).val();
						if (!value){
							var match_user_name = $('#currentUserName').val();
							var match_user_id = $('#currentUserId').val();
							if (match_user_name){
								$('#matchUserName'+randomNum).combotree('setValue',match_user_name);
							}
							if (match_user_id){
								$('#matchUserId'+randomNum).val(match_user_id);	
							}
						}
					},
					onSelect : function(node){
						if(node.children.length!=0){
							$('#matchUserName'+randomNum).combotree('clear');
						}else{
							$('#matchUserName'+randomNum).combotree('setValue',node.text);	
							$('#matchUserId'+randomNum).val(node.id.replace("user",""));	
						}
					}
				});
				$("#deliveryTypeAdd"+randomNum).combobox({
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
				//开票日期
//				$("#invoiceDate"+randomNum).combobox({
//					data:_invoiceDateJson, 
//					valueField:'dictCd',
//					textField:'dictName',
//					panelHeight:"auto",
//					required: true,
//					editable: false
//				});
				//收全款时间
				var processCode = $("#processCode").val();
				if (processCode != "APPLY_MATCH_IOUS"){
					$("#receiveFullTime"+randomNum).datebox({
						height:30,
						editable:false,
						onChange : function(newValue,oldValue){
							var dateFullTime = strDateForDate($('#receiveBondTime'+randomNum).datebox('getValue')).getTime();
							var dateFullTimeValue = $('#receiveBondTime'+randomNum).datebox('getValue');
							var dateBondTime = strDateForDate(newValue).getTime();
							$('#receiveBondTime'+randomNum).datebox().datebox('calendar').calendar({  
								validator: function(date){  
					                return strDateForDate(newValue) >=date;//<=  
					            }  
							});
							if(dateBondTime < dateFullTime){
								$('#receiveBondTime'+randomNum).datebox('setValue', newValue);
							}  else {
								$('#receiveBondTime'+randomNum).datebox('setValue', dateFullTimeValue);
							}  
						}
					});
				}else{
					$("#receiveFullTime"+randomNum).datebox({
						height:30,
						editable:false,
						readonly:true,
						hasDownArrow:false,
						onChange : function(newValue,oldValue){
							var dateFullTime = strDateForDate($('#receiveBondTime'+randomNum).datebox('getValue')).getTime();
							var dateFullTimeValue = $('#receiveBondTime'+randomNum).datebox('getValue');
							var dateBondTime = strDateForDate(newValue).getTime();
							$('#receiveBondTime'+randomNum).datebox().datebox('calendar').calendar({  
								validator: function(date){  
					                return strDateForDate(newValue) >=date;//<=  
					            }  
							});
							if(dateBondTime < dateFullTime){
								$('#receiveBondTime'+randomNum).datebox('setValue', newValue);
							}  else {
								$('#receiveBondTime'+randomNum).datebox('setValue', dateFullTimeValue);
							}  
						}
					});
				}
				
				//收定金时间
				$("#receiveBondTime"+randomNum).datebox({
					height:30,
					editable:false,
					onChange : function(newValue,oldValue){
						/*var dateFullTime = strDateForDate($('#receiveFullTime'+randomNum).datebox('getValue')).getTime();
						var dateFullTimeValue = $('#receiveFullTime'+randomNum).datebox('getValue');
						var dateBondTime = strDateForDate(newValue).getTime();
						$('#receiveFullTime'+randomNum).datebox().datebox('calendar').calendar({  
							validator: function(date){  
				                return strDateForDate(newValue) <=date;//<=  
				            }  
						 });
						if(dateBondTime > dateFullTime){
							 $('#receiveFullTime'+randomNum).datebox('setValue', newValue);
						}  else {
							$('#receiveFullTime'+randomNum).datebox('setValue', dateFullTimeValue);
						}*/
					}
				});
				$('#receiveRate'+randomNum).combobox({
					data:_contractBondRateJson,
					valueField:'dictCd',
					textField:'dictName',
					panelHeight:'auto',
					editable:false,
					onLoadSuccess:function(data){
						var value = $(this).val();
						if (!value){
							$(this).combobox('setValue',data[0].dictCd);
						}
					},
					onSelect : function(node){
						var value = node.dictCd;
						var total_amount = $('#totalAmount'+randomNum).val();
						var bond_amount = Number(total_amount)*value;
						$('#receiveBondAmount'+randomNum).numberbox('setValue',bond_amount);
						if (value > 0){
							$("#receiveBondTime"+randomNum).parent().removeClass("hide");
							$("#receiveBondTime"+randomNum).datebox({required:true});
							//$("#deliveryMode"+randomNum).combobox('setValue','XKHH');
							//$("#creditDays"+randomNum).parent().addClass("hide");
							//$("#creditDays"+randomNum).numberbox('setValue',"");
						}else{
							$("#receiveBondTime"+randomNum).parent().addClass("hide");
							$("#receiveBondTime"+randomNum).datebox({required:false});
							//$("#deliveryMode"+randomNum).combobox('setValue','XHHK');
							var processCode = $("#processCode").val();
							if (processCode != "APPLY_MATCH_IOUS"){
								$("#creditDays"+randomNum).attr("readonly",true);
							}else{
								//$("#creditDays"+randomNum).parent().removeClass("hide");
							}
						}
					}
				});
				$('#creditDays'+randomNum).numberbox({
					min:1,
					max:30,
					onChange : function(newValue,oldValue){
						var arrivalTime_time = $('#arrivalTime'+randomNum).datebox('getValue');
						var max_full_time = addDate(arrivalTime_time,newValue);
						$('#receiveFullTime'+randomNum).datebox('setValue',max_full_time);
						getMinDealPrice(randomNum);
						getDealPrice(randomNum);
					}
				});
				var processCode = $("#processCode").val();
				if (processCode == "APPLY_MATCH_IOUS"){
					$('#creditDays'+randomNum).parent().removeClass('hide');
					$('#creditDays'+randomNum).numberbox({required:true});
				}else{
					$('#creditDays'+randomNum).attr("readonly",true);
				}
//				var max_creditDays = $('#creditAmountDays').val();
//				if (!max_creditDays){
//					max_creditDays = 30;
//				}
				
				$('#ourCompany_name'+randomNum).combobox({
					data:_ourCompanyNameJson, 
					valueField:'dictCd',
					textField:'dictName',
					panelHeight:"auto",
					editable:false,
					onLoadSuccess:function(data){
						var value = $(this).val();
						if (!value){
							$(this).combobox('setValue',data[0].dictCd);
							value = data[0].dictCd;
						}
					}
				});
				
				
				/*var _payTime ;
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
				}*/
				/*if(!_payTime){
					var curr_time = new Date();
					$("#payBondTime"+randomNum+","+"#payFullTime"+randomNum+","+"#receiveFullTime"+randomNum+","+"#receiveBondTime"+randomNum).datebox('setValue',myformatter(curr_time));
				}*/
				
				var processCode = $("#processCode").val();
				if (processCode == "APPLY_MATCH_IOUS"){
					//销售-结算方式
					$('#deliveryModeS'+randomNum).combobox({
						data:_deliveryModeJson,
						valueField:'dictCd',
						textField:'dictName',
						required: true,
						panelHeight:"auto",
						editable:false,
						readonly:true,
						hasDownArrow:false,
						onLoadSuccess:function(){
							var _this = $(this);
							_this.combobox("setValue","SX");
						},
						onHidePanel:function(){
							BasCombobox.onHidePanel(this);
						}
					});
					
				}else{
					//销售-结算方式
					$('#deliveryModeS'+randomNum).combobox({
						data:_deliveryModeJson,
						valueField:'dictCd',
						textField:'dictName',
						required: true,
						panelHeight:"auto",
						editable:false,
						onLoadSuccess:function(){
							var _this = $(this);
							var processCode = $("#processCode").val();
							var _val = _this.val();
							if(""==_val){
								if (processCode == "APPLY_MATCH_IOUS"){
									_this.combobox("setValue","SX");
								}else{
									_this.combobox("setValue","XKHH");
								}
							}else{
								_this.combobox("setValue",_val);
							}
						},
						onHidePanel:function(){
							BasCombobox.onHidePanel(this);
						}
					});
					
				}
				//供货商
				var _companyId = $("#companyIdAdd"+randomNum).val();
		        if(!_companyId){
		          _companyId = 0
		        }
		        $("#companyIdAdd"+randomNum).combobox({
		        	url:_ctx+"/bs/company/listMyCompany/"+0+"/"+2,
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
					    var _contactPhoneDom = $("#contactPhone"+_number)
					    if(_contactPhone){
					    	_contactPhoneDom.val(_contactPhone);
					    }else{
					    	_contactPhoneDom.val("");
					    }
					    var _contactAddrDom = $("#contactAddr"+_number);
					    if(_address){
					    	_contactAddrDom.val(_address);
					    }else{
					    	_contactAddrDom.val("");
					    }
					    var myFlag = record.myFlag;
						if (myFlag!=true){
							setTimeout(function () { $("#companyName").combobox("clear"); }, 1)
						}
					}
				});
				
				//收款方式
				$("#receiveTypeAdd"+randomNum).combobox({
					data:_payTypeJson,
					textField:'dictName',
					valueField:'dictCd',
					panelHeight:"auto"
				});
				//港口
				$("#port"+randomNum).textbox({
					height:30
				});
				
				$("#objectivePort"+randomNum).textbox({
					height:30
				});
				
				$("#foreignContractNo"+randomNum).textbox({
					height:30
				});
				//合同编号
				$("#contractNo"+randomNum).textbox({
					height:30
				});

				if("S"==type){
					//运输费仓储费
					$('#warehouseCost'+randomNum).numberbox({
						min:0,    
					    precision:2,
					    required: true,
					    onChange:function(newValue,oldValue){
					    	getCostProfit();
					    	var _tax_warehouseCost = parseFloat(newValue/1.09).toFixed(2);
							$('#tax_warehouseCost'+randomNum).numberbox('setValue',_tax_warehouseCost);
							getMinDealPrice(randomNum);
							getDealPrice(randomNum);
						}
					});
					$('#transportCost'+randomNum).numberbox({
						min:0,    
					    precision:2,
					    required: true,
					    onChange:function(newValue,oldValue){
					    	getCostProfit();
					    	var _tax_transportCost = parseFloat(newValue/1.09).toFixed(2);
							$('#tax_transportCost'+randomNum).numberbox('setValue',_tax_transportCost);
							getMinDealPrice(randomNum);
							getDealPrice(randomNum);
						}
					});
				}
			}else{
				//禁用所有输入框
				$(".control").attr("readOnly","true");
				//合同类型
				var contractTypeDom=$("#contractType"+randomNum);
				var contractType=Convert.findValueByKey(_contractTypeJson,"dictCd",contractTypeDom.val(),"dictName");
				contractTypeDom.val(contractType);
				
				//付款方式
				var payTypeDom=$("#payType"+randomNum);
				var payType=Convert.findValueByKey(_payTypeJson,"dictCd",payTypeDom.val(),"dictName");
				payTypeDom.val(payType);
				
				//销售方式
				var deliveryModeDom=$("#deliveryMode"+randomNum);
				var delivery = Convert.findValueByKey(_deliveryModeJson,"dictCd",deliveryModeDom.val(),"dictName");
				deliveryModeDom.val(delivery);
				
				var deliveryModeSDom=$("#deliveryModeS"+randomNum);
				var deliveryS = Convert.findValueByKey(_deliveryModeJson,"dictCd",deliveryModeSDom.val(),"dictName");
				deliveryModeSDom.val(deliveryS);
				
				//交货方式
				var deliveryTypeDom=$("#deliveryType"+randomNum);
				var deliveryType = Convert.findValueByKey(_deliveryTypeJson,"dictCd",deliveryTypeDom.val(),"dictName");
				deliveryTypeDom.val(deliveryType);
				
				//开票日期
//				var _invoiceDate = $('#invoiceDate'+randomNum).val();
//				var invoiceDate = Convert.findValueByKey(_invoiceDateJson,"dictCd",_invoiceDate,"dictName");
//				$("#invoiceDate"+randomNum).val(invoiceDate);

				//到货日期
				$("#arrivalTimeExt"+randomNum).attr('type','hidden');
				var _arrivalTimeExt = $('#arrivalTimeExt'+randomNum).val();
				var arrivalTimeExt = Convert.findValueByKey(_arrivalTimeExtJson,"dictCd",_arrivalTimeExt,"dictName");
				var arrivalTime = $("#arrivalTime"+randomNum).val();
				if (arrivalTimeExt==undefined||arrivalTimeExt==null){
					arrivalTimeExt = '';
				}
				$("#arrivalTime"+randomNum).val(arrivalTime + arrivalTimeExt);
				
				//到货日期
				$("#arrivalTimeExtB"+randomNum).attr('type','hidden');
				var _arrivalTimeExt = $('#arrivalTimeExtB'+randomNum).val();
				var arrivalTimeExt = Convert.findValueByKey(_arrivalTimeExtJson,"dictCd",_arrivalTimeExt,"dictName");
				var arrivalTime = $("#arrivalTimeB"+randomNum).val();
				if (arrivalTimeExt==undefined||arrivalTimeExt==null){
					arrivalTimeExt = '';
				}
				$("#arrivalTimeB"+randomNum).val(arrivalTime + arrivalTimeExt);

				//质量标准
//				var _qualityStandard = $('#qualityStandard'+randomNum).val();
//				var qualityStandard = Convert.findValueByKey(_qualityStandardJson,"dictCd",_qualityStandard,"dictName");
//				$('#qualityStandard'+randomNum).val(qualityStandard);
				
				var _payRate = $('#payRate'+randomNum).val();
				var payRate = Convert.findValueByKey(_contractBondRateJson,"dictCd",_payRate,"dictName");
				$("#payRate"+randomNum).val(payRate);
				
				var _receiveRate = $('#receiveRate'+randomNum).val();
				var receiveRate = Convert.findValueByKey(_contractBondRateJson,"dictCd",_receiveRate,"dictName");
				$("#receiveRate"+randomNum).val(receiveRate);
				
				//我方抬头
				$('#ourCompanyName').combobox({
					hasDownArrow:false
				});
				//合同属性
				$('#contractAttr').combobox({
					hasDownArrow:false
				});
				$('#qualityStandard').combobox({
					hasDownArrow:false
				})
				//账期
				/*var creditDays = $('#creditDays'+randomNum).val();
				if (!creditDays){
					$('#creditDays'+randomNum).parent().addClass('hide');
				}*/
				//收保证金日期
				var receiveBondTime = $('#receiveBondTime'+randomNum).val();
				if (!receiveBondTime){
					$('#receiveBondTime'+randomNum).parent().addClass('hide');
				}
				//供货商
//				var companyIdDom=$("#companyId"+randomNum);
//				var companyIdValue = companyIdDom.val();
//				var companyName=Convert.findValueByKey(_companyJson,"id",companyIdDom.val(),"companyName");
//				$.post(_ctx+"/bs/company/findCompanyById?id="+companyIdValue,function(result){
//					companyIdDom.val(companyName);
//				});
				
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

function getStotalAmount(){
	var sTotalAmount = 0;
	$(".s_totalAmount").each(function(){
		var value = $(this).val();
		if (value){
			sTotalAmount = Number(sTotalAmount) + Number(value);
		}
	});
	return sTotalAmount;
}

function getCostProfit(){
	var grossProfit = $('#grossProfit').val();
	var sTotalAmount = getStotalAmount();
	if(!grossProfit){
		grossProfit = Number(0);
	}
	var totalWarehouseCost = getTotalWarehouseCost();
	var totalTransportCost = getTotalTransportCost();
	if(grossProfit){
		$('#costProfit').val(Number(grossProfit));
	}
	if(grossProfit != '' && grossProfit != undefined && grossProfit > 0){
		//增值税=毛利/1.13*0.13
		var vatAmount = Number(grossProfit/1.13*0.13).toFixed(4);
		//附加税=增值税*0.12
		var extraAmount = Number(vatAmount*0.12).toFixed(4);
		//印花税=含税销售收入*3/10000
		var printAmount = Number(sTotalAmount*3/10000).toFixed(4);
		//资金成本=含税采购成本*（销售回款时间-采购付款时间）*0.0002
		//仓储运输费
		var warehouseTransAmount = Number(totalTransportCost/1.09+totalWarehouseCost/1.06).toFixed(4);
		var _cost_profit = Number(grossProfit-vatAmount-extraAmount-printAmount-warehouseTransAmount).toFixed(2);
		$('#costProfit').val(Number(_cost_profit));
	}

}

function getMinDealPrice(randomNum){
	if (processCode != "APPLY_MATCH_IOUS"){
		return;
	}
	var rows = $('#relaTable'+randomNum).datagrid('getRows');
	var total_number = 0;
	var transportCost_price = 0;
	var warehouseCost_price = 0;
	var transportCost = $('#transportCost'+randomNum).numberbox('getValue');
	var warehouseCost = $('#warehouseCost'+randomNum).numberbox('getValue');
	var creditDays = $('#creditDays'+randomNum).numberbox('getValue');
	if (!creditDays){
		creditDays = 0;
	}
	for(var i=0; i<rows.length; i++){
		var _ed_dealNumber =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'dealNumber'});
		if(!_ed_dealNumber){
			$('#relaTable'+randomNum).datagrid('beginEdit', i);
			_ed_dealNumber =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'dealNumber'});
			var deal_Number = Number($(_ed_dealNumber.target).numberbox('textbox').val());
			total_number += deal_Number;
		}else{
			var deal_Number = Number($(_ed_dealNumber.target).numberbox('textbox').val());
			total_number += deal_Number;
		}		
	}
	if (!total_number){
		return;
	}
	if (transportCost){
		transportCost_price = transportCost/total_number;
	}
	if (warehouseCost){
		warehouseCost_price = warehouseCost/total_number;
	}
	for(var i=0; i<rows.length; i++){
		var other_dealPrice = rows[i].otherDealPrice;
		var _ed_minDealPrice =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'minDealPrice'});
		var _ed_dealPrice =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'dealPrice'});
		if(other_dealPrice){
			if (other_dealPrice){
				//var value = Number(other_dealPrice)*Number(0.0004)*Number(creditDays);
				//最低销售价=采购价+运费+仓储费+采购价*日息万分之四*赊销时长
				//var min_price = Number(other_dealPrice)+Number(transportCost_price)+Number(warehouseCost_price)+value;
				//var min_price = Number((other_dealPrice/1.13+transportCost_price/1.09+warehouseCost_price/1.06+other_dealPrice*0.0004*Number(creditDays)))*Number(1.13);
				
				var productCd = rows[i].productCd;
				var _interest = Number(0.0004);
				$.ajax({
		 			  url: _ctx+"/ctr/product/getProductConfig?productCd="+productCd,
		 			  type:"post",
		 			  async: false,
		 			  success:function(data){
			 			  if (data){
			 				 _interest = Number(data.interest);
				 		  }
		 			  }
		 		});
				var a = Number(other_dealPrice)/1.13+Number(transportCost_price)/(1.06*1.08)+Number(warehouseCost_price)/(1.09*1.05);
				var min_price = Number(a)*1.13 + Number(other_dealPrice)*_interest*Number(creditDays);
				if (!min_price){
					min_price = other_dealPrice;
				}
				
				if(_ed_minDealPrice){
					$(_ed_minDealPrice.target).numberbox('setValue',min_price);
					//$(_ed_dealPrice.target).numberbox({min:min_price});
				}
			}
		}		
	}
}

//获取销售价
function getDealPrice(randomNum){
	if (processCode != "APPLY_MATCH_IOUS"){
		return;
	}
	var rows = $('#relaTable'+randomNum).datagrid('getRows');
	for(var i=0; i<rows.length; i++){
		var _ed_minDealPrice =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'minDealPrice'});
		var _ed_premium =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'premium'});
		var _ed_dealPrice =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'dealPrice'});
		var _ed_taxPrice =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'taxPrice'});
		var buy_price = rows[i].otherDealPrice;
		if(_ed_minDealPrice&&_ed_minDealPrice){
			var min_dealPrice = Number($(_ed_minDealPrice.target).numberbox('textbox').val());
			var premium = $(_ed_premium.target).numberbox('textbox').val();
			var productCd = rows[i].productCd;
			if (premium != "" && premium != null && premium != undefined){
				//销售价 =(采购价+运费+仓储费+(约定结算日-送货日)*采购价*0.0004+加价)*(1+保费比率)
				//var value = Number(getDays(randomNum))*Number(buy_price)*Number(0.0004)+Number(premium);
				//var deal_price = (Number(buy_price)+Number(transportCost_price)+Number(warehouseCost_price)+value)*(1.12);
				var insuranceRate = 1.0012;
		 		$.ajax({
	 			  url: _ctx+"/ctr/product/getProductConfig?productCd="+productCd,
	 			  type:"post",
	 			  async: false,
	 			  success:function(data){
		 			  if (data){
		 				 insuranceRate = Number(1+data.insuranceRate);
			 		  }
	 			  }
	 			});
		 		premium = Number(premium);
		 		var deal_price = Number(min_dealPrice+premium)*Number(insuranceRate);
				if (deal_price){
					$(_ed_dealPrice.target).numberbox('setValue',deal_price);
					$(_ed_taxPrice.target).numberbox('setValue',deal_price/1.13);
				}
			}
		}		
	}
	getTotalAmount(randomNum);
}

//获取销售价
function getPremium(randomNum){
	if (processCode != "APPLY_MATCH_IOUS"){
		return;
	}
	var rows = $('#relaTable'+randomNum).datagrid('getRows');
	for(var i=0; i<rows.length; i++){
		var _ed_minDealPrice =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'minDealPrice'});
		var _ed_premium =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'premium'});
		var _ed_dealPrice =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'dealPrice'});
		var _ed_taxPrice =  $('#relaTable'+randomNum).datagrid('getEditor', {index:i,field:'taxPrice'});
		var buy_price = rows[i].otherDealPrice;
		if(_ed_minDealPrice&&_ed_minDealPrice){
			var min_dealPrice = Number($(_ed_minDealPrice.target).numberbox('textbox').val());
			var dealPrice = Number($(_ed_dealPrice.target).numberbox('textbox').val());
			var premium = $(_ed_premium.target).numberbox('textbox').val();
			var productCd = rows[i].productCd;
			if (!premium){
				premium = 0;
			}
			var insuranceRate = 1.0012;
	 		$.ajax({
 			  url: _ctx+"/ctr/product/getProductConfig?productCd="+productCd,
 			  type:"post",
 			  async: false,
 			  success:function(data){
	 			  if (data){
	 				 insuranceRate = Number(1+data.insuranceRate);
		 		  }
 			  }
 			});
	 		premium = Number(premium);
	 		var premium_price = Number(dealPrice/insuranceRate)-Number(min_dealPrice); 
			if (_ed_premium){
				$(_ed_premium.target).numberbox('setValue',premium_price);
			}
		}		
	}
}

function getTotalAmount(randomNum){
	var processCode = $("#processCode").val();
	if (processCode != "APPLY_MATCH_IOUS"){
		return;
	}
	var _datagrid = $('#relaTable'+randomNum);
	var rows = $('#relaTable'+randomNum).datagrid('getRows');
	var totalAmount = 0;
	var _totalPrice=0;
	for(var i=0; i<rows.length; i++){
		var _ed_dealPrice =  _datagrid.datagrid('getEditor', {index:i,field:'dealPrice'});
		var _ed_dealNumber =  _datagrid.datagrid('getEditor', {index:i,field:'dealNumber'});
		var _ed_totalPrice=  _datagrid.datagrid('getEditor', {index:i,field:'totalPrice'});
		if(_ed_dealPrice){
			 _dealPrice =$(_ed_dealPrice.target).numberbox('textbox').val();
			 _dealNumber =$(_ed_dealNumber.target).numberbox('textbox').val();
			 _totalPrice = _dealPrice*_dealNumber;
			 $(_ed_totalPrice.target).numberbox('setValue',_totalPrice);
		}		
		totalAmount = Number(totalAmount) + Number(_totalPrice);
	}
	$("#totalAmount"+randomNum).numberbox('setValue', totalAmount);
}

function getDays(randomNum){
	var payFullTime = $('#receiveFullTime'+randomNum).datebox('getValue');
	var arrivalTime = $('#arrivalTime'+randomNum).datebox('getValue');
	if (!payFullTime){
		return;
	}
	if (!arrivalTime){
		return;
	}
	var payFull_time = strDateForDate(payFullTime);
	var arrival_time = strDateForDate(arrivalTime);
	var days = (payFull_time-arrival_time)/86400000;
	if (days == null || days < 0){
		days = 0;
	}
	return days;
}

function strDateForDate(str){
	var strDate = str.split(" ");
	var strDatepart = strDate[0].split("-");
	var dtDate = new Date(strDatepart[0],strDatepart[1]-1,strDatepart[2]);
	return dtDate;
}

function addDate(date, days) {
	var d = new Date(date);
	var time = new Date(d.getTime()+days*24*3600000);
	var a = time.getFullYear();
	var b = time.getMonth() + 1;
	var c = time.getDate();
	return a+'-'+b+'-'+c;
}