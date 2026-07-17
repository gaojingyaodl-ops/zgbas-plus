package com.spt.bas.report.client.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.spt.bas.client.constant.BasConstants;
import lombok.Data;

@Data
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 5)
@HeadFontStyle(fontHeightInPoints = 12)
@ContentFontStyle(fontName="宋体")
@ColumnWidth(25)
@ContentStyle
public class RptInvoiceDetailExcel {
    // 流水号
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"必填（限20字符）","发票流水号"},index = 0)
    @ContentStyle(horizontalAlignment= HorizontalAlignmentEnum.RIGHT)
    private String serialNumber;
    // 项目名称
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"必填（限100字符）","项目名称"},index = 1)
    private String projectName;
    // 商品和服务税收编码
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"必填（限20字符）","商品和服务税收编码"},index = 2)
    private String goodsServicesTaxCode;
    // 规格型号
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"非必填（限40字符）","规格型号"},index = 3)
    private String specificationsModels;
    // 单位
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"有条件必填（限22字符）","单位"},index = 4)
    private String unit;
    // 数量
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"有条件必填（限16字符）最大保留13位小数","数量"},index = 5)
    @ContentStyle(horizontalAlignment= HorizontalAlignmentEnum.RIGHT)
    private String number;
    // 单价
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"有条件必填（限16字符）最大保留13位小数 ","单价"},index = 6)
    @ContentStyle(horizontalAlignment= HorizontalAlignmentEnum.RIGHT)
    private String price;
    // 金额
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"必填（限16字符）最大保留两位小数 ","金额"},index = 7)
    @ContentStyle(horizontalAlignment= HorizontalAlignmentEnum.RIGHT)
    private String amount;
    // 税率
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"必填（限8字符）以小数形式填写如“0.13”代表税率13%","税率"},index = 8)
    @ContentStyle(horizontalAlignment= HorizontalAlignmentEnum.RIGHT)
    private String taxRate;
    // 折扣金额
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"非必填（限16字符）保留两位小数，系统据此添加折扣行","折扣金额"},index = 9)
    @ContentStyle(horizontalAlignment= HorizontalAlignmentEnum.RIGHT)
    private String discountAmount;
    // 是否使用优惠政策
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"非必填（是/否）（限2字符）","是否使用优惠政策"},index = 10)
    private String usePreferentialPolicies;
    // 优惠政策类型
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"非必填（限15字符）","优惠政策类型"},index = 11)
    private String preferentialPoliciesType;
    // 即征即退类型
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_DETAIL,"非必填（限46字符）","即征即退类型"},index = 12)
    private String immediateTaxType;
}
