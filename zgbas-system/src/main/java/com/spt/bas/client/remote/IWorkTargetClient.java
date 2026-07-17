package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.WorkTarget;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/work/target", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IWorkTargetClient extends BaseClient<WorkTarget> {

    @PostMapping("/findByBranchCdAndTargetMonth")
    String findByBranchCdAndTargetMonth(@RequestBody WorkTarget query);

    @PostMapping("/saveData")
    void saveData(@RequestBody WorkTarget query);

    @GetMapping("/deleteData")
    void deleteData(@RequestParam(value = "id", required = false) Long id);
}
