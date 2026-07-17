package com.spt.bas.web.controller.bs;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyShare;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsCompanyOphisClient;
import com.spt.bas.client.remote.IBsCompanyShareClient;
import com.spt.bas.client.vo.BsCompanyOphisVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/bs/companyShare")
public class BsCompanyShareController extends PageController<BsCompanyShare, BaseVo>{
	@Autowired
	private IBsCompanyClient companyClient;
	@Autowired
	private IBsCompanyShareClient companyShareClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IBsCompanyOphisClient bsCompanyOphisClient;
	@Override
	public BaseClient<BsCompanyShare> getService() {
		return companyShareClient;
	}

	@RequestMapping(value = "openShare/{id}/{matchUserId}", method = RequestMethod.GET)
	public String openShare(@PathVariable("id") Long id, @PathVariable("matchUserId") Long matchUserId, Model model) {
		model.addAttribute("companyId", id);
		model.addAttribute("matchUserId", matchUserId);
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("deptTree",JsonUtil.obj2Json(nodes.getChildren()));
		return "bs/company_share";
	}


	@RequestMapping(value = "listShare/{id}")
	public void listShare(@PathVariable("id") Long id, HttpServletResponse response) {
		if (id != null && id > 0) {
			 PageSearchVo queryVo =new PageSearchVo();
			 queryVo.setRows(100);
			 Map<String, Object> searchParams=new HashMap<>();
			 searchParams.put("EQL_companyId", id);
//			 searchParams.put("EQL_createUserId", ShiroUtil.getCurrentUserId());
			 searchParams.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
			 queryVo.setSearchParams(searchParams);
			 Page<BsCompanyShare> page = companyShareClient.findPage(queryVo);
			JsonEasyUI.renderListJson(response, page.getContent());
		} else {
			JsonEasyUI.renderListJson(response, new ArrayList<>(0));
		}
	}
	/**
	 * 共享
	 */
	@RequestMapping(value = "shareCompany", method = RequestMethod.POST)
	public void shareCompany(BsCompanyShare vo, HttpServletRequest request, HttpServletResponse response) {
		try{
			companyClient.shareCompany(vo);
			RenderUtil.renderText("success", response);
		}catch(Exception e){
			e.printStackTrace();
			RenderUtil.renderText("fail", response);
		}
	}
	@RequestMapping(value = "saveBatch")
	public String saveBatch(HttpServletRequest request, HttpServletResponse response) {
		Class<BaseVo> voClass = getVoClass();
		Class<BsCompanyShare> entityClass = getEntityClass();
		List<BsCompanyShare> listInserted;
		List<BsCompanyShare> listUpdated;
		List<BsCompanyShare> listDeleted;
		if (voClass != BaseVo.class) {
			List<BaseVo> insertedRecords = JsonEasyUI.getInsertRecords(voClass, request);
			List<BaseVo> updatedRecords = JsonEasyUI.getUpdatedRecords(voClass, request);
			List<BaseVo> deletedRecords = JsonEasyUI.getDeletedRecords(voClass, request);
			listInserted = vo2Entity(insertedRecords);
			listUpdated = vo2Entity(updatedRecords);
			listDeleted = vo2Entity(deletedRecords);
		} else {
			listInserted = JsonEasyUI.getInsertRecords(entityClass, request);
			listUpdated = JsonEasyUI.getUpdatedRecords(entityClass, request);
			listDeleted = JsonEasyUI.getDeletedRecords(entityClass, request);
		}
		BatchSaveVo<BsCompanyShare> batchVo =new BatchSaveVo<>();
		batchVo.setDeletedRecords(listDeleted);
		batchVo.setInsertedRecords(listInserted);
		batchVo.setUpdatedRecords(listUpdated);
		preInsert(listInserted);
		preUpdate(listUpdated);
		getService().saveBatch(batchVo);
		RenderUtil.renderSuccess("保存成功", response);
		for (BsCompanyShare bsCompanyShare : listInserted) {
			BsCompany entity = companyClient.getEntity(bsCompanyShare.getCompanyId());
			SysUserSdk SysUserSdk = authOpenFacade.findUserById(bsCompanyShare.getSharedUserId());
			//添加新增操作记录
			BsCompanyOphisVo opHis = new BsCompanyOphisVo();
			opHis.setCompanyId(entity.getId());
			opHis.setCreateUserId(entity.getCreateUserId());
			opHis.setCreateUserName(bsCompanyShare.getCreateUserName());
			opHis.setStatus(entity.getStatus());
			opHis.setOptionType(BasConstants.COMPANY_STATUS_S);
			opHis.setEnterpriseId(entity.getEnterpriseId());
			opHis.setOperation("2");
			opHis.setTargetName("企业管理");
			opHis.setRemark(SysUserSdk.getNickName());
			bsCompanyOphisClient.addCompanyHis(opHis);
		}
		return null;
	}
	@ModelAttribute("preload")
	public BsCompanyShare getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				BsCompanyShare entity = new BsCompanyShare();
				entity.setId(0L);
				return entity;
			}
		}
		return null;
	}

	private void preUpdate(List<BsCompanyShare> listUpdated){
		for(BsCompanyShare e:listUpdated){
			preUpdate(e);
		}
	}
	protected void preUpdate(BsCompanyShare e){};
	private void preInsert(List<BsCompanyShare> listInserted){
		for(BsCompanyShare e:listInserted){
			preInsert(e);
		}
	}
	protected void preInsert(BsCompanyShare e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
		e.setCreateUserId(ShiroUtil.getCurrentUserId());
		e.setCreateUserName(ShiroUtil.getCurrentUserName());
	}
	private List<BsCompanyShare> vo2Entity(List<BaseVo> ListVo) {
		try {
			List<BsCompanyShare> list = new ArrayList<>(ListVo.size());
			for (BaseVo vo : ListVo) {
				BsCompanyShare entity = getEntityClass().newInstance();
				copyVo2Entity(vo, entity);
				list.add(entity);
			}
			return list;
		} catch (Exception e) {
			logger.error("vo2Entity error", e);
		}
		return null;
	}

	/** 将Vo属性copy到实体，用于保存 */
	protected void copyVo2Entity(BaseVo vo, BsCompanyShare entity) {
		entity.setId(vo.getId());
	}
}
