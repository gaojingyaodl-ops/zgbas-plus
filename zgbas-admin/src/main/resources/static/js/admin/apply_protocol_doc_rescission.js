var applyProtocolDocRescission = {
    initRescissionView: function (docType) {
        if (docType == "CP") {
            var url = _ctx + "/apply/protocolDocument/addProtocolDocCkhDetail";
            $.post(url, {docType: docType, curNumber: 0, id: $("#entityId").val()}, function (html) {
                $("#rescissioDiv").html(html);
                applyProtocolDocRescission.initRescissionMode();
                applyProtocolDocRescission.dealWithOtherMode();
            });
        } else {
            $("#rescissioDiv").html("");
        }
    },
    initRescissionMode() {
        $('.easyui-linkbutton').linkbutton();
        var _entityStatus = $("#entityStatus").val();
        var _editflg = (_entityStatus == "A" || _entityStatus == "D");
        $("#targetCompanyName,#our_company_name,#protocolNo,#contractNo,#productName,#factoryName").textbox({
            required: true,
            readonly: _editflg
        });
        $("#signCompanyName").combobox({required: true, readonly: _editflg});
        $("#protocolDate,#contractDate").datebox({required: true, readonly: _editflg});
        $("#totalNumber,#dealPrice,#totalAmount").numberbox({
            min: 0,
            precision: 4,
            readonly: _editflg,
            required: true,
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

        $("#refundAmount").numberbox({
            min: 0,
            precision: 4,
            readonly: _editflg,
            required: true,
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
        if (_editflg) {
            $(".syncClass").addClass("hide");
        }
    },
    syncContractDetail(docType) {
        var _contractNo = $("#contractNo").val();
        if (!_contractNo) {
            return;
        }
        $.ajax({
            url: _ctx + "/ctr/contract/findDetailByContractNo?contractNo=" + _contractNo + "&endDate=",
            type: "post",
            async: false,
            success: function (contract) {
                if (contract != null && contract != undefined) {
                    if ('S' === contract.contractType) {
                        $("#targetCompanyName").textbox("setValue", contract.ourCompanyName);
                        $("#our_company_name").textbox("setValue", contract.companyName);
                    } else {
                        $("#targetCompanyName").textbox("setValue", contract.companyName);
                        $("#our_company_name").textbox("setValue", contract.ourCompanyName);
                    }
                    $("#signCompanyName").textbox("setValue", contract.ourCompanyName);
                    // 获取已申请的相同的协议编号
                    $.ajax({
                        url: _ctx + "/apply/protocolDocument/findProtocolDocumentByContent?contractNo=" + _contractNo+"&docType="+docType,
                        type: "get",
                        async: false,
                        success: function (content){
                            var jsonObject = JSON.parse(content);
                            var total = jsonObject.total+1;
                            $("#protocolNo").textbox("setValue", contract.contractNo.replace(/\D/g, '')+"-"+total);
                        }
                    })
                    $("#contractNo").textbox("setValue", contract.contractNo);
                    $("#productName").textbox("setValue", applyProtocolDocRescission.splitString(contract.productsName).remainingParts);
                    $("#factoryName").textbox("setValue", applyProtocolDocRescission.splitString(contract.productsName).lastPart);
                    $("#contractDate").datebox("setValue", contract.contractTime);
                    $("#protocolDate").datebox("setValue", Convert.getNowDateStr());
                    $("#totalNumber").numberbox("setValue", contract.totalNumber);
                    $("#dealPrice").numberbox("setValue", contract.dealPrice);
                    $("#totalAmount").numberbox("setValue", contract.totalAmount);
                    $("#refundAmount").numberbox("setValue", contract.dealedAmount);
                }
            }
        });
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