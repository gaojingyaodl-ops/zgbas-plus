package com.spt.bas.web.controller.bas;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.ApplyDeliveryIn;
import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.report.client.entity.RptStockDetailReport;
import com.spt.bas.report.client.entity.RptWarehouseOutEntity;
import com.spt.bas.report.client.remote.IRptStockInventoryClient;
import com.spt.bas.report.client.vo.RptWarehouseOutSearchVo;
import com.spt.bas.web.config.BasicErrorController;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.ErrorResp;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 库存明细
 * @author
 *
 */
@Controller
@RequestMapping(value = "/bas/stockDetail")
public class BasStockDetailController extends SingleCrudControll<StockDetail, BaseVo>{

	@Autowired
	private IStockDetailClient stockDetailClient;
	@Autowired
	private IStockDetailHisClient stockDetailHisClient ;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Autowired
	private IBsWarehouseClient bsWarehouseClient;
	@Autowired
	private IPmApproveClient approveClient;
	@Autowired
	private IApplyDeliveryOutClient applyDeliveryOutClient;
	@Autowired
	private IApplyDeliveryInClient applyDeliveryInClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IRptStockInventoryClient stockInventoryClient;
	@Override
	public BaseClient<StockDetail> getService() {
		return stockDetailClient;
	}
	/**查询库存明细中存在的仓库列表*/
	@RequestMapping(value = "findWarehoseList")
	public void findCommboList(Model model, HttpServletRequest request, HttpServletResponse response,
			StockDetailSearchVo vo) {
		List<WarehouseAndInNumberVo> list = stockDetailClient.findWarehoseList(vo);
//		List<Map<String, String>> lstMap =new ArrayList<>();
//		for (StockDetail detail : list) {
//			if(detail.getFrozenNumber().compareTo(BigDecimal.ZERO)>0){
//				Map<String, String> map =new HashMap<>();
//				map.put("text", detail.getWarehouseName());
//				map.put("buyContractId", detail.getBuyContractId());
//				map.put("frozenNumber", detail.getFrozenNumber().toString());
//				map.put("availableNumber", detail.getAvailableNumber().toString());
//				lstMap.add(map);
//			}
//		}
		RenderUtil.renderJson(list, response);
	}

