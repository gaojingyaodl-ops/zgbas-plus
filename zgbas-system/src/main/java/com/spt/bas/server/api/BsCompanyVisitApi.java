package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyQuotaV1;
import com.spt.bas.client.entity.BsCompanyVisit;
import com.spt.bas.client.vo.ApplyCompanyVisitVo;
import com.spt.bas.server.service.IBsCompanyVisitService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 田起立
 * @Date 2024/6/3 10:21
 * @Description:
 */
@RestController
@RequestMapping(value = "bs/companyVisit")
public class BsCompanyVisitApi extends BaseApi<BsCompanyVisit> {
    @Autowired
    private IBsCompanyVisitService iBsCompanyVisitService;
    @Override
    public IDataService<BsCompanyVisit> getService() {
        return iBsCompanyVisitService;
    }

    @RequestMapping("/getCompanyVisitById")
    public BsCompanyVisit getCompanyVisitById(@RequestParam Long id){
       return iBsCompanyVisitService.getCompanyVisitById(id);
    }
}
