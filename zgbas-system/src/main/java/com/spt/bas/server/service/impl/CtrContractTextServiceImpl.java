package com.spt.bas.server.service.impl;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IApplyChargeSalesClient;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.util.RmbUtil;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.MidstreamUtil;
import com.spt.bas.server.util.TemplateContentUtility;
import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Transactional(readOnly = true)
public class CtrContractTextServiceImpl extends BaseService<CtrContractText> implements ICtrContractTextService {
	@Autowired
	private CtrContractTextDao ctrContractTextDao;
	@Autowired
	private CtrProductDao productDao;
	@Autowired
	private IBsCompanyService bsCompanyService;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private BsWarehouseDao bsWarehouseDao;
	@Autowired
	private BsWarehouseAddrDao bsWarehouseAddrDao;
	@Autowired
	private BsContractTemplateDao contractTemplateDao;
	@Autowired
	private ApplyMatchDetailDao applyMatchDetailDao;
	@Autowired
	private ApplyMatchDao applyMatchDao;
	@Autowired
	private ApplyProductDetailDao applyProductDetailDao;
	@Autowired
	private IBsCompanyDcsxClient bsCompanyDcsxClient;
	@Autowired
	private IBsCompanyAccountService bsCompanyAccountService;
	@Autowired
	private IBsCompanyOurService bsCompanyOurService;
	@Autowired
	private IApplyChargeSalesClient applyChargeSalesClient;
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private BsDictDataDao bsDictDataDao;
	@Resource
	private MidstreamUtil midstreamUtil;

	@Override
	public BaseDao<CtrContractText> getBaseDao() {
		return ctrContractTextDao;
	}

	@Override
	public Class<CtrContractText> getEntityClazz() {
		return CtrContractText.class;
	}

	@Override
	@ServerTransactional
	public CtrContractText saveContractText(CtrContract entity) throws ApplicationException {
		CtrContractText saveContractText = saveContractText(entity, null);
		return saveContractText;
	}

	@Override
	@ServerTransactional
	public CtrContractText saveServiceText(CtrServiceContract ctrServiceContract) throws ApplicationException {
		Long bsTemplateContractId = ctrServiceContract.getBsTemplateContractId();
		if (bsTemplateContractId != null && bsTemplateContractId != 0L){
			CtrContractText contractTex = ctrContractTextDao.findByCtrContractIdAndContractType(ctrServiceContract.getId(),BasConstants.CONTRACT_TYPE_F);
			if(contractTex!=null){
				ctrContractTextDao.delete(contractTex);
			}
			BsContractTemplateVo templateVo = new BsContractTemplateVo();
			BsContractTemplate tempalte = contractTemplateDao.findOne(ctrServiceContract.getBsTemplateContractId());
			templateVo.setContent(tempalte.getContent());
			templateVo.setTemplateId(tempalte.getId());
			CtrContractTextVo vo = new CtrContractTextVo();
			CtrContractText text = new CtrContractText();
			text.setCtrContractId(ctrServiceContract.getId());
			text.setEnterpriseId(ctrServiceContract.getEnterpriseId());
			text.setTemplateId(templateVo.getTemplateId());
			BeanUtils.copyProperties(ctrServiceContract, vo);
			//格式化金额
			vo.setTottalAmountStr(RmbUtil.number2Chinese(ctrServiceContract.getTotalAmount()));
			//格式化时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			vo.setContractTimeStr(sdf.format(ctrServiceContract.getContractStartTime()));
			vo.setServiceContractNo(ctrServiceContract.getServiceContractNo());
			text.setEnterpriseId(ctrServiceContract.getEnterpriseId());
			text.setContractType(BasConstants.CONTRACT_TYPE_F);
			text.setContent(contentMerge(templateVo.getContent(),vo));
			CtrContractText contractText = ctrContractTextDao.save(text);
			return contractText;
		}
		return null;
	}

	@Override
	@ServerTransactional
	public CtrContractText saveContractText(CtrContract entity,List<CtrProduct> lstProduct) throws ApplicationException {
		return saveContractText(entity, lstProduct, null);
	}

	@Override
	@ServerTransactional
	public CtrContractText saveContractText(CtrContract entity,List<CtrProduct> lstProduct, ApplyMatchDetail matchDetails) throws ApplicationException {
		String contractType = entity.getContractType();
		if (StringUtils.equals(BasConstants.CONTRACTTYPE_SELL,contractType)){
			if (StringUtils.isNotBlank(entity.getSellContentFileId())){
				return null;
			}
		}else{
			if (StringUtils.isNotBlank(entity.getBuyContentFileId())){
				return null;
			}
		}
		List<CtrProductVo> productTextList = new ArrayList<>();
		String deliveryType = entity.getDeliveryType();
		String qualityStandard = entity.getQualityStandard();
		Date deliveryDateFrom = entity.getDeliveryDateFrom();
		String attachDeliveryTime = entity.getAttachDeliveryTime();
		String deliveryAddr = entity.getDeliveryAddr();
		String invoiceDate = entity.getInvoiceDate();
		CtrContractText contractTex = ctrContractTextDao.findByCtrContractIdAndContractType(entity.getId(),entity.getContractType());
		if(contractTex!=null){
			ctrContractTextDao.delete(contractTex);
		}
		BsContractTemplateVo templateVo = getTemplateContent(entity);

		if(StringUtils.isNotBlank(templateVo.getContent())){
			String wareHouseName = "";
			String wareHouseAddr = "";
			CtrContractText text = new CtrContractText();
			text.setCtrContractId(entity.getId());
			text.setEnterpriseId(entity.getEnterpriseId());
			text.setTemplateId(templateVo.getTemplateId());
			CtrContractTextVo vo = new CtrContractTextVo();
			BeanUtils.copyProperties(entity, vo);

			vo.setPayRateAmount(entity.getBondAmount());
			//获取合同的商品信息
			if (lstProduct == null) {
				lstProduct = productDao.findByCtrContractId(entity.getId());
			}
			// 总计
			BigDecimal totalAmount = BigDecimal.ZERO;
			String rates = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_TAX_RATES, BasConstants.DICT_TYPE_TAX_RATES_SL);
			BigDecimal sumDealNumber = BigDecimal.ZERO;
			BigDecimal sumTaxPriceNoTax = BigDecimal.ZERO;
			BigDecimal sumTotalPriceNoTax = BigDecimal.ZERO;
			BigDecimal sumDealPrice = BigDecimal.ZERO;
			BigDecimal sumTotalPrice = BigDecimal.ZERO;
			// 1.13
			BigDecimal taxRates = BigDecimal.ONE.add(new BigDecimal(rates));

			for (CtrProduct ctrProduct : lstProduct) {
				CtrProductVo productVo = new CtrProductVo();
				BeanUtils.copyProperties(ctrProduct, productVo);
				wareHouseName = ctrProduct.getWarehouseName();
				//包装规格
				String wrapSpecs = ctrProduct.getWrapSpecs();
				if (StringUtils.isNotBlank(wrapSpecs)) {
					String value = DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT, wrapSpecs);
					productVo.setWrapSpecsStr(value);
					productVo.setWrapSpecs(value);

				}
				// 不含税单价
				productVo.setDealAmountNotax(productVo.getDealPrice().divide(taxRates, 2, RoundingMode.HALF_UP));

				BigDecimal dealPriceNoTax = ctrProduct.getDealPrice().divide(taxRates,2, RoundingMode.HALF_UP);
				// 不含税单价
				productVo.setDealPriceNoTax(dealPriceNoTax);
				BigDecimal totalPriceNoTax = ctrProduct.getTotalPrice().divide(taxRates, 2, RoundingMode.HALF_UP);
				// 不含税总价
				productVo.setTotalPriceNoTax(totalPriceNoTax);
				sumDealNumber = sumDealNumber.add(ctrProduct.getDealNumber());
				sumDealPrice = ctrProduct.getDealPrice();
				sumTotalPrice = sumTotalPrice.add(ctrProduct.getTotalPrice());
				sumTaxPriceNoTax = sumTaxPriceNoTax.add(dealPriceNoTax);
				sumTotalPriceNoTax = sumTotalPriceNoTax.add(totalPriceNoTax);
				productTextList.add(productVo);
				// 总计
				totalAmount = totalAmount.add(productVo.getTotalPrice());

			}
			vo.setProductList(productTextList);
			vo.setOurCompanyName(entity.getOurCompanyName());
			vo.setTotalAmount(totalAmount);
			vo.setTotalAmountStr(RmbUtil.number2Chinese(totalAmount));

