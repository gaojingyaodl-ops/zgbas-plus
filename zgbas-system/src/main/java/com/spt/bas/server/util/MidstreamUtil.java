package com.spt.bas.server.util;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.entity.BsProductConfig;
import com.spt.bas.server.dao.BsProductConfigDao;
import com.spt.tools.core.date.DateOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

/**
 * 中游合同计算工具类
 *
 * @Author MoonLight
 * @Date 2024/4/3 16:20
 * @Version 1.0
 */
@Component
public class MidstreamUtil {
    private final static String MIDSTREAM_EXPRESS = "MIDSTREAM_EXPRESS";
    private final static String STR_RESP_EXPRESS = "STR_RESP_EXPRESS";
    private final static String TP_FUND_RATE = "TP_FUND_RATE";
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private BsProductConfigDao bsProductConfigDao;

    private static class EqOperator extends Operator {
        @Override
        public Object executeInner(Object[] list) {
            String str1 = String.valueOf(list[0]);
            String str2 = String.valueOf(list[1]);
            return StringUtils.equals(str1, str2);
        }
    }

    private static class NEqOperator extends Operator {
        @Override
        public Object executeInner(Object[] list) {
            String str1 = String.valueOf(list[0]);
            String str2 = String.valueOf(list[1]);
            return !StringUtils.equals(str1, str2);
        }
    }

    private static class DifDateOperator extends Operator {
        @Override
        public Object executeInner(Object[] list) {
            return DateOperator.compareDays((Date) list[0], (Date) list[1]) + 1L;
        }
    }

    private static class AddDateOperator extends Operator {
        @Override
        public Object executeInner(Object[] list) {
            return DateOperator.addDays((Date) list[0], Integer.parseInt(list[1].toString()));
        }
    }

    public BigDecimal generateMidstream(ApplyCtrDCSX entity, ApplyMatch applyMatch, ApplyMatchDetail buyDetail,
                                        ApplyMatchDetail sellDetail){
        Long creditDays = entity.getCreditDays();
        String ourCompanyName = applyMatch.getOurCompanyName();
        String buyOurCompanyName = applyMatch.getBuyOurCompanyName();
        String sellOurCompanyName = applyMatch.getSellOurCompanyName();
        BigDecimal realCreditDays = (Objects.isNull(creditDays) || creditDays <= 0) ? BigDecimal.ONE : BigDecimal.valueOf(creditDays);
        BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(MIDSTREAM_EXPRESS, entity.getEnterpriseId());
        if (Objects.isNull(config) || StringUtils.isBlank(config.getConfigValue())) {
            logger.error("generateMidstream error MIDSTREAM_EXPRESS 配置项有误，无法计算中游合同单价!");
            return BigDecimal.ZERO;
        }
        try {
            String midstreamExpress = config.getConfigValue();
            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<>();
            context.put("entity", entity);
            context.put("match", applyMatch);
            context.put("buyDetail", buyDetail);
            context.put("sellDetail", sellDetail);
            context.put("我方", ourCompanyName);
            context.put("代采方", buyOurCompanyName);
            context.put("中游代采方", sellOurCompanyName);
            context.put("赊销模式", applyMatch.getContractModel());
            context.put("上游采购单价", buyDetail.getDealPrice());
            context.put("下游销售单价", sellDetail.getDealPrice());
            context.put("上游交货日期", buyDetail.getDeliveryDate());
            context.put("上游付全款日期", buyDetail.getPayFullTime());
            context.put("下游交货日期", sellDetail.getDeliveryDate());
            context.put("下游付全款日期", sellDetail.getReceiveFullTime());
            context.put("中游合同账期", realCreditDays);
            // 设置运算精度为10位小数
            context.put("$scale", 12);
            // 设置运算模式为四舍五入
            context.put("$roundingMode", BigDecimal.ROUND_HALF_UP);

            runner.addFunction("strEQ", new MidstreamUtil.EqOperator());
            runner.addFunction("strNEQ", new MidstreamUtil.NEqOperator());
            runner.addFunction("difDay", new MidstreamUtil.DifDateOperator());
            runner.addFunction("addDay", new MidstreamUtil.AddDateOperator());
            Object result = runner.execute(midstreamExpress, context, null, false, false);
            logger.info("generateMidstream result :{}", result);
            if (result instanceof Integer) {
                result = BigDecimal.valueOf((Integer) result);
                // 进一步处理
            }
            BigDecimal dealPrice = (BigDecimal) result;
            return dealPrice.setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            logger.error("generateMidstream error", e);
        }
        return BigDecimal.ZERO;
    }

    public String generateRespStr(String reqStr,String oldValue){
        BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(STR_RESP_EXPRESS, BasConstants.ZG_ENTERPRISE_ID);
        if (Objects.isNull(config) || StringUtils.isBlank(config.getConfigValue())) {
            return oldValue;
        }
        try {
            String strRespExpress = config.getConfigValue();
            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<>();
            context.put("oldStr", reqStr);
            runner.addFunction("strEQ", new MidstreamUtil.EqOperator());
            runner.addFunction("strNEQ", new MidstreamUtil.NEqOperator());
            runner.addFunction("difDay", new MidstreamUtil.DifDateOperator());
            runner.addFunction("addDay", new MidstreamUtil.AddDateOperator());
            Object result = runner.execute(strRespExpress, context, null, false, false);
            logger.info("generateRespStr result :{}", result);
            String respStr = (String) result;
            if(StringUtils.isNotBlank(respStr)){
                return respStr;
            } else {
                return oldValue;
            }
        } catch (Exception e) {
            logger.error("generateMidstream error", e);
        }
        return oldValue;
    }

    public BigDecimal generateFundRate(String companyName,String ourCompanyName,String contractType){
        BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(TP_FUND_RATE, BasConstants.ZG_ENTERPRISE_ID);
        if (Objects.isNull(config) || StringUtils.isBlank(config.getConfigValue())) {
            logger.error("generateMidstream error TP_FUND_RATE 配置项有误，无法计算中游合同单价!");
            return BigDecimal.ZERO;
        }
        try {
            String strRespExpress = config.getConfigValue();
            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<>();
            context.put("ourCompanyName", companyName);
            context.put("ourCompanyName", ourCompanyName);
            context.put("contractType", contractType);
            runner.addFunction("strEQ", new MidstreamUtil.EqOperator());
            runner.addFunction("strNEQ", new MidstreamUtil.NEqOperator());
            runner.addFunction("difDay", new MidstreamUtil.DifDateOperator());
            runner.addFunction("addDay", new MidstreamUtil.AddDateOperator());
            String result = (String) runner.execute(strRespExpress, context, null, false, false);
            logger.info("generateFundRate result :{}", result);
            BigDecimal rate = new BigDecimal(result);
            return rate.setScale(6, RoundingMode.HALF_UP);

        } catch (Exception e) {
            logger.error("generateMidstream error", e);
        }
        return BigDecimal.ZERO;
    }
}
