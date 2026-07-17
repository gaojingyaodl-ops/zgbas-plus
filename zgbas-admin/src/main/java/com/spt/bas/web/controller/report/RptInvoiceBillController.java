package com.spt.bas.web.controller.report;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.ruoyi.common.utils.StringUtils;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.remote.IBsCompanyOurClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.vo.CtrContractFileDownloadVo;
import com.spt.bas.report.client.entity.RptInvoiceDetailExcel;
import com.spt.bas.report.client.entity.RptInvoiceInfoExcel;
import com.spt.bas.report.client.entity.RptInvoiceBill;
import com.spt.bas.report.client.remote.IRptInvoiceBillClient;
import com.spt.bas.report.client.vo.RptInvoiceBillSearchVo;
import com.spt.bas.web.excel.InvoiceStyleStrategy;
import com.spt.bas.web.excel.InvoiceWriteHandler;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 开票管理
 */
@Controller
@RequestMapping(value = "/rpt/invoiceBill")
public class RptInvoiceBillController extends PageController<RptInvoiceBill, BaseVo> {

	@Autowired
	private IRptInvoiceBillClient rptInvoiceBillClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Override
	public BaseClient<RptInvoiceBill> getService() {
		return rptInvoiceBillClient;
	}
	@Autowired
	private IBsCompanyOurClient bsCompanyOurClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@RequestMapping(value = "")
	public String index(Model model,HttpServletRequest request) {

		model.addAttribute("statisticalTypeJson",
				JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_STATISTICAL_TYPE)));
		//我方抬头
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		model.addAttribute("statisticsType", request.getParameter("statisticsType"));
		model.addAttribute("ourCompanyCd", request.getParameter("ourCompanyCd"));
		model.addAttribute("businessType", request.getParameter("businessType"));
		
		model.addAttribute("companyName", request.getParameter("companyName"));
		model.addAttribute("contractTimeBegin", request.getParameter("contractDateBegin"));
		model.addAttribute("contractTimeEnd", request.getParameter("contractDateEnd"));
		model.addAttribute("contractType", request.getParameter("contractType"));
		model.addAttribute("contractStatus", request.getParameter("contractStatus"));
		model.addAttribute("productType", request.getParameter("productType"));

		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));

		DeptSearchVo deptSearchVo = new DeptSearchVo();
		deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true, true);
		model.addAttribute("userNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		
		
		return "report/rptInvoiceBill";
	}

	/**
	 * 开票信息分页查询
	 * @param searchVo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "findRptInvoiceBillPage")
	public void findContractFinancePage(RptInvoiceBillSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
			searchVo.setFunderFlg(true);
			searchVo.setUserId(ShiroUtil.getCurrentUserId());
		}
		PageDown<RptInvoiceBill> page = rptInvoiceBillClient.findRptInvoiceBillPage(searchVo);
		JsonEasyUI.renderJson(response, page, null, null);
	}

	/**
	 * 开票信息EXCEL导出
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(RptInvoiceBillSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
			searchVo.setFunderFlg(true);
			searchVo.setUserId(ShiroUtil.getCurrentUserId());
		}
		PageDown<RptInvoiceBill> page = rptInvoiceBillClient.findRptInvoiceBillPage(searchVo);
		page = preContractData(page);
		String title = "开票管理";

		String[] titles = new String[]{"供方", "需方", "合同编号", "签约日期", "品种","牌号", "数量（吨）", "单价（元）", "合同单价（元）", "申请日期", "开票日期","开票金额","备注",
				"发票号码", "公司地址", "公司电话", "税号", "开户银行", "银行账号", "开票申请编号", "审批状态", "当前审批人", "中游合同编号", "中游开票状态"};
		String[] attrs = new String[]{"ourCompanyName", "companyName", "contractNo", "contractTime", "productName","brandNumber",
				"dealNumber", "dealPrice", "totalAmount", "applyDate", "invoiceDate","invoiceAmount","remark", "invoiceNo", "address", "companyPhone", "taxNo", "bankName", "bankAccount",
				"approveNo", "approveStatusName", "currApproveUserName", "dcsxContractNo", "dcsxInvoiceStatus"};
		int[] widths = new int[]{30, 30, 15, 20, 20, 15, 15, 15, 20, 15, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 15, 15};
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
		while (page != null && page.getContent().size() > 0) {
			PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR);
			if (page.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				page = rptInvoiceBillClient.findRptInvoiceBillPage(searchVo);
				start += batchSize;
			} else {
				page = null;
			}
		}
		try {
			PoiExcelUtil.write(workbook, response, title);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private PageDown<RptInvoiceBill> preContractData(PageDown<RptInvoiceBill> page){
		if (page != null && page.getContent().size() > 0) {
			for (RptInvoiceBill invoiceBill : page.getContent()) {
				String approveStatus = invoiceBill.getApproveStatus();
				if (StringUtils.equals(BasConstants.APPROVE_STATUS_A,approveStatus)) {
					invoiceBill.setApproveStatusName("审批中");
				} if (StringUtils.equals(BasConstants.APPROVE_STATUS_D,approveStatus)) {
					invoiceBill.setApproveStatusName("完成");
				}
			}
		}
		return page;
	}
	@ResponseBody
	@RequestMapping(value = "exportContractPdf")
	public void exportContractPdf(RptInvoiceBillSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
			searchVo.setFunderFlg(true);
			searchVo.setUserId(ShiroUtil.getCurrentUserId());
		}
		PageDown<RptInvoiceBill> page = rptInvoiceBillClient.findRptInvoiceBillPage(searchVo);
		List<RptInvoiceBill> content = page.getContent();

		List<CtrContractFileDownloadVo> fileDownloadVoList = new ArrayList<>();
		StringBuffer url = request.getRequestURL();
		String uri = request.getRequestURI();
		String domain = url.substring(0, url.indexOf(uri));
		logger.info("url:{}", url);
		logger.info("uri:{}", uri);
		logger.info("domain:{}", domain);

		if (CollectionUtils.isNotEmpty(content)) {
			CtrContractFileDownloadVo fileDownloadVo;
			for (RptInvoiceBill invoiceBill : content) {
				fileDownloadVo = new CtrContractFileDownloadVo();
				fileDownloadVo.setContractNo(invoiceBill.getContractNo());
				fileDownloadVo.setContractType(invoiceBill.getContractType());
				fileDownloadVo.setCompanyName(invoiceBill.getCompanyName());
				fileDownloadVo.setOurCompanyName(invoiceBill.getOurCompanyName());
				fileDownloadVo.setBuyContentFileId(invoiceBill.getBuyContentFileId());
				fileDownloadVo.setSellContentFileId(invoiceBill.getSellContentFileId());
				fileDownloadVo.setBusinessType(invoiceBill.getBusinessType());
				fileDownloadVo.setFileId(invoiceBill.getFileId());
				fileDownloadVo.setRequestUrl(domain);
				fileDownloadVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
				fileDownloadVo.setUserId(ShiroUtil.getCurrentUserId());
				fileDownloadVoList.add(fileDownloadVo);
			}
		}
		ctrContractClient.downloadContractFileMergePdf(fileDownloadVoList);
	}

	@ResponseBody
	@RequestMapping(value = "exportDcsxContractPdf")
	public void exportDcsxContractPdf(RptInvoiceBillSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
			searchVo.setFunderFlg(true);
			searchVo.setUserId(ShiroUtil.getCurrentUserId());
		}
		PageDown<RptInvoiceBill> page = rptInvoiceBillClient.findRptInvoiceBillPage(searchVo);
		List<RptInvoiceBill> content = page.getContent();

		List<CtrContractFileDownloadVo> fileDownloadVoList = new ArrayList<>();
		StringBuffer url = request.getRequestURL();
		String uri = request.getRequestURI();
		String domain = url.substring(0, url.indexOf(uri));
		logger.info("url:{}", url);
		logger.info("uri:{}", uri);
		logger.info("domain:{}", domain);

		if (CollectionUtils.isNotEmpty(content)) {
			CtrContractFileDownloadVo fileDownloadVo;
			for (RptInvoiceBill invoiceBill : content) {
				fileDownloadVo = new CtrContractFileDownloadVo();
				fileDownloadVo.setContractNo(invoiceBill.getContractNo());
				fileDownloadVo.setContractType(invoiceBill.getContractType());
				fileDownloadVo.setCompanyName(invoiceBill.getCompanyName());
				fileDownloadVo.setOurCompanyName(invoiceBill.getOurCompanyName());
				fileDownloadVo.setBuyContentFileId(invoiceBill.getBuyContentFileId());
				fileDownloadVo.setSellContentFileId(invoiceBill.getSellContentFileId());
				fileDownloadVo.setBusinessType(invoiceBill.getBusinessType());
				fileDownloadVo.setFileId(invoiceBill.getFileId());
				fileDownloadVo.setRequestUrl(domain);
				fileDownloadVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
				fileDownloadVo.setUserId(ShiroUtil.getCurrentUserId());
				fileDownloadVo.setDcsxContractFileId(invoiceBill.getDcsxContractFileId());
				fileDownloadVoList.add(fileDownloadVo);
			}
		}
		ctrContractClient.downloadDcsxContractFileMergePdf(fileDownloadVoList);
	}

	/**
	 * 导出税务
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportTaxation")
	@ResponseBody
	public void exportTaxation(RptInvoiceBillSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException, IOException {
		int batchSize = 500;
		searchVo.setRows(batchSize);
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
			searchVo.setFunderFlg(true);
			searchVo.setUserId(ShiroUtil.getCurrentUserId());
		}
		PageDown<RptInvoiceBill> page = rptInvoiceBillClient.findRptInvoiceBillPage(searchVo);
		List<RptInvoiceBill> content = page.getContent();
		if(CollectionUtils.isNotEmpty(content)){
			// 流水号自增
			AtomicReference<Integer> serialNumberInfo = new AtomicReference<>(1);
			ArrayList<RptInvoiceInfoExcel> invoiceInfoData = new ArrayList<>();
			ArrayList<RptInvoiceDetailExcel> invoiceDetailData= new ArrayList<>();
			// 数据字典获取一些固定值
			List<BsDictData> goodsServicesTaxCodeList = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.GOODS_SERVICES_TAX_CODE);
			String goodsServicesTaxCode = goodsServicesTaxCodeList.get(0).getDictName();
			List<BsDictData> taxRateList = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.INVOICE_DETAIL_TAX_RATE);
			String taxRate = taxRateList.get(0).getDictName();
			// 填充数据
			content.forEach(it->{
				// 基本信息
				RptInvoiceInfoExcel invoiceInfoExcel1 = new RptInvoiceInfoExcel();
				invoiceInfoExcel1.setSerialNumber(String.valueOf(serialNumberInfo.get()));
				invoiceInfoExcel1.setInvoiceType("增值税开票专用");
				invoiceInfoExcel1.setTaxFlg("是");
				invoiceInfoExcel1.setCompanyName(it.getCompanyName());
				invoiceInfoExcel1.setPurchaserTaxNumber(it.getTaxNo());
				invoiceInfoExcel1.setPurchaserAddr(it.getAddress());
				invoiceInfoExcel1.setPurchaserTelephone(it.getCompanyPhone());
				invoiceInfoExcel1.setPurchaserBankName(it.getBankName());
				invoiceInfoExcel1.setPurchaserBankAccount(it.getBankAccount());
				invoiceInfoExcel1.setRemark(it.getContractNo());
				invoiceInfoExcel1.setPayee("黄晨芳");
				invoiceInfoExcel1.setReviewer("冀亚净");
				//详情信息
				RptInvoiceDetailExcel invoiceDetailExcel1 = new RptInvoiceDetailExcel();
				invoiceDetailExcel1.setSerialNumber(String.valueOf(serialNumberInfo.get()));
				invoiceDetailExcel1.setProjectName(it.getProductName());
				invoiceDetailExcel1.setSpecificationsModels(it.getBrandNumber());
				invoiceDetailExcel1.setUnit("吨");
				invoiceDetailExcel1.setNumber(it.getDealNumber()==null?"":it.getDealNumber().toString());
				invoiceDetailExcel1.setPrice(it.getDealPrice()==null?"":it.getDealPrice().toString());
				invoiceDetailExcel1.setAmount(it.getTotalAmount()==null?"":it.getTotalAmount().toString());
				invoiceDetailExcel1.setTaxRate(taxRate);
				invoiceDetailExcel1.setGoodsServicesTaxCode(goodsServicesTaxCode);
				// 税号自增
				serialNumberInfo.getAndSet(serialNumberInfo.get() + 1);
				invoiceInfoData.add(invoiceInfoExcel1);
				invoiceDetailData.add(invoiceDetailExcel1);
			});
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setCharacterEncoding("utf-8");
			// 这里URLEncoder.encode可以防止中文乱码
			String fileName = URLEncoder.encode("开票税务", "UTF-8").replaceAll("\\+", "%20");
			response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
			ExcelWriter writer = EasyExcel.write(response.getOutputStream()).build();
			// 写入发票基本信息,表格内的下拉框数据
			Map<Integer, String[]> invoiceInfoMap = initInvoiceInfoMap();
			WriteSheet writeSheet1 = EasyExcel.writerSheet("1-发票基本信息")
					.registerWriteHandler(new InvoiceWriteHandler(invoiceInfoMap))
					.registerWriteHandler(new InvoiceStyleStrategy(new WriteCellStyle()))
					.head(RptInvoiceInfoExcel.class).build();
			writer.write(invoiceInfoData, writeSheet1);
			// 写入发票明细信息
			Map<Integer, String[]> invoiceDetailMap = initInvoiceDetailMap();
			WriteSheet writeSheet2 = EasyExcel.writerSheet("2-发票明细信息")
					.registerWriteHandler(new InvoiceWriteHandler(invoiceDetailMap))
					.registerWriteHandler(new InvoiceStyleStrategy(new WriteCellStyle()))
					.head(RptInvoiceDetailExcel.class).build();
			writer.write(invoiceDetailData, writeSheet2);
			writer.finish();
		}
	}
	// 初始化发票基本信息表格下拉框
	public Map<Integer, String[]> initInvoiceInfoMap(){
		Map<Integer,String[]> map = new HashMap<>();
		String []invoiceType={"增值税专用发票","普通发票"};
		String []specificBusinessType={"成品油","稀土","建筑服务","货物运输服务","不动产销售","不动产经营租赁服务","代收车船税",
		"旅客运输服务","自产农产品销售","拖拉机和联合收割机","机动车","农产品收购","光伏收购","卷烟","专票农产品","电子烟","白酒","报废产品收购"};
		String []taxFlg = {"是","否"};
		String []naturalPersonFlg = {"是","否"};
		String []certificateType = {"税务登记证","居民身份证","外国护照","外国人居留证","港澳居民来往内地通行证","台湾居民来往大陆通行证",
		"香港永久性居民身份证","台湾身份证","澳门特别行政区永久性居民身份证","外国人永久居留身份证（外国人永久居留证）","其他个人证件"};
		String []scrapProductSalesType = {"销售自己使用过的报废产品","销售收购的报废产品"};
		String []displayPAddTelAcc = {"展示地址、电话","展示开户银行、银行账号","展示地址、电话、开户银行及银行账号"};
		String []displaySAddTelAcc = {"展示地址、电话","展示开户银行、银行账号","展示地址、电话、开户银行及银行账号"};
		String []forgoTheLevy = {"前期已开具3%征收率的发票，发生销售折让、中止或者退回等情形需要开具3%征收率的红字发票，或者开票有误需要重新开具3%征收率的发票","因为实际经营业务需要，放弃享受减按1%征收率征收增值税政策。"};

		map.put(1,invoiceType);
		map.put(2,specificBusinessType);
		map.put(3,taxFlg);
		map.put(4,naturalPersonFlg);
		map.put(6,certificateType);
		map.put(13,scrapProductSalesType);
		map.put(14,displayPAddTelAcc);
		map.put(17,displaySAddTelAcc);
		map.put(24,forgoTheLevy);
		return map;
	}
	// 初始化发票明细信息表格下拉框
	public Map<Integer, String[]> initInvoiceDetailMap(){
		Map<Integer,String[]> map = new HashMap<>();
		return map;
	}


}
