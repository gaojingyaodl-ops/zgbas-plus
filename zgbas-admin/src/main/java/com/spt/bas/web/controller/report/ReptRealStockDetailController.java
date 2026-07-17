package com.spt.bas.web.controller.report;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IStockDetailClient;
import com.spt.bas.report.client.entity.RptStockDetailReport;
import com.spt.bas.report.client.remote.IRptStockInventoryClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 实际库存明细
 * @author
 *
 */
@Controller
@RequestMapping(value = "/report/realStockDetail")
public class ReptRealStockDetailController extends SingleCrudControll<StockDetail, BaseVo>{

	@Autowired
	private IStockDetailClient stockDetailClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IRptStockInventoryClient stockInventoryClient;
	@Override
	public BaseClient<StockDetail> getService() {
		return stockDetailClient;
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
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		model.addAttribute("deptAllJson", JsonUtil.obj2Json(deptList));
		//更改库存类型权限
		boolean canUpdate = false;
		if(ShiroUtil.isPermitted(PermissionEnum.APPROVE_CTR_UPDATE_STOCKTYPE.getPermissionCode())){
			canUpdate = true;
		}
		model.addAttribute("canUpdate", canUpdate);
		
		return "report/realStockDetail";
	}
	
	/** 列表查询方法 */
	@RequestMapping(value = "listVo")
	public void listVo(RptStockDetailReport searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptStockDetailReport> page = stockInventoryClient.findRealStockDetailPage(searchVo);
		JsonEasyUI.renderJson(response, page,null,null);
		
	}
	
	@RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(RptStockDetailReport searchVo, HttpServletRequest request,HttpServletResponse response) throws ApplicationException {
		 initSearch(searchVo, request);
		 int batchSize = 500;
		 searchVo.setRows(batchSize);
		 searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		 PageDown<RptStockDetailReport> page = stockInventoryClient.findRealStockDetailPage(searchVo);
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
				 page = stockInventoryClient.findRealStockDetailPage(searchVo);
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
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		if(pageVo!=null && pageVo.getContent().size()>0){
			for (RptStockDetailReport report : pageVo.getContent()) {
				report.setProductAttr(DictUtil.getValue(BasConstants.STOCK__PRODUCT_ATTR, report.getProductAttr()));
				report.setStockType(DictUtil.getValue(BasConstants.DICT_TYPE_STOCKTYPE, report.getStockType()));
				report.setSpotType(DictUtil.getValue(BasConstants.DICT_TYPE_SPOTTYPE, report.getSpotType()));
				for (SysDeptSdk sysDept : deptList) {
					Long deptId = report.getDeptId();
					if(deptId != null && deptId.equals(sysDept.getDeptId())){
						report.setDeptName(sysDept.getDeptName());
					}
				}
			}
		}
		return pageVo;
	}
}