			vo.setSumDealNumber(sumDealNumber);
			vo.setSumTaxPriceNoTax(sumTaxPriceNoTax);
			vo.setSumTotalPriceNoTax(sumTotalPriceNoTax);
			vo.setSumDealPrice(sumDealPrice);
			vo.setSumTotalPrice(sumTotalPrice);

			DecimalFormat df = new DecimalFormat("0%");
			BigDecimal payRate = entity.getBondRate();
			if(payRate == null){
				vo.setBondRateStr("");
			} else {
				vo.setBondRateStr(df.format(payRate));
			}

			if (StringUtils.isNotBlank(wareHouseName)) {
				List<BsWarehouse> list = bsWarehouseDao.findByWarehouseNameAndEnterpriseId(wareHouseName,
						entity.getEnterpriseId());
				for (BsWarehouse bsWarehouse : list) {
					List<BsWarehouseAddr> addr = bsWarehouseAddrDao.queryDefaultFlg(bsWarehouse.getId(), true);
					if (addr.size() > 0) {
						wareHouseAddr = addr.get(0).getWarehouseAddr();
					}
				}
			}

			//格式化时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			vo.setContractTimeStr(sdf.format(entity.getContractTime()));
			if(entity.getDeliveryDateFrom()==null){
				//若无值，则设置为当天
				entity.setDeliveryDateFrom(new Date());
			}
			if(entity.getDeliveryDateTo()==null) {
				entity.setDeliveryDateTo(new Date());
			}

