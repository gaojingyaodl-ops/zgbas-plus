package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.vo.ApplyDeliveryOutVo;
import com.spt.bas.client.vo.ApplyProductDetailVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.MidstreamVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;


@FeignClient(qualifier = "applyDeliveryOutClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/deliveryOut", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyDeliveryOutClient extends BaseClient<ApplyDeliveryOut> {

    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findPageDetail")
    PageDown<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo);

    /**
     *
     * @param applyNo
     * @return
     */
    @PostMapping("findByApplyNo")
    ApplyDeliveryOut findByApplyNo(@RequestBody String applyNo);

    @PostMapping("findEntity")
    ApplyDeliveryOut findEntity(@RequestBody Long approveId);

    @PostMapping("findByContractId")
    List<ApplyDeliveryOut> findByContractId(@RequestBody Long contractId);


    /**
     * 查询已出库未确认批次信息
     *
     * @param contractId
     * @return
     */
    @PostMapping("getUnConfirmDeliveryOut")
    List<ApplyProductDetailVo> getUnConfirmDeliveryOut(@RequestBody Long contractId);  
    
    /**
     * 查询已出库批次信息
     *
     * @param contractId
     * @return
     */
    @PostMapping("getAllDeliveryOut")
    List<ApplyProductDetailVo> getAllDeliveryOut(@RequestBody Long contractId); 
    
    /**
     * 查询中游已出库未确认批次信息
     *
     * @param contractId
     * @return
     */
    @PostMapping("getUnConfirmDeliveryOutDcsx")
    List<ApplyProductDetailVo> getUnConfirmDeliveryOutDcsx(@RequestBody Long contractId);

    /**
     * 查询详细
     * @param applyDeliveryOutId
     * @return
     */
    @PostMapping("findByApplyDeliveryOutId")
    ApplyProductDetail findByApplyDeliveryOutId(@RequestBody Long applyDeliveryOutId);

    @PostMapping("findByApplyDeliveryOutApplyNo")
    ApplyProductDetail findByApplyDeliveryOutApplyNo(@RequestBody String applyNo);



    @PostMapping("applyDeliveryOut")
    void applyDeliveryOut(@RequestBody ApplyDeliveryOutVo deliveryOutVo)throws WebApplicationException;

    /**
     *
     * 查询有效的出库审批单
     * @param contractId
     * @return
     */
    @PostMapping("findByContractIdNoStatusB")
    List<ApplyDeliveryOut> findByContractIdNoStatusB(@RequestBody Long contractId);


    @PostMapping("findByContractNo2")
    List<ApplyDeliveryOut> findByContractNo2(@RequestBody String contractNo);

    @PostMapping("generateApplyNo")
    ApplyDeliveryOut generateApplyNo(@RequestBody Long contractId);

    @PostMapping("generateFundRate")
    public BigDecimal generateFundRate(@RequestBody MidstreamVo midstreamVo);
}

