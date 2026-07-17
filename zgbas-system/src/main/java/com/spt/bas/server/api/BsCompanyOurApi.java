package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyOur;
import com.spt.bas.client.vo.BsCompanyOurSearchVo;
import com.spt.bas.server.service.IBsCompanyOurService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "bs/companyOur")
public class BsCompanyOurApi extends BaseApi<BsCompanyOur> {
    @Autowired
    private IBsCompanyOurService bsCompanyOurService;

    @Override
    public IBaseService<BsCompanyOur> getService() {
        return bsCompanyOurService;
    }

    @PostMapping(value = "getCompanyOurDetail")
    public BsCompanyOur getCompanyOurDetail(@RequestBody BsCompanyOurSearchVo searchVo) {
        return bsCompanyOurService.findByCompanyName(searchVo.getCompanyName());
    }
}
