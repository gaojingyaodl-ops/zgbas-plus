package com.spt.bas.server.api;

import com.spt.bas.client.entity.AssComplaints;
import com.spt.bas.client.vo.AssComplaintsSearchVo;
import com.spt.bas.server.service.IAssComplaintsService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: gaojy
 * @create 2022/5/23 16:00
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping(value = "ass/complaints")
public class AssComplaintsApi extends BaseApi<AssComplaints> {
    @Autowired
    private IAssComplaintsService assComplaintsService;

    @Override
    public IBaseService<AssComplaints> getService() {
        return assComplaintsService;
    }

    @PostMapping(value = "saveComplaints")
    public AssComplaints saveComplaints(@RequestBody AssComplaints assComplaints){
        return assComplaintsService.saveComplaints(assComplaints);
    }

    @PostMapping(value = "findComplaintsPage")
    Page<AssComplaints> findComplaintsPage(@RequestBody AssComplaintsSearchVo complaintsSearchVo){
        return assComplaintsService.findComplaintsPage(complaintsSearchVo);
    }
}
