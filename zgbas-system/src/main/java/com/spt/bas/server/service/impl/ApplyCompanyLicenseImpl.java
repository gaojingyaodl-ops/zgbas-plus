package com.spt.bas.server.service.impl;

import com.hsoft.file.sdk.util.Base64Utility;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IFileRecordClient;
import com.spt.bas.server.dao.ApplyCompanyLicenseDao;
import com.spt.bas.server.dao.BsCompanyOurDao;
import com.spt.bas.server.service.IApplyCompanyLicenseService;
import com.spt.bas.server.service.IApproveWaitDealService;
import com.spt.bas.server.service.ICompanyLicenseService;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.http.util.TokenUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;


@Component("applyLicenseService")
@Transactional
public class ApplyCompanyLicenseImpl extends BaseService<ApplyCompanyLicense> implements IPmService,IApplyCompanyLicenseService, IPmApproveListener {
    @Autowired
    private ApplyCompanyLicenseDao applyCompanyLicenseDao;
    @Autowired
    private BsCompanyOurDao bsCompanyOurDao;
    @Autowired
    private IApproveWaitDealService approveWaitDealService;
    @Value("${res.fileView}")
    private String kkFileViewUrl;
    @Value("${file.show.url}")
    private String fileUrl;
    @Value("${share.key}")
    private String SHARE_KEY;
    @Autowired
    private IFileRecordClient fileRecordClient;
    @Autowired
    private ICompanyLicenseService companyLicenseService;

    @Override
    public BaseDao<ApplyCompanyLicense> getBaseDao() {
        return applyCompanyLicenseDao;
    }
    @Override
    public Class<ApplyCompanyLicense> getEntityClazz() {
        return ApplyCompanyLicense.class;
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyCompanyLicense entity = getEntity(approve.getBizId());
        entity.setUpdatedDate(approve.getUpdatedDate());//更新时间
        entity.setStatus(approve.getStatus());
        save(entity);
        if (StringUtils.equals(approve.getStatus(), BasConstants.APPROVE_STATUS_D)) {
            List<CompanyLicense> licenseList = downloadPicUrl(entity);
            approveWaitDealService.addCompanyLicenseFile(entity, licenseList);
        }
    }


    /**
     * 生成kkfileview 预览
     *
     * @param fileId       文件id
     * @param watermarkTxt 水印信息
     * @param periodDate   过期时间
     * @return 预览地址
     * @throws UnsupportedEncodingException 异常
     */
    private String generateUrl(String fileId, String watermarkTxt, Date periodDate)  {
        Map<String, Object> param = new HashMap<>();
        FileRecord file = fileRecordClient.findByFileId(fileId);
        String fileName = file.getFileName();
        String expireDate = DateOperator.formatDate(periodDate) + " 23:59:59";
        param.put("fileId", fileId);
        param.put("expireDate", expireDate);
        String token = TokenUtil.createToken(param, SHARE_KEY);
        String url = fileUrl + "/view/share/" + token + "?fullfilename=" + fileName;
        String showUrl;
        try {
            showUrl = kkFileViewUrl + URLEncoder.encode(Base64Utility.base64Encode(url.getBytes(StandardCharsets.UTF_8)), "UTF-8");
            return MessageFormat.format("{0}&watermarkTxt={1}", showUrl, URLEncoder.encode(watermarkTxt, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("预览地址encode异常：{}", e.toString());
            return null;
        }
    }

    @Override
    public List<CompanyLicense> downloadPicUrl(ApplyCompanyLicense license) {
        List<CompanyLicense> licenseList = companyLicenseService.getCodeAndFileType(license.getCompanyCode(), license.getFileType());
        List<BsDictData> list = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.COMPANY_LICENSE_USER_TYPE);
        Map<String, String> map = list.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getDictName, (a, b) -> b));
        if (CollectionUtils.isNotEmpty(licenseList)) {
            for (CompanyLicense companyLicense : licenseList) {
                String watermarkTxt = MessageFormat.format("{0} 仅限{1}使用 限用至{2}", license.getApplyUserName(), map.get(license.getUseType()), DateOperator.formatDate(license.getPeriodDate()));
                String fileId = companyLicense.getFileId();
                if (fileId.contains(",")) {
                    fileId = fileId.substring(0, fileId.indexOf(","));
                }
                companyLicense.setUrl(generateUrl(fileId, watermarkTxt, license.getPeriodDate()));
            }
        }
        return licenseList;
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    @ServiceTransactional
    public void updateFileId(Long id, String fileId) {
        applyCompanyLicenseDao.updateFileId(id, fileId);
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyCompanyLicense entity = (ApplyCompanyLicense) pmEntity;
            return save(entity);
        }
        return null;
    }

    /**
     * 标题
     *
     * @param pmEntity
     * @param pmProcess
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        if (pmEntity instanceof ApplyCompanyLicense) {
            ApplyCompanyLicense license = (ApplyCompanyLicense) pmEntity;
            BsCompanyOur companyOur = bsCompanyOurDao.findByCompanyName(license.getCompanyName());
            String fileType = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BsDictConstants.COMPANY_LICENSE_FILE_TYPE, license.getFileType());
            String userType = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BsDictConstants.COMPANY_LICENSE_USER_TYPE, license.getUseType());
            return SubjectUtil.formatSubject(companyOur.getCompanyAbbr(), fileType, userType, DateOperator.formatDate(license.getPeriodDate()));
        }
        return null;
    }

    /**
     * 获取业务员id
     *
     * @param pmEntity
     */
    @Override
    public Long getMatchUserId(IPmEntity pmEntity) {
        return IPmService.super.getMatchUserId(pmEntity);
    }
}
