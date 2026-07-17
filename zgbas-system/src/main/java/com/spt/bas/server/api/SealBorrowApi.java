package com.spt.bas.server.api;

import com.spt.bas.client.entity.SealBorrow;
import com.spt.bas.client.vo.SealBorrowSearchVo;
import com.spt.bas.client.vo.SealBorrowVo;
import com.spt.bas.server.service.ISealBorrowService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "seal/borrow")
public class SealBorrowApi extends BaseApi<SealBorrow> {
    @Autowired
    private ISealBorrowService sealBorrowService;

    @Override
    public IBaseService<SealBorrow> getService() {
        return sealBorrowService;
    }

    @RequestMapping(value = "findBorrowPage")
    public Page<SealBorrow> findBorrowPage(@RequestBody SealBorrowSearchVo searchVo) {
        return sealBorrowService.findBorrowPage(searchVo);
    }

    @RequestMapping(value = "updateSealBorrow")
    public void updateSealBorrow(@RequestBody SealBorrowVo borrowVo) {
        sealBorrowService.updateSealBorrow(borrowVo);
    }
}

