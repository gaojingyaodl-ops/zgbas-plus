package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.FileProcessRel;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 附件操作
 * @author shengong
 */
@FeignClient(qualifier="fileProcessRelClient", name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/fileProcessRel",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IFileProcessRelClient extends BaseClient<FileProcessRel>{

    /**
     * 查询审批单附件选项
     * @param processCode
     * @return
     */
    @PostMapping("findList")
    List<FileProcessRel> findList(@RequestBody String processCode);
}

