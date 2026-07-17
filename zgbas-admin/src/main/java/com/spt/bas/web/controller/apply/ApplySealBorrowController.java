package com.spt.bas.web.controller.apply;

import com.beust.jcommander.internal.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.SealBorrow;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.client.remote.ISealBorrowClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.SealBorrowSearchVo;
import com.spt.bas.client.vo.SealBorrowVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 印章外借申请单
 */
@Controller
@RequestMapping(value = "/apply/sealBorrow")
public class ApplySealBorrowController extends PageController<SealBorrow, BaseVo> {
	@Autowired
	private ISealBorrowClient sealBorrowClient;
	@Autowired
	private IPmApproveContentsClient pmApproveContentsClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Resource
	private WebParamUtils webParamUtils;

	@Override
	public BaseClient<SealBorrow> getService() {
		return sealBorrowClient;
	}

	@RequestMapping(value = "")
	public String index(Model model,HttpServletRequest request) {
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		// 印章-公司名称
		model.addAttribute("customerNameJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
		// 印章外借-物品类型
		model.addAttribute("itemTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ITEM_TYPE)));
		// 印章外借-印章状态
		model.addAttribute("sealStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SEAL_STATUS)));
		//印章外借归还权限
		model.addAttribute("returnSealFlg", ShiroUtil.isPermitted(PermissionEnum.PERM_SEAL_BORROW.getPermissionCode()));
		return "seal/seal_borrow";
	}

	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,
			HttpServletRequest request) {
		String processCode = request.getParameter("processCode");
		SealBorrow entity = getEntity(id, processCode);
		// 印章-公司名称
		model.addAttribute("customerNameJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
		// 印章外借-物品类型
		model.addAttribute("itemTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ITEM_TYPE)));
		//获取业务员树
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		model.addAttribute("entity", entity);
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

		model.addAttribute("psv", permissionVo);
		model.addAttribute("status",getStatus(id));
		return "apply/seal-borrow";
	}

	@RequestMapping(value = "findBorrowPage")
	public void findBorrowPage(SealBorrowSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Page<SealBorrow> page = sealBorrowClient.findBorrowPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}

	/**
	 * 跳转至印章归还页面
	 * @param id
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "toReturnSeal", method = RequestMethod.GET)
	public String toReturnSeal(@RequestParam(value="id") Long id,Model model,HttpServletRequest request) {
		SealBorrow entity = sealBorrowClient.getEntity(id);
		// 印章外借-物品类型
		model.addAttribute("itemTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ITEM_TYPE)));
		// 印章-公司名称
		model.addAttribute("customerNameJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
		model.addAttribute("entity", entity);
		return "seal/seal_return_borrow";
	}

	/**
	 *  印章外借归还
	 * @param borrow
	 * @param response
	 */
	@RequestMapping(value = "returnSeal", method = RequestMethod.POST)
	public void returnSeal(SealBorrow borrow, HttpServletResponse response) {
		Long id = borrow.getId();
		String itemType = borrow.getItemType();
		if (id == null || id == 0L) {
			RenderUtil.renderFailure("sealBorrow id is null !", response);
			return;
		}
		if (StringUtils.isBlank(itemType)) {
			RenderUtil.renderFailure("itemType is null !", response);
			return;
		}
		try {
			SealBorrow entity = sealBorrowClient.getEntity(id);
			if (entity != null) {
				SealBorrowVo vo = new SealBorrowVo();
				vo.setOpType(BasConstants.DICT_TYPE_SEAL_STATUS_R);
				vo.setSealBorrowId(entity.getId());
				vo.setOpUserId(ShiroUtil.getCurrentUserId());
				vo.setOpUserName(ShiroUtil.getCurrentUserName());
				vo.setReturnItemType(itemType);
				vo.setRemark(borrow.getRemark());
				sealBorrowClient.updateSealBorrow(vo);
				RenderUtil.renderSuccess("success", response);
			}
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
		try {
			pmApproveContentsClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(SealBorrowSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		Page<SealBorrow> page = sealBorrowClient.findBorrowPage(searchVo);
		Page<SealBorrow> pageVo = preSealBorrowData(page);
		String title = "印章外借记录";
		String[] attrs = new String[] { "itemType", "companyName", "keepUserName", "applyUserName", "reason", "address",
				"sealStatus", "startDate", "endDate", "returnDate" };
		String[] titles = new String[] { "印章类型", "公司名称", "保管人", "申请人", "外借原因", "外借地点", "印章状态", "借出日期", "归还日期",
				"实际归还日期" };
		int[] widths = new int[] { 15, 20, 15, 15, 20, 20, 15, 15, 15, 15 };
		Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
		// 生成一个表格
		Sheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为 15 个字节
		sheet.setDefaultColumnWidth(15);
		// 产生表格标题行
		// 生成一个样式
		CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
		/** 创建表头 */
		int[] widthes = new int[titles.length];
		for (int i = 0; i < titles.length; i++) {
			widthes[i] = widths[i];
		}
		PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
		int start = 0;
		while (pageVo != null && pageVo.getContent().size() > 0) {
			PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
					DateOperator.FORMAT_STR_WITH_TIME);
			if (pageVo.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				page = sealBorrowClient.findBorrowPage(searchVo);
				pageVo = preSealBorrowData(page);
				start += batchSize;
			} else {
				pageVo = null;
			}
		}

		try {
			PoiExcelUtil.write(workbook, response, title);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}

	@ModelAttribute("preload")
	public SealBorrow getEntity(@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "processCode", required = false) String processCode) {
		SealBorrow enObject = null;
		if (id != null) {
			if (id > 0 && StringUtils.isNotBlank(processCode)) {
				enObject = (SealBorrow) ProcessControlUtil.getEntity(id, processCode);
				if (enObject != null) {
					enObject.setId(id);
				}
				return enObject;
			} else {
				SealBorrow sealBorrow = new SealBorrow();
				sealBorrow.setStartDate(new Date());
				sealBorrow.setEndDate(new Date());
				sealBorrow.setId(0L);
				return sealBorrow;
			}
		}
		return enObject;
	}

	private String getStatus(Long id) {
		if (id != null && id > 0L) {
			PmApproveContents entity = pmApproveContentsClient.getEntity(id);
			if (entity != null) {
				return entity.getStatus();
			}
		}
		return BasConstants.APPROVE_STATUS_N;
	}

	private Page<SealBorrow> preSealBorrowData(Page<SealBorrow> pageVo){
		if(pageVo!=null && pageVo.getContent().size()>0){
			for (SealBorrow borrow : pageVo.getContent()) {
				String itemType = borrow.getItemType();
				String companyName = borrow.getCompanyName();
				String sealStatus = borrow.getSealStatus();
				String itemTypeNameStr = "";
				String[] splitItem = itemType.split(",");
				for (String item : splitItem) {
					itemTypeNameStr += DictUtil.getValue(BasConstants.DICT_TYPE_ITEM_TYPE, item) + ",";
				}
				if (itemTypeNameStr.length() > 1) {
					itemTypeNameStr = itemTypeNameStr.substring(0, itemTypeNameStr.length()-1);
				}
				borrow.setItemType(itemTypeNameStr);
				borrow.setCompanyName(DictUtil.getValue(BasConstants.DICT_TYPE_CUSTOMER_NAME, companyName));
				borrow.setSealStatus(DictUtil.getValue(BasConstants.DICT_TYPE_SEAL_STATUS, sealStatus));
			}
		}
		return pageVo;
	}
}
