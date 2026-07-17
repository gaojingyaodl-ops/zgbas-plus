package com.spt.bas.web.util;

import org.apache.commons.lang3.StringUtils;

import com.spt.bas.client.remote.IPmApproveClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.util.SpringContextHolder;

public class ProcessControlUtil {
	
	public static IPmEntity getEntity(Long id,String processCode) {
		IPmEntity pmEntity = null;
		try {
			IPmApproveContentsClient pmApproveContentsClient = SpringContextHolder.getBean(IPmApproveContentsClient.class);
			IPmProcessClient pmProcessClient = SpringContextHolder.getBean(IPmProcessClient.class);
			IPmApproveClient pmApproveClient = SpringContextHolder.getBean(IPmApproveClient.class);
			PmApproveContents entity = pmApproveContentsClient.getEntity(id);
			PmProcessSearchVo searchVo = new PmProcessSearchVo();
			searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
			searchVo.setProcessCode(processCode);
			PmProcess pmProcess = pmProcessClient.findByProcessCode(searchVo);
			if (StringUtils.isBlank(processCode)) {
				PmApprove pmApprove = pmApproveClient.getEntity(entity.getApproveId());
				pmProcess = pmProcessClient.getEntity(pmApprove.getProcessId());
			}
			if (entity != null && StringUtils.isNotBlank(entity.getContents())) {
				String contents = entity.getContents();
				pmEntity = (IPmEntity) JsonUtil.json2Object(Class.forName(pmProcess.getEntityName()), contents);
				pmEntity.setFileId(entity.getFileId());
				pmEntity.setApproveId(entity.getApproveId());
				pmEntity.setStatus(entity.getStatus());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return pmEntity;
	}
}
