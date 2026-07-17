package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.FileRecord;
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
@FeignClient(qualifier="fileRecordClient", name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/fileRecord",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IFileRecordClient extends BaseClient<FileRecord>{

    @PostMapping("findByFileId")
    FileRecord findByFileId(@RequestBody String fileId);

    @PostMapping("deleteByFileId")
    void deleteByFileId(@RequestBody String fileId);

    @PostMapping("findByFileIds")
    List<FileRecord> findByFileIds(@RequestBody List<String> fileIds);

}

