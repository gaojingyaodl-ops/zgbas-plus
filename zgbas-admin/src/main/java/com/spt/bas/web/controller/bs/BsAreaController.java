package com.spt.bas.web.controller.bs;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.spt.bas.client.entity.BsArea;
import com.spt.bas.client.remote.IBsAreaClient;
import com.spt.bas.client.util.TreeNode;
import com.spt.bas.client.vo.CompanyAreaVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;

@Controller
@RequestMapping(value = "/bs/area")
public class BsAreaController extends PageController<BsArea, BaseVo>{

	@Autowired
	private IBsAreaClient areaClient;
	
	@Override
	public BaseClient<BsArea> getService() {
		// TODO Auto-generated method stub
		return areaClient;
	}
	
	@ResponseBody
	@RequestMapping(value = "getArea", method = RequestMethod.GET)
	public List<TreeNode> getArea(HttpServletRequest request, HttpServletResponse response) {
		String pid = request.getParameter("id");
		List<BsArea> bsArea = null;
		if(pid.equals("0") || pid == "0")
			bsArea = areaClient.findTopLevel();//最顶层目录
		else
			bsArea = areaClient.findByParentId(pid);//下级目录
		
		List<TreeNode> result = Lists.newArrayList();
		result.addAll(getTreeNodes(bsArea,pid));
		//String bsAreastr=JsonUtil.obj2Json(result);
		return result;
	}
	
	
	/**
	 * 转为前台需要的json数据
	 * 
	 * @param list
	 * @param pid
	 */
	public List<TreeNode> getTreeNodes(List<BsArea> list,String pid){
		TreeNode node = null;
		List<TreeNode> children = Lists.newArrayList();
		List<BsArea> bsArea = null;
		for(BsArea area : list){
			node = new TreeNode();
			if(area.getGrand().equals("3")){
				node.setState("");
			}else if(area.getGrand().equals("2")){//如果grand为2查询是否有下级目录
				bsArea = areaClient.findByParentId(area.getCode());
				if(bsArea.size()>1){
					node.setState("closed");
				}else{
					node.setState("");
				}
				
			}else{
				node.setState("closed");
			}
			node.setId(area.getCode());
			node.setText(area.getName());
			
			children.add(node);
		}
		return children;
	}
	
	/**
	 * 查询省市区
	 * @param id
	 */
	@ResponseBody
	@RequestMapping(value = "findGetAreaVo", method = RequestMethod.POST)
	public String findGetAreaVo(@RequestParam("id") Long id){
		CompanyAreaVo vo = areaClient.getAreaVo(id);
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
		return area;
	}

}
