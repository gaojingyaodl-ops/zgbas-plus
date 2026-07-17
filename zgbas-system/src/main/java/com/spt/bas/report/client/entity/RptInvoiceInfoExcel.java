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
public class RptInvoiceInfoExcel {
    // 流水号
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"必填（限20字符）","发票流水号"},index = 0)
    @ContentStyle(horizontalAlignment= HorizontalAlignmentEnum.RIGHT)
    private String serialNumber;
    // 发票类型
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"必填（限10字符）","发票类型"},index=1)
    private String invoiceType;
    // 特定业务类型
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限10字符）","特定业务类型"},index=2)
    private String specificBusinessType;
    // 是否含税
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（是/否）（限2字符）","是否含税"},index=3)
    private String taxFlg;
    // 受票方自然人标识
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填 （是/否）（限2字符）","受票方自然人标识"},index=4)
    private String naturalPersonFlg;
    // 购买方名称
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"必填（限100字符）","购买方名称"},index=5)
    private String companyName;
    // 证件类型
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限20字符）","证件类型"},index=6)
    private String certificateType;
    // 购买方纳税人识别号
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"专票必填（限20字符）","购买方纳税人识别号"},index=7)
    private String purchaserTaxNumber;
    // 购买方地址
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"有条件必填（限100字符）","购买方地址"},index=8)
    private String purchaserAddr;
    // 购买方电话
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"有条件必填（限50字符）","购买方电话"},index=9)
    private String purchaserTelephone;
    // 购买方开户银行
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限100字符）","购买方开户银行"},index = 10)
    private String purchaserBankName;
    // 购买方开户银行账户
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限100字符） ","购买方开户银行账户"},index=11)
    private String purchaserBankAccount;
    // 备注
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限230字符） ","备注"},index=12)
    private String remark;
    // 报废产品销售类型
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"有条件必填（限24字符） ","报废产品销售类型"},index=13)
    private String scrapProductSalesType;
    // 是否展示购买方地址电话银行账号
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限20字符） ","是否展示购买方地址电话银行账号"},index=14)
    private String displayPAddTelAcc;
    // 销售方开户行
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限100字符） ","销售方开户行"},index = 15)
    private String sellBankName;
    // 销售方银行账号
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限60字符） ","销售方银行账号"},index = 16)
    private String sellBankAccount;
    // 是否展示销售方地址电话银行账号
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限20字符） ","是否展示销售方地址电话银行账号"},index=17)
    private String displaySAddTelAcc;
    // 购买方邮箱
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限72字符） ","购买方邮箱"},index=18)
    private String purchaserEmail;
    // 购买方经办人姓名
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限150字符） ","购买方经办人姓名"},index = 19)
    private String purchaserHandlerName;
    // 购买方经办人证件类型
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限40字符） ","购买方经办人证件类型"},index = 20)
    private String purchaserHandlerCertificateType;
    // 购买方经办人证件号码
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限30字符） ","购买方经办人证件号码"},index = 21)
    private String purchaserHandlerIdNumber;
    // 经办人国籍(地区)
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限40字符） ","经办人国籍(地区)"},index = 22)
    private String handlerNationalAddr;
    // 经办人自然人纳税人识别号
    @ExcelProperty(value={BasConstants.INVOICE_EXCEL_INFO,"非必填（限20字符） ","经办人自然人纳税人识别号"},index = 23)
    private String handlerTaxNumber;
    // 放弃享受减按1%征收率原因
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_INFO,"非必填（限100字符） ","放弃享受减按1%征收率原因"},index = 24)
    private String forgoTheLevy;
    // 收款人
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_INFO,"非必填（限16字符） ","收款人"},index = 25)
    private String payee;
    // 复核人
    @ExcelProperty(value = {BasConstants.INVOICE_EXCEL_INFO,"非必填（限16字符）  ","复核人"},index = 26)
    private String reviewer;
}
