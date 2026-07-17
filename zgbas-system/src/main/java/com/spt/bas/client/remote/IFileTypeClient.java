package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.FileType;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 附件操作
 * @author shengong
 */
@FeignClient(qualifier="fileTypeClient", name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/fileType",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IFileTypeClient extends BaseClient<FileType>{

}

