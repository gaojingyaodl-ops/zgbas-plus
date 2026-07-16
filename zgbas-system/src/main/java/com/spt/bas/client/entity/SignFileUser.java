package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "t_sign_file_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SignFileUser extends IdEntity {
    private static final long serialVersionUID = 1L;
    private Long signFileId;    //签署文件表ID
    private String companyName; //签署公司
    private String keyWord;     //签章关键字
    private Integer signOnPage; //签名域页数
    private String signType;    //签署印章类型
    private Date signDate;      //签署日期
    private String status;      //状态
    private String SignPhone;   //签署人手机号
    private String shortUrl;    //签署链接
    private String SignName;   //签署人姓名
    private String offsetCoordX = "30";
    private String offsetCoordY = "-50";
    private String imageWidth = "150";
    private String imageHeight = "150";
    private String signEmail;

    public String getSignName() {
        return SignName;
    }

    public void setSignName(String signName) {
        SignName = signName;
    }

    public Long getSignFileId() {
        return signFileId;
    }

    public void setSignFileId(Long signFileId) {
        this.signFileId = signFileId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Integer getSignOnPage() {
        return signOnPage;
    }

    public void setSignOnPage(Integer signOnPage) {
        this.signOnPage = signOnPage;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getSignPhone() {
        return SignPhone;
    }

    public void setSignPhone(String signPhone) {
        SignPhone = signPhone;
    }

    public String getSignEmail() {
        return signEmail;
    }

    public void setSignEmail(String signEmail) {
        this.signEmail = signEmail;
    }

    public String getOffsetCoordX() {
        return offsetCoordX;
    }

    public void setOffsetCoordX(String offsetCoordX) {
        this.offsetCoordX = offsetCoordX;
    }

    public String getOffsetCoordY() {
        return offsetCoordY;
    }

    public void setOffsetCoordY(String offsetCoordY) {
        this.offsetCoordY = offsetCoordY;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }
}
