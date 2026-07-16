var applyProtocolDoc_rePayment={
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
                applyProtocolDoc_rePayment.initLoadEasyui(_curNumber);
                number1 = number1+1;
            } else {
                for (var i = 0; i < _detailSize; i++) {
                    applyProtocolDoc_rePayment.initLoadEasyui(i);
                    applyProtocolDoc_rePayment.initLoadEasyui(number1);
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
        var _editflg = (entityStatus == "A" || entityStatus == "D");
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
        $("#dealedAmount"+randomNum).numberbox({
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
        $("#shouldPayAmount"+randomNum).numberbox({
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

        if(entityStatus=="N"|| entityStatus=="B"|| entityStatus=="C"){
            // $("#realPayDateStr"+randomNum).validatebox({
            //     required:true
            // });
            // $("#overdueLateFees"+randomNum).validatebox({
            //     required:true
            // });
            $("#overdueLateFeesSum").validatebox({required: false});
        }else{
            //禁用所有输入框
            $(".detail-content").attr("readOnly","true");
            //合同类型
            $("#contractNo"+randomNum).textbox('readonly', true);
            $("#totalAmount"+randomNum).numberbox('readonly', true);
            $("#deliveryDate"+randomNum).datebox('readonly', true);
            $("#payDateStr"+randomNum).datebox('readonly', true);
            $("#creditDays"+randomNum).numberbox('readonly', true);
            // $("#realPayDateStr"+randomNum).validatebox({
            //     required:false
            // });
            // $("#overdueLateFees"+randomNum).validatebox({
            //     required:false
            // });
            // $("#realPayDateStr"+randomNum).validatebox('readonly', true);
            // $("#overdueLateFees"+randomNum).validatebox('readonly', true);

            $("#productCd"+randomNum).combobox('readonly', true);
            $("#brandNumber"+randomNum).combobox('readonly', true);
            $("#totalNumber"+randomNum).textbox('readonly', true);
        }
        if (_editflg) {
            $(".syncClass").addClass("hide");
        }
    },
    syncContractDetail(val) {
        var _contractNo = $("#contractNo"+val).val();
        if (!_contractNo) {
            return;
        }
        $.ajax({
            url: _ctx + "/ctr/contract/findDetailByContractNo?contractNo=" + _contractNo + "&endDate=",
            type: "post",
            async: false,
            success: function (contract) {
                if (contract != null && contract != undefined) {
                    let node =applyProtocolDoc_rePayment.findNodeByText(_productTypeJson,applyProtocolDoc_rePayment.splitString(contract.productsName).productCd)
                    $("#productCd"+val).combotree("setValue",node);
                    $("#productName"+val).val(node.text);
                    // 初始化牌号数据
                    var new_brandJson = _brandJson.filter(function (e) {
                        return e.productCd == node.id && e.enterpriseId == 44;
                    })
                    $("#brandNumber"+val).combobox({
                        data:new_brandJson,
                        required:true,
                        limitToList:true,
                        editable:true,
                        textField:'brandNumber',
                        valueField:'brandNumber'
                    });
                    $("#brandNumber"+val).combobox("setValue",applyProtocolDoc_rePayment.splitString(contract.productsName).brandNumber);
                    $("#totalNumber"+val).numberbox("setValue", contract.totalNumber);
                    $("#totalAmount"+val).numberbox("setValue", contract.totalAmount);
                    $("#creditDays"+val).numberbox("setValue", contract.creditCycle);
                    $("#deliveryDate"+val).datebox("setValue", contract.confirmDate);
                    $("#payDateStr"+val).datebox("setValue", contract.payFullTime);
                    $("#dealedAmount"+val).numberbox("setValue", contract.dealedAmount);
                    $("#shouldPayAmount"+val).numberbox("setValue", contract.totalAmount-contract.dealedAmount);

                    $("#companyName").textbox("setValue", contract.companyName);
                    $("#accountName").combobox("setValue", contract.ourCompanyName);
                    $("#paymentAccount").textbox("setValue", contract.ourCompanyName);
                    $("#signCompanyName").combobox("setValue", contract.ourCompanyName);
                    $("#bankName").textbox("setValue", contract.bankName);
                    $("#bankAccount").textbox("setValue", contract.bankAccount);
                }
            }
        });
    },
    splitString(value) {
        try {
            // 以斜杠为分隔符将字符串分割成数组
            const parts = value.split('/');
            // 获取第一个
            const productCd = parts[0]
            // 获取第二个
            const brandNumber =  parts[1]

            return {
                brandNumber: brandNumber,
                productCd: productCd
            };
        }catch (e){
            return {
                remainingParts: "",
                lastPart: ""
            };
        }
    },
    // 过滤树节点
    findNodeByText(data, text) {
        for (let i = 0; i < data.length; i++) {
            var node = data[i];
            if (node.text === text) {
                return node;
            }
            if (node.children) {
                const result = applyProtocolDoc_rePayment.findNodeByText(node.children, text);
                if (result) {
                    return result;
                }
            }
        }
        return null;
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
