package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsTextContent;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsTextContentService extends IBaseService<BsTextContent> {


    BsTextContent findNewTextContentByType(String textType);
}