			if(entity.getBusinessTypeDcsx()!=null){
				if("S".equals(entity.getContractType()) ){
					vo.setPayMode("电汇");
				}
				if("B".equals(entity.getContractType())){
					String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,entity.getPayType());
					vo.setPayMode(payType);
				}
			}else{
				String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,entity.getPayType());
				vo.setPayMode(payType);
			}
			if(entity.getPayFullTime()!=null){
				vo.setPayRemaindTime(sdf.format(entity.getPayFullTime()));
			}
			if(entity.getPayBondTime()!=null){
				vo.setPayBondTimeStr(sdf.format(entity.getPayBondTime()));
			}
			BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(entity.getOurCompanyName());
			if(byCompanyName!=null){
				vo.setSignAddress(byCompanyName.getSigningAddr());
			}
			entity.setContractModel(entity.getBusinessTypeDcsx());
			if (entity.getContractModel()!=null&&entity.getContractModel().equals("BL")){
				vo.setAdditionalAgreement(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_ADDITONALAGEREEMENT, "bctk"));
			}
			//交货日期
			attachDeliveryTime = attachDeliveryTime == null ? BasConstants.ATTACH_DELIVERY_TIME_LR :attachDeliveryTime;
			String attachDeliveryTimeStr = DictUtil.getValue(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME, attachDeliveryTime);
			String deliveryDate = "";
			if(deliveryDateFrom!=null) {
				deliveryDate = sdf.format(deliveryDateFrom);
				vo.setDeliveryTimeStr(deliveryDate);
			}
			attachDeliveryTimeStr = StringUtils.isBlank(attachDeliveryTimeStr) ? "" : attachDeliveryTimeStr;
			vo.setDeliveryDateStr(deliveryDate + attachDeliveryTimeStr);
			if (StringUtils.equals(attachDeliveryTime, BasConstants.ATTACH_DELIVERY_TIME_K)) {
				vo.setDeliveryDateStr(attachDeliveryTimeStr);
			}
			if(entity.getPayFullTime()!=null) {
				vo.setPayTimeStr(sdf.format(entity.getPayFullTime()));
			} else{
				vo.setPayTimeStr("");
			}
			if(vo.getContactAddr()==null){
				vo.setContactAddr("");
			}


			// 交货地点
			String deliveryAddr1 = entity.getDeliveryAddr() == null ? "" : entity.getDeliveryAddr();
			String contactAddr = entity.getContactAddr() == null ? "" : entity.getContactAddr();
			vo.setDeliAddr(deliveryAddr1+contactAddr);
			// 账期

			vo.setCreditCycle(entity.getCreditCycle());

			//格式化提货方式
			if(entity.getDeliveryType().equals(BasConstants.DICT_TYPE_BUYDELIVERY_Z)){
				vo.setDeliveryTypeName(BsDictUtil.getValue(entity.getEnterpriseId(),BasConstants.DICT_TYPE_BUYDELIVERY, BasConstants.DICT_TYPE_BUYDELIVERY_Z));
			}else{
				vo.setDeliveryTypeName(BsDictUtil.getValue(entity.getEnterpriseId(), BasConstants.DICT_TYPE_BUYDELIVERY, BasConstants.DICT_TYPE_BUYDELIVERY_P));
			}
			//格式化交货方式
			vo.setDeliveryMode(BsDictUtil.getValue(entity.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE, entity.getDeliveryMode()));
			if(StringUtils.isBlank(entity.getDeliveryMode())){
				vo.setDeliveryMode("");
			}else{
				vo.setDeliveryMode(entity.getDeliveryMode());
			}
			//配送地址deliveryAddr
			if (StringUtils.isNotBlank(deliveryAddr)) {
				vo.setDeliveryAddr(deliveryAddr);
			}else {
				vo.setDeliveryAddr(wareHouseAddr);
			}
			//联系电话
			if(StringUtils.isBlank(entity.getContactPhone())){
				vo.setContactPhone("");
			}
			//联系人
			if(StringUtils.isBlank(entity.getContactName())){
				vo.setContactName("");
			}
			if(entity.getCompanyId()!=null){
				CompanyAccountVo company = bsCompanyService.findCompanyAccountVo(entity.getCompanyId());
				//收款行、收款账号
				if(StringUtils.isBlank(company.getBankAccount())){
					vo.setReceiveAccount("");
				}else{
					vo.setReceiveAccount(company.getBankAccount());
				}

				if(StringUtils.isBlank(company.getBankName())){
					vo.setReceiveBank("");
				}else{
					vo.setReceiveBank(company.getBankName());
				}

				if(StringUtils.isBlank(company.getEmail())) {
					vo.setEmail("");
				}else {
					vo.setEmail(company.getEmail());
				}
			}else{
				vo.setReceiveAccount("");
				vo.setReceiveBank("");
				vo.setEmail("");
			}
			SysUserSdk matchUser = authOpenFacade.findUserById(entity.getMatchUserId());
			vo.setMatchUserPhone(StringUtils.isBlank(matchUser.getPhonenumber()) ? "" : matchUser.getPhonenumber());
			if(entity.getBondAmount()==null){
				vo.setBondAmount(new BigDecimal(0));
			}else{
				vo.setBondAmount(entity.getBondAmount());
			}
			if(entity.getTotalAmount()==null){
				vo.setTotalAmount(new BigDecimal(0));
			}else{
				vo.setTotalAmount(entity.getTotalAmount());
			}
			if (entity.getBondAmount() == null) {
				entity.setBondAmount(BigDecimal.ZERO);
			}
			if(BigDecimal.ZERO.equals(entity.getBondAmount())){
				vo.setStatus("H");
			}else if(entity.getTotalAmount().equals(entity.getBondAmount())){
				vo.setStatus("Q");
			}else{
				vo.setStatus("B");
			}
			BsCompanyOur companyOur = bsCompanyOurService.findByCompanyName(entity.getOurCompanyName());
			if(Objects.nonNull(companyOur)){
				vo.setOurBankName(StringUtils.isBlank(companyOur.getCompanyBankName()) ? "" : companyOur.getCompanyBankName());
				vo.setOurBankAccount(StringUtils.isBlank(companyOur.getCompanyCardId()) ? "" : companyOur.getCompanyCardId());
				String businessTypeDcsx = entity.getBusinessTypeDcsx();
				if (StringUtils.isNotBlank(businessTypeDcsx) && businessTypeDcsx.contains("HDFK")){
					vo.setOurBankName(StringUtils.isBlank(companyOur.getCompanyBankName2()) ? "" : companyOur.getCompanyBankName2());
					vo.setOurBankAccount(StringUtils.isBlank(companyOur.getCompanyCardId2()) ? "" : companyOur.getCompanyCardId2());
				}
				vo.setOurCompanyAddres(StringUtils.isBlank(companyOur.getAddress()) ? "" : companyOur.getAddress());
				vo.setOurCompanyEmail(StringUtils.isBlank(companyOur.getEmail()) ? "" : companyOur.getEmail());
				vo.setOurCompanyFax(StringUtils.isBlank(companyOur.getCompanyFax()) ? "" : companyOur.getCompanyFax());
				vo.setOurCompanyPerson(StringUtils.isBlank(companyOur.getCompanyPerson()) ? "" : companyOur.getCompanyPerson());
				vo.setMatchUserPhone(StringUtils.isBlank(companyOur.getCompanyPhone()) ? "" : companyOur.getCompanyPhone());
				vo.setOurCompanyName(StringUtils.isBlank(companyOur.getCompanyName()) ? "" : companyOur.getCompanyName());
				vo.setSignAddress(StringUtils.isBlank(companyOur.getSigningAddr()) ? "" : companyOur.getSigningAddr());
				vo.setOurCompanyContact(StringUtils.isBlank(companyOur.getCompanyContact()) ? "" : companyOur.getCompanyContact());
				vo.setOurCompanyPhone(StringUtils.isBlank(companyOur.getCompanyPhone()) ? "" : companyOur.getCompanyPhone());
			}else {
				BsCompanyDcsx bsCompanyDcsx = bsCompanyDcsxClient.findByCompanyName(entity.getOurCompanyName());
				if (bsCompanyDcsx!=null) {
					String companyPerson = bsCompanyDcsx.getCompanyPerson();
					String companyContact = bsCompanyDcsx.getCompanyContact();
					String companyFax = bsCompanyDcsx.getCompanyFax();
					String companyPhone =bsCompanyDcsx.getCompanyPhone();

					vo.setOurBankName(StringUtils.isBlank(bsCompanyDcsx.getCompanyBankName()) ? "" : bsCompanyDcsx.getCompanyBankName());
					vo.setOurBankAccount(StringUtils.isBlank(bsCompanyDcsx.getCompanyCardId()) ? "" : bsCompanyDcsx.getCompanyCardId());
					vo.setOurCompanyAddres(StringUtils.isBlank(bsCompanyDcsx.getAddress()) ? "" : bsCompanyDcsx.getAddress());
					vo.setOurCompanyPerson(StringUtils.isBlank(companyPerson) ? "" : companyPerson);
					vo.setOurCompanyFax(StringUtils.isBlank(companyFax) ? "" : companyFax);
					vo.setMatchUserName(StringUtils.isBlank(companyContact) ? "" : companyContact);
					vo.setMatchUserPhone(StringUtils.isBlank(companyPhone) ? "" : companyPhone);
					vo.setOurCompanyName(StringUtils.isBlank(entity.getOurCompanyName()) ? "" : entity.getOurCompanyName());
					vo.setSignAddress(StringUtils.isBlank(bsCompanyDcsx.getSigningAddr()) ? "" : bsCompanyDcsx.getSigningAddr());
					vo.setOurCompanyContact(StringUtils.isBlank(companyContact) ? "" : companyContact);
					vo.setOurCompanyPhone(StringUtils.isBlank(companyPhone) ? "" : companyPhone);
				}
			}
			//公司法人
			BsCompany company = bsCompanyService.getEntity(entity.getCompanyId());
			String legalRepresent = Objects.nonNull(company) ? company.getLegalRepresent() : "";
			String contactPhone = Objects.nonNull(company) ? company.getContactPhone() : "";
			String address = Objects.nonNull(company) ? company.getAddress() : "";
			String contactName = Objects.nonNull(company) ? company.getContactName() : "";
			String companyFax = Objects.nonNull(company) ? company.getCompanyFax() : "";
			String bankName = Objects.nonNull(company) ? company.getBankName() : "";
			String bankAccount = Objects.nonNull(company) ? company.getBankAccount() : "";
			vo.setCompanyPerson(legalRepresent);
			vo.setContactName(contactName);
			vo.setContactPhone(contactPhone);
			vo.setContactAddr(address);
			vo.setCompanyFax(companyFax);
			List<BsCompanyAccount> accountList = null;
			if (Objects.nonNull(company)){
				accountList = bsCompanyAccountService.findByCompanyId(company.getId());
			}
			if(CollectionUtils.isNotEmpty(accountList)) {
				BsCompanyAccount account = accountList.stream().filter(a -> Boolean.TRUE.equals(a.getDefaultFlg())).findAny().orElse(new BsCompanyAccount());
				vo.setBankName(StringUtils.isBlank(account.getBankName()) ? "" : account.getBankName());
				vo.setBankAccount(StringUtils.isBlank(account.getBankAccount()) ? "" : account.getBankAccount());
			} else {
				vo.setBankName(bankName);
				vo.setBankAccount(bankAccount);
			}

			String qualityStandardStr = "";
			String deliveryTypeStr = "";
			if (StringUtils.equals(BasConstants.CONTRACTTYPE_BUY, entity.getContractType())) {
				qualityStandardStr = DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT,
						StringUtils.isBlank(qualityStandard) ? BasConstants.QUALITY_Y : qualityStandard);
				deliveryTypeStr = midstreamUtil.generateRespStr(entity.getOurCompanyName(), DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, deliveryType));
			} else {
				qualityStandardStr = DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD,
						StringUtils.isBlank(qualityStandard) ? BasConstants.QUALITY_Y : qualityStandard);
				deliveryTypeStr = midstreamUtil.generateRespStr(entity.getOurCompanyName(), DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, deliveryType));
			}
			// 配送
			if (BasConstants.DICT_TYPE_BUYDELIVERY_P.equals(deliveryType)) {
				vo.setFreightBearing("如供方配送，费用由供方承担。");
			} else if (BasConstants.DICT_TYPE_BUYDELIVERY_Z.equals(deliveryType)) {
				// 自提
				vo.setFreightBearing("如需方自提，提货费用自理；若产生入库费用，由需方承担。仓储费自交割日期免三天，超期仓储费和损耗由需方承担。");
			}
			vo.setReceiptArrivedStr(Boolean.TRUE.equals(entity.getReceiptArrivedFlg()) ? "，货到票到" : "");
			// 质量标准
			vo.setQualityStandardStr(qualityStandardStr);
			//交货方式
			vo.setDeliveryTypeStr(deliveryTypeStr);
			//开票时间
			if (invoiceDate != null) {
				String invoiceDateStr = DictUtil.getValue(BasConstants.DICT_TYPE_INVOICEDATE, invoiceDate);
				vo.setInvoiceDateStr(invoiceDateStr);
			}
			setCompanyParam(vo,vo.getOurCompanyName());
			text.setContractType(entity.getContractType());

			// 奥顺宇特殊处理
			if (Objects.nonNull(matchDetails)){
				boolean specialFlg = ctrContractService.judgeUseSpecialBankApplyMatchDetailId(matchDetails.getId());
				if (Boolean.TRUE.equals(specialFlg)) {
					BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(BasConstants.ZG_ENTERPRISE_ID);
					if (Objects.nonNull(specialBank)) {
						vo.setOurBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
						vo.setOurBankAccount(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());

					}
				}
				ApplyMatch applyMatch = applyMatchDao.findOne(matchDetails.getApplyMatchId());
				if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, entity.getContractType())
						&& StringUtils.equals(BasConstants.ZJWS_COMPANY_NAME, entity.getOurCompanyName())
						&& StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, applyMatch.getSellOurCompanyName())){
					vo.setOurBankName("上海银行股份有限公司杨浦支行");
					vo.setOurBankAccount("03006283844");
				}


				vo = dealWithSpecialBank(vo, applyMatch);
				vo = dealWithExtraBank(vo, applyMatch, entity, companyOur, entity.getContractType());
				if (StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, applyMatch.getBuyOurCompanyName())
						&& StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, applyMatch.getSellOurCompanyName())
						&& StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, applyMatch.getOurCompanyName())){
					if (Boolean.TRUE.equals(entity.getSpecialChainFlag()) || StringUtils.equals(BasConstants.CONTRACT_TYPE_S, entity.getContractType())){
						vo.setSignAddress("上海市金山区");
					}
				}
			}
			text.setContent(contentMerge(templateVo.getContent(),vo));
			return ctrContractTextDao.save(text);
		}
		return null;
	}

	@Override
	public CtrContractText findByContractIdAndContractType(Long contractId,String contractType) {
		return ctrContractTextDao.findByCtrContractIdAndContractType(contractId,contractType);
	}

	/**
	 * 根据代采申请单信息合并合同模板
	 * @param textVo
	 * @return
	 */
	@Override
	public String synthesisMathContractText(MatchContractTextVo textVo) throws ApplicationException {
		Boolean specialFlag = textVo.getSpecialFlag();
		Long enterpriseId = textVo.getEnterpriseId();
		BsContractTemplate template = null;
		ApplyMatchDetail matchDetail = applyMatchDetailDao.findOne(textVo.getMatchApplyId());
		ApplyMatch applyMatch = applyMatchDao.findOne(matchDetail.getApplyMatchId());
		if (Boolean.TRUE.equals(specialFlag) && StringUtils.equals(BasConstants.COMPANY_NAME_FLK, applyMatch.getBuyOurCompanyName())) {
			BsContractTemplate specialTemplate = contractTemplateDao.findByTemplateTagAndEnterpriseId(BasConstants.TEMPLATETAG_BUY_FLK_DC_CONTRACT, enterpriseId);
			template = Objects.nonNull(specialTemplate) ? specialTemplate : template;
		}
		if (Boolean.TRUE.equals(specialFlag) && StringUtils.equals(BasConstants.COMPANY_NAME_ZJKR, applyMatch.getBuyOurCompanyName())) {
			BsContractTemplate specialTemplate = contractTemplateDao.findByTemplateTagAndEnterpriseId(BasConstants.TEMPLATETAG_BUY_ZJKR_AHZY_CONTRACT, enterpriseId);
			template = Objects.nonNull(specialTemplate) ? specialTemplate : template;
		}
		if (Boolean.TRUE.equals(specialFlag) && StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, applyMatch.getBuyOurCompanyName())) {
			BsContractTemplate specialTemplate = contractTemplateDao.findByTemplateTagAndEnterpriseId("sgx_buy_contract_template", enterpriseId);
			template = Objects.nonNull(specialTemplate) ? specialTemplate : template;
		}
		if (Boolean.TRUE.equals(specialFlag) && StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, applyMatch.getBuyOurCompanyName())) {
			BsContractTemplate specialTemplate = contractTemplateDao.findByTemplateTagAndEnterpriseId("sgx_buy_contract_template", enterpriseId);
			template = Objects.nonNull(specialTemplate) ? specialTemplate : template;
		}
		if (Boolean.TRUE.equals(specialFlag) && StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, applyMatch.getBuyOurCompanyName())) {
			BsContractTemplate specialTemplate = contractTemplateDao.findByTemplateTagAndEnterpriseId("sgx_buy_contract_template", enterpriseId);
			template = Objects.nonNull(specialTemplate) ? specialTemplate : template;
		}
		if (Objects.isNull(template)){
			template = contractTemplateDao.findByIdAndEnterpriseId(textVo.getTemplateId(), enterpriseId);
		}
		if (Objects.isNull(template)){
			return null;
		}

		List<ApplyProductDetail> productDetailList = applyProductDetailDao.findApplyDetail(textVo.getMatchApplyId(), BasConstants.APPLY_TYPE_M);
		CtrContractTextVo ctrContractTextVo = paramConversion(matchDetail, productDetailList, specialFlag);
		setCompanyParam(ctrContractTextVo,ctrContractTextVo.getOurCompanyName());
		ctrContractTextVo.setDeliAddr(matchDetail.getDeliveryAddr()+matchDetail.getContactAddr());
		// 交货方式
		String deliveryTypeStr = midstreamUtil.generateRespStr(matchDetail.getOurCompanyName(), DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY,matchDetail.getDeliveryType()));
		ctrContractTextVo.setDeliveryTypeStr(deliveryTypeStr);
		BigDecimal payRateAmount = ctrContractTextVo.getPayRateAmount();
		if(payRateAmount==null){
			ctrContractTextVo.setPayRateAmount(BigDecimal.ZERO);
		}
		if (StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DC_TP_CONTRACT_SUGX, template.getTemplateTag())) {
			ctrContractTextVo.setQualityStandardStr(DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD, matchDetail.getQualityStandard()));
		}
		if (Boolean.TRUE.equals(specialFlag)) {
			String buyOurCompanyName = applyMatch.getBuyOurCompanyName();
			String sellOurCompanyName = applyMatch.getSellOurCompanyName();
			if (!StringUtils.equals(buyOurCompanyName, sellOurCompanyName) &&
					(StringUtils.equals(BasConstants.COMPANY_NAME_FLK, buyOurCompanyName)
							|| StringUtils.equals(BasConstants.COMPANY_NAME_ZJKR, buyOurCompanyName)
							|| StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, buyOurCompanyName))){
				String contractNo = ctrContractTextVo.getContractNo();
				contractNo = contractNo.replaceAll("SPTB","SPT1");
				contractNo = contractNo.replaceAll("KCB","KC1");
				contractNo = contractNo.replaceAll("XYB","XY1");
				ctrContractTextVo.setContractNo(contractNo);
				ctrContractTextVo.setCompanyName(buyOurCompanyName);
				ctrContractTextVo.setOurCompanyName(sellOurCompanyName);
			}
		}
		if (StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, applyMatch.getBuyOurCompanyName())
				&& StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, applyMatch.getSellOurCompanyName())
				&& StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, applyMatch.getOurCompanyName())
				&& Boolean.TRUE.equals(specialFlag)){
				ctrContractTextVo.setSignAddress("上海市金山区");
		}
		return contentMerge(template.getContent(), ctrContractTextVo);
	}

	private void setCompanyParam(CtrContractTextVo tContract, String companyName) {
		BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(companyName);
		if (byCompanyName != null) {
			String companyContact = byCompanyName.getCompanyContact();
			String companyPhone = byCompanyName.getCompanyPhone();
			tContract.setMatchUserName(StringUtils.isBlank(companyContact) ? "" : companyContact);
			tContract.setMatchUserPhone(StringUtils.isBlank(companyPhone) ? "" : companyPhone);
		}
	}

	/**
	 * 根据企业代码或企业名称获取合同我方补充信息字段
	 * @param companyName
	 * @param enterpriseId
	 * @return
	 */
	@Override
	public OurCompanyContractDetail findOurCompanyContractDetail(String companyName, Long enterpriseId) {
		OurCompanyContractDetail detail = new OurCompanyContractDetail();
		BsCompanyOur bsCompanyOur = BsCompanyOurUtil.getBsCompanyOur(enterpriseId, companyName);
		if(Objects.nonNull(bsCompanyOur)){
			String signingAddr = bsCompanyOur.getSigningAddr();
			String ourCompanyPerson = bsCompanyOur.getCompanyPerson();
			String ourCompanyContact = bsCompanyOur.getCompanyContact();
			String ourCompanyFax = bsCompanyOur.getCompanyFax();
			String ourCompanyPhone = bsCompanyOur.getCompanyPhone();
			String ourCompanyTaxNo = bsCompanyOur.getCompanyTaxNo();
			String ourCompanyBankName = bsCompanyOur.getCompanyBankName();
			String ourCompanyBankNo = bsCompanyOur.getCompanyTaxNo();

			detail.setSigningAddr(StringUtils.isBlank(signingAddr) ? "" : signingAddr);
			detail.setOurCompanyPerson(StringUtils.isBlank(ourCompanyPerson) ? "" : ourCompanyPerson);
			detail.setOurCompanyContact(StringUtils.isBlank(ourCompanyContact) ? "" : ourCompanyContact);
			detail.setOurCompanyFax(StringUtils.isBlank(ourCompanyFax) ? "" : ourCompanyFax);
			detail.setOurCompanyPhone(StringUtils.isBlank(ourCompanyPhone) ? "" : ourCompanyPhone);
			detail.setOurCompanyTaxNo(StringUtils.isBlank(ourCompanyTaxNo) ? "" : ourCompanyTaxNo);
			detail.setOurCompanyBankName(StringUtils.isBlank(ourCompanyBankName) ? "" : ourCompanyBankName);
			detail.setOurCompanyBankNo(StringUtils.isBlank(ourCompanyBankNo) ? "" : ourCompanyBankNo);
		}
		detail.setOurCompanyName(companyName);
		return detail;
	}

	private CtrContractTextVo paramConversion(ApplyMatchDetail matchDetail, List<ApplyProductDetail> productDetailList, Boolean specialFlag){

		String ourCompanyName = matchDetail.getOurCompanyName();
		String companyName = matchDetail.getCompanyName();
		ApplyMatch applyMatch = applyMatchDao.findOne(matchDetail.getApplyMatchId());
		if (Boolean.TRUE.equals(specialFlag)){
			companyName = applyMatch.getBuyOurCompanyName();
			ourCompanyName = applyMatch.getSellOurCompanyName();
		}
		if (StringUtils.isBlank(ourCompanyName)) {
			//我方企业为空 则为赊销预算 需要从明细主表中拉取数据
			ourCompanyName = applyMatch.getOurCompanyName();
		}
		CtrContractTextVo vo = new CtrContractTextVo();

		BeanUtils.copyProperties(matchDetail,vo);
		if(StringUtils.isBlank(matchDetail.getSettlementType())) {

			if("B".equals(matchDetail.getContractType())){
				vo.setPayRateAmount(matchDetail.getPayRateAmount());
			}
			if("S".equals(matchDetail.getContractType())){
				vo.setPayRateAmount(matchDetail.getReceiveRateAmount());
			}
		} else {
			vo.setPayRateAmount(matchDetail.getPayRateAmount());
		}


		vo.setQualityStandardStr(DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT, matchDetail.getQualityStandard()));

		vo.setDeliveryTypeStr( midstreamUtil.generateRespStr(ourCompanyName, DictUtil.getValue(BasConstants.DICT_DELIVERYTYPETEXT, matchDetail.getDeliveryType())));

		vo.setCompanyName(companyName);
		vo.setOurCompanyName(ourCompanyName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

		if(matchDetail.getReceiveFullTime()!=null){
			String format1 = sdf.format(matchDetail.getReceiveFullTime());
			vo.setPayRemaindTime(format1);
		}
		if(matchDetail.getPayBondTime()!=null){
			String format2 = sdf.format(matchDetail.getPayBondTime());
			vo.setPayBondTimeStr(format2);
		}
		if("B".equals(matchDetail.getContractType())){
			if(matchDetail.getPayFullTime()!=null){
				String format3 = sdf.format(matchDetail.getPayFullTime());
				vo.setPayRemaindTime(format3);
			}
		}
		if("S".equals(matchDetail.getContractType())){
			vo.setExtraTerm(StringUtils.isNotBlank(vo.getExtraTerm())?vo.getExtraTerm():"无");
		}

		ApplyMatch one = applyMatchDao.findOne(matchDetail.getApplyMatchId());
		if (one.getContractModel()!=null && one.getContractModel().equals("BL")){
			vo.setAdditionalAgreement(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_ADDITONALAGEREEMENT, "bctk"));
		}


		ApplyMatch applyMatchs = applyMatchDao.findOne(matchDetail.getApplyMatchId());

		if (matchDetail.getPayBondTime()!=null){
			if("S".equals(matchDetail.getContractType()) ){
				if(matchDetail.getReceiveFullTime()!=null){
					vo.setPayRemaindTime(sdf.format(matchDetail.getReceiveFullTime()));
				}
			}
		}
		if(applyMatchs.getContractModel()!=null){
			if("S".equals(matchDetail.getContractType()) ){
				vo.setPayMode("电汇");
			}
			if("B".equals(matchDetail.getContractType())){
				String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,matchDetail.getPayType());
				vo.setPayMode(payType);
			}
		}else{
			if("S".equals(matchDetail.getContractType()) ){
				String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,matchDetail.getReceiveType());
				vo.setPayMode(payType);
			}
			if("B".equals(matchDetail.getContractType())){
				String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,matchDetail.getPayType());
				vo.setPayMode(payType);
			}

		}
		vo.setContractNo(matchDetail.getContractNo());
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日");
		vo.setContractTimeStr(sdf2.format(matchDetail.getCreatedDate()));
		if(matchDetail.getDeliveryDate()!=null){
			String deliveryDateStr = sdf2.format(matchDetail.getDeliveryDate());
			String attachDeliveryTimeStr = DictUtil.getValue(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME, matchDetail.getArrivalTimeExt());
			vo.setDeliveryDateStr(deliveryDateStr + (StringUtils.isBlank(attachDeliveryTimeStr) ? "" : attachDeliveryTimeStr));
		}
		BigDecimal totalAmount = BigDecimal.ZERO;
		List<CtrProductVo> productList = new ArrayList<>();

		String rates = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_TAX_RATES, BasConstants.DICT_TYPE_TAX_RATES_SL);
		BigDecimal sumDealNumber = BigDecimal.ZERO;
		BigDecimal sumTaxPriceNoTax = BigDecimal.ZERO;
		BigDecimal sumTotalPriceNoTax = BigDecimal.ZERO;
		BigDecimal sumDealPrice = BigDecimal.ZERO;
		BigDecimal sumTotalPrice = BigDecimal.ZERO;

		BigDecimal taxRates = BigDecimal.ONE.add(new BigDecimal(rates));
		for (ApplyProductDetail detail : productDetailList) {
			CtrProductVo product = new CtrProductVo();
			product.setWrapSpecs(DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT,detail.getWrapSpecs()));
			product.setProductName(detail.getProductName());
			product.setBrandNumber(detail.getBrandNumber());
			product.setFactoryName(detail.getFactoryName());
			product.setDealNumber(detail.getDealNumber());
			product.setDealPrice(detail.getDealPrice());
			product.setTotalPrice(detail.getTotalPrice());
			if (Boolean.TRUE.equals(specialFlag) && StringUtils.equals(BasConstants.COMPANY_NAME_FLK, applyMatch.getBuyOurCompanyName())){
				product.setDealPrice(detail.getDealPrice().multiply(new BigDecimal("1.003")).setScale(2, RoundingMode.HALF_UP));
				product.setTotalPrice(product.getDealPrice().multiply(detail.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
			}
			if (Boolean.TRUE.equals(specialFlag) && StringUtils.equals(BasConstants.COMPANY_NAME_ZJKR, applyMatch.getBuyOurCompanyName())){
				product.setDealPrice(detail.getDealPrice().add(new BigDecimal("5")));
				product.setTotalPrice(product.getDealPrice().multiply(detail.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
			}
			if (Boolean.TRUE.equals(specialFlag) && StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, applyMatch.getBuyOurCompanyName())){
				ApplyMatchDetail sellMatchDetail = applyMatchDetailDao.findOtherMatchDetail(matchDetail.getApplyMatchId(), matchDetail.getId());
				Long creditDay = DateOperator.compareDays(matchDetail.getPayFullTime(), sellMatchDetail.getReceiveFullTime()) + 1L;
				BigDecimal sellPrice = sellMatchDetail.getDealPrice();
				BigDecimal realDealPrice = (sellPrice.subtract(new BigDecimal("10"))).divide((BigDecimal.ONE.add((new BigDecimal("0.00019").multiply(new BigDecimal(creditDay))))), 2, RoundingMode.HALF_UP);
				product.setDealPrice(realDealPrice);
				product.setTotalPrice(product.getDealPrice().multiply(detail.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
			}
			if (Boolean.TRUE.equals(specialFlag) && StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, applyMatch.getBuyOurCompanyName())){
				BigDecimal realDealPrice = matchDetail.getDealPrice().add(new BigDecimal("10"));
				product.setDealPrice(realDealPrice);
				product.setTotalPrice(product.getDealPrice().multiply(detail.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
			}
			if (Boolean.TRUE.equals(specialFlag) && StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, applyMatch.getBuyOurCompanyName())){
				ApplyMatchDetail sellMatchDetail = applyMatchDetailDao.findOtherMatchDetail(matchDetail.getApplyMatchId(), matchDetail.getId());
				Long creditDay = DateOperator.compareDays(sellMatchDetail.getDeliveryDate(), sellMatchDetail.getReceiveFullTime()) + 1L;
				BigDecimal sellPrice = sellMatchDetail.getDealPrice();
				BigDecimal buyPrice = matchDetail.getDealPrice();
				BigDecimal realDealPrice = buyPrice.multiply(new BigDecimal("0.1").divide(new BigDecimal(365), 8, RoundingMode.HALF_UP).multiply(new BigDecimal(creditDay))).setScale(2, RoundingMode.HALF_UP);
				product.setDealPrice(sellPrice.subtract(realDealPrice));
				product.setTotalPrice(product.getDealPrice().multiply(detail.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
			}

			BigDecimal dealPriceNoTax = product.getDealPrice().divide(taxRates,2, RoundingMode.HALF_UP);
			// 不含税单价
			product.setDealPriceNoTax(dealPriceNoTax);
			BigDecimal totalPriceNoTax = product.getTotalPrice().divide(taxRates, 2, RoundingMode.HALF_UP);
			// 不含税总价
			product.setTotalPriceNoTax(totalPriceNoTax);

			totalAmount = totalAmount.add(product.getTotalPrice());
			sumDealNumber = sumDealNumber.add(product.getDealNumber());
			sumDealPrice = product.getDealPrice();
			sumTotalPrice = sumTotalPrice.add(product.getTotalPrice());
			sumTaxPriceNoTax = sumTaxPriceNoTax.add(dealPriceNoTax);
			sumTotalPriceNoTax = sumTotalPriceNoTax.add(totalPriceNoTax);
			productList.add(product);
		}
		vo.setProductList(productList);
		vo.setTotalAmountStr(RmbUtil.number2Chinese(totalAmount));

		vo.setSumDealNumber(sumDealNumber);
		vo.setSumTaxPriceNoTax(sumTaxPriceNoTax);
		vo.setSumTotalPriceNoTax(sumTotalPriceNoTax);
		vo.setSumDealPrice(sumDealPrice);
		vo.setSumTotalPrice(sumTotalPrice);
		vo.setBondAmount(matchDetail.getPayBondAmount());

		DecimalFormat df = new DecimalFormat("0%");
		BigDecimal payRate = matchDetail.getPayRate();

		// 判断是否是代采
		boolean matchCreditFlg = !StringUtils.isEmpty(matchDetail.getSettlementType());
		if(!matchCreditFlg){
			vo.setBondAmount(matchDetail.getReceiveBondAmount());
			payRate = matchDetail.getReceiveRate();
		}

		if(payRate == null){
			vo.setBondRateStr("");
		} else {
			vo.setBondRateStr(df.format(payRate));
		}

		BsCompany company = bsCompanyService.findByCompanyName(vo.getCompanyName());
		if (Objects.nonNull(company)) {
			String legalRepresent = company.getLegalRepresent();
			String contactPhone = company.getContactPhone();
			String address = company.getAddress();
			String contactName = company.getContactName();
			String companyFax = company.getCompanyFax();

			vo.setCompanyPerson(StringUtils.isBlank(legalRepresent) ? "" : legalRepresent);
			vo.setContactName(StringUtils.isBlank(contactName) ? "" : contactName);
			vo.setContactPhone(StringUtils.isBlank(contactPhone) ? "" : contactPhone);
			vo.setContactAddr(StringUtils.isBlank(address) ? "" : address);
			vo.setCompanyFax(StringUtils.isBlank(companyFax) ? "" : companyFax);
			vo.setEmail(StringUtils.isBlank(company.getEmail()) ? "" : company.getEmail());

			List<BsCompanyAccount> accountList = bsCompanyAccountService.findByCompanyId(company.getId());

			if(CollectionUtils.isNotEmpty(accountList)) {
				BsCompanyAccount account = null;
				for (BsCompanyAccount companyAccount : accountList) {
					if(Boolean.TRUE.equals(companyAccount.getDefaultFlg())){
						account = companyAccount;
					}
				}
				if(Objects.isNull(account)){
					account = accountList.get(0);
				}
				vo.setBankName(StringUtils.isBlank(account.getBankName()) ? "" : account.getBankName());
				vo.setBankAccount(StringUtils.isBlank(account.getBankAccount()) ? "" : account.getBankAccount());
			} else {
				vo.setBankName(StringUtils.isBlank(company.getBankName()) ? "" : company.getBankName());
				vo.setBankAccount(StringUtils.isBlank(company.getBankAccount()) ? "" : company.getBankAccount());
			}
		}

		BsCompanyOur companyOur = bsCompanyOurService.findByCompanyName(ourCompanyName);
		if(Objects.nonNull(companyOur)){
			vo.setOurBankName(StringUtils.isBlank(companyOur.getCompanyBankName()) ? "" : companyOur.getCompanyBankName());
			vo.setOurBankAccount(StringUtils.isBlank(companyOur.getCompanyCardId()) ? "" : companyOur.getCompanyCardId());
			String businessTypeDcsx = applyMatch.getContractModel();
			if (StringUtils.isNotBlank(businessTypeDcsx) && businessTypeDcsx.contains("HDFK")){
				vo.setOurBankName(StringUtils.isBlank(companyOur.getCompanyBankName2()) ? "" : companyOur.getCompanyBankName2());
				vo.setOurBankAccount(StringUtils.isBlank(companyOur.getCompanyCardId2()) ? "" : companyOur.getCompanyCardId2());
			}
			vo.setOurCompanyAddres(StringUtils.isBlank(companyOur.getAddress()) ? "" : companyOur.getAddress());
			vo.setOurCompanyFax(StringUtils.isBlank(companyOur.getCompanyFax()) ? "" : companyOur.getCompanyFax());
			vo.setOurCompanyPerson(StringUtils.isBlank(companyOur.getCompanyPerson()) ? "" : companyOur.getCompanyPerson());
			vo.setMatchUserPhone(StringUtils.isBlank(companyOur.getCompanyPhone()) ? "" : companyOur.getCompanyPhone());
			vo.setOurCompanyName(StringUtils.isBlank(companyOur.getCompanyName()) ? "" : companyOur.getCompanyName());
			vo.setSignAddress(StringUtils.isBlank(companyOur.getSigningAddr()) ? "" : companyOur.getSigningAddr());
			vo.setOurCompanyEmail(StringUtils.isBlank(companyOur.getEmail()) ? "" : companyOur.getEmail());
			vo.setOurCompanyContact(StringUtils.isBlank(companyOur.getCompanyContact()) ? "" : companyOur.getCompanyContact());
			vo.setOurCompanyPhone(StringUtils.isBlank(companyOur.getCompanyPhone()) ? "" : companyOur.getCompanyPhone());

		}else {
			BsCompanyDcsx bsCompanyDcsx = bsCompanyDcsxClient.findByCompanyName(ourCompanyName);
			if (bsCompanyDcsx!=null) {
				String companyPerson = bsCompanyDcsx.getCompanyPerson();
				String companyContact = bsCompanyDcsx.getCompanyContact();
				String companyFax = bsCompanyDcsx.getCompanyFax();
				String companyPhone =bsCompanyDcsx.getCompanyPhone();

				vo.setOurBankName(StringUtils.isBlank(bsCompanyDcsx.getCompanyBankName()) ? "" : bsCompanyDcsx.getCompanyBankName());
				vo.setOurBankAccount(StringUtils.isBlank(bsCompanyDcsx.getCompanyCardId()) ? "" : bsCompanyDcsx.getCompanyCardId());
				vo.setOurCompanyAddres(StringUtils.isBlank(bsCompanyDcsx.getAddress()) ? "" : bsCompanyDcsx.getAddress());
				vo.setOurCompanyPerson(StringUtils.isBlank(companyPerson) ? "" : companyPerson);
				vo.setOurCompanyFax(StringUtils.isBlank(companyFax) ? "" : companyFax);
				vo.setMatchUserName(StringUtils.isBlank(companyContact) ? "" : companyContact);
				vo.setMatchUserPhone(StringUtils.isBlank(companyPhone) ? "" : companyPhone);
				vo.setOurCompanyName(StringUtils.isBlank(ourCompanyName) ? "" : ourCompanyName);
				vo.setSignAddress(StringUtils.isBlank(bsCompanyDcsx.getSigningAddr()) ? "" : bsCompanyDcsx.getSigningAddr());
				vo.setOurCompanyContact(StringUtils.isBlank(companyContact) ? "" : companyContact);
				vo.setOurCompanyPhone(StringUtils.isBlank(companyPhone) ? "" : companyPhone);
			}
		}
		// 奥顺宇特殊处理
		boolean specialFlg = ctrContractService.judgeUseSpecialBankApplyMatchDetailId(matchDetail.getId());
		if (Boolean.TRUE.equals(specialFlg)) {
			BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(BasConstants.ZG_ENTERPRISE_ID);
			if (Objects.nonNull(specialBank)) {
				vo.setOurBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
				vo.setOurBankAccount(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());

			}
		}
		if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, matchDetail.getContractType())
				&& StringUtils.equals(BasConstants.ZJWS_COMPANY_NAME, ourCompanyName)
				&& StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, applyMatchs.getSellOurCompanyName())){
			vo.setOurBankName("上海银行股份有限公司杨浦支行");
			vo.setOurBankAccount("03006283844");
		}

		if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, matchDetail.getContractType())) {
			vo.setReceiptArrivedStr(Boolean.TRUE.equals(matchDetail.getReceiptArrivedFlg()) ? "，货到票到" : "");
		}
		vo.setDeliAddr(matchDetail.getDeliveryAddr());
		vo.setContractModel(applyMatch.getContractModel());
		vo = dealWithSpecialBank(vo, applyMatch);
		vo = dealWithExtraBank(vo, applyMatch, null, companyOur, matchDetail.getContractType());
		return vo;
	}

	/**
	 * 目前奥顺宇赊销，如果青岛中光抬头，普通赊销——2400账户；货到付款——1034账户
	 * 目前奥顺宇赊销，如果网塑宁波抬头，暂时还是全部用1034账户
	 * @param vo
	 * @param applyMatch
	 * @return
	 */
	private CtrContractTextVo dealWithSpecialBank(CtrContractTextVo vo, ApplyMatch applyMatch) {
		String ourCompanyName = applyMatch.getOurCompanyName();
		String sellOurCompanyName = applyMatch.getSellOurCompanyName();
		String businessTypeDcsx = applyMatch.getContractModel();
		Long enterpriseId = applyMatch.getEnterpriseId();
		BsDictData specialBankFlg = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "specialBankFlg", enterpriseId);
		if (Objects.isNull(specialBankFlg) || !StringUtils.equalsIgnoreCase("true", specialBankFlg.getDictName())) {
			// 不启用特殊银行账号逻辑
			return vo;
		}
		BsDictData bk1 = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "bk1", enterpriseId);
		BsDictData bk2 = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "bk2", enterpriseId);
		String bank1034 = Objects.nonNull(bk1) ? bk1.getDictName() : "636651034";
		String bank2400 = Objects.nonNull(bk2) ? bk2.getDictName() : "637632400";
		// 青岛奥顺宇-青岛中光
		if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, sellOurCompanyName)) {
			boolean modeFlg = StringUtils.isNotBlank(businessTypeDcsx) && businessTypeDcsx.contains("HDFK");
			vo.setOurBankAccount(modeFlg ? bank1034 : bank2400);
		}

		// 青岛奥顺宇-网塑宁波
		if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_WSNB, sellOurCompanyName)) {
			vo.setOurBankAccount(bank1034);
		}
		return vo;
	}


	//将合同内容填充至模板
	@SuppressWarnings("deprecation")
	private String contentMerge(String content,CtrContract entity) throws ApplicationException {
		Configuration  cfg = new Configuration();
		StringWriter sw = new StringWriter();
		try {
			Template t  = new Template("", new StringReader(content), cfg);
			t.process(entity, sw);
			content = sw.toString();
		}  catch (Exception e) {
			throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
		}
		return content;
	}

	//获取模板
	private BsContractTemplateVo getTemplateContent(CtrContract entity){
		Long bsTemplateContractId = entity.getBsTemplateContractId();
		BsContractTemplate template1 = null;
		BsContractTemplateVo vo = new BsContractTemplateVo();
		if (bsTemplateContractId != null && bsTemplateContractId != 0L){
			template1 = contractTemplateDao.findOne(bsTemplateContractId);
		}else{
			Long enterpriseId = entity.getEnterpriseId();
			String contractType = entity.getContractType();
			//先从contractTemplate中获取
			//String templateTag1 = entity.getContractType().equals(BasConstants.CONTRACTTYPE_BUY) ? "contract_buy" : "contract_sale";
			String templateTag1 = "";
			if (StringUtils.equals(BasConstants.CONTRACTTYPE_BUY, contractType)) {
				templateTag1 = "contract_buy";
			}else {
				templateTag1 = "contract_sale";
			}
			template1 = contractTemplateDao.findByTemplateTagAndEnterpriseId(templateTag1, enterpriseId);
		}

		if(template1 !=null && template1.getContent()!=null){
			vo.setContent(template1.getContent());
			vo.setTemplateId(template1.getId());
		}else{
			//从templateConfig中获取
			String templateTag2 = entity.getContractType().equals(BasConstants.CONTRACTTYPE_BUY) ? "FMC_BUY_CONTRACT" : "FMC_SALE_CONTRACT";
			BsTemplateConfig template2 = TemplateContentUtility.getTemplate("matchContract",templateTag2,"CH",entity.getEnterpriseId());
			if(template2 !=null && template2.getContent()!=null){
				vo.setContent(template2.getContent());
				vo.setTemplateId(template2.getId());
			}
		}
		return vo;
	}

	/**
	 * 企业扩展银行逻辑处理
	 * @param textVo
	 * @param applyMatch
	 * @param companyOur
	 * @return
	 */
	@Override
	public CtrContractTextVo dealWithExtraBank(CtrContractTextVo textVo, ApplyMatch applyMatch, CtrContract contract, BsCompanyOur companyOur, String textKind) {
		try {
			if (Objects.isNull(companyOur) || StringUtils.isBlank(companyOur.getExtraBank())) {
				return textVo;
			}
			ExtraBankVo extraBankVo = new ExtraBankVo();
			String extraBankExpress = companyOur.getExtraBank();
			ExpressRunner runner = new ExpressRunner();
			DefaultContext<String, Object> context = new DefaultContext<>();
			context.put("preFlg", Objects.isNull(contract));
			context.put("match", applyMatch);
			context.put("contract", contract);
			context.put("textKind", textKind);
			context.put("extraBank", extraBankVo);
			runner.addFunction("strEQ", new EqOperator());
			runner.addFunction("strNEQ", new NEqOperator());
			runner.execute(extraBankExpress, context, null, false, false);
			if (StringUtils.isNotBlank(extraBankVo.getBankNo())){
				textVo.setOurBankAccount(extraBankVo.getBankNo());
			}
			if (StringUtils.isNotBlank(extraBankVo.getBankName())){
				textVo.setOurBankName(extraBankVo.getBankName());
			}
		} catch (Exception e) {
			logger.error("dealWithExtraBank error", e);
		}
		return textVo;
	}

	/**
	 * 企业扩展银行逻辑处理
	 * @param textVo
	 * @param entity
	 * @return
	 */
	@Override
	public DcContractText dealWithExtraBank(DcContractText textVo,ApplyMatch applyMatch, ApplyCtrDCSX entity, String textKind) {
		try {
			BsCompanyOur companyOur = bsCompanyOurService.findByCompanyName(entity.getOurCompanyName());
			if (Objects.isNull(companyOur) || StringUtils.isBlank(companyOur.getExtraBank())) {
				return textVo;
			}
			ExtraBankVo extraBankVo = new ExtraBankVo();
			String extraBankExpress = companyOur.getExtraBank();
			ExpressRunner runner = new ExpressRunner();
			DefaultContext<String, Object> context = new DefaultContext<>();
			context.put("match", applyMatch);
			context.put("dcsx", entity);
			context.put("textKind", textKind);
			context.put("extraBank", extraBankVo);
			runner.addFunction("strEQ", new EqOperator());
			runner.addFunction("strNEQ", new NEqOperator());
			runner.execute(extraBankExpress, context, null, false, false);
			if (StringUtils.isNotBlank(extraBankVo.getBankNo())){
				textVo.setOurCompanyBankNo(extraBankVo.getBankNo());
			}
			if (StringUtils.isNotBlank(extraBankVo.getBankName())){
				textVo.setOurCompanyBankName(extraBankVo.getBankName());
			}
			if (StringUtils.isNotBlank(extraBankVo.getBankNo1())){
				textVo.setCompanyBankNo(extraBankVo.getBankNo1());
			}
			if (StringUtils.isNotBlank(extraBankVo.getBankName1())){
				textVo.setCompanyBankName(extraBankVo.getBankName1());
			}
		} catch (Exception e) {
			logger.error("dealWithExtraBank error", e);
		}
		return textVo;
	}

	public static class EqOperator extends Operator {
		@Override
		public Object executeInner(Object[] list) {
			String str1 = String.valueOf(list[0]);
			String str2 = String.valueOf(list[1]);
			return StringUtils.equals(str1, str2);
		}
	}

	public static class NEqOperator extends Operator {
		@Override
		public Object executeInner(Object[] list) {
			String str1 = String.valueOf(list[0]);
			String str2 = String.valueOf(list[1]);
			return !StringUtils.equals(str1, str2);
		}
	}
}

