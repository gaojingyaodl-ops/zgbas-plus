package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealUsage;
import com.spt.bas.client.vo.BusinessDeliveryExcelVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.client.vo.CtrContractOphisVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/contractOphis",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrContractOphisClient extends BaseClient<CtrContractOphisVo> {

//	@PostMapping("updateContractStatusByContractId")
//	public void updateContractStatusByContractId(@RequestBody CtrContractOphis ophis);

    @PostMapping(value = "addHis")
    void addHis(@RequestBody CtrContractOphisRequest request);

    @PostMapping(value = "getBusinessDelivery")
    BusinessDeliveryExcelVo getBusinessDelivery(@RequestBody Long approveId);
    // 添加合同盖章发起记录
    @PostMapping(value = "addSealStartHis")
    void addSealStartHis(@RequestBody PmApprove approve);
}

