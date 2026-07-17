package com.spt.bas.server.api.basData;

import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.vo.PageVo;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.ICtrContractSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "bas/data")
public class basDataApi {
    
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private ICtrContractService contractService;
    @Autowired
    private ICtrContractSettlementService contractSettlementService;

    @PostMapping("findByEnterpriseId")
    public List<BsCompany> findByEnterpriseId(@RequestBody Long enterpriseId) {
        return bsCompanyService.findByEnterpriseId(enterpriseId);
    }

    @PostMapping("findPageContract")
    public List<CtrContract> findPageContract(@RequestBody PageVo pageVo){
        Page<CtrContract> page = contractService.findCtrContractPage(PageRequest.of(pageVo.getPageIndex(), pageVo.getPerPageCount()));
        return page.getContent();
    } 
    
    @PostMapping("selectAllCount")
    public Integer selectAllCount(){
        return contractService.selectAllCount();
    }

    @PostMapping("findPageContractSettlement")
    public List<CtrContractSettlement> findPageContractSettlement(@RequestBody PageVo pageVo){
        Page<CtrContractSettlement> page = contractSettlementService.findContractSettlementPage(PageRequest.of(pageVo.getPageIndex(), pageVo.getPerPageCount()));
        return page.getContent();
    }

    @PostMapping("selectAllContractSettlementCount")
    public Integer selectAllContractSettlementCount(){
        return contractSettlementService.selectAllCount();
    }
    
}