	@RequestMapping(value = "warehouseOut")
	public String warehouseOut(Model model, HttpServletRequest request, HttpServletResponse response,
			RptWarehouseOutSearchVo searchVo) {
		// 获取业务员树
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		model.addAttribute("searchVo", searchVo);
		return "stock/warehouseOut-choose";
	}
	@RequestMapping(value = "findWarehouseOut")
	public void findWarehouseOut(Model model, HttpServletRequest request, HttpServletResponse response,
			RptWarehouseOutSearchVo searchVo) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		Page<RptWarehouseOutEntity> page = stockInventoryClient.findWarehouseOut(searchVo);
		JsonEasyUI.renderJson(response, page);
	}
	/**根据合同库存id查询原货源可出库记录*/
	@RequestMapping(value = "loadWarehouseOutSelf")
	public void loadWarehouseOutSelf(Model model, HttpServletRequest request, HttpServletResponse response,
			RptWarehouseOutSearchVo searchVo) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		if (searchVo.getStockContractId() == null || searchVo.getStockContractId() == 0) {
			return;
		}
		Page<RptWarehouseOutEntity> page = stockInventoryClient.findWarehouseOut(searchVo);
		if (page.getContent().size()>0) {
			RptWarehouseOutEntity entity = page.getContent().get(0);
			RenderUtil.renderJson(entity, response);
		}
	}


	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	@Override
	protected void preInsert(StockDetail e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
//	@Override
//	protected Map<String, Object> entity2Footer(StockDetail e) {
//		Map<String, Object> footer =new HashMap<>();
//		if (e!=null) {
//			footer.put("warehouseName", "合计");
//			footer.put("deliveryInNumber", e.getDeliveryInNumber());
//			footer.put("availableNumber", e.getAvailableNumber());
//			footer.put("frozenNumber", e.getFrozenNumber());
//		}
//
//		return footer;
//	}

	@RequestMapping(value = "detailHis/{id}", method = RequestMethod.GET)
	public String detailHis2(@PathVariable("id") Long id,Model model){
		if(id!=null&&id>0l){
			StockDetail st = stockDetailClient.getEntity(id);
			model.addAttribute("stockDetailId", st.getId());
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
		return "bas/stockDetailHis";
	}


	@RequestMapping(value = "listDetailHis/{id}/{status}", method = RequestMethod.POST)
	public void listDetailHis(@PathVariable("id") Long id,@PathVariable("status") String status, HttpServletResponse response){
		if(id!=null && id > 0L && status != null){
			PageSearchVo searchVo = new PageSearchVo();
			searchVo.setRows(100);
			searchVo.setSort("id");
			searchVo.setOrder("DESC");
			Map<String, Object> searchParams = new HashMap<String, Object>();
			if(status.equals(BasConstants.DICT_TYPE_DELIVERY)){
				//查询操作类型为 出入库相关
				searchParams.put("INS_operationType", BasConstants.DELIVERY_ARRAY);
			}else {
				//查询操作类型为 采购销售相关
				searchParams.put("INS_operationType", BasConstants.BUYANDSELL_ARRAY);
			}
			searchParams.put("EQL_stockDetailId",id);
			searchVo.setSearchParams(searchParams);
			PageDown<StockDetailHisVo> page = stockDetailHisClient.findPageVo(searchVo);
			JsonEasyUI.renderJson(response, page);
		}
	}

	@RequestMapping(value = "")
	public String index(Model model) {
		//货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		model.addAttribute("stockStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_STOCKSTATUS)));
		model.addAttribute("productAttrJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("stockTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_STOCKTYPE)));
		model.addAttribute("spotTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SPOTTYPE)));
		model.addAttribute("stockCategoryJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_STOCK_GATEGORY)));
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		//获取业务员树
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		model.addAttribute("deptAllJson", JsonUtil.obj2Json(deptList));
		//更改库存类型权限
		boolean canUpdate = false;
		if(ShiroUtil.isPermitted(PermissionEnum.APPROVE_CTR_UPDATE_STOCKTYPE.getPermissionCode())){
			canUpdate = true;
		}
		model.addAttribute("movestock", ShiroUtil.isPermitted(PermissionEnum.PERM_STOCK_MOVESTOCK.getPermissionCode()));
		model.addAttribute("canUpdate", canUpdate);

		return "bas/stockDetail";
	}

	//跳转库存明细选择页面
	@RequestMapping(value = "choose")
	public String choose(Model model) {
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		model.addAttribute("stockStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_STOCKSTATUS)));
		model.addAttribute("productType",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		//获取业务员树
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		return "bas/stockDetail-choose";
	}
	@RequestMapping(value = "doRedirect")
	public String doRedirect(Model model) {

		return "bas/contractAdjust-choose";
	}

	@Override
	@RequestMapping(value = "list")
	public String list(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		PageDown<BasStockDetailVo> page = stockDetailClient.findPageList(searchVo);
		JsonEasyUI.renderJson(response, page);
		return null;
	}

	/** 列表查询方法 */
	@RequestMapping(value = "listVo")
	public void listVo(RptStockDetailReport searchVo, HttpServletRequest request, HttpServletResponse response) {
		/*initSearch(searchVo, request);
		Page<StockDetailVo> page = stockDetailClient.findPageVo(searchVo);
		Map<String, Object> footer =new HashMap<>();
		StockDetail stockDetail = stockDetailClient.sumPageVo(searchVo);
		footer.put("warehouseName","合计");
		footer.put("deliveryInNumber", stockDetail.getDeliveryInNumber());
		footer.put("deliveryOutNumber", stockDetail.getDeliveryOutNumber());
		footer.put("availableNumber", stockDetail.getAvailableNumber());
		footer.put("frozenNumber", stockDetail.getFrozenNumber());
		JsonEasyUI.renderJson(response, page,null,footer);*/
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptStockDetailReport> page = stockInventoryClient.findStockDetailPage(searchVo);
		RptStockDetailReport total = stockInventoryClient.findStockDetailTotal(searchVo);
		Map<String, Object> footer =new HashMap<>();
		footer.put("warehouseName","合计");
		footer.put("deliveryInNumber", total.getDeliveryInNumber());
		footer.put("deliveryOutNumber", total.getDeliveryOutNumber());
		footer.put("availableNumber", total.getAvailableNumber());
		footer.put("frozenNumber", total.getFrozenNumber());
		JsonEasyUI.renderJson(response, page,null,footer);

	}

	@RequestMapping(value = "getEntityById")
	public String getEntityById(@RequestParam(value="id") Long id, Model model){
		StockDetail detail = stockDetailClient.getEntity(id);
		model.addAttribute("entity", detail);
		model.addAttribute("spotTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SPOTTYPE)));
		model.addAttribute("deliveryInTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYIN_TYPE)));
		List<BsWarehouse> list = bsWarehouseClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		Iterator<BsWarehouse> it = list.iterator();
		while(it.hasNext()){
			BsWarehouse s = it.next();
			if(s.getWarehouseName().equals(detail.getWarehouseName())){
				it.remove();
			}
		}
		model.addAttribute("warehouseJson", JsonUtil.obj2Json(list));
		return "bas/moveStock";
	}

	@RequestMapping(value = "getEntity/{id}")
	@ResponseBody
	public StockDetail getEntityBy(@PathVariable(value="id") Long id, Model model){
		return stockDetailClient.getEntity(id);
	}

	@PostMapping(value="changeWarehouse")
	public void changeWarehouse(StockDetailMoveVo changeVo,HttpServletResponse response){
		changeVo.setCurUserId(ShiroUtil.getCurrentUserId());
		changeVo.setCurUserName(ShiroUtil.getCurrentUserName());
		stockDetailClient.changeWarehouse(changeVo);
		RenderUtil.renderSuccess("success", response);
	}

	@PostMapping(value="findByContractId")
	@ResponseBody
	public StockDetail findByContractId(@RequestParam(value="contractId") String contractId){
		StockDetail detail = stockDetailClient.findByContractId(contractId);
		return detail;
	}

	@PostMapping(value="findApprove")
	@ResponseBody
	public PmApprove findApprove(@RequestParam(value = "optionType") String optionType,@RequestParam(value = "applyId") Long applyId){
		PmApprove approve = null;
		Long approveId = 0L;
		String[] aboutO = new String[]{BasConstants.APPLY_TYPE_O,BasConstants.OPERATE_TYPE_OB};
		String[] aboutI = new String[]{BasConstants.APPLY_TYPE_I,BasConstants.OPERATE_TYPE_IS,BasConstants.OPERATE_TYPE_IC,BasConstants.OPERATE_TYPE_IB};
		if(Arrays.asList(aboutO).contains(optionType)){
			ApplyDeliveryOut out = applyDeliveryOutClient.getEntity(applyId);
			approveId = out.getApproveId();
		}else if(Arrays.asList(aboutI).contains(optionType)){
			ApplyDeliveryIn in = applyDeliveryInClient.getEntity(applyId);
			approveId = in.getApproveId();
		}
		approve = approveClient.getEntity(approveId);
		return approve;
	}

	//打开库存明细详情tab框
	@RequestMapping(value="detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model) {
		StockDetail stockDetail = stockDetailClient.getEntity(id);
		model.addAttribute("stockTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_STOCKTYPE)));
		model.addAttribute("spotTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SPOTTYPE)));
		model.addAttribute("entity", stockDetail);
		return "bas/stockDetail-editor";
	}

	//保存所更改的库存类型
	@RequestMapping(value="saveEditor",method = RequestMethod.POST)
	public void saveEditor(StockDetail stockDetail,HttpServletRequest request,HttpServletResponse response){
		try {
			StockDetail entity = stockDetailClient.getEntity(stockDetail.getId());
			entity.setStockType(stockDetail.getStockType());
			if(BasConstants.DICT_TYPE_STOCKTYPE_QH.equals(stockDetail.getStockType())){
				entity.setSpotType(null);
			}else{
				entity.setSpotType(stockDetail.getSpotType());
			}
			stockDetailClient.save(entity);
			RenderUtil.renderJson("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			ErrorResp errorResp = BasicErrorController.getErrorResp(e);
			RenderUtil.renderFailure(errorResp + e.getMessage(), response);
		}
	}
	@RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(RptStockDetailReport searchVo, HttpServletRequest request,HttpServletResponse response) throws ApplicationException {
		 initSearch(searchVo, request);
		 int batchSize = 500;
		 searchVo.setRows(batchSize);
		 searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		 PageDown<RptStockDetailReport> page = stockInventoryClient.findStockDetailPage(searchVo);
		 //Page<StockDetailVo> page = stockDetailClient.findPageVo(searchVo);
		 PageDown<RptStockDetailReport> pageVo = preStockDetailData(page);
		 String title ="库存明细查询";

		 String[] titles=new String[] {"品名","牌号","厂商","仓库","合同编号","我方抬头","供货商","采购数量(吨)","入库数量(吨)","出库数量(吨)","单价(元)","在途/现货","库存类型","货权类型","业务员","事业部","创建时间"};
		 String[] attrs=new String[] {"productName","brandNumber","factoryName","warehouseName","contractNo","ourCompanyName","buyCompanyName","availableNumber","deliveryInNumber","deliveryOutNumber",
				 "dealPrice","productAttr","stockType","spotType","bizUserName","deptName","createdDate"};
		 int [] widths =new int[] {15,15,20,15,15,20,15,15,15,15,15,15,15,15,15,15,20};
		 Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
		 // 生成一个表格
		 Sheet sheet = workbook.createSheet(title);
		 // 设置表格默认列宽度为 15 个字节
		 sheet.setDefaultColumnWidth(15);
		 // 产生表格标题行
		 // 生成一个样式
		 CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
		 /** 创建表头 */
		 int[] widthes = new int[titles.length];
		 for (int i = 0; i < titles.length; i++) {
		    widthes[i] = widths[i];
		 }
		 PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
		 int start =0;
		 while (pageVo!=null && pageVo.getContent().size()>0) {
			 PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,DateOperator.FORMAT_STR_WITH_TIME);
			 if (pageVo.hasNext()) {
				 searchVo.setPage(searchVo.getPage()+1);
				 page = stockInventoryClient.findStockDetailPage(searchVo);
				 pageVo = preStockDetailData(page);
				 start += batchSize;
			 }else {
				 pageVo=null;
			 }
		 }

	    try {
	      PoiExcelUtil.write(workbook, response, title);
	    } catch (IOException e) {
	      logger.error(e.getMessage(),e);
	    }

	}

	private PageDown<RptStockDetailReport> preStockDetailData(PageDown<RptStockDetailReport> pageVo){
		DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> sydDeptList = authOpenFacade.findDeptAll(deptSearchVo);
		if(pageVo!=null && pageVo.getContent().size()>0){
			for (RptStockDetailReport report : pageVo.getContent()) {
				report.setProductAttr(DictUtil.getValue(BasConstants.STOCK__PRODUCT_ATTR, report.getProductAttr()));
				report.setStockType(DictUtil.getValue(BasConstants.DICT_TYPE_STOCKTYPE, report.getStockType()));
				report.setSpotType(DictUtil.getValue(BasConstants.DICT_TYPE_SPOTTYPE, report.getSpotType()));
				for (SysDeptSdk sysDept : sydDeptList) {
					Long deptId = report.getDeptId();
					if(deptId != null && deptId.equals(sysDept.getDeptId())){
						report.setDeptName(sysDept.getDeptName());
					}
				}
			}
		}
		return pageVo;
	}

	@PostMapping("findByCondition")
	public void findByCondition(DeliveryOutChangeVo vo,HttpServletResponse response){
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<StockDetail> page = stockDetailClient.findByCondition(vo);
		JsonEasyUI.renderJson(response, page);
	}
}
