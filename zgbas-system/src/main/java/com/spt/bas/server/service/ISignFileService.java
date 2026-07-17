package com.spt.bas.server.service;

import com.spt.bas.client.entity.SignFile;
import com.spt.bas.client.vo.sign.SignFileSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;

public interface ISignFileService extends IBaseService<SignFile> {
    Page<SignFile> findPageSignFile(SignFileSearchVo searchVo);

    SignFile findByCfcaContractNo(String cfcaContractNo);

    SignFile generateSignature(Long signId, Integer signAuthType ) throws ApplicationException;

    SignFile refreshSignFile(String cfcaContractNo) throws ApplicationException;

    SignFile findByAllLimit();

}
