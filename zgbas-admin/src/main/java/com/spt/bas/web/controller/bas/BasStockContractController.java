package com.spt.bas.web.controller.bas;


import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IStockContractClient;
import com.spt.bas.client.vo.StockContractRelaVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存管理
 * @author wanjie
 *
 */
@Controller
@RequestMapping("/bas/stockContract")
public class BasStockContractController extends PageController<StockContract, BaseVo>{

	@Autowired
	private IStockContractClient stockContractClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Override
	public BaseClient<StockContract> getService() {
		return stockContractClient;
	}

	/**
	 * 库存列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value="")
	public String findStock(Model model){
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		//获取业务员树
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		return "bas/stockContract";
	}

//	@RequestMapping(value = "findPageStockContractList")
//	public String findPageStockContractList(StockContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
//		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
//		initSearch(searchVo, request);
//		Map<String, Object> searchParams = searchVo.getSearchParams();
//		if (searchParams.isEmpty()){
//			Map<String, Object> map = new HashMap<>();
//			map.put("EQL_enterpriseId",  ShiroUtil.getEnterpriseId());
//			searchVo.setSearchParams(map);
//		}else{
//			searchParams.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
//		}
//		Map<String, Object> footer =new HashMap<>();
//		Page<StockContractVo> page=	stockContractClient.findPageStockContractList(searchVo);
//		JsonEasyUI.renderJson(response, page,null,footer);
//		return null;
//	}

	@RequestMapping(value = "detailHis/{id}", method = RequestMethod.GET)
	public String detailHis2(@PathVariable("id") Long id,Model model){
		if(id!=null&&id>0l){
			model.addAttribute("stockContractId", id);
			//库存类型
			model.addAttribute("operationTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_OPERATIONTYPE)));
			//查看合同权限
			boolean canViewContract = false;
			if(ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())){
				canViewContract = true;
			}
			model.addAttribute("canViewContract", canViewContract);
		}
		return "bas/stockContractDetailHis";
	}

	@RequestMapping(value = "listDetailHis/{stockContractId}", method = RequestMethod.POST)
	public void listDetailHis(@PathVariable("stockContractId") Long stockContractId,PageSearchVo searchVo, HttpServletResponse response){
		if(stockContractId!=null && stockContractId > 0L){
			searchVo.setSort("id");
			searchVo.setOrder("DESC");
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("EQL_stockContractId",stockContractId);
			searchParams.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
			searchVo.setSearchParams(searchParams);
			PageDown<StockContractRelaVo> page = stockContractClient.findStockContractRela(searchVo);
			JsonEasyUI.renderJson(response, page);

		}
	}
}
