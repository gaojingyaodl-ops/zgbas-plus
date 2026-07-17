package com.spt.bas.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.PushContract;
import com.spt.bas.client.vo.ContractExcelVo;
import com.spt.bas.client.vo.ContractStatusResponseVo;
import com.spt.bas.client.vo.PushSettlementVo;
import com.spt.bas.client.vo.UcsContractStatusVo;
import com.spt.bas.report.client.entity.RptCtrContractAgencyReport;
import com.spt.bas.report.client.remote.IRptCtrContractAgencyClient;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.bas.report.client.vo.RptPushContractVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrContractSettlementDao;
import com.spt.bas.server.dao.PushContractDao;
import com.spt.bas.server.service.ICtrProductService;
import com.spt.bas.server.service.IPushContractService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.http.util.HTTPUtility;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Transactional(readOnly = true)
public class PushContractServiceImpl extends BaseService<PushContract> implements IPushContractService {
	private static final ScheduledExecutorService schedulPool = Executors.newScheduledThreadPool(10);
	@Autowired
	private PushContractDao pushContractDao;
//	@Autowired
//	private ICtrContractTextService ctrContractTextService;
//	@Autowired
//	private IBsCompanyService bsCompanyService;
	@Autowired
	private ICtrProductService ctrProductService;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private CtrContractSettlementDao ctrContractSettlementDao;
	@Autowired
	private IRptCtrContractAgencyClient ctrContractAgencyReportClient;

	@Value("${picc.contract.switch}")
	private Boolean piccSwitch;

	@Override
	public BaseDao<PushContract> getBaseDao() {
		return pushContractDao;
	}

	@Override
	public Class<PushContract> getEntityClazz() {
		return PushContract.class;
	}

	/**
	 * 添加合同推送任务并执行
	 */
	@Override
	@ServerTransactional
	public void addContractPushTasks(CtrContract contract, List<CtrProduct> ctrProductList) throws ApplicationException {

		if (contract == null) {
			logger.error("addContractPushTasks contract is null!!!");
			return;
		}
		if (ctrProductList.isEmpty()) {
			logger.error("addContractPushTasks ctrProductList is null!!!");
			return;
		}
		//保存UCS合同推送任务
		//addUCSContractPushTask(contract);

		//执行UCS合同推送任务
		//doContractTask(ucsPushContract);
		doUcsTask(contract);

		//saas条件:需货商为【工业客户】的【有效】销售合同
//		BsCompany bsCompany = bsCompanyService.getEntity(contract.getCompanyId());
//		String companyType = bsCompany.getCompanyType();
//		String status = contract.getStatus();
//		if (StringUtils.equals(BasConstants.DICT_TYPE_COMPANYTYPE_I, companyType)
//				&& !StringUtils.equals(BasConstants.CONTRACTSTATUS_C, status)) {
			//保存SAAS合同推送任务
			PushContract saasPushContract = addSaasContractPushTask(contract,ctrProductList);
			//执行SAAS合同推送任务
			doContractTask(saasPushContract);
//		}
	}

	/**
	 * 添加合同状态推送任务并执行
	 */
	@Override
	@ServerTransactional
	public void addContractStatusPushTasks(ContractStatusResponseVo respVo) throws ApplicationException {
		CtrContract ctrContract = ctrContractDao.findByContractNo(respVo.getContractNo());
		if (ctrContract == null) {
			logger.warn("未查询到该合同!");
			return;
		}
		String contractType = ctrContract.getContractType();
		if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType)) {
			return;
		}
		Boolean creditFlg = ctrContract.getCreditFlg();
		Boolean onLineFlg = ctrContract.getOnLineFlg();
		logger.info("===>合同状态推送任务,contractNo:{},creditFlg:{},onLindFlg:{},piccSwitch:{}",
		 	ctrContract.getContractNo(),creditFlg, onLineFlg, piccSwitch);
		respVo.setAppCode("bps");
		respVo.setCreditFlg(creditFlg);
		respVo.setOnLineFlg(onLineFlg);
		String type = respVo.getType();
		//保存SAAS合同状态推送任务
//		BsCompany bsCompany = bsCompanyService.getEntity(ctrContract.getCompanyId());
//		String companyType = bsCompany.getCompanyType();
//		if (StringUtils.equals(BasConstants.DICT_TYPE_COMPANYTYPE_I, companyType)) {
		//if (onLineFlg) {
			PushContract sassPushContract = addSAASContractStatusTask(respVo);
			//执行合同推送任务
			pushContractStatus(sassPushContract);
