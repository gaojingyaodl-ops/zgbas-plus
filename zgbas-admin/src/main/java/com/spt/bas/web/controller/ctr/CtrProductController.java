package com.spt.bas.web.controller.ctr;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsProductConfig;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.remote.IBsProductConfigClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.ICtrProductClient;
import com.spt.bas.client.vo.BsProductConfigVo;
import com.spt.bas.client.vo.CtrProductSearchVo;
import com.spt.bas.client.vo.CtrProductVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/ctr/product")
public class CtrProductController extends PageController<CtrProduct, BaseVo>{

	@Autowired
	private ICtrProductClient  ctrProductClient;
	@Autowired
	private IBsProductConfigClient bsProductConfigClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Override
	public BaseClient<CtrProduct> getService() {
		// TODO Auto-generated method stub
		return ctrProductClient;
	}
	/**出库商品详情*/
	@RequestMapping(value = "findProductList")
	public String list(@RequestParam("ctrContractId") Long ctrContractId, @RequestParam(value = "waybillCode", required = false) String waybillCode, HttpServletRequest request, HttpServletResponse response) {
		CtrProductSearchVo vo = new CtrProductSearchVo();
		vo.setCtrContractId(ctrContractId);
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<CtrProductVo> page = ctrProductClient.findProductList(vo);
		JsonEasyUI.renderJson(response, page);
		return null;
	}

	/**确认收货商品详情*/
	@RequestMapping(value = "findConfirmProductList")
	public String findConfirmProductList(@RequestParam("ctrContractId") Long ctrContractId, HttpServletRequest request, HttpServletResponse response) {
		CtrProductSearchVo vo = new CtrProductSearchVo();
		vo.setCtrContractId(ctrContractId);
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<CtrProductVo> page = ctrProductClient.findConfirmProductList(vo);
		JsonEasyUI.renderJson(response, page);
		return null;
	}



	@RequestMapping(value = "findList")
	public String findList(@RequestParam("contractId") Long contractId, HttpServletRequest request, HttpServletResponse response) {
		PageSearchVo pageSearchVo = new PageSearchVo();
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("EQL_ctrContractId", contractId);
		pageSearchVo.setSearchParams(map);
		List<CtrProductVo> findList = ctrProductClient.findList(pageSearchVo);
		JsonEasyUI.renderListJson(response, findList);
		return null;
	}

	/**
	 * 商品列表 无库存
	 * @param contractId
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "findListWithNoStock")
	public String findListWithNoStock(@RequestParam("contractId") Long contractId, HttpServletRequest request, HttpServletResponse response) {
		PageSearchVo pageSearchVo = new PageSearchVo();
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("EQL_ctrContractId", contractId);
		pageSearchVo.setSearchParams(map);
		String LogisticsQuotation = "";

		CtrContract contract = ctrContractClient.findByContractId(contractId);
		List<CtrContract> byApproveId = ctrContractClient.findByApproveId(contract.getApproveId());
		CtrContract ctrContract = byApproveId.get(1);
		String customerOrderCode = ctrContract.getCustomerOrderCode();
		List<CtrProductVo> findList = ctrProductClient.findListWithNoStock(pageSearchVo);
		if (CollectionUtils.isNotEmpty(findList)) {
			findList.get(0).setLogisticsQuotation(LogisticsQuotation);
			if (StringUtils.isNotBlank(customerOrderCode)){
				findList.get(0).setZyflag("T");
			}else{
				findList.get(0).setZyflag("F");
			}
		}
		JsonEasyUI.renderListJson(response, findList);
		return null;
	}

	//商品详情
	@RequestMapping(value = "findProduct/{contractId}", method = RequestMethod.GET)
	public String findProduct(@PathVariable("contractId") Long contractId,Model model){
		if(contractId!=null && contractId>01){
			model.addAttribute("contractId", contractId);
		}
		//包装规格
		model.addAttribute("packingSpecificaJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
		return "ctr/productDetail";
	}

	@RequestMapping(value = "/getProduct/{contractId}", method = RequestMethod.GET)
	@ResponseBody
	public CtrProduct getProduct(@PathVariable("contractId") Long contractId, Model model) {

		List<CtrProduct> list = ctrProductClient.findByOutCtrContractId(contractId);
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}

	@RequestMapping(value = "findMinDeliveryDateByProductId", method = RequestMethod.POST)
	@ResponseBody
	public Object findMaxDeliveryDateByProductId(@RequestParam("list") List<String> list){
		if (list != null && list.size() > 0) {
			List<Long> collect = list.stream().map(m->Long.valueOf(m)).collect(Collectors.toList());
			return ctrProductClient.findMinDeliveryDateByProductId(collect);
		}
		return null;
	}

	@RequestMapping(value = "getProductConfig", method = RequestMethod.POST)
	@ResponseBody
	public BsProductConfigVo getProductConfig(@RequestParam("productCd") String productCd, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotBlank(productCd)) {
			BsProductConfig config = new BsProductConfig();
			config.setConfigKey(productCd);
			config.setEnterpriseId(ShiroUtil.getEnterpriseId());
			BsProductConfigVo productConfig = bsProductConfigClient.getProductConfig(config);
			return productConfig;
		}
		return null;
	}

}
