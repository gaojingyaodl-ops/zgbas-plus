package com.spt.bas.server.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.google.common.base.Splitter;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.BsConfigReqVo;
import com.spt.bas.client.vo.BsConfigRespVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsConfigDao;
import com.spt.bas.server.dao.BsDictDataDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.IBsCompanyConfigService;
import com.spt.bas.server.service.IBsCompanyDcsxService;
import com.spt.bas.server.service.IBsConfigService;
import com.spt.bas.server.service.IBusinessRestrictRelieveService;
import com.spt.bas.server.stock.service.IStockVirtualService;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.service.IPmProcessService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;

/**
 * @Author: gaojy
 * @create 2021/12/13 11:07
 * @version: 1.0
 * @description:
 */
@Component
@Transactional(readOnly = true)
public class BsConfigServiceImpl extends BaseService<BsConfig> implements IBsConfigService {
    @Autowired
    private BsConfigDao bsConfigDao;
    @Autowired
    private BsDictDataDao bsDictDataDao;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Autowired
    private IBusinessRestrictRelieveService businessRestrictRelieveService;
    @Autowired
    private IStockVirtualService stockVirtualService;
    @Resource
    private CtrContractDao ctrContractDao;
    @Resource
    private IBsCompanyConfigService bsCompanyConfigService;

    @Override
    public BaseDao<BsConfig> getBaseDao() {
        return bsConfigDao;
    }

    @Override
    public Class<BsConfig> getEntityClazz() {
        return BsConfig.class;
    }

    @Override
    public BsConfigRespVo judgmentStart(BsConfigReqVo configReqVo) {
        BsConfigRespVo respVo = new BsConfigRespVo();
        ApplyMatch applyMatch = configReqVo.getApplyMatch();
        List<ApplyMatchDetail> applyMatchDetailList = configReqVo.getApplyMatchDetailList();
        if (Objects.nonNull(applyMatch) && Objects.nonNull(applyMatch.getStockVirtualId())) {
            StockVirtual stockVirtual = stockVirtualService.getEntity(applyMatch.getStockVirtualId());
            if (Objects.nonNull(stockVirtual) && StringUtils.equals(BasConstants.STOCK_VIRTUAL_KC, stockVirtual.getVirtualBuyType())) {
                BigDecimal minSellPrice = Objects.nonNull(stockVirtual.getMinSellPrice()) ? stockVirtual.getMinSellPrice() : stockVirtual.getDealPrice();
                String matchUserName = stockVirtual.getMatchUserName();
                BigDecimal currSellPrice = applyMatchDetailList.stream()
                        .filter(d -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, d.getContractType()))
                        .map(ApplyMatchDetail::getDealPrice).findFirst().orElse(BigDecimal.ZERO);
                if (minSellPrice.compareTo(currSellPrice) > 0) {
                    respVo.setStartFlg(false);
                    respVo.setMessage(String.format("本预算单所关联的库存采购【%s】-（%s），规定销售指导价为：%s；请联系采购业务员：%s修改指导价后再次发起或直接修改销售价后重新发起!",
                            stockVirtual.getStockVirtualNo(), matchUserName, minSellPrice.stripTrailingZeros().toPlainString(),matchUserName));
                    return respVo;
                }
            }
        }

