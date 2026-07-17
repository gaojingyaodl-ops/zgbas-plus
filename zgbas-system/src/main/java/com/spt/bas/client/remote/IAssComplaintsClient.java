package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.AssComplaints;
import com.spt.bas.client.vo.AssComplaintsSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: gaojy
 * @create 2022/5/23 16:02
 * @version: 1.0
 * @description:
 */
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ass/complaints",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IAssComplaintsClient extends BaseClient<AssComplaints> {

    @PostMapping(value = "saveComplaints")
    AssComplaints saveComplaints(@RequestBody AssComplaints assComplaints);

    @PostMapping(value = "findComplaintsPage")
    PageDown<AssComplaints> findComplaintsPage(@RequestBody AssComplaintsSearchVo complaintsSearchVo);
}
