/**
 * 
 */
package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.BsDictType;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.http.feign.FeignConfig;

/**
 * @author huangjian
 *
 */
@FeignClient(name =BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME+"/api/dict", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IBsDictClient extends BaseClient<BsDictType> {

	@PostMapping(value = "loadDatasByTypeCd")
	public List<BsDictData> loadDatasByTypeCd(@RequestBody String dictTypeCd, @RequestParam("enterpriseId") Long enterpriseId);

	@PostMapping(value = "existDictTypeCd")
	public boolean existDictTypeCd(@RequestBody String dictTypeCd, @RequestParam("dictCd") String dictTypeOld, @RequestParam("enterpriseId") Long enterpriseId);

	@PostMapping(value = "deleteData")
	public void deleteData(@RequestBody Long id);
	
	@PostMapping(value = "saveDatas")
	public void saveDatas(@RequestBody BatchSaveVo<BsDictData> batchSaveVo, @RequestParam("dictTypeId") Long dictTypeId);
}
