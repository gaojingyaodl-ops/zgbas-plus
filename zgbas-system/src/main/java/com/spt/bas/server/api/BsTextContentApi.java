package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsTextContent;
import com.spt.bas.server.service.IBsTextContentService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "text/content")
public class BsTextContentApi extends BaseApi<BsTextContent> {
    @Autowired
    private IBsTextContentService bsTextContentService;

    @Override
    public IDataService<BsTextContent> getService() {
        return bsTextContentService;
    }


    @PostMapping(value = "findNewTextContentByType")
    public BsTextContent findNewTextContentByType(@RequestBody String textType){
        return bsTextContentService.findNewTextContentByType(textType);
    }

}
