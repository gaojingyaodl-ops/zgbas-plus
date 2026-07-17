package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.hsoft.push.sdk.remote.PushClientHttp;
import com.hsoft.push.sdk.vo.PushRequest;
import com.hsoft.push.sdk.vo.PushTarget;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasReceive;
import com.spt.bas.client.vo.BasReceiveVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BasReceiveDao;
import com.spt.bas.server.service.IBasContractService;
import com.spt.bas.server.service.IBasReceiveService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(readOnly = true)
public class BasReceiveServiceImpl extends BaseService<BasReceive> implements IBasReceiveService {
	@Autowired
	private BasReceiveDao basReceiveDao;
	@Autowired
	private IBasContractService contractService;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private PushClientHttp pushRemote;
	@Override
	public BaseDao<BasReceive> getBaseDao() {
		return basReceiveDao;
	}

	@Override
	public Class<BasReceive> getEntityClazz() {
		return BasReceive.class;
	}

	@Override
	public void saveReceive(BasContract bs) {
		//定金收款记录
		BasReceive brBond = new BasReceive();
		brBond.setContractId(bs.getId());
		brBond.setBusinessNo(bs.getBusinessNo());
		brBond.setContractNo(bs.getContractNo());
		brBond.setReceiveDate(bs.getPayTime());
		brBond.setCompanyId(bs.getOppCompanyId());
		brBond.setCompanyName(bs.getOppCompanyName());
		brBond.setProductCode(bs.getProductCode());
		brBond.setProductName(bs.getProductName());
		brBond.setCloseFlg(bs.getCloseFlg());
		brBond.setStatus(BasConstants.RECEIVE_STATUS_N);
		//定金比率为零则表示全款支付
		if(bs.getBondRate().compareTo(BigDecimal.ZERO)==0){
			brBond.setReceiveAmount(bs.getDealAmount());
			brBond.setReceiveType(BasConstants.RECEIVE_TYPE_A);
			basReceiveDao.save(brBond);
		}else{
			//定金为合同总价与比率的乘积
			BigDecimal bond = bs.getDealAmount().multiply(bs.getBondRate().divide(new BigDecimal("100")));
			brBond.setReceiveAmount(bond);
			brBond.setReceiveType(BasConstants.RECEIVE_TYPE_B);
			basReceiveDao.save(brBond);
			//余款收款记录
			BasReceive brR = new BasReceive();
			BeanUtils.copyProperties(brBond, brR);
			brR.setId(null);
			//付款金额为合同总价与定金的差
			brR.setReceiveAmount(bs.getDealAmount().subtract(bond));
			brR.setReceiveType(BasConstants.RECEIVE_TYPE_R);
			basReceiveDao.save(brR);
		}

	}

	@Override
	@ServerTransactional
	public void saveStatus(BasReceive receive) {
		// TODO Auto-generated method stub
		basReceiveDao.save(receive);
		BasContract contract = contractService.getEntity(receive.getContractId());

		receiveNotice(receive, contract.getMatchUserId());
	}

	/**
	 * 收款通知
	 * @param userId
	 */
	private void receiveNotice(BasReceive receive,Long userId) {
		if(userId!=null){
			SysUserSdk sysUser = authOpenFacade.findUserById(userId);
			if(sysUser!=null && StringUtils.isNotBlank(sysUser.getEmail())) {
				PushRequest req = new PushRequest();
				req.setModule("S");
				req.setPushType("basReceiveNotice");//收款通知
				req.setSubmitUserId("sys");
				List<PushTarget> lst = new ArrayList<>();
				lst.add(new PushTarget(String.valueOf(userId), sysUser.getPhonenumber(), sysUser.getEmail()));
				req.setTargets(lst);
				Map<String, Object> param = new HashMap<>();
				param.put("contractNo", receive.getContractNo());//合同编号
				param.put("payType", DictUtil.getValue(BasConstants.RECEIVE_TYPE, receive.getReceiveType()));//收款类型
				param.put("payAmount", receive.getReceiveAmount());//收款金额
				req.setParam(param);
				try {
					pushRemote.send(req);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public BasReceive findByReceiveVo(BasReceiveVo vo) {
		return basReceiveDao.findByReceiveVo(vo.getContractNo(),vo.getReceiveType());
	}

}

