package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BizSign;
import com.spt.bas.client.entity.BizSignDetail;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.sign.BizSignDao;
import com.spt.bas.server.dao.sign.BizSignDetailDao;
import com.spt.bas.server.service.IBizSignService;
import com.spt.sign.client.cfca.CfcaResp;
import com.spt.sign.client.remote.ICfcaSignClient;
import com.spt.sign.client.vo.AxqAutoSignVo;
import com.spt.sign.client.vo.AxqContractVo;
import com.spt.sign.client.vo.AxqUploadVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author MoonLight
 * @Date 2024/10/28 14:17
 * @Version 1.0
 */
@Slf4j
@Component
public class BizSignServiceImpl extends BaseService<BizSign> implements IBizSignService {
    @Resource
    private BizSignDao bizSignDao;
    @Resource
    private BizSignDetailDao bizSignDetailDao;
    @Resource
    private ICfcaSignClient cfcaSignClient;

    @Override
    public BaseDao<BizSign> getBaseDao() {
        return bizSignDao;
    }

    @Override
    @ServerTransactional
    public void generateSign(BizSign bizSign) throws ApplicationException {
        List<BizSignDetail> bizSignDetailList = bizSign.getBizSignDetailList();
        if (StringUtils.isBlank(bizSign.getSignFileId())){
            throw new ApplicationException("signFileId 签署原文附件不可为空!");
        }
        if (CollectionUtils.isEmpty(bizSignDetailList)) {
            throw new ApplicationException("bizSignDetail 签署方明细配置不可为空!");
        }
        List<AxqContractVo> axqContractVoList = convertSignParam(bizSign);
        log.info("generateSign prams:{}", JsonUtil.obj2Json(axqContractVoList));
        AxqUploadVo axqUploadVo = cfcaSignClient.axqUploadContractSigned(axqContractVoList);
        log.info("generateSign axqUploadContractSigned:{}", JsonUtil.obj2Json(axqUploadVo));
        if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
            String cfcaContractNo = axqUploadVo.getContractNo();
            bizSign.setCfcaContractNo(cfcaContractNo);
            BizSign saveSign = bizSignDao.save(bizSign);

            bizSignDetailList.forEach(b -> b.setBizSignId(saveSign.getId()));
            bizSignDetailDao.saveAll(bizSignDetailList);
        } else {
            log.error("generateSign 安心签上传补充协议签署创建失败 result:{}", axqUploadVo);
        }
    }

    @Override
    @ServerTransactional
    public void successBizSign(Long approveId) {
        List<BizSign> signList = this.getSignList(approveId);
        signList.forEach(bizSign -> {
            String cfcaFileId = this.autoSignContract(bizSign);
            if (StringUtils.isNotBlank(cfcaFileId)) {
                bizSign.setCfcaFileId(cfcaFileId);
                bizSign.setSignStatus("D");
                bizSignDao.save(bizSign);
            }
        });
    }

    @Override
    public List<BizSign> getSignList(Long approveId) {
        List<BizSign> bizSignList = bizSignDao.findByApproveId(approveId);
        bizSignList.forEach(b -> {
            List<BizSignDetail> bizSignDetailList = bizSignDetailDao.findByBizSignId(b.getId());
            b.setBizSignDetailList(bizSignDetailList);
        });
        return bizSignList;
    }

    private String autoSignContract(BizSign bizSign){
        AxqAutoSignVo axqAutoSignVo = new AxqAutoSignVo();
        axqAutoSignVo.setCfcaContractNo(bizSign.getCfcaContractNo());
        List<AxqAutoSignVo.Signatorie> signatorieList = new ArrayList<>();
        bizSign.getBizSignDetailList().forEach(d -> signatorieList.add(buildAutoSingParam(d)));
        axqAutoSignVo.setSignatorieList(signatorieList);
        CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
        if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
            String fileIdStr = cfcaResp.getData();
            if (StringUtils.isNotBlank(fileIdStr)) {
                return fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
            }
        }
        return "";
    }

    private AxqAutoSignVo.Signatorie buildAutoSingParam(BizSignDetail bizSignDetail) {
        AxqAutoSignVo.Signatorie signatorie = new AxqAutoSignVo.Signatorie();
        signatorie.setCompanyName(bizSignDetail.getSignCompanyName());
        signatorie.setKeyWord(bizSignDetail.getSignKeyWord());
        signatorie.setSealType(bizSignDetail.getSignSealType());
        signatorie.setImageHeight(bizSignDetail.getSignImageHeight());
        signatorie.setImageWidth(bizSignDetail.getSignImageWidth());
        signatorie.setOffsetCoordX(bizSignDetail.getSignOffsetCoordX());
        signatorie.setOffsetCoordY(bizSignDetail.getSignOffsetCoordY());
        return signatorie;
    }

    private List<AxqContractVo> convertSignParam(BizSign bizSign){
        List<AxqContractVo> axqContractVoList = new ArrayList<>();
        String signFileId = bizSign.getSignFileId();
        String signFileName = bizSign.getSignFileName() + bizSign.getContractId();
        List<BizSignDetail> bizSignDetailList = bizSign.getBizSignDetailList();
        bizSignDetailList.forEach(s->{
            AxqContractVo vo = new AxqContractVo();
            vo.setSignKeyword(s.getSignKeyWord());
            vo.setCfcaTemplateName(signFileName);
            vo.setBuyerCompanyName(s.getSignCompanyName());
            vo.setSignType(StringUtils.isNotBlank(s.getSignSealType()) ? s.getSignSealType() : "CTR");
            vo.setFileId(signFileId);
            if (StringUtils.isNotBlank(signFileId) && signFileId.endsWith(",")) {
                vo.setFileId(signFileId.replaceAll(",", ""));
            }
            vo.setGenerateShortUrlFlg(false);
            vo.setProductCode("003");
            axqContractVoList.add(vo);
        });
        return axqContractVoList;
    }
}
