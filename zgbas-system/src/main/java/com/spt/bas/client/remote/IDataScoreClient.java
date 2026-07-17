package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.DataScore;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author shengong
 */
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/dataScore",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IDataScoreClient extends BaseClient<DataScore> {

}
