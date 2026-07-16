package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author MoonLight
 * @Date 2023/3/23 9:09
 * @Version 1.0
 */
public class ApplyDeliveryExportVo {
    private Long contractId;

    private List<ExportVo> exportList;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public List<ExportVo> getExportList() {
        return exportList;
    }

    public void setExportList(List<ExportVo> exportList) {
        this.exportList = exportList;
    }

    public ApplyDeliveryExportVo() {
    }

    public static class ExportVo {
        private BigDecimal dealNumber;
        private Date deliveryDate;

        public BigDecimal getDealNumber() {
            return dealNumber;
        }

        public void setDealNumber(BigDecimal dealNumber) {
            this.dealNumber = dealNumber;
        }

        public Date getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(Date deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public ExportVo() {
        }
    }
}
