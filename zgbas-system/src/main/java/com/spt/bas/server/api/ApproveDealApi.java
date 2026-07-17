package com.spt.bas.server.api;

import com.spt.auth.sdk.cache.UserCache;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApproveDeal;
import com.spt.bas.client.vo.ApproveDealQueryVo;
import com.spt.bas.client.vo.ApproveDealSerachVo;
import com.spt.bas.server.service.IApproveDealService;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmProcess;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "approve/deal")
public class ApproveDealApi extends BaseApi<ApproveDeal> {
	@Autowired
	private IApproveDealService approveDealService;
	@Autowired
	private PmProcessDao  processDao;

	@Override
	public IBaseService<ApproveDeal> getService() {
		return approveDealService;
	}

	@PostMapping(value = "findPageVo")
	public Page<ApproveDealQueryVo> findPageVo(@RequestBody ApproveDealSerachVo queryVo){
		Map<String, Object> map=new HashMap<>();
		if(queryVo.getSearchParams()!=null){
			map=queryVo.getSearchParams();
		}

		map.put("LIKES_relaUserId", "|"+queryVo.getUserId()+"|");
		queryVo.setSearchParams(map);

		Page<ApproveDeal> page=	approveDealService.findPage(queryVo);
		List<ApproveDealQueryVo> listVo=new ArrayList<>();

		for(ApproveDeal entity:page.getContent()){
			ApproveDealQueryVo vo=new ApproveDealQueryVo();
			BeanUtils.copyProperties(entity, vo);
			String userName=UserCache.getUserName(entity.getRelaUserId());
			vo.setUserName(userName);
			String dealTypeName=DictUtil.getValue(BasConstants.APPLY_TYPE, entity.getDealType());
			vo.setDealTypeName(dealTypeName);
			PmProcess process=processDao.findByProcessCodeAndEnterpriseId(entity.getProcessCode(), entity.getEnterpriseId());
			if (process != null) {
				vo.setProcessId(process.getId());
				vo.setProcessName(process.getProcessName());
			}
			listVo.add(vo);
		}
		PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
		Page<ApproveDealQueryVo> pageVo = new PageImpl<>(listVo, pageRequest_new, page.getTotalElements());
		return pageVo;

	}

}

