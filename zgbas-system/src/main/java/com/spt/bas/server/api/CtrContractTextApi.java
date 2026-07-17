package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrContractText;
import com.spt.bas.client.vo.DcContractText;
import com.spt.bas.client.vo.ExtraBankTextVo;
import com.spt.bas.client.vo.MatchContractTextVo;
import com.spt.bas.server.service.ICtrContractTextService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "ctr/contractText")
public class CtrContractTextApi extends BaseApi<CtrContractText> {
	@Autowired
	private ICtrContractTextService ctrContractTextService;

	@Override
	public IBaseService<CtrContractText> getService() {
		return ctrContractTextService;
	}

	@PostMapping("findContractText")
	public CtrContractText findContractText(@RequestBody CtrContractText text){
		return ctrContractTextService.findByContractIdAndContractType(text.getCtrContractId(),text.getContractType());
	}

	@PostMapping("synthesisMathContractText")
	public String synthesisMathContractText(@RequestBody MatchContractTextVo textVo) throws ApplicationException {
		return ctrContractTextService.synthesisMathContractText(textVo);
	}

	@PostMapping("dealWithExtraBank")
	public DcContractText dealWithExtraBank(@RequestBody ExtraBankTextVo extraBankTextVo){
		return ctrContractTextService.dealWithExtraBank(extraBankTextVo.getTextVo(), extraBankTextVo.getApplyMatch(), extraBankTextVo.getEntity(), extraBankTextVo.getTextKind());
	}
}

