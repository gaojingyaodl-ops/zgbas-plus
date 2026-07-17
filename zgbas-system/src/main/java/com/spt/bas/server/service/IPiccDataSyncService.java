package com.spt.bas.server.service;

import com.spt.bas.client.vo.ImportExcelVo;

import java.util.List;

public interface IPiccDataSyncService {

    List<String> initPiccData(ImportExcelVo importExcelVo);

}
