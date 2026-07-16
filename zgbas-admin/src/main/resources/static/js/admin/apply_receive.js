// var DiscountReceive = {
//     changeReceiveMode: function (receiveModeCd){
//         if (receiveModeCd === 'H') {
//             $('.discountDom').removeClass('hide');
//         } else {
//             $('.discountDom').addClass('hide');
//         }
//     },
//     initDisCountDomRequired: function (domSize){
//         if (domSize) {
//             for (let i = 0; i < domSize; i++) {
//                 $('#receiveAmount' + i).numberbox({
//                     required: true,
//                     readonly: false,
//                     precision: 2,
//                     min: 0
//                 });
//
//                 $('#discountAmount' + i).numberbox({
//                     required: true,
//                     readonly: false,
//                     precision: 2,
//                     min: 0
//                 });
//             }
//         }
//     },
//     refreshDiscountList: function (data){
//         if (data.length === 0){
//             return;
//         }
//         var html = "";
//         var _$receiveMode = $('#receiveMode').combobox('getValue');
//         var _$receiveType = $('#receiveType').combobox('getValue');
//         for (let i = 0; i < data.length; i++) {
//             html += "<div class=\"div-v2\" id=\"discountDiv"+ i +"\">";
//             html += "<fieldset class=\"form-section-v2\">";
//             html += "<legend class=\"form-section-title-v2\">";
//             html += "<span>"+ data[i].contractNo +"</span>";
//             html += "<i></i></legend>";
//             html += "<div class=\"mb6\">";
//             html += "<p class=\"yw-table-filter\">";
//             html += "<label>合同总金额（元）:</label>";
//             html += "<input class=\"contractNum\" type=\"text\" id=\"totalAmount"+ i +"\" value=\""+ data[i].totalAmount +"\" readonly/>";
//             html += "</p>";
//             html += "<p class=\"yw-table-filter\">";
//             html += "<label>待收合同金额（元）:</label>";
//             html += "<input class=\"contractNum\" type=\"text\" id=\"unpayedAmount"+ i +"\" value=\""+ (data[i].totalAmount - data[i].payedAmount) +"\" readonly/>";
//             html += "<input class=\"hide\" type=\"text\" id=\"payedAmount"+ i +"\" value=\""+ data[i].payedAmount +"\"/>";
//             html += "</p>";
//             html += "<p class=\"yw-table-filter\">";
//             html += "<label>待收罚息金额（元）:</label>";
//             html += "<input class=\"contractNum\" type=\"text\" id=\"needBreachAmount"+ i +"\" value=\""+ (data[i].breachAmount - data[i].receiveBreachAmount) +"\" readonly/>";
//             html += "<input class=\"hide\" type=\"text\" id=\"receiveBreachAmount"+ i +"\" value=\""+ data[i].receiveBreachAmount +"\"/>";
//             html += "<input class=\"hide\" type=\"text\" id=\"breachAmount"+ i +"\" value=\""+ data[i].breachAmount +"\"/>";
//             html += "</p>";
//             html += "<p class=\"yw-table-filter\">";
//             html += "<label>待收贴现费用（元）:</label>";
//             html += "<input class=\"contractNum\" type=\"text\" id=\"needDiscountAmount"+ i +"\" value=\""+ data[i].discountAmount +"\" readonly/>";
//             html += "</p>";
//             html += "</div>";
//             html += "<div class=\"mb6 discount-mb6\">";
//             html += "<p class=\"yw-table-filter\">";
//             html += "<label>合同编号:</label>";
//             html += "<input type=\"text\" id=\"contractNo"+ i +"\" value=\""+ data[i].contractNo +"\" readonly/>";
//             html += "<input class=\"hide\" type=\"text\" id=\"contractId"+ i +"\" value=\""+ data[i].contractId +"\" readonly/>";
//             html += "<input class=\"hide\" type=\"text\" id=\"detailId"+ i +"\" value=\""+ data[i].id +"\" readonly/>";
//             html += "</p>";
//             html += "<p class=\"yw-table-filter\">";
//             html += "<label><i>*</i>收款金额（元）:</label>";
//             html += "<input class=\"receiveAmount\" type=\"text\" id=\"receiveAmount"+ i +"\"/>";
//             html += "</p>";
//             html += "<p class=\"yw-table-filter\">";
//             html += "<label><i>*</i>收贴现费用（元）:</label>";
//             html += "<input class=\"discountAmount\" type=\"text\" id=\"discountAmount"+ i +"\"/>";
//             html += "</p>";
//             html += "<p class=\"yw-table-filter hide discountCheckDom\">";
//             if (i === 0){
//                 html += "<input type=\"checkbox\" class=\"checkbox\" checked=\"checked\" id=\"discountCheck"+ i +"\"/> <span>抵扣</span>";
//             }else{
//                 html += "<input type=\"checkbox\" class=\"checkbox\" id=\"discountCheck"+ i +"\"/> <span>抵扣</span>";
//             }
//             html += "</p>";
//             html += "</div>";
//             html += "</fieldset>";
//             html += "</div>";
//             let _dynamicDiscountDom = document.getElementById("dynamicDiscountDom");
//             _dynamicDiscountDom.innerHTML = html;
//         }
//         DiscountReceive.setDiscountDomStyle(data.length);
//         DiscountReceive.setUnRequired(data.length);
//     },
//     queryDiscountContractList: function (v1, v2){
//         if (v1 === 'H' || v2 === 'T') {
//             var _ourCompanyName = $('#ourCompanyName').val();
//             var _companyName = $('#companyName').val();
//             var _contractId = $('#contractId').val();
//             if (_entityId === "0" && _ourCompanyName && _companyName) {
//                 $.ajax({
//                     url: _ctx + "/apply/receive/queryDiscountContractList",
//                     async: false,
//                     type: "POST",
//                     dateType: "json",
//                     contentType: 'application/json',
//                     data: JSON.stringify({'ourCompanyName': _ourCompanyName, 'companyName': _companyName, 'id': _contractId, 'payMode': v1, 'payType': v2}),
//                     success: function (data) {
//                         DiscountReceive.refreshDiscountList(data);
//                     }
//                 });
//             }
//         } else {
//             var resultData = [];
//             if (_$currContract) {
//                 let receiveData = {
//                     "id":null,
//                     "contractId": _$currContract.id,
//                     "contractNo": _$currContract.contractNo,
//                     "totalAmount": _$currContract.totalAmount,
//                     "payedAmount": _$currContract.dealedAmount,
//                     "breachAmount": _$currContract.breachAmount,
//                     "receiveBreachAmount": _$currContract.receiveBreachAmount
//                 }
//                 resultData.push(receiveData);
//             } else {
//                 let contractDetailListStr = $("#receiveDetailListStr").val();
//                 resultData = JSON.parse(contractDetailListStr);
//             }
//             DiscountReceive.refreshDiscountList(resultData);
//             $("#receiveAmount0").numberbox('setValue', $("#receiveAmount").numberbox('getValue'));
//         }
//     },
//     verifyShowDiscountFlg: function (v1, v2){
//         var _$receiveMode = !v1 ? $('#receiveMode').combobox('getValue') : v1;
//         var _$receiveType = !v2 ? $('#receiveType').combobox('getValue') : v2;
//         DiscountReceive.queryDiscountContractList(_$receiveMode, _$receiveType);
//         if (_$receiveMode == 'H') {
//             $('.discountDom').removeClass('hide');
//             $('.discountCheckDom').removeClass('hide');
//             $("#discountTarget").combobox({required: true});
//             $("#receiveAmount,#discountAmount").numberbox({required: true});
//             $("#receiveAcceptDate").datebox({required: true});
//         } else {
//             $('.discountDom').addClass('hide');
//             $('.discountCheckDom').addClass('hide');
//             $("#discountTarget").combobox({required: false});
//             $("#receiveAmount,#discountAmount").numberbox({required: false});
//             $("#receiveAcceptDate").datebox({required: false});
//         }
//     },
//     setDiscountDomStyle: function (domSize){
//         $('.contractNum').numberbox({
//             required: false,
//             precision:2,
//             min:0,
//             parser: function(val) {
//                 // 解析时去除千分位分隔符
//                 if(val) {
//                     return parseFloat(String(val).replace(/,/g, ''));
//                 } else {
//                     if(isNaN(val)) {
//                         return '';
//                     } else {
//                         return val;
//                     }
//                 }
//             },
//             formatter: function(val) {
//                 if (val || val === 0) {
//                     val = parseFloat(val).toFixed(2).toString().split(".");
//                     val[0] = val[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");
//                     return val.join(".");
//                 } else {
//                     if(isNaN(val)){
//                         return ""
//                     } else {
//                         return val;
//                     }
//                 }
//             }
//         });
//         for (let i = 0; i < domSize; i++) {
//             $("#receiveAmount" + i).numberbox({
//                 required: true,
//                 precision: 2,
//                 onChange: function (v1){
//                     DiscountReceive.updateReceiveAmount();
//                 },
//                 parser: function(val) {
//                     // 解析时去除千分位分隔符
//                     if(val) {
//                         return parseFloat(String(val).replace(/,/g, ''));
//                     } else {
//                         if(isNaN(val)) {
//                             return '';
//                         } else {
//                             return val;
//                         }
//                     }
//                 },
//                 formatter: function(val) {
//                     if (val || val === 0) {
//                         val = parseFloat(val).toFixed(2).toString().split(".");
//                         val[0] = val[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");
//                         return val.join(".");
//                     } else {
//                         if(isNaN(val)){
//                             return ""
//                         } else {
//                             return val;
//                         }
//                     }
//                 }
//             });
//         }
//         $('.checkbox').change(function () {
//             let check_id = $(this).attr('id');
//             let _domId = check_id.replace("discountCheck", "");
//             if ($(this).prop('checked')) {
//                 $(this).prop('checked', true);
//                 DiscountReceive.changeRequired(true, _domId);
//                 DiscountReceive.updateReceiveAmount();
//             } else {
//                 DiscountReceive.changeRequired(false, _domId);
//                 DiscountReceive.updateReceiveAmount();
//             }
//         });
//     },
//     setUnRequired: function (domSize){
//         for (let i = 1; i < domSize; i++) {
//             $("#receiveAmount" + i).numberbox({
//                 required: false,
//                 precision: 2,
//                 onChange: function (v1){
//                     DiscountReceive.updateReceiveAmount();
//                 },
//                 parser: function(val) {
//                     // 解析时去除千分位分隔符
//                     if(val) {
//                         return parseFloat(String(val).replace(/,/g, ''));
//                     } else {
//                         if(isNaN(val)) {
//                             return '';
//                         } else {
//                             return val;
//                         }
//                     }
//                 },
//                 formatter: function(val) {
//                     if (val || val === 0) {
//                         val = parseFloat(val).toFixed(2).toString().split(".");
//                         val[0] = val[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");
//                         return val.join(".");
//                     } else {
//                         if(isNaN(val)){
//                             return ""
//                         } else {
//                             return val;
//                         }
//                     }
//                 }
//             });
//         }
//     },
//     changeRequired: function (flg, domId){
//         $("#receiveAmount" + domId).numberbox({
//             required: flg,
//             precision: 2
//         });
//     },
//     updateReceiveAmount: function (){
//         var _currTotalAmount = 0;
//         var _currDiscountAmount = $("#discountAmount").numberbox("getValue");
//         var discountDomAll = document.querySelectorAll('#dynamicDiscountDom .discount-mb6');
//         for (let i = 0; i < discountDomAll.length; i++) {
//             let currCheckFlg = document.getElementById('discountCheck' + i).checked;
//             if (currCheckFlg){
//                 let currReceiveAmount = $('#receiveAmount' + i).numberbox("getValue");
//                 _currTotalAmount += Number(currReceiveAmount);
//             }
//         }
//         _currTotalAmount = Number(_currTotalAmount) + Number(_currDiscountAmount);
//         $("#receiveAmount").numberbox('setValue', _currTotalAmount);
//     },
// };