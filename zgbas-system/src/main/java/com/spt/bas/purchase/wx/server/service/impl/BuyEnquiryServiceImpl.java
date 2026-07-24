package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.purchase.wx.client.entity.BuyEnquiry;
import com.spt.bas.purchase.wx.client.entity.BuyQuote;
import com.spt.bas.purchase.wx.client.vo.BuyEnquirySearchVo;
import com.spt.bas.purchase.wx.server.cache.BsDictUtil;
import com.spt.bas.purchase.wx.server.dao.BuyEnquiryDao;
import com.spt.bas.purchase.wx.server.dao.BuyQuoteDao;
import com.spt.bas.purchase.wx.server.enums.MessageEnums;
import com.spt.bas.purchase.wx.server.service.IBuyEnquiryService;
import com.spt.bas.purchase.wx.server.service.IBuyMessageService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/12/21 14:09
 */
@Service
@Slf4j
public class BuyEnquiryServiceImpl extends BaseService<BuyEnquiry> implements IBuyEnquiryService {

    @Autowired
    private BuyEnquiryDao buyEnquiryDao;
    @Autowired
    private IBsCompanyClient companyClient;
    @Autowired
    private BuyQuoteDao buyQuoteDao;
    @Autowired
    private IBuyMessageService messageService;
    @Autowired
    private BuyQuoteServiceImpl buyQuoteService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveBuyEnquiry(BuyEnquiry buyEnquiry) {
        String title = "";
        // 通知标题，判断新增还是变更
        if (Objects.isNull(buyEnquiry.getId())) {
            title = "询价通知";
        } else {
            title = "询价变更通知";
        }
        // 单号生成规则生成
        String oddNumber = generateOddNumber();
        buyEnquiry.setOddNumber(oddNumber);
        String companyName = buyEnquiry.getCompanyName();
        if (StringUtils.isNotEmpty(companyName)) {
            BsCompany company = companyClient.findByCompanyName(companyName);
            if (Objects.nonNull(company)) {
                buyEnquiry.setCompanyId(company.getId());
                buyEnquiry.setPiccCreditAmount(company.getPiccCreditAmount());
            }
        }
        BuyEnquiry entity = buyEnquiryDao.save(buyEnquiry);
        MessageEnums.EnquiryMessage message = new MessageEnums.EnquiryMessage.Builder()
                .odderNumber(buyEnquiry.getOddNumber())
                .productName(buyEnquiry.getProductName() + "/" + buyEnquiry.getBrandNumber() + "/" + buyEnquiry.getFactoryName())// 产品名称
                .dealNumber(buyEnquiry.getDealNumber())
                .enquiryId(entity.getId())
                .arriveDate(buyEnquiry.getArriveDate())
                .paymentDays(buyEnquiry.getPaymentDays())
                .expireTime(buyEnquiry.getExpireTime())
                .build();
        BsDictData bsDictData = BsDictUtil.getBsDictData(BasConstants.EWECHAT_MESSAGE_DEPT, BasConstants.MESSAGE_PUCH_DEPT);
        if (Objects.isNull(bsDictData) || StringUtils.isBlank(bsDictData.getDictName())) {
            logger.error("询价消息推送到企业微信失败原因，数据字典获取失败！");
        }
        Set<String> pushList = getPushList(Long.valueOf(bsDictData.getDictName()));
        Set<String> pushBlackList = getPushBlackList();
        Set<String> pushWhiteList = getPushWhiteList();
        if (CollectionUtils.isNotEmpty(pushList)) {
            pushList.removeAll(pushBlackList);
        }
        if (CollectionUtils.isNotEmpty(pushWhiteList)) {
            pushList.addAll(pushWhiteList);
        }
        messageService.sendEWxEnquiryMessage(message, pushList,title);
        return entity.getId();
    }

    /**
     * 单号生成规则 XyyMMdd001,XyyMMdd002
     */
    public String generateOddNumber() {
        // 规定的前缀
        String prefix = "X";
        // 获取当前年、月、日
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        String datePart = dateFormat.format(new Date());
        // 从数据库中获取最大的odd_number，如果没有则从001开始
        int lastOddNumber = buyEnquiryDao.getOddNumber(prefix + datePart);
        // 格式化序列号为三位数，不足三位时补零
        String formattedLastOddNumber = String.format("%03d", lastOddNumber);
        // 拼接生成的odd_number
        String generatedOddNumber = prefix + datePart + formattedLastOddNumber;
        return generatedOddNumber;
    }

    /**
     * 根据指定部门获取推送名单
     *
     * @param deptId 指定部门id
     * @return 推送名单
     */
    private Set<String> getPushList(Long deptId) {
        List<SysUserSdk> pushUserList = authOpenFacade.findByDeptIds(Collections.singletonList(deptId));
        return pushUserList.stream().map(SysUserSdk::getUserName).collect(Collectors.toSet());
    }

    /**
     * 获取推送白名单
     *
     * @return 白名单
     */
    private Set<String> getPushWhiteList() {
        List<BsDictData> whiteList = BsDictUtil.getListByCategory(BasConstants.MESSAGE_WHITE_LIST);
        return whiteList.stream().map(BsDictData::getDictCd).collect(Collectors.toSet());
    }

