package com.spt.bas.server.service;

import java.util.List;
import java.util.Map;

import com.spt.tools.core.exception.ApplicationException;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.vo.ApplyDeliveryApplyIdVo;
import com.spt.bas.client.vo.ApplyMatchDetailVo;
import com.spt.bas.client.vo.ApplyMatchVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyProductDetailService extends IBaseService<ApplyProductDetail> {

    List<ApplyProductDetail> findApplyDetail(Long applyId, String applyType);

    List<ApplyProductDetail> saveDetailBatch(List<ApplyProductDetail> lstInsert, List<ApplyProductDetail> lstUpdate,
                                             List<ApplyProductDetail> lstDelete, ApplyProductDetailSaveVo vo) throws ApplicationException;

    ApplyProductDetail findEntityByParam(Map<String, Object> queryParams);

    List<ApplyProductDetail> findApplyId(@RequestBody ApplyDeliveryApplyIdVo vo);

    void saveBatchEnterpriseId(ApplyProductDetailSaveVo vo);

    void saveBatchByCtrProductList(List<CtrProduct> list, ApplyProductDetailSaveVo vo);

    void saveBystockDetail(ApplyProductDetailSaveVo vo, Long stockDetailId);

    List<Object[]> sumApplyDetail(Long id, String applyTypeI);

    ApplyProductDetail saveDetailMatch(ApplyMatchDetailVo detailVo, ApplyMatchVo matchVo, ApplyProductDetailSaveVo vo);

    List<ApplyProductDetail> findByProductName(String  productName);
}

