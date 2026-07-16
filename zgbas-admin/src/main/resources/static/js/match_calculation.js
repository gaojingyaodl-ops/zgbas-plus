/**
 * 代采、赊销计算公式
 * */
DCMatchCalculationPlugin = function (buyPriceDomId,
                                     premiumDomId,
                                     buyTotalAmountId,
                                     buyWarehouseAmountDomId,
                                     buyTransformAmountDomId,
                                     sellWarehouseAmountDomId,
                                     sellTransformAmountDomId,
                                     qingguanFeeDomId,
                                     totalNumberDomId,
                                     notTaxBuyPriceDomId,
                                     sellPriceDomId,
                                     sellTotalAmountDomId,
                                     grossProfitRateDomId,
                                     grossProfitDomId,
                                     buyCommissionDomId,
                                     sellCommissionDomId,
                                     companyCommissionDomId,
                                     marketingRetentionDomId) {
    // 含税采购单价
    this.buyPriceDomId = buyPriceDomId;
    // 数量
    this.totalNumberDomId = totalNumberDomId;
    // 不含税采购价
    this.notTaxBuyPriceDomId = notTaxBuyPriceDomId;
    // 销售总价
    this.buyTotalAmountId = buyTotalAmountId;
    // 采购仓储费
    this.buyWarehouseAmountDomId = buyWarehouseAmountDomId;
    // 采购运输费
    this.buyTransformAmountDomId = buyTransformAmountDomId;
    // 销售仓储费
    this.sellWarehouseAmountDomId = sellWarehouseAmountDomId;
    // 销售运输费
    this.sellTransformAmountDomId = sellTransformAmountDomId;
    // 加价
    this.premiumDomId = premiumDomId;
    // 销售单价
    this.sellPriceDomId = sellPriceDomId;
    // 销售总价
    this.sellTotalAmountDomId = sellTotalAmountDomId;
    // 利润（毛利）
    this.grossProfitDomId = grossProfitDomId;
    // 利润率
    this.grossProfitRateDomId = grossProfitRateDomId;
    this.buyCommissionDomId = buyCommissionDomId;
    this.sellCommissionDomId = sellCommissionDomId;
    this.companyCommissionDomId = companyCommissionDomId;
    this.marketingRetentionDomId = marketingRetentionDomId;
};

DCMatchCalculationPlugin.prototype = {
    load: function () {
        // 数量
        var totalNumber = Number($("#" + this.totalNumber).numberbox("getValue"));
        if (totalNumber <= 0) {
            return;
        }
        this.totalNumber = Number(totalNumber);
        console.log("总数量:" + this.totalNumber);
        // 采购含税单价
        var buyPrice = Number($("#" + this.buyPriceDomId).numberbox("getValue"));
        this.buyPrice = buyPrice;
        console.log("采购含税单价:" + this.buyPrice);
        // 采购不含税单价
        $("#" + this.notTaxBuyPriceDomId).numberbox("setValue", buyPrice / 1.13);
        // 采购总价
        var buyTotalAmount = totalNumber * buyPrice;
        this.buyTotalAmount = buyTotalAmount;
        console.log("采购总价:" + this.buyTotalAmount);
        $("#" + this.buyTotalAmountId).numberbox("setValue", buyTotalAmount);
        // 采购仓储费
        var buyWarehouseAmount = Number($("#" + this.buyWarehouseAmountDomId).numberbox("getValue"));
        this.buyWarehouseAmount = buyWarehouseAmount;
        console.log("采购仓储费:" + this.buyWarehouseAmount);
        // 采购运输费
        var buyTransformAmount = Number($("#" + this.buyTransformAmountDomId).numberbox("getValue"));
        this.buyTransformAmount = buyTransformAmount;
        console.log("采购运输费:" + this.buyTransformAmount);
        // 销售仓储费
        var sellWarehouseAmount = Number($("#" + this.sellWarehouseAmountDomId).numberbox("getValue"));
        this.sellWarehouseAmount = sellWarehouseAmount;
        console.log("销售仓储费:" + this.sellWarehouseAmount);
        // 销售运输费
        var sellTransformAmount = Number($("#" + this.sellTransformAmountDomId).numberbox("getValue"));
        this.sellTransformAmount = sellTransformAmount;
        console.log("销售运输费:" + this.sellTransformAmount);
        // 加价
        var premium = $("#" + premiumDomId).numberbox('getValue');
        if (isBlank(premium)) {
            premium = 0;
        }
        premium = Number(premium);
        // 销售仓储单价
        var transportCost_price;
        var warehouseCost_price;
        var warehouseCost = sellWarehouseAmount;
        // 销售运输单价
        var transportCost = sellTransformAmount;
        if (transportCost){
            transportCost_price = transportCost/totalNumber;
        }
        if (warehouseCost){
            warehouseCost_price = warehouseCost/totalNumber;
        }
        this.sellTransportPrice = transportCost_price;
        this.sellWarehousePrice = warehouseCost_price;
        console.log("销售仓储单价:" + this.sellWarehousePrice);
        console.log("销售运输单价:" + this.sellTransportPrice);
        // 销售价 = 仓储费单价+运输费单价+加价+含税采购价
        var deal_price = premium + Number(warehouseCost_price) + Number(transportCost_price) + Number(buyPrice);
        this.sellPrice = Number(deal_price);
        if (deal_price) {
            $("#" + this.sellPriceDomId).numberbox('setValue', deal_price);
        }
        console.log("销售单价:" + this.sellPrice);
        // 销售总价 = 销售价 * 数量
        var sTotalAmount = Number(deal_price) * Number(totalNumber).numberbox("getValue");
        this.sellTotalAmount = sTotalAmount;
        if (sTotalAmount) {
            $("#" + this.sellTotalAmountDomId).numberbox('setValue', sTotalAmount);
        }
        console.log("销售总价:" + this.sellTotalAmount);
        this.getGrossProfit();
    },
    getGrossProfit:function() {
        console.log("getGrossProfit()=========================")
        var transportCost_price;
        var warehouseCost_price;

        // 计算利润时，运输和仓储费用是采购和销售的合计
        var t_transportCost = Number(this.sellTransportPrice) + Number(this.buyTransformAmount);
        this.t_transportCost = t_transportCost;
        var t_warehouseCost = Number(this.sellWarehouseAmount) + Number(this.buyWarehouseAmount);
        this.t_warehouseCost = t_warehouseCost;
        if (transportCost) {
            transportCost_price = t_transportCost / this.totalNumber;
        }
        if (warehouseCost) {
            warehouseCost_price = t_warehouseCost / this.totalNumber;
        }
        this.SBTransportPrice = Number(transportCost_price);
        this.SBWarehousePrice = Number(warehouseCost_price);

        // 增值税
        var vatAmount = this.added_tax();

        // 印花税
        var printAmount = this.stampTax();

        // 附加税
        var surtax = this.surtax();

        // 利润 = 销售合同总价 - 采购合同总价-上下游仓储费/1.06-上下游运费/1.09- 增值税 - 附加税-印花税-清关费
        var grossProfit = Number(this.sellTotalAmount) - Number(this.buyTotalAmount) - Number(this.t_warehouseCost) / 1.06 - Number(this.t_transportCost) / 1.09 - Number(vatAmount) - Number(surtax) - Number(printAmount);
        this.grossProfit = grossProfit;
        console.log("利润 grossProfit : " + grossProfit);

        $("#" + this.grossProfitDomId).val(Number(this.grossProfit).toFixed(2));
        var grossProfitRate = Number(this.grossProfit / this.buyTotalAmount * 100).toFixed(2);
        $("#" + this.grossProfitRateDomId).val(grossProfitRate);

        // 采购提成
        $("#"+this.buyCommissionDomId).val(Number(Number(grossProfit) * Number(0.08).toFixed(2)));
        // 销售提成
        $("#"+this.sellCommissionDomId).val(Number(Number(grossProfit) * Number(0.29).toFixed(2)));
        // 营销留存
        $("#"+this.marketingRetentionDomId).val(Number(Number(grossProfit) * Number(0.05)).toFixed(2));
        // 公司净利
        $("#"+this.companyCommissionDomId).val(Number(Number(grossProfit) * Number(0.54)).toFixed(2));

    },
    //==============================================================================================
    // 增值税 = ((销售价-采购价)/1.13*0.13-上下游运费/1.09*0.09-上下游仓储费/1.06*0.06-销售价*保费比率/1.06*0.06)*数量
    added_tax :function () {
        var added_tax = ((Number(this.sellPrice) - Number(this.buyPrice)) / 1.13 * 0.13 - Number(this.SBTransportPrice) / 1.09 * 0.09 - Number(this.SBWarehousePrice) / 1.06 * 0.06 - Number(this.sellPrice) * Number(this.insuranceRatio(false, 0)) / 1.06 * 0.06) * Number(this.totalNumber);
        console.log("增值税:" + added_tax);
        return added_tax;
    },
    // 保险费率 = (回款周期<=30天) ？0.001 : 0.0012
    insuranceRatio : function (creditFlg,creditDays) {
        if (creditFlg == true) {
            return Number(0);
        }
        return Number(creditDays) <= 30 ? Number(0.001) : Number(0.0012);
    },
    // 印花税 = 销售合同总价 / 1.13 * 0.0003
    stampTax:function () {
        var stampTax = Number(this.sellTotalAmount) / 1.13 * 0.0003;
        console.log("印花税:" + stampTax);
        return stampTax;
    },
    // 附加税 = 增值税*0.1
    surtax:function () {
        var surtax = Number(this.added_tax()) * 0.1;
        console.log("附加税:" + surtax);
        return surtax;
    }

}



var MatchCalculation = {
    // 代采销售价 = 仓储费单价+运输费单价+加价+含税采购价+清关费单价
    dc_sell_price:function (sellWarehousePrice,sellTransformPrice,premium,buyPrice,qingguanFee) {

    },
    // 销售总价 = 销售价 * 数量
    dc_sell_total_amount:function (dc_sell_price,totalNumber) {

    },
    // 利润（毛利） = 销售合同总价 - 采购合同总价-上下游仓储费/1.06-上下游运费/1.09- 增值税 - 附加税-印花税-清关费
    dc_gross_profit:function (dc_sell_total_amount,dc_buy_total_amount,sell_transform_amount,buy_transform_amount,
                              sell_warehouse_amount,buy_warehouse_amount,) {

    },


    //==============================================================================================
    // 增值税 = ((销售价-采购价)/1.13*0.13-上下游运费/1.09*0.09-上下游仓储费/1.06*0.06-销售价*保费比率/1.06*0.06)*数量
    added_tax :function () {

    },





}
