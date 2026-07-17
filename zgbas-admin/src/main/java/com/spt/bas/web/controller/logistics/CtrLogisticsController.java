package com.spt.bas.web.controller.logistics;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LogisticsEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.util.RmbUtil;
import com.spt.bas.client.vo.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.StringUtils;
import com.spt.bas.web.util.XssExcelExp;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@Controller
@RequestMapping(value = "/ctr/logistics")
public class CtrLogisticsController extends PageController<CtrLogistics, BaseVo> {
	@Autowired
	private ICtrLogisticsClient ctrLogisticsClient;
	@Autowired
	private ICtrLogisticsDeliveryClient ctrLogisticsDeliveryClient;
	@Autowired
	protected ICtrLogisticsDriverClient ctrLogisticsDriverClient;
	@Autowired
	private IBsWarehouseClient bsWarehouseClient;
	@Autowired
	private IBsCompanyOurClient bsCompanyOurClient;
	@Autowired
	private LogisticsCompanyConfigClient logisticsCompanyConfigClient;
	@Autowired
	private IPmProcessClient pmProcessClient;
	@Autowired
	private ICtrLogisticsFileClient ctrLogisticsFileClient;
	@Resource
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IApplyCtrDcsxClinent ctrDcsxClinent;
	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@RequestMapping(value = "index")
	public String index(Model model) {
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		DeptSearchVo deptSearchVo = new DeptSearchVo();
		deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
		return "logistics/ctr-logistics";
	}

