package com.spt.bas.purchase.wx.client.remote;

import com.spt.bas.purchase.wx.client.constant.PurchaseWxConstant;
import com.spt.bas.purchase.wx.client.entity.CompanyUser;
import com.spt.bas.purchase.wx.client.vo.CompanyOnLineApplyVo;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = PurchaseWxConstant.SERVER_NAME,path= PurchaseWxConstant.SERVER_NAME+"/purchase/user",url=PurchaseWxConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IWxUserClient extends BaseClient<CompanyUser> {

    @PostMapping(value = "saveApplyOnLineData")
    RespVo<CompanyUser> saveApplyOnLineData(@RequestBody CompanyOnLineApplyVo vo);
}
