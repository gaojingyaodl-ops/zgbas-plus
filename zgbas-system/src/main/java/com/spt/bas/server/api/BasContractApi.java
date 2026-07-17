package com.spt.bas.server.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasContractRela;
import com.spt.bas.client.vo.BasContractExistVo;
import com.spt.bas.client.vo.BasContractVo;
import com.spt.bas.client.vo.ContractOpVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IBasContractRelaService;
import com.spt.bas.server.service.IBasContractService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bas/contract")
public class BasContractApi extends BaseApi<BasContract> {
    @Autowired
    private IBasContractService basContractService;

    @Autowired
    private IBasContractRelaService basContractRelaService;
    @Autowired
    private IPmApproveService approveService;


    @Override
    public IBaseService<BasContract> getService() {
        return basContractService;
    }

    @PostMapping("existContractNo")
    public boolean existContractNo(@RequestBody BasContractExistVo vo) {
        return basContractService.existGoodsCode(vo);
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        basContractService.updateFileId(vo.getId(), vo.getFileId());
    }

//	@PostMapping("updateContractStatusByFond")
//	public void updateContractStatusByFond(@RequestBody BasContract contract){
//		basContractService.updateContractStatusByFond(contract);
//	}
//	
//	@PostMapping("updateContractStatusByBill")
//	public void updateContractStatusByBill(@RequestBody BasContract contract){
//		basContractService.updateContractStatusByBill(contract);
//	}

    @PostMapping("doContractOp")
    public void doContractOp(@RequestBody ContractOpVo opVo) {
        basContractService.doContractOp(opVo);
    }

    @PostMapping("findPageVo")
    public Page<BasContractVo> findPageVo(@RequestBody PageSearchVo queryVo) {
        String[] buyContractId = null;
        String[] sellContractId = null;
        Page<BasContract> page = super.findPage(queryVo);
        Map<String, Object> searchParam = queryVo.getSearchParams();
        String contractRelaId = (String) searchParam.get("EQL_contractRelaId");
        if (contractRelaId != null && !contractRelaId.equals("0")) {
            BasContractRela contractRela = basContractRelaService.getEntity(Long.valueOf(contractRelaId));
            if (contractRela.getExposureFlg() != null && contractRela.getExposureFlg()) {
                String contract1 = contractRela.getBuyContractId();
                if (null != contract1) {
                    buyContractId = contract1.split("\\|");
                }
                String contract2 = contractRela.getSellContractId();
                if (null != contract2) {
                    sellContractId = contract2.split("\\|");
                }
            }
        }

        List<BasContractVo> listVo = new ArrayList<BasContractVo>();
        List<BasContract> data = page.getContent();
        for (int i = 0; i < data.size(); i++) {
            BasContract entity = data.get(i);
            BasContractVo vo = new BasContractVo();
            BeanUtils.copyProperties(entity, vo);
            if ("B".equals(entity.getContractType())) {
                if (sellContractId != null) {
                    Long sellId = Long.parseLong(sellContractId[i]);
                    BasContract bc = basContractService.getEntity(sellId);
                    if (bc != null) {
                        vo.setBuyOrSellContractNo(bc.getContractNo());
                        vo.setBuyOrSellContractId(sellId);
                    }
                }
            } else {
                if (buyContractId != null) {
                    Long buyerId = Long.parseLong(buyContractId[i]);
                    BasContract bc = basContractService.getEntity(buyerId);
                    if (bc != null) {
                        vo.setBuyOrSellContractNo(bc.getContractNo());
                        vo.setBuyOrSellContractId(buyerId);
                    }
                }
            }

            listVo.add(vo);
        }
        PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
        Page<BasContractVo> pageVo = new PageImpl<>(listVo, pageRequest, page.getTotalElements());
        return pageVo;

    }

    @PostMapping("findPageByContQuery")
    public Page<BasContract> findPageByContQuery(@RequestBody PageSearchVo searchVo) {
        Page<BasContract> page = super.findPage(searchVo);
        for (BasContract entity : page.getContent()) {
            Long approveId = entity.getApproveId();
            if (approveId != null) {
                PmApprove approve = approveService.getEntity(approveId);
                if (approve != null) {
                    entity.setCreateUserName(approve.getCreateUserName());
                }
            }
        }

        return page;
    }

    @PostMapping(value = "findByContractRelaId")
    public List<BasContract> findByContractRelaId(@RequestBody Long contractRealaId) {
        return basContractService.findByContractRelaId(contractRealaId);
    }

}