	/**
	 * 查询所有信息
	 *
	 * @param searchVo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "findAllLogisticsLoading")
	public void findAllLogisticsLoading(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Map<String, Object> searchParams = searchVo.getSearchParams();
		searchParams.put("EQB_enableFlg", true);
		PageDown<CtrLogistics> page = ctrLogisticsClient.findPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}

	/**
	 * 进入明细页面
	 *
	 * @return
	 */
	@RequestMapping(value = "getLogisticsLoading/{id}", method = RequestMethod.GET)
	public String getLogisticsLoading(@PathVariable("id") Long id, Model model) {
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		model.addAttribute("phoneProtectJson",
				JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_TYPE_PHONEPROTECT)));

		CtrLogistics entity = getEntity(id);
		model.addAttribute("entity", entity);
		BsWarehouseSearchVo bsWarehouseSearchVo = new BsWarehouseSearchVo();
		bsWarehouseSearchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		List<BsWarehouse> bsWarehouseList = bsWarehouseClient.findByBusiness(bsWarehouseSearchVo);
		model.addAttribute("bsWarehouseList",
				JsonUtil.obj2Json(bsWarehouseList));
		model.addAttribute("randomNumber1", 1);
		CtrLogisticsDeliveryVo searchVo = new CtrLogisticsDeliveryVo();
		searchVo.setLogisticsId(entity.getId());
		List<CtrLogisticsDelivery> deliveryList = ctrLogisticsDeliveryClient.findByLogisticsId(searchVo);
		if(CollectionUtils.isNotEmpty(deliveryList)) {
			model.addAttribute("deliveryListSize", deliveryList.size());
		} else {
			model.addAttribute("deliveryListSize", 0);
		}
		List<LogisticsCompanyConfig> carrierList = getCarrierInfo(entity.getSellOurCompanyName());
		model.addAttribute("carrierListJson",JsonUtil.obj2Json(carrierList));
		// 审批流程Json
		PmProcessSearchVo vo = new PmProcessSearchVo(ShiroUtil.getEnterpriseId());
		List<PmProcess> processList = pmProcessClient.findByEnterpriseId(vo);
		model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
		// 入库、出库、确认收货 按钮显现逻辑
		CtrContract buyContract = ctrContractClient.getEntity(entity.getBuyContractId());
		CtrContract sellContract = ctrContractClient.getEntity(entity.getSellContractId());
		if (Objects.nonNull(buyContract) && Objects.nonNull(sellContract)){
			ApplyCtrDCSX applyCtrDCSX = ctrDcsxClinent.findByDCSXApproveId(sellContract.getApproveId());
			Boolean buySealFlg = buyContract.getSealFlg();
			Boolean sellSealFlg = sellContract.getSealFlg();
			Boolean inFlg = buyContract.getTotalNumber().compareTo(buyContract.getWarehouseNumber()) > 0;
			boolean virtualFlg = Objects.isNull(buyContract.getVirtualContractId());
			model.addAttribute("deliveryInFlg", buySealFlg && sellSealFlg && inFlg && virtualFlg );
			model.addAttribute("deliveryOutFlg", buyContract.getWarehouseNumber().compareTo(sellContract.getWarehouseNumber()) > 0);
			model.addAttribute("confirmFlg", sellContract.getWarehouseNumber().compareTo(sellContract.getConfirmReceiveNumber()) > 0);
			model.addAttribute("fundFlg", StringUtils.isNotBlank(sellContract.getBusinessTypeDcsx())
					&& sellContract.getBusinessTypeDcsx().contains(BasConstants.BUSINESS_TYPE_DCSX)
					&& Objects.nonNull(applyCtrDCSX));
		}
		return "logistics/ctr-logistics-detail";
	}

	@RequestMapping(value = "exists/{contractNo}", method = RequestMethod.GET)
	@ResponseBody
	public Long exists(@PathVariable("contractNo") String contractNo, HttpServletRequest request, HttpServletResponse response) {
		List<CtrLogistics> byLogisticsNo = ctrLogisticsClient.findByLogisticsNo(contractNo);
		final int size = byLogisticsNo.size();
		if(size>0){
			return byLogisticsNo.get(0).getId();
		}else{
			return null;
		}
	}

	/**
	 * 获取承运商信息
	 */
	public List<LogisticsCompanyConfig> getCarrierInfo(String ourCompanyName){
		List<LogisticsCompanyConfig> logisticsCompanyConfigList = new ArrayList<>();
		int flag=1;
		if(org.apache.commons.lang3.StringUtils.isNotBlank(ourCompanyName)){
			List<BsDictData> listByCategory = BsCompanyOurUtil.getCompanyOurToBsDictDataList();
			for (BsDictData bsDictData : listByCategory) {
				if(org.apache.commons.lang3.StringUtils.equals(ourCompanyName,bsDictData.getDictName())){
					ourCompanyName=bsDictData.getDictCd();
				}
			}
			logisticsCompanyConfigList = logisticsCompanyConfigClient.findByOurCompanyNames(ourCompanyName);
			Iterator<LogisticsCompanyConfig> iterator = logisticsCompanyConfigList.iterator();
			while (iterator .hasNext()) {
				LogisticsCompanyConfig next = iterator.next();
				String ourCompanyNames = next.getOurCompanyNames();
				String[] split = ourCompanyNames.split(",");
				for (String s : split) {
					if(org.apache.commons.lang3.StringUtils.equals(s,ourCompanyName)){
						flag=2;
					}
				}
				if(flag!=2){
					iterator.remove();
				}
				flag=1;
			}
		}
		return logisticsCompanyConfigList;
	}

	/***
	 * 增加 提货 输入域
	 * @return
	 */
	@RequestMapping(value="addLogisticsDeliveryDetail")
	public String addLogisticsDeliveryDetail(Model model,Integer curNumber,Long logisticsId,String logisticsCount){
		model.addAttribute("randomNumber", curNumber);
		model.addAttribute("logisticsId", logisticsId);
		CtrLogisticsDeliveryVo searchVo = new CtrLogisticsDeliveryVo();
		searchVo.setLogisticsId(logisticsId);
		searchVo.setLogisticsCount(logisticsCount);
		CtrLogisticsDelivery delivery = new CtrLogisticsDelivery();
		if(StringUtils.isNotEmpty(logisticsCount)) {
			delivery = ctrLogisticsDeliveryClient.findByLogisticsIdAndLogisticsCount(searchVo);
		} else {
			delivery.setLogisticsNumber(null);
		}

		model.addAttribute("deliveryEntity", delivery);
		return "logistics/logistics-delivery";
	}

	@RequestMapping(value = "findAllLogisticsDriverLoading")
	public void findAllLogisticsDriverLoading(CtrLogisticsDriver searchVo, HttpServletRequest request, HttpServletResponse response) {
		Long logisticsId = searchVo.getLogisticsId();
		Long logisticsDeliveryId = searchVo.getLogisticsDeliveryId();
		if(!Objects.equals(0L,logisticsId) && !Objects.equals(0L,logisticsDeliveryId)) {
			List<CtrLogisticsDriver> driverList = ctrLogisticsDriverClient.findByLogisticsIdAndLogisticsDeliveryId(searchVo);
			JsonEasyUI.renderListJson(response, driverList);
		} else {
			JsonEasyUI.renderListJson(response, new ArrayList<>(0));
		}

	}

	/**
	 * 附件回显查询
	 * @param logisticsFile
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "findByLogisticsIdAndLogisticsDeliveryId", method = RequestMethod.POST)
	public CtrLogisticsFileRespVo findByLogisticsIdAndLogisticsDeliveryId(@RequestBody CtrLogisticsFile logisticsFile){
		return ctrLogisticsFileClient.findByLogisticsIdAndLogisticsDeliveryId(logisticsFile);
	}

	/***
	 * 增加 提货 输入域
	 * @return
	 */
	@RequestMapping(value="addLogisticsDeliveryTap")
	public String addLogisticsDeliveryTap(Model model,Integer curNumber){
		model.addAttribute("randomNumber", curNumber);
		return "logistics/logistics-tap";
	}

	/**
	 * 保存物流单据明细
	 * @return
	 */
	@RequestMapping(value = "saveLogistics", method = RequestMethod.POST)
	public void saveLogistics(@RequestBody CtrLogisticsVo ctrLogisticsVo, HttpServletResponse response){
		try {
			ctrLogisticsClient.saveLogistics(ctrLogisticsVo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("error:" + e.getMessage(), response);
		}
	}

	@Override
	public BaseClient<CtrLogistics> getService() {
		return ctrLogisticsClient;
	}


	@ModelAttribute("preload")
	public CtrLogistics getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return ctrLogisticsClient.getEntity(id);
			else {
				CtrLogistics ctrLogistics = new CtrLogistics();
				ctrLogistics.setId(0l);
				return ctrLogistics;
			}
		}
		return null;
	}

	/**
	 * 导出送货通知单
	 *
	 * @param response 响应
	 * @param request  请求
	 * @throws IOException 异常
	 */
	@RequestMapping(value = "exportDeliveryNoticeExcel", method = RequestMethod.GET)
	public void exportDeliveryNoticeExcel(Long id, String logisticsCount, HttpServletResponse response, HttpServletRequest request) throws IOException {

		Map<String, Object> paramMap = new HashMap<>();
		CtrLogistics entity = getEntity(id);
		CtrLogisticsDeliveryVo searchVo = new CtrLogisticsDeliveryVo();
		searchVo.setLogisticsId(id);
		searchVo.setLogisticsCount(logisticsCount);
		CtrLogisticsDelivery delivery = ctrLogisticsDeliveryClient.findByLogisticsIdAndLogisticsCount(searchVo);
		CtrLogisticsDriver driverSearchVo = new CtrLogisticsDriver();
		driverSearchVo.setLogisticsId(id);
		driverSearchVo.setLogisticsDeliveryId(delivery.getId());
		List<CtrLogisticsDriver> driverList = ctrLogisticsDriverClient.findByLogisticsIdAndLogisticsDeliveryId(driverSearchVo);
		BsWarehouse warehouse = bsWarehouseClient.findid(entity.getWarehouseId());
		LogisticsCompanyConfig carrierCompany = null;
		if(Objects.nonNull(delivery.getCarrierId())){
			carrierCompany = logisticsCompanyConfigClient.getEntity(delivery.getCarrierId());
		}


		paramMap.put("commissionDate", parseDefaultParam(DateUtil.format(delivery.getLogisticsDate(), DatePattern.CHINESE_DATE_PATTERN))); // 委托日期
		String orderNumber = entity.getLogisticsNo() + "-" + logisticsCount;
		paramMap.put("orderNumber", parseDefaultParam(orderNumber));// WL + 单号 + 第几次提货
		BigDecimal dealNumber = delivery.getLogisticsNumber();
		paramMap.put("dealNumber", parseDefaultParam(NumberUtil.formatNumber(dealNumber, "#.###"))); // 数量
		paramMap.put("productNames", parseDefaultParam(entity.getProductNames()));// 品名/牌号/产地
		BigDecimal sellDealPrice = entity.getSellDealPrice();
		paramMap.put("dealPrice", parseDefaultParam(NumberUtil.formatNumber(sellDealPrice, "#.###")));// 单价
		BigDecimal logisticsNumber = delivery.getLogisticsNumber();
		BigDecimal totalMoney = sellDealPrice.multiply(logisticsNumber);
		paramMap.put("totalAmount", parseDefaultParam(NumberUtil.formatNumber(totalMoney, "#.###")));// 金额
		paramMap.put("bigTotalAmount", parseDefaultParam(RmbUtil.number2Chinese(totalMoney)));// 大写金额
		paramMap.put("piece", parseDefaultParam(NumberUtil.formatNumber(logisticsNumber.multiply(BigDecimal.valueOf(40L)), "#.###")));// 件数
		paramMap.put("outCompany", parseDefaultParam(getOutCompanyName(entity)));// 发货单位
		Date realDeliveryDate = delivery.getRealDeliveryDate();
		if(Objects.isNull(realDeliveryDate)) {
			realDeliveryDate = entity.getBuyDeliveryDate();
		}
		paramMap.put("outDate", parseDefaultParam(DateUtil.format(realDeliveryDate, DatePattern.CHINESE_DATE_PATTERN)));// 提货日期
		paramMap.put("outContactPerson", parseMatchUserName(entity.getMatchUserName()));// 发货联系人（业务员姓氏 + "经理")
		Date realArrivalDate = delivery.getRealArrivalDate();
		if(Objects.isNull(realArrivalDate)) {
			realArrivalDate = entity.getSellDeliveryDate();
		}
		paramMap.put("arrivalDate", parseDefaultParam(DateUtil.format(realArrivalDate, DatePattern.CHINESE_DATE_PATTERN)));// 要求到货日期
		paramMap.put("contactPhone", parseDefaultParam(entity.getMatchUserPhone()));// 联系电话
		paramMap.put("outAddress", parseDefaultParam(entity.getTakeDelieveryAddr()));// 提货地址
		paramMap.put("warehousePerson", parseDefaultParam(warehouse.getContactPhone()));// 仓库联系人
		paramMap.put("deliveryCompany", parseDefaultParam(delivery.getCarrier()));// 送货单位
		paramMap.put("receiveCompany", parseDefaultParam(null));// 收货单位（业务保密要求)
		paramMap.put("deliveryContactPerson", parseDefaultParam(delivery.getMasterPorter()));// 送货单位联系人
		paramMap.put("receiveContactPerson", parseMatchUserName(entity.getMatchUserName()));// 收货单位联系人
		paramMap.put("deliveryContactPhone", parseDefaultParam(delivery.getMasterPhone()));// 送货单位联系电话
		paramMap.put("receiveContactPhone", parseDefaultParam(entity.getMatchUserPhone()));// 收货单位联系电话
		if (Objects.nonNull(carrierCompany)) {
			paramMap.put("deliveryAddress", parseDefaultParam(carrierCompany.getAddress()));// 送货地址
		} else {
			paramMap.put("deliveryAddress", parseDefaultParam(null));// 送货地址
		}
		paramMap.put("receiveAddress", parseDefaultParam(entity.getReceiveDeliveryAddr()));// 收货地址
		paramMap.put("remark", parseDefaultParam(delivery.getDeliveryRemark()));// 备注
		paramMap.put("carNumber", parseDefaultParam(parseDriver(driverList, CtrLogisticsDriver::getPlateNumber)));// 车号
		paramMap.put("carUserName", parseDefaultParam(parseDriver(driverList, CtrLogisticsDriver::getDriverName)));// 司机姓名
		paramMap.put("carUserPhone", parseDefaultParam(parseDriver(driverList, CtrLogisticsDriver::getContactPhone)));// 司机联系电话
		paramMap.put("carUserIdCard", parseDefaultParam(parseDriver(driverList, CtrLogisticsDriver::getDriverCardNo)));// 司机身份证号码
		XssExcelExp.MyCellType transportAmountMoney = new XssExcelExp.MyCellType(CellType.NUMERIC,parseDefaultParam(NumberUtil.formatNumber(delivery.getTransportAmount(), "#.###")));
		paramMap.put("transportAmount", transportAmountMoney);// 运费费总额
		paramMap.put("transportAmountMoney", parseDefaultParam(NumberUtil.formatNumber(calDivide(delivery.getTransportAmount(), dealNumber), "#.###")));// 运费费单价
		XssExcelExp.MyCellType deliveryOutFee = new XssExcelExp.MyCellType(CellType.NUMERIC, parseDefaultParam(NumberUtil.formatNumber(delivery.getDeliveryOutFee(), "#.###")));
		paramMap.put("deliveryOutFee", deliveryOutFee);// 出库费总额
		paramMap.put("deliveryOutFeeMoney", parseDefaultParam(NumberUtil.formatNumber(calDivide(delivery.getDeliveryOutFee(), dealNumber), "#.###")));// 出库费单价
		XssExcelExp.MyCellType otherFee = new XssExcelExp.MyCellType(CellType.NUMERIC, parseDefaultParam(NumberUtil.formatNumber(delivery.getOtherFee(), "#.###")));
		paramMap.put("otherFee", otherFee);// 拆托费总额
		paramMap.put("otherFeeMoney", parseDefaultParam(NumberUtil.formatNumber(calDivide(delivery.getOtherFee(), dealNumber), "#.###")));// 拆托费单价
		BigDecimal sum = calAdd(delivery.getTransportAmount(), delivery.getDeliveryOutFee(), delivery.getOtherFee());
		//paramMap.put("allMoney", parseDefaultParam(NumberUtil.formatNumber(sum, "#.###")));// 合计金额
		//paramMap.put("allMoneyChinese", parseDefaultParam(RmbUtil.number2Chinese(sum)));// 合计金额大写
		XssExcelExp.deliveryNoticeExcelByType("/excel/deliveryTemplate.xlsx", paramMap, response, orderNumber + "送货通知单.xlsx");
	}

	private String getOutCompanyName(CtrLogistics entity) {
		ApplyCtrDCSX ctrDCSX = ctrDcsxClinent.findByDCSXApproveId(entity.getApproveId());
		String outCompanyName;
		if (Objects.nonNull(ctrDCSX)) {
			String companyName = ctrDCSX.getOurCompanyName();
			BsCompanyOurSearchVo bsSearchVo = new BsCompanyOurSearchVo();
			bsSearchVo.setCompanyName(companyName);
			BsCompanyOur companyOurDetail = bsCompanyOurClient.getCompanyOurDetail(bsSearchVo);
			if (companyOurDetail.getOurCompanyFlag()) {
				outCompanyName = companyName;
			} else {
				outCompanyName = ctrDCSX.getCompanyName();
			}
		} else {
			outCompanyName = entity.getSellOurCompanyName();
		}
		return outCompanyName;
	}

	private BigDecimal calAdd(BigDecimal... args) {
		if (Objects.nonNull(args)) {
			return Arrays.stream(args).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
		}
		return null;
	}

	private BigDecimal calDivide(BigDecimal a, BigDecimal b) {
		if (Objects.isNull(a) || Objects.isNull(b) || b.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}
		return a.divide(b, 2, RoundingMode.UP);
	}


	private  String parseDriver(List<CtrLogisticsDriver> driverList,Function<CtrLogisticsDriver, String> function) {
		return driverList.stream().map(function).collect(Collectors.joining("\\"));
	}

	/**
	 * 处理业务员名称
	 *
	 * @param matchUserName 业务员名称
	 * @return 业务员姓氏 + "经理"
	 */
	private String parseMatchUserName(String matchUserName) {
		if (StringUtils.isBlank(matchUserName)) {
			return "-";
		}
		String firstName = matchUserName.substring(0, 1);
		return firstName + "经理";
	}

	private String parseDefaultParam(String param) {
		return StringUtils.isNotBlank(param) ? param : "-";
	}

	@RequestMapping(value = "exportReceiptExcel",method = RequestMethod.GET)
	public void exportReceiptExcel(Long id, String logisticsCount, HttpServletResponse response, HttpServletRequest request) throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		CtrLogistics entity = getEntity(id);
		CtrLogisticsDeliveryVo searchVo = new CtrLogisticsDeliveryVo();
		searchVo.setLogisticsId(id);
		searchVo.setLogisticsCount(logisticsCount);
		CtrLogisticsDelivery delivery = ctrLogisticsDeliveryClient.findByLogisticsIdAndLogisticsCount(searchVo);
		CtrLogisticsDriver driverSearchVo = new CtrLogisticsDriver();
		driverSearchVo.setLogisticsId(id);
		driverSearchVo.setLogisticsDeliveryId(delivery.getId());
		List<CtrLogisticsDriver> driverList = ctrLogisticsDriverClient.findByLogisticsIdAndLogisticsDeliveryId(driverSearchVo);

		String supplierName = entity.getSellOurCompanyName();
		paramMap.put("outCompany", parseDefaultParam(supplierName));// 采购合同的合同号
		paramMap.put("contractNo", parseDefaultParam(entity.getSellContractNo()));// 合同号
		paramMap.put("dealNumber", parseDefaultParam(NumberUtil.formatNumber(delivery.getLogisticsNumber(), "#.###")) + "吨"); // 数量
		paramMap.put("productNames", parseDefaultParam(entity.getProductNames()));// 品名/牌号/产地
		paramMap.put("outNo", parseOutNo(supplierName, entity.getBuyDeliveryType(), entity.getLogisticsNo(), logisticsCount));// 出库单号
		paramMap.put("receiveAddress", parseDefaultParam(entity.getReceiveDeliveryAddr()));// 收货地址
		paramMap.put("carNumber", parseDefaultParam(parseDriver(driverList, CtrLogisticsDriver::getPlateNumber)));// 车号
		paramMap.put("contactPerson", parseDefaultParam(entity.getMatchUserName()));// 联系人
		paramMap.put("carUserName", parseDefaultParam(parseDriver(driverList, CtrLogisticsDriver::getDriverName)));// 司机名称
		paramMap.put("contactPhone", parseDefaultParam(entity.getMatchUserPhone()));// 联系人电话
		paramMap.put("userIdCard", parseDefaultParam(parseDriver(driverList, CtrLogisticsDriver::getDriverCardNo)));// 司机身份证号
		paramMap.put("remark", parseRemark(entity.getSellDeliveryType()));// 备注
		Date realArrivalDate = delivery.getRealArrivalDate();
		if(Objects.isNull(realArrivalDate)) {
			realArrivalDate = entity.getSellDeliveryDate();
		}
		paramMap.put("commissionDate", parseDefaultParam(DateUtil.format(realArrivalDate, DatePattern.CHINESE_DATE_PATTERN)));// 收货日期
		paramMap.put("receiveCompany", parseDefaultParam(entity.getCompanyName()));// 收货单位
		String orderNumber = entity.getLogisticsNo() + "-" + logisticsCount;
		XssExcelExp.deliveryNoticeExcel("/excel/receiptTemplate.xlsx", paramMap, response, orderNumber + "货物签收单.xlsx");
	}

	/**
	 * 资方 ZZ+contractNo+count
	 * 我方
	 * 自提/配送：TH+contractNo+count
	 * 上家配送：PS+contractNo+count
	 *
	 * @param companyName  采购合同上家
	 * @param deliveryType 提货方式
	 * @param contractNo   合同号
	 * @param count        次数
	 * @return 结果
	 */
	private String parseOutNo(String companyName, String deliveryType, String contractNo, String count) {
		BsCompanyOurSearchVo searchVo = new BsCompanyOurSearchVo();
		searchVo.setCompanyName(companyName);
		BsCompanyOur companyOurDetail = bsCompanyOurClient.getCompanyOurDetail(searchVo);
		if (Objects.nonNull(companyOurDetail)) {
			if (companyOurDetail.getOurCompanyFlag()) {
				if (BasConstants.DICT_TYPE_DELIVERY_P1.equals(deliveryType)) {
					return "PS" + contractNo + "-" + count;
				} else {
					return "TH" + contractNo + "-" + count;
				}

			} else {
				return "ZZ" + contractNo + "-" + count;
			}
		}
		return "-";
	}

	private String parseRemark(String buyDeliveryType) {
		if (org.apache.commons.lang3.StringUtils.isNotBlank(buyDeliveryType)) {
			if ("Z".equals(buyDeliveryType)) {
				return "自提";
			} else {
				return "配送";
			}
		}
		return "-";
	}

	/**
	 * 发起物流单据盖章申请
	 *
	 * @return
	 */
	@RequestMapping(value = "generateLogisticsSealUsage", method = RequestMethod.POST)
	public void generateLogisticsSealUsage(HttpServletRequest request, HttpServletResponse response) {
		CtrLogisticsReqVo reqVo = new CtrLogisticsReqVo();
		try {
			reqVo.setBizUserId(ShiroUtil.getCurrentUserId());
			reqVo.setBizUserName(ShiroUtil.getCurrentUserName());
			reqVo.setLogisticsDeliveryId(Long.parseLong(request.getParameter("logisticsDeliveryId")));
			reqVo.setContractId(Long.parseLong(request.getParameter("contractId")));
			String typeCode = request.getParameter("bizTypeCode");
			if (StringUtils.equalsIgnoreCase(LogisticsEnum.DISTRIBUTION.getLogisticsCode(), typeCode)) {
				reqVo.setLogisticsEnum(LogisticsEnum.DISTRIBUTION);
			} else if (StringUtils.equalsIgnoreCase(LogisticsEnum.FUND_DISTRIBUTION.getLogisticsCode(), typeCode)) {
				reqVo.setLogisticsEnum(LogisticsEnum.FUND_DISTRIBUTION);
				reqVo.setFundFlg(true);
			} else if (StringUtils.equalsIgnoreCase(LogisticsEnum.LADING.getLogisticsCode(), typeCode)) {
				reqVo.setLogisticsEnum(LogisticsEnum.LADING);
			} else if (StringUtils.equalsIgnoreCase(LogisticsEnum.FUND_LADING.getLogisticsCode(), typeCode)) {
				reqVo.setLogisticsEnum(LogisticsEnum.FUND_LADING);
				reqVo.setFundFlg(true);
			}
			CtrLogisticsFile logisticsFile = ctrLogisticsDeliveryClient.generateLogisticsSealUsage(reqVo);
			RenderUtil.renderJson(JsonUtil.obj2Json(logisticsFile), response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			String msg = e.getMessage();
			if (e.getCause() != null) {
				msg = e.getCause().getMessage();
			}
			RenderUtil.renderFailure(msg, response);
		}
	}

	@RequestMapping(value = "exportExcelTemplate", method = RequestMethod.GET)
	public void exportExcelTemplate(@RequestParam(value = "contractId", required = false) Long contractId,
									@RequestParam(value = "id", required = false) Long id,
									@RequestParam(value = "logisticsCount", required = false) String logisticsCount,
									@RequestParam(value = "logisticsDeliveryId", required = false) Long logisticsDeliveryId,
									@RequestParam(value = "bizTypeCode", required = false) String bizTypeCode,
									HttpServletRequest request, HttpServletResponse response) {
		if (Objects.isNull(contractId) || Objects.isNull(logisticsDeliveryId)) {
			logger.error("生成错误，参数缺失!");
			return;
		}
		CtrLogisticsReqVo reqVo = new CtrLogisticsReqVo();
		try {
			CtrLogistics entity = getEntity(id);
			reqVo.setBizUserId(ShiroUtil.getCurrentUserId());
			reqVo.setBizUserName(ShiroUtil.getCurrentUserName());
			reqVo.setLogisticsDeliveryId(logisticsDeliveryId);
			reqVo.setContractId(contractId);
			if (StringUtils.equalsIgnoreCase(LogisticsEnum.DISTRIBUTION.getLogisticsCode(), bizTypeCode)) {
				reqVo.setLogisticsEnum(LogisticsEnum.DISTRIBUTION);
			} else if (StringUtils.equalsIgnoreCase(LogisticsEnum.FUND_DISTRIBUTION.getLogisticsCode(), bizTypeCode)) {
				reqVo.setLogisticsEnum(LogisticsEnum.FUND_DISTRIBUTION);
				reqVo.setFundFlg(true);
			} else if (StringUtils.equalsIgnoreCase(LogisticsEnum.LADING.getLogisticsCode(), bizTypeCode)) {
				reqVo.setLogisticsEnum(LogisticsEnum.LADING);
			} else if (StringUtils.equalsIgnoreCase(LogisticsEnum.FUND_LADING.getLogisticsCode(), bizTypeCode)) {
				reqVo.setLogisticsEnum(LogisticsEnum.FUND_LADING);
				reqVo.setFundFlg(true);
			}
			Map<String, String> paramMap = ctrLogisticsDeliveryClient.exportExcelTemplate(reqVo);
			String orderNumber = entity.getLogisticsNo() + "-" + logisticsCount;
			if (org.apache.commons.lang3.StringUtils.equals("P", bizTypeCode)) {
				XssExcelExp.deliveryNoticeExcel("/excel/deliveryExportTemplate.xlsx", paramMap, response, orderNumber + "配送单.xlsx");
			} else if (org.apache.commons.lang3.StringUtils.equals("T", bizTypeCode)) {
				XssExcelExp.deliveryNoticeExcel("/excel/takeExportTemplate.xlsx", paramMap, response, orderNumber + "提货单.xlsx");
			} else if (org.apache.commons.lang3.StringUtils.equals("FP", bizTypeCode)) {
				XssExcelExp.deliveryNoticeExcel("/excel/deliveryExportTemplate.xlsx", paramMap, response, orderNumber + "资方配送单.xlsx");
			} else if (org.apache.commons.lang3.StringUtils.equals("FT", bizTypeCode)) {
				XssExcelExp.deliveryNoticeExcel("/excel/takeExportTemplate.xlsx", paramMap, response, orderNumber + "资方提货单.xlsx");
			} else {
				logger.error("导出文件失败！");
			}
		} catch (Exception e) {
			logger.error("导出文件失败:", e);
			String msg = e.getMessage();
			if (e.getCause() != null) {
				msg = e.getCause().getMessage();
			}
			RenderUtil.renderFailure(msg, response);
		}
	}
}
