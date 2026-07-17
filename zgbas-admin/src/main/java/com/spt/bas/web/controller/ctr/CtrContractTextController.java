package com.spt.bas.web.controller.ctr;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallbun.screw.core.util.CollectionUtils;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.util.RmbUtil;
import com.spt.bas.client.vo.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.exception.ApplicationException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/ctr/contractText")
public class CtrContractTextController {
	@Autowired
	private IApplyInventoryVirtualClient applyInventoryVirtualClient;
	@Autowired
	private IApplyAgreementVirtualClient applyAgreementVirtualClient;
	@Autowired
	private ICtrContractTextClient contractTextClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private ICtrServiceContractClient ctrServiceContractClient;
	@Autowired
	private IBsContractTemplateClient bsContractTemplateClient;
	@Autowired
	private IBsCompanyClient bsCompanyClient;
	@Autowired
	private  IBsCompanyDcsxClient bsCompanyDcsxClient;
	@Autowired
	private ICtrContractChainTextClient ctrContractChainTextClient;
	@Autowired
	private IApplyChargeSalesClient applyChargeSalesClient;
	@Autowired
	private IBsCompanyOurClient bsCompanyOurClient;
	@Autowired
	private  IPmApproveClient pmApproveClient;
	@Autowired
	private  IApplyCtrDcsxClinent applyCtrDcsxClinent;
	@Autowired
	private IApplyMatchClient applyMatchClient;
	@Autowired
	private IApplyMatchDetailClient applyMatchDetailClient;
	@Resource
	private IMidstreamClient midstreamClient;

