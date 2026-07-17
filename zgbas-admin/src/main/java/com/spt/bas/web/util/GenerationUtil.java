package com.spt.bas.web.util;

import com.spt.bas.client.vo.ApplyDeliveryOutVo;
import gui.ava.html.image.generator.HtmlImageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-12-04 10:15
 */
public class GenerationUtil {
    private static Logger logger = LoggerFactory.getLogger(GenerationUtil.class);

    /**
     * 合成图片
     *
     * @param response
     * @param html
     */
    public static void generateTranshipment(HttpServletResponse response, String html, String filename) {
        // html转图片
        try {
            HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
            imageGenerator.loadHtml(html);
            BufferedImage image = imageGenerator.getBufferedImage();
            //BufferedImage 转 InputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageOutputStream imageOutput = ImageIO.createImageOutputStream(byteArrayOutputStream);
            ImageIO.write(image, "png", imageOutput);
            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            long length = imageOutput.length();

            //设置response
            response.setContentType("application/x-msdownload");
            response.setContentLength((int) length);
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("gbk"), "iso-8859-1"));

            //输出流
            byte[] bytes = new byte[1024];
            OutputStream outputStream = response.getOutputStream();
            long count = 0;
            while (count < length) {
                int len = inputStream.read(bytes, 0, 1024);
                count += len;
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();

        } catch (Exception e) {
            logger.error("图片转化失败", e);
        }
    }

}
