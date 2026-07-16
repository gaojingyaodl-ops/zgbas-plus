var BasCombobox = {
    onHidePanel: function (dom) {
        var _this = $(dom);
        var valueField = _this.combobox("options").valueField;
        var val = _this.combobox("getValue");  //当前combobox的值
        var allData = _this.combobox("getData");   //获取combobox所有数据
        var result = true;      //为true说明输入的值在下拉框数据中不存在
        for (var i = 0; i < allData.length; i++) {
            if (val == allData[i][valueField]) {
                result = false;
            }
        }
        if (result) {
            _this.combobox("clear");
        }
    }
}
var BasApply = {
    //合同详情
    contractDetail: function () {
        var contractNo = $("#contractNo").val();
        var title = contractNo;
        $.post(_ctx + '/bs/matchDetail/content4/' + contractNo, function (result) {
            if (result.source == 'MB' || result.source == 'MS') {
                if (result.businessType == 'ZY-TP') {
                    // 托盘
                    window.parent.addTab(title, _ctx + '/bs/matchDetail/contentTP/' + result.approveId);
                } else if (result.matchCreditFlg == "true") {
                    // 赊销
                    window.parent.addTab(title, _ctx + '/bs/matchDetail/content2/' + result.approveId);
                } else {
                    //代采
                    window.parent.addTab(title, _ctx + "/bs/matchDetail/content/" + result.approveId);
                }
            } else {
                //自营
                window.parent.addTab(title, _ctx + "/ctr/contract/detail/" + result.id);
            }
        });
    }
}
var VirtualDetail = {
    openVirtualApproveDetail: function () {
        let virtualApproveId = $("#virtualApproveId").val();
        let stockVirtualType = $("#stockVirtualType").val();
        var title = '协议采购:' + virtualApproveId;
        if (stockVirtualType === "KC") {
            title = '自营库存采购:' + virtualApproveId;
        }
        window.parent.addTab(title, _ctx + '/pm/approve/detail/' + virtualApproveId);
    }
}
