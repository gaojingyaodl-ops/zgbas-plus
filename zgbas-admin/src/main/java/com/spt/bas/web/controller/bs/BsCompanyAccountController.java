package com.spt.bas.web.controller.bs;

import cn.hutool.core.util.BooleanUtil;
import com.google.common.collect.Maps;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyAccount;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.BsCompanyVo;
import com.spt.bas.client.vo.BsWarehouseVo;
import com.spt.bas.client.vo.CompanyAccountVo;
import com.spt.bas.client.vo.CompanyAreaVo;
import com.spt.bas.web.config.BasicErrorController;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.StringUtils;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.ErrorResp;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户财务信息管理
 *
 * @author zhangyanping
 */
@Controller
@RequestMapping(value = "/bs/companyAccount")
public class BsCompanyAccountController extends SingleCrudControll<BsCompanyAccount, BaseVo> {


    @Autowired
    private IBsAreaClient areaClient;

    @Autowired
    private IBsCompanyAccountClient bsCompanyAccountClient;

    @Autowired
    private IBsWarehouseClient bsWarehouseClient;

    @Autowired
    private IBsCompanyClient companyClient;

    @Autowired
    private com.spt.bas.report.client.remote.IRptBsCompanyClient recompanyClient;

    @Autowired
    private IBsCompanyDcsxClient bsCompanyDcsxClient;


    @Override
    public BaseClient<BsCompanyAccount> getService() {
        return bsCompanyAccountClient;
    }

