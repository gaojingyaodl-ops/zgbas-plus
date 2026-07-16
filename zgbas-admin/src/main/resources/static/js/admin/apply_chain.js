var Chain = {
    initChainTable: function (applyMatchId) {
        var _chain_columns;
        var _curStatus = $("#entityStatus").val();
        var _chain_url = _ctx + "/apply/matchChain/list?sort=serialNumber&order=asc&search_EQL_applyMatchId=" + applyMatchId;
        if (applyMatchId && applyMatchId > 0) {
            _chain_columns = [[
                {
                    field: 'chainCompanyName',
                    title: '企业名称',
                    width: 220,
                    align: 'left'
                },
                {
                    field: 'buyDealPrice',
                    title: '采购单价(元)',
                    align: 'center',
                    width: 120
                },
                {
                    field: 'sellDealPrice',
                    title: '销售单价(元)',
                    align: 'center',
                    width: 120
                },
                {
                    field: 'serialNumber',
                    title: '序号',
                    align: 'center',
                    width: 80
                },
                {
                    field: 'remark',
                    title: '备注',
                    align: 'left',
                    width: 250
                }
            ]];
        } else {
            _chain_columns = [[
                {
                    field: 'chainCompanyName',
                    title: '企业名称',
                    width: 300,
                    align: 'left',
                    editor: {
                        type: 'combobox',
                        options: {
                            required: true,
                            panelWidth: 280,
                            panelHeight: 'auto',
                            data: _dcsxOurCompanyNameJson,
                            textField: 'companyName',
                            valueField: 'companyName',
                            onHidePanel: function () {
                                BasCombobox.onHidePanel(this);
                            }
                        }
                    }
                },
                {
                    field: 'buyDealPrice',
                    title: '采购单价(元)',
                    align: 'center',
                    width: 160,
                    editor: {
                        type: 'numberbox',
                        options: {
                            required: 'true',
                            validType: 'length[1,20]',
                            precision: 3
                        }
                    }
                },
                {
                    field: 'sellDealPrice',
                    title: '销售单价(元)',
                    align: 'center',
                    width: 160,
                    editor: {
                        type: 'numberbox',
                        options: {
                            required: 'true',
                            validType: 'length[1,20]',
                            precision: 3
                        }
                    }
                },
                {
                    field: 'serialNumber',
                    title: '序号',
                    align: 'center',
                    width: 120,
                    editor: {
                        type: 'numberbox',
                        options: {
                            required: 'true',
                            validType: 'length[1,20]',
                            precision: 0
                        }
                    }
                },
                {
                    field: 'remark',
                    title: '备注',
                    align: 'left',
                    width: 300,
                    editor: {
                        type: 'textbox'
                    }
                },
                {
                    field: 'option',
                    title: '',
                    width: 80,
                    hidden: false,
                    align: 'center',
                    formatter: function (value, row, index) {
                        var str = '';
                        if (index == 0) {
                            str = '<i class="icon-add icon cursor operator mt4" style="margin-right: 26px;" onclick="Chain.insertChainRow()"></i>'
                        } else {
                            str = '<i class="icon-add icon cursor operator mt4" onclick="Chain.insertChainRow()"></i><i class="icon-remove ml10 icon cursor mt4" onclick="Chain.removeChainRow(\'' + row.id + '\',\'' + index + '\',event,this)"></i>'
                        }
                        return str;
                    }
                }
            ]];
        }
        $('#chainTable').datagrid({
            url: _chain_url,
            singleSelect: true,
            rownumbers: true,
            columns: _chain_columns,
            onDblClickRow: function (index, row, field) {
                if (_curStatus == 'N' || _curStatus == 'B' || _curStatus == 'C') {
                    //进入可编辑状态
                    $(this).datagrid('endEdit', _chain_last_index);
                    $(this).datagrid('beginEdit', index);
                    lastIndex = index;
                    Convert.focusEditor("chainTable", field, _chain_last_index);
                }
            },
            onClickCell: function (index, field) {
                if (_curStatus == 'N' || _curStatus == 'B' || _curStatus == 'C') {
                    if (_chain_last_index != index) {
                        $(this).datagrid('endEdit', _chain_last_index);
                        $(this).datagrid('beginEdit', index);
                    }
                    _chain_last_index = index;
                    Convert.focusEditor('chainTable', field, _chain_last_index);
                }
            },
            onClickRow: function (index, row) {
                _chain_last_index = index;
            },
            onLoadSuccess: function (data) {
                if(_curStatus && _curStatus=='N'||_curStatus=='B'){
                    if(data.total==0){
                        var new_row = {id: 0, serialNumber: _chain_last_index + 1, buyDealPrice: Chain.getChainPrice()};
                        $(this).datagrid('appendRow', new_row);
                        var rowIndex = $(this).datagrid('getRowIndex', new_row);
                        _chain_last_index = rowIndex;
                        $(this).datagrid('beginEdit', rowIndex);
                        $(this).datagrid('selectRow', rowIndex);
                    }
                }
            }
        });
    },
    insertChainRow: function () {
        var _$table = $('#chainTable');
        var changeRows = _$table.datagrid('getRows');
        if (changeRows.length > 9) {
            $.messager.alert('提示', '最多只能添加10条数据！', 'info');
            return;
        }
        if (!_chain_last_index) {
            _chain_last_index = 1;
        }else{
            _chain_last_index = _chain_last_index + 1;
        }
        var operators = $('.operator')
        for (var i = 0; i < operators.length; i++) {
            $(operators[i]).removeClass('icon-add').addClass('icon-add-no').attr('onClick', '');
            if ($(operators[i]).next()) {
                $(operators[i]).next().removeClass('icon-remove').attr('onClick', '');
            }
        }
        _$table.datagrid('endEdit', _chain_last_index );
        _$table.datagrid('selectRow', _chain_last_index);
        var field = {id: 0, serialNumber: changeRows.length + 1, buyDealPrice: Chain.getChainPrice()};
        _$table.datagrid('appendRow', field);
        _chain_last_index = _$table.datagrid('getRows').length - 1;
        _$table.datagrid('beginEdit', _chain_last_index);
        Convert.focusEditor("chainTable", field, _chain_last_index);
    },
    getChainPrice:function(){
        var chainPrice;
        var buyPrice = $("#bdealPrice").numberbox("getValue");
        if (buyPrice){
            var _$table = $('#chainTable');
            var allRows = _$table.datagrid('getRows');
            if (allRows.length >= 1){
                var row = _$table.datagrid('getSelected');
                var _index = _$table.datagrid('getRowIndex', row);
                var chain_target = _$table.datagrid("getEditor",{index:_index ,field:"buyDealPrice"});
                var before_chain_target = _$table.datagrid("getEditor",{index:_index ,field:"sellDealPrice"});
                if (chain_target){
                   var _dealPrice = $(chain_target.target).numberbox('getValue');
                    chainPrice = Number(_dealPrice * 1.002);
                }
                if (before_chain_target){
                    $(before_chain_target.target).numberbox('setValue', Number(_dealPrice * 1.002));
                }
            }else{
                chainPrice = Number(buyPrice * 1.002);
            }
        }
        return chainPrice;
    },
    removeChainRow: function (id, _index, event, target) {
        var _$table = $('#chainTable');
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
        var operators = $('.operator');
        $(operators[operators.length - 1]).removeClass('icon-add-no').addClass('icon-add').attr('onClick', 'Chain.insertChainRow()');
        if (operators.length > 1) {
            $(operators[operators.length - 1]).next().addClass('icon-remove').attr('onClick', 'Chain.removeChainRow(\'' + 0 + '\',\'' + (_index - 1) + '\',event)');
        }
        event.stopPropagation();
    },
    removeAllRows: function () {
        var _$table = $('#chainTable');
        var allRows = _$table.datagrid('getRows');
        for (var i = 0; i < allRows.length; i++) {
            _$table.datagrid('deleteRow', i);
        }
    }
}