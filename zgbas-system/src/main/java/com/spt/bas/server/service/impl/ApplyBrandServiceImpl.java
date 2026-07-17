package com.spt.bas.server.service.impl;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.server.dao.ApplyBrandDao;
import com.spt.bas.server.service.IApplyBrandService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("applyBrandService")
public class ApplyBrandServiceImpl extends BaseService<BasBrand> implements IApplyBrandService, IPmApproveListener {

    @Autowired
    private ApplyBrandDao applyBrandDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;


    @Override
    public BaseDao<BasBrand> getBaseDao() {
        return applyBrandDao;
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            BasBrand basBrand = JsonUtil.json2Object(BasBrand.class, contents);
            basBrand.setApproveId(approve.getId());//审批id
            basBrand.setId(0L);//编号
            basBrand.setCreatedDate(approve.getCreatedDate());//创建时间
            basBrand.setUpdatedDate(approve.getUpdatedDate());//更新时间
            save(basBrand);
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
      return;
    }

}
