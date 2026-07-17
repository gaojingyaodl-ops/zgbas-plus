/**
 * 
 */
package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.BsDictType;
import com.spt.bas.server.service.IBsDictService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.jpa.service.IBaseService;

/**
 * @author huangjian
 *
 */
@RestController
@RequestMapping(value = "api/dict")
public class BsDictApi extends BaseApi<BsDictType>{

	@Autowired
	private IBsDictService dictService;

	@Override
	public IBaseService<BsDictType> getService() {
		return dictService;
	}

	@PostMapping(value = "loadDatasByTypeCd")
	public List<BsDictData> loadDatasByTypeCd(@RequestBody String dictTypeCd,@RequestParam("enterpriseId") Long enterpriseId) throws ApplicationException{
		return dictService.loadDatasByTypeCd(dictTypeCd,enterpriseId);
	}
	
	@PostMapping(value = "saveDatas")
	public void saveDatas(@RequestBody BatchSaveVo<BsDictData> batchSaveVo,@RequestParam("dictTypeId") Long dictTypeId){
		dictService.saveDatas(batchSaveVo.getInsertedRecords(), batchSaveVo.getUpdatedRecords(),batchSaveVo.getDeletedRecords(), dictTypeId);
	}
	
	@PostMapping(value = "existDictTypeCd")
	public boolean existDictTypeCd(@RequestBody String dictTypeCd, @RequestParam("dictCd") String dictTypeOld,@RequestParam("enterpriseId") Long enterpriseId){
		return dictService.existDictTypeCd(dictTypeCd, dictTypeOld,enterpriseId);
	}
	
	@PostMapping(value = "deleteData")
	public void deleteData(@RequestBody Long id) {
		dictService.deleteData(id);
	}
}
