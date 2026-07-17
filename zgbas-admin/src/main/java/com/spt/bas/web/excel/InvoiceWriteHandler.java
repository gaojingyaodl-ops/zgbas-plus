package com.spt.bas.web.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.Map;

public class InvoiceWriteHandler implements SheetWriteHandler {
    private Map<Integer, String[]> mapDropDown;


    public InvoiceWriteHandler(Map<Integer, String[]> mapDropDown) {
        this.mapDropDown = mapDropDown;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        //获取工作簿
        Sheet sheet = writeSheetHolder.getSheet();
        ///开始设置下拉框
        DataValidationHelper helper = sheet.getDataValidationHelper();
        if (mapDropDown != null && !mapDropDown.isEmpty()) {
            //设置下拉框
            for (Map.Entry<Integer, String[]> entry : mapDropDown.entrySet()) {
                /*起始行、终止行、起始列、终止列  起始行为1即表示表头不设置**/
                CellRangeAddressList addressList = new CellRangeAddressList(3, 65535, entry.getKey(), entry.getKey());
                /*设置下拉框数据**/
                DataValidationConstraint constraint = helper.createExplicitListConstraint(entry.getValue());
                DataValidation dataValidation = helper.createValidation(constraint, addressList);
                sheet.addValidationData(dataValidation);
            }
        }
    }
}
