package com.spt.bas.web.controller.ctr;

import com.google.common.collect.Maps;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractLoss;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.ICtrContractLossClient;
import com.spt.bas.client.vo.CtrContractLossVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Controller
@RequestMapping(value = "/ctr/contractLoss")
public class CtrContractLossController extends PageController<CtrContractLoss, BaseVo>{
	@Autowired
	private ICtrContractLossClient  ctrContractLossClient;
	
	
	@Autowired
	private ICtrContractClient  ctrContractClient;

	@Override
	public BaseClient<CtrContractLoss> getService() {
		return ctrContractLossClient;
	}
	@RequestMapping(value = "content")
	public String content(PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		model.addAttribute("lossTypeJson",
				JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_LOSS_TYPE)));
		
		
		return "ctr/ctrContract-loss";
	}
	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id,PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		CtrContractLoss entity = getEntity(id);
		if (entity.getId() == null) {
			entity.setId(0L);
		}
		model.addAttribute("lossTypeFromTypeJson",
				JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_LOSS_TYPE)));
		model.addAttribute("lossTypeToTypeJson",
				JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_LOSS_TYPE)));

		model.addAttribute("entity", entity);
		return "ctr/ctrContract-loss-detail";
	}
	/**
	 * 查询所有信息
	 * @param searchVo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "findAllCtrContractLossLoading")
	public void findAllExternalPayLoading(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		
		PageDown<CtrContractLoss> page = ctrContractLossClient.findPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}

	/**
	 * 删除损耗登记信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "deleteContractLoss", method = RequestMethod.POST)
	public void deleteContractLoss(@RequestParam(value = "id") Long id,@RequestParam(value = "contractId") Long contractId, HttpServletResponse response){
		try {
			if(id != null && contractId != null){
				CtrContractLossVo vo = new CtrContractLossVo();
				vo.setContractId(contractId);
				vo.setId(id);
				vo.setEnableFlg(false);
				ctrContractLossClient.updateEnableFlg(vo);
				RenderUtil.renderSuccess("success", response);
			}
		} catch (Exception e){
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
	
	@ModelAttribute("preload")
	public CtrContractLoss getEntity(@RequestParam(value = "id", required = false) Long id) {
		CtrContractLoss entity = new CtrContractLoss();
//		entity.setStatus(BasConstants.APPROVE_STATUS_N);
		if (id != null && id != 0L) {
			entity = getService().getEntity(id);
		}
		return entity;
	}
	
		@Override
		public Map<String, Object> getDefaultFilter() {
			Map<String, Object> map = Maps.newHashMap();
			map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
			return map;
		}
		
		@RequestMapping(value = "updateLossFileId", method = RequestMethod.POST)
		public void updateLossFileId(FileIdUpdateVo vo,	HttpServletResponse response) {
			try {
				ctrContractLossClient.updateLossFileId(vo);
				RenderUtil.renderSuccess("success", response);
			} catch (Exception e) {
				logger.error("errorId:", e);
				RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
			}
		}
		
		// 保存/更新合同损耗数据
		@RequestMapping(value = "saveContractLoss")
		public void saveContractLoss(CtrContractLoss ctrContractLoss, HttpServletRequest request, HttpServletResponse response) {
			try {
				if(null != ctrContractLoss.getContractId()){
//					ctrContractLoss.setEnterpriseId(ShiroUtil.getEnterpriseId());
					//	CtrContractLoss entity = ctrContractLossClient.findByContractId(ctrContractLoss.getContractId());
						ctrContractLossClient.save(ctrContractLoss);
						//合并到合同附件
						if(null != ctrContractLoss.getContractId()){
							CtrContract ctrContract = ctrContractClient.getEntity(ctrContractLoss.getContractId());
							if(StringUtils.isNotEmpty(ctrContract.getFileId())){
								String[] split = ctrContract.getFileId().split(",");
								ctrContract.setFileId(split[0]+"," + ctrContractLoss.getFileId());
							} else{
								ctrContract.setFileId(ctrContractLoss.getFileId());
							}
							ctrContractClient.save(ctrContract);
						}
						RenderUtil.renderSuccess("success", response);
				}
			} catch (Exception e) {
				logger.error("saveContractLoss:", e);
				RenderUtil.renderFailure("saveContractLoss:" + e.getMessage(), response);
			}
		}
}
