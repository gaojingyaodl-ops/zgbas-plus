package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractDelivery;
import com.spt.bas.client.remote.ICtrContractDeliveryClient;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.CtrContractDeliveryVo;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrContractDeliveryDao;
import com.spt.bas.server.service.ICtrContractDeliveryService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class CtrContractDeliveryServiceImpl extends BaseService<CtrContractDelivery> implements ICtrContractDeliveryService {
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrContractDeliveryDao ctrContractDeliveryDao;
    @Autowired
    private ICtrContractDeliveryClient ctrContractDeliveryClient;
    @Resource
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseDao<CtrContractDelivery> getBaseDao() {
        return ctrContractDeliveryDao;
    }

    @Override
    public CtrContractDelivery findByDeliveryId(String waybillCode) {

        return ctrContractDeliveryDao.findByDeliveryId(waybillCode);
    }

    @Override
    public CtrContractDelivery findByContractId(Long contractId) {
        return ctrContractDeliveryDao.findByContractId(contractId);
    }

    @Override
    public void deliveryNoteUpdate(String waybillCode, String driverName, String driverPhone, String plateNumber){
        ctrContractDeliveryDao.deliveryNoteUpdate(waybillCode,driverName,driverPhone,plateNumber);
    }

    @Override
    public Page<CtrContractDeliveryVo> findPageContract(ContractSearchVo queryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        String businessType = queryVo.getBusinessType();
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType)) {
            List<Sort.Order> sorts = new ArrayList<>();
            sorts.add(new Sort.Order(Sort.Direction.DESC, "id"));
            sort = Sort.by(sorts);
        }
        Long deptLeaderId = queryVo.getDeptLeaderId();
        PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);//分页
        Specification<CtrContractDelivery> spec_userId = WebUtil.buildSpecification("EQL_matchUserId", queryVo.getUserId());
        Specification<CtrContractDelivery> spe = WebUtil.buildSpecification(queryVo.getSearchParams());
        String searchType = queryVo.getSearchType();// A：查询本中心所有hetong  P:查看本中心所有预售合同
        List<Long> myDeptId = authOpenFacade.findMyDeptId(queryVo.getUserId());
        List<Long> allDeptId = authOpenFacade.findMyDeptId(deptLeaderId);

        if (!queryVo.isAdmin()) {
            if (allDeptId.size() == 0 || myDeptId.size() == 0) {
                myDeptId.add(0L);
                allDeptId.add(0L);
            }
            if (StringUtils.equals("A", searchType)) {
                //可以查看本中心所有合同
                Specification<CtrContractDelivery> spec_department = WebUtil.buildSpecification("INL_deptId", allDeptId);
                Specification<CtrContractDelivery> spec_department_userId = Specification.where(spec_userId).or(spec_department);
                spe = Specification.where(spec_department_userId);
            } else if (StringUtils.equals("D", searchType)) {
                // 业务助理查询本业务部所有合同
                //可以查看所属于自己的合同
                DeptSearchVo sysDeptSearchVo = new DeptSearchVo();
                sysDeptSearchVo.setUserId(queryVo.getUserId());
                SysDeptSdk dept = authOpenFacade.findDept(sysDeptSearchVo);
                Specification<CtrContractDelivery> spec_department = WebUtil.buildSpecification("INL_deptId", dept.getDeptId());
                Specification<CtrContractDelivery> spec_department_userId = Specification.where(spec_userId).or(spec_department);
                spe = Specification.where(spe).and(spec_department_userId);
            } else {
                //可以查看所属于自己的合同
                Specification<CtrContractDelivery> spec_department = WebUtil.buildSpecification("INL_deptId", myDeptId);
                Specification<CtrContractDelivery> spec_department_userId = Specification.where(spec_userId).or(spec_department);
                spe = Specification.where(spe).and(spec_department_userId);
            }
        }
        Page<CtrContractDelivery> page = ctrContractDeliveryDao.findAll(spe,pageRequest);
        List<CtrContractDelivery> content = page.getContent();
        List<CtrContractDeliveryVo> voList = new ArrayList<CtrContractDeliveryVo>();
        int i = 0;
        for (CtrContractDelivery ctr : content) {
             CtrContract one = ctrContractDao.findOne(ctr.getContractId());
            i++;
            CtrContractDeliveryVo vo = new CtrContractDeliveryVo();
            BeanUtils.copyProperties(ctr, vo);
            vo.setPairId(Long.valueOf(i));
            vo.setContractNo(one.getContractNo());
             String waybillCode = ctr.getWaybillCode();
             if(StringUtils.isNotBlank(waybillCode)){
                 CtrContractDelivery deliveryId = ctrContractDeliveryClient.findByDeliveryId(waybillCode);
                  vo.setCarryAmount(deliveryId.getCarryAmount());
                 if(vo.getLoadAmount()!=null){
                     vo.setTotalAmount(vo.getLoadAmount().add(deliveryId.getCarryAmount()));
                 }else{
                     vo.setTotalAmount(vo.getLoadAmount());
                 }
                 vo.setProductStatus(one.getProductStatus());
             }
            vo.setProductStatus(ctr.getProductStatus());
            vo.setReceiptStatus(ctr.getReceiptStatus());
            vo.setContractId(one.getId());
            voList.add(vo);
        }
        // sort属性无法反序列化，下面代码重新组装page对象，去掉sort属性
        PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
        Page<CtrContractDeliveryVo> pageVo = new PageImpl<>(voList, pageRequest_new, page.getTotalElements());
        return pageVo;
    }
}
