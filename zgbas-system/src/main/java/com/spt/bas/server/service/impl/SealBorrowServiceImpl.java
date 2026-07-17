package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealBorrow;
import com.spt.bas.client.entity.SealBorrowOphis;
import com.spt.bas.client.vo.SealBorrowSearchVo;
import com.spt.bas.client.vo.SealBorrowVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.SealBorrowDao;
import com.spt.bas.server.dao.SealBorrowOphisDao;
import com.spt.bas.server.service.ISealBorrowService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component("sealBorrowService")
@Transactional(readOnly = true)
public class SealBorrowServiceImpl extends BaseService<SealBorrow> implements ISealBorrowService, IPmApproveListener {
    @Autowired
    private SealBorrowDao sealBorrowDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private SealBorrowOphisDao sealBorrowOphisDao;

    @Autowired
    private IPmApproveService pmApproveService;

    @Autowired
    private IPmProcessService pmProcessService;

    @Override
    public BaseDao<SealBorrow> getBaseDao() {
        return sealBorrowDao;
    }

    @Override
    public Class<SealBorrow> getEntityClazz() {
        return SealBorrow.class;
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            SealBorrow entity = JsonUtil.json2Object(SealBorrow.class, contents);
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setSealStatus(approve.getStatus());
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            entity.setFileId(pmApproveContents.getFileId());
            save(entity);
        }

    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public Page<SealBorrow> findBorrowPage(SealBorrowSearchVo searchVo) {
        Sort sort = Sort.by(Direction.DESC, "id");
        Map<String, Object> searchParams = searchVo.getSearchParams();
        Specification<SealBorrow> spec = WebUtil.buildSpecification(searchParams);
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
        Page<SealBorrow> page = getBaseDao().findAll(spec, pageRequest);

        PageRequest pageRequest_new = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<SealBorrow> pageVo = new PageImpl<>(page.getContent(), pageRequest_new, page.getTotalElements());
        return pageVo;
    }

    @Override
    @ServerTransactional
    public void updateSealBorrow(SealBorrowVo borrowVo) {
        Long sealBorrowId = borrowVo.getSealBorrowId();
        String opType = borrowVo.getOpType();
        String returnItemType = borrowVo.getReturnItemType();
        SealBorrow sealBorrow = sealBorrowDao.findOne(sealBorrowId);
        // 1.修改印章外借状态
        if (sealBorrow != null) {
            if (StringUtils.equals(BasConstants.DICT_TYPE_SEAL_STATUS_R, opType)) {
                sealBorrow.setReturnDate(new Date());
            }
            String alreadyReturn = sealBorrow.getAlreadyReturn();
            if (StringUtils.isNotBlank(alreadyReturn)) {
                returnItemType = returnItemType + "," + alreadyReturn;
            }
            String itemType = sealBorrow.getItemType();
            String[] itemTypeArr = itemType.split(",");
            String[] returnItemTypeArr = returnItemType.split(",");
            Arrays.sort(itemTypeArr);
            Arrays.sort(returnItemTypeArr);
            if (Arrays.equals(itemTypeArr, returnItemTypeArr)) {
                sealBorrow.setSealStatus(opType);
            }
            sealBorrow.setAlreadyReturn(returnItemType);
        }
        sealBorrowDao.save(sealBorrow);
        // 2.生成印章外借操作记录
        SealBorrowOphis ophis = new SealBorrowOphis();
        ophis.setSealBorrowId(sealBorrowId);
        ophis.setOpUserId(borrowVo.getOpUserId());
        ophis.setOpUserName(borrowVo.getOpUserName());
        ophis.setOpType(borrowVo.getOpType());
        ophis.setEnterpriseId(sealBorrow.getEnterpriseId());
        ophis.setItemType(borrowVo.getReturnItemType());
        ophis.setRemark(borrowVo.getRemark());
        sealBorrowOphisDao.save(ophis);

    }

    @Override
    @ServiceTransactional
    public void doSealBorrowTask() {
        List<SealBorrow> sealBorrowList = sealBorrowDao.findSealBorrowOverdue();
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
            int taskSize = sealBorrowList.size();
            float bathSize = 50F;
            int bath = (int) Math.ceil((double) (taskSize / bathSize));
            for (int i = 0; i < bath; i++) {
                int start = (int) (bathSize * i);
                int end = (int) (start + bathSize);
                end = end > taskSize ? taskSize : end;
                List<SealBorrow> list = sealBorrowList.subList(start, end);
                execu.submit(() -> {
                    for (SealBorrow borrow : list) {
                        borrow.setSealStatus(BasConstants.DICT_TYPE_SEAL_STATUS_L);
                        sealBorrowDao.save(borrow);

                        SealBorrowOphis ophis = new SealBorrowOphis();
                        ophis.setSealBorrowId(borrow.getId());
                        ophis.setOpUserId(0L);
                        ophis.setOpUserName("系统任务");
                        ophis.setOpType(BasConstants.DICT_TYPE_SEAL_STATUS_L);
                        ophis.setEnterpriseId(borrow.getEnterpriseId());
                        sealBorrowOphisDao.save(ophis);

                    }
                    return "OK";
                });
            }
            for (int i = 0; i < bath; i++) {
                Future<String> future = execu.take();
                logger.info("doSealBorrowTask:{},{}", i, future.get());
            }
            executorService.shutdown();
        } catch (Exception e) {
            logger.info("更新印章外借状态定时任务异常!", e.getMessage());
        }
    }
}

