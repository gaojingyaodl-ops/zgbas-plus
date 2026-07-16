var applyProtocolDocRepayment = {
    initRepaymentView: function (docType) {
        if (docType == "RA") {
            var url = _ctx + "/apply/protocolDocument/addProtocolDocCkhDetail";
            $.post(url, {docType: docType, curNumber: 0, id: entityId}, function (html) {
                $("#repaymentDiv").html(html);
                applyProtocolDocRepayment.initRepaymentMode();
                applyProtocolDocRepayment.dealWithOtherMode();
                applyProtocolDocRepayment.initRepTable();
                if (!entityId || entityId == 0){
                    setTimeout(function () {
                        applyProtocolDocRepayment.insertRepTableRow();
                    }, 200);
                }
                applyProtocolDocRepayment.editRepTableRow();
            });
        } else {
            $("#repaymentDiv").html("");
        }
    },
    initRepTable: function () {
        var _repTable_columns;
        var _repTableDetailStr = $("#repaymentDetailListStr").val();
        var _repTable_data = _repTableDetailStr ? JSON.parse(_repTableDetailStr) : [];
        if ( !(_curStatus == 'N' || _curStatus == 'B' || _curStatus == 'C' || _curStatus == 'E')) {
            _repTable_columns = [[
                {
                    field: 'contractNo',
                    title: '合同编号',
                    width: 150,
                    align: 'left'
                },
                {
                    field: 'totalAmount',
                    title: '合同金额(元)',
                    align: 'center',
                    width: 130
                },
                {
                    field: 'dealedAmount',
                    title: '已回款金额(元)',
                    align: 'center',
                    width: 130
                },
                {
                    field: 'needReceiveAmount',
                    title: '待回款金额(元)',
                    align: 'center',
                    width: 130
                },
                {
                    field: 'breachAmount',
                    title: '待收逾期罚息(元)',
                    align: 'center',
                    width: 130
                }
            ]];
        } else {
            _repTable_columns = [[
                {
                    field: 'contractNo',
                    title: '合同编号',
                    width: 150,
                    align: 'center',
                    editor: {
                        type: 'textbox',
                        options: {
                            required: 'true'
                        }
                    }
                },
                {
                    field: 'totalAmount',
                    title: '合同金额(元)',
                    align: 'center',
                    width: 130,
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
                    field: 'dealedAmount',
                    title: '已回款金额(元)',
                    align: 'center',
                    width: 130,
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
                    field: 'needReceiveAmount',
                    title: '待回款金额(元)',
                    align: 'center',
                    width: 130,
                    editor: {
                        type: 'numberbox',
                        options: {
                            required: 'true',
                            validType: 'length[1,20]',
                            precision: 3,
                            onChange: function (newVal,oldVal){
                                applyProtocolDocRepayment.getAgreementAmountOnChange()
                            }
                        }
                    }
                },
                {
                    field: 'breachAmount',
                    title: '待收逾期罚息(元)',
                    align: 'center',
                    width: 130,
                    editor: {
                        type: 'numberbox',
                        options: {
                            required: 'true',
                            validType: 'length[1,20]',
                            precision: 3,
                            onChange: function (newVal,oldVal){
                                applyProtocolDocRepayment.getAgreementAmountOnChange()
                            }
                        }
                    }
                },
                {
                    field: 'option2',
                    title: '',
                    width: 120,
                    hidden: false,
                    align: 'center',
                    formatter: function (value, row, index) {
                        var button = '<a href="#" class="easyui-linkbutton-self" onclick="applyProtocolDocRepayment.syncContractDetail(' + index + ')">同步合同信息</a>';
                        // 解析特定的按钮样式
                        setTimeout(function() {
                            $.parser.parse($(button));
                        }, 200);
                        return button;
                    }
                }
            ]];
        }

        $('#repTable').datagrid({
            data: _repTable_data,
            singleSelect: true,
            rownumbers: true,
            columns: _repTable_columns,
            onDblClickRow: function (index, row, field) {
                if (_curStatus == 'N' || _curStatus == 'B' || _curStatus == 'C') {
                    //进入可编辑状态
                    $(this).datagrid('endEdit', _repTable_last_index);
                    $(this).datagrid('beginEdit', index);
                    Convert.focusEditor("repTable", field, _repTable_last_index);
                }
            },
            onClickCell: function (index, field) {
                if (_curStatus == 'N' || _curStatus == 'B' || _curStatus == 'C') {
                    if (_repTable_last_index != index) {
                        $(this).datagrid('endEdit', _repTable_last_index);
                        $(this).datagrid('beginEdit', index);
                    }
                    _repTable_last_index = index;
                    Convert.focusEditor('repTable', field, _repTable_last_index);
                }
            },
            onClickRow: function (index, row) {
                _repTable_last_index = index;
            },
            onLoadSuccess: function (data) {
                if (!data){
                    applyProtocolDocRepayment.insertRepTableRow();
                }
            },
        });
    },
    insertRepTableRow: function () {
        var _$table = $('#repTable');
        var changeRows = _$table.datagrid('getRows');
        if (changeRows.length > 9) {
            $.messager.alert('提示', '最多只能添加10条数据！', 'info');
            return;
        }
        var field = {
            contractNo:"",
            totalAmount:"",
            dealedAmount:"",
            needReceiveAmount:"",
            breachAmount:""
        }
        _$table.datagrid('appendRow', field);
        _repTable_last_index = _$table.datagrid('getRows').length - 1;
        _$table.datagrid('beginEdit', _repTable_last_index);
        Convert.focusEditor("repTable", field, _repTable_last_index);
    },
    removeRepTableRow: function () {
        var _$table = $('#repTable');
        var row = _$table.datagrid('getSelected');
        if(!row){
            $.messager.alert('提示','请先选中一条进行删除！','info');
            return;
        }
        var index = _$table.datagrid('getRowIndex', row);
        var data = _$table.datagrid('getRows');
        if(data.length>1){
            if (!row.contractNo) {
                _$table.datagrid('deleteRow', index);
                applyProtocolDocRepayment.getAgreementAmountOnChange()
            } else {
                $.messager.confirm('提示', '确认要删除该记录吗?', function (t) {
                    if (t) {
                        _$table.datagrid('deleteRow', index);
                        // 更新债务金额
                        applyProtocolDocRepayment.getAgreementDate()
                        applyProtocolDocRepayment.getAgreementAmountOnChange()
                    }
                });
            }
        }
    },
    initRepaymentMode() {
        $('.easyui-linkbutton').linkbutton();
        var _entityStatus = $("#entityStatus").val();
        var _editflg = (_entityStatus == "A" || _entityStatus == "D");
        if(_editflg) {
            $("#addTable,#removeTable").hide()
        }
        $("#ourCompanyName,#targetCompanyName,#agreementAmountCN").textbox({
            readonly: true
        });
        $("#ourCompanyNo,#targetCompanyNameNo").textbox({
            required: true,
            readonly: _editflg
        });
        $("#party,#partyPhone,#partyCardNo").textbox({
            required: false,
            readonly: _editflg
        });
        $("#signCompanyName").combobox({required: true, readonly: _editflg});
        $("#agreementDate,#cutOffDate").datebox({required: true, readonly: _editflg});
        // 新增的时候截止日期默认是当前
        if(_entityStatus == 'N'){
            var currentDate = new Date();
            var formattedDate = currentDate.toISOString().split('T')[0]; // 格式化为 YYYY-MM-DD
            $("#cutOffDate").datebox('setValue', formattedDate);
        }
        $("#agreementAmount").numberbox({
            min: 0,
            precision: 4,
            readonly: true,
            parser: function (val) {
                // 解析时去除千分位分隔符
                if (val) {
                    return parseFloat(String(val).replace(/,/g, ''));
                } else {
                    if (isNaN(val)) {
                        return '';
                    } else {
                        return val;
                    }
                }
            },
            formatter: function (val) {
                if (val || val === 0) {
                    val = parseFloat(val).toFixed(2).toString().split(".");
                    val[0] = val[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");
                    return val.join(".");
                } else {
                    if (isNaN(val)) {
                        return ""
                    } else {
                        return val;
                    }
                }
            }
        });
    },
    editRepTableRow(){
        if (_curStatus == 'N' || _curStatus == 'B' || _curStatus == 'C' || _curStatus == 'E'){
            var _tab = $("#repTable").datagrid("getRows");
            var _len = _tab.length;
            for (var i = 0; i < _len; i++) {
                var rowIndex = $("#repTable").datagrid('getRowIndex', _tab[i]);
                $("#repTable").datagrid('beginEdit', rowIndex);
            }
        }
    },
    verifyRepTableRow(){
        try {
            var _tab = $("#repTable").datagrid("getRows");
            var _len = _tab.length;
            let flg =true
            for (var i = 0; i < _len; i++) {
                var rowIndex = $("#repTable").datagrid('getRowIndex', _tab[i]);
                var flag = $("#repTable").datagrid('validateRow', rowIndex);
                if (!flag) {
                    $("#repTable").datagrid('selectRow', rowIndex);
                    $("#repTable").datagrid('beginEdit', rowIndex);
                    flg=false
                    break;
                }
            }
            _exist_repTable = flg;
        }catch (err){
            _exist_repTable = false
            console.log("verifyRepTableRow error");
        }
    },
    setDetailListValue(){
        applyProtocolDocRepayment.verifyRepTableRow();
        if (_exist_repTable){
            var _tab = $("#repTable").datagrid("getRows");
            for (let i = 0; i < _tab.length; i++) {
                $("#repTable").datagrid('endEdit',i)
            }
            $("#repaymentDetailListStr").val(JSON.stringify(_tab));
        }
    },
    dealWithOtherMode() {
        $("#overdueLateFeesSum").validatebox({required: false});
        $("#companyName").textbox({required: false});
        $("#signCompanyName").combobox({required: true});
        $("#accountName").combobox({required: false});
        $("#bankName").textbox({required: false});
        $("#bankAccount").textbox({required: false});
        $("#endDate").datebox({required: false});
        $("#paymentAccount").textbox({required: false});
    },
    syncContractDetail(index) {
        var ed = $('#repTable').datagrid('getEditor', {index: index, field: 'contractNo'});
        var contractNo = $(ed.target).val();
        // $("#repTable").datagrid('beginEdit', index);
        if (!contractNo) {
            debugger
            $.messager.alert('提示', '合同编号不可为空!', 'warning', function() {
            });
            return;
        }
        var _cutOffDate = $("#cutOffDate").val();
        if (!_cutOffDate) {
            $.messager.alert('提示','截止日期不可为空!','warning');
            return;
        }
        var _ourCompanyName = $('#ourCompanyName').textbox('getValue');
        var _targetCompanyName = $('#targetCompanyName').textbox('getValue');
        var _agreementDate = $("#agreementDate").datebox('getValue')
        $.ajax({
            url: _ctx + "/ctr/contract/findDetailByContractNo?contractNo=" + contractNo + "&endDate=" + _cutOffDate,
            type: "post",
            async: false,
            success: function (contract) {
                if (contract != '' && contract != null && contract != undefined) {
                    var ourCompanyName = contract.ourCompanyName;
                    var targetCompanyName = contract.companyName;
                    var ourCompanyNo = contract.ourCompanyNo;
                    var companyNo = contract.companyNo;
                    var agreementDate = contract.contractTime;
                    // 甲乙方信息
                    if(!_ourCompanyName||!_targetCompanyName){
                        $('#ourCompanyName').textbox('setValue',ourCompanyName);
                        $('#targetCompanyName').textbox('setValue',targetCompanyName);
                        $('#ourCompanyNo').textbox('setValue',ourCompanyNo);
                        $('#targetCompanyNameNo').textbox('setValue',companyNo);
                        $("#agreementDate").datebox('setValue',agreementDate)
                        // 假设 'contract' 是一个包含合同详细信息的对象
                        $('#repTable').datagrid('updateRow', {
                            index: index,
                            row: {
                                contractNo: contract.contractNo,
                                totalAmount: contract.totalAmount.toFixed(3),
                                dealedAmount: contract.dealedAmount.toFixed(3),
                                needReceiveAmount: (contract.totalAmount.toFixed(3) - contract.dealedAmount.toFixed(3)).toFixed(3),
                                breachAmount: (contract.breachAmount.toFixed(3) - contract.receiveBreachAmount.toFixed(3)).toFixed(3),
                                agreementDate: contract.contractTime
                            }
                        });
                        $('#repTable').datagrid('refreshRow', index); // 刷新表格行以反映更改
                        $("#repTable").datagrid('beginEdit', index);
                        // 求债务金额
                        applyProtocolDocRepayment.getAgreementDate();
                        applyProtocolDocRepayment.getAgreementAmountOnChange();
                    } else {
                        if(ourCompanyName!=_ourCompanyName||targetCompanyName!=_targetCompanyName){
                            $.messager.alert('提示', '甲乙方信息不统一！', 'info');
                        } else {
                            // 假设 'contract' 是一个包含合同详细信息的对象
                            $('#repTable').datagrid('updateRow', {
                                index: index,
                                row: {
                                    contractNo: contract.contractNo,
                                    totalAmount: contract.totalAmount.toFixed(3),
                                    dealedAmount: contract.dealedAmount.toFixed(3),
                                    needReceiveAmount: (contract.totalAmount.toFixed(3) - contract.dealedAmount.toFixed(3)).toFixed(3),
                                    breachAmount: (contract.breachAmount.toFixed(3) - contract.receiveBreachAmount.toFixed(3)).toFixed(3),
                                    agreementDate: contract.contractTime
                                }
                            });
                            $('#repTable').datagrid('refreshRow', index); // 刷新表格行以反映更改
                            $("#repTable").datagrid('beginEdit', index);
                            // 求债务金额
                            applyProtocolDocRepayment.getAgreementDate();
                            applyProtocolDocRepayment.getAgreementAmountOnChange();
                        }
                    }
                } else {
                    $("#repTable").datagrid('beginEdit', index);
                    $.messager.alert('提示', '未找到合同详情！', 'info');
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
    },
    getAgreementDate(){
        var rows = $('#repTable').datagrid('getRows');
        // 针对债务日期 取最小日期
        const agreementDateArr = rows
            .map(row => row.agreementDate)
            .filter(date => date)
            .map(date => new Date(date))
        if(agreementDateArr.length>0){
            const agreementDate = agreementDateArr.reduce((minDate, current) => current < minDate ? current : minDate)
            $("#agreementDate").datebox('setValue',agreementDate.toISOString().split('T')[0])
        }
    },
    getAgreementAmountOnChange(){
        var agreementAmountSum = 0;
        var needReceiveAmountSum = 0;
        var breachAmountSum = 0;
        let rows = $('#repTable').datagrid('getRows');
        $.each(rows, function(index, row) {
            var editor1 = $('#repTable').datagrid('getEditor', {index: index, field: 'needReceiveAmount'});
            if (editor1) {
                var needReceiveAmount = $(editor1.target).numberbox('getValue');
                // 对 breachAmount 进行处理
                needReceiveAmountSum += parseFloat(needReceiveAmount) || 0;
            }
            var editor = $('#repTable').datagrid('getEditor', {index: index, field: 'breachAmount'});
            if (editor) {
                var breachAmount = $(editor.target).numberbox('getValue');
                // 对 breachAmount 进行处理
                breachAmountSum += parseFloat(breachAmount) || 0;
            }
            agreementAmountSum= needReceiveAmountSum+breachAmountSum
            $('#agreementAmount').numberbox('setValue',agreementAmountSum);
            $('#agreementAmountCN').textbox('setValue',applyProtocolDocRepayment.convertToChineseUpperCase(agreementAmountSum))
        });
    },
    convertToChineseUpperCase(n) {
        // 定义数字及其对应的大写字符
        var fraction = ['角', '分'];
        var digit = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
        var unit = [
            ['元', '万', '亿'],
            ['', '拾', '佰', '仟']
        ];
        var head = n < 0 ? '欠' : '';
        n = Math.abs(n);

        var s = '';

        for (var i = 0; i < fraction.length; i++) {
            s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '');
        }
        s = s || '整';
        n = Math.floor(n);

        for (var i = 0; i < unit[0].length && n > 0; i++) {
            var p = '';
            for (var j = 0; j < unit[1].length && n > 0; j++) {
                p = digit[n % 10] + unit[1][j] + p;
                n = Math.floor(n / 10);
            }
            s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;
        }
        return head + s.replace(/(零.)*零元/, '元').replace(/(零.)+/g, '零').replace(/^整$/, '零元整');
    }
}