package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyProtocolDocument;
import com.spt.bas.server.service.IApplyProtocolDocumentService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author MoonLight
 * @Date 2024/5/21 16:20
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/apply/protocolDocument")
public class ApplyProtocolDocumentApi extends BaseApi<ApplyProtocolDocument> {
    @Resource
    private IApplyProtocolDocumentService applyProtocolDocumentService;

    @Override
    public IDataService<ApplyProtocolDocument> getService() {
        return applyProtocolDocumentService;
    }
}
