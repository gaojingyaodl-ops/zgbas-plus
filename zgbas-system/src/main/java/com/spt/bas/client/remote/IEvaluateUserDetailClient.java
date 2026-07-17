package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.EvaluateUserDetail;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/evaluate/user/detail",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IEvaluateUserDetailClient extends BaseClient<EvaluateUserDetail> {


    @PostMapping("/selectEvaluateUserDetailByIds")
    List<EvaluateUserDetail> selectEvaluateUserDetailByIds(@RequestParam(value = "ids") String ids);

    @PostMapping("/selectDetailByEvaluateUserId")
    List<EvaluateUserDetail> selectDetailByEvaluateUserId(@RequestParam(value = "ids") String ids);
}
