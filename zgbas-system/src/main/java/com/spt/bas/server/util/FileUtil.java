package com.spt.bas.server.util;

import com.google.common.base.Splitter;
import com.spt.bas.client.constant.BasConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/4/8 17:50
 * @version: 1.0
 * @description:
 */
public class FileUtil {
    public static boolean downloadFile(String httpUrl, String saveFile) {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return false;
        }
        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(saveFile);
            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {

            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static InputStream downloadFileStream(String httpUrl, String saveFile) {
        int bytesum = 0;
        int byteread = 0;
        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return null;
        }
        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(saveFile);
            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();
            return inStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean existFileFlg(String url) {
        boolean existFlg = false;
        URL urlfile;
        InputStream inStream = null;
        try {
            //下载
            urlfile = new URL(url);
            inStream = urlfile.openStream();
            existFlg = inStream.available() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inStream) {
                    inStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return existFlg;
    }

    public static File getFile(String fileId, String fileServerUrl) {
        String fileUrl = fileServerUrl + "/view/download/" + fileId;
        //对本地文件命名
        String fileName = "test.pdf";
        File file = null;
        URL urlfile;
        InputStream inStream = null;
        OutputStream os = null;
        try {
            file = File.createTempFile("File", fileName);
            //下载
            urlfile = new URL(fileUrl);
            inStream = urlfile.openStream();
            os = new FileOutputStream(file);

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
                if (null != inStream) {
                    inStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static boolean verifyPDF(String fileId, String fileServerUrl) {
        try {
            if (StringUtils.isBlank(fileId)){
                return false;
            }
            List<String> fileList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(fileId);
            if (CollectionUtils.isEmpty(fileList)){
                return false;
            }
            fileId = fileList.get(0);
            // 创建 URL 对象
            URL url = new URL(fileServerUrl + "/view/download/" + fileId);
            // 打开 URL 连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // 只请求头部信息
            connection.connect();
            // 获取内容类型
            String contentType = connection.getContentType();
            // 关闭连接
            connection.disconnect();
            // 检查内容类型是否为 PDF
            return contentType != null && contentType.equals("application/pdf");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
