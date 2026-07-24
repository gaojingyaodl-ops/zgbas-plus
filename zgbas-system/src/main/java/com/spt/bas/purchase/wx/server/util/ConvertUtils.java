package com.spt.bas.purchase.wx.server.util;

import cn.hutool.core.img.ImgUtil;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import gui.ava.html.image.generator.HtmlImageGenerator;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;

/**
 * <p>
 *  转换工具类
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-13 09:52
 */
@Slf4j
public class ConvertUtils {
    /**
     * 将html转为图片 并转成base64
     * @param content
     * @param imgType
     * @return
     */
    public static String html2Img(String content, String imgType) {
        try {
            HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
            imageGenerator.loadHtml(content);
            BufferedImage bufferedImage = imageGenerator.getBufferedImage();
            String png = ImgUtil.toBase64(bufferedImage, "png");
            return png;
        }catch (Exception e){
            log.error("图片转化失败", e);
            throw new BaseException(Status.IMAGE_CONVERT_FAIL);
        }
    }

    /**
     * 将关系代码转为文字
     * @param relationShipCode
     * @return
     */
    public static String convertRelationship(String relationShipCode) {
        switch (relationShipCode) {
            case "0":
                return "员工";
            case "1":
                return "法人";
            default:
                return "其他";
        }
    }

    /**
     * 转义性别
     * @param genderCode
     * @return
     */
    public static String convertGender(String genderCode) {
        switch (genderCode) {
            case "0":
                return "男";
            case "1":
                return "女";
            default:
                return "其他";
        }
    }
}
