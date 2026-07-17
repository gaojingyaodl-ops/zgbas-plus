package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.RptCompanyCreditInfo0;
import com.spt.bas.report.client.vo.RptCompanyCreditVo;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditQueryVo;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditVo;
import com.spt.bas.report.client.vo.RptPartBsCompanyVo;
import com.spt.bas.report.server.dao.RptBsCompanyMapper;
import com.spt.bas.report.server.service.IRptBsCompanyService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RptBsCompanyServiceImpl implements IRptBsCompanyService {
    @Autowired
    private RptBsCompanyMapper bsCompanyDao;

    @Override
    public List<RptPartBsCompanyVo> findCompanyList(RptPartBsCompanyVo vo) {
        return buildCredit(bsCompanyDao.findCompanyList(vo));
    }

    @Override
    public RptPartBsCompanyVo findCompanyById(RptPartBsCompanyVo vo) {
        return buildCredit(bsCompanyDao.findCompanyById(vo));
    }

    @Override
    public List<RptPartBsCompanyVo> findCompany(RptPartBsCompanyVo vo) {
        return buildCredit(bsCompanyDao.findCompany(vo));
    }

    //查询有没有重名的企业
    @Override
    public int countCompanyByName(String companyName) {
        return bsCompanyDao.countCompanyByName(companyName);
    }

    @Override
    public List<Long> getRelationShipApproveIdByCompanyId(Long matchUserId) {
        return bsCompanyDao.getRelationShipApproveIdByCompanyId(matchUserId);
    }

    @Override
    public List<Long> getRelationShipApproveIdByCompanyIds(List<Long> matchUserIds) {
        return bsCompanyDao.getRelationShipApproveIdByCompanyIds(matchUserIds);
    }

    @Override
    public List<RptCompanyCreditInfo0> getCompanyCreditInfo0() {
        return bsCompanyDao.getCompanyCreditInfo0();
    }

    @Override
    public List<RptOpenCompanyCreditVo> findOpenCreditList(RptOpenCompanyCreditQueryVo vo) {
        return bsCompanyDao.findOpenCreditList(vo);
    }

    private List<RptPartBsCompanyVo> buildCredit(List<RptPartBsCompanyVo> resultList) {
        List<Long> companyIdList = resultList.stream().map(RptPartBsCompanyVo::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(companyIdList)) {
            return resultList;
        }
        List<RptCompanyCreditVo> companyCredit = bsCompanyDao.findCompanyCredit(new RptCompanyCreditVo(companyIdList));
        resultList.forEach(p -> {
            BigDecimal piccAmount = companyCredit.stream()
                    .filter(c -> Objects.equals(p.getId(), c.getCompanyId()))
                    .filter(c -> Objects.equals("0", c.getCreditType()))
                    .map(RptCompanyCreditVo::getRemainingAmount)
                    .findAny().orElse(BigDecimal.ZERO);

            BigDecimal daDiAmount = companyCredit.stream()
                    .filter(c -> Objects.equals(p.getId(), c.getCompanyId()))
                    .filter(c -> Objects.equals("1", c.getCreditType()))
                    .map(RptCompanyCreditVo::getRemainingAmount)
                    .findAny().orElse(BigDecimal.ZERO);

            BigDecimal zyAmount = companyCredit.stream()
                    .filter(c -> Objects.equals(p.getId(), c.getCompanyId()))
                    .filter(c -> Objects.equals("2", c.getCreditType()))
                    .map(RptCompanyCreditVo::getRemainingAmount)
                    .findAny().orElse(BigDecimal.ZERO);

            BigDecimal ourAmount = companyCredit.stream()
                    .filter(c -> Objects.equals(p.getId(), c.getCompanyId()))
                    .filter(c -> Objects.equals("9", c.getCreditType()))
                    .map(RptCompanyCreditVo::getRemainingAmount)
                    .findAny().orElse(BigDecimal.ZERO);
            p.setPiccAmount(piccAmount);
            p.setDaDiAmount(daDiAmount);
            p.setZyAmount(zyAmount);
            p.setOurAmount(ourAmount);
            RptCompanyCreditVo piccCompanyCredit = companyCredit.stream()
                    .filter(c -> Objects.equals(p.getId(), c.getCompanyId()))
                    .filter(c -> Objects.equals("0", c.getCreditType()))
                    .findAny()
                    .orElse(null);
            BigDecimal piccRiskAmount = BigDecimal.ZERO;
            BigDecimal piccTemporaryAmount = BigDecimal.ZERO;
            if (Objects.nonNull(piccCompanyCredit)) {
                piccRiskAmount = piccCompanyCredit.getRiskAmount();
                if (Objects.isNull(piccRiskAmount)) {
                    piccRiskAmount = BigDecimal.ZERO;
                }
                piccTemporaryAmount = piccCompanyCredit.getTemporaryAmount();
                if (Objects.isNull(piccTemporaryAmount)) {
                    piccTemporaryAmount = BigDecimal.ZERO;
                }
            }
            RptCompanyCreditVo daDiCompanyCredit = companyCredit.stream()
                    .filter(c -> Objects.equals(p.getId(), c.getCompanyId()))
                    .filter(c -> Objects.equals("1", c.getCreditType()))
                    .findAny()
                    .orElse(null);
            BigDecimal daDiRiskAmount = BigDecimal.ZERO;
            BigDecimal daDiTemporaryAmount = BigDecimal.ZERO;
            if (Objects.nonNull(daDiCompanyCredit)) {
                daDiRiskAmount = daDiCompanyCredit.getRiskAmount();
                if (Objects.isNull(daDiRiskAmount)) {
                    daDiRiskAmount = BigDecimal.ZERO;
                }
                daDiTemporaryAmount = daDiCompanyCredit.getTemporaryAmount();
                if (Objects.isNull(daDiTemporaryAmount)) {
                    daDiTemporaryAmount = BigDecimal.ZERO;
                }
            }
            RptCompanyCreditVo zhongYinCompanyCredit = companyCredit.stream()
                    .filter(c -> Objects.equals(p.getId(), c.getCompanyId()))
                    .filter(c -> Objects.equals("2", c.getCreditType()))
                    .findAny()
                    .orElse(null);
            BigDecimal zhongYinRiskAmount = BigDecimal.ZERO;
            BigDecimal zhongYinTemporaryAmount = BigDecimal.ZERO;
            if (Objects.nonNull(zhongYinCompanyCredit)) {
                zhongYinRiskAmount = zhongYinCompanyCredit.getRiskAmount();
                if (Objects.isNull(zhongYinRiskAmount)) {
                    zhongYinRiskAmount = BigDecimal.ZERO;
                }
                zhongYinTemporaryAmount = zhongYinCompanyCredit.getTemporaryAmount();
                if (Objects.isNull(zhongYinTemporaryAmount)) {
                    zhongYinTemporaryAmount = BigDecimal.ZERO;
                }
            }
            
            
            RptCompanyCreditVo ourCompanyCredit = companyCredit.stream()
                    .filter(c -> Objects.equals(p.getId(), c.getCompanyId()))
                    .filter(c -> Objects.equals("1", c.getCreditType()))
                    .findAny()
                    .orElse(null);
            BigDecimal ourRiskAmount = BigDecimal.ZERO;
            BigDecimal ourTemporaryAmount = BigDecimal.ZERO;
            if (Objects.nonNull(ourCompanyCredit)) {
                ourRiskAmount = ourCompanyCredit.getRiskAmount();
                if (Objects.isNull(ourRiskAmount)) {
                    ourRiskAmount = BigDecimal.ZERO;
                }
                ourTemporaryAmount = ourCompanyCredit.getTemporaryAmount();
                if (Objects.isNull(ourTemporaryAmount)) {
                    ourTemporaryAmount = BigDecimal.ZERO;
                }
            }
            
            p.setPiccRiskAmount(piccRiskAmount);
            p.setDaDiRiskAmount(daDiRiskAmount);
            p.setZhongYinRiskAmount(zhongYinRiskAmount);
            p.setOurRiskAmount(ourRiskAmount);
            p.setPiccTemporaryAmount(piccTemporaryAmount);
            p.setDaDiTemporaryAmount(daDiTemporaryAmount);
            p.setZhongYinTemporaryAmount(zhongYinTemporaryAmount);
            p.setOurTemporaryAmount(ourTemporaryAmount);
        });
        return resultList;
    }

    private RptPartBsCompanyVo buildCredit(RptPartBsCompanyVo partBsCompanyVo) {
        
        if (Objects.isNull(partBsCompanyVo)) {
            return null;
        }
        Long companyId = partBsCompanyVo.getId();
        if (Objects.isNull(companyId)) {
            return partBsCompanyVo;
        }
        List<Long> companyIdList = new java.util.ArrayList<>();
        companyIdList.add(companyId);
        List<RptCompanyCreditVo> companyCredit = bsCompanyDao.findCompanyCredit(new RptCompanyCreditVo(companyIdList));
        BigDecimal piccAmount = companyCredit.stream()
                .filter(c -> Objects.equals(partBsCompanyVo.getId(), c.getCompanyId()))
                .filter(c -> Objects.equals("0", c.getCreditType()))
                .map(RptCompanyCreditVo::getRemainingAmount)
                .findAny().orElse(BigDecimal.ZERO);
        BigDecimal daDiAmount = companyCredit.stream()
                .filter(c -> Objects.equals(partBsCompanyVo.getId(), c.getCompanyId()))
                .filter(c -> Objects.equals("1", c.getCreditType()))
                .map(RptCompanyCreditVo::getRemainingAmount)
                .findAny().orElse(BigDecimal.ZERO);

        BigDecimal ourAmount = companyCredit.stream()
                .filter(c -> Objects.equals(partBsCompanyVo.getId(), c.getCompanyId()))
                .filter(c -> Objects.equals("9", c.getCreditType()))
                .map(RptCompanyCreditVo::getRemainingAmount)
                .findAny().orElse(BigDecimal.ZERO);
        partBsCompanyVo.setPiccAmount(piccAmount);
        partBsCompanyVo.setDaDiAmount(daDiAmount);
        partBsCompanyVo.setOurAmount(ourAmount);
        RptCompanyCreditVo piccCompanyCredit = companyCredit.stream()
                .filter(c -> Objects.equals(partBsCompanyVo.getId(), c.getCompanyId()))
                .filter(c -> Objects.equals("0", c.getCreditType()))
                .findAny()
                .orElse(null);
        BigDecimal piccRiskAmount = BigDecimal.ZERO;
        BigDecimal piccTemporaryAmount = BigDecimal.ZERO;
        if (Objects.nonNull(piccCompanyCredit)) {
            piccRiskAmount = piccCompanyCredit.getRiskAmount();
            if (Objects.isNull(piccRiskAmount)) {
                piccRiskAmount = BigDecimal.ZERO;
            }
            piccTemporaryAmount = piccCompanyCredit.getTemporaryAmount();
            if (Objects.isNull(piccTemporaryAmount)) {
                piccTemporaryAmount = BigDecimal.ZERO;
            }
        }
        RptCompanyCreditVo daDiCompanyCredit = companyCredit.stream()
                .filter(c -> Objects.equals(partBsCompanyVo.getId(), c.getCompanyId()))
                .filter(c -> Objects.equals("1", c.getCreditType()))
                .findAny()
                .orElse(null);
        BigDecimal daDiRiskAmount = BigDecimal.ZERO;
        BigDecimal daDiTemporaryAmount = BigDecimal.ZERO;
        if (Objects.nonNull(daDiCompanyCredit)) {
            daDiRiskAmount = daDiCompanyCredit.getRiskAmount();
            if (Objects.isNull(daDiRiskAmount)) {
                daDiRiskAmount = BigDecimal.ZERO;
            }
            daDiTemporaryAmount = daDiCompanyCredit.getTemporaryAmount();
            if (Objects.isNull(daDiTemporaryAmount)) {
                daDiTemporaryAmount = BigDecimal.ZERO;
            }
        }
        RptCompanyCreditVo ourCompanyCredit = companyCredit.stream()
                .filter(c -> Objects.equals(partBsCompanyVo.getId(), c.getCompanyId()))
                .filter(c -> Objects.equals("1", c.getCreditType()))
                .findAny()
                .orElse(null);
        BigDecimal ourRiskAmount = BigDecimal.ZERO;
        BigDecimal ourTemporaryAmount = BigDecimal.ZERO;
        if (Objects.nonNull(ourCompanyCredit)) {
            ourRiskAmount = ourCompanyCredit.getRiskAmount();
            if (Objects.isNull(ourRiskAmount)) {
                ourRiskAmount = BigDecimal.ZERO;
            }
            ourTemporaryAmount = ourCompanyCredit.getTemporaryAmount();
            if (Objects.isNull(ourTemporaryAmount)) {
                ourTemporaryAmount = BigDecimal.ZERO;
            }
        }

        partBsCompanyVo.setPiccRiskAmount(piccRiskAmount);
        partBsCompanyVo.setDaDiRiskAmount(daDiRiskAmount);
        partBsCompanyVo.setOurRiskAmount(ourRiskAmount);
        partBsCompanyVo.setPiccTemporaryAmount(piccTemporaryAmount);
        partBsCompanyVo.setDaDiTemporaryAmount(daDiTemporaryAmount);
        partBsCompanyVo.setOurTemporaryAmount(ourTemporaryAmount);
        
        return partBsCompanyVo;
    }
}
