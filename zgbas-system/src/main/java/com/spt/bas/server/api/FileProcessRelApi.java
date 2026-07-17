package com.spt.bas.server.api;

import com.spt.bas.client.entity.FileProcessRel;
import com.spt.bas.server.service.IFileProcessRelService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 附件记录
 * @author shengong
 */
@RestController
@RequestMapping(value = "bs/fileProcessRel")
public class FileProcessRelApi extends BaseApi<FileProcessRel> {
	@Autowired
	private IFileProcessRelService fileProcessRelService;

	@Override
	public IDataService<FileProcessRel> getService() {
		return fileProcessRelService;
	}

	@PostMapping("findList")
	public List<FileProcessRel> findList(@RequestBody String processCode){
		return fileProcessRelService.findList(processCode);
	}

}

