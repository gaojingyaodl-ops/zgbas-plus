package com.spt.bas.web.excel;

import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

public class InvoiceStyleStrategy extends HorizontalCellStyleStrategy {
    private final WriteCellStyle headWriteCellStyle;

    public InvoiceStyleStrategy(WriteCellStyle headWriteCellStyle) {
        this.headWriteCellStyle = headWriteCellStyle;
    }

    @Override
    protected void setHeadCellStyle(CellWriteHandlerContext context) {
        // 根据行索引为不同级别的表头应用不同样式
        if (context.getRowIndex() == 0) {
            context.getRow().setHeight((short) (200 * 20));
            // 设置背景色为蓝色
            headWriteCellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
            // 设置对齐方式为左对齐
            headWriteCellStyle.setHorizontalAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
            WriteFont writeFont = new WriteFont();
            writeFont.setFontHeightInPoints((short) 11);
            writeFont.setFontName("宋体");
            writeFont.setColor(IndexedColors.RED.getIndex());
            writeFont.setBold(false);
            // 设置字体大小为10号
            headWriteCellStyle.setWriteFont(writeFont);
        } else if (context.getRowIndex() == 1) {
            headWriteCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
            WriteFont writeFont = new WriteFont();
            writeFont.setColor(IndexedColors.BLACK.getIndex());
            writeFont.setFontHeightInPoints((short) 12);
            // 设置字体大小为10号
            headWriteCellStyle.setWriteFont(writeFont);
        } else if (context.getRowIndex() == 2) {
            headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
            headWriteCellStyle.setHorizontalAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            WriteFont writeFont = new WriteFont();
            writeFont.setColor(IndexedColors.BLACK.getIndex());
            writeFont.setFontHeightInPoints((short) 11);
            // 设置字体大小为10号
            headWriteCellStyle.setWriteFont(writeFont);
        }
        if (stopProcessing(context)) {
            return;
        }
        WriteCellData<?> cellData = context.getFirstCellData();
        WriteCellStyle.merge(this.headWriteCellStyle, cellData.getOrCreateStyle());
    }

}
