package com.spt.bas.purchase.wx.client.remote;

import com.spt.bas.purchase.wx.client.constant.PurchaseWxConstant;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = PurchaseWxConstant.SERVER_NAME,path= PurchaseWxConstant.SERVER_NAME+"/purchase/userDetail",url=PurchaseWxConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IWxUserDetailClient extends BaseClient<UserDetail> {

    @PostMapping(value = "findByCompanyIdAndIsBindTrue")
    UserDetail findByCompanyIdAndIsBindTrue(Long companyId);

    @PostMapping(value = "findByCompanyIdAndEnableFlgTrue")
    List<UserDetail> findByCompanyIdAndEnableFlgTrue(@RequestParam("companyId") Long companyId);

    @PostMapping(value = "findByCompanyIdAndIsBindTrueAndEnableFlgTrue")
    UserDetail findByCompanyIdAndIsBindTrueAndEnableFlgTrue(Long companyId);

    @PostMapping(value = "findByUserId")
    UserDetail findByUserId(Long userId);

    @PostMapping(value = "getUserPhone")
    String getUserPhone(Long userId);

    @PostMapping(value = "findByContactPhone")
    UserDetail findByContactPhone(String phone);


}