//		}

		if (StringUtils.equals(BasConstants.APPLY_TYPE_O, type) && creditFlg != null && creditFlg && piccSwitch) {
			//保存UCS合同出库状态推送任务
			respVo.setPiccHappenDate(new Date());
			PushContract ucsPushContract = addUCSContractStatusTask(respVo);
			//执行合同状态推送任务
			pushContractStatus(ucsPushContract);
		}

		if (StringUtils.equals(BasConstants.APPLY_TYPE_E, type) && piccSwitch) {
			//保存UCS合同出库状态推送任务
			PushContract ucsPushContract = addUCSContractStatusTask(respVo);
			//执行合同状态推送任务
			pushContractStatus(ucsPushContract);
		}
		PushContract pushContract = addUCSContractStatusUpdateTask(respVo);
		pushContractStatus(pushContract);
	}

	/**
	 * 推送任务批量处理
	 */
	@Override
	@ServerTransactional
	public void doContractTask(PushContract pushContract) {
		//1.获取推送任务
		String pushType = pushContract.getPushType();
		String targetCode = pushContract.getTargetCode();
		List<PushContract> pushList = pushContractDao.findByPushTypeAndTargetCode(pushType, targetCode);
		logger.info(">>>>>>>>定时推送合同任务开始"+targetCode+",本次推送任务数据量为:"+pushList.size()+"<<<<<<<<");
		if (StringUtils.equals("t_ctr_contract", pushType)) {
			for (PushContract push : pushList) {
				String pushContractNo = push.getPushContractNo();
				logger.info(">>push {} {},contractNo:{}",targetCode, push.getPushType(),pushContractNo);
				CtrContract contract = ctrContractDao.findByContractNo(pushContractNo);
				if (StringUtils.equals(BasConstants.SAAS_APP_CODE, targetCode)){
					//SAAS采购机器人推送任务
					dealWithSaasPushTask(push,contract);
				}else if (StringUtils.equals(BasConstants.UCS_APP_CODE, targetCode)){
					//UCS信用评价推送任务
					dealWithUcsPushTask(push,contract);
				}
			}
		}else if (StringUtils.equals("contractStatus", pushType)) {
			for (PushContract push : pushList) {
				if (StringUtils.equals(BasConstants.SAAS_APP_CODE, targetCode)) {
					//SAAS合同状态推送任务
					pushContractStatus(push);
				}
			}
		}else if (StringUtils.equals("settlement", pushType)) {
			for (PushContract push : pushList) {
				if (StringUtils.equals(BasConstants.UCS_APP_CODE, targetCode)){
					//UCS结算单推送任务
					dealWithSettlementPushTask(push);
				}else if (StringUtils.equals(BasConstants.SAAS_APP_CODE, targetCode)) {
					//SAAS结算单推送任务
					dealWithSettlementPushTask(push);
				}
			}
		}else if (StringUtils.equals("settlementStatus", pushType)) {
			for (PushContract push : pushList) {
				if (StringUtils.equals(BasConstants.UCS_APP_CODE, targetCode)){
					//UCS结算单推送任务
					dealWithSettlementPushTask(push);
				}
			}
		}
	}

	@Override
	@ServiceTransactional
	public void removeContractTasks(String contractNo) {
		try {
			List<PushContract> pushContractList = pushContractDao.findByPushContractNo(contractNo);
			for (PushContract pushContract : pushContractList) {
				Boolean pushFlg = pushContract.getPushFlg();
				if (!pushFlg) {
					pushContractDao.delete(pushContract.getId());
				} else {
					pushContract.setPushFlg(false);
					this.save(pushContract);
				}
			}
		} catch (Exception e) {
			logger.error("合同作废后去除合同推送任务异常！！！",e);
		}
	}

	/**
	 * ucs-赊销合同推送至企业信用评价系统
	 * @param push
	 */
	private void dealWithUcsPushTask(PushContract push,CtrContract contract) {
		try {
			String pushData = push.getPushData();
			if (StringUtils.isBlank(pushData)) {
				pushData = getUcsContractPushData(contract);
				push.setPushData(pushData);
			}
			String pushUrl = push.getPushUrl();
			String resultJson = HTTPUtility.doPostJson(pushUrl, pushData, null);
			//2.推送成功后修改推送状态值
			JSONObject jsonObj = JSONObject.parseObject(resultJson);
			String code = jsonObj.getString("code");
			if("200".equals(code)){
				push.setPushFlg(true);
				this.save(push);
			}
		} catch (Exception e) {
			logger.error("定时推送赊销合同至UCS异常！！！",e);
		}
	}

	/**
	 * saas-赊销合同推送至采购机器人
	 * @param push
	 */
	private void dealWithSaasPushTask(PushContract push,CtrContract contract) {
		try {
			String pushData = push.getPushData();
			if (StringUtils.isBlank(pushData)) {
				List<CtrProduct> ctrProductList = ctrProductService.findByContractId(contract.getId());
				pushData = getSaasContractPushData(contract, ctrProductList);
				push.setPushData(pushData);
			}
			String pushUrl = push.getPushUrl();
			String resultJson = HTTPUtility.doPostJson(pushUrl, pushData, null);
			if (StringUtils.isNotBlank(resultJson)) {
				//.推送成功后修改推送状态值
				JSONObject jsonObj = JSONObject.parseObject(resultJson);
				String code = jsonObj.getString("code");
				if("200".equals(code)){
					push.setPushFlg(true);
					this.save(push);
					logger.info(">>>dealWithSaasPushTask OK！");
				}
			}
		} catch (Exception e) {
			logger.error("定时推送赊销合同至SAAS异常！！！",e);
		}
	}

	/**
	 * 结算单推送
	 * @param push
	 */
	private void dealWithSettlementPushTask(PushContract push) {
		try {
			String pushData = push.getPushData();
			String pushUrl = push.getPushUrl();
			String resultJson = HTTPUtility.doPostJson(pushUrl, pushData, null);
			if (StringUtils.isNotBlank(resultJson)) {
				//2.推送成功后修改推送状态值
				JSONObject jsonObj = JSONObject.parseObject(resultJson);
				String code = jsonObj.getString("code");
				if("200".equals(code)){
					push.setPushFlg(true);
					this.save(push);
				}
			}
		} catch (Exception e) {
			logger.error("定时推送结算单异常！！！",e);
		}
	}

	public void pushContractStatus(PushContract push) {
		try {
			String pushData = push.getPushData();
			String pushUrl = push.getPushUrl();
			String resultJson = HTTPUtility.doPostJson(pushUrl, pushData, null);
			if (StringUtils.isNotBlank(resultJson)) {
				JSONObject jsonObj = JSONObject.parseObject(resultJson);
				String code = jsonObj.getString("code");
				//2.推送失败就添加至推送任务列表
				if("200".equals(code)){
					push.setPushFlg(true);
					this.save(push);
					logger.info(">>>pushContractStatus OK！");
				}
			}
		} catch (Exception e) {
			logger.error("推送合同状态异常",e);
		}
	}

	/**
	 * 是否可以作废合同
	 */
	@Override
	public Boolean canInvalidContract(String  contractNo) {
		Boolean canInvalid = false;
		try {
			ContractStatusResponseVo respVo = new ContractStatusResponseVo();
			respVo.setContractNo(contractNo);
			String saasUrl = PropertiesUtil.getProperty(BasConstants.SAAS_GATEWAY_URL);
			String pushUrl = saasUrl + BasConstants.SAAS_CANINVALID_URL;
			String resultJson = HTTPUtility.doPostBody(pushUrl, respVo, null);
			if (StringUtils.isNotBlank(resultJson)) {
				JSONObject jsonObj = JSONObject.parseObject(resultJson);
				String code = jsonObj.getString("code");
				if("200".equals(code)){
					Boolean flg = jsonObj.getBoolean("data");
					if (flg != null) {
						canInvalid = flg;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Saas是否可以作废合同请求异常!!!",e);
		}
		return canInvalid;
	}

	//添加SAAS合同推送任务
	private PushContract addSaasContractPushTask(CtrContract contract, List<CtrProduct> ctrProductList) throws ApplicationException {
		PushContract saasPushContract = new PushContract();
		saasPushContract.setPushContractNo(contract.getContractNo());
		saasPushContract.setPushType("t_ctr_contract");
		String saasUrl = PropertiesUtil.getProperty(BasConstants.SAAS_GATEWAY_URL);
		String saasPushUrl = saasUrl + BasConstants.SAAS_CONTRACT_URL;
		saasPushContract.setPushUrl(saasPushUrl);
		saasPushContract.setTargetCode(BasConstants.SAAS_APP_CODE);
		saasPushContract.setEnterpriseId(contract.getEnterpriseId());
		//获取推送内容
		String pushData = getSaasContractPushData(contract, ctrProductList);
		saasPushContract.setPushData(pushData);
		saasPushContract = this.save(saasPushContract);
		logger.info(">>>insert saasPushContract <<< ");
		return saasPushContract;
	}
	//处理SAAS合同推送内容JSON
	private String getSaasContractPushData(CtrContract contract, List<CtrProduct> ctrProductList) {
		String pushData = "";
		ContractExcelVo vo = new ContractExcelVo();
		SysUserSdk sysUser = authOpenFacade.findUserById(contract.getMatchUserId());
		String mobile = sysUser.getPhonenumber();

		String payMode = contract.getPayMode();
		String qualityStandard = contract.getQualityStandard();
		vo.setSellCompanyName(contract.getOurCompanyName());
		vo.setBuyCompanyName(contract.getCompanyName());
		vo.setContractNo(contract.getContractNo());
		vo.setTradeDate(contract.getContractTime());
		vo.setPayDate(contract.getPayFullTime());
		vo.setDeliveryDate(contract.getDeliveryDateFrom());
		vo.setSellUserName(contract.getMatchUserName());
		vo.setStatus(contract.getStatus());
		vo.setDeliveryPlace(contract.getDeliveryAddr());
		vo.setCtrProductList(ctrProductList);
		vo.setTotalPrice(contract.getTotalAmount());
		vo.setDealNumber(contract.getTotalNumber());
		vo.setCreditFlg(contract.getCreditFlg());
		vo.setDeliveryType(contract.getDeliveryType());
		vo.setPayMode(payMode);
		vo.setSellMobile(mobile);
		vo.setBasFlg(contract.getOnLineFlg());
		vo.setBusinessType(contract.getBusinessType());
		if (StringUtils.isBlank(qualityStandard)) {
			qualityStandard = BasConstants.QUALITY_Y;
		}
		String packageSpec = DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD, qualityStandard);
		vo.setPackageSpec(packageSpec);
		pushData = JsonUtil.obj2Json(vo);
		return pushData;
	}
	//添加UCS合同推送任务
	private PushContract addUCSContractPushTask(CtrContract contract) throws ApplicationException {
		PushContract ucsPushContract = new PushContract();
		ucsPushContract.setPushContractNo(contract.getContractNo());
		ucsPushContract.setPushType("t_ctr_contract");
		String ucsUrl = PropertiesUtil.getProperty(BasConstants.UCS_PUSH_URL);
		String ucsPushUrl = ucsUrl + BasConstants.UCS_CONTRACT_URL;
		ucsPushContract.setPushUrl(ucsPushUrl);
		ucsPushContract.setTargetCode(BasConstants.UCS_APP_CODE);
		ucsPushContract.setEnterpriseId(contract.getEnterpriseId());
		//获取UCS合同推送内容
		String pushData = getUcsContractPushData(contract);
		ucsPushContract.setPushData(pushData);
		ucsPushContract = this.save(ucsPushContract);
		logger.info(">>>insert ucsPushContract <<< ");
		return ucsPushContract;
	}
	//处理UCS合同推送内容JSON
	private String getUcsContractPushData(CtrContract contract) {
		String pushData = "";
		List<RptCtrContractAgencyReport> list = new ArrayList<>();
		RptPushContractVo pushVo = new RptPushContractVo();
//		CtrContract ctrContract = ctrContractDao.findByContractNo(contract.getContractNo());
		RptCtrContractAgencyReport report = new RptCtrContractAgencyReport();
		if (StringUtils.equals(BasConstants.CONTRACTSTATUS_C, contract.getStatus())) {
			report.setSellContractNo(contract.getContractNo());
			report.setStatus(contract.getStatus());
			report.setAppCode("bps");
			list.add(report);
		}else {
			RptAssementSearchVo vo = new RptAssementSearchVo();
			vo.setSellContractNo(contract.getContractNo());
			list = ctrContractAgencyReportClient.findAgencyBySellId(vo);
//			report.setSellContractNo(contract.getContractNo());
//			report.setOurCompanyName(contract.getOurCompanyName());
//			report.setSellCompanyName(contract.getCompanyName());
//			report.setProductName(contract.getProductsName());
//			report.setSellTotalNumber(contract.getTotalNumber());
//			report.setSellTotalAmount(contract.getTotalAmount());
//			report.setSellContractDate(contract.getContractTime());
//			report.setCreditFlg(contract.getCreditFlg());
//			report.setPiccAccrualDate(contract.getPayFullTime());
//			report.setPiccHappenDate(contract.getDeliveryDateFrom());
//			report.setPiccTotalAmount(contract.getTotalAmount());
//			report.setPiccTotalNumber(contract.getTotalNumber());
//			report.setAppCode(BasConstants.APP_CODE);
//
//			CtrContractText ctrContractText = ctrContractTextService.findByContractId(ctrContract.getId());
//			if (ctrContractText != null) {
//				report.setSellContent(ctrContractText.getContent());
//			}
//			list.add(report);
		}
		if (!list.isEmpty()) {
			pushVo.setPushVo(list);
			pushData = JsonUtil.obj2Json(pushVo);
		}
		return pushData;
	}

	private PushContract addSAASContractStatusTask(ContractStatusResponseVo respVo) throws ApplicationException {
		PushContract saasPushContract = new PushContract();
		saasPushContract.setPushContractNo(respVo.getContractNo());
		saasPushContract.setPushType("contractStatus");
		String saasUrl = PropertiesUtil.getProperty(BasConstants.SAAS_GATEWAY_URL);
		String pushUrl = saasUrl + BasConstants.SAAS_CONTRACT_STATUS_URL;
		saasPushContract.setPushUrl(pushUrl);
		saasPushContract.setTargetCode(BasConstants.SAAS_APP_CODE);
		String pushData = JsonUtil.obj2Json(respVo);
		saasPushContract.setPushData(pushData);
		saasPushContract.setEnterpriseId(respVo.getEnterpriseId());
		saasPushContract = this.save(saasPushContract);
		logger.info(">>>insert contractStatusPush <<< ");
		return saasPushContract;
	}

	private void doUcsTask(CtrContract contract) {
		// 延时5秒执行
		schedulPool.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					PushContract ucsPushContract = addUCSContractPushTask(contract);
					if (StringUtils.isNotBlank(ucsPushContract.getPushData())) {
						//执行UCS合同推送任务
						doContractTask(ucsPushContract);
					}
				} catch (Exception e) {
					logger.info("推送合同详情至企业信用评价异常！",e.getMessage());
				}
			}
		}, 5, TimeUnit.SECONDS);
	}

	private PushContract addUCSContractStatusTask(ContractStatusResponseVo respVo) throws ApplicationException {
		PushContract ucsPushContract = new PushContract();
		ucsPushContract.setPushContractNo(respVo.getContractNo());
		ucsPushContract.setPushType("contractStatus");
		String ucsUrl = PropertiesUtil.getProperty(BasConstants.UCS_PUSH_URL);
		String pushUrl = ucsUrl + BasConstants.UCS_CONTRACT_STATUS_URL;
		ucsPushContract.setPushUrl(pushUrl);
		ucsPushContract.setTargetCode(BasConstants.UCS_APP_CODE);
		String pushData = JsonUtil.obj2Json(respVo);
		ucsPushContract.setPushData(pushData);
		ucsPushContract.setEnterpriseId(respVo.getEnterpriseId());
		ucsPushContract = this.save(ucsPushContract);
		logger.info(">>>insert contractStatusPush <<< ");
		return ucsPushContract;
	}

	private PushContract addUCSContractStatusUpdateTask(ContractStatusResponseVo respVo) throws ApplicationException {
		PushContract ucsPushContract = new PushContract();
		UcsContractStatusVo statusVo = new UcsContractStatusVo();
		statusVo.setSellContractNo(respVo.getContractNo());
		statusVo.setContractStatus(respVo.getContractStatus());
		statusVo.setAppCode("bps");
		ucsPushContract.setPushContractNo(respVo.getContractNo());
		ucsPushContract.setPushType("contractStatus");
		String ucsUrl = PropertiesUtil.getProperty(BasConstants.UCS_PUSH_URL);
		String pushUrl = ucsUrl + BasConstants.UCS_UPDATE_STATUS_URL;
		ucsPushContract.setPushUrl(pushUrl);
		ucsPushContract.setTargetCode(BasConstants.UCS_APP_CODE);
		String pushData = JsonUtil.obj2Json(statusVo);
		ucsPushContract.setPushData(pushData);
		ucsPushContract.setEnterpriseId(respVo.getEnterpriseId());
		ucsPushContract = this.save(ucsPushContract);
		logger.info(">>>insert contractStatusPush <<< ");
		return ucsPushContract;
	}

	@Override
	@ServerTransactional
	public void addSettlementTasks(String contractNo) throws Exception{
		PushContract ucsPushContract = new PushContract();
		List<CtrContractSettlement> settlementList = ctrContractSettlementDao.findBySellContractNo(contractNo);
		RptPushContractVo vo = new RptPushContractVo();
		List<RptCtrContractAgencyReport> reports = new ArrayList<RptCtrContractAgencyReport>();
		Long enterpriseId = 0L;
		for (CtrContractSettlement settlement : settlementList) {
			RptCtrContractAgencyReport report = new RptCtrContractAgencyReport();
			BeanUtils.copyProperties(settlement, report);
			report.setSettlementStatus(settlement.getStatus());
			report.setSettlementId(settlement.getId());
			report.setAppCode("bps");
			report.setSettlementNo(settlement.getSettlementCode());
			reports.add(report);
			enterpriseId = settlement.getEnterpriseId();
		}
		vo.setPushVo(reports);

		//UCS结算表推送
		ucsPushContract.setPushContractNo(contractNo);
		ucsPushContract.setPushType("settlement");
		String ucsUrl = PropertiesUtil.getProperty(BasConstants.UCS_PUSH_URL);
		String pushUrl = ucsUrl + BasConstants.UCS_SETTLEMENT_URL;
		ucsPushContract.setPushUrl(pushUrl);
		ucsPushContract.setTargetCode(BasConstants.UCS_APP_CODE);
		String pushData = JsonUtil.obj2Json(vo);
		ucsPushContract.setPushData(pushData);
		ucsPushContract.setEnterpriseId(enterpriseId);
		ucsPushContract = this.save(ucsPushContract);
		logger.info(">>>insert ucs settlementPush <<< ");
		dealWithSettlementPushTask(ucsPushContract);

		//SAAS结算表推送
		PushContract saasPushContract = new PushContract();
		PushSettlementVo settlementVo = new PushSettlementVo();
		settlementVo.setSettlementList(settlementList);
		String saasUrl = PropertiesUtil.getProperty(BasConstants.SAAS_GATEWAY_URL);
		pushUrl = saasUrl + BasConstants.SAAS_SETTLEMENT_URL;
		saasPushContract.setPushContractNo(contractNo);
		saasPushContract.setPushType("settlement");
		saasPushContract.setPushUrl(pushUrl);
		saasPushContract.setTargetCode(BasConstants.SAAS_APP_CODE);
		String saasPushData = JsonUtil.obj2Json(settlementVo);
		saasPushContract.setPushData(saasPushData);
		saasPushContract.setEnterpriseId(enterpriseId);
		saasPushContract = this.save(saasPushContract);
		logger.info(">>>insert sass settlementPush <<< ");
		dealWithSettlementPushTask(saasPushContract);
	}

	@Override
	@ServerTransactional
	public void addSettlementStatusTasks(String contractNo) throws Exception {
		PushContract pushContract = new PushContract();
		List<CtrContractSettlement> settlementList = ctrContractSettlementDao.findBySellContractNo(contractNo);
		RptPushContractVo vo = new RptPushContractVo();
		List<RptCtrContractAgencyReport> reports = new ArrayList<RptCtrContractAgencyReport>();
		Long enterpriseId = 0L;
		for (CtrContractSettlement settlement : settlementList) {
			RptCtrContractAgencyReport report = new RptCtrContractAgencyReport();
			BeanUtils.copyProperties(settlement, report);
			report.setSettlementStatus(settlement.getStatus());
			report.setSettlementId(settlement.getId());
			report.setAppCode("bps");
			report.setSettlementNo(settlement.getSettlementCode());
			report.setSettlementId(settlement.getId());
			reports.add(report);
			enterpriseId = settlement.getEnterpriseId();
		}
		vo.setPushVo(reports);

		pushContract.setPushContractNo(contractNo);
		pushContract.setPushType("settlementStatus");
		String ucsUrl = PropertiesUtil.getProperty(BasConstants.UCS_PUSH_URL);
		String pushUrl = ucsUrl + BasConstants.UCS_SETTLEMENT_URL;
		pushContract.setPushUrl(pushUrl);
		pushContract.setTargetCode(BasConstants.UCS_APP_CODE);
		String pushData = JsonUtil.obj2Json(vo);
		pushContract.setPushData(pushData);
		pushContract.setEnterpriseId(enterpriseId);
		pushContract = this.save(pushContract);
		logger.info(">>>insert settlementStatusPush <<< ");

		dealWithSettlementPushTask(pushContract);
	}
}

