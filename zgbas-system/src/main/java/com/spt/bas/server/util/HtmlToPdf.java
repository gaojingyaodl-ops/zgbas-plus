package com.spt.bas.server.util;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * HTML标签转PDF文件工具类
 *
 * @Author MoonLight
 * @Date 2023/3/28 16:51
 * @Version 1.0
 */
@Slf4j
public class HtmlToPdf {

    /**
     * 通过html的字符串转pdf
     *
     * @param out
     * @param html
     * @throws IOException
     * @throws DocumentException
     */
    public static void createPdfByHtml(OutputStream out, String html) throws IOException, DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        // 解决中文支持问题
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFont("simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.layout();
        renderer.createPDF(out);
    }

    /**
     * 通过html的字符串转pdf流
     *
     * @param html
     */
    public static ByteArrayOutputStream createPdfByHtml(String html) throws IOException, DocumentException {
        // 1. 创建一个ITextRenderer对象
        ITextRenderer renderer = new ITextRenderer();

        // 2. 将HTML内容转换为InputStream
        InputStream inputStream = IOUtils.toInputStream(html, StandardCharsets.UTF_8);

        // 3. 解决中文支持问题
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFont("simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

        // 3. 使用ITextRenderer对象将InputStream渲染为PDF，并将其写入ByteArrayOutputStream中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        // 4. 关闭InputStream
        inputStream.close();

        // 5. 返回ByteArrayOutputStream
        return outputStream;
    }

    /**
     * 通过html的字符串转pdf流
     *
     * @param html
     */
    public static ByteArrayOutputStream createPdfByHtml(String html, Object targetParam) {
        try {
            // 1. 创建一个ITextRenderer对象
            ITextRenderer renderer = new ITextRenderer();

            // 2. 将HTML内容转换为InputStream
            html = contentMerge(html, targetParam);
            InputStream inputStream = IOUtils.toInputStream(html, StandardCharsets.UTF_8);

            // 3. 解决中文支持问题
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont("simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

            // 3. 使用ITextRenderer对象将InputStream渲染为PDF，并将其写入ByteArrayOutputStream中
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);

            // 4. 关闭InputStream
            inputStream.close();

            // 5. 返回ByteArrayOutputStream
            return outputStream;
        } catch (Exception e) {
            log.error("createPdfByHtml error ",e);
        }
        return null;
    }

    /**
     * 通过html的文件路径转pdf
     *
     * @param out
     * @param htmlFilePath
     * @throws IOException
     * @throws DocumentException
     */
    public static void createPdfByUrl(OutputStream out, String htmlFilePath) throws IOException, DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        String url = new File(htmlFilePath).toURI().toURL().toString();
        renderer.setDocument(url);
        // 解决中文支持问题
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFont("simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        //解决图片的相对路径问题
        renderer.layout();
        renderer.createPDF(out);
    }

    public static String contentMerge(String content, Object targetParam) {
        Configuration cfg = new Configuration();
        StringWriter sw = new StringWriter();
        try {
            Template t = new freemarker.template.Template("", new StringReader(content), cfg);
            t.process(targetParam, sw);
            content = sw.toString();
        } catch (Exception e) {
            log.error("合并模板异常", e);
        }
        return content;
    }
}


