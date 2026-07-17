package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IApplyChargeSalesClient;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.remote.IBsCompanyOurClient;
import com.spt.bas.client.util.RmbUtil;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.ICtrContractChainTextService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.util.MidstreamUtil;
import com.spt.bas.server.util.TemplateContentUtility;
import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Transactional(readOnly = true)
public class CtrContractChainTextServiceImpl extends BaseService<CtrContractChainText> implements ICtrContractChainTextService {
    @Autowired
    private CtrContractChainTextDao ctrContractChainTextDao;
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
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Autowired
    private IBsCompanyOurClient bsCompanyOurClient;
    @Autowired
    private IApplyChargeSalesClient applyChargeSalesClient;
    @Autowired
    private ICtrContractService ctrContractService;
    @Resource
    private MidstreamUtil midstreamUtil;

    @Override
    public BaseDao<CtrContractChainText> getBaseDao() {
        return ctrContractChainTextDao;
    }

    @Override
    public Class<CtrContractChainText> getEntityClazz() {
        return CtrContractChainText.class;
    }
    @Override
    @ServerTransactional
    public CtrContractChainText saveContractText(CtrContract entity) throws ApplicationException {
        CtrContractChainText saveContractText = saveContractText(entity, null);
        return saveContractText;
    }

    @Override
    @ServerTransactional
    public CtrContractChainText saveServiceText(CtrServiceContract ctrServiceContract) throws ApplicationException {
        Long bsTemplateContractId = ctrServiceContract.getBsTemplateContractId();
        if (bsTemplateContractId != null && bsTemplateContractId != 0L){
            CtrContractChainText contractTex = ctrContractChainTextDao.findByCtrContractIdAndContractType(ctrServiceContract.getId(),BasConstants.CONTRACT_TYPE_F);
            if(contractTex!=null){
                ctrContractChainTextDao.delete(contractTex);
            }
            BsContractTemplateVo templateVo = new BsContractTemplateVo();
            BsContractTemplate tempalte = contractTemplateDao.findOne(ctrServiceContract.getBsTemplateContractId());
            templateVo.setContent(tempalte.getContent());
            templateVo.setTemplateId(tempalte.getId());
            CtrContractTextVo vo = new CtrContractTextVo();
            CtrContractChainText text = new CtrContractChainText();
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
            CtrContractChainText save = ctrContractChainTextDao.save(text);
            return save;
        }
        return null;
    }


