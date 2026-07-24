package com.spt.bas.purchase.wx.server.controller;

import com.spt.bas.client.remote.ICtrContractLoadingClient;
import com.spt.bas.client.remote.ISignFileClient;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.service.ISuccessContractService;
import com.spt.bas.purchase.wx.server.service.IUserInfoService;
import com.spt.tools.core.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;



@RestController
@RequestMapping(value = "/axq")
public class OtherController {

    private static final Logger log = LoggerFactory.getLogger(OtherController.class);
    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private ISignFileClient signFileClient;

    @Autowired
    private ICtrContractLoadingClient ctrContractLoadingClient;

    @Resource
    private ISuccessContractService successContractService;

    /**
     * 1.签署成功后回调
     *
     * @return
     */
//    @GetMapping("/successContract/{contractNo}")
//    public String successContract(@PathVariable("contractNo") String contractNo) throws ApplicationException, IOException {
//        userInfoService.successContract(contractNo);
//        return "back";
//    }
    @PostMapping("/successContract/{contractNo}")
    public ApiResult successContract(@PathVariable("contractNo") String contractNo) throws ApplicationException, IOException {
        userInfoService.successContract(contractNo);
        return ApiResult.ofSuccess();
    }

    @PostMapping("/doSuccessContract")
    public ApiResult doSuccessContract() {
        log.info("--->doSuccessContract<---");
        successContractService.doSuccessContract();
        return ApiResult.ofSuccess();
    }

    /**
     * 2.签署成功后回调
     *
     * @return
     */
    @PostMapping("/successGoodReceive/{contractNo}")
    public ApiResult successGoodReceive(@PathVariable("contractNo") String contractNo) throws ApplicationException, IOException {
        userInfoService.successGoodReceive(contractNo);
        return ApiResult.ofSuccess();
    }

    /**
     * 3.应收账款债权-签署成功后回调
     *
     * @return
     */
    @RequestMapping("/successDebtCertificate/{contractNo}")
    public ApiResult successDebtCertificate(@PathVariable("contractNo") String contractNo) throws IOException {
        userInfoService.successDebtCertificate(contractNo);
        return ApiResult.ofSuccess();
    }

    /**
     * 4.提货单/配送单-签署成功后回调
     *
     * @return
     */
    @RequestMapping("/successLoadingBill/{contractNo}")
    public ApiResult successLoadingBill(@PathVariable("contractNo") String contractNo) throws IOException {
        ctrContractLoadingClient.refreshLoadingBillByContractNo(contractNo);
        return ApiResult.ofSuccess();
    }

    /**
     * 5.上传文件签署-签署成功后回调
     *
     * @return
     */
    @RequestMapping("/successSignFile/{contractNo}")
    public ApiResult successDebtCertifsuccessSignFileicate(@PathVariable("contractNo") String contractNo) throws ApplicationException {
        signFileClient.refreshSignFile(contractNo);
        return ApiResult.ofSuccess();
    }

}
