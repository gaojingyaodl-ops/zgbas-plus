var Discount = {
    changeReceiveMode: function (receiveModeCd) {
        if (receiveModeCd === 'H') {
            $('.discountDom').removeClass('hide');
        } else {
            $('.discountDom').addClass('hide');
        }
        Discount.verifyShowDiscountFlg(receiveModeCd, '');
    },
    changeReceiveType: function (receiveTypeCd) {
        if (receiveTypeCd === 'T') {
            $("#receiveMode").combobox('loadData', _receiveModeJson);
            $("#receiveMode").combobox('setValue', "T");
            $('.discountDom').removeClass('hide');
        } else {
            $("#receiveMode").combobox('loadData', receiveModeJson);
            $('.discountDom').addClass('hide');
        }
        Discount.verifyShowDiscountFlg('', receiveTypeCd);
    },
    verifyShowDiscountFlg: function (receiveModeCd, receiveTypeCd) {
        if (_initFlg){
            return;
        }
        const _$receiveMode = !receiveModeCd ? Discount.getComboboxValue('#receiveMode') : receiveModeCd;
        const _$receiveType = !receiveTypeCd ? Discount.getComboboxValue('#receiveType') : receiveTypeCd;
        Discount.queryDiscountContractList(_$receiveMode, _$receiveType);
        if (_$receiveMode === 'H' || _$receiveType === 'T') {
            $('.discountCheckDom').removeClass('hide');
            let _$required = false;
            if (_$receiveMode === 'H') {
                _$required = true;
                $('.discountDom').removeClass('hide');
            } else if (_$receiveType === 'T') {
                _$required = false;
                $('.discountDom').addClass('hide');
            }
            $("#discountTarget").combobox({required: _$required});
            $("#billDueTime").datebox({required: _$required});
            $("#dueTime").datebox({required: _$required});
        } else {
            $('.discountDom').addClass('hide');
            $('.discountCheckDom').addClass('hide');
            $("#discountTarget").combobox({required: false});
            $("#billDueTime").datebox({required: false});
            $("#dueTime").datebox({required: false});
        }
    },
    queryDiscountContractList: function (receiveModeCd, receiveTypeCd) {
        var resultData = [];
        var contractData = Discount.getDefaultDetail();
        var _ourCompanyName = $('#ourCompanyName').val();
        var _companyName = $('#companyName').val();
        var _contractId = $('#contractId').val();
        _contractId = _contractId ? _contractId : $("#contractId0").val();
        if (receiveModeCd === 'H' || receiveTypeCd === 'T') {
            if (_ourCompanyName && _companyName) {
                $.ajax({
                    url: _ctx + "/apply/receive/queryDiscountContractList",
                    async: false,
                    type: "POST",
                    dateType: "json",
                    contentType: 'application/json',
                    data: JSON.stringify({
                        'ourCompanyName': _ourCompanyName,
                        'companyName': _companyName,
                        'id': _contractId,
                        'payMode': receiveModeCd,
                        'payType': receiveTypeCd
                    }),
                    success: function (data) {
                        resultData = data;
                    }
                });
            }
        } else if(contractData){
            resultData.push(contractData);
            $("#receiveAmount0").numberbox('setValue', $("#receiveAmount").numberbox('getValue'));
        }
        if (resultData.length === 0){
            $.ajax({
                url: _ctx + "/apply/receive/queryDiscountContractList",
                async: false,
                type: "POST",
                dateType: "json",
                contentType: 'application/json',
                data: JSON.stringify({
                    'ourCompanyName': _ourCompanyName,
                    'companyName': _companyName,
                    'id': _contractId,
                }),
                success: function (data) {
                    resultData = data;
                }
            });
        }
        Discount.buildDiscountShowDom(resultData, receiveModeCd, receiveTypeCd);
        Discount.initViewDom(resultData.length);
    },
    buildDiscountShowDom: function (data, receiveModeCd, receiveTypeCd) {
        if (data.length === 0) {
            return;
        }
        // console.log("--------------------------------")
        // console.log("===receiveModeCd====" + receiveModeCd);
        // console.log("===receiveTypeCd====" + receiveTypeCd);
        let html = "";
        for (let i = 0; i < data.length; i++) {
            console.log(data[i]);
            html += "<div class=\"div-v2\" id=\"discountDiv" + i + "\">";
            html += "<fieldset class=\"form-section-v2\">";
            html += "<legend class=\"form-section-title-v2\">";
            html += "<span>" + data[i].contractNo + "</span>";
            html += "<i></i></legend>";
            html += "<div class=\"mb6\">";
            html += "<p class=\"yw-table-filter\">";
            html += "<label>合同编号:</label>";
            html += "<input type=\"text\" id=\"contractNo" + i + "\" value=\"" + data[i].contractNo + "\" readonly/>";
            html += "<input class=\"hide\" type=\"text\" id=\"contractId" + i + "\" value=\"" + data[i].contractId + "\" readonly/>";
            html += "<input class=\"hide\" type=\"text\" id=\"detailId" + i + "\" value=\"" + data[i].id + "\" readonly/>";
            html += "</p>";
            html += "<p class=\"yw-table-filter\">";
            html += "<label>待收合同金额（元）:</label>";
            html += "<input class=\"targetNum\" type=\"text\" id=\"unpayedAmount" + i + "\" value=\"" + data[i].unpayedAmount + "\" readonly/>";
            html += "<input class=\"hide\" type=\"text\" id=\"payedAmount" + i + "\" value=\"" + data[i].payedAmount + "\"/>";
            html += "</p>";
            html += "<p class=\"yw-table-filter\">";
            html += "<label>待收罚息金额（元）:</label>";
            html += "<input class=\"targetNum\" type=\"text\" id=\"needBreachAmount" + i + "\" value=\"" + (data[i].breachAmount - data[i].receiveBreachAmount) + "\" readonly/>";
            html += "<input class=\"hide\" type=\"text\" id=\"receiveBreachAmount" + i + "\" value=\"" + data[i].receiveBreachAmount + "\"/>";
            html += "<input class=\"hide\" type=\"text\" id=\"breachAmount" + i + "\" value=\"" + data[i].breachAmount + "\"/>";
            html += "</p>";
            html += "</div>";
            html += "<div class=\"mb6 discount-mb6\">";
            html += "<p class=\"yw-table-filter\">";
            html += "<label>合同总金额（元）:</label>";
            html += "<input class=\"targetNum\" type=\"text\" id=\"totalAmount" + i + "\" value=\"" + data[i].totalAmount + "\" readonly/>";
            html += "</p>";
            if (receiveTypeCd === 'T') {
                html += "<p class=\"yw-table-filter\">";
                html += "<label>待收贴现费用（元）:</label>";
                html += "<input class=\"targetNum\" type=\"text\" id=\"unDiscountAmount" + i + "\" value=\"" + data[i].unDiscountAmount + "\" readonly/>";
                html += "</p>";
                html += "<p class=\"yw-table-filter\">";
                html += "<label><i>*</i>收款金额（元）:</label>";
                html += "<input class=\"receiveAmount\" type=\"text\" id=\"receiveAmount" + i + "\" value=\"" + data[i].unDiscountAmount + "\"/>";
                html += "</p>";
            } else if (receiveTypeCd === 'M') {
                html += "<p class=\"yw-table-filter\">";
                html += "<label><i>*</i>收款金额（元）:</label>";
                html += "<input class=\"receiveAmount\" type=\"text\" id=\"receiveAmount" + i + "\" value=\"" + (data[i].breachAmount - data[i].receiveBreachAmount) + "\"/>";
                html += "</p>";
            } else {
                html += "<p class=\"yw-table-filter\">";
                html += "<label><i>*</i>收款金额（元）:</label>";
                html += "<input class=\"receiveAmount\" type=\"text\" id=\"receiveAmount" + i + "\" value=\"" + data[i].unpayedAmount + "\"/>";
                html += "</p>";
            }

            if (receiveModeCd === 'H'){
                html += "<p class=\"yw-table-filter\">";
                html += "<label><i>*</i>贴现费用（元）:</label>";
                html += "<input class=\"discountAmount\" type=\"text\" id=\"discountAmount" + i + "\"/>";
                html += "</p>";
            }
            html += "<p class=\"yw-table-filter hide discountCheckDom\">";
            if (i === 0) {
                html += "<input type=\"checkbox\" class=\"checkbox\" checked=\"checked\" id=\"discountCheck" + i + "\"/> <span>抵扣</span>";
            } else {
                html += "<input type=\"checkbox\" class=\"checkbox\" id=\"discountCheck" + i + "\"/> <span>抵扣</span>";
            }
            html += "</p>";
            html += "</div>";
            html += "</fieldset>";
            html += "</div>";
        }
        let _dynamicDiscountDom = document.getElementById("dynamicDiscountDom");
        _dynamicDiscountDom.innerHTML = html;
        if (receiveTypeCd === 'T') {
            $("#receiveAmount").numberbox("setValue", data[0].unDiscountAmount);
        } else if (receiveTypeCd === 'M') {
            $("#receiveAmount").numberbox("setValue", data[0].breachAmount - data[0].receiveBreachAmount);
        } else {
            $("#receiveAmount").numberbox("setValue", data[0].unpayedAmount);
        }
    },
    initViewDom: function (domSize) {
        Discount.initNumberPlug(".targetNum", '', false);
        for (let i = 0; i < domSize; i++) {
            Discount.initReceiveAmountPlug(i, i === 0);
            Discount.initDiscountAmountPlug(i, i === 0);
            Discount.initNumberPlug("#discountAmount", i, i === 0);
        }
        Discount.initDiscountCheck();
    },
    initDiscountCheck() {
        $('.checkbox').change(function () {
            let check_id = $(this).attr('id');
            let _domId = check_id.replace("discountCheck", "");
            let _required = false;
            if ($(this).prop('checked')) {
                $(this).prop('checked', true);
                _required = true;
            }
            Discount.initNumberPlug("#receiveAmount", _domId, _required);
            Discount.initNumberPlug("#discountAmount", _domId, _required);
            Discount.updateReceiveAmount();
            Discount.updateDiscountAmount();
            Discount.calculateDiscountAmount();
        });
    },
    initReceiveAmountPlug: function (domId, _required) {
        $("#receiveAmount" + domId).numberbox({
            required: _required,
            precision: 2,
            onChange: function (v1) {
                Discount.updateReceiveAmount();
                Discount.calculateDiscountAmount();
            },
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
    initDiscountAmountPlug: function (domId, _required) {
        $("#discountAmount" + domId).numberbox({
            required: _required,
            precision: 2,
            onChange: function (v1) {
                Discount.updateDiscountAmount();
            },
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
    updateReceiveAmount: function () {
        let _currTotalAmount = 0;
        const discountDomAll = document.querySelectorAll('#dynamicDiscountDom .discount-mb6');
        for (let i = 0; i < discountDomAll.length; i++) {
            let currCheckFlg = document.getElementById('discountCheck' + i).checked;
            if (currCheckFlg) {
                let currReceiveAmount = $('#receiveAmount' + i).numberbox("getValue");
                _currTotalAmount += Number(currReceiveAmount);
            }
        }
        $("#receiveAmount").numberbox('setValue', Math.round(_currTotalAmount * 100) / 100);
    },
    updateDiscountAmount: function () {
        let _currDiscountAmount = 0;
        const discountDomAll = document.querySelectorAll('#dynamicDiscountDom .discount-mb6');
        for (let i = 0; i < discountDomAll.length; i++) {
            let currCheckFlg = document.getElementById('discountCheck' + i).checked;
            if (currCheckFlg) {
                let currDiscountAmount = $('#discountAmount' + i).numberbox("getValue");
                _currDiscountAmount += Number(currDiscountAmount);
            }
        }
        $("#discountAmount").numberbox('setValue', Math.round(_currDiscountAmount * 100) / 100);
    },
    initNumberPlug: function (_domName, _domId, _required) {
        $(_domName + _domId).numberbox({
            required: _required,
            precision: 2,
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
    dealWithEdit: function (domSize){
        let _receiveModeCd = Discount.getComboboxValue("#receiveMode");
        if (_receiveModeCd === "H" || _receiveModeCd === "票汇"){
            $('.discountDom').removeClass('hide');
        }
        if (canApproveEdit === 'true') {
            $('#discountRate').numberbox({
                readonly: false,
                precision: 6,
                min: 0,
                onChange: function (){
                    Discount.calculateDiscountAmount();
                }
            });
            $("#discountTarget").combobox('readonly', false);
            $("#receiveDate,#billDueTime,#dueTime").datebox({
                readonly: false,
                onChange: function (){
                    Discount.calculateDiscountAmount();
                }
            });
            for (let i = 0; i < domSize; i++) {
                Discount.initReceiveAmountPlug(i, true);
                Discount.initDiscountAmountPlug(i, true);
            }
        } else {
            $("#receiveDate,#billDueTime,#dueTime").datebox('readonly', true);
            $("#discountTarget").combobox('readonly', true);
        }
    },
    getComboboxValue: function (domName) {
        try {
            return $(domName).combobox("getValue");
        } catch (e) {
            console.log("getComboboxValue error" + domName, e)
        }
        return $(domName).val();
    },
    getDateboxValue: function (domName) {
        try {
            return $(domName).datebox("getValue");
        } catch (e) {
            console.log("getDateboxValue error" + domName, e)
        }
        return $(domName).val();
    },
    getNumberboxValue: function (domName) {
        try {
            return $(domName).numberbox("getValue");
        } catch (e) {
            console.log("getNumberboxValue error" + domName, e)
        }
        return $(domName).val();
    },
    getDefaultDetail: function (){
        if (_$currContract) {
            return {
                "id": null,
                "contractId": _$currContract.id,
                "contractNo": _$currContract.contractNo,
                "totalAmount": _$currContract.totalAmount,
                "payedAmount": _$currContract.dealedAmount,
                "breachAmount": _$currContract.breachAmount,
                "receiveBreachAmount": _$currContract.receiveBreachAmount,
                "unpayedAmount": Number(_$currContract.totalAmount) - Number(_$currContract.applyPayAmount),
                "unDiscountAmount": Number(_$currContract.discountChargeAmount) - Number(_$currContract.discountReceiveAmount),
            };
        }
    },
    calculateDiscountAmount: function (){
        if (_initFlg){
            return;
        }
        let _receiveMode = Discount.getComboboxValue('#receiveMode');
        if (_receiveMode !== 'H'){
            return;
        }
        let _billDueTime = Discount.getDateboxValue("#billDueTime");
        let _dueTime = Discount.getDateboxValue("#dueTime");
        let _discountRate = Discount.getNumberboxValue("#discountRate");
        if (!_billDueTime || !_dueTime || !_discountRate){
            return;
        }
        let dayDiff = Math.ceil((new Date(_dueTime).getTime() - new Date(_billDueTime).getTime()) / (24 * 3600 * 1000)) + 1;
        // 贴现费用 = 收款金额 * 贴现利率 / 360 * (承兑到期日 - 收承兑日期 +1)
        const discountDomAll = document.querySelectorAll('#dynamicDiscountDom .discount-mb6');
        for (let i = 0; i < discountDomAll.length; i++) {
            let currCheckFlg = document.getElementById('discountCheck' + i).checked;
            if (currCheckFlg) {
                let currReceiveAmount = $('#receiveAmount' + i).numberbox("getValue");
                let _calculateAmount = currReceiveAmount * _discountRate / 360 * dayDiff;
                $('#discountAmount' + i).numberbox("setValue", Math.round(_calculateAmount * 100) / 100);
            }
        }
    }
}