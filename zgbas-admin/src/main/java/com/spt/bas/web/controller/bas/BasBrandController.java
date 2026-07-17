package com.spt.bas.web.controller.bas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.client.entity.BsProductType;
import com.spt.bas.client.remote.IBasBrandClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.vo.BasBrandSearchVo;
import com.spt.bas.client.vo.api.RespVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.ImportExcelUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.RenderUtil;

/**
 * 牌号管理
 *
 */
@Controller
@RequestMapping("/bas/brand")
public class BasBrandController extends SingleCrudControll<BasBrand, BaseVo>{

	@Autowired
	private IBasBrandClient basBrandClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	
	@Override
	public BaseClient<BasBrand> getService() {
		return basBrandClient;
	}
	@Override
	protected void preInsert(BasBrand e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
	/**
	 * 牌号列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value="")
	public String index(Model model){
		//货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
			model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		model.addAttribute("productJson",
				JsonUtil.obj2Json(bsProductTypeClient.findAll()));
		List<BasBrand> brand = basBrandClient.findBrand();
//		List<String> brandNumberList = brand.stream().map(BasBrand::getBrandNumber).collect(Collectors.toList());
		model.addAttribute("brandSortJson",JsonUtil.obj2Json(brand));
		return "bas/brand";
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	
	@RequestMapping(value="findBrand")
	public void findBrand(@RequestParam("productCode") String productCode,HttpServletResponse response){
		BasBrandSearchVo vo=new BasBrandSearchVo();
		vo.setProductCd(productCode);
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<BasBrand> entity=basBrandClient.findsBrand(vo);
		RenderUtil.renderJson(entity,response);
	}
	
	
	//导入excel
	@ResponseBody
	@RequestMapping(value="uploadBrandExcel",method = RequestMethod.POST)
	public RespVo<?> uploadBrandExcel(@RequestParam(value="file",required = false) MultipartFile file,HttpServletRequest request, HttpServletResponse response){
		List<BasBrand> importList = ImportExcelUtil.getExcelInfo(file);
		List<BsProductType> productTypeList = bsProductTypeClient.findAll();
		BasBrandSearchVo searchVo =new BasBrandSearchVo();
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<String> brandNumberList = basBrandClient.findBrandNumberList(searchVo);
		RespVo<String> vo = canUpLoad(importList,productTypeList,brandNumberList);
		if(vo == null){
			for (BasBrand basBrand : importList) {
				for (BsProductType productType : productTypeList) {
					if(basBrand.getProductCd().equals(productType.getTypeName())){
						basBrand.setProductCd(productType.getTypeCode());
						basBrandClient.save(basBrand);
					}
				}
			}
			RespVo<String> upvo = new RespVo<>();
			upvo.setData("上传成功！");
			return upvo;
		}
		return vo;
	}
	
	//判断上传的文件数据是否可以上传
    public RespVo<String> canUpLoad(List<BasBrand> importList,List<BsProductType> productTypeList,List<String> brandNumberList){
    	RespVo<String> vo = new RespVo<>();
    	if(importList!=null){
			List<String> typeNameList = new ArrayList<String>();
			for (BsProductType productType : productTypeList) {
				typeNameList.add(productType.getTypeName());
			}
			for( int i=0; i<importList.size(); i++) {
				BasBrand brand = importList.get(i);
				if(!typeNameList.contains(brand.getProductCd())){
					vo.setMessage("fail");
					vo.setData("Excel表格中第"+(i+1)+"行商品不存在！");
					return vo;
				}
				if(brandNumberList.contains(brand.getBrandNumber())){
					vo.setMessage("fail");
					vo.setData("Excel表格中第"+(i+1)+"行牌号已存在！");
					return vo;
				}
			}
		}else{
			vo.setMessage("fail");
			vo.setData("上传文件为空！");
			return vo;
		}
    	return null;
    }
	
}
