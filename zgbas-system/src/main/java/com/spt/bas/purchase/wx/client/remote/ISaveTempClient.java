package com.spt.bas.purchase.wx.client.remote;

import com.spt.bas.purchase.wx.client.constant.PurchaseWxConstant;
import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 微信端临时信息保存
 */
@FeignClient(name = PurchaseWxConstant.SERVER_NAME, path = PurchaseWxConstant.SERVER_NAME + "/purchase/saveTemp", url = PurchaseWxConstant.SERVER_URL, configuration = FeignConfig.class)
public interface ISaveTempClient extends BaseClient<SaveInfo> {

    /**
     * 审核后临时保存信息
     * @param type
     * @param userId
     */
    @PostMapping(value = "commitTempInfo")
    void commitTempInfo(@RequestParam("type") String type, @RequestParam("userId") Long userId);

    @PostMapping(value = "getEntrustInfo")
    SaveInfo getEntrustInfo(Long wxUserId);

    @PostMapping(value = "getInfoByType")
    SaveInfo getInfoByType(@RequestParam("wxUserId") Long wxUserId, @RequestParam("saveType") String saveType);

    /**
     * 查询临时保存信息
     *
     * @param companyId
     * @param isCommit
     * @param type
     * @return
     */
    @PostMapping(value = "getInfoByCompanyId")
    SaveInfo getInfoByCompanyId(@RequestParam("companyId") Long companyId,
                                @RequestParam("isCommit") Boolean isCommit,
                                @RequestParam("type") String type);

    @PostMapping(value = "getInfoByCompanyId2")
    SaveInfo getInfoByCompanyId2(@RequestParam("companyId") Long companyId, @RequestParam("type") String type);

    /**
     * 查询临时保存信息
     *
     * @param companyId
     * @param isCommit
     * @param types
     * @return
     */
    @PostMapping(value = "getInfosByCompanyId")
    List<SaveInfo> getInfosByCompanyId(@RequestParam("companyId") Long companyId,
                                       @RequestParam("isCommit") Boolean isCommit,
                                       @RequestParam("types") String types);

}