    /**
     * 查询所有
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Long id, Model model) {
        List<BsCompany> lstCompany = companyClient.findAll();
        model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
        model.addAttribute("id", id);
        return "bs/companyAccount";
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }

    @Override
    protected void preInsert(BsCompanyAccount e) {
        e.setEnterpriseId(ShiroUtil.getEnterpriseId());
    }

    @RequestMapping("/findListByCompanyId")
    public void findListByCompanyId(@RequestParam("id") Long id, PageSearchVo queryVo, HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("EQL_companyId", id);
        //initSearch2(queryVo, request, map);
        queryVo.setSearchParams(map);
        PageDown<BsCompanyAccount> page = bsCompanyAccountClient.findPage(queryVo);
		/*BsCompanyAccount e = sumPage(request, response);
		Map<String, Object> footer = null;
		if (e != null) {
			footer = entity2Footer(e);
		}*/
        JsonEasyUI.renderJson(response, page);
    }
    /**
     * 查询省市区
     * @param id
     */
    @ResponseBody
    @RequestMapping(value = "findGetAreaVo", method = RequestMethod.POST)
    public String findGetAreaVo(@RequestParam("id") String id){
        String area=" ";
        if(StringUtils.isNotEmpty(id)){
            CompanyAreaVo vo = areaClient.getAreaVo(Long.valueOf(id));
            if(null==vo.getCityName() && null==vo.getRegionName()){
                area=vo.getProvinceName();
            }
            else if(null==vo.getCityName()){
                area=vo.getProvinceName()+"/"+vo.getRegionName();
            }
            else if(null==vo.getRegionName()){
                area=vo.getProvinceName()+"/"+vo.getCityName();
            }
            else{
                area=vo.getProvinceName()+"/"+vo.getCityName()+"/"+vo.getRegionName();
            }
        }
        return area;
    }

    @RequestMapping("/findListByCompanyIdAddr")
    public void findListByCompanyIdAddr(@RequestParam("id") Long id, PageSearchVo queryVo, HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("EQL_companyId", id);
        queryVo.setSearchParams(map);
        PageDown<BsWarehouse> page = bsWarehouseClient.findPage(queryVo);
        PageDown<BsWarehouseVo> pagew = new PageDown<BsWarehouseVo>();
        BeanUtils.copyProperties(page, pagew);
        List<BsWarehouse> content1 = page.getContent();
        List<BsWarehouseVo> content2 = new ArrayList<>();
        for (BsWarehouse warehouse : content1) {
            BsWarehouseVo vo = new BsWarehouseVo();
            BeanUtils.copyProperties(warehouse, vo);
            content2.add(vo);
        }
        pagew.setContent(content2);
        List<BsWarehouseVo> content = pagew.getContent();
        for (BsWarehouseVo bsWarehouseVo : content) {
            bsWarehouseVo.setwCompanyId(bsWarehouseVo.getCompanyId());
            bsWarehouseVo.setwContactPhone(bsWarehouseVo.getContactPhone());
            bsWarehouseVo.setwDefaultFlg(bsWarehouseVo.getDefaultFlg());
            if(StringUtils.isNotEmpty(bsWarehouseVo.getAreaCode())){
                CompanyAreaVo vo = areaClient.getAreaVo(Long.valueOf(bsWarehouseVo.getAreaCode()));
                String area=" ";
                if(null==vo.getCityName() && null==vo.getRegionName()){
                    area=vo.getProvinceName();
                }
                else if(null==vo.getCityName()){
                    area=vo.getProvinceName()+"/"+vo.getRegionName();
                }
                else if(null==vo.getRegionName()){
                    area=vo.getProvinceName()+"/"+vo.getCityName();
                }
                else{
                    area=vo.getProvinceName()+"/"+vo.getCityName()+"/"+vo.getRegionName();
                }
                bsWarehouseVo.setAreaCode(area);
            }
        }
        pagew.setContent(content);
        JsonEasyUI.renderJson(response, pagew);
    }

    /**
     * 查询默认公司默认账号
     *
     * @param id
     * @param response
     * @param request
     */
    @RequestMapping("/findCompanyAccountFlg/{id}")
    public void findCompanyAccountFlg(@PathVariable("id") Long id, HttpServletResponse response, HttpServletRequest request) {
        if (id != null) {
            BsCompanyVo bsCompanyVo = new BsCompanyVo();
            bsCompanyVo.setId(id);
            bsCompanyVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
            List<BsCompanyAccount> list = bsCompanyAccountClient.findCompanyAccountFlg(bsCompanyVo);
            if (!list.isEmpty()) {
                RenderUtil.renderJson(list, response);
            } else {
                RenderUtil.renderFailure("fail", response);
            }
        }
    }
    /**
     * 代采赊销查询默认公司默认账号
     *
     * @param ourCompanyName
     * @param response
     * @param request
     */
    @RequestMapping("/findCompanyAccountDcsxFlg/{ourCompanyName}")
    public void findCompanyAccountDcsxFlg(@PathVariable("ourCompanyName") String ourCompanyName, HttpServletResponse response, HttpServletRequest request) {
      List<BsCompany> list=new ArrayList<>();
        if (ourCompanyName != null) {
            BsCompany bsCompany=new BsCompany();
            BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(ourCompanyName);
             if (byCompanyName==null){
                 BsCompany byCompanyName1 = companyClient.findByCompanyName(ourCompanyName);
                 bsCompany.setBankName(byCompanyName1.getBankName()==null ? "" : byCompanyName1.getBankName());
                 bsCompany.setBankAccount(byCompanyName1.getBankAccount()==null ? "" :byCompanyName1.getBankAccount() );
                 bsCompany.setTaxNo(byCompanyName1.getTaxNo()==null ? "" : byCompanyName1.getTaxNo());
                 bsCompany.setCompanyPhone(byCompanyName1.getCompanyPhone()==null?"":byCompanyName1.getCompanyPhone());
                 bsCompany.setAddress(byCompanyName1.getAddress()==null?"":byCompanyName1.getAddress());
             }else{
                 bsCompany.setBankName(byCompanyName.getCompanyBankName()==null ? "" : byCompanyName.getCompanyBankName());
                 bsCompany.setBankAccount(byCompanyName.getCompanyCardId()==null ? "" : byCompanyName.getCompanyCardId());
                 bsCompany.setTaxNo(byCompanyName.getCompanyTaxNo()==null ? "":byCompanyName.getCompanyTaxNo());
                 bsCompany.setCompanyPhone(byCompanyName.getCompanyPhone()==null?"":byCompanyName.getCompanyPhone());
                 bsCompany.setAddress(byCompanyName.getAddress()==null?"":byCompanyName.getAddress());
             }
             list.add(bsCompany);
            RenderUtil.renderJson(list, response);

        }
    }

    /**
     * 删除
     *
     * @param id
     * @param response
     */
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public void delete(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            BsCompanyAccount findid = bsCompanyAccountClient.findid(id);
            getService().delete(id);
            BsCompanyVo bsCompanyVo = new BsCompanyVo();
            bsCompanyVo.setId(id);
            bsCompanyVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
            List<BsCompanyAccount> list = bsCompanyAccountClient.findByCompanyIdTrue(findid.getCompanyId());
            if(list.size()>0){
                for (BsCompanyAccount bsCompanyAccount : list) {
                    if(bsCompanyAccount.getDefaultFlg()==true){
                        break;
                    }else{
                        BsCompanyAccount bsCompanyAccount1 = list.get(0);
                        bsCompanyAccount1.setDefaultFlg(true);
                        bsCompanyAccountClient.save(bsCompanyAccount1);
                    }
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            RenderUtil.renderFailure("failure", response);
        }
        RenderUtil.renderSuccess("success", response);

    }

    /**
     * 删除
     *
     * @param id
     * @param response
     */
    @RequestMapping(value = "deleteAddr/{id}", method = RequestMethod.GET)
    public void deleteAddr(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            BsWarehouse findid = bsWarehouseClient.findid(id);
            bsWarehouseClient.delete(id);
            List<BsWarehouse> list = bsWarehouseClient.findByCompanyIdAddr(findid.getCompanyId());
            if(list.size()>0){
                boolean defaultFlg2 =false;
                for (BsWarehouse vo : list) {
                    if(vo.getDefaultFlg()==true){
                        defaultFlg2=true;
                        break;
                    }
                }
                if(defaultFlg2==false){
                    BsWarehouse warehouse = list.get(0);
                    warehouse.setDefaultFlg(true);
                    bsWarehouseClient.save(warehouse);
                }
            }

        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            RenderUtil.renderFailure("failure", response);
        }
        RenderUtil.renderSuccess("success", response);

    }

    @ModelAttribute("preload")
    public BsCompanyAccount getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0)
                return getService().getEntity(id);
            else {
                BsCompanyAccount entity = new BsCompanyAccount();
                entity.setId(0l);
                return entity;
            }
        }
        return null;
    }

    @PostMapping(value = "saveAccount")
    public void saveAccount(CompanyAccountVo vo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        if (vo.getId() == 0) {//检测企业是否已经存在
            int companyNameCount = recompanyClient.countCompanyByName(vo.getCompanyName());
            if (companyNameCount > 0) {
                RenderUtil.renderFailure("该企业已经存在！", response);
                return;
            }
        }
        logger.info("saveAccount : init " + JsonUtil.obj2Json(vo));
        try {
            List<BsWarehouseVo> wLstInsert = JsonEasyUI.getInsertRecords(BsWarehouseVo.class, request);
            List<BsWarehouseVo> wLstUpdate = JsonEasyUI.getUpdatedRecords(BsWarehouseVo.class, request);
            List<BsWarehouseVo> wLstDelete = JsonEasyUI.getDeletedRecords(BsWarehouseVo.class, request);
            List<BsCompanyAccount> lstInsert = JsonEasyUI.getInsertRecords(BsCompanyAccount.class, request);
            List<BsCompanyAccount> lstUpdate = JsonEasyUI.getUpdatedRecords(BsCompanyAccount.class, request);
            List<BsCompanyAccount> lstDelete = JsonEasyUI.getDeletedRecords(BsCompanyAccount.class, request);
            if (lstInsert.size() > 0 || wLstInsert.size() > 0) {
                if (wLstInsert.get(0).getwDefaultFlg() == null) {
                    wLstInsert.removeAll(wLstInsert);
                }
                if (lstInsert.get(0).getDefaultFlg() == null) {
                    lstInsert.removeAll(lstInsert);
                }
            }
            if (lstUpdate.size() > 0 || wLstUpdate.size() > 0) {
                if (wLstUpdate.get(0).getwDefaultFlg() == null) {
                    wLstUpdate.removeAll(wLstUpdate);
                }
                if (lstUpdate.get(0).getBankAccount() == null) {
                    lstUpdate.removeAll(lstUpdate);
                }
            }
            vo.setWbatchSub(wLstInsert, wLstUpdate, wLstDelete);
            vo.setBatchSub(lstInsert, lstUpdate, lstDelete);
            vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
            vo.setCreateUserId(ShiroUtil.getCurrentUserId());
            vo.setCreateUserName(ShiroUtil.getCurrentUserName());
            //是否有风控编辑客户权限
            boolean permittedFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_COMPANY_EDIT.getPermissionCode());
            vo.setPermittedFlg(permittedFlg);

            String plasticType = vo.getPlasticType();
            String temporaryPlasticType = vo.getTemporaryPlasticType();
            boolean riskFlag = ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_RISK.getPermissionCode());
            if (BooleanUtil.isTrue(riskFlag)){
                vo.setPlasticType(StringUtils.isNotBlank(plasticType) ? plasticType : temporaryPlasticType);
                vo.setTemporaryPlasticType(null);
            } else if (StringUtils.equals("PS", plasticType)){
                vo.setTemporaryPlasticType(plasticType);
                vo.setPlasticType(null);
            } else if (StringUtils.isNotBlank(plasticType) && !StringUtils.equals("PS", plasticType)){
                vo.setPlasticType(plasticType);
                vo.setTemporaryPlasticType(null);
            } else if (StringUtils.isNotBlank(temporaryPlasticType) && !StringUtils.equals("PS", temporaryPlasticType)){
                vo.setPlasticType(temporaryPlasticType);
                vo.setTemporaryPlasticType(null);
            }
            logger.info("saveAccount : " + JsonUtil.obj2Json(vo));
            // allowed字段不再使用 为兼容默认设置为'Y'
            vo.setAllowed(BasConstants.DICT_TYPE_ALLOWED_Y);
            vo.setEnableFlg(true);
            BsCompany company = companyClient.saveAccount(vo);
            Map<String, Object> map = new HashMap<>();
            map.put("success", "success");
            map.put("company", company);
            RenderUtil.renderJson(map, response);
        } catch (Exception e) {
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
            logger.error("saveAccount", e);
        }

    }


}