    /**
     * 获取推送黑名单
     *
     * @return 黑名单
     */
    private Set<String> getPushBlackList() {
        List<BsDictData> blackList = BsDictUtil.getListByCategory(BasConstants.MESSAGE_BLACK_LIST);
        return blackList.stream().map(BsDictData::getDictCd).collect(Collectors.toSet());
    }

    @Override
    public List<BuyEnquiry> findBuyEnquiryList(BuyEnquirySearchVo searchVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);//分页
        Map<String, Object> searchParams = new HashMap<>();
        // 未删除
        searchParams.put("EQB_deleteFlg", false);
        // 获取自己公司的信息
        searchParams.put("EQS_companyName", searchVo.getCompanyName());
        if (StringUtils.isNotEmpty(searchVo.getStatus())) {
            searchParams.put("EQS_status", searchVo.getStatus());
        }
        // 新增模糊查询条件
        if (StringUtils.isNotEmpty(searchVo.getOddNumber())) {
            searchParams.put("LIKES_oddNumber", searchVo.getOddNumber());
        }
        Specification<BuyEnquiry> spec = WebUtil.buildSpecification(searchParams);
        Page<BuyEnquiry> page = getBaseDao().findAll(spec, pageRequest);
        List<BuyEnquiry> content = page.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            for (BuyEnquiry buyEnquiry : content) {
                // 获取报价列表数据,获取报价列表数量
                List<BuyQuote> effectiveMin = buyQuoteService.getEffectiveMin(buyEnquiry.getId());
                if (!effectiveMin.isEmpty()) {
                    buyEnquiry.setEffectiveQuoteNum(effectiveMin.size());
                    //判断是否存在报价已经成交(控制前端编辑按钮)
                    boolean b = effectiveMin.stream().anyMatch(it -> it.getStatus().equals("1"));
                    if (b) {
                        // 有
                        buyEnquiry.setDealOffer(1);
                    } else {
                        // 无
                        buyEnquiry.setDealOffer(0);
                    }
                    // 获取剩余待成交数量
                    BigDecimal reduce = effectiveMin.stream()
                            .filter(it -> it.getStatus().equals("1"))
                            .map(BuyQuote::getDealNumber)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    // 报价数量-已成交数量
                    buyEnquiry.setSurplusNum(buyEnquiry.getDealNumber().subtract(reduce));
                } else {
                    // 如果没有报价信息，默认待成交数为询价吨数
                    buyEnquiry.setSurplusNum(buyEnquiry.getDealNumber());
                }
                //失效日期大于当前日期，未失效
                if (buyEnquiry.getExpireTime().compareTo(new Date()) > 0) {
                    buyEnquiry.setIsValidFlag(0);
                } else {
                    buyEnquiry.setIsValidFlag(1);
                }
            }
        }
        return content;
    }

    @Override
    public BuyEnquiry findBuyEnquiryById(Long id) {
        BuyEnquiry buyEnquiry = buyEnquiryDao.findOne(id);
        // 获取报价列表数据,获取报价列表数量
        List<BuyQuote> effectiveMin = buyQuoteService.getEffectiveMin(buyEnquiry.getId());
        if (!effectiveMin.isEmpty()) {
            buyEnquiry.setEffectiveQuoteNum(effectiveMin.size());
            //判断是否存在报价已经成交(控制前端编辑按钮)
            boolean b = effectiveMin.stream().anyMatch(it -> it.getStatus().equals("1"));
            if (b) {
                // 有
                buyEnquiry.setDealOffer(1);
            } else {
                // 无
                buyEnquiry.setDealOffer(0);
            }
            // 获取剩余待成交数量
            BigDecimal reduce = effectiveMin.stream()
                    .filter(it -> it.getStatus().equals("1"))
                    .map(BuyQuote::getDealNumber)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 报价数量-已成交数量
            buyEnquiry.setSurplusNum(buyEnquiry.getDealNumber().subtract(reduce));

        } else {
            // 如果没有报价信息，默认待成交数为询价吨数
            buyEnquiry.setSurplusNum(buyEnquiry.getDealNumber());
        }
        //失效日期大于当前日期，未失效
        if (buyEnquiry.getExpireTime().compareTo(new Date()) > 0) {
            buyEnquiry.setIsValidFlag(0);
        } else {
            buyEnquiry.setIsValidFlag(1);
        }

        return buyEnquiry;

    }

    @Override
    public Long updateBuyEnquiry(BuyEnquiry buyEnquiry) {
        BuyEnquiry entity = buyEnquiryDao.findOne(buyEnquiry.getId());
        if (Objects.nonNull(entity)) {
            BeanUtils.copyProperties(buyEnquiry, entity);
            buyQuoteDao.updateDeleteFlgByEnquiryId(entity.getId(), true);
        }
        BuyEnquiry save = buyEnquiryDao.save(entity);
        return save.getId();
    }

    @Override
    public void deleteBuyEnquiry(BuyEnquiry buyEnquiry) {
        if (buyEnquiry.getId() != null) {
            buyEnquiryDao.updateDeleteFlg(buyEnquiry.getId(), true);
            buyQuoteDao.updateDeleteFlgByEnquiryId(buyEnquiry.getId(), true);
        }
    }

    // 获取询价对应的报价成交数量，确认校验
    @Override
    public BigDecimal getTransactionNum(Long enquiryId) {
        return buyEnquiryDao.getTransactionNum(enquiryId);
    }


    @Override
    public BaseDao<BuyEnquiry> getBaseDao() {
        return buyEnquiryDao;
    }
}
