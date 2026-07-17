package com.spt.bas.server.api;

import com.spt.bas.client.entity.FileRecord;
import com.spt.bas.server.service.IFileRecordService;
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
@RequestMapping(value = "bs/fileRecord")
public class FileRecordApi extends BaseApi<FileRecord> {
	@Autowired
	private IFileRecordService fileRecordService;

	@Override
	public IDataService<FileRecord> getService() {
		return fileRecordService;
	}

	@PostMapping("deleteByFileId")
	public void deleteByFileId(@RequestBody String fileId){
		fileRecordService.deleteByFileId(fileId);
	}

	@PostMapping("findByFileIds")
	List<FileRecord> findByFileIds(@RequestBody List<String> fileIds){
		return fileRecordService.findByFileIds(fileIds);
	}

	@PostMapping("findByFileId")
	FileRecord findByFileId(@RequestBody String fileId){
		return fileRecordService.findByFileId(fileId);
	}
}

