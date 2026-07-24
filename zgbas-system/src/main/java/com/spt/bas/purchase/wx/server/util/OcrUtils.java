package com.spt.bas.purchase.wx.server.util;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.CardType;
import com.spt.bas.purchase.wx.server.common.Constant;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.config.OcrConfig;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-22 12:07
 */
@EnableConfigurationProperties(OcrConfig.class)
@Configuration
public class OcrUtils {
    private static Logger logger = LoggerFactory.getLogger(OcrUtils.class);

    @Autowired
    private OcrConfig config;

    /**
     * 根据类型返回url
     * @param type
     * @return url
     */
    private String getUrl(CardType type) {
        switch (type) {
            case BUSINESS_LICENSE:
                return config.getBusinessLicenseUrl();
            case ID_CARD:
                return config.getIdCardUrl();
            case VEHICLE:
                return config.getVehicleUrl();
            case DRIVER_LICENSE:
                return config.getDriverLicenseUrl();
            case HOUSEHOLD_REGISTER:
                return config.getHouseholdRegisterUrl();
            case BANK_CARD:
                return config.getBankCardUrl();
            case PASSPORT:
                return config.getPassportUrl();
            default:
                logger.error("没有找到需要图片识别的类型,type:{}", type);
                throw new BaseException(Status.ERROR, "没有找到需要图片识别的类型");
        }

    }

    /**
     * 识别身份证
     * @param base64
     * @param direction
     * @return
     */
    public String ocrIdCard(String base64,String direction) {
        String result = getResult(CardType.ID_CARD, base64, direction);
        return result;
    }

    /**
     * 识别身份证
     * @param base64
     * @return
     */
    public String ocrIdCard(String base64) {
        String result = ocrIdCard(base64, Constant.ID_CARD_SIDE_FACE);
        return result;
    }

    /**
     * 识别营业执照
     * @param base64
     * @return
     */
    public String ocrBusinessLicense(String base64) {
        return getResult(CardType.BUSINESS_LICENSE, base64, null);
    }


    private String getResult(CardType type, String imgBase64, String direction) {
        // 获取api请求地址
        String orcUrl = getUrl(type);
        // 根据线上文档修改configure字段
        JSONObject configObj = new JSONObject();
        switch (type) {
            case ID_CARD:
                if (Constant.ID_CARD_SIDE_BACK.equals(direction)) {
                    configObj.put("side", "back");
                } else{
                    configObj.put("side", "face");
                }
                break;
            default:
                break;
        }

        String config_str = configObj.toString();
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + config.getAppcode());
        Map<String, String> querys = new HashMap<String, String>();
        // 拼装请求body的json字符串
        JSONObject requestObj = new JSONObject();
        String[] split = imgBase64.split("base64,");
        if (split.length>1) {
            requestObj.put("image", split[1]);
        }else {
            requestObj.put("image", split[0]);
        }
        if (config_str.length() > 0) {
            requestObj.put("configure", config_str);
        }
        String bodys = requestObj.toString();
        String r = "";
        try {
            HttpResponse response = HttpUtils.doPost(config.getHost(), orcUrl, method, headers, querys, bodys);
            int stat = response.getStatusLine().getStatusCode();
            if (stat != 200) {
                System.out.println("Http code: " + stat);
                System.out.println("http header error msg: " + response.getFirstHeader("X-Ca-Error-Message"));
                System.out.println("Http body error msg:" + EntityUtils.toString(response.getEntity()));
                throw new BaseException(Status.ERROR, "图片无法识别");
            }
            String res = EntityUtils.toString(response.getEntity());
            JSONObject res_obj = JSON.parseObject(res);
            r = res_obj.toString();
            System.out.println(res_obj.toJSONString());
        } catch (Exception e) {
            logger.error("图片识别发生错误，", e);
            throw new BaseException(Status.ERROR, "图片无法识别");
        }
        return r;
    }

    /**
     * 身份证返回数据
     *
     * {
     * 	"address"    : "浙江省杭州市余杭区文一西路969号",   #地址信息
     * 	"config_str" : "{\\\"side\\\":\\\"face\\\"}",    #配置信息，同输入configure
     * 	"face_rect":{       #人脸位置
     * 		"angle": -90,   #angle表示矩形顺时针旋转的度数
     * 		"center":{      #center表示人脸矩形中心坐标
     * 			"x" : 952,
     * 			"y" : 325.5
     *                },
     * 		"size":{        #size表示人脸矩形长宽
     * 			"height":181.99,
     * 			"width":164.99
     *        }* 	},
     * 	"face_rect_vertices":[  #人脸位置，四个顶点表示
     *       {
     *          "x":1024.6600341796875,
     *          "y":336.629638671875
     *       },
     *       {
     *          "x":906.66107177734375,
     *          "y":336.14801025390625
     *       },
     *       {
     *          "x":907.1590576171875,
     *          "y":214.1490478515625
     *       },
     *       {
     *          "x":1025.157958984375,
     *          "y":214.63067626953125
     *       }
     *     ],
     * 	"name" : "张三",                 #姓名
     * 	"nationality": "汉"，            #民族
     * 	"num" : "1234567890",            #身份证号
     * 	"sex" : "男",                    #性别
     * 	"birth" : "20000101",            #出生日期
     * 	"nationality" : "汉",            #民族
     * 	"success" : true                 #识别结果，true表示成功，false表示失败
     * }
     *
     *
     * 反面返回结果:
     * {
     *     "config_str" : "{\\\"side\\\":\\\"back\\\"}",  #配置信息，同输入configure
     *     "start_date" : "19700101",       #有效期起始时间
     *     "end_date" : "19800101",         #有效期结束时间
     *     "issue" : "杭州市公安局",         #签发机关
     *     "success" : true                 #识别结果，true表示成功，false表示失败
     * }
     */

    /**
     * 营业执照返回数据
     * {
     *     "config_str" : "null\n", #配置字符串信息
     *     "angle" : float, #输入图片的角度（顺时针旋转），［0， 90， 180，270］
     *     "reg_num" : string, #注册号，没有识别出来时返回"FailInRecognition"
     *     "name" : string, #公司名称，没有识别出来时返回"FailInRecognition"
     *     "type" : string, #公司类型，没有识别出来时返回"FailInRecognition"
     *     "person" : string, #公司法人，没有识别出来时返回"FailInRecognition"
     *     "establish_date": string, #公司注册日期(例：证件上为"2014年04月16日"，算法返回"20140416")
     *     "valid_period": string, #公司营业期限终止日期(例：证件上为"2014年04月16日至2034年04月15日"，算法返回"20340415")
     *     #当前算法将日期格式统一为输出为"年月日"(如"20391130"),并将"长期"表示为"29991231"，若证件上没有营业期限，则默认其为"长期",返回"29991231"。
     *     "address" : string, #公司地址，没有识别出来时返回"FailInRecognition"
     *     "capital" : string, #注册资本，没有识别出来时返回"FailInRecognition"
     *     "business": string, #经营范围，没有识别出来时返回"FailInRecognition"
     *     "emblem" : string, #国徽位置［top,left,height,width］，没有识别出来时返回"FailInDetection"
     *     "title" : string, #标题位置［top,left,height,width］，没有识别出来时返回"FailInDetection"
     *     "stamp" : string, #印章位置［top,left,height,width］，没有识别出来时返回"FailInDetection"
     *     "qrcode" : string, #二维码位置［top,left,height,width］，没有识别出来时返回"FailInDetection"
     *     "is_fake": false, #是否是复印件
     *     "success" : bool, #识别成功与否 true/false
     *     "request_id": string
     * }
     */

}
