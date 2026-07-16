var StockVirtual = {
    chooseVirtualCallback: function (virtualRows) {
        if (virtualRows) {
            var virtual = virtualRows[0];
            StockVirtual.setPublicParam(virtual);
            _buyVirtualFlg = true;
            StockVirtual.setBuyVirtual(virtual);
        }
    },
    setPublicParam: function (virtual) {
        $("#stockVirtualId").val(virtual.id);
        // 我方
        ModuleUtil.comboboxReadOnly("#ourCompanyName", virtual.ourCompanyName);
        // 品种
        ModuleUtil.combotreeReadOnly("#productCd", virtual.productCd);
        ModuleUtil.inputReadOnly("#productName", virtual.productName);
        // 牌号
        StockVirtual.setBrandNumberData(virtual.productCd);
        ModuleUtil.comboboxReadOnly("#brandNumber", virtual.brandNumber);
        // 厂商
        ModuleUtil.comboboxReadOnly("#factoryName", virtual.factoryName);
        // 包装规格
        ModuleUtil.comboboxReadOnly("#wrapSpecs", virtual.wrapSpecs);
        // 质量标准
        ModuleUtil.comboboxReadOnly("#qualityStandard", virtual.qualityStandard);
        // 数量
        $("#dealNumber").numberbox("setValue", virtual.dealNumber);
        ModuleUtil.inputReadOnly("#maxNumber", virtual.dealNumber);
        $("#dealNumber").numberbox({
            required: true,
            validType: 'length[1,20]',
            precision: 3,
            max: virtual.dealNumber
        });
    },
    setBuyVirtual: function (virtual) {
        // 采购来源
        ModuleUtil.comboboxReadOnly("#buySource", virtual.virtualBuyType);
        // 供方
        StockVirtual.setBuyCompanyNameData(virtual.companyName);
        ModuleUtil.comboboxReadOnly("#buyCompanyName", virtual.companyName);
        ModuleUtil.inputReadOnly("#buyCompanyId", virtual.companyId)
        // 业务员
        ModuleUtil.inputReadOnly("#buyMatchUserId", virtual.matchUserId);
        ModuleUtil.combotreeReadOnly("#buyMatchUserName", virtual.matchUserName);
        // 结算方式
        ModuleUtil.comboboxReadOnly("#deliveryModeB", virtual.deliveryMode);
        // 支付方式
        ModuleUtil.comboboxReadOnly("#payType", virtual.payType);
        // 定金比例
        ModuleUtil.comboboxReadOnly("#payRate", virtual.payRate);
        // 定金
        ModuleUtil.numberboxReadOnly("#payRateAmount", virtual.payBondAmount);
        // 付全款日期
        ModuleUtil.dateboxReadOnly("#payFullTime", virtual.payFullTime);
        // 交货方式
        ModuleUtil.comboboxReadOnly("#buyDeliveryType", virtual.deliveryType);
        // 交货日期
        ModuleUtil.dateboxReadOnly("#buyDeliveryDate", virtual.deliveryDate);
        // 交货日期补充
        ModuleUtil.comboboxReadOnly("#BarrivalTimeExt", virtual.arrivalTimeExt);
        // 详细地址
        ModuleUtil.inputReadOnly("#bcontactAddr", virtual.contactAddr)
        // 仓储费
        $("#bwarehouseCost").numberbox('setValue', virtual.warehouseCost);
        // 运输费
        $("#btransportCost").numberbox('setValue', virtual.transportCost);
        // 含税单价
        let $buyPrice =  virtual.dealPrice;
        if (virtual.virtualBuyType === 'KC'){
            $buyPrice = virtual.minSellPrice;
        }
        let $buyTotalAmount = Math.round($buyPrice * virtual.dealNumber * 100) / 100;
        let $buyPriceNotax = Math.round($buyPrice / 1.13 * 100) / 100;
        ModuleUtil.numberboxReadOnly("#bdealPrice", $buyPrice)
        // 不含税单价
        $("#bdealAmountNotax").numberbox('setValue', $buyPriceNotax);
        // 总价
        ModuleUtil.numberboxReadOnly("#btotalAmount", $buyTotalAmount);
        // 补充条款
        $("#extraTerm").val(virtual.extraTerm);
        // 采购合同模板
        ModuleUtil.comboboxReadOnly("#buyTemplateId", virtual.templateId);
        // 采购合同模板附件ID
        $("#buyContentTemplateId").val(virtual.contentTemplateId);
        filePlugin = new FilePlugin('#buyContentTemplateId',$('#entityId').val(),'/apply/match/updateFileId',false);
        filePlugin.load(virtual.contentTemplateId,'#upbtempPath');
        // 补充条款
        $("#bextraTerm").val(virtual.extraTerm);
        // 备注
        $("#payRemark").val(virtual.remark);
        // 省市区
        var bsAreaId = virtual.bsAreaId;
        var deliveryAddr = virtual.deliveryAddr;
        $("#bdeliveryAddr").textbox("setValue",deliveryAddr);
        if (bsAreaId && deliveryAddr) {
            var areaIdList = bsAreaId.split("/");
            var areaNameList = deliveryAddr.split("/");
            var provinceId = areaIdList[0] == null ? "" : areaIdList[0];
            var provinceName = areaNameList[0] == null ? "" : areaNameList[0];
            var cityId = areaIdList[1] == null ? "" : areaIdList[1];
            var cityName = areaNameList[1] == null ? "" : areaNameList[1];
            var areaId = areaIdList[2] == null ? "" : areaIdList[2];
            var areaName = areaNameList[2] == null ? "" : areaNameList[2];
            StockVirtual.setBuyProvinceNameData(provinceId, cityId);

            ModuleUtil.comboboxReadOnly("#provinceName", provinceId);
            ModuleUtil.inputReadOnly("#provinceNameUtil", provinceName);

            ModuleUtil.comboboxReadOnly("#cityName", cityId);
            ModuleUtil.inputReadOnly("#cityNameUtil", cityName);

            ModuleUtil.comboboxReadOnly("#areaCode", areaId);
            ModuleUtil.inputReadOnly("#areaCodeUtil", areaName);
        }
    },
    setSellVirtual: function (virtual) {
        // 采购来源
        ModuleUtil.comboboxReadOnly("#sellSource", "S");
        // 需方
        StockVirtual.setSellCompanyNameData(virtual.companyName);
        ModuleUtil.comboboxReadOnly("#sellCompanyName", virtual.companyName);
        ModuleUtil.inputReadOnly("#sellCompanyId", virtual.companyId)
        // 业务员
        ModuleUtil.inputReadOnly("#sellMatchUserId", virtual.matchUserId);
        ModuleUtil.combotreeReadOnly("#sellMatchUserName", virtual.matchUserName);
        // 交货方式
        ModuleUtil.comboboxReadOnly("#sellDeliveryType", virtual.deliveryType);
        // 交货日期
        ModuleUtil.dateboxReadOnly("#sellDeliveryDate", virtual.deliveryDate);
        // 交货日期补充
        ModuleUtil.comboboxReadOnly("#SarrivalTimeExt", virtual.arrivalTimeExt);
        // 详细地址
        ModuleUtil.inputReadOnly("#contactAddr", virtual.contactAddr);
        // 结算方式
        ModuleUtil.comboboxReadOnly("#deliveryModeS", virtual.deliveryMode);
        // 仓储费
        $("#swarehouseCost").numberbox('setValue', virtual.warehouseCost);
        // 运输费
        $("#stransportCost").numberbox('setValue', virtual.transportCost);
        // 补充条款
        $("#extraTerm").val(virtual.extraTerm);
        // 付全款日期
        ModuleUtil.dateboxReadOnly("#receiveFullTime", virtual.payFullTime);
        // 销售价
        ModuleUtil.numberboxReadOnly("#sdealPrice", virtual.dealPrice)
        // 总价
        ModuleUtil.numberboxReadOnly("#stotalAmount", virtual.totalAmount)
        // 账期
        var dateDiff = new Date(virtual.payFullTime).getTime() - new Date(virtual.deliveryDate).getTime();
        var dayDiff = Math.ceil(dateDiff / (24 * 3600 * 1000)) + 1;
        $("#creditDays").numberbox('setValue', dayDiff);
        // 销售合同模板
        ModuleUtil.comboboxReadOnly("#sellTemplateId", virtual.templateId);
        // 销售合同模板附件ID
        $("#sellContentTemplateId").val(virtual.contentTemplateId);
        sellFilePlugin = new FilePlugin('#sellContentTemplateId',$('#entityId').val(),'/apply/match/updateFileId',false);
        sellFilePlugin.load(virtual.contentTemplateId,'#upselltempPath');
        // 服务合同模板
        ModuleUtil.comboboxReadOnly("#serviceTemplateId", virtual.serviceTemplateId);
        // 服务合同模板附件ID
        $("#serviceContentTemplateId").val(virtual.serviceContentTemplateId);
        serviceFilePlugin = new FilePlugin('#serviceContentTemplateId',$('#entityId').val(),'/apply/match/updateFileId',false);
        serviceFilePlugin.load(virtual.serviceContentTemplateId,'#upsertempPath');
        // 服务合同我方抬头
        ModuleUtil.comboboxReadOnly("#serviceOurCompanyName", virtual.serviceOurCompanyName);
        // 补充条款
        $("#sextraTerm").val(virtual.extraTerm);
        // 备注
        $("#receiveRemark").val(virtual.remark);
        // 省市区
        var bsAreaId = virtual.bsAreaId;
        var deliveryAddr = virtual.deliveryAddr;
        $("#deliveryAddr").textbox("setValue",deliveryAddr);
        if (bsAreaId && deliveryAddr) {
            var areaIdList = bsAreaId.split("/");
            var areaNameList = deliveryAddr.split("/");
            var provinceId = areaIdList[0] == null ? "" : areaIdList[0];
            var provinceName = areaNameList[0] == null ? "" : areaNameList[0];
            var cityId = areaIdList[1] == null ? "" : areaIdList[1];
            var cityName = areaNameList[1] == null ? "" : areaNameList[1];
            var areaId = areaIdList[2] == null ? "" : areaIdList[2];
            var areaName = areaNameList[2] == null ? "" : areaNameList[2];
            StockVirtual.setSellProvinceNameData(provinceId, cityId);

            ModuleUtil.comboboxReadOnly("#provinceNames", provinceId);
            ModuleUtil.inputReadOnly("#provinceNamesUtil", provinceName);

            ModuleUtil.comboboxReadOnly("#cityNames", cityId);
            ModuleUtil.inputReadOnly("#cityNamesUtil", cityName);

            ModuleUtil.comboboxReadOnly("#areaCodes", areaId);
            ModuleUtil.inputReadOnly("#areaCodesUtil", areaName);
        }
    },
    setBrandNumberData: function (productCd) {
        var new_brandJson = _brandJson.filter(function (e) {
            return e.productCd == productCd && e.enterpriseId == 44;
        })
        $("#brandNumber").combobox({
            data: new_brandJson,
            required: true,
            limitToList: true,
            editable: true,
            textField: 'brandNumber',
            valueField: 'brandNumber',
            readonly: true
        });
    },
    setBuyCompanyNameData: function (companyName) {
        $("#buyCompanyName").combobox({
            url: _ctx + "/bs/company/listMyCompany/" + 0 + "/" + 1 + "?q=" + companyName + "&type=virtual",
            mode: 'remote',
            limitToList: true,
            valueField: 'companyName',
            textField: 'text',
            panelHeight: 300,
            required: true,
            readonly: true
        });
    },
    setSellCompanyNameData: function (companyName) {
        $("#sellCompanyName").combobox({
            url: _ctx + "/bs/company/listMyCompany/" + 0 + "/" + 2 + "?q=" + companyName + "&type=virtual",
            mode: 'remote',
            limitToList: true,
            valueField: 'companyName',
            textField: 'text',
            panelHeight: 300,
            required: true,
            readonly: true
        });
    },
    setBuyProvinceNameData: function (provinceId, cityId) {
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
    setSellProvinceNameData: function (provinceId, cityId) {
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
    }
}
var ModuleUtil = {
    comboboxReadOnly: function (id, value) {
        $(id).combobox("setValue", value);
        $(id).combotree('readonly', true);
    },
    combotreeReadOnly: function (id, value) {
        $(id).combotree("setValue", value);
        $(id).combotree('readonly', true);
    },
    numberboxReadOnly: function (id, value) {
        $(id).numberbox("setValue", value);
        $(id).numberbox('readonly', true);
    },
    dateboxReadOnly: function (id, value) {
        $(id).datebox("setValue", value);
        $(id).datebox('readonly', true);
    },
    inputReadOnly: function (id, value) {
        $(id).val(value);
        $(id).attr("readonly", true);
    }
}