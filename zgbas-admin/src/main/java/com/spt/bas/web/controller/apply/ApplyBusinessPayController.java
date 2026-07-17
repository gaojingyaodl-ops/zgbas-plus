package com.spt.bas.web.controller.apply;


import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LitigationCaseFeeEnum;
import com.spt.bas.client.entity.ApplyBusinessPay;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.FileRecord;
import com.spt.bas.client.entity.LitigationCase;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.report.client.entity.RptApplyBusinessPayVo;
import com.spt.bas.report.client.remote.IRptApplyBusinessPayRepClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/apply/businessPay")
public class ApplyBusinessPayController extends PageController<ApplyBusinessPay, BaseVo> {
	@Autowired
	private IApplyBusinessPayClient businessPayClient;
	@Autowired
	private IPmApproveClient pmApproveClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IRptApplyBusinessPayRepClient applyBusinessPayRepClient;

	@Autowired
	private  IFileRecordClient fileRecordClient;
    @Autowired
    private ILitigationCaseClient litigationCaseClient;

	@Override
	public BaseClient<ApplyBusinessPay> getService() {
		return businessPayClient;
	}

	/**
	 * 经营费用申请
	 * @param id
	 * @param permissionVo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "operating/content/{id}", method = RequestMethod.GET)
	public String operatingContent(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,HttpServletRequest request){
        ApplyBusinessPay entity = getEntity(id);
        String contractIdres = request.getParameter("contractId");
        String litigationCaseId = request.getParameter("litigationCaseId");
        String feeType = request.getParameter("feeType");
        if (!contractIdres.equals("null") && !contractIdres.equals("") && contractIdres != null) {
            Long contractId = Long.valueOf(contractIdres);
            CtrContract ctrContract = ctrContractClient.getEntity(contractId);
            entity.setCostType("14");//费用类别
            String ourCompanyName = ctrContract.getOurCompanyName();
            entity.setCompanyName(ourCompanyName);//我方企业
            entity.setContractId(contractId);//合同Id
        }
        // 适配诉讼案件 费用申请
        if (StringUtils.isNotEmpty(litigationCaseId)&&!litigationCaseId.equals("null")) {
            LitigationCase litigationCase = litigationCaseClient.getEntity(Long.valueOf(litigationCaseId));
            String linkContractIds = litigationCase.getLinkContractIds();
            String linkContractNos = litigationCase.getLinkContractNos();
            List<String> contractIdList = Arrays.asList(linkContractIds.split(","));
            CtrContract ctrContract = ctrContractClient.getEntity(Long.valueOf(contractIdList.get(0)));
            String remark = null;
            if (StringUtils.isNotEmpty(feeType)) {
				LitigationCaseFeeEnum enumByCode = LitigationCaseFeeEnum.getEnumByCode(feeType);
				switch (enumByCode) {
                    case ATTORNEY_FEE:
                        entity.setCostType("16");
                        entity.setDealAmount(litigationCase.getAttorneyFee());
                        remark =enumByCode.getName();
                        break;
                    case PROCESSING_FEE:
                        entity.setCostType("14");
                        entity.setDealAmount(litigationCase.getProcessingFee());
                        remark =enumByCode.getName();
                        break;
                    case PRESERVATION_FEE:
                        entity.setCostType("15");
                        entity.setDealAmount(litigationCase.getPreservationFee());
                        remark =enumByCode.getName();
                        break;
                    case LIABILITY_FEE:
                        entity.setCostType("14");
                        entity.setDealAmount(litigationCase.getLiabilityFee());
                        remark =enumByCode.getName();
                        break;
                }
                String ourCompanyName = ctrContract.getOurCompanyName();
                entity.setCompanyName(ourCompanyName);
                entity.setRemark(remark);
                entity.setLinkContractIds(linkContractIds);
                entity.setLinkContractNos(linkContractNos);
				entity.setLitigationCaseId(litigationCaseId);
				entity.setLitigationCaseType(feeType);
            }
        }
        setModel(model, permissionVo, entity);
        return "business/operating-business-content";
	}
	/**
	 * 退款费用申请
	 * @param id
	 * @param permissionVo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "refund/content/{id}", method = RequestMethod.GET)
	public String refundContent(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model){
		ApplyBusinessPay entity = getEntity(id);
		setModel(model, permissionVo, entity);
		return "business/refund-business-content";
	}

	/**
	 * 管理费用申请
	 * @param id
	 * @param permissionVo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "manage/content/{id}", method = RequestMethod.GET)
	public String manageContent(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model){
		ApplyBusinessPay entity = getEntity(id);
		setModel(model, permissionVo, entity);
		return "business/manage-business-content";
	}

	/**
	 * 内部调拨费用申请
	 * @param id
	 * @param permissionVo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "internal/content/{id}", method = RequestMethod.GET)
	public String internalContent(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model){
		ApplyBusinessPay entity = getEntity(id);
		setModel(model, permissionVo, entity);
		return "business/internal-business-content";
	}

	/**
	 * 外部往来费用申请
	 * @param id
	 * @param permissionVo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "external/content/{id}", method = RequestMethod.GET)
	public String externalContent(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model){
		ApplyBusinessPay entity = getEntity(id);
		setModel(model, permissionVo, entity);
		return "business/external-business-content";
	}


	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) {
		ApplyBusinessPay entity = getEntity(id);
		setModel(model, permissionVo, entity);
		return "apply/business-pay-content";
	}

	private Model setModel(Model model,PmPermissionVo permissionVo,ApplyBusinessPay entity){
		model.addAttribute("ownRegionType", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_OWN_REGION)));
		model.addAttribute("entity", entity);
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		// 印章-公司名称
		model.addAttribute("customerNameJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
		// 所属部门
		model.addAttribute("businessDeptTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_BUSINESS_DEPT_TYPE)));
		// 前台费用类别
		model.addAttribute("receptionCostTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_RECEPTION_COST_TYPE)));
		// 人事费用类别
		model.addAttribute("personnelCostTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_PERSONNEL_COST_TYPE)));
		// 后台商开费用类别
		model.addAttribute("backgroundCostTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_BACKGROUND_COST_TYPE)));
		// 经营费用申请-费用类别
		model.addAttribute("operatingCostTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_OPERATING_COST_TYPE)));
		// 业务退款申请-费用类别
		model.addAttribute("refundCostTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_REFUND_COST_TYPE)));
		// 管理费用申请-费用类别
		model.addAttribute("manageCostTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_MANAGE_COST_TYPE)));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);
		return model;
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
		try {
			businessPayClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	/**
	 * 使用@ModelAttribute, 实现Struts2
	 * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
	 */
	@ModelAttribute("preload")
	public ApplyBusinessPay getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyBusinessPay entity = new ApplyBusinessPay();
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				entity.setApplyDate(new Date());
				entity.setApplyUserName(ShiroUtil.getCurrentUserName());
				entity.setApplyUserId(ShiroUtil.getCurrentUserId());
				return entity;
			}
		}
		return null;
	}

	//费用查询
	@RequestMapping(value = "applyBusinessPay")
	public String applyBusinessPay(Model model) {
		 List<SysDictDataSdk> listByCategory=new ArrayList<>();
		 List<SysDictDataSdk> jyfy = DictUtil.getListByCategory(BasConstants.DICT_OPERATING_COST_TYPE);
		 List<SysDictDataSdk> glfy = DictUtil.getListByCategory(BasConstants.DICT_MANAGE_COST_TYPE);
		 List<SysDictDataSdk> fy = DictUtil.getListByCategory(BasConstants.DICT_PERSONNEL_COST_TYPE);
		 List<SysDictDataSdk> ywtkfy =DictUtil.getListByCategory(BasConstants.DICT_REFUND_COST_TYPE);
		 listByCategory.addAll(jyfy);
		 listByCategory.addAll(glfy);
		 listByCategory.addAll(fy);
		listByCategory.addAll(ywtkfy);
		ArrayList<SysDictDataSdk> collect = listByCategory.stream().collect(
				Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SysDictDataSdk::getDictName))), ArrayList::new));
		model.addAttribute("CostTypeJson",
				JsonUtil.obj2Json(collect));
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		return "apply/applyBusinessPay";
	}

	@RequestMapping(value = "applyBusinessPayList")
	public void applyBusinessPayList( RptApplyBusinessPayVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		logger.info("searchVo : " + JsonUtil.obj2Json(searchVo));
		Map<String, Object> footer = new HashMap<>();
		 String  eqs_costType =searchVo.getCostType();
		 String   costType2= searchVo.getCostType2();
		List<String> typeList = new ArrayList<>();
		if(StringUtils.isNotBlank(eqs_costType)&& StringUtils.isNotBlank(costType2)){
			List<String> idList = Arrays.stream(eqs_costType.split(",")).map(String::valueOf).collect(Collectors.toList());
			for (String s : idList) {
				if(StringUtils.equals("业务退款申请",costType2)){
					List<SysDictDataSdk> ywtkfy =DictUtil.getListByCategory(BasConstants.DICT_REFUND_COST_TYPE);
					for (SysDictDataSdk SysDictDataSdk : ywtkfy) {
						if(StringUtils.equals(SysDictDataSdk.getDictName(),s)){
							typeList.add(SysDictDataSdk.getDictCd());
						}
					}
				}
				if(StringUtils.equals("经营费用申请",costType2)){
					List<SysDictDataSdk> jyfy = DictUtil.getListByCategory(BasConstants.DICT_OPERATING_COST_TYPE);
					for (SysDictDataSdk SysDictDataSdk : jyfy) {
						if(StringUtils.equals(SysDictDataSdk.getDictName(),s)){
							typeList.add(SysDictDataSdk.getDictCd());
						}
					}
				}
				if(StringUtils.equals("管理费用申请",costType2)){
					List<SysDictDataSdk> glfy = DictUtil.getListByCategory(BasConstants.DICT_MANAGE_COST_TYPE);
					for (SysDictDataSdk SysDictDataSdk : glfy) {
						if(StringUtils.equals(SysDictDataSdk.getDictName(),s)){
							typeList.add(SysDictDataSdk.getDictCd());
						}
					}
				}
				if(StringUtils.equals("费用申请",costType2)){
					List<SysDictDataSdk> fy = DictUtil.getListByCategory(BasConstants.DICT_PERSONNEL_COST_TYPE);
					for (SysDictDataSdk SysDictDataSdk : fy) {
						if(StringUtils.equals(SysDictDataSdk.getDictName(),s)){
							typeList.add(SysDictDataSdk.getDictCd());
						}
					}
				}
			}
			if(typeList==null || typeList.size()==0){
				typeList.add("-1");
			}
			searchVo.setTypeList(typeList);
		}

		PageDown<RptApplyBusinessPayVo> page = applyBusinessPayRepClient.findPageContract(searchVo);
		List<RptApplyBusinessPayVo> content = page.getContent();
		BigDecimal sum=new BigDecimal(0);
		for (RptApplyBusinessPayVo vo : content) {
//			String dic = DictUtil.getValue(BasConstants.DICT_BUSINESS_DEPT_TYPE, vo.getBelogDept());
			String companyName = BsCompanyOurUtil.getValue(vo.getEnterpriseId(), vo.getCompanyName());
			vo.setCompanyName(companyName);
//			vo.setBelogDept(dic);
			PmApprove approve = pmApproveClient.findApproveNoByApproveId(vo.getApproveId());
			if(StringUtils.equals("业务退款申请",approve.getProcessName()) && StringUtils.isNotBlank(vo.getCostType())){
				String value = DictUtil.getValue(BasConstants.DICT_REFUND_COST_TYPE, vo.getCostType());
				vo.setCostType(value);
			}
			if(StringUtils.equals("经营费用申请",approve.getProcessName()) && StringUtils.isNotBlank(vo.getCostType())){
				 String value = DictUtil.getValue(BasConstants.DICT_OPERATING_COST_TYPE, vo.getCostType());
			     vo.setCostType(value);
			}
			if(StringUtils.equals("管理费用申请",approve.getProcessName()) && StringUtils.isNotBlank(vo.getCostType())){
				String value = DictUtil.getValue(BasConstants.DICT_MANAGE_COST_TYPE, vo.getCostType());
				vo.setCostType(value);
			}
			if(StringUtils.equals("费用申请",approve.getProcessName()) && StringUtils.isNotBlank(vo.getCostType())){
				String value = DictUtil.getValue(BasConstants.DICT_PERSONNEL_COST_TYPE, vo.getCostType());
				vo.setCostType(value);
			}
			    sum=sum.add(new BigDecimal(String.valueOf(vo.getDealAmount())));

		}
		footer.put("belogDept", "汇总");
		footer.put("dealAmount",sum);
		JsonEasyUI.renderJson(response, page, null, footer);
  }

    //展示附件
    @RequestMapping(value="fileList")
	public  String fileList (Model model ,HttpServletRequest request,HttpServletResponse response){
		 String fileId = request.getParameter("fileId");
		 Long aLong = Long.valueOf(fileId);
		 ApplyBusinessPay entity = businessPayClient.getEntity(aLong);
		 String sfile = entity.getFileId();
		 List<FileRecord> fileRecords=new ArrayList<>();
		 List<String> idList = Arrays.stream(sfile.split(",")).map(String::valueOf).collect(Collectors.toList());
		for (String s : idList) {
			FileRecord fileRecord = fileRecordClient.findByFileId(s);
			fileRecords.add(fileRecord);
		}
		model.addAttribute("list",fileRecords);
		 return "bs/fileList";
	}


	//导出
	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(RptApplyBusinessPayVo searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException, ParseException {
		System.out.println("-------导出模板方法-------");
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		String  eqs_costType =searchVo.getCostType();
		String   costType2= searchVo.getCostType2();
		List<String> typeList = new ArrayList<>();
		if(StringUtils.isNotBlank(eqs_costType)&& StringUtils.isNotBlank(costType2)){
			List<String> idList = Arrays.stream(eqs_costType.split(",")).map(String::valueOf).collect(Collectors.toList());
			for (String s : idList) {
				if(StringUtils.equals("业务退款申请",costType2)){
					List<SysDictDataSdk> ywtkfy =DictUtil.getListByCategory(BasConstants.DICT_REFUND_COST_TYPE);
					for (SysDictDataSdk SysDictDataSdk : ywtkfy) {
						if(StringUtils.equals(SysDictDataSdk.getDictName(),s)){
							typeList.add(SysDictDataSdk.getDictCd());
						}
					}
				}
				if(StringUtils.equals("经营费用申请",costType2)){
					List<SysDictDataSdk> jyfy = DictUtil.getListByCategory(BasConstants.DICT_OPERATING_COST_TYPE);
					for (SysDictDataSdk SysDictDataSdk : jyfy) {
						if(StringUtils.equals(SysDictDataSdk.getDictName(),s)){
							typeList.add(SysDictDataSdk.getDictCd());
						}
					}
				}
				if(StringUtils.equals("管理费用申请",costType2)){
					List<SysDictDataSdk> glfy = DictUtil.getListByCategory(BasConstants.DICT_MANAGE_COST_TYPE);
					for (SysDictDataSdk SysDictDataSdk : glfy) {
						if(StringUtils.equals(SysDictDataSdk.getDictName(),s)){
							typeList.add(SysDictDataSdk.getDictCd());
						}
					}
				}
				if(StringUtils.equals("费用申请",costType2)){
					List<SysDictDataSdk> fy = DictUtil.getListByCategory(BasConstants.DICT_PERSONNEL_COST_TYPE);
					for (SysDictDataSdk SysDictDataSdk : fy) {
						if(StringUtils.equals(SysDictDataSdk.getDictName(),s)){
							typeList.add(SysDictDataSdk.getDictCd());
						}
					}
				}
			}
			if(typeList==null || typeList.size()==0){
				typeList.add("-1");
			}
			searchVo.setTypeList(typeList);
		}

		PageDown<RptApplyBusinessPayVo> page = applyBusinessPayRepClient.findPageContract(searchVo);
		List<RptApplyBusinessPayVo> content = page.getContent();
		for (RptApplyBusinessPayVo vo : content) {
//			String dic = DictUtil.getValue(BasConstants.DICT_BUSINESS_DEPT_TYPE, vo.getBelogDept());
			String companyName = BsCompanyOurUtil.getValue(vo.getEnterpriseId(), vo.getCompanyName());
			vo.setCompanyName(companyName);
//			vo.setBelogDept(dic);
			PmApprove approve = pmApproveClient.findApproveNoByApproveId(vo.getApproveId());
			if(StringUtils.equals("业务退款申请",approve.getProcessName()) && StringUtils.isNotBlank(vo.getCostType())){
				String value = DictUtil.getValue(BasConstants.DICT_REFUND_COST_TYPE, vo.getCostType());
				vo.setCostType(value);
			}
			if(StringUtils.equals("经营费用申请",approve.getProcessName()) && StringUtils.isNotBlank(vo.getCostType())){
				String value = DictUtil.getValue(BasConstants.DICT_OPERATING_COST_TYPE, vo.getCostType());
				vo.setCostType(value);
			}
			if(StringUtils.equals("管理费用申请",approve.getProcessName()) && StringUtils.isNotBlank(vo.getCostType())){
				String value = DictUtil.getValue(BasConstants.DICT_MANAGE_COST_TYPE, vo.getCostType());
				vo.setCostType(value);
			}
			if(StringUtils.equals("费用申请",approve.getProcessName()) && StringUtils.isNotBlank(vo.getCostType())){
				String value = DictUtil.getValue(BasConstants.DICT_PERSONNEL_COST_TYPE, vo.getCostType());
				vo.setCostType(value);
			}
		}
		Page<RptApplyBusinessPayVo> pageVo = page;
		String title = "费用查询表";
		//指定格式 2021/2/25形式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String[] titles = new String[]{"所属部门", "申请人", "费用类型","费用类型(大类)", "金额", "公司名称", "申请日期","摘要","收款方", "银行名称","银行账户", "备注"};
		//应该是excel列属性
		String[] attrs = new String[]{"belogDept", "applyUserName", "costType", "costType2", "dealAmount","companyName",
				"applyDate", "subject", "receiveCompanyName", "bankName", "bankAccount",
				"remark"};
		int[] widths = new int[]{15, 15, 20, 15,15, 30, 20, 30, 20, 20, 20, 30};
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
			PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR);
			if (pageVo.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				page =  applyBusinessPayRepClient.findPageContract(searchVo);
				pageVo = page;
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

	}//end exportExcel



}
