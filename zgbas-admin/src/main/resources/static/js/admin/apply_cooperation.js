var Cooperation = {
    initComponent: function (readonly) {
        // 赊销模式
        $('#contractModel').combobox({
            data: [{id: 'DCSX', text: '普通模式'}, {id: 'DCSXBL', text: '保理模式'}, {
                id: 'DCSXHDFK',
                text: '货到付款'
            }],
            valueField: 'id',
            textField: 'text',
            panelHeight: "auto",
            editable: false,
            required: true,
            readonly: readonly
        });

        // 合作模式
        $('#cooperationMode').combobox({
            data: _cooperationModeJson,
            valueField: 'dictCd',
            textField: 'dictName',
            panelHeight: "auto",
            editable: false,
            required: true,
            readonly: readonly,
            onChange: function (value) {
                Cooperation.changeCooperationMode(value);
            }
        });

        // 合作业务员
        $('#cooperationUserName').combotree({
            data: _matchUserNameTree,
            multiple: false,
            editable: true,
            required: true,
            readonly: readonly,
            formatter: function (node) {
                var enableFlg = node.attributes.enableFlg;
                if (enableFlg == false) {
                    return '<span style="color:red;">' + node.text + '(无效)</span>';
                }
                return node.text;
            },
            onSelect: function (node) {
                if (node.children.length != 0) {
                    $('#cooperationUserName').combotree('clear');
                } else {
                    var _cooperationMode = $("#cooperationMode").combotree("getValue");
                    Cooperation.changeCooperationUser(node, _cooperationMode);
                }
            }
        });

        //牌照
        $("#brandNumber").combobox({
            data: "",
            required: true,
            limitToList: true,
            editable: true,
            textField: 'brandNumber',
            valueField: 'brandNumber',
            readonly: readonly
        });

        // 品种
        $("#productCd").combotree({
            data: _productTypeJson,
            required: true,
            editable: true,
            panelWidth: 150,
            panelHeight: 300,
            readonly: readonly,
            onSelect: function (node) {
                var text = node.text;
                $("#productName").val(text);
                var new_brandJson = _brandJson.filter(function (e) {
                    return e.productCd == node.id && e.enterpriseId == 44;
                })
                //初始化牌号
                $("#brandNumber").combobox("loadData", new_brandJson);
            }
        });

        // 数量
        $("#dealNumber").numberbox({
            required: true,
            validType: 'length[1,20]',
            precision: 4,
            readonly: readonly,
            onChange: function (value) {
                Cooperation.formatterBuy(null);
                Cooperation.formatterSell(null);
                if (buyShowFlg && sellShowFlg) {
                    MinPrice.setMinSellPriceDcsx(false);
                }
            }
        });

        // 厂商
        $("#factoryName").combobox({
            data: _factoryJson,
            required: true,
            textField: 'factoryName',
            valueField: 'factoryName',
            panelHeight: '100',
            readonly: readonly,
            onSelect: function (node) {
                var id = node.id;
                $("#factoryId").val(id);
            }
        });

        // 质量标准
        $("#qualityStandard").combobox({
            data: _qualityStandardJson,
            required: true,
            textField: 'dictName',
            valueField: 'dictCd',
            panelHeight: 'auto',
            readonly: readonly
        });

        // 包装规格
        $("#wrapSpecs").combobox({
            data: _packingSpecificaJson,
            required: true,
            textField: 'dictName',
            valueField: 'dictCd',
            panelHeight: 'auto',
            readonly: readonly
        });
    },
    initBuyComponent(required,readonly) {
        // 采购来源
        $("#buySource").combobox({
            data: _buySourceJson,
            required: true,
            textField: 'dictName',
            valueField: 'dictCd',
            readonly: true,
            hasDownArrow: false,
            onLoadSuccess: function (data) {
                var value = $(this).val();
                if (!value) {
                    $(this).combobox('setValue', 'G');
                }
            }
        });

        // 采购业务员
        $('#buyMatchUserName').combotree({
            data: _matchUserNameTree,
            multiple: false,
            editable: true,
            required: true,
            readonly: true,
            formatter: function (node) {
                if (node.attributes.enableFlg === false) {
                    return '<span style="color:red;">' + node.text + '(无效)</span>';
                }
                return node.text;
            },
            onSelect: function (node) {
                if (node.children.length != 0) {
                    $('#buyMatchUserName').combotree('clear');
                } else {
                    var value = node.id;
                    var value1 = value.replace('user', '');
                    $("#buyMatchUserId").val(value1);
                    $('#buyMatchUserName').combotree('setValue', node.text);
                }
            }
        });

        $("#provinceNames").combobox({
            url: _ctx + "/bs/areaCost/ajaxGetCity?id=1",
            valueField: "id",
            textField: "name",
            width: 150,
            height: 30,
            readonly: readonly,
        });
        $("#cityNames").combobox({
            url: _ctx + "/bs/areaCost/ajaxGetCity?id=0",
            valueField: "id",
            textField: "name",
            width: 150,
            height: 30,
            readonly: readonly,
        });
        $("#areaCodes").combobox({
            url: _ctx + "/bs/areaCost/ajaxGetCity?id=0",
            valueField: "id",
            textField: "name",
            width: 150,
            height: 30,
            readonly: readonly,
        });
        $('#deliveryAddr').textbox({required:false});

        $("#provinceNames").combobox({
            url: _ctx + "/bs/areaCost/ajaxGetCity?id=1",
            valueField: "id",
            textField: "name",
            width: 150,
            height: 30,
            onSelect: function (record) {
                $("#cityNames").combobox({
                    url: _ctx + "/bs/areaCost/ajaxGetCity?id=" + record.id,
                    valueField: "id",
                    textField: "name",
                    width: 150,
                    height: 30,
                    onSelect: function (record) {
                        if (_deliveryAddrFlg) {
                            return;
                        }
                        $("#areaCodes").combobox({
                            url: _ctx + "/bs/areaCost/ajaxGetCity?id=" + record.id,
                            valueField: "id",
                            textField: "name",
                            width: 150,
                            height: 30,
                            onSelect: function (record) {
                                $("#areaCodesUtil").text("/" + record.name);
                                areaCodeUtil = $("#areaCodesUtil").text();
                                console.log(record.name)
                                if (record.name != "") {
                                    var tt = $("#provinceNamesUtil").text();
                                    var cc = $("#cityNamesUtil").text();
                                    $("#deliveryAddr").textbox("setValue", tt + cc + areaCodeUtil);
                                }
                            }
                        });
                        $("#cityNamesUtil").text("/" + record.name);
                        cityNameUtilUtil = $("#cityNamesUtil").text();
                        var tt = $("#provinceNamesUtil").text();
                        $("#deliveryAddr").textbox("setValue", tt + cityNameUtilUtil);
                    }
                });
                $("#provinceNamesUtil").text(record.name);
                $("#deliveryAddr").textbox("setValue", "");
                $("#deliveryAddr").textbox("setValue", record.name);
            }
        });
        // 供应商
        $('#buyCompanyName').combobox({
            url: _ctx + "/bs/company/listMyCompany/" + 0 + "/" + 1,
            mode: 'remote',
            limitToList: true,
            valueField: 'companyName',
            textField: 'text',
            panelHeight: 300,
            required: true,
            readonly: readonly,
            formatter: function (row) {
                var text = row.text;
                var myFlag = row.myFlag;
                if (myFlag != true) {
                    text = '<span style="color:#ccc">' + text + '</span>';
                }
                return text + "[剩余额度:" + (Number(row.supplierPurchaseAmount) - Number(row.usedSupplierPurchaseAmount)) + "]";
            },
            onSelect: function (node) {
                _supplierFuture = node.supplierFuture == '1' ? false : true;
                _supplierDelivery = node.supplierDelivery == '1' ? true : false;

                var tt_deliverJson = _deliveryTypeJson;
                // 不能上游配送
                if (!_supplierDelivery) {
                    tt_deliverJson = _deliveryTypeJson.filter(function (a) {
                        return a.dictCd != 'P1';
                    });
                    $("#buyDeliveryType").combobox("clear");
                }
                $("#buyDeliveryType").combobox("loadData", tt_deliverJson);


                $("#supplierPrepayAmount").val(node.supplierPrepayAmount);
                $("#usedSupplierPrepayAmount").val(node.usedSupplierPrepayAmount);
                $('#buyCompanyId').val(node.id);
                _deliveryModeJson_ = _deliveryModeJson.filter(function (a) {
                    return a.dictCd == "XHHK" || a.dictCd == "XKHH";
                })
                // 预付款额度充足时，采购合同可以选择先款后货；
                // 不做限制
                // 预付款额度不足时，采购合同只能选择先货后款
                if (Number(node.supplierPrepayAmount) - Number(node.usedSupplierPrepayAmount) < Number($("#btotalAmount").numberbox("getValue"))) {
                    _deliveryModeJson_ = _deliveryModeJson.filter(function (a) {
                        return a.dictCd == "XHHK";
                    })
                    $('#deliveryModeB').combobox("clear");
                }
                $('#deliveryModeB').combobox('loadData', _deliveryModeJson_);

                $("#b_contactAddr").combobox({
                    url: _ctx + "/bas/warehouse/findByCompanyIdAddr/" + node.id,
                    valueField: 'warehouseAddr',
                    textField: 'warehouseAddr',
                    panelHeight: 300,
                    required:false,
                    readonly: readonly,
                    onLoadSuccess: function () {
                        _deliveryAddrFlg = true;
                        var data = $(this).combobox("getData");
                        if (data && data.length > 0) {
                            for (let i = 0; i < data.length; i++) {
                                if (data[i].defaultFlg == "true") {
                                    $(this).combobox("setValue", data[i].warehouseAddr);
                                    Cooperation.setSellAddrs(data[i].provinceCode, data[i].cityCode, data[i].areaCode);
                                    $("#deliveryAddr").textbox("setValue", data[i].warehouseName);
                                }
                            }
                        } else {
                            _deliveryAddrFlg = false;
                        }
                    },

                    onSelect: function (row) {
                        Cooperation.setSellAddrs(row.provinceCode, row.cityCode, row.areaCode);
                        $("#deliveryAddr").textbox('setValue', row.warehouseName);
                    }
                });
            }
        });

        // 采购需方
        $('#buyOurCompanyName').combobox({
            data: _dcsxOurCompanyNameJson,
            valueField: 'companyName',
            textField: 'companyName',
            panelHeight: "auto",
            editable: false,
            required: true,
            readonly: readonly,
        });

        // 仓储费、运输费、
        $("#bwarehouseCost,#btransportCost").numberbox({
            required: true,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 2,
            onChange: function (value) {
                if (buyShowFlg && sellShowFlg) {
                    MinPrice.setMinSellPriceDcsx(false, _oldBuyDealPrice);
                }
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
        });

        // 装卸费
        $("#bstevedorage").numberbox({
            required: true,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 2,
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
        });

        // 结算方式
        $('#deliveryModeB').combobox({
            data: _deliveryModeJson_,
            valueField: 'dictCd',
            textField: 'dictName',
            required: true,
            readonly: readonly,
            panelHeight: "auto",
            editable: false,
            onHidePanel: function () {
                BasCombobox.onHidePanel(this);
            }
        });

        // 支付方式
        $("#payType").combobox({
            data: _payTypeJson,
            textField: 'dictName',
            valueField: 'dictCd',
            panelHeight: "auto",
            required: true,
            readonly: readonly,
            editable: false,
            onHidePanel: function () {
                BasCombobox.onHidePanel(this);
            }
        });

        // 采购单价
        $("#bdealPrice").numberbox({
            required: true,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 2,
            onChange: function (value) {
                Cooperation.formatterBuy(value);
                if (buyShowFlg && sellShowFlg) {
                    MinPrice.setMinSellPriceDcsx(false, _oldBuyDealPrice);
                }
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
        });

        // 不含税采购单价
        $("#bdealAmountNotax").numberbox({
            required: true,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 2,
            editable: false,
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
        });

        // 采购总价
        $("#btotalAmount").numberbox({
            editable: false,
            precision: 2,
            onChange: function () {
                if (buyShowFlg && sellShowFlg) {
                    MinPrice.setGrossProfit(false);
                }
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
        });

        // 付全款日期、交货日期
        $("#payFullTime,#buyDeliveryDate").datebox({
            height: 30,
            required: true,
            readonly: readonly,
            editable: false
        });

        // 交货方式
        $("#buyDeliveryType").combobox({
            data: _deliveryTypeJson,
            valueField: 'dictCd',
            textField: 'dictName',
            panelHeight: "auto",
            editable: false,
            required: true,
            readonly: readonly,
        });

        // 收货日期(补充)
        $("#BarrivalTimeExt").combobox({
            data: _arrivalTimeExtJson,
            valueField: 'dictCd',
            textField: 'dictName',
            panelHeight: "auto",
            required: true,
            readonly: readonly,
            editable: false,
            onLoadSuccess: function () {
                if (!$(this).combobox('getValue')) {
                    $(this).combobox("setValue", "LR");
                }
            }
        });

        // 定金
        $("#payRateAmount").numberbox({
            required: false,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 2,
            onChange: function (value){
                if (value > 0) {
                    $("#buyPayBondTime").datebox({
                        editable: false,
                        required: true,
                        readonly: readonly,
                        height: 30
                    });
                } else {
                    $("#buyPayBondTime").datebox({
                        editable: false,
                        required: false,
                        readonly: readonly,
                        height: 30
                    });
                }
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
        });

        // 定金比例
        $('#payRate').combobox({
            data: _contractBondRateJson,
            valueField: 'dictCd',
            textField: 'dictName',
            panelHeight: 'auto',
            editable: false,
            readonly: readonly,
            onSelect: function (node) {
                var value = node.dictCd;
                var totalAmount = $("#btotalAmount").numberbox('getValue');
                var payRateAmount = Number(totalAmount) * Number(value).toFixed(2);
                $("#payRateAmount").numberbox('setValue', payRateAmount);
            }
        });

        //付定金日期
        $("#buyPayBondTime").datebox({
            editable: false,
            readonly: readonly,
            height: 30
        });

        // 采购合同模板
        var other = {
            id: -1,
            templateName: "其他",
        }
        _buyTemplateList.push(other);
        $("#buyTemplateId").combobox({
            data: _buyTemplateList,
            required: false,
            readonly: readonly,
            editable: false,
            textField: 'templateName',
            valueField: 'id',
            panelHeight: '200',
            onChange: function () {
                Cooperation.loadBuyTemplate($(this).val());
            },
            onLoadSuccess() {
                Cooperation.loadBuyTemplate($(this).combobox('getValue'));
            }
        });

        if (_$buy_readonly){
            $(".inputB").attr("readOnly","true");
        }
    },
    initSellComponent(required,readonly) {
        // 销售来源
        $("#sellSource").combobox({
            data: _sellSourceJson,
            required: true,
            textField: 'dictName',
            valueField: 'dictCd',
            readonly: true,
            hasDownArrow: false,
            onLoadSuccess: function (data) {
                if (!$(this).val()) {
                    $(this).combobox('setValue', 'K');
                }
            }
        });

        // 销售业务员
        $('#sellMatchUserName').combotree({
            data: _matchUserNameTree,
            multiple: false,
            editable: true,
            required: true,
            readonly: true,
            formatter: function (node) {
                var enableFlg = node.attributes.enableFlg;
                if (enableFlg == false) {
                    return '<span style="color:red;">' + node.text + '(无效)</span>';
                }
                return node.text;
            },
            onSelect: function (node) {
                if (node.children.length != 0) {
                    $('#sellMatchUserName').combotree('clear');
                } else {
                    var value = node.id;
                    var value1 = value.replace('user', '');
                    $('#sellMatchUserId').val(value1);
                    $('#sellMatchUserName').combotree('setValue', node.text);
                }
            }
        });

        // 中游代采方
        $('#sellOurCompanyName').combobox({
            data: _dcsxOurCompanyNameJson,
            valueField: 'companyName',
            textField: 'companyName',
            panelHeight: "auto",
            editable: false,
            required: true,
            readonly: readonly,
        });
        $('#sdeliveryAddr').textbox({required:false});
        $("#provinceName").combobox({
            url:_ctx+"/bs/areaCost/ajaxGetCity?id=1",
            valueField:"id",
            textField:"name",
            width : 150,
            height : 30,
            onSelect:function(record){
                $("#cityName").combobox({
                    url:_ctx+"/bs/areaCost/ajaxGetCity?id=" + record.id,
                    valueField:"id",
                    textField:"name",
                    width : 150,
                    height : 30,
                    onSelect:function(record){
                        if (_deliveryAddrFlg){
                            return;
                        }
                        $("#areaCode").combobox({
                            url:_ctx+"/bs/areaCost/ajaxGetCity?id=" + record.id,
                            valueField:"id",
                            textField:"name",
                            width : 150,
                            height : 30,
                            onSelect:function(record){
                                $("#areaCodeUtil").text("/"+record.name);
                                areaCodeUtil=$("#areaCodeUtil").text();
                                if(record.name!=""){
                                    var tt=$("#provinceNameUtil").text();
                                    var cc=$("#cityNameUtil").text();
                                    $("#sdeliveryAddr").textbox("setValue",tt+cc+areaCodeUtil);
                                }
                            }
                        });
                        $("#cityNameUtil").text("/"+record.name);
                        cityNameUtilUtil=$("#cityNameUtil").text();
                        var tt=$("#provinceNameUtil").text();
                        $("#sdeliveryAddr").textbox("setValue",tt+cityNameUtilUtil);
                    }
                });
                $("#provinceNameUtil").text(record.name);
                $("#sdeliveryAddr").textbox("setValue","");
                $("#sdeliveryAddr").textbox("setValue",record.name);
            }

        });
        // 客户
        $('#sellCompanyName').combobox({
            url: _ctx + "/bs/company/listMyCompany/" + 0 + "/" + 2,
            mode: 'remote',
            limitToList: true,
            valueField: 'companyName',
            textField: 'text',
            panelHeight: 300,
            required: true,
            readonly: readonly,
            formatter: function (row) {
                var text = row.text;
                var myFlag = row.myFlag;
                if (myFlag != true) {
                    text = '<span style="color:#ccc">' + text + '</span>';
                }
                return text + "[剩余额度:" + (Number(row.totalCreditAmount) - Number(row.usedCreditAmount)) + "]";

            },
            onSelect: function (node) {
                if (node.companyGrade == "A") {
                    serverRate = Number(0.00025);
                } else if (node.companyGrade == "B") {
                    serverRate = Number(0.0003);
                } else if (node.companyGrade == "C") {
                    serverRate = Number(0.00035);
                } else if (node.companyGrade == "D") {
                    serverRate = Number(0.0004);
                }
                $('#sellCompanyId').val(node.id);
                $("#provinceName").combobox({
                    url: _ctx + "/bs/areaCost/ajaxGetCity?id=1",
                    valueField: "id",
                    textField: "name",
                    readonly: readonly,
                    required: true,
                    width: 150,
                    height: 30
                });
                $("#cityName").combobox({
                    url: _ctx + "/bs/areaCost/ajaxGetCity?id=0",
                    valueField: "id",
                    textField: "name",
                    readonly: readonly,
                    width: 150,
                    height: 30,
                });
                $("#areaCode").combobox({
                    url: _ctx + "/bs/areaCost/ajaxGetCity?id=0",
                    valueField: "id",
                    textField: "name",
                    readonly: readonly,
                    width: 150,
                    height: 30,
                });

                $("#s_contactAddr").combobox({
                    url: _ctx + "/bas/warehouse/findByCompanyIdAddr/" + node.id,
                    valueField: 'warehouseAddr',
                    textField: 'warehouseAddr',
                    panelHeight: 300,
                    required: false,
                    readonly: readonly,
                    onLoadSuccess: function () {
                        _deliveryAddrFlg = true;
                        var data = $(this).combobox("getData");
                        if (data && data.length > 0) {
                            for (let i = 0; i < data.length; i++) {
                                if (data[i].defaultFlg == "true") {
                                    $(this).combobox("setValue", data[i].warehouseAddr);
                                    Cooperation.setBuyAddrs(data[i].provinceCode, data[i].cityCode, data[i].areaCode);
                                    $("#bdeliveryAddr").textbox("setValue", data[i].warehouseName);
                                }
                            }
                        } else {
                            _deliveryAddrFlg = false;
                        }
                    },
                    onSelect: function (row) {
                        Cooperation.setBuyAddrs(row.provinceCode, row.cityCode, row.areaCode);
                        $("#bdeliveryAddr").textbox("setValue", row.warehouseName);
                    }
                });
                _vipLevel = Number(node.vipLevel);
                if (serverRate > 0) {
                    // 服务费率大于0 可以做两票制合同
                    $("#deliveryModeS").combobox({readonly: false});
                } else {
                    // 服务费率为0 只能选择一票制
                    $("#deliveryModeS").combobox({readonly: true});
                    $("#deliveryModeS").combobox('setValue', '0');
                }
            }
        });


        // 我方抬头
        $('#ourCompanyName').combobox({
            data: _ourCompanyNameJson,
            valueField: 'dictName',
            textField: 'dictName',
            panelHeight: "auto",
            editable: false,
            required: true,
            readonly: readonly,
            onChange: function () {
                var value = $(this).val();
                $("#ourName").val(value);
            }
        });

        // 仓储费、运输费
        $("#swarehouseCost,#stransportCost").numberbox({
            required: true,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 2,
            onChange: function (value) {
                if (buyShowFlg && sellShowFlg) {
                    MinPrice.setMinSellPriceDcsx(false);
                }
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
        });

        // 装卸费
        $("#sstevedorage").numberbox({
            required: true,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 2,
            onChange: function (value) {
                if (buyShowFlg && sellShowFlg) {
                    MinPrice.setMinSellPriceDcsx(false);
                }
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
        });

        // 结算方式
        $("#deliveryModeS").combobox({
            data: [{"id": "0", "text": "赊销(一票制)"}],
            valueField: 'id',
            textField: 'text',
            panelHeight: "auto",
            editable: false,
            required: true,
            readonly: readonly,
            onLoadSuccess: function (data) {
                if (!$(this).val()) {
                    $(this).combobox('setValue', '0');
                }
            }
        });

        // 交货日期
        $("#sellDeliveryDate").datebox({
            height: 30,
            required: true,
            readonly: readonly,
            editable: false
        });

        // 交货补充说明
        $("#SarrivalTimeExt").combobox({
            data: _arrivalTimeExtJson,
            valueField: 'dictCd',
            textField: 'dictName',
            panelHeight: "auto",
            required: true,
            readonly: readonly,
            editable: false,
            onLoadSuccess: function () {
                if (!$(this).combobox('getValue')) {
                    $(this).combobox("setValue", "LR");
                }
            }
        });

        // 回款周期
        $("#creditDays").numberbox({
            required: true,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 0,
            editable: false,
            onChange: function (value) {
                if (buyShowFlg && sellShowFlg) {
                    MinPrice.setMinSellPriceDcsx(false);
                }
            }
        });

        // 收全款日期
        $("#receiveFullTime").datebox({
            height: 30,
            required: true,
            readonly: readonly,
            editable: false,
            onSelect: function (date) {
                var sellDeliveryDate = new Date($("#sellDeliveryDate").datebox('getValue'));
                var selectedDay = new Date(date);
                var dateDiff = selectedDay.getTime() - sellDeliveryDate.getTime();
                //计算出相差天数
                var dayDiff = Math.ceil(dateDiff / (24 * 3600 * 1000)) + 1;
                $("#creditDays").numberbox('setValue', dayDiff);
            }
        });

        // 交货方式
        $("#sellDeliveryType").combobox({
            data: _deliveryTypeJson,
            valueField: 'dictCd',
            textField: 'dictName',
            panelHeight: "auto",
            editable: false,
            required: true,
            readonly: readonly,
        });

        // 资金服务费
        $("#serviceAmount").numberbox({
            required: false,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 2,
            editable: false,
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
        });

        // 销售价
        $("#sdealPrice").numberbox({
            required: true,
            readonly: readonly,
            editable: true,
            validType: 'length[1,20]',
            precision: 2,
            onChange: function (value) {
                if (buyShowFlg){
                    var bdealPrice = $("#bdealPrice").numberbox("getValue");
                    if (!bdealPrice) {
                        $("#bdealPrice").numberbox('setValue', 0);
                    }
                }
                Cooperation.formatterSell(value);
                if (buyShowFlg && sellShowFlg) {
                    MinPrice.setMinSellPriceDcsx(true);
                }
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
        });

        // 加价
        $("#premium").numberbox({
            required: true,
            validType: 'length[1,20]',
            precision: 2,
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
        });

        // 销售总价
        $("#stotalAmount").numberbox({
            editable: false,
            precision: 2,
            onChange: function () {
                if (buyShowFlg && sellShowFlg) {
                    MinPrice.setGrossProfit(false);
                }
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
        });

        // 定金
        $("#payRateAmountSell").numberbox({
            required: false,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 2,
            onChange:function (value){
                if (value > 0) {
                    $("#sellPayBondTime").datebox({
                        editable: false,
                        required: true,
                        readonly: readonly,
                        height: 30
                    });
                } else {
                    $("#sellPayBondTime").datebox({
                        editable: false,
                        required: false,
                        readonly: readonly,
                        height: 30
                    });
                }
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
        });

        // 定金比例
        $('#payRateSell').combobox({
            data: _contractBondRateSellJson,
            valueField: 'dictCd',
            textField: 'dictName',
            panelHeight: 'auto',
            editable: false,
            readonly: readonly,
            onSelect: function (node) {
                var value = node.dictCd;
                var totalAmount = $("#stotalAmount").numberbox('getValue');
                var payRateAmount = Number(totalAmount) * Number(value).toFixed(2);
                $("#payRateAmountSell").numberbox('setValue', payRateAmount);
            }
        });

        // 付定金日期
        $("#sellPayBondTime").datebox({
            editable: false,
            readonly: readonly,
            height: 30
        });

        // 毛利润
        $("#sgrossProfit").numberbox({
            required: true,
            readonly: readonly,
            validType: 'length[1,20]',
            precision: 2,
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
        });

        // 不含险销售价
        $("#sdealPriceNoInsurance").numberbox({
            validType: 'length[1,20]',
            precision: 2,
            editable: false,
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
        });

        var other = {
            id: -1,
            templateName: "其他",
        }
        _sellTemplateList.push(other);
        $("#sellTemplateId").combobox({
            data: _sellTemplateList,
            required: false,
            editable: false,
            readonly: readonly,
            textField: 'templateName',
            valueField: 'id',
            panelHeight: '200',
            onChange: function () {
                Cooperation.loadSellTemplate($(this).val());
            },
            onLoadSuccess() {
                Cooperation.loadSellTemplate($(this).combobox('getValue'));
            }
        });

        if (_$sell_readonly){
            $(".inputS").attr("readOnly","true");
        }
    },
    changeCooperationUser(node, cooperationMode) {
        var value = node.id;
        var userId = value.replace('user', '');
        $("#cooperationUserId").val(userId);
        $('#cooperationUserName').combotree('setValue', node.text);
        if (cooperationMode && cooperationMode === 'B') {
            $("#sellMatchUserId").val(userId);
            $('#sellMatchUserName').val(node.text);
        }
        if (cooperationMode && cooperationMode === 'S') {
            $("#buyMatchUserId").val(userId);
            $('#buyMatchUserName').val(node.text);
        }
    },
    changeCooperationMode(value) {
        if (value === 'B') {
            Cooperation.initBuyComponent(true, _$buy_readonly);
            $("#buyTitle").show();
            $("#formB").show();
            $("#sellTitle").hide();
            $("#formS").hide();
            buyShowFlg = true;
            sellShowFlg = false;
            $("#buyMatchUserId").val($("#current_userId").val());
            $('#buyMatchUserName').combotree('setValue', $("#current_userName").val());
        } else {
            Cooperation.initSellComponent(true, _$sell_readonly);
            $("#buyTitle").hide();
            $("#formB").hide();
            $("#sellTitle").show();
            $("#formS").show();
            buyShowFlg = false;
            sellShowFlg = true;
            $("#sellMatchUserId").val($("#current_userId").val());
            $('#sellMatchUserName').combotree('setValue', $("#current_userName").val());
        }
    },
    handleHide(buyShowFlg, sellShowFlg) {
        if (!buyShowFlg){
            $("#buyTitle").hide();
            $("#formB").hide();
        }
        if (!sellShowFlg){
            $("#sellTitle").hide();
            $("#formS").hide();
        }
        if (buyShowFlg) {
            $("#sellTitle").hide();
            $("#formS").hide();
            Cooperation.initBuyComponent(true, _$buy_readonly);
        }
        if (sellShowFlg) {
            $("#buyTitle").hide();
            $("#formB").hide();
            Cooperation.initSellComponent(true, _$sell_readonly);
        }
        if (buyShowFlg && sellShowFlg){
            $("#sellTitle").show();
            $("#formS").show();
            $("#buyTitle").show();
            $("#formB").show();
        }
    },
    loadBuyTemplate: function (_buyTemplateId) {
        if (_buyTemplateId && _buyTemplateId != -1) {
            $(".upbtempBtnClass").hide();
            $("#upbtempBtn").removeClass("webuploader-container");
            $(".bContractPreviewClass").show();
            $(".bContractPreviewClass").on("click", function () {
                Cooperation.printContract(_buyTemplateId, buyApplyId);
            });
        } else {
            $(".upbtempBtnClass").show();
            $("#upbtempBtn").addClass("webuploader-container");
            Cooperation.fileUpload("upbtempBtn", "buyContentTemplateId", "upbtempPath", filePlugin);
            $(".bContractPreviewClass").hide();
        }
    },
    loadSellTemplate: function (_sellTemplateId) {
        if (_sellTemplateId && _sellTemplateId != -1) {
            $(".upstempBtnClass").hide();
            $("#upstempBtn").removeClass("webuploader-container");
            $(".sContractPreviewClass").show();
            $(".sContractPreviewClass").on("click", function () {
                Cooperation.printContract(_sellTemplateId, sellApplyId);
            });
        } else {
            $(".upstempBtnClass").show();
            $("#upstempBtn").addClass("webuploader-container");
            Cooperation.fileUpload("upstempBtn", "sellContentTemplateId", "upselltempPath", sellFilePlugin);
            $(".sContractPreviewClass").hide();
        }
    },
    printContract: function (templateId, applyId) {
        $('#w_contract').window({
            title: '电子合同',
            modal:true,
            closed: true,
            collapsible : false,
            minimizable : false,
            maximizable : false,
            cache:false
        });
        if (templateId) {
            // var url = _ctx + "/ctr/contractText/getTemplateContract/" + templateId;
            var url = _ctx+"/ctr/contractText/getMatchContractNew/"+templateId+"?applyId="+applyId + "&windowId=w_contract";
            $('#w_contract').window({href: url});
            // 手机端默认最大化
            if (window.innerWidth <= 768) {
                $('#w_contract').window('maximize');
            }
            $('#w_contract').window("open");
        }
    },
    formatterBuy: function (value) {
        if (!buyShowFlg) {
            return;
        }
        if (!value) {
            value = $("#bdealPrice").numberbox('getValue');
        }
        var notax = parseFloat(value / 1.13).toFixed(2);
        $("#bdealAmountNotax").numberbox('setValue', notax);
        var dealNumber = $("#dealNumber").numberbox('getValue');
        if (dealNumber) {
            var totalAmount = parseFloat(value * dealNumber);
            $("#btotalAmount").numberbox('setValue', Math.round(totalAmount * 100) / 100);
            _deliveryModeJson_ = _deliveryModeJson.filter(function (a) {
                return a.dictCd == "XHHK" || a.dictCd == "XKHH";
            })
            // 预付款额度不足时，采购合同只能选择先货后款
            if (Number($("#supplierPrepayAmount").val()) - Number($("#usedSupplierPrepayAmount").val()) < Number($("#btotalAmount").numberbox("getValue"))) {
                _deliveryModeJson_ = _deliveryModeJson.filter(function (a) {
                    return a.dictCd == "XHHK";
                })
                $('#deliveryModeB').combobox("clear");
                $('#deliveryModeB').combobox('loadData', _deliveryModeJson_);
            }
        }
    },
    formatterSell: function (value) {
        if (!sellShowFlg) {
            return;
        }
        if (!value) {
            value = $("#sdealPrice").numberbox('getValue');
        }
        var dealNumber = $("#dealNumber").numberbox('getValue');
        if (dealNumber) {
            var totalAmount = parseFloat(value * dealNumber).toFixed(2);
            $("#stotalAmount").numberbox('setValue', totalAmount);
        }
    },
    fileUpload: function (fileBtn, fileId, filePath, filePlugin) {
        var $this = $('#' + fileId);
        var uploader = WebUploader.create({
            auto: true,
            // swf文件路径
            swf: _ctx + '/static/webuploader-0.1.5/Uploader.swf',
            // 文件接收服务端。
            server: _ctx + '/file/uploadFile',
            // 选择文件的按钮。可选。
            // 内部根据当前运行是创建，可能是input元素，也可能是flash.
            pick: '#' + fileBtn,
            // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
            resize: false,
            duplicate: true,//true可重复上传，默认不可多次重复上传
            // 只允许选择图片文件。
            accept: {
                title: 'file',
                extensions: 'gif,jpg,jpeg,bmp,png,pdf,rar,zip,doc,docx,xlsx,xls',
                mimeTypes: "*"
            }
        });
        // 去掉文件上传组件样式，使用上传dom 自己样式
        $(".webuploader-pick").removeClass();

        uploader.on('beforeFileQueued', function (file) {
            $.messager.progress('close');
            if (file && file.size > 10485760) {
                $.messager.alert('tip', "上传文件不能超过10M!", 'info');
                return false;
            }
        })

        // 当有文件添加进来的时候
        uploader.on('fileQueued', function (file) {
            $.messager.progress();
        });

        // 文件上传成功，给item添加成功class, 用样式标记上传成功。
        uploader.on('uploadSuccess', function (file, response) {
            $.messager.progress('close');
            if (file && file.size > 10485760) {
                $.messager.alert('tip', "上传文件不能超过10M!", 'info');
                return;
            }
            if (response && response.result) {//上传成功
                var fileId = response.fileId;

                filePlugin.uploadSuccess(fileId);
                //加载附件列表
                filePlugin.load($this.val(), '#' + filePath);
                if (filePath == "upbtempPath") {
                    Cooperation.upbtempBtnFun(false, true, false);
                }
                if (filePath == "upselltempPath") {
                    Cooperation.upstempBtnFun(false, true, false);
                }
            } else {
                $.messager.alert('tip', "上传文件失败，失败信息：" + response._raw, 'info');
            }

        });
        uploader.on('error', function (file) {
            $.messager.progress('close');
            $.messager.alert('tip', '文件格式不支持!', 'info');
        })
        // 文件上传失败，显示上传出错。
        uploader.on('file', function (file) {
            $.messager.progress('close');
            $.messager.alert('tip', '文件上传失败,请重试!', 'info');
        });
    },
    upbtempBtnFun: function (flg1, flg2, flg3) {
        var buy_template_id = $("#buyTemplateId").combobox("getValue");
        var buy_content_template_id = $("#buyContentTemplateId").val();
        var buyApplyId = $("#buyApplyId").val();
        if (flg1){
            if (buy_template_id == -1) {
                // 如果选择其他 显示上传按钮
                $(".upbtempBtnClass").show();
                $("#upbtempBtn").addClass("webuploader-container");
                $(".bContractPreviewClass").hide();
                $("#bContractPreviewBtn").off("click");
            }else{
                $(".upbtempBtnClass").hide();
                $("#upbtempBtn").removeClass("webuploader-container");
                // 显示合同预览按钮
                $(".bContractPreviewClass").show();
                $("#bContractPreviewBtn").off("click");
                $("#bContractPreviewBtn").on("click",function () {
                    Cooperation.printContract(buy_template_id,buyApplyId);
                })
            }
        }else if(flg2){
            if (flg3){
                $("#buyTemplateId").combobox({readonly:false});
                $("#buyTemplateId").combobox({
                    data:_buyTemplateList,
                    required:false,
                    editable:false,
                    textField:'templateName',
                    valueField:'id',
                    panelHeight:'100',
                    onChange:function(){
                        Cooperation.upbtempBtnFun(true,false);
                    }
                });
            }else if(buy_content_template_id){
                $("#buyTemplateId").combobox({readonly:true});
            }else{
                $("#buyTemplateId").combobox({readonly:false});
                $("#buyTemplateId").combobox({
                    data:_buyTemplateList,
                    required:false,
                    editable:false,
                    textField:'templateName',
                    valueField:'id',
                    panelHeight:'100',
                    onChange:function(){
                        Cooperation.upbtempBtnFun(true,false);
                    }
                });
            }
        }
    },
    upstempBtnFun:function (flg1,flg2,flg3){
        var sell_template_id = $("#sellTemplateId").combobox("getValue");
        var sell_content_template_id = $("#sellContentTemplateId").val();
        var sellApplyId = $("#sellApplyId").val();
        if (flg1){
            if (sell_template_id == -1) {
                // 如果选择其他 显示上传按钮
                $(".upstempBtnClass").show();
                $("#upstempBtn").addClass("webuploader-container");
                $(".sContractPreviewClass").hide();
                $("#sContractPreviewBtn").off("click")
            }else{
                $(".upstempBtnClass").hide();
                $("#upstempBtn").removeClass("webuploader-container");
                $(".sContractPreviewClass").show();
                $("#sContractPreviewBtn").off("click")
                $("#sContractPreviewBtn").on("click",function () {
                    Cooperation.printContract(sell_template_id,sellApplyId);
                });
            }
        }else if(flg2){
            if (flg3){
                $("#sellTemplateId").combobox({readonly:false});
                $("#sellTemplateId").combobox({
                    data:_sellTemplateList,
                    required:false,
                    editable:false,
                    textField:'templateName',
                    valueField:'id',
                    panelHeight:'100',
                    onChange:function(){
                        Cooperation.upstempBtnFun(true,false);
                    }
                });
            }else if(sell_content_template_id){
                $("#sellTemplateId").combobox({readonly:true});
            }else{
                $("#sellTemplateId").combobox({readonly:false});
                $("#sellTemplateId").combobox({
                    data:_sellTemplateList,
                    required:false,
                    editable:false,
                    textField:'templateName',
                    valueField:'id',
                    panelHeight:'100',
                    onChange:function(){
                        Cooperation.upstempBtnFun(true,false);
                    }
                });
            }
        }
    },
    setSellProvince: function (provinceId, cityId) {
        $("#provinceNames").combobox({
            url: _ctx + "/bs/areaCost/ajaxGetCity?id=1",
            valueField: "id",
            textField: "name"
        });
        $("#cityNames").combobox({
            url: _ctx + "/bs/areaCost/ajaxGetCity?id=" + provinceId,
            valueField: "id",
            textField: "name"
        });
        $("#areaCodes").combobox({
            url: _ctx + "/bs/areaCost/ajaxGetCity?id=" + cityId,
            valueField: "id",
            textField: "name"
        });
    },
    setBuyProvince: function (provinceId, cityId) {
        $("#provinceName").combobox({
            url: _ctx + "/bs/areaCost/ajaxGetCity?id=1",
            valueField: "id",
            textField: "name"
        });
        $("#cityName").combobox({
            url: _ctx + "/bs/areaCost/ajaxGetCity?id=" + provinceId,
            valueField: "id",
            textField: "name"
        });
        $("#areaCode").combobox({
            url: _ctx + "/bs/areaCost/ajaxGetCity?id=" + cityId,
            valueField: "id",
            textField: "name"
        });
    },
    setBuyAddrs: function (provinceId, cityId, areaId) {
        Cooperation.setBuyProvince(provinceId, cityId);
        $("#provinceName").combobox("setValue", provinceId);
        $("#cityName").combobox("setValue", cityId);
        $("#areaCode").combobox("setValue", areaId);
    },
    setSellAddrs: function (provinceId, cityId, areaId) {
        Cooperation.setSellProvince(provinceId, cityId);
        $("#provinceNames").combobox("setValue", provinceId);
        $("#cityNames").combobox("setValue", cityId);
        $("#areaCodes").combobox("setValue", areaId);
    },
    checkContractTemplate: function () {
        if (buyShowFlg) {
            // 检验采购合同模板是否上传或选择
            var buyContract = $("#buyTemplateId").combobox('getValue');
            if ((isBlank(buyContract) && isBlank($("#buyContentTemplateId").val()))
                || (buyContract == '-1' && isBlank($("#buyContentTemplateId").val()))
            ) {
                $.messager.alert('提示', '采购合同模板必须选择或上传!', 'warning');
                return false;
            }
        } else {
            deliveryModeS = $("#deliveryModeS").combobox("getValue");
            var sellContract = $("#sellTemplateId").combobox('getValue');
            if ((isBlank(sellContract) && isBlank($("#sellContentTemplateId").val()))
                || (sellContract == '-1' && isBlank($("#sellContentTemplateId").val()))
            ) {
                $.messager.alert('提示', '销售合同模板必须选择或上传!', 'warning');
                return false;
            }
        }
        return true;
    }
}