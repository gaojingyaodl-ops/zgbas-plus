package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockVirtual;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: gaojy
 * @create 2022/5/9 10:45
 * @version: 1.0
 * @description:
 */
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/stock/virtual",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IStockVirtualClient extends BaseClient<StockVirtual> {
    @PostMapping("updateFileId")
    void updateFileId(FileIdUpdateVo vo);

    @PostMapping("existEnableVirtual")
    boolean existEnableVirtual();

    @PostMapping("updateStockVirtual")
    void updateStockVirtual(@RequestBody StockVirtual stockVirtual);

    @PostMapping("invalidStockVirtual")
    void invalidStockVirtual(@RequestBody Long stockVirtualId);
}
