package com.spt.bas.purchase.wx.server.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.CardType;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.vo.IdentityCardVo;
import com.spt.bas.purchase.wx.server.vo.LicenseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 *  ocrHelper
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-20 14:21
 */
@Component
public class OcrHelper {
    private static Logger logger = LoggerFactory.getLogger(OcrHelper.class);

    @Autowired
    private OcrUtils ocrUtils;

    /**
     * ocr识别营业执照
     * @param imageBase64
     * @return
     */
    public LicenseVo ocrLicenses(String imageBase64) {
        LicenseVo licenseVo = new LicenseVo();
        String json = ocrUtils.ocrBusinessLicense(imageBase64);
        if (StrUtil.isEmpty(json)) {
            throw new BaseException(Status.OCR_ERROR);
        }
        JSONObject jsonObject = JSONUtil.parseObj(json);
        Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            if (StrUtil.equals(next.getKey(), "name")) {
                String companyName = next.getValue().toString();
                if ("FailInRecognition".equals(companyName)) {
                    throw new BaseException(Status.OCR_ERROR);
                }
                companyName = StrUtil.replace(companyName, "(", "（");
                companyName = StrUtil.replace(companyName, ")", "）");
                licenseVo.setCompanyName(companyName);
                continue;
            }
            if (StrUtil.equals(next.getKey(), "reg_num")) {
                licenseVo.setLicenseNumber(next.getValue().toString());
                continue;
            }
            if (StrUtil.equals(next.getKey(), "address")) {
                licenseVo.setAddress(next.getValue().toString());
                continue;
            }
            if (StrUtil.equals(next.getKey(),"person")) {
                String person = next.getValue().toString();
                if ("FailInRecognition".equals(person)) {
                    throw new BaseException(Status.OCR_ERROR);
                }
                licenseVo.setLegalRepresent(person);
                continue;
            }
            if (StrUtil.equals(next.getKey(), "success")) {
                if (!(Boolean) next.getValue()) {
                    throw new BaseException(Status.OCR_ERROR);
                }
            }
        }
        return licenseVo;
    }


    /**
     * ocr识别身份证
     * @param ImageBase64 图片base64位
     * @param direction 方向
     * @return
     */
    public IdentityCardVo ocrIdentityCard(String ImageBase64,String direction) {
        IdentityCardVo identityCardVo = new IdentityCardVo();
        String card = ocrUtils.ocrIdCard(ImageBase64, direction);
        JSONObject jsonObject = JSONUtil.parseObj(card);
        Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            if (StrUtil.equals(next.getKey(), "name")) {
                identityCardVo.setLegalRepresent(next.getValue().toString());
                continue;
            }
            if (StrUtil.equals(next.getKey(), "num")) {
                identityCardVo.setIdentityCardNumber(next.getValue().toString());
                continue;
            }
            if (StrUtil.equals(next.getKey(), "success")) {
                if (!(Boolean) next.getValue()) {
                    throw new BaseException(Status.ERROR, "图片识别失败");
                }
                continue;
            }
        }
        identityCardVo.setCardType(CardType.ID_CARD.getCardType());
        return identityCardVo;
    }

}
