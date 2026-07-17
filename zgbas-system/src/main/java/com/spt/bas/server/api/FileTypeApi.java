package com.spt.bas.server.api;

import com.spt.bas.client.entity.FileType;
import com.spt.bas.server.service.IFileTypeService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 附件记录
 * @author shengong
 */
@RestController
@RequestMapping(value = "bs/fileType")
public class FileTypeApi extends BaseApi<FileType> {
	@Autowired
	private IFileTypeService fileTypeService;

	@Override
	public IDataService<FileType> getService() {
		return fileTypeService;
	}
}

