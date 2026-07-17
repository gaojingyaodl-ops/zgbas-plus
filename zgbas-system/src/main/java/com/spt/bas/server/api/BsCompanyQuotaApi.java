package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyQuota;
import com.spt.bas.server.service.IBsCompanyQuotaService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "bs/companyQuota")
public class BsCompanyQuotaApi extends BaseApi<BsCompanyQuota> {
    @Autowired
    private IBsCompanyQuotaService bsCompanyQuotaService;
    @Override
    public IDataService<BsCompanyQuota> getService() {
        return bsCompanyQuotaService;
    }

    /**
     * 获取最新完成了的额度申请
     * @param companyId
     * @return
     */
    @PostMapping("getLatestApply")
    public BsCompanyQuota getLatestApply(@RequestBody Long companyId){
        return bsCompanyQuotaService.getLatestApply(companyId);
    }
}
