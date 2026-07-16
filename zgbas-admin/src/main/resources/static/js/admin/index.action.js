/* 弹出新增框 */
function openWaitDeal(id) {
    var url = _ctx + "/approve/waitDeal/index/" + id;
    // $('#tth').window({href:url});
    // $('#tth').window("open");
    top.layer.open({
        title: '待办事项',
        type: 2,
        fix: false,
        shadeClose:true,
        area: ['850px', '500px'],
        // btn: ['确定', '关闭'],
        content: url
    });
}
function openStockInquiry(id) {
    var url = _ctx + "/bs/config/getStockInquiryList"
    // $('#tth').window({href:url});
    // $('#tth').window("open");
    top.layer.open({
        title: '库存查询',
        type: 2,
        fix: false,
        shadeClose:true,
        area: ['850px', '600px'],
        // btn: ['确定', '关闭'],
        content: url
    });
}
function openConfigMessage(){
    // $.post(_ctx+"/bs/config/getConfigMessageList",function(data){
    //     var html = '<p style="margin-left: 258px;font-size: 15px;">暂无可申请的赊销预算!</p>';
    //     if (data != null && data.length >= 1){
    //         html = '<div style="margin: 20px">';
    //         for (let i = 0; i < data.length; i++) {
    //             html += '<p class="mt10">'+data[i]+'</p>';
    //         }
    //         html += '</div>';
    //     }
    //     top.layer.open({
    //         title: '业务查询',
    //         type: 1,
    //         fix: false,
    //         shadeClose:true,
    //         area: ['750px', '320px'],
    //         btn: ['确定', '关闭'],
    //         content: html
    //     });
    //     // $.modal.open("重置密码", html, '770', '380');
    //     // $("#configHtml").html(html);
    //     // $('#configDiv').dialog('open');
    // });
    var url = _ctx + "/bs/config/getBsConfigList"
    // $('#tth').window({href:url});
    // $('#tth').window("open");
    top.layer.open({
        title: '业务链条：供应商 > 代采方 > 我方 > 客户',
        type: 2,
        fix: false,
        shadeClose:true,
        area: ['650px', '500px'],
        btn: ['确定', '关闭'],
        content: url
    });
}

function openManual() {
    addTab("用户手册", _ctx + '/bas/manual');
}

function openBusinessOverview() {
    addTab("业务总览", _ctx + '/business/overview/index');
}

function openFundFlow(type) {
    addTab("余额流水", _ctx + '/apply/fundRecharge/fundAmountFlow?type='+type);
}