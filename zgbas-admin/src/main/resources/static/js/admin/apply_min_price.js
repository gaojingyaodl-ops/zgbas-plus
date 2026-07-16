// 计算销售最低价
var MinPrice = {
    calculateMinPrice: function (flagType,oldBuyDealPrice) {
        if (_sellVirtualFlg) {
            return;
        }
        // 采购单价
        var _$buyPrice = $("#bdealPrice").numberbox("getValue");

        if(oldBuyDealPrice) {
            _$buyPrice = oldBuyDealPrice;
        }
        if(!_$buyPrice) {
            _$buyPrice = 0;
        }
        // 账期
        var _$creditDays;
        //贴现费用
        var _$discountAmount = 0;
        if (_dcFlg) {
            _$creditDays = 1;
        } else {
            _$creditDays = $("#creditDays").numberbox("getValue");
            _$creditDays = _$creditDays < 7 ? 7 : _$creditDays;
            _$discountAmount = $("#discountAmount").numberbox("getValue");
        }
        // 采购仓储费
        var _$buyWarehouseCost = $("#bwarehouseCost").numberbox("getValue");

        // 采购运输费
        var _$buyTransportCost = $("#btransportCost").numberbox("getValue");

        // 销售仓储费
        var _$sellWarehouseCost = $("#swarehouseCost").numberbox("getValue");

        // 销售运输费
        var _$sellTransportCost = $("#stransportCost").numberbox("getValue");

        // 数量
        var _$dealNumber = $("#dealNumber").numberbox("getValue");
        console.log("dcFlg:", _dcFlg);
        console.log("buyPrice:", _$buyPrice, "creditDays:", _$creditDays, "dealNumber:", _$dealNumber, "buyWarehouseCost:", _$buyWarehouseCost,
            "buyTransportCost:", _$buyTransportCost, "sellWarehouseCost:", _$sellWarehouseCost, "sellTransportCost:", _$sellTransportCost);
        if (!_$creditDays || !_$dealNumber) {
            console.warn("参数缺失，终止最低价计算!");
            return undefined;
        }
        var _ware_tran_stevedorage_amount =0;
        if(flagType==3){
            //采购装卸费
            var _$buyStevedorage = $("#bstevedorage").numberbox("getValue");
            //销售装卸费
            var _$sellStevedorage = $("#sstevedorage").numberbox("getValue");
            _ware_tran_stevedorage_amount =Number(_$sellStevedorage) + Number(_$buyStevedorage) + Number(_$buyWarehouseCost) + Number(_$buyTransportCost) + Number(_$sellWarehouseCost) + Number(_$sellTransportCost) + Number(_$discountAmount);
        }else{
             _ware_tran_stevedorage_amount =Number(_$buyWarehouseCost) + Number(_$buyTransportCost) + Number(_$sellWarehouseCost) + Number(_$sellTransportCost) + Number(_$discountAmount);
        }
       // 最低销售单价 = 最低毛利率(0.0007) * 采购单价 * 账期 + 采购单价 + (运输费 + 仓储费 + 贴现费) / 数量
        // 代采赊销最低销售单价 =  (1 + 0.0007 * 账期) * 采购价 + (运输费 + 仓储费 + 装卸费 + 贴现费) / 数量
        // 代采最低销售单价 =  (1 + 0.0007 * 账期) * 采购价 + (运输费 + 仓储费 + 贴现费) / 数量
        if (!_min_profit_rate && _dcFlg) {
            _min_profit_rate = 0.001;
        } else if (!_min_profit_rate && !_dcFlg) {
            _min_profit_rate = 0.0007;
        }
        console.log("minProfitRate:", _min_profit_rate);
        return parseFloat((Number(_min_profit_rate) * _$creditDays + 1) * _$buyPrice + (_ware_tran_stevedorage_amount / _$dealNumber)).toFixed(2);

    },
    setMinSellPriceDcsx(flg,oldBuyDealPrice) {
        // 代采赊销预算 最低销售价与加价 设值
        // 代采赊销预算加价逻辑 ==> 加价 = 销售价 - 最低销售价
        if (_sellVirtualFlg) {
            return;
        }
        // 
        var _$sdealPrice = $("#sdealPrice").numberbox("getValue");
        //最低销售价
        var flagType=3;
        // 如果oldBuyDealPrice不为空，计算的_min_deal_price是修改前的采购单价计算出来的最低销售价
        var _min_deal_price = MinPrice.calculateMinPrice(flagType,oldBuyDealPrice);
        var calculatePremiumflg = false;
        if(_$sdealPrice && oldBuyDealPrice) {
            if(_min_deal_price !== _$sdealPrice) {
                _min_deal_price = _$sdealPrice;
                calculatePremiumflg = true;
            } else {
                // 重新计算最低销售价
                _min_deal_price = MinPrice.calculateMinPrice(flagType);
            }
        }
        
        
        $("#sdealPrice").val(_min_deal_price);
        if (flg === true) {
            $("#premium").numberbox("setValue", parseFloat(_$sdealPrice - _min_deal_price).toFixed(2));
        } else {
            $("#sdealPrice").numberbox('setValue', _min_deal_price);
            // 计算加价
            if(calculatePremiumflg) {
                _min_deal_price = MinPrice.calculateMinPrice(flagType);
                $("#premium").numberbox("setValue", parseFloat(_$sdealPrice - _min_deal_price).toFixed(2));
            } else {
                $("#premium").numberbox('setValue', 0);
            }
        }
    },
    setMinSellPrice(flg) {
        // 赊销预算 最低销售价与加价 设值
        // 赊销预算 加价逻辑 ==> 加价 = 销售价 - 最低销售价
        if (_sellVirtualFlg) {
            return;
        }
        var _$sdealPrice = $("#sdealPrice").numberbox("getValue");
        //最低销售价
        var flagType=2;
        var _min_deal_price = MinPrice.calculateMinPrice(flagType);
        $("#sdealPrice").val(_min_deal_price);
        if (flg === true) {
            $("#premium").numberbox("setValue", parseFloat(_$sdealPrice - _min_deal_price).toFixed(2));
        } else {

            $("#sdealPrice").numberbox('setValue', _min_deal_price);
            $("#premium").numberbox('setValue', 0);
        }
    },
    setDcMinSellPrice(flg) {
        debugger;
        // 代采预算 最低销售价与加价 设值
        // 代采预算加价逻辑 ==> 加价 = 销售价 - 采购价 - (采购销售运输费、仓储费、装卸费)/合同数量
        if (_sellVirtualFlg) {
            return;
        }
        var flagType=1;
        var _min_deal_price = MinPrice.calculateMinPrice(flagType);
        $("#sdealPrice").val(_min_deal_price);

        var _$sdealPrice = $("#sdealPrice").numberbox("getValue");
        var _$bdealPrice = $("#bdealPrice").numberbox("getValue");
        if (!flg) {
            $("#sdealPrice").numberbox('setValue', _min_deal_price);
            $("#premium").numberbox("setValue", parseFloat(_min_deal_price - _$bdealPrice).toFixed(2));
        }else{
            var dealNumber = $("#dealNumber").numberbox("getValue");
            var logisticsCostAmount = MinPrice.getLogisticsCostTotal();
            var _$logisticsPrice = logisticsCostAmount / dealNumber;
            $("#premium").numberbox("setValue", parseFloat(_$sdealPrice - _$bdealPrice - _$logisticsPrice).toFixed(2));
        }
    },
    setGrossProfit(flg) {
        //采购总价
        var btotalAmount = $("#btotalAmount").numberbox("getValue");

        //销售总价
        var stotalAmount = $("#stotalAmount").numberbox("getValue");

        //贴现费用
        var discountAmount = $("#discountAmount").numberbox("getValue");

        // 数量
        var dealNumber = $("#dealNumber").numberbox("getValue");

        //毛利润 ：（销售总价-采购总价-运输费-装卸费-仓储费-贴现费）/吨数。
        var Amount = Number(stotalAmount) - Number(btotalAmount) - Number(discountAmount) - MinPrice.getLogisticsCostTotal();
        var grossProfit = Amount / dealNumber;
        $("#sgrossProfit").numberbox("setValue", parseFloat(grossProfit).toFixed(2));
    },
    getLogisticsCostTotal(){
        // 采购仓储费
        var _$buyWarehouseCost = $("#bwarehouseCost").numberbox("getValue");

        //采购装卸费
        var _$buyStevedorage = $("#bstevedorage").numberbox("getValue");

        // 采购运输费
        var _$buyTransportCost = $("#btransportCost").numberbox("getValue");

        // 销售仓储费
        var _$sellWarehouseCost = $("#swarehouseCost").numberbox("getValue");

        //销售装卸费
        var _$sellStevedorage = $("#sstevedorage").numberbox("getValue");

        // 销售运输费
        var _$sellTransportCost = $("#stransportCost").numberbox("getValue");

        return Number(_$buyWarehouseCost) + Number(_$buyTransportCost) + Number(_$buyStevedorage) + Number(_$sellWarehouseCost) + Number(_$sellTransportCost) + Number(_$sellStevedorage);
    }
}