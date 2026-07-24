package com.spt.bas.purchase.wx.server.service;

import com.spt.bas.client.entity.CtrContract;

/**
 * @Author: gaojy
 * @create 2021/12/9 18:05
 * @version: 1.0
 * @description:
 */
public interface ISuccessContractService {

    void doSuccessContract();

    void doSuccessDebtCertificate();

    void doReceiveGood();

    void doUploadContractSigned();

    void autoStartFactorSign(CtrContract ctrContract);
}