	@RequestMapping("getBycontractId/{contractId}")
	public String getByContractId(@PathVariable("contractId") Long contractId,Model model,HttpServletRequest request){
		String contractType;
		CtrContractText text=new CtrContractText();
		String chain = request.getParameter("chain");
		String url = "ctr/contractText";
		CtrContractText contractText = new CtrContractText();
		CtrContractChainText contractChainText= new CtrContractChainText();
		if(StringUtils.equals("T",chain)){
			//预算表未保存中间链条合同（中间链条只为销售合同）
			contractType="S";
			contractChainText.setContractType(contractType);
			contractChainText.setCtrContractId(contractId);
			CtrContractChainText contractText1 = ctrContractChainTextClient.findContractText(contractChainText);
			BeanUtils.copyProperties(contractText1,text);
		}else{
			CtrContractChooseVo contract = ctrContractClient.findByContractId(contractId);
			contractType=contract.getContractType();
			contractText.setContractType(contractType);
			contractText.setCtrContractId(contractId);
			text = contractTextClient.findContractText(contractText);
		}
		if(text!=null){
			model.addAttribute("contractText", text.getContent());
			model.addAttribute("contractId",contractId);
		}else{
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		return url;
	}

	@RequestMapping("getByContractIdNew/{contractId}")
	public String getByContractIdNew(@PathVariable("contractId") Long contractId,Model model,HttpServletRequest request){
		String contractType;
		CtrContractText text=new CtrContractText();
		String chain = request.getParameter("chain");
		String url = "ctr/contractTextNew";
		CtrContractText contractText = new CtrContractText();
		CtrContractChainText contractChainText= new CtrContractChainText();
		if(StringUtils.equals("T",chain)){
			//预算表未保存中间链条合同（中间链条只为销售合同）
			contractType="S";
			contractChainText.setContractType(contractType);
			contractChainText.setCtrContractId(contractId);
			CtrContractChainText contractText1 = ctrContractChainTextClient.findContractText(contractChainText);
			BeanUtils.copyProperties(contractText1,text);
		}else{
			CtrContractChooseVo contract = ctrContractClient.findByContractId(contractId);
			contractType=contract.getContractType();
			contractText.setContractType(contractType);
			contractText.setCtrContractId(contractId);
			text = contractTextClient.findContractText(contractText);
		}
		if(text!=null){
			model.addAttribute("contractText", text.getContent());
			model.addAttribute("contractId",contractId);
		}else{
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		String windowId = request.getParameter("windowId");
		model.addAttribute("windowId", windowId);
		return url;
	}

	@RequestMapping("getServiceText/{contractId}")
	public String getServiceText(@PathVariable("contractId") Long contractId,Model model){
		CtrServiceContract serviceContract = ctrServiceContractClient.findByCtrContract(contractId);
		CtrContractText contractText = new CtrContractText();
		contractText.setCtrContractId(serviceContract.getId());
		contractText.setContractType(BasConstants.CONTRACT_TYPE_F);
		CtrContractText text = contractTextClient.findContractText(contractText);
		if(text!=null){
			model.addAttribute("contractText", text.getContent());
			model.addAttribute("contractId",contractId);
		}else{
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		return "ctr/contractText";
	}

	@RequestMapping("getServiceTextNew/{contractId}")
	public String getServiceTextNew(@PathVariable("contractId") Long contractId,Model model, HttpServletRequest request){
		CtrServiceContract serviceContract = ctrServiceContractClient.findByCtrContract(contractId);
		CtrContractText contractText = new CtrContractText();
		contractText.setCtrContractId(serviceContract.getId());
		contractText.setContractType(BasConstants.CONTRACT_TYPE_F);
		CtrContractText text = contractTextClient.findContractText(contractText);
		if(text!=null){
			model.addAttribute("contractText", text.getContent());
			model.addAttribute("contractId",contractId);
		}else{
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		String windowId = request.getParameter("windowId");
		model.addAttribute("windowId", windowId);
		return "ctr/contractTextNew";
	}

	/**
	 * 获取合同模板
	 * @param templateId
	 * @param model
	 * @return
	 */
	@RequestMapping("getTemplateContract/{templateId}")
	public String getTemplateContract(@PathVariable("templateId") Long templateId,Model model) {
		//采购合同模板
		BsContractTemplate template = new BsContractTemplate();
		template.setEnterpriseId(ShiroUtil.getEnterpriseId());
		template.setContractType(BasConstants.CONTRACT_TYPE_B);
		template.setId(templateId);
		BsContractTemplate bsContractTemplate = bsContractTemplateClient.findByIdAndEnterpriseId(template);
		if (bsContractTemplate != null && !StringUtils.isEmpty(bsContractTemplate.getContent())) {
			model.addAttribute("contractText", bsContractTemplate.getContent());
		}else{
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		return "ctr/contractText";
	}

	/**
	 * 获取合同模板
	 * @param templateId
	 * @param model
	 * @return
	 */
	@RequestMapping("getTemplateContractNew/{templateId}")
	public String getTemplateContractNew(@PathVariable("templateId") Long templateId,Model model, HttpServletRequest request) {
		//采购合同模板
		BsContractTemplate template = new BsContractTemplate();
		template.setEnterpriseId(ShiroUtil.getEnterpriseId());
		template.setContractType(BasConstants.CONTRACT_TYPE_B);
		template.setId(templateId);
		BsContractTemplate bsContractTemplate = bsContractTemplateClient.findByIdAndEnterpriseId(template);
		if (bsContractTemplate != null && !StringUtils.isEmpty(bsContractTemplate.getContent())) {
			model.addAttribute("contractText", bsContractTemplate.getContent());
		}else{
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		String windowId = request.getParameter("windowId");
		model.addAttribute("windowId", windowId);
		return "ctr/contractTextNew";
	}

	/**
	 * 代采赊销预算合同预览
	 * @param templateId
	 * @param model
	 * @return
	 */
	@RequestMapping("getMatchContract/{templateId}")
	public String getMatchContract(@PathVariable("templateId") Long templateId,Model model, HttpServletRequest request) {
		String applyIdStr = request.getParameter("applyId");
		Long applyId = StringUtils.isNotBlank(applyIdStr) ? Long.parseLong(applyIdStr) : 0L;
		MatchContractTextVo textVo = new MatchContractTextVo(templateId,applyId,ShiroUtil.getEnterpriseId());
		String contractText = contractTextClient.synthesisMathContractText(textVo);
		model.addAttribute("contractText", contractText);
		return "ctr/contractText";
	}

	/**
	 * 代采赊销预算合同预览
	 * @param templateId
	 * @param model
	 * @return
	 */
	@RequestMapping("getMatchContractNew/{templateId}")
	public String getMatchContractNew(@PathVariable("templateId") Long templateId,Model model, HttpServletRequest request) {
		String applyIdStr = request.getParameter("applyId");
		Long applyId = StringUtils.isNotBlank(applyIdStr) ? Long.parseLong(applyIdStr) : 0L;
		MatchContractTextVo textVo = new MatchContractTextVo(templateId,applyId,ShiroUtil.getEnterpriseId());
		String contractText = contractTextClient.synthesisMathContractText(textVo);
		model.addAttribute("contractText", contractText);
		String windowId = request.getParameter("windowId");
		model.addAttribute("windowId", windowId);
		return "ctr/contractTextNew";
	}

	/**
	 * 代采赊销预算合同预览
	 * @param applyId
	 * @param model
	 * @return
	 */
	@RequestMapping("getSpecialChainText/{applyId}")
	public String getSpecialChainText(@PathVariable("applyId") Long applyId,Model model, HttpServletRequest request) {
		MatchContractTextVo textVo = new MatchContractTextVo(applyId,ShiroUtil.getEnterpriseId(),true);
		String contractText = contractTextClient.synthesisMathContractText(textVo);
		model.addAttribute("contractText", contractText);
		String windowId = request.getParameter("windowId");
		model.addAttribute("windowId", windowId);
		return "ctr/contractTextNew";
	}

	/**
	 * 协议采购预览合同详情
	 * @param templateId
	 * @param model
	 * @return
	 */
	@RequestMapping("getAgreementText/{templateId}/{agreementId}")
	public String getAgreementText(@PathVariable("templateId") Long templateId,@PathVariable("agreementId") Long agreementId,Model model, HttpServletRequest request) throws ApplicationException {
		BsContractTemplate template = new BsContractTemplate();
		template.setId(templateId);
		template.setEnterpriseId(ShiroUtil.getEnterpriseId());
		// 获取合同模板
		BsContractTemplate bsContractTemplate = bsContractTemplateClient.findByIdAndEnterpriseId(template);
		ApplyAgreementVirtual agreementVirtual = applyAgreementVirtualClient.getEntity(agreementId);
		if(bsContractTemplate != null && !StringUtils.isEmpty(bsContractTemplate.getContent())){
			CtrContractTextVo tContract = new CtrContractTextVo();
			tContract.setContractNo(agreementVirtual.getStockVirtualNo());// 合同编号
			tContract.setContractTimeStr(DateUtil.format(agreementVirtual.getCreatedDate(), "yyyy年MM月dd日"));// 签订时间
			List<CtrProductVo> productList = getProductList(agreementVirtual);
			String cnMoney = RmbUtil.number2Chinese(agreementVirtual.getTotalAmount());
			tContract.setTotalAmountStr(cnMoney);// // 金额大写
			tContract.setDeliAddr(agreementVirtual.getDeliveryAddr()+agreementVirtual.getContactAddr());// 交货地点
			tContract.setQualityStandardStr(DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT, agreementVirtual.getQualityStandard()));// 质量标准
			tContract.setDeliveryTypeStr( midstreamClient.generateRespStr(agreementVirtual.getOurCompanyName(), DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY,agreementVirtual.getDeliveryType())));// 交货方式
			tContract.setProductList(productList);
			String str = DictUtil.getValue(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME, agreementVirtual.getArrivalTimeExt());
			String deliveryDate = DateUtil.format(agreementVirtual.getDeliveryDate(), "yyyy年MM月dd日") + str;
			tContract.setDeliveryDateStr(deliveryDate);// 交货日期
			tContract.setPayRemaindTime(DateUtil.format(agreementVirtual.getPayFullTime(), "yyyy年MM月dd日"));
			tContract.setPayRateAmount(agreementVirtual.getPayBondAmount());
			tContract.setExtraTerm(agreementVirtual.getExtraTerm());
			if (Objects.nonNull(agreementVirtual.getPayBondTime()) && agreementVirtual.getPayBondAmount().compareTo(BigDecimal.ZERO) > 0){
				tContract.setPayBondTimeStr(DateUtil.format(agreementVirtual.getPayBondTime(), "yyyy年MM月dd日"));
			}
			String deliveryType = agreementVirtual.getDeliveryType();
			String deliveryTypeDictName = DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, deliveryType);
			if (StringUtils.isNotBlank(deliveryTypeDictName)) {
				tContract.setDeliveryType(deliveryTypeDictName);
			} else {
				tContract.setDeliveryType(deliveryType);
			}
			String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,agreementVirtual.getPayType());
			tContract.setPayMode(payType);
			tContract.setCompanyName(agreementVirtual.getCompanyName());
			// 我方抬头
			String key = agreementVirtual.getOurCompanyName();
			String ourCompanyName;
			ourCompanyName = BsCompanyOurUtil.getValue(ShiroUtil.getEnterpriseId(), key);
			if(StringUtils.isBlank(ourCompanyName)){
				BsCompanyDcsx bsCompanyDcsx = bsCompanyDcsxClient.findByCompanyCd(key);
				ourCompanyName = Objects.nonNull(bsCompanyDcsx) ? bsCompanyDcsx.getCompanyName() : "";
			}
			if("中光亿云供应链管理有限公司".equals(ourCompanyName)||"青岛中光亿云供应链管理有限公司".equals(ourCompanyName)||"上海中光亿云供应链管理有限公司".equals(ourCompanyName)){
				tContract.setSignAddress(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_SIGNADDRESS, "zg"));
			}
			if("浙江网塑科技股份有限公司".equals(ourCompanyName)||"网塑（宁波）化工有限公司".equals(ourCompanyName)){
				tContract.setSignAddress(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_SIGNADDRESS, "ws"));
			}
			tContract.setOurCompanyName(ourCompanyName);
			packOurCompany(tContract,ourCompanyName);
			packCompany(tContract,agreementVirtual.getCompanyName());
			try {
				String s = contentMerge(bsContractTemplate.getContent(), tContract);
				model.addAttribute("contractText", s);
			} catch (ApplicationException e) {
				throw new ApplicationException("合同模板合并错误");
			}
		}else {
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		String windowId = request.getParameter("windowId");
		model.addAttribute("windowId", windowId);
		return "ctr/contractTextNew";
	}

	/**
	 * 库存采购预览合同详情
	 * @param templateId
	 * @param model
	 * @return
	 */
	@RequestMapping("getInventoryText/{templateId}/{inventoryId}")
	public String getInventoryText(@PathVariable("templateId") Long templateId,@PathVariable("inventoryId") Long inventoryId,Model model, HttpServletRequest request) throws ApplicationException {
		BsContractTemplate template = new BsContractTemplate();
		template.setId(templateId);
		template.setEnterpriseId(ShiroUtil.getEnterpriseId());
		// 获取合同模板
		BsContractTemplate bsContractTemplate = bsContractTemplateClient.findByIdAndEnterpriseId(template);
		ApplyInventoryVirtual inventoryVirtual = applyInventoryVirtualClient.getEntity(inventoryId);
		if(bsContractTemplate != null && !StringUtils.isEmpty(bsContractTemplate.getContent())){
			CtrContractTextVo tContract = new CtrContractTextVo();
			tContract.setContractNo(inventoryVirtual.getStockVirtualNo());// 合同编号
			tContract.setContractTimeStr(DateUtil.format(inventoryVirtual.getCreatedDate(), "yyyy年MM月dd日"));// 签订时间
			List<CtrProductVo> productList = getProductList(inventoryVirtual);
			String cnMoney = RmbUtil.number2Chinese(inventoryVirtual.getTotalAmount());
			tContract.setTotalAmountStr(cnMoney);// // 金额大写
			tContract.setDeliAddr(inventoryVirtual.getDeliveryAddr()+inventoryVirtual.getContactAddr());// 交货地点
			tContract.setQualityStandardStr(DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT, inventoryVirtual.getQualityStandard()));// 质量标准
			tContract.setDeliveryTypeStr( midstreamClient.generateRespStr(inventoryVirtual.getOurCompanyName(), DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY,inventoryVirtual.getDeliveryType())));// 交货方式
			tContract.setProductList(productList);
			String str = DictUtil.getValue(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME, inventoryVirtual.getArrivalTimeExt());
			String deliveryDate = DateUtil.format(inventoryVirtual.getDeliveryDate(), "yyyy年MM月dd日") + str;
			tContract.setDeliveryDateStr(deliveryDate);// 交货日期
			tContract.setPayRemaindTime(DateUtil.format(inventoryVirtual.getPayFullTime(), "yyyy年MM月dd日"));
			tContract.setPayRateAmount(inventoryVirtual.getPayBondAmount());
			if (Objects.nonNull(inventoryVirtual.getPayBondTime()) && inventoryVirtual.getPayBondAmount().compareTo(BigDecimal.ZERO) > 0){
				tContract.setPayBondTimeStr(DateUtil.format(inventoryVirtual.getPayBondTime(), "yyyy年MM月dd日"));
			}
			String deliveryType = inventoryVirtual.getDeliveryType();
			String deliveryTypeDictName = DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, deliveryType);
			if (StringUtils.isNotBlank(deliveryTypeDictName)) {
				tContract.setDeliveryType(deliveryTypeDictName);
			} else {
				tContract.setDeliveryType(deliveryType);
			}
			String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,inventoryVirtual.getPayType());
			tContract.setPayMode(payType);
			tContract.setCompanyName(inventoryVirtual.getCompanyName());
			// 我方抬头
			String key = inventoryVirtual.getOurCompanyName();
			String ourCompanyName;
			ourCompanyName = BsCompanyOurUtil.getValue(ShiroUtil.getEnterpriseId(), key);
			if(StringUtils.isBlank(ourCompanyName)){
				BsCompanyDcsx bsCompanyDcsx = bsCompanyDcsxClient.findByCompanyCd(key);
				ourCompanyName = Objects.nonNull(bsCompanyDcsx) ? bsCompanyDcsx.getCompanyName() : "";
			}
			if("中光亿云供应链管理有限公司".equals(ourCompanyName)||"青岛中光亿云供应链管理有限公司".equals(ourCompanyName)||"上海中光亿云供应链管理有限公司".equals(ourCompanyName)){
				tContract.setSignAddress(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_SIGNADDRESS, "zg"));
			}
			if("浙江网塑科技股份有限公司".equals(ourCompanyName)||"网塑（宁波）化工有限公司".equals(ourCompanyName)){
				tContract.setSignAddress(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_SIGNADDRESS, "ws"));
			}
			tContract.setOurCompanyName(ourCompanyName);
			packOurCompany(tContract,ourCompanyName);
			packCompany(tContract,inventoryVirtual.getCompanyName());
			try {
				String s = contentMerge(bsContractTemplate.getContent(), tContract);
				model.addAttribute("contractText", s);
			} catch (ApplicationException e) {
				throw new ApplicationException("合同模板合并错误");
			}
		}else {
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		String windowId = request.getParameter("windowId");
		model.addAttribute("windowId", windowId);
		return "ctr/contractTextNew";
	}

	private void packCompany(CtrContractTextVo tContract, String companyName) {
		BsCompany company = bsCompanyClient.findByCompanyName(companyName);
		if (Objects.nonNull(company)) {
			String legalRepresent = company.getLegalRepresent();
			String contactPhone = company.getContactPhone();
			String address = company.getAddress();
			String contactName = company.getContactName();
			String companyFax = company.getCompanyFax();
			tContract.setCompanyPerson(StringUtils.isBlank(legalRepresent) ? "" : legalRepresent);
			tContract.setContactName(StringUtils.isBlank(contactName) ? "" : contactName);
			tContract.setContactPhone(StringUtils.isBlank(contactPhone) ? "" : contactPhone);
			tContract.setContactAddr(StringUtils.isBlank(address) ? "" : address);
			tContract.setCompanyFax(StringUtils.isBlank(companyFax) ? "" : companyFax);
			tContract.setEmail(StringUtils.isBlank(company.getEmail()) ? "" : company.getEmail());
		}
	}

	private String contentMerge(String content,CtrContract entity) throws ApplicationException {
		Configuration  cfg = new Configuration();
		StringWriter sw = new StringWriter();
		try {
			Template t  = new freemarker.template.Template("", new StringReader(content), cfg);
			t.process(entity, sw);
			content = sw.toString();
		}  catch (Exception e) {
			throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
		}
		return content;
	}

	private void packOurCompany(CtrContractTextVo tContract,String companyName) {
		Long enterpriseId = ShiroUtil.getEnterpriseId();
		BsCompanyOur bsCompanyOur = BsCompanyOurUtil.getBsCompanyOur(enterpriseId, companyName);
		if(Objects.nonNull(bsCompanyOur)){
			String addres = bsCompanyOur.getAddress();
			String fax = bsCompanyOur.getCompanyFax();
			String email = bsCompanyOur.getEmail();
			String person = bsCompanyOur.getCompanyPerson();
			String phone = bsCompanyOur.getCompanyPhone();
			tContract.setOurCompanyAddres(StringUtils.isBlank(addres) ? "" : addres);
			tContract.setOurCompanyFax(StringUtils.isBlank(fax) ? "" : fax);
			tContract.setOurCompanyEmail(StringUtils.isBlank(email) ? "" : email);
			tContract.setOurCompanyPerson(StringUtils.isBlank(person) ? "" : person);
			tContract.setMatchUserPhone(StringUtils.isBlank(phone) ? "" : phone);
		}
	}

	private List<CtrProductVo> getProductList(ApplyInventoryVirtual inventoryVirtual) {
		CtrProductVo ctrProductVo = new CtrProductVo();
		ctrProductVo.setProductName(inventoryVirtual.getProductName());// 产品名称
		ctrProductVo.setBrandNumber(inventoryVirtual.getBrandNumber());// 规格型号
		ctrProductVo.setFactoryName(inventoryVirtual.getFactoryName());// 生产厂商
		ctrProductVo.setDealNumber(inventoryVirtual.getDealNumber());// 数量
		ctrProductVo.setDealPrice(inventoryVirtual.getDealPrice());// 含税单价
		ctrProductVo.setTotalPrice(inventoryVirtual.getTotalAmount());// 合计
		ctrProductVo.setWrapSpecs(DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT,inventoryVirtual.getWrapSpecs()));
		ctrProductVo.setProductName(inventoryVirtual.getProductName());
		ctrProductVo.setBrandNumber(inventoryVirtual.getBrandNumber());
		ctrProductVo.setFactoryName(inventoryVirtual.getFactoryName());
		ctrProductVo.setDealNumber(inventoryVirtual.getDealNumber());
		ctrProductVo.setDealPrice(inventoryVirtual.getDealPrice());
		ctrProductVo.setTotalPrice(inventoryVirtual.getTotalAmount());
		List<CtrProductVo> list = new ArrayList<>();
		list.add(ctrProductVo);
		return list;
	}

	private List<CtrProductVo> getProductList(ApplyAgreementVirtual agreementVirtual) {
		CtrProductVo ctrProductVo = new CtrProductVo();
		ctrProductVo.setProductName(agreementVirtual.getProductName());// 产品名称
		ctrProductVo.setBrandNumber(agreementVirtual.getBrandNumber());// 规格型号
		ctrProductVo.setFactoryName(agreementVirtual.getFactoryName());// 生产厂商
		ctrProductVo.setDealNumber(agreementVirtual.getDealNumber());// 数量
		ctrProductVo.setDealPrice(agreementVirtual.getDealPrice());// 含税单价
		ctrProductVo.setTotalPrice(agreementVirtual.getTotalAmount());// 合计
		ctrProductVo.setWrapSpecs(DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT,agreementVirtual.getWrapSpecs()));
		ctrProductVo.setProductName(agreementVirtual.getProductName());
		ctrProductVo.setBrandNumber(agreementVirtual.getBrandNumber());
		ctrProductVo.setFactoryName(agreementVirtual.getFactoryName());
		ctrProductVo.setDealNumber(agreementVirtual.getDealNumber());
		ctrProductVo.setDealPrice(agreementVirtual.getDealPrice());
		ctrProductVo.setTotalPrice(agreementVirtual.getTotalAmount());
		List<CtrProductVo> list = new ArrayList<>();
		list.add(ctrProductVo);
		return list;
	}

	/**
	 * 代采赊销合同模板
	 *
	 * @param model
	 * @return
	 */
	@RequestMapping("getDcsxTemplateContract")
	public String getDcsxTemplateContract(Model model, HttpServletRequest request) throws ApplicationException {
		
		String rates = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_TAX_RATES, BasConstants.DICT_TYPE_TAX_RATES_SL);
		BigDecimal taxRates = BigDecimal.ONE.add(new BigDecimal(rates));
		DcContractText tContract = new DcContractText();
		// 合同编号
		String contractNo = request.getParameter("contractNo");
		// 我方抬头
		String ourCompanyName = request.getParameter("ourCompanyName");
		// 代采购方
		String companyName = request.getParameter("companyName");
		BsCompany bsCompany = bsCompanyClient.findByCompanyName(companyName);
		if(Objects.nonNull(bsCompany)){
			tContract.setAddress(bsCompany.getAddress());
		}
		tContract.setContractNo(contractNo);
		// 签订时间
		String contractTimeStr = request.getParameter("contractTimeStr");
		if (!StrUtil.isBlank(contractTimeStr)) {
			tContract.setContractTimeStr(DateUtil.format(DateUtil.parseDate(contractTimeStr), "yyyy年MM月dd日"));
		}
		// 货品名称
		String productName = request.getParameter("productName");
		// 货品名称
		String ourBankName = request.getParameter("ourBankName");
		// 货品名称
		String ourBankAccount = request.getParameter("ourBankAccount");
		tContract.setProductName(productName);
		// 规格型号
		String brandNumber = request.getParameter("brandNumber");
		tContract.setBrandNumber(brandNumber);
		// 厂商
		String factoryName = request.getParameter("factoryName");
		tContract.setFactoryName(factoryName);
		// 包装规格
		String wrapSpecs = request.getParameter("wrapSpecs");
		tContract.setWrapSpecs(wrapSpecs);
		// 数量
		String dealNumber = request.getParameter("dealNumber");
		tContract.setDealNumber(dealNumber);
		// 单价
		String dealPrice = request.getParameter("dealPrice");
		tContract.setDealPrice(dealPrice);
		BigDecimal dealPriceNoTax = new BigDecimal(dealPrice).divide(taxRates, 2, RoundingMode.HALF_UP);
		tContract.setDealPriceNoTax(dealPriceNoTax);
		BigDecimal totalPriceNoTax = dealPriceNoTax.multiply(new BigDecimal(dealNumber));
		tContract.setTotalPriceNoTax(totalPriceNoTax);
		// 合计
		String totalPrice = request.getParameter("totalAmount");
		tContract.setTotalPrice(totalPrice);
		// 金额大写
		String cnMoney = RmbUtil.number2Chinese(new BigDecimal(totalPrice));
		tContract.setCnMoney(cnMoney);

		// 交货地点
		String deliAddr = request.getParameter("deliveryAddr");
		tContract.setDeliAddr(deliAddr);


		// 交货日期
		String deliveryDateStr = request.getParameter("deliveryDate");
		tContract.setDeliveryDateStr(deliveryDateStr);
		if (!StrUtil.isBlank(deliveryDateStr)) {
			tContract.setDeliveryDateStr(DateUtil.format(DateUtil.parseDate(deliveryDateStr), "yyyy年MM月dd日"));
		}
		// 运输方式
		String deliveryType = request.getParameter("deliveryType");
		String deliveryTypeDictName = DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, deliveryType);
		if (StringUtils.isNotBlank(deliveryTypeDictName)) {
			tContract.setDeliveryType(deliveryTypeDictName);
		} else {
			tContract.setDeliveryType(deliveryType);
		}
		if (StringUtils.equals("需方自提", deliveryType) || StringUtils.equals(BasConstants.DICT_TYPE_BUYDELIVERY_Z, deliveryType)) {
			tContract.setTransAmountRemark("如需方自提，提货费用自理；若产生入库费用，由需方承担。仓储费自交割日期免三天，超期仓储费和损耗由需方承担。");
		} else {
			tContract.setTransAmountRemark("如供方配送，费用由供方承担。");
		}
		// 付全款日期
		String payFullTime = request.getParameter("lastPayDate");
		if (!StrUtil.isBlank(payFullTime)) {
			tContract.setPayFullTimeStr(DateUtil.format(DateUtil.parseDate(payFullTime), "yyyy年MM月dd日"));
		}
		setOurCompanyParam(tContract, ourCompanyName);
		setCompanyParam(tContract, companyName);

		ApplyCtrDCSX byContractNo = applyCtrDcsxClinent.findByContractNo(contractNo);
		// 奥顺宇特殊处理
		if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, companyName)) {
			BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(ShiroUtil.getEnterpriseId());
			if (Objects.nonNull(specialBank)) {
				tContract.setOurCompanyBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
				tContract.setOurCompanyBankNo(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());

			}
			List<CtrContract> contractList = ctrContractClient.findByApproveId(byContractNo.getApproveId());
			if (CollectionUtils.isNotEmpty(contractList)){
				CtrContract sellContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_STATUS_S, c.getContractType())).findFirst().orElse(null);
				if (Objects.nonNull(sellContract) && StringUtils.isNotBlank(sellContract.getBusinessTypeDcsx()) && sellContract.getBusinessTypeDcsx().contains("HDFK")){
					BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
					companyOurSearchVo.setCompanyName(companyName);
					BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
					if (Objects.nonNull(companyOur)){
						tContract.setCompanyBankName(StringUtils.isBlank(companyOur.getCompanyBankName2()) ? "" : companyOur.getCompanyBankName2());
						tContract.setCompanyBankNo(StringUtils.isBlank(companyOur.getCompanyCardId2()) ? "" : companyOur.getCompanyCardId2());
					}
				}
			}
		}
		if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName)) {
			BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(ShiroUtil.getEnterpriseId());
			if (Objects.nonNull(specialBank)) {
				tContract.setCompanyBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
				tContract.setCompanyBankNo(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());
			}
			List<CtrContract> contractList = ctrContractClient.findByApproveId(byContractNo.getApproveId());
			if (CollectionUtils.isNotEmpty(contractList)){
				CtrContract sellContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_STATUS_S, c.getContractType())).findFirst().orElse(null);
				if (Objects.nonNull(sellContract) && StringUtils.isNotBlank(sellContract.getBusinessTypeDcsx()) && sellContract.getBusinessTypeDcsx().contains("HDFK")){
					BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
					companyOurSearchVo.setCompanyName(ourCompanyName);
					BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
					if (Objects.nonNull(companyOur)){
						tContract.setOurCompanyBankName(StringUtils.isBlank(companyOur.getCompanyBankName2()) ? "" : companyOur.getCompanyBankName2());
						tContract.setOurCompanyBankNo(StringUtils.isBlank(companyOur.getCompanyCardId2()) ? "" : companyOur.getCompanyCardId2());
					}
				}
			}
		}

		tContract.setOurCompanyName(ourCompanyName);
		tContract.setCompanyName(companyName);

		PmApprove byApproveNo = pmApproveClient.findByApproveNo(byContractNo.getBudgetNo());
		List<CtrContract> byApproveId = ctrContractClient.findByApproveId(byApproveNo.getId());
		ApplyMatch applyMatch = applyMatchClient.findByApproveId(byApproveNo.getId());
		List<CtrContract> s1 = byApproveId.stream().filter(s -> s.getContractType().equals("B")).collect(Collectors.toList());
		List<CtrContract> s2 = byApproveId.stream().filter(s -> s.getContractType().equals("S")).collect(Collectors.toList());
		CtrContract buyContract = s1.get(0);
		CtrContract sellContract = s2.get(0);
		BigDecimal bondAmount = buyContract.getBondAmount();
		if(bondAmount.compareTo(BigDecimal.ZERO)>0){
			//付定金
			tContract.setClause("需要于"+DateUtil.format(buyContract.getPayBondTime(), "yyyy年MM月dd日")+"支付定金"+bondAmount+"元，交货前付清全款");
		}else{
			//付全款
			tContract.setClause("需方于合同签订之日起一周内支付货款");
		}
		
		String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,sellContract.getPayType());
		if(StringUtils.isBlank(payType)) {
			tContract.setPayMode("电汇");
		} else {
			tContract.setPayMode(payType);
		}
		tContract.setQualityStandardStr(DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT, sellContract.getQualityStandard()));
		tContract.setContractModel(sellContract.getContractModel());

		String buyPayFullTimeStr = request.getParameter("buyPayFullTime");
		if (!StrUtil.isBlank(buyPayFullTimeStr)) {
			tContract.setPayRemaindTime(DateUtil.format(DateUtil.parseDate(buyPayFullTimeStr), "yyyy年MM月dd日"));
		} else {
			tContract.setPayRemaindTime(DateUtil.format(buyContract.getPayFullTime(), "yyyy年MM月dd日"));
		}
		if (Objects.nonNull(buyContract.getPayBondTime())){
			tContract.setBuyPayBondDate(DateUtil.format(buyContract.getPayBondTime(), "yyyy年MM月dd日"));
		}
		tContract.setTotalPriceNum(new BigDecimal(totalPrice));
		tContract.setBuyBondAmount(buyContract.getBondAmount());
		tContract.setBuyPayFullDate(DateUtil.format(buyContract.getPayFullTime(), "yyyy年MM月dd日"));
		tContract.setSellPayFullDate(DateUtil.format(sellContract.getPayFullTime(), "yyyy年MM月dd日"));
		tContract.setExtraTerm(StringUtils.isNotBlank(sellContract.getExtraTerm())?sellContract.getExtraTerm():"无");
		tContract.setBondAmount(sellContract.getBondAmount());
		tContract.setPayRateAmount(sellContract.getBondAmount());
		
		//采购合同模板
		BsContractTemplate template = new BsContractTemplate();
		template.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		if (StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, ourCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, companyName)) {
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_SDNH);
		} else if (StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, companyName)) {
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_SUGX);
			String sellDeliveryDateStr = request.getParameter("sellDeliveryDate");
			if (!StrUtil.isBlank(sellDeliveryDateStr)) {
				tContract.setDeliveryDateStr(DateUtil.format(DateUtil.parseDate(sellDeliveryDateStr), "yyyy年MM月dd日"));
			} 
		} else {
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT);
		}
		BsContractTemplate bsContractTemplate = bsContractTemplateClient.findByTemplateTagAndEnterpriseId(template);
		if (bsContractTemplate != null && !StringUtils.isEmpty(bsContractTemplate.getContent())) {
			try {
				tContract = dealWithSpecialBank(tContract, byContractNo);
				tContract = contractTextClient.dealWithExtraBank(new ExtraBankTextVo(tContract, applyMatch, byContractNo, "C"));
				if (StringUtils.isNotBlank(ourBankName)){
					tContract.setOurCompanyBankName(ourBankName);
				}
				if (StringUtils.isNotBlank(ourBankAccount)){
					tContract.setOurCompanyBankNo(ourBankAccount);
				}
				String s = contentMerge(bsContractTemplate.getContent(), tContract);
				model.addAttribute("contractText", s);
			} catch (ApplicationException e) {
				throw new ApplicationException("合同模板合并错误");
			}
		} else {
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		return "ctr/contractText";
	}

	/**
	 * 代采赊销合同模板
	 *
	 * @param model
	 * @return
	 */
	@RequestMapping("getDcsxTemplateContractNew")
	public String getDcsxTemplateContractNew(Model model, HttpServletRequest request) throws ApplicationException {

		String rates = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_TAX_RATES, BasConstants.DICT_TYPE_TAX_RATES_SL);
		BigDecimal taxRates = BigDecimal.ONE.add(new BigDecimal(rates));
		DcContractText tContract = new DcContractText();
		// 合同编号
		String contractNo = request.getParameter("contractNo");
		// 我方抬头
		String ourCompanyName = request.getParameter("ourCompanyName");
		// 代采购方
		String companyName = request.getParameter("companyName");
		BsCompany bsCompany = bsCompanyClient.findByCompanyName(companyName);
		if(Objects.nonNull(bsCompany)){
			tContract.setAddress(bsCompany.getAddress());
		}
		tContract.setContractNo(contractNo);
		// 签订时间
		String contractTimeStr = request.getParameter("contractTimeStr");
		if (!StrUtil.isBlank(contractTimeStr)) {
			tContract.setContractTimeStr(DateUtil.format(DateUtil.parseDate(contractTimeStr), "yyyy年MM月dd日"));
		}
		// 货品名称
		String productName = request.getParameter("productName");
		// 货品名称
		String ourBankName = request.getParameter("ourBankName");
		// 货品名称
		String ourBankAccount = request.getParameter("ourBankAccount");
		tContract.setProductName(productName);
		// 规格型号
		String brandNumber = request.getParameter("brandNumber");
		tContract.setBrandNumber(brandNumber);
		// 厂商
		String factoryName = request.getParameter("factoryName");
		tContract.setFactoryName(factoryName);
		// 包装规格
		String wrapSpecs = request.getParameter("wrapSpecs");
		tContract.setWrapSpecs(wrapSpecs);
		// 数量
		String dealNumber = request.getParameter("dealNumber");
		tContract.setDealNumber(dealNumber);
		// 单价
		String dealPrice = request.getParameter("dealPrice");
		tContract.setDealPrice(dealPrice);
		BigDecimal dealPriceNoTax = new BigDecimal(dealPrice).divide(taxRates, 2, RoundingMode.HALF_UP);
		tContract.setDealPriceNoTax(dealPriceNoTax);
		BigDecimal totalPriceNoTax = dealPriceNoTax.multiply(new BigDecimal(dealNumber));
		tContract.setTotalPriceNoTax(totalPriceNoTax);
		// 合计
		String totalPrice = request.getParameter("totalAmount");
		tContract.setTotalPrice(totalPrice);
		// 金额大写
		String cnMoney = RmbUtil.number2Chinese(new BigDecimal(totalPrice));
		tContract.setCnMoney(cnMoney);

		// 交货地点
		String deliAddr = request.getParameter("deliveryAddr");
		tContract.setDeliAddr(deliAddr);


		// 交货日期
		String deliveryDateStr = request.getParameter("deliveryDate");
		tContract.setDeliveryDateStr(deliveryDateStr);
		if (!StrUtil.isBlank(deliveryDateStr)) {
			tContract.setDeliveryDateStr(DateUtil.format(DateUtil.parseDate(deliveryDateStr), "yyyy年MM月dd日"));
		}
		// 运输方式
		String deliveryType = request.getParameter("deliveryType");
		String deliveryTypeDictName = DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, deliveryType);
		if (StringUtils.isNotBlank(deliveryTypeDictName)) {
			tContract.setDeliveryType(deliveryTypeDictName);
		} else {
			tContract.setDeliveryType(deliveryType);
		}
		if (StringUtils.equals("需方自提", deliveryType) || StringUtils.equals(BasConstants.DICT_TYPE_BUYDELIVERY_Z, deliveryType)) {
			tContract.setTransAmountRemark("如需方自提，提货费用自理；若产生入库费用，由需方承担。仓储费自交割日期免三天，超期仓储费和损耗由需方承担。");
		} else {
			tContract.setTransAmountRemark("如供方配送，费用由供方承担。");
		}
		// 付全款日期
		String payFullTime = request.getParameter("lastPayDate");
		if (!StrUtil.isBlank(payFullTime)) {
			tContract.setPayFullTimeStr(DateUtil.format(DateUtil.parseDate(payFullTime), "yyyy年MM月dd日"));
		}
		setOurCompanyParam(tContract, ourCompanyName);
		setCompanyParam(tContract, companyName);

		ApplyCtrDCSX byContractNo = applyCtrDcsxClinent.findByContractNo(contractNo);
		// 奥顺宇特殊处理
		if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, companyName)) {
			BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(ShiroUtil.getEnterpriseId());
			if (Objects.nonNull(specialBank)) {
				tContract.setOurCompanyBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
				tContract.setOurCompanyBankNo(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());

			}
			List<CtrContract> contractList = ctrContractClient.findByApproveId(byContractNo.getApproveId());
			if (CollectionUtils.isNotEmpty(contractList)){
				CtrContract sellContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_STATUS_S, c.getContractType())).findFirst().orElse(null);
				if (Objects.nonNull(sellContract) && StringUtils.isNotBlank(sellContract.getBusinessTypeDcsx()) && sellContract.getBusinessTypeDcsx().contains("HDFK")){
					BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
					companyOurSearchVo.setCompanyName(companyName);
					BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
					if (Objects.nonNull(companyOur)){
						tContract.setCompanyBankName(StringUtils.isBlank(companyOur.getCompanyBankName2()) ? "" : companyOur.getCompanyBankName2());
						tContract.setCompanyBankNo(StringUtils.isBlank(companyOur.getCompanyCardId2()) ? "" : companyOur.getCompanyCardId2());
					}
				}
			}
		}
		if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName)) {
			BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(ShiroUtil.getEnterpriseId());
			if (Objects.nonNull(specialBank)) {
				tContract.setCompanyBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
				tContract.setCompanyBankNo(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());
			}
			List<CtrContract> contractList = ctrContractClient.findByApproveId(byContractNo.getApproveId());
			if (CollectionUtils.isNotEmpty(contractList)){
				CtrContract sellContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_STATUS_S, c.getContractType())).findFirst().orElse(null);
				if (Objects.nonNull(sellContract) && StringUtils.isNotBlank(sellContract.getBusinessTypeDcsx()) && sellContract.getBusinessTypeDcsx().contains("HDFK")){
					BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
					companyOurSearchVo.setCompanyName(ourCompanyName);
					BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
					if (Objects.nonNull(companyOur)){
						tContract.setOurCompanyBankName(StringUtils.isBlank(companyOur.getCompanyBankName2()) ? "" : companyOur.getCompanyBankName2());
						tContract.setOurCompanyBankNo(StringUtils.isBlank(companyOur.getCompanyCardId2()) ? "" : companyOur.getCompanyCardId2());
					}
				}
			}
		}

		if (StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, ourCompanyName)
				&& StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, companyName)){
			tContract.setOurCompanyBankName("上海银行股份有限公司杨浦支行");
			tContract.setOurCompanyBankNo("03006283844");
		}

		if (StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName)
				&& StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, companyName)){
			tContract.setCompanyBankName("宁波银行阳明支行");
			tContract.setCompanyBankNo("61040122000182065");
		}

		tContract.setOurCompanyName(ourCompanyName);
		tContract.setCompanyName(companyName);

		PmApprove byApproveNo = pmApproveClient.findByApproveNo(byContractNo.getBudgetNo());
		List<CtrContract> byApproveId = ctrContractClient.findByApproveId(byApproveNo.getId());
		ApplyMatch applyMatch = applyMatchClient.findByApproveId(byApproveNo.getId());
		List<CtrContract> s1 = byApproveId.stream().filter(s -> s.getContractType().equals("B")).collect(Collectors.toList());
		List<CtrContract> s2 = byApproveId.stream().filter(s -> s.getContractType().equals("S")).collect(Collectors.toList());
		CtrContract buyContract = s1.get(0);
		CtrContract sellContract = s2.get(0);
		BigDecimal bondAmount = buyContract.getBondAmount();
		if(bondAmount.compareTo(BigDecimal.ZERO)>0){
			//付定金
			tContract.setClause("需要于"+DateUtil.format(buyContract.getPayBondTime(), "yyyy年MM月dd日")+"支付定金"+bondAmount+"元，交货前付清全款");
		}else{
			//付全款
			tContract.setClause("需方于合同签订之日起一周内支付货款");
		}

		String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,sellContract.getPayType());
		if(StringUtils.isBlank(payType)) {
			tContract.setPayMode("电汇");
		} else {
			tContract.setPayMode(payType);
		}
		tContract.setQualityStandardStr(DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT, sellContract.getQualityStandard()));
		tContract.setContractModel(sellContract.getContractModel());

		String buyPayFullTimeStr = request.getParameter("buyPayFullTime");
		if (!StrUtil.isBlank(buyPayFullTimeStr)) {
			tContract.setPayRemaindTime(DateUtil.format(DateUtil.parseDate(buyPayFullTimeStr), "yyyy年MM月dd日"));
		} else {
			tContract.setPayRemaindTime(DateUtil.format(buyContract.getPayFullTime(), "yyyy年MM月dd日"));
		}
		if (Objects.nonNull(buyContract.getPayBondTime())){
			tContract.setBuyPayBondDate(DateUtil.format(buyContract.getPayBondTime(), "yyyy年MM月dd日"));
		}
		tContract.setTotalPriceNum(new BigDecimal(totalPrice));
		tContract.setBuyBondAmount(buyContract.getBondAmount());
		tContract.setBuyPayFullDate(DateUtil.format(buyContract.getPayFullTime(), "yyyy年MM月dd日"));
		tContract.setSellPayFullDate(DateUtil.format(sellContract.getPayFullTime(), "yyyy年MM月dd日"));
		tContract.setExtraTerm(StringUtils.isNotBlank(sellContract.getExtraTerm())?sellContract.getExtraTerm():"无");
		tContract.setBondAmount(sellContract.getBondAmount());
		tContract.setPayRateAmount(sellContract.getBondAmount());

		if (contractNo.contains("KCX")){
			tContract.setBuyBondAmount(null);
			tContract.setBuyPayBondDate(null);
			tContract.setBuyPayFullDate(DateUtil.format(sellContract.getPayFullTime(), "yyyy年MM月dd日"));
		}

		//采购合同模板
		BsContractTemplate template = new BsContractTemplate();
		template.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		tContract.setCreditDays(sellContract.getCreditCycle().intValue());
		if (sellContract.getCompanyName().contains("远东") && (!byContractNo.getCompanyName().contains("奥顺宇")) && !byContractNo.getOurCompanyName().contains("奥顺宇")){
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_YD);
		}else if (StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, ourCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, companyName)) {
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_SDNH);
		} else if (StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, companyName)) {
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_SUGX);
			String sellDeliveryDateStr = request.getParameter("sellDeliveryDate");
			if (!StrUtil.isBlank(sellDeliveryDateStr)) {
				tContract.setDeliveryDateStr(DateUtil.format(DateUtil.parseDate(sellDeliveryDateStr), "yyyy年MM月dd日"));
			}
		} else {
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT);
		}
		BsContractTemplate bsContractTemplate = bsContractTemplateClient.findByTemplateTagAndEnterpriseId(template);
		if (bsContractTemplate != null && !StringUtils.isEmpty(bsContractTemplate.getContent())) {
			try {
				tContract = dealWithSpecialBank(tContract, byContractNo);
				tContract = contractTextClient.dealWithExtraBank(new ExtraBankTextVo(tContract, applyMatch, byContractNo, "C"));
				if (StringUtils.isNotBlank(ourBankName)){
					tContract.setOurCompanyBankName(ourBankName);
				}
				if (StringUtils.isNotBlank(ourBankAccount)){
					tContract.setOurCompanyBankNo(ourBankAccount);
				}
				String s = contentMerge(bsContractTemplate.getContent(), tContract);
				model.addAttribute("contractText", s);
			} catch (ApplicationException e) {
				throw new ApplicationException("合同模板合并错误");
			}
		} else {
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		String windowId = request.getParameter("windowId");
		model.addAttribute("windowId", windowId);
		return "ctr/contractTextNew";
	}

	/**
	 * 代采赊销审批中合同预览
	 *
	 * @param model
	 * @return
	 */
	@RequestMapping("getDcsxTemplateContractApproveIng")
	public String getDcsxTemplateContractApproveIng(Model model, HttpServletRequest request) throws ApplicationException {

		String rates = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_TAX_RATES, BasConstants.DICT_TYPE_TAX_RATES_SL);
		BigDecimal taxRates = BigDecimal.ONE.add(new BigDecimal(rates));
		DcContractText tContract = new DcContractText();
		// 合同编号
		String contractNo = request.getParameter("contractNo");
		// 我方抬头
		String ourCompanyName = request.getParameter("ourCompanyName");
		// 代采购方
		String companyName = request.getParameter("companyName");
		BsCompany bsCompany = bsCompanyClient.findByCompanyName(companyName);
		if(Objects.nonNull(bsCompany)){
			tContract.setAddress(bsCompany.getAddress());
		}
		tContract.setContractNo(contractNo);
		// 签订时间
		String contractTimeStr = request.getParameter("contractTimeStr");
		if (!StrUtil.isBlank(contractTimeStr)) {
			tContract.setContractTimeStr(DateUtil.format(DateUtil.parseDate(contractTimeStr), "yyyy年MM月dd日"));
		}
		// 货品名称
		String productName = request.getParameter("productName");
		tContract.setProductName(productName);
		// 规格型号
		String brandNumber = request.getParameter("brandNumber");
		tContract.setBrandNumber(brandNumber);
		// 厂商
		String factoryName = request.getParameter("factoryName");
		tContract.setFactoryName(factoryName);
		// 包装规格
		String wrapSpecs = request.getParameter("wrapSpecs");
		tContract.setWrapSpecs(wrapSpecs);
		// 数量
		String dealNumber = request.getParameter("dealNumber");
		tContract.setDealNumber(dealNumber);
		
		String approveId = request.getParameter("approveId");
		ApplyCtrDCSX byContractNo = applyChargeSalesClient.parseCtrDcsxByApproveId(Long.valueOf(approveId));

		// 单价
		String dealPrice = byContractNo.getDealPrice().setScale(2, RoundingMode.HALF_UP).toString();
		tContract.setDealPrice(dealPrice);
		BigDecimal dealPriceNoTax = new BigDecimal(dealPrice).divide(taxRates, 2, RoundingMode.HALF_UP);
		tContract.setDealPriceNoTax(dealPriceNoTax);
		BigDecimal totalPriceNoTax = dealPriceNoTax.multiply(new BigDecimal(dealNumber));
		tContract.setTotalPriceNoTax(totalPriceNoTax);
		// 合计
		String totalPrice = byContractNo.getTotalAmount().setScale(2, RoundingMode.HALF_UP).toString();
		tContract.setTotalPrice(totalPrice);
		// 金额大写
		String cnMoney = RmbUtil.number2Chinese(new BigDecimal(totalPrice));
		tContract.setCnMoney(cnMoney);

		// 交货地点
		String deliAddr = request.getParameter("deliveryAddr");
		tContract.setDeliAddr(deliAddr);


		// 交货日期
		String deliveryDateStr = request.getParameter("deliveryDate");
		Date deliveryDateTo = byContractNo.getDeliveryDateTo();
		tContract.setDeliveryDateStr(deliveryDateStr);
		if (deliveryDateTo != null) {
			tContract.setDeliveryDateStr(DateUtil.format(deliveryDateTo, "yyyy年MM月dd日"));
		}
		// 运输方式
		String deliveryType = request.getParameter("deliveryType");
		String deliveryTypeDictName = DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, deliveryType);
		if (StringUtils.isNotBlank(deliveryTypeDictName)) {
			tContract.setDeliveryType(deliveryTypeDictName);
		} else {
			tContract.setDeliveryType(deliveryType);
		}
		if (StringUtils.equals("需方自提", deliveryType) || StringUtils.equals(BasConstants.DICT_TYPE_BUYDELIVERY_Z, deliveryType)) {
			tContract.setTransAmountRemark("如需方自提，提货费用自理；若产生入库费用，由需方承担。仓储费自交割日期免三天，超期仓储费和损耗由需方承担。");
		} else {
			tContract.setTransAmountRemark("如供方配送，费用由供方承担。");
		}
		// 付全款日期
		String payFullTime = request.getParameter("lastPayDate");
		if (!StrUtil.isBlank(payFullTime)) {
			tContract.setPayFullTimeStr(DateUtil.format(DateUtil.parseDate(payFullTime), "yyyy年MM月dd日"));
		}
		setOurCompanyParam(tContract, ourCompanyName);
		setCompanyParam(tContract, companyName);

		ApplyMatch applyMatch = applyMatchClient.findByApproveId(byContractNo.getApproveId());
		
		// 奥顺宇特殊处理
		if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, companyName)) {
			BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(ShiroUtil.getEnterpriseId());
			if (Objects.nonNull(specialBank)) {
				tContract.setOurCompanyBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
				tContract.setOurCompanyBankNo(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());

			}
			if(Objects.nonNull(applyMatch) && StringUtils.isNotBlank(applyMatch.getContractModel()) && applyMatch.getContractModel().contains("HDFK")) {
				BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
				companyOurSearchVo.setCompanyName(companyName);
				BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
				if (Objects.nonNull(companyOur)){
					tContract.setCompanyBankName(StringUtils.isBlank(companyOur.getCompanyBankName2()) ? "" : companyOur.getCompanyBankName2());
					tContract.setCompanyBankNo(StringUtils.isBlank(companyOur.getCompanyCardId2()) ? "" : companyOur.getCompanyCardId2());
				}
			}
		}
		if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName)) {
			BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(ShiroUtil.getEnterpriseId());
			if (Objects.nonNull(specialBank)) {
				tContract.setCompanyBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
				tContract.setCompanyBankNo(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());
			}
			if(Objects.nonNull(applyMatch) && StringUtils.isNotBlank(applyMatch.getContractModel()) && applyMatch.getContractModel().contains("HDFK")) {
				BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
				companyOurSearchVo.setCompanyName(ourCompanyName);
				BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
				if (Objects.nonNull(companyOur)){
					tContract.setOurCompanyBankName(StringUtils.isBlank(companyOur.getCompanyBankName2()) ? "" : companyOur.getCompanyBankName2());
					tContract.setOurCompanyBankNo(StringUtils.isBlank(companyOur.getCompanyCardId2()) ? "" : companyOur.getCompanyCardId2());
				}
			}
		}

		if (StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, ourCompanyName)
				&& StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, companyName)){
			tContract.setOurCompanyBankName("上海银行股份有限公司杨浦支行");
			tContract.setOurCompanyBankNo("03006283844");
		}

		if (StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName)
				&& StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, companyName)){
			tContract.setCompanyBankName("宁波银行阳明支行");
			tContract.setCompanyBankNo("61040122000182065");
		}

		tContract.setOurCompanyName(ourCompanyName);
		tContract.setCompanyName(companyName);

		List<ApplyMatchDetail> byApplyMatchId = applyMatchDetailClient.findByApproveId(byContractNo.getApproveId());
		List<ApplyMatchDetail> s1 = byApplyMatchId.stream().filter(s -> s.getContractType().equals("B")).collect(Collectors.toList());
		List<ApplyMatchDetail> s2 = byApplyMatchId.stream().filter(s -> s.getContractType().equals("S")).collect(Collectors.toList());
		ApplyMatchDetail buyContract = s1.get(0);
		ApplyMatchDetail sellContract = s2.get(0);
		BigDecimal bondAmount = buyContract.getPayBondAmount();
		if(bondAmount.compareTo(BigDecimal.ZERO)>0){
			//付定金
			tContract.setClause("需要于"+DateUtil.format(buyContract.getPayBondTime(), "yyyy年MM月dd日")+"支付定金"+bondAmount+"元，交货前付清全款");
		}else{
			//付全款
			tContract.setClause("需方于合同签订之日起一周内支付货款");
		}

		String payType = DictUtil.getValue(BasConstants.DICT_APPLY_PAYMODE,sellContract.getPayType());
		if(StringUtils.isBlank(payType)) {
			tContract.setPayMode("电汇");
		} else {
			tContract.setPayMode(payType);
		}
		tContract.setQualityStandardStr(DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT, sellContract.getQualityStandard()));

		String buyPayFullTimeStr = request.getParameter("buyPayFullTime");
		if (!StrUtil.isBlank(buyPayFullTimeStr)) {
			tContract.setPayRemaindTime(DateUtil.format(DateUtil.parseDate(buyPayFullTimeStr), "yyyy年MM月dd日"));
		} else {
			tContract.setPayRemaindTime(DateUtil.format(buyContract.getPayFullTime(), "yyyy年MM月dd日"));
		}
		if (Objects.nonNull(buyContract.getPayBondTime())){
			tContract.setBuyPayBondDate(DateUtil.format(buyContract.getPayBondTime(), "yyyy年MM月dd日"));
		}
		tContract.setTotalPriceNum(new BigDecimal(totalPrice));
		tContract.setBuyBondAmount(buyContract.getPayBondAmount());
		tContract.setBuyPayFullDate(DateUtil.format(buyContract.getPayFullTime(), "yyyy年MM月dd日"));
		tContract.setSellPayFullDate(DateUtil.format(sellContract.getReceiveFullTime(), "yyyy年MM月dd日"));
		tContract.setExtraTerm(StringUtils.isNotBlank(sellContract.getExtraTerm())?sellContract.getExtraTerm():"无");
		if (contractNo.contains("KCX")){
			tContract.setBuyBondAmount(null);
			tContract.setBuyPayBondDate(null);
			tContract.setBuyPayFullDate(DateUtil.format(sellContract.getReceiveFullTime(), "yyyy年MM月dd日"));
		}
		if (StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, ourCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, companyName)){
			tContract.setBuyPayFullDate(DateUtil.format(sellContract.getReceiveFullTime(), "yyyy年MM月dd日"));
			tContract.setSigningAddr("上海市金山区");
		}
		//采购合同模板
		BsContractTemplate template = new BsContractTemplate();
		template.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		tContract.setCreditDays(sellContract.getCreditDays());
		if (sellContract.getCompanyName().contains("远东") && (!companyName.contains("奥顺宇")) && !ourCompanyName.contains("奥顺宇")){
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_YD);
		}else if (StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, ourCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, companyName)) {
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_SDNH);
		} else if (StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, companyName)) {
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT_SUGX);
			String sellDeliveryDateStr = request.getParameter("sellDeliveryDate");
			if (!StrUtil.isBlank(sellDeliveryDateStr)) {
				tContract.setDeliveryDateStr(DateUtil.format(DateUtil.parseDate(sellDeliveryDateStr), "yyyy年MM月dd日"));
			}
		} else {
			template.setTemplateTag(BasConstants.TEMPLATETAG_DCSX_CONTRACT);
		}
		BsContractTemplate bsContractTemplate = bsContractTemplateClient.findByTemplateTagAndEnterpriseId(template);
		if (bsContractTemplate != null && !StringUtils.isEmpty(bsContractTemplate.getContent())) {
			try {
				tContract = dealWithSpecialBank(tContract, byContractNo);
				tContract = contractTextClient.dealWithExtraBank(new ExtraBankTextVo(tContract, applyMatch, byContractNo, "C"));
				String s = contentMerge(bsContractTemplate.getContent(), tContract);
				model.addAttribute("contractText", s);
			} catch (ApplicationException e) {
				throw new ApplicationException("合同模板合并错误");
			}
		} else {
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		String windowId = request.getParameter("windowId");
		model.addAttribute("windowId", windowId);
		return "ctr/contractTextNew";
	}

	/**
	 * 目前奥顺宇赊销，如果青岛中光抬头，普通赊销——2400账户；货到付款——1034账户
	 * 目前奥顺宇赊销，如果网塑宁波抬头，暂时还是全部用1034账户
	 * @param vo
	 * @param entity
	 * @return
	 */
	private DcContractText dealWithSpecialBank(DcContractText vo, ApplyCtrDCSX entity) {
		ApplyMatch applyMatch = applyMatchClient.findByApproveId(entity.getApproveId());
		String ourCompanyName = applyMatch.getOurCompanyName();
		String sellOurCompanyName = applyMatch.getSellOurCompanyName();
		String businessTypeDcsx = applyMatch.getContractModel();
		String bank1034 =  "636651034";
		String bank2400 = "637632400";
		// 青岛奥顺宇-青岛中光
		if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, sellOurCompanyName)) {
			boolean modeFlg = StringUtils.isNotBlank(businessTypeDcsx) && businessTypeDcsx.contains("HDFK");
			vo.setOurCompanyBankNo(modeFlg ? bank1034 : bank2400);
		}

		// 青岛奥顺宇-网塑宁波
		if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_WSNB, sellOurCompanyName)) {
			vo.setOurCompanyBankNo(bank1034);
		}

		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(entity.getEnterpriseId(), BasConstants.CONFIG_FLG_SWITCH);
		// 使用Stream过滤出键（dictCd）等于'sugx'的BsDictData对象
		BsDictData sugxData = listByCategory.stream()
				.filter(data -> BasConstants.CONFIG_FLG_SWITCH_SUGX.equals(data.getDictCd()))
				.findFirst() // 返回第一个匹配的对象，如果不存在则返回Optional.empty()
				.orElse(null); // 如果没有匹配的对象，返回null或者适当的默认值

		// 青岛中光-苏高新
		if (StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, ourCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, sellOurCompanyName)) {
			if(Objects.nonNull(sugxData)) {
				vo.setOurCompanyBankNo(sugxData.getDictName());
				vo.setOurCompanyBankName(sugxData.getRemark());
			}
		}
		// 苏高新-青岛中光
		if (StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, sellOurCompanyName)) {
			if(Objects.nonNull(sugxData)) {
				vo.setCompanyBankNo(sugxData.getDictName());
				vo.setCompanyBankName(sugxData.getRemark());
			}
		}
		return vo;
	}

	private String contentMerge(String content, DcContractText entity) throws ApplicationException {
		Configuration cfg = new Configuration();
		StringWriter sw = new StringWriter();
		try {
			Template t  = new freemarker.template.Template("", new StringReader(content), cfg);
			t.process(entity, sw);
			content = sw.toString();
		}  catch (Exception e) {
			throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
		}
		return content;
	}


	private void setOurCompanyParam(DcContractText tContract, String companyName){
		BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
		companyOurSearchVo.setCompanyName(companyName);
		BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
		if(Objects.nonNull(companyOur)){
			tContract.setOurCompanyBankName(StringUtils.isBlank(companyOur.getCompanyBankName()) ? "" : companyOur.getCompanyBankName());
			tContract.setOurCompanyBankNo(StringUtils.isBlank(companyOur.getCompanyCardId()) ? "" : companyOur.getCompanyCardId());
			tContract.setOurAddress(StringUtils.isBlank(companyOur.getAddress()) ? "" : companyOur.getAddress());
			tContract.setOurCompanyFax(StringUtils.isBlank(companyOur.getCompanyFax()) ? "" : companyOur.getCompanyFax());
			tContract.setOurCompanyPerson(StringUtils.isBlank(companyOur.getCompanyPerson()) ? "" : companyOur.getCompanyPerson());
			tContract.setOurCompanyPhone(StringUtils.isBlank(companyOur.getCompanyPhone()) ? "" : companyOur.getCompanyPhone());
			tContract.setOurCompanyContact(StringUtils.isBlank(companyOur.getCompanyContact()) ? "" : companyOur.getCompanyContact());
			tContract.setOurCompanyName(StringUtils.isBlank(companyOur.getCompanyName()) ? "" : companyOur.getCompanyName());
			tContract.setSigningAddr(StringUtils.isBlank(companyOur.getSigningAddr()) ? "" : companyOur.getSigningAddr());
			tContract.setOurCompanyTaxNo(StringUtils.isBlank(companyOur.getCompanyTaxNo()) ? "" : companyOur.getCompanyTaxNo());
		} else {
			BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(companyName);
			if (byCompanyName!=null) {
				String signingAddr =byCompanyName.getSigningAddr();
				String ourCompanyPerson = byCompanyName.getCompanyPerson();
				String ourCompanyContact = byCompanyName.getCompanyContact();
				String ourCompanyFax =byCompanyName.getCompanyFax();
				String ourCompanyPhone = byCompanyName.getCompanyPhone();
				String ourCompanyTaxNo = byCompanyName.getCompanyTaxNo();
				String ourCompanyBankName = byCompanyName.getCompanyBankName();
				String ourCompanyBankNo =byCompanyName.getCompanyCardId();

				tContract.setOurCompanyBankName(StringUtils.isBlank(byCompanyName.getCompanyBankName()) ? "" : byCompanyName.getCompanyBankName());
				tContract.setOurCompanyBankNo(StringUtils.isBlank(byCompanyName.getCompanyCardId()) ? "" : byCompanyName.getCompanyCardId());
				tContract.setOurAddress(StringUtils.isBlank(byCompanyName.getAddress()) ? "" : byCompanyName.getAddress());
				tContract.setOurCompanyFax(StringUtils.isBlank(ourCompanyFax) ? "" : ourCompanyFax);
				tContract.setOurCompanyPerson(StringUtils.isBlank(ourCompanyPerson) ? "" : ourCompanyPerson);
				tContract.setOurCompanyPhone(StringUtils.isBlank(ourCompanyPhone) ? "" : ourCompanyPhone);
				tContract.setOurCompanyContact(StringUtils.isBlank(ourCompanyContact) ? "" : ourCompanyContact);
				tContract.setOurCompanyName(StringUtils.isBlank(companyName) ? "" : companyName);
				tContract.setSigningAddr(StringUtils.isBlank(signingAddr) ? "" : signingAddr);
				tContract.setOurCompanyTaxNo(StringUtils.isBlank(ourCompanyTaxNo) ? "" : ourCompanyTaxNo);
			}
		}

	}


	private void setCompanyParam(DcContractText tContract, String companyName){
		BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(companyName);
			if (byCompanyName!=null) {
				String companyPerson =byCompanyName.getCompanyPerson();
				String companyContact = byCompanyName.getCompanyContact();
				String companyFax =byCompanyName.getCompanyFax();
				String companyPhone =byCompanyName.getCompanyPhone();
				String companyTaxNo =byCompanyName.getCompanyTaxNo();
				String companyBankName = byCompanyName.getCompanyBankName();
				String companyBankNo =byCompanyName.getCompanyCardId();
				String address = byCompanyName.getAddress();

				tContract.setCompanyPerson(StringUtils.isBlank(companyPerson) ? "" : companyPerson);
				tContract.setCompanyContact(StringUtils.isBlank(companyContact) ? "" : companyContact);
				tContract.setCompanyFax(StringUtils.isBlank(companyFax) ? "" : companyFax);
				tContract.setCompanyPhone(StringUtils.isBlank(companyPhone) ? "" : companyPhone);
				tContract.setCompanyTaxNo(StringUtils.isBlank(companyTaxNo) ? "" : companyTaxNo);
				tContract.setCompanyBankName(StringUtils.isBlank(companyBankName) ? "" : companyBankName);
				tContract.setCompanyBankNo(StringUtils.isBlank(companyBankNo) ? "" : companyBankNo);
				tContract.setAddress(address);
			}


	}
	/**
	 * 连带责任保证书模板
	 * @param model
	 * @return
	 */
	@RequestMapping("getLiabilityText")
	public String getLiabilityText( HttpServletRequest request,Model model)throws ApplicationException{
		//连带责任保证书模板
		BsContractTemplate template = new BsContractTemplate();
		template.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		template.setTemplateTag(BasConstants.TEMPLATETAG_LIABILITY);
		BsContractTemplate bsContractTemplate = bsContractTemplateClient.findByTemplateTagAndEnterpriseId(template);
		if (bsContractTemplate != null && !StringUtils.isEmpty(bsContractTemplate.getContent())) {
			LiabilityText liabilityText = new LiabilityText();
			// 业务员
			String matchUserName = request.getParameter("matchUserName");
			liabilityText.setMatchUserName(matchUserName);
			// 当前时间
			Date date = new Date();
			liabilityText.setCreatedDate(DateUtil.format(date, "yyyy年MM月dd日"));
			// 公司名称
			String companyName = request.getParameter("companyName");
			liabilityText.setCompanyName(companyName);
			// 合同编号
			String contractNo = request.getParameter("contractNo");
			liabilityText.setContractNo(contractNo);
			// 货名（品名/牌号/厂商）
			String brandNumber = request.getParameter("brandNumber");
			String factoryName = request.getParameter("factoryName");
			String productName = request.getParameter("productName");
			String pName = productName+"/"+brandNumber+"/"+factoryName;
			liabilityText.setProductsName(pName);
			// 金额（C类:金额50%,D类:金额100%）
			String companyId = request.getParameter("companyId");
			BsCompany bsCompany = bsCompanyClient.getEntity(Long.valueOf(companyId));
			String companyGrade = bsCompany.getCompanyGrade();
			if (companyGrade.equals("C")){
				liabilityText.setCompanyPrice("50%");
			}else{
				liabilityText.setCompanyPrice("100%");
			}
			try {
				String s = LiabilityMerge(bsContractTemplate.getContent(), liabilityText);
				model.addAttribute("contractText", s);
			} catch (ApplicationException e) {
				throw new ApplicationException("合同模板合并错误");
			}
		} else {
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		return "ctr/contractText";

	}

	/**
	 * 连带责任保证书模板
	 * @param model
	 * @return
	 */
	@RequestMapping("getLiabilityTextNew")
	public String getLiabilityTextNew( HttpServletRequest request,Model model)throws ApplicationException{
		//连带责任保证书模板
		BsContractTemplate template = new BsContractTemplate();
		template.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		template.setTemplateTag(BasConstants.TEMPLATETAG_LIABILITY);
		BsContractTemplate bsContractTemplate = bsContractTemplateClient.findByTemplateTagAndEnterpriseId(template);
		if (bsContractTemplate != null && !StringUtils.isEmpty(bsContractTemplate.getContent())) {
			LiabilityText liabilityText = new LiabilityText();
			// 业务员
			String matchUserName = request.getParameter("matchUserName");
			liabilityText.setMatchUserName(matchUserName);
			// 当前时间
			Date date = new Date();
			liabilityText.setCreatedDate(DateUtil.format(date, "yyyy年MM月dd日"));
			// 公司名称
			String companyName = request.getParameter("companyName");
			liabilityText.setCompanyName(companyName);
			// 合同编号
			String contractNo = request.getParameter("contractNo");
			liabilityText.setContractNo(contractNo);
			// 货名（品名/牌号/厂商）
			String brandNumber = request.getParameter("brandNumber");
			String factoryName = request.getParameter("factoryName");
			String productName = request.getParameter("productName");
			String pName = productName+"/"+brandNumber+"/"+factoryName;
			liabilityText.setProductsName(pName);
			// 金额（C类:金额50%,D类:金额100%）
			String companyId = request.getParameter("companyId");
			BsCompany bsCompany = bsCompanyClient.getEntity(Long.valueOf(companyId));
			String companyGrade = bsCompany.getCompanyGrade();
			if (companyGrade.equals("C")){
				liabilityText.setCompanyPrice("50%");
			}else{
				liabilityText.setCompanyPrice("100%");
			}
			try {
				String s = LiabilityMerge(bsContractTemplate.getContent(), liabilityText);
				model.addAttribute("contractText", s);
			} catch (ApplicationException e) {
				throw new ApplicationException("合同模板合并错误");
			}
		} else {
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		String windowId = request.getParameter("windowId");
		model.addAttribute("windowId", windowId);
		return "ctr/contractTextNew";

	}

	private String LiabilityMerge(String content, LiabilityText entity) throws ApplicationException {
		Configuration cfg = new Configuration();
		StringWriter sw = new StringWriter();
		try {
			Template t  = new freemarker.template.Template("", new StringReader(content), cfg);
			t.process(entity, sw);
			content = sw.toString();
		}  catch (Exception e) {
			throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
		}
		return content;
	}


}
