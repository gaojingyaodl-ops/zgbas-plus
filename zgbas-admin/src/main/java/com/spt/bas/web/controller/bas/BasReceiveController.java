package com.spt.bas.web.controller.bas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasReceive;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBasReceiveClient;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.vo.BasReceiveVo;
import com.spt.bas.client.vo.BsCompanySearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;

/**
 * 收款信息
 * */
@Controller
@RequestMapping(value = "/bas/receive")
public class BasReceiveController extends PageController<BasReceive,BaseVo>{
	@Autowired
	private IBasReceiveClient receiveClient;
	@Autowired
	private IBsCompanyClient companyClient;
	@Override
	public BaseClient<BasReceive> getService() {
		return receiveClient;
	}
	
	@RequestMapping(value = "")
	public String index(Model model) {
		model.addAttribute("receiveTypeJson", 
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.RECEIVE_TYPE)));
		model.addAttribute("receiveStatusJson", 
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.RECEIVE_STATUS)));
		BsCompanySearchVo queryVo =new BsCompanySearchVo();
		queryVo.setRows(1000);
		queryVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		queryVo.setUserId(ShiroUtil.getCurrentUserId());
		queryVo.setMode(BasConstants.COMPANY_SEARCH_MODE_MY);
		PageDown<BsCompany> pageCompany= companyClient.findPageCompnay(queryVo);
		model.addAttribute("companyJson", JsonUtil.obj2Json(pageCompany.getContent()));
		return "bas/receive";
	}
	
	@RequestMapping(value = "haveReceived/{id}")
	public void haveReceived(@PathVariable("id") Long id,HttpServletResponse response) {
		try {
			BasReceive entity = receiveClient.getEntity(id);
			entity.setStatus(BasConstants.RECEIVE_STATUS_D);
			//BasReceive receive = receiveClient.save(entity);	
			//保存状态并发送通知
			receiveClient.saveStatus(entity);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			RenderUtil.renderFailure("failure", response);	
		}	
	}
	
	@RequestMapping("saveEdit")
	public void saveEdit (@RequestParam("param") String param,HttpServletResponse response){
		List<BasReceive> list=JSON.parseArray(param, BasReceive.class);
		try {
			for(BasReceive entity:list){
				//更新分批收款合同的余额(做添加操作时)
				BasReceiveVo vo = new BasReceiveVo();
				vo.setContractNo(entity.getContractNo());
				vo.setReceiveType(BasConstants.RECEIVE_TYPE_R);
				BasReceive receive = receiveClient.findByReceiveVo(vo);
				if(receive==null){
					vo.setReceiveType(BasConstants.RECEIVE_TYPE_A);
					receive = receiveClient.findByReceiveVo(vo);
				}
				receive.setReceiveAmount(receive.getReceiveAmount().subtract(entity.getReceiveAmount()));
				receiveClient.save(receive);
				//保存添加的收款记录
				entity.setContractId(receive.getContractId());
				entity.setCompanyId(receive.getCompanyId());
				entity.setProductCode(receive.getProductCode());
				entity.setCloseFlg(receive.getCloseFlg());
				entity.setReceiveType(BasConstants.RECEIVE_TYPE_Z);
				entity.setStatus(BasConstants.RECEIVE_STATUS_N);
				receiveClient.save(entity);
			}
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			e.printStackTrace();
			RenderUtil.renderFailure("fail", response);
		}
		
	}
	
	@RequestMapping(value = "choose")
	public String choose(Model model) {
		return "bas/receive-choose";
	}
	
	@RequestMapping(value = "listChoose")
	public void listChoose(HttpServletRequest request, HttpServletResponse response) {
		PageSearchVo searchVo =new PageSearchVo();
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQS_status", BasConstants.RECEIVE_STATUS_N);
		String[] str = new String[]{BasConstants.RECEIVE_TYPE_R,BasConstants.RECEIVE_TYPE_A};
		searchParams.put("INS_receiveType", str);
		searchVo.setSearchParams(searchParams);
		PageDown<BasReceive> page = getService().findPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}
}
