package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractApply;
import com.spt.bas.client.entity.CtrProduct;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author MoonLight
 * @Date 2023/2/27 10:29
 * @Version 1.0
 */
@Setter
@Getter
public class CtrContractProfitVo {

    /**
     * 代采赊销中游合同标识
     */
    private boolean chargeFlg = false;

    /**
     * 采购合同
     */
    private CtrContract buyContract;

    /**
     * 中间链采购合同
     */
    private CtrContract specialBuyContract;

    /**
     * 销售合同
     */
    private CtrContract sellContract;

    /**
     * 采购合同商品明细
     */
    private CtrProduct buyProduct;

    /**
     * 中间链采购合同商品明细
     */
    private CtrProduct specialBuyProduct;

    /**
     * 销售合同商品明细
     */
    private CtrProduct sellProduct;

    /**
     * 中游合同
     */
    private ApplyCtrDCSX applyCtrDCSX;

    /**
     * 代采赊销中间链条合同列表
     */
    private List<ApplyCtrDCSX> dcsxList;


    private CtrContractApply buyContractApply;

    private CtrContractApply specialBuyContractApply;

    private CtrContractApply sellContractApply;

}