    @Override
    @ServerTransactional
    public CtrContractChainText saveContractText(CtrContract entity,List<CtrProduct> lstProduct) throws ApplicationException {
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
        List<CtrProductVo> productTextList = new ArrayList<CtrProductVo>();
        Long enterpriseId = entity.getEnterpriseId();
        String deliveryType = entity.getDeliveryType();
        String qualityStandard = entity.getQualityStandard();
        String ourCompanyName = entity.getOurCompanyName();
        Date deliveryDateFrom = entity.getDeliveryDateFrom();
        String attachDeliveryTime = entity.getAttachDeliveryTime();
        String deliveryAddr = entity.getDeliveryAddr();
        String invoiceDate = entity.getInvoiceDate();
        CtrContractChainText contractTex = ctrContractChainTextDao.findByCtrContractIdAndContractType(entity.getId(),entity.getContractType());
        if(contractTex!=null){
            ctrContractChainTextDao.delete(contractTex);
        }
        BsContractTemplateVo templateVo = getTemplateContent(entity);

        if(StringUtils.isNotBlank(templateVo.getContent())){
            String wareHouseName = "";
            String wareHouseAddr = "";
            CtrContractChainText text = new CtrContractChainText();
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
                productVo.setDealAmountNotax(productVo.getDealPrice().divide(taxRates, 2, BigDecimal.ROUND_HALF_UP));
                
                BigDecimal dealPriceNoTax = ctrProduct.getDealPrice().divide(taxRates,2, BigDecimal.ROUND_HALF_UP);
                // 不含税单价
                productVo.setDealPriceNoTax(dealPriceNoTax);
                BigDecimal totalPriceNoTax = ctrProduct.getTotalPrice().divide(taxRates, 2, BigDecimal.ROUND_HALF_UP);
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
            
            vo.setSumDealNumber(sumDealNumber);
            vo.setSumTaxPriceNoTax(sumTaxPriceNoTax);
            vo.setSumTotalPriceNoTax(sumTotalPriceNoTax);
            vo.setSumDealPrice(sumDealPrice);
            vo.setSumTotalPrice(sumTotalPrice);

            DecimalFormat df = new DecimalFormat("0%");
            BigDecimal bondRate =  entity.getBondRate();
            if(bondRate == null) {
                vo.setBondRateStr("");
            } else {
                vo.setBondRateStr(df.format(bondRate));
            }

            vo.setProductList(productTextList);
            vo.setOurCompanyName(ourCompanyName);
            vo.setTotalAmount(totalAmount);
            vo.setTotalAmountStr(RmbUtil.number2Chinese(totalAmount));
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
            vo.setContractTimeStr(sdf.format(new Date()));
            if(entity.getDeliveryDateFrom()==null){
                //若无值，则设置为当天
                entity.setDeliveryDateFrom(new Date());
                deliveryDateFrom=new Date();
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

//            List<CtrContract> byApproveId = ctrContractClient.findByApproveId(entity.getApproveId());
//            if(byApproveId.size()>0){
//             String addr=StringUtils.isBlank(byApproveId.get(0).getDeliveryAddr()) ? "" : byApproveId.get(0).getDeliveryAddr()+"/";
//             String addrc=StringUtils.isNotBlank(byApproveId.get(0).getContactAddr()) ? "" :byApproveId.get(0).getContactAddr();
//             vo.setDeliAddr(addr+addrc);
//            }
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

            BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
            companyOurSearchVo.setCompanyName(entity.getOurCompanyName());
            BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
            if(Objects.nonNull(companyOur)){
                vo.setOurBankName(StringUtils.isBlank(companyOur.getCompanyBankName()) ? "" : companyOur.getCompanyBankName());
                vo.setOurBankAccount(StringUtils.isBlank(companyOur.getCompanyCardId()) ? "" : companyOur.getCompanyCardId());
                vo.setOurCompanyAddres(StringUtils.isBlank(companyOur.getAddress()) ? "" : companyOur.getAddress());
                vo.setOurCompanyEmail(StringUtils.isBlank(companyOur.getEmail()) ? "" : companyOur.getEmail());
                vo.setOurCompanyFax(StringUtils.isBlank(companyOur.getCompanyFax()) ? "" : companyOur.getCompanyFax());
                vo.setOurCompanyPerson(StringUtils.isBlank(companyOur.getCompanyPerson()) ? "" : companyOur.getCompanyPerson());
                vo.setMatchUserPhone(StringUtils.isBlank(companyOur.getCompanyPhone()) ? "" : companyOur.getCompanyPhone());
                vo.setOurCompanyName(StringUtils.isBlank(companyOur.getCompanyName()) ? "" : companyOur.getCompanyName());
                String taxNo = StringUtils.isBlank(companyOur.getCompanyTaxNo()) ? "" : companyOur.getCompanyTaxNo();
                vo.setSignAddress(StringUtils.isBlank(companyOur.getSigningAddr()) ? "" : companyOur.getSigningAddr());
                vo.setOurCompanyContact(StringUtils.isBlank(companyOur.getCompanyContact()) ? "" : companyOur.getCompanyContact());
                vo.setOurCompanyPhone(StringUtils.isBlank(companyOur.getCompanyPhone()) ? "" : companyOur.getCompanyPhone());
            } else {
                BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(entity.getOurCompanyName());
                if (byCompanyName!=null) {
                    String companyPerson = byCompanyName.getCompanyPerson();
                    String companyContact = byCompanyName.getCompanyContact();
                    String companyFax = byCompanyName.getCompanyFax();
                    String companyPhone =byCompanyName.getCompanyPhone();
                    String ourCompanyAddres =byCompanyName.getSigningAddr();

                    vo.setOurBankName(StringUtils.isBlank(byCompanyName.getCompanyBankName()) ? "" : byCompanyName.getCompanyBankName());
                    vo.setOurBankAccount(StringUtils.isBlank(byCompanyName.getCompanyCardId()) ? "" : byCompanyName.getCompanyCardId());
                    vo.setOurCompanyAddres(StringUtils.isBlank(byCompanyName.getAddress()) ? "" : byCompanyName.getAddress());
                    vo.setOurCompanyPerson(StringUtils.isBlank(companyPerson) ? "" : companyPerson);
                    vo.setOurCompanyFax(StringUtils.isBlank(companyFax) ? "" : companyFax);
                    vo.setMatchUserName(StringUtils.isBlank(companyContact) ? "" : companyContact);
                    vo.setMatchUserPhone(StringUtils.isBlank(companyPhone) ? "" : companyPhone);
                    vo.setOurCompanyName(StringUtils.isBlank(entity.getOurCompanyName()) ? "" : entity.getOurCompanyName());
                    vo.setSignAddress(StringUtils.isBlank(byCompanyName.getSigningAddr()) ? "" : byCompanyName.getSigningAddr());
                    vo.setOurCompanyContact(StringUtils.isBlank(companyContact) ? "" : companyContact);
                    vo.setOurCompanyPhone(StringUtils.isBlank(companyPhone) ? "" : companyPhone);
                }
            }
            BsCompanyDcsx byCompanyName2 = bsCompanyDcsxClient.findByCompanyName(entity.getCompanyName());
            if (byCompanyName2!=null) {
                String companyPerson = byCompanyName2.getCompanyPerson();
                String companyContact = byCompanyName2.getCompanyContact();
                String companyFax = byCompanyName2.getCompanyFax();

                vo.setContactAddr(byCompanyName2.getSigningAddr());
                vo.setContactName(byCompanyName2.getCompanyContact());
                vo.setContactPhone(byCompanyName2.getCompanyPhone());
                vo.setCompanyPerson(StringUtils.isBlank(companyPerson) ? "" : companyPerson);
                vo.setCompanyFax(StringUtils.isBlank(companyFax) ? "" : companyFax);
            }
            String qualityStandardStr = "";
            String deliveryTypeStr = "";
            if (StringUtils.equals(BasConstants.CONTRACTTYPE_BUY, entity.getContractType())) {
                qualityStandardStr = DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT,
                        StringUtils.isBlank(qualityStandard) ? BasConstants.QUALITY_Y : qualityStandard);
                deliveryTypeStr = midstreamUtil.generateRespStr(ourCompanyName, DictUtil.getValue(BasConstants.DICT_DELIVERYTYPETEXT, deliveryType));
            } else {
                qualityStandardStr = DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD,
                        StringUtils.isBlank(qualityStandard) ? BasConstants.QUALITY_Y : qualityStandard);
                deliveryTypeStr = midstreamUtil.generateRespStr(ourCompanyName, DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, deliveryType));
            }
            // 配送
            if (BasConstants.DICT_TYPE_BUYDELIVERY_P.equals(deliveryType)) {
                vo.setFreightBearing("如供方配送，费用由供方承担。");
            } else if (BasConstants.DICT_TYPE_BUYDELIVERY_Z.equals(deliveryType)) {
                // 自提
                vo.setFreightBearing("如需方自提，提货费用自理；若产生入库费用，由需方承担。仓储费自交割日期免三天，超期仓储费和损耗由需方承担。");
            }

            // 质量标准
            vo.setQualityStandardStr(qualityStandardStr);
            //交货方式
            vo.setDeliveryTypeStr(deliveryTypeStr);
            //开票时间
            if (invoiceDate != null) {
                String invoiceDateStr = DictUtil.getValue(BasConstants.DICT_TYPE_INVOICEDATE, invoiceDate);
                vo.setInvoiceDateStr(invoiceDateStr);
            }

            boolean specialFlg = ctrContractService.judgeUseSpecialBankContractId(entity.getId());
            if (Boolean.TRUE.equals(specialFlg)) {
                BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(BasConstants.ZG_ENTERPRISE_ID);
                if (Objects.nonNull(specialBank)) {
                    vo.setOurBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
                    vo.setOurBankAccount(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());
                }
            }
            
            text.setContractType(entity.getContractType());
            text.setContent(contentMerge(templateVo.getContent(),vo));
            CtrContractChainText contractText = ctrContractChainTextDao.save(text);
            return contractText;
        }
        return null;
    }

    @Override
    public CtrContractChainText findByContractIdAndContractType(Long contractId,String contractType) {
        return ctrContractChainTextDao.findByCtrContractIdAndContractType(contractId,contractType);
    }
    //将合同内容填充至模板
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

}

