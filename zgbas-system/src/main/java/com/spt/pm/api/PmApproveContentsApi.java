package com.spt.pm.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.service.IPmApproveContentsService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;


@RestController
@RequestMapping(value = "pm/contents")
public class PmApproveContentsApi extends BaseApi<PmApproveContents> {
	@Autowired
	private IPmApproveContentsService pmApproveContentsService;
	
	@Override
	public IBaseService<PmApproveContents> getService() {
		return pmApproveContentsService;
	}
	
	@RequestMapping(value = "updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		pmApproveContentsService.updateFileId(vo.getId(),vo.getFileId());
	}

	@RequestMapping(value = "findByApproveId")
	public PmApproveContents findByApproveId(@RequestBody Long approveId) {
		PmApproveContents contents = pmApproveContentsService.findByApproveId(approveId);
		return contents;
	}

	@RequestMapping(value = "findByRealApproveId")
	public List<PmApproveContents> findByRealApproveId(@RequestBody Long approveId) {
		return pmApproveContentsService.findByRealApproveId(approveId);
	}
}