        // 判断是否启用业务发起判断逻辑
        Long enterpriseId = configReqVo.getEnterpriseId();
        Long processId = configReqVo.getProcessId();
        BigDecimal contractAmount = configReqVo.getContractAmount();
        BsDictData configSwitch = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "configSwitch", enterpriseId);
        if (Objects.isNull(configSwitch) || !StringUtils.equalsIgnoreCase("true", configSwitch.getDictName())) {
            // 不启用业务判断
            respVo.setStartFlg(true);
            return respVo;
        }
        PmProcess pmProcess = pmProcessService.getEntity(processId);
        if (Objects.isNull(pmProcess) || !BasConstants.BS_CONFIG_FILTER_PROCESS_LIST.contains(pmProcess.getProcessCode())) {
            // 不启用业务判断 仅判断 赊销预算、代采赊销预算
            respVo.setStartFlg(true);
            return respVo;
        }
        try {
            // 进行业务配置判断
            String ourCompanyName = configReqVo.getOurCompanyName();
            String fundSource = configReqVo.getFundSource();
            String contractModel = configReqVo.getContractModel();
            String sxCompany = configReqVo.getSxCompany();
            String ourCompanyNameKey = BsCompanyOurUtil.getKey(enterpriseId, ourCompanyName);
            String fundSourceValue = BsDictUtil.getValue(enterpriseId, BasConstants.CONFIG_TYPE_FUND_SOURCE, fundSource);
            String contractModelValue = BsDictUtil.getValue(enterpriseId, BasConstants.CONFIG_TYPE_CONTRACT_MODEL, contractModel);
            BsCompanyDcsx companyDcsx = bsCompanyDcsxService.findByCompanyName(sxCompany);
            String sxCompanyKey = Objects.nonNull(companyDcsx) ? companyDcsx.getCompanyCd() : "";

            BsConfig bsConfig = findBsConfigList(enterpriseId, ourCompanyNameKey, fundSource, contractModel, sxCompanyKey, contractAmount);
            if (Objects.isNull(bsConfig)) {
                respVo.setStartFlg(false);
                respVo.setMessage(getMessage(false, sxCompany, contractModelValue, ourCompanyName, fundSourceValue, BigDecimal.ZERO));
                return respVo;
            }
            BigDecimal balance = bsConfig.getBalance();
            // 是否可以发起申请
            boolean canStartFlg = balance.compareTo(contractAmount) >= 0;
            respVo.setStartFlg(canStartFlg);
            respVo.setBsConfig(bsConfig);
            respVo.setMessage(getMessage(canStartFlg, sxCompany, contractModelValue, ourCompanyName, fundSourceValue, balance));
        } catch (Exception e) {
            logger.info("judgmentStart error", e);
            respVo.setStartFlg(false);
        }
        return respVo;
    }

    /**
     * 1.预算申请控制毛利率必须大于规定比率
     *      代采预算限制毛利率 千1
     *      赊销预算限制毛利率 万7
     * 2.如果有合同超三天未发货，不允许发起
     * 3.如果有合同本金未收回，不允许发起
     *
     * @param matchDetailList 采购销售信息列表
     * @param enterpriseId    企业账套ID
     * @param creditFlg       是否使用授信
     * @return
     */
    @Override
    public BsConfigRespVo judgmentMatchProfit(List<ApplyMatchDetail> matchDetailList, Long enterpriseId, Boolean creditFlg) {
        BsConfigRespVo respVo = new BsConfigRespVo();
        respVo.setBusinessRestrictRelieveFlg(false);
        // 判断总开关是否开启
        boolean switchFlg = verifyProfitSwitch(respVo, creditFlg, enterpriseId);
        if (Boolean.TRUE.equals(switchFlg)) {
            return respVo;
        }
        if (CollectionUtils.isEmpty(matchDetailList)) {
            return respVo;
        }
        BigDecimal profitRate = respVo.getProfitRate();
        ApplyMatchDetail sellMatch = matchDetailList.stream()
                .filter(s -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, s.getContractType()))
                .findFirst().orElse(null);
        if (Objects.isNull(sellMatch)){
            return respVo;
        }
        Long companyId = sellMatch.getCompanyId();
        Long matchUserId = sellMatch.getMatchUserId();
        // 1.客户存在逾期订单不能发起
        boolean unReceiveFlg = verifyBreachContract(companyId, respVo);

        // 2.客户存在超三天未发货订单不能发起
        boolean unDeliveryFlg = verifyDelivery3Day(companyId, respVo);

        // 3.判断订单毛利是否符合规定

        // 4.若企业存在特户计划名单，则使用配置的限制毛利
        if (Boolean.TRUE.equals(creditFlg)){
            BsCompanyConfig companyConfig = bsCompanyConfigService.findByBsCompanyIdAndMatchUserId(companyId, matchUserId);
            if (Objects.nonNull(companyConfig) && Objects.nonNull(companyConfig.getProfitRate()) && companyConfig.getProfitRate().compareTo(BigDecimal.ZERO) != 0){
                profitRate = companyConfig.getProfitRate();
            }
        }
        boolean profitFlg = verifyProfit(matchDetailList, creditFlg, profitRate, respVo);

        boolean startFlg = profitFlg && unDeliveryFlg && unReceiveFlg;
        if (Boolean.FALSE.equals(startFlg)) {
            // 存在可用业务限制解除发起次数
            BusinessRestrictRelieve businessRestrictRelieve = businessRestrictRelieveService.findByCompanyIdAndAndUserId(companyId, matchUserId);
            if (Objects.nonNull(businessRestrictRelieve) && businessRestrictRelieve.getUsableCount() > 0) {
                startFlg = true;
                int usableCount = businessRestrictRelieve.getUsableCount() - 1;
                businessRestrictRelieveService.updateUsableCount(businessRestrictRelieve.getId(), usableCount);
                respVo.setBusinessRestrictRelieveFlg(true);
            }
        }
        respVo.setStartFlg(startFlg);
        return respVo;
    }

    /**
     * 判断业务发起校验开关是否开启
     * @param respVo
     * @param creditFlg
     * @param enterpriseId
     * @return
     */
    private boolean verifyProfitSwitch(BsConfigRespVo respVo, Boolean creditFlg, Long enterpriseId){
        BsDictData profitSwitch;
        if (Boolean.FALSE.equals(creditFlg)) {
            profitSwitch = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "profitSwitch", enterpriseId);
        } else {
            profitSwitch = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "profitSwitchCredit", enterpriseId);
        }
        if (Objects.isNull(profitSwitch) || !StringUtils.equalsIgnoreCase("true", profitSwitch.getDictName())) {
            // 不启用毛利率控制开关
            respVo.setStartFlg(true);
            return true;
        }

        BsDictData profitRateDict;
        BigDecimal profitRate = Boolean.TRUE.equals(creditFlg) ? BigDecimal.valueOf(0.00007) : BigDecimal.valueOf(0.0001);
        if (Boolean.TRUE.equals(creditFlg)) {
            profitRateDict = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, BasConstants.PROFIT_RATE_CREDIT, enterpriseId);
        } else {
            profitRateDict = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, BasConstants.PROFIT_RATE, enterpriseId);
        }
        if (Objects.nonNull(profitRateDict) && NumberUtil.isNumber(profitRateDict.getDictName())) {
            profitRate = new BigDecimal(profitRateDict.getDictName());
        }
        respVo.setProfitRate(profitRate);
        return false;
    }

    /**
     * 订单毛利是否符合规定
     * @param matchDetailList
     * @param creditFlg
     * @param profitRate
     * @param respVo
     * @return
     */
    private boolean verifyProfit(List<ApplyMatchDetail> matchDetailList, Boolean creditFlg, BigDecimal profitRate, BsConfigRespVo respVo){
        ApplyMatchDetail buyMatch = matchDetailList.stream().filter(s -> StringUtils.equals(BasConstants.CONTRACT_TYPE_B, s.getContractType())).findFirst().orElse(new ApplyMatchDetail());
        ApplyMatchDetail sellMatch = matchDetailList.stream().filter(s -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, s.getContractType())).findFirst().orElse(new ApplyMatchDetail());
        // 销售/采购总额
        BigDecimal sellAmount = sellMatch.getTotalAmount();
        BigDecimal buyAmount = buyMatch.getTotalAmount();

        // 销售/采购仓储费
        BigDecimal sellWarehouse = sellMatch.getWarehouseCost();
        BigDecimal buyWarehouse = buyMatch.getWarehouseCost();

        // 销售/采购运输费
        BigDecimal sellTransport = sellMatch.getTransportCost();
        BigDecimal buyTransport = buyMatch.getTransportCost();

        // 销售/采购装卸费
        BigDecimal sellStevedorage = sellMatch.getStevedorage();
        BigDecimal buyStevedorage = buyMatch.getStevedorage();

        // 销售/采购贴现费用
        BigDecimal sellDiscountAmount = sellMatch.getDiscountAmount();
        BigDecimal buyDiscountAmount = buyMatch.getDiscountAmount();

        // 账期
        int creditDays = Objects.isNull(sellMatch.getCreditDays()) ? 1 : sellMatch.getCreditDays();
        creditDays = (creditFlg && (creditDays < 7)) ? 7 : creditDays;
        BigDecimal realCreditDays = creditDays <= 1 ? BigDecimal.ONE : BigDecimal.valueOf(creditDays);
        // 毛利率 = (销售总价 - 采购总价 - 采购运输费 - 采购仓储费 - 销售运输费 - 销售仓储费 - 采购装卸费 - 销售装卸费) / 采购总价 / 账期
        BigDecimal realProfit = (sellAmount.subtract(buyAmount).subtract(buyWarehouse).subtract(sellWarehouse).subtract(buyTransport).
                subtract(sellTransport).subtract(buyStevedorage).subtract(sellStevedorage).subtract(sellDiscountAmount).subtract(buyDiscountAmount)).
                divide(buyAmount, 6, RoundingMode.HALF_UP).divide(realCreditDays, 6, RoundingMode.HALF_UP);
        logger.info("客户:{}，销售额：{}，采购额：{}，采购仓储费：{}，销售仓储费：{}，采购运输费：{}，销售运输费：{}，采购装卸费：{}，销售装卸费：{},采购贴现费用：{}，销售贴现费用：{}，账期：{}", sellMatch.getCompanyName(),
                sellAmount, buyAmount, buyWarehouse, sellWarehouse, buyTransport, sellTransport, buyStevedorage, sellStevedorage, buyDiscountAmount, sellDiscountAmount, realCreditDays);
        logger.info("系统设置最低毛利率:{},本单计算毛利率:{}", profitRate, realProfit);
        boolean profitFlg = realProfit.compareTo(profitRate) >= 0;
        respVo.setStartFlg(profitFlg);
        if (Boolean.FALSE.equals(profitFlg)){
            BigDecimal oneHundred = BigDecimal.valueOf(100);
            String[] param = new String[]{profitRate.multiply(oneHundred).stripTrailingZeros().toPlainString(),
                    realProfit.multiply(oneHundred).stripTrailingZeros().toPlainString()};
            respVo.setMessage(MessageFormat.format("预算毛利率必须≥{0}%,当前申请毛利率{1}%，不能发起!", param));
        }
        return profitFlg;
    }

    /**
     * 判断客户存在超三天未发货订单不能发起
     * @param companyId
     * @param respVo
     * @return
     */
    private boolean verifyDelivery3Day(Long companyId, BsConfigRespVo respVo){
        String unDeliveryContractNos = ctrContractDao.findUnDelivery3Day(companyId);
        if (StringUtils.isNotBlank(unDeliveryContractNos) && CollectionUtils.isNotEmpty(Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(unDeliveryContractNos))) {
            respVo.setStartFlg(false);
            respVo.setMessage(MessageFormat.format("该客户存在超三天未发货订单，不能发起，请联系风控或申请业务限制解除申请，合同编号:{0}", unDeliveryContractNos));
            return false;
        }
        return true;
    }

    /**
     * 判断客户存在逾期订单不能发起
     * @param companyId
     * @param respVo
     * @return
     */
    private boolean verifyBreachContract(Long companyId, BsConfigRespVo respVo){
        String unReceiveContractNos = ctrContractDao.findUnReceive(companyId);
        if (StringUtils.isNotBlank(unReceiveContractNos) && CollectionUtils.isNotEmpty(Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(unReceiveContractNos))) {
            respVo.setStartFlg(false);
            respVo.setMessage(MessageFormat.format("该客户存在逾期订单，不能发起，请联系风控或申请业务限制解除申请，合同编号:{0}", unReceiveContractNos));
            return false;
        }
        return true;
    }

    private String getMessage(Boolean canStartFlg, String sxCompany, String contractModelValue, String ourCompanyName, String fundSourceValue, BigDecimal balance) {
        String message = "";
        if (Boolean.TRUE.equals(canStartFlg)) {
            return message;
        }
        balance = Objects.isNull(balance) ? BigDecimal.ZERO : balance;
        if (StringUtils.isNotBlank(sxCompany) && StringUtils.isNotBlank(contractModelValue)) {
            message = String.format("[%s]-[%s]-[%s]-[%s]，可用额度%s元，不能发起!", ourCompanyName, sxCompany, fundSourceValue, contractModelValue, balance);
        } else if (StringUtils.isNotBlank(sxCompany)) {
            message = String.format("[%s]-[%s]-[%s]，可用额度%s元，不能发起!", ourCompanyName, sxCompany, fundSourceValue, balance);
        } else if (StringUtils.isNotBlank(contractModelValue)) {
            message = String.format("[%s]-[%s]-[%s]，可用额度%s元，不能发起!", ourCompanyName, fundSourceValue, contractModelValue, balance);
        } else {
            message = String.format("[%s]-[%s]，可用额度%s元，不能发起!", ourCompanyName, fundSourceValue, balance);
        }
        return message;
    }

    @Override
    public List<String> findConfigMessageList(Long enterpriseId) {
        List<String> bsConfigMessageList = new ArrayList<>();
        StringBuilder message;
        List<BsConfig> bsConfigList = bsConfigDao.findByEnterpriseIdAndEnableFlgTrue(enterpriseId);
        int index = 1;
        for (BsConfig bsConfig : bsConfigList) {
            message = new StringBuilder(index + ".");
            String fundSource = BsDictUtil.getValue(enterpriseId, BasConstants.CONFIG_TYPE_FUND_SOURCE, bsConfig.getFundSource());
            String contractModel = BsDictUtil.getValue(enterpriseId, BasConstants.CONFIG_TYPE_CONTRACT_MODEL, bsConfig.getContractModel());
            if (StringUtils.isNotBlank(fundSource)) {
                message.append(fundSource).append("，我方【");
            }
            String ourCompanyName = bsConfig.getOurCompanyName();
            List<String> ourCompanyNameList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(ourCompanyName);
            if (CollectionUtils.isNotEmpty(ourCompanyNameList)) {
                for (String companyCd : ourCompanyNameList) {
                    message.append(BsCompanyOurUtil.getCompanyAbbr(enterpriseId, companyCd)).append("/ ");
                }
                message = new StringBuilder(message.substring(0, message.length() - 2));
            }
            String sxCompany = bsConfig.getSxCompany();
            StringBuilder sxCompanyAbbr = new StringBuilder();
            if (StringUtils.isNotBlank(sxCompany)) {
                List<String> sxCompanyNameList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(sxCompany);
                if (CollectionUtils.isNotEmpty(sxCompanyNameList)) {
                    for (String sxCompanyCd : sxCompanyNameList) {
                        BsCompanyDcsx companyDcsx = bsCompanyDcsxService.findByCompanyCd(sxCompanyCd);
                        if (Objects.nonNull(companyDcsx)) {
                            sxCompanyAbbr.append(companyDcsx.getCompanyAbbr()).append("/ ");
                        }
                    }
                    if (StringUtils.isNotEmpty(sxCompanyAbbr.toString())) {
                        sxCompanyAbbr = new StringBuilder(sxCompanyAbbr.substring(0, sxCompanyAbbr.length() - 2));
                    }
                }
            }
            if (StringUtils.isNotBlank(sxCompanyAbbr.toString())) {
                message.append("】，代采方【").append(sxCompanyAbbr);
            }
            if (StringUtils.isNotBlank(contractModel)) {
                message.append("】，").append(contractModel);
            }
            BigDecimal balance = Objects.isNull(bsConfig.getBalance()) ? BigDecimal.ZERO : bsConfig.getBalance();
            DecimalFormat df = new DecimalFormat("###,###.##");
            String format = df.format(balance);
            message.append("，剩余可用额度：").append(format).append("元；");
            bsConfigMessageList.add(message.toString());
            index++;
        }
        return bsConfigMessageList;
    }

    /**
     * 更新业务配置数据可用额度
     *
     * @param approveNo
     * @param bsConfigId
     * @param applyAmount
     */
    @Override
    @ServerTransactional
    public void refreshBalance(String approveNo, Long bsConfigId, BigDecimal applyAmount) {
        BsConfig entity = this.getEntity(bsConfigId);
        if (Objects.isNull(entity)) {
            logger.error("业务配置查询异常 bsConfigId:{},approveNo:{},applyAmount:{}", bsConfigId, approveNo, applyAmount);
            return;
        }
        BigDecimal balance = entity.getBalance();
        entity.setBalance(balance.add(applyAmount));
        bsConfigDao.save(entity);
    }

    public BsConfig findBsConfigList(Long enterpriseId, String companyNameKey, String fundSource, String contractModel, String sxCompany, BigDecimal contractAmount) {
        logger.info("findBsConfigList enterpriseId:{},companyNameKey:{},fundSource:{},contractModel:{},sxCompany:{}",
                enterpriseId, companyNameKey, fundSource, contractModel, sxCompany);
        // 处理之后添加的代采赊销预算-赊销模式数据字典不一致问题
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_DCSXBL, contractModel)) {
            contractModel = BasConstants.CONFIG_TYPE_CONTRACT_MODEL_BL;
        } else if (BasConstants.PT_BUSINESS_CODE.contains(contractModel)) {
            contractModel = BasConstants.CONFIG_TYPE_CONTRACT_MODEL_PT;
        }

        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("EQL_enterpriseId", enterpriseId);
        searchParams.put("EQB_enableFlg", true);
        searchParams.put("LIKES_ourCompanyName", companyNameKey);
        if (StringUtils.isNotBlank(fundSource)) {
            searchParams.put("EQS_fundSource", fundSource);
        }

        List<String> contractModelParams = new ArrayList<>();
        contractModelParams.add(contractModel);
        contractModelParams.add(BasConstants.CONFIG_TYPE_CONTRACT_MODEL_0);
        if (StringUtils.isBlank(contractModel)) {
            searchParams.put("NEQS_contractModel", BasConstants.CONFIG_TYPE_CONTRACT_MODEL_BL);
        } else {
            searchParams.put("INS_contractModel", contractModelParams);
        }

        if (StringUtils.isNotBlank(sxCompany)) {
            searchParams.put("LIKES_sxCompany_OR_ISNULLS_sxCompany", sxCompany);
        } else {
            searchParams.put("ISNULLS_sxCompany", true);
        }
        Specification<BsConfig> specification = WebUtil.buildSpecification(searchParams);
        List<BsConfig> bsConfigList = bsConfigDao.findAll(specification);
        // 优先使用人保授信
        if (CollectionUtils.isNotEmpty(bsConfigList)) {
            BsConfig piccCreditConfig = bsConfigList.stream()
                    .filter(c -> StringUtils.equals(c.getCreditType(), BasConstants.CREDIT_TYPE_0))
                    .filter(c -> Objects.nonNull(c.getBalance()))
                    .filter(c -> c.getBalance().compareTo(contractAmount) >= 0)
                    .findFirst().orElse(null);
            if (Objects.nonNull(piccCreditConfig)) {
                return piccCreditConfig;
            }
            return bsConfigList.stream()
                    .filter(c -> Objects.nonNull(c.getBalance()))
                    .max(Comparator.comparing(BsConfig::getBalance))
                    .orElse(null);
        }
        return null;
    }


    @Override
    public List<BsConfig> getBsConfigList(Long enterpriseId) {
        List<BsConfig> bsConfigList = bsConfigDao.findByEnterpriseIdAndEnableFlgTrue(enterpriseId);
        for (BsConfig bsConfig : bsConfigList) {
            // 我方公司名称处理
            String ourCompanyName = bsConfig.getOurCompanyName();
            if(StringUtils.isNotBlank(ourCompanyName)){
                List<String> ourCompanyNameList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(ourCompanyName);
                if (CollectionUtils.isNotEmpty(ourCompanyNameList)) {
                    StringBuilder message = new StringBuilder();
                    for (String companyCd : ourCompanyNameList) {
                        message.append(BsCompanyOurUtil.getCompanyAbbr(enterpriseId, companyCd)).append("/ ");
                    }
                    message = new StringBuilder(message.substring(0, message.length() - 2));
                    bsConfig.setOurCompanyName(message.toString());
                }
            }
            // 代采方公司名称处理
            String sxCompany = bsConfig.getSxCompany();
            if (StringUtils.isNotBlank(sxCompany)) {
                StringBuilder sxCompanyAbbr = new StringBuilder();
                List<String> sxCompanyNameList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(sxCompany);
                if (CollectionUtils.isNotEmpty(sxCompanyNameList)) {
                    for (String sxCompanyCd : sxCompanyNameList) {
                        BsCompanyDcsx companyDcsx = bsCompanyDcsxService.findByCompanyCd(sxCompanyCd);
                        if (Objects.nonNull(companyDcsx)) {
                            sxCompanyAbbr.append(companyDcsx.getCompanyAbbr()).append("/ ");
                        }
                    }
                    sxCompanyAbbr = new StringBuilder(sxCompanyAbbr.substring(0, sxCompanyAbbr.length() - 2));
                    bsConfig.setSxCompany(sxCompanyAbbr.toString());
                }
            }
        }
        return bsConfigList;
    }
}
