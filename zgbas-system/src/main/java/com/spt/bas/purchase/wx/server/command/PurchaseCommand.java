package com.spt.bas.purchase.wx.server.command;

import com.spt.bas.purchase.wx.server.payload.ContractNoRequest;
import com.spt.bas.purchase.wx.server.service.ISuccessContractService;
import com.spt.bas.purchase.wx.server.service.IUserInfoService;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.cmd.ICommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PurchaseCommand implements ICommand {
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private ISuccessContractService successContractService;
    @Override
    public boolean executeCommand(String commandline) throws Exception {
        if (StringUtils.isNotBlank(commandline)) {
            if (commandline.contains("axq ")) {
                String[] args = commandline.split(" ");
                ContractNoRequest contractNoRequest = new ContractNoRequest();
                contractNoRequest.setContractNo(args[1]);
                userInfoService.axqContract(contractNoRequest);
                return true;
            }else if (commandline.contains("successContract")){
                // 已完成未签署合同定时任务
                successContractService.doSuccessContract();
                return true;
            }else if (commandline.contains("doSuccessDebtCertificate")){
                // 应收账款债权完成签署定时任务
                successContractService.doSuccessDebtCertificate();
                return true;
            }else if (commandline.equalsIgnoreCase("cache")){
                // 刷新缓存
                LocalCacheManager.refreshAll();
                return true;
            }if (commandline.contains("doReceiveGood")) {
                // 确认收货签收单完成签署
                successContractService.doReceiveGood();
                return true;
            } if (commandline.contains("userInfoService")) {
                ContractNoRequest contractNoRequest = new ContractNoRequest();
                contractNoRequest.setContractNo("SPTS221212001");
                userInfoService.axqContract(contractNoRequest);
                
            }
        }
        return false;
    }
}
