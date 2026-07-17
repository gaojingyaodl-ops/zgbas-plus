package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealBorrow;
import com.spt.bas.client.vo.SealBorrowSearchVo;
import com.spt.bas.client.vo.SealBorrowVo;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/seal/borrow", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface ISealBorrowClient extends BaseClient<SealBorrow> {

    @RequestMapping(value = "findBorrowPage")
    public PageDown<SealBorrow> findBorrowPage(@RequestBody SealBorrowSearchVo searchVo);

    @RequestMapping(value = "updateSealBorrow")
    public void updateSealBorrow(@RequestBody SealBorrowVo borrowVo);
}

