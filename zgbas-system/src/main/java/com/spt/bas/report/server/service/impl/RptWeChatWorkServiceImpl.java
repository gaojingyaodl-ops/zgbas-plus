package com.spt.bas.report.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.auth.sdk.vo.HolidayVo;
import com.spt.auth.sdk.vo.UserSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.vo.WeChatWorkVo;
import com.spt.bas.report.client.entity.RptLeaderboard;
import com.spt.bas.report.client.utils.DateUtils;
import com.spt.bas.report.client.vo.RptLeaderboardSearchVo;
import com.spt.bas.report.server.dao.RptWeChatWorkMapper;
import com.spt.bas.report.server.service.IRptWeChatWorkService;
import com.spt.pm.util.SubjectPmUtil;
import com.spt.tools.http.util.HTTPUtility;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class RptWeChatWorkServiceImpl implements IRptWeChatWorkService {

    private static final Logger log = LoggerFactory.getLogger(RptWeChatWorkServiceImpl.class);
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private RptWeChatWorkMapper weChatWorkMapper;

    @Value("${weChatWork.webhook.url}")
    private String weChatWorkWebhookUrl;

    @Value("${weChatWork.webhook.url2}")
    private String weChatWorkWebhookUrl2;

    @Override
    public void pushWeChatWorkLeaderboard(WeChatWorkVo vo) {
        try {
            Boolean wordDay = authOpenFacade.isWordDay(new HolidayVo(new Date()));
            if (!wordDay) {
                return;
            }
            RptLeaderboardSearchVo searchVo = new RptLeaderboardSearchVo();
            Date startDate = DateUtils.getBeginDayOfMonth();
            Date endDate = DateUtils.getEndDayOfMonth();
            searchVo.setStartDate(startDate);
            searchVo.setEndDate(endDate);

            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            // 定义自定义格式化器
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月份");
            // 格式化日期为指定格式的字符串
            String formattedDate = currentDate.format(formatter);

            searchVo.setDeptIdList(vo.getDeptIdList());
            List<RptLeaderboard> leaderBoardList = weChatWorkMapper.findLeaderBoardMatchUserGroupList(searchVo);
            List<RptLeaderboard> deptLeaderBoardList = weChatWorkMapper.findLeaderBoardDeptGroupList(searchVo);
            RptLeaderboard monthLeaderboard = weChatWorkMapper.findLeaderBoardTotalGrossProfitAmount(searchVo);
            BigDecimal monthTotalGrossProfitAmount = BigDecimal.ZERO;
            if (Objects.nonNull(monthLeaderboard)) {
                monthTotalGrossProfitAmount = monthLeaderboard.getGrossProfitAmount();
            }

            searchVo.setStartDate(new Date());
            searchVo.setEndDate(new Date());
            RptLeaderboard dayLeaderboard = weChatWorkMapper.findLeaderBoardTotalGrossProfitAmount(searchVo);
            BigDecimal dayTotalGrossProfitAmount = BigDecimal.ZERO;
            if (Objects.nonNull(dayLeaderboard)) {
                dayTotalGrossProfitAmount = dayLeaderboard.getGrossProfitAmount();
            }

            UserSearchVo userSearchVo = new UserSearchVo();
            userSearchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            userSearchVo.setContainInvalid(true);
            List<SysUserSdk> userAll = authOpenFacade.findUserAll(userSearchVo);
            Map<Long, SysUserSdk> userMap = userAll.stream()
                    .collect(Collectors.toMap(SysUserSdk::getUserId, user -> user, (existing, replacement) -> existing));
            DeptSearchVo deptSearchVo = new DeptSearchVo();
            deptSearchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(deptSearchVo);
            Map<Long, SysDeptSdk> deptMap = deptAll.stream()
                    .collect(Collectors.toMap(SysDeptSdk::getDeptId, user -> user, (existing, replacement) -> existing));

            if (CollectionUtils.isNotEmpty(leaderBoardList) && CollectionUtils.isNotEmpty(deptLeaderBoardList)) {
                // 获取推送的markdown消息

                String markdownMessage = buildMarkdownMessage(formattedDate, leaderBoardList, deptLeaderBoardList, userMap, deptMap, monthTotalGrossProfitAmount, dayTotalGrossProfitAmount);
                log.info("消息",monthLeaderboard);
                String s = HTTPUtility.doPost(weChatWorkWebhookUrl, markdownMessage);
                log.info("推送企业微信机器人结果",s);
            }
        } catch (Exception e) {
            log.error("机器人推送消息失败：{}",e);
        }
    }

    /**
     * 推送客户开发部业绩排行榜到企业微信
     *
     * @param vo
     */
    @Override
    public void pushWeChantWorkLeaderboardForCustomerDevelop(WeChatWorkVo vo) {
        try {
            Boolean wordDay = authOpenFacade.isWordDay(new HolidayVo(new Date()));
            if (!wordDay) {
                return;
            }
            BigDecimal dayTotalGrossProfitAmount = BigDecimal.ZERO;
            BigDecimal monthTotalGrossProfitAmount = BigDecimal.ZERO;
            RptLeaderboardSearchVo searchVo = new RptLeaderboardSearchVo();
            Date startDate = DateUtils.getBeginDayOfMonth();
            Date endDate = DateUtils.getEndDayOfMonth();
            searchVo.setStartDate(startDate);
            searchVo.setEndDate(endDate);

            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            // 定义自定义格式化器
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月份");
            // 格式化日期为指定格式的字符串
            String formattedDate = currentDate.format(formatter);

            searchVo.setDeptIdList(vo.getDeptIdList());
            List<RptLeaderboard> leaderBoardList = weChatWorkMapper.findLeaderBoardCustomerDevelopMatchList(searchVo);
            RptLeaderboard monthLeaderboard = weChatWorkMapper.findLeaderBoardCustomerDevelopTotalGrossProfitAmount(searchVo);
            if (Objects.nonNull(monthLeaderboard)) {
                monthTotalGrossProfitAmount = monthLeaderboard.getGrossProfitAmount();
            }
            searchVo.setStartDate(new Date());
            searchVo.setEndDate(new Date());
            RptLeaderboard dayLeaderboard = weChatWorkMapper.findLeaderBoardCustomerDevelopTotalGrossProfitAmount(searchVo);
            if (Objects.nonNull(dayLeaderboard)) {
                dayTotalGrossProfitAmount = dayLeaderboard.getGrossProfitAmount();
            }

            UserSearchVo userSearchVo = new UserSearchVo();
            userSearchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            userSearchVo.setContainInvalid(true);
            List<SysUserSdk> userAll = authOpenFacade.findUserAll(userSearchVo);
            Map<Long, SysUserSdk> userMap = userAll.stream()
                    .collect(Collectors.toMap(SysUserSdk::getUserId, user -> user, (existing, replacement) -> existing));
            if (CollectionUtils.isNotEmpty(leaderBoardList)) {
                // 获取推送的markdown消息
                String markdownMessage = buildCustomerDevelopMarkdownMessage(formattedDate, leaderBoardList, userMap, dayTotalGrossProfitAmount, monthTotalGrossProfitAmount);
                log.info("消息{}", markdownMessage);
                String s = HTTPUtility.doPost(weChatWorkWebhookUrl2, markdownMessage);
                log.info("推送企业微信机器人信息：{}", s);
            }
        } catch (Exception e) {
            log.error("机器人推送消息失败：{}", e);
        }
    }

    /**
     * 构建markdown消息
     * 改性塑料事业部 群播报
     * 格式：
     * 姓名   吨位   销售额
     * 张三    3吨    3000000
     * 李四    5吨    5000000
     *
     * @param formattedDate   日期
     * @param leaderBoardList 业绩数据
     * @param userMap         业务员数据
     * @return
     */
    public String buildCustomerDevelopMarkdownMessage(
            String formattedDate,
            List<RptLeaderboard> leaderBoardList,
            Map<Long, SysUserSdk> userMap,
            BigDecimal dayTotalGrossProfitAmount,
            BigDecimal monthTotalGrossProfitAmount) {

        StringBuilder content = new StringBuilder();
        content.append("## ").append(formattedDate).append("改性塑料事业部业绩排行\n\n");
        for (int i = 0; i < leaderBoardList.size(); i++) {
            RptLeaderboard lb = leaderBoardList.get(i);
            SysUserSdk user = userMap.get(lb.getMatchUserId());
            String userName = user != null ? user.getNickName() : lb.getMatchUserName();
            if (StringUtils.isNotBlank(userName)) {
                if (userName.length() == 2) {
                    userName = userName.charAt(0) + "　" + userName.charAt(1);
                }
                content.append(i + 1)
                        .append("、")
                        .append(userName)
                        .append("：")
                        .append(SubjectPmUtil.formatMoney2(lb.getTotalNum(), "T"))
                        .append("  ")
                        .append("￥" + SubjectPmUtil.formatMoney2(lb.getTotalAmount(),""))
                        .append("  ")
                        .append("￥" + SubjectPmUtil.formatMoney2(lb.getGrossProfitAmount(), ""))
                        .append(" \n");
            }
        }
        // 总毛利合计
        content.append("  \n")
                .append("<font color=\"warning\">**当日总毛利：")
                .append("￥" + SubjectPmUtil.formatMoney(dayTotalGrossProfitAmount, ""))
                .append("**</font>  \n")
                .append("<font color=\"warning\">**当月总毛利：")
                .append("￥" + SubjectPmUtil.formatMoney(monthTotalGrossProfitAmount, ""))
                .append("**</font>  \n");

        // 使用 JSON 库生成最终消息体
        JSONObject markdown = new JSONObject();
        markdown.put("content", content.toString());
        JSONObject msg = new JSONObject();
        msg.put("msgtype", "markdown");
        msg.put("markdown", markdown);
        return msg.toJSONString();
    }


    /**
     * ### 10月份业绩排行
     * 1、张军印：64,008.5
     * 2、张军印：4,008.5
     * 3、张军印：508.5
     * 4、张__军：64,008.5
     * 5、张军印：64,008.5
     * 6、张军印：64,008.5
     *
     * ### 区域毛利润
     * 1、华北区域：280,647.69
     * 2、华北区域：280,647.69
     * 3、华北区域：280,647.69
     * 4、华南区域：280,647.69
     *
     *
     * <font color="warning">**当日总毛利：100,000,11**</font>
     * <font color="warning">**当月总毛利：100,000,11**</font>
     * @param formattedDate
     * @param leaderBoardList
     * @param deptLeaderBoardList
     * @param userMap
     * @param deptMap
     * @return
     */
    public String buildMarkdownMessage(String formattedDate, List<RptLeaderboard> leaderBoardList, List<RptLeaderboard> deptLeaderBoardList,
                                       Map<Long, SysUserSdk> userMap, Map<Long, SysDeptSdk> deptMap, BigDecimal monthTotalGrossProfitAmount, BigDecimal dayTotalGrossProfitAmount) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"msgtype\": \"markdown\",\"markdown\": {");

        StringBuilder contentSb = new StringBuilder();
        contentSb.append("## "+ formattedDate +"业绩排行\\n");


        for (int i = 0; i < leaderBoardList.size(); i++) {
            RptLeaderboard leaderboard = leaderBoardList.get(i);
            SysUserSdk sysUserSdk = userMap.get(leaderboard.getMatchUserId());
            String userName = leaderboard.getMatchUserName();
            if (Objects.nonNull(sysUserSdk)) {
                userName =sysUserSdk.getNickName();
            }
            if (StringUtils.isNotBlank(userName)) {
                if (userName.length() == 2) {
                    userName = userName.substring(0,1) + "　" + userName.substring(1,2);
                }
            }
            contentSb.append(i+1 + "、" + userName + "：" + SubjectPmUtil.formatMoney(leaderboard.getGrossProfitAmount(),"") + "\\n");
        }
        contentSb.append("\\n");

        contentSb.append("## 区域毛利润\\n\\n");
        for (int i = 0; i < deptLeaderBoardList.size(); i++) {
            RptLeaderboard leaderboard = deptLeaderBoardList.get(i);
            SysDeptSdk sysDeptSdk = deptMap.get(leaderboard.getDeptId());
            String deptName = "";
            if (Objects.nonNull(sysDeptSdk)) {
                deptName = sysDeptSdk.getDeptName();
            }
            if (leaderboard.getGrossProfitAmount().compareTo(BigDecimal.ZERO) > 0){
                contentSb.append(i+1 + "、" + deptName + "：" + SubjectPmUtil.formatMoney(leaderboard.getGrossProfitAmount(),"") + "\\n");
            }
        }

        contentSb.append("\\n");

        contentSb.append("<font color=\\\"warning\\\">**当日总毛利：").append(SubjectPmUtil.formatMoney(dayTotalGrossProfitAmount,"")).append("**</font>\\n");
        contentSb.append("<font color=\\\"warning\\\">**当月总毛利：").append(SubjectPmUtil.formatMoney(monthTotalGrossProfitAmount,"")).append("**</font>\\n");


        sb.append("\"content\":\""+contentSb+"\"");
        sb.append("}}");

        return sb.toString();
    }

//    public String buildJpgMessage(String formattedDate, List<RptLeaderboard> leaderBoardList, List<RptLeaderboard> deptLeaderBoardList,
//                                       Map<Long, SysUserSdk> userMap, Map<Long, SysDeptSdk> deptMap) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("{\"msgtype\": \"image\",\"image\": {");
//
//        // html转图片
//        String contentHtml = getContentHtml(formattedDate, leaderBoardList, deptLeaderBoardList, userMap, deptMap);
//        String base64 = htmlToImage(contentHtml, "png");
//        String md5 = getMd5(base64);
//
//        sb.append("\"base64\":\""+base64+"\",");
//        sb.append("\"md5\":\""+md5+"\"");
//        sb.append("}}");
//
//        return sb.toString();
//    }
    public String getContentHtml(String formattedDate, List<RptLeaderboard> leaderBoardList, List<RptLeaderboard> deptLeaderBoardList,
                                 Map<Long, SysUserSdk> userMap, Map<Long, SysDeptSdk> deptMap){
        StringBuilder contentSb = new StringBuilder();


        contentSb.append("<!DOCTYPE html><html><head><meta charset=\"utf-8\"></meta><title>业绩排行榜</title>\n" +
                "    <style>\n" +
                "        .s-th {\n" +
                "            color: #9F7D5F;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        .s-td {\n" +
                "            color: #BF5C3F;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        .dept-div {\n" +
                "            background-color: #DC574B;\n" +
                "            margin-right: 50px;\n" +
                "            margin-left: 50px;\n" +
                "            margin-bottom: 10px;\n" +
                "            padding-top: 5px;\n" +
                "            padding-bottom: 5px;\n" +
                "            color: #E6E46E;\n" +
                "            font-weight: bold;\n" +
                "            border-radius: 10px;\n" +
                "        }\n" +
                "        .content-div {\n" +
                "            width:300px;height:auto;background-color:#FCF5DF;text-align: center;\n" +
                "        }\n" +
                "        .content-title-1 {\n" +
                "            color:blob;font-size:20px;font-weight: bold;\n" +
                "        }\n" +
                "        .content-date {\n" +
                "            color:#BF5C3F;\n" +
                "        }\n" +
                "        .content-title-2 {\n" +
                "            color:blob;font-size:20px;font-weight: bold;margin-top:20px;margin-bottom:10px;\n" +
                "        }\n" +
                "        .outer {\n" +
                "            display: flex;\n" +
                "            width: 200px; \n" +
                "            background-color: #DC574B;\n" +
                "            margin-right: 50px;\n" +
                "            margin-left: 50px;\n" +
                "            margin-bottom: 10px;\n" +
                "            padding-top: 5px;\n" +
                "            padding-bottom: 5px;\n" +
                "            color: #E6E46E;\n" +
                "            font-weight: bold;\n" +
                "            border-radius: 10px;    \n" +
                "        }\n" +
                "\n" +
                "        .inner {\n" +
                "            flex: 1; \n" +
                "            text-align: center; \n" +
                "        }\n" +
                "\n" +
                "    </style>\n" +
                "</head><body>");
        contentSb.append("</head><body>");
        contentSb.append("<div class='content-div'>\n" +
                "    <div class='content-title-1'> 业绩排行榜</div>");
        contentSb.append("<div class=\"content-date\"> "+formattedDate+" </div>");
        contentSb.append("<table style='width:100%'>\n" +
                "        <thead>\n" +
                "            <tr>\n" +
                "                <th class='s-th'>排名</th>\n" +
                "                <th class='s-th'>姓名</th>\n" +
                "                <th class='s-th'>毛利润</th>\n" +
                "            </tr>\n" +
                "        </thead>\n" +
                "        <tbody>");
        for (int i = 0; i < leaderBoardList.size(); i++) {
            RptLeaderboard leaderboard = leaderBoardList.get(i);
            SysUserSdk sysUserSdk = userMap.get(leaderboard.getMatchUserId());
            contentSb.append("<tr>");
            contentSb.append("<td class='s-td'>");
            contentSb.append("NO."+ (i+1));
            contentSb.append("</td>");
            contentSb.append("<td class='s-td'>");
            if (Objects.nonNull(sysUserSdk)) {
                contentSb.append(sysUserSdk.getNickName());
            }
            contentSb.append("</td>");
            contentSb.append("<td class='s-td'>");
            contentSb.append(leaderboard.getGrossProfitAmount());
            contentSb.append("</td>");

            contentSb.append("</tr>");
        }
        contentSb.append("</tbody>\n" +
                "    </table>\n" +
                "    <div class='content-title-2'> 区域毛利润排名\n" +
                "    </div>");
        for (RptLeaderboard leaderboard : deptLeaderBoardList) {
            SysDeptSdk sysDeptSdk = deptMap.get(leaderboard.getDeptId());
            contentSb.append("<div class='dept-div'>");
            if (Objects.nonNull(sysDeptSdk)) {
                contentSb.append(sysDeptSdk.getDeptName() + "&nbsp;&nbsp;&nbsp;&nbsp;");
            }
            contentSb.append(leaderboard.getGrossProfitAmount());
            contentSb.append("</div>");
        }
        contentSb.append("<div style='height:20px;'></div>\n" +
                "</body>\n" +
                "</html>");

        return contentSb.toString();
    }

//    /**
//     * 将html转为图片 并转成base64
//     * @param content
//     * @param imgType
//     * @return
//     */
//    public static String html2Img(String content, String imgType) {
//        try {
//            HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
//            imageGenerator.loadHtml(content);
//            BufferedImage bufferedImage = imageGenerator.getBufferedImage();
//            String png = ImgUtil.toBase64(bufferedImage, "png");
//            // 保存图片到本地
////            saveImageToFile(bufferedImage, imgType, "/Users/lishuangjian/Downloads/output." + imgType);
//
//            return png;
//        }catch (Exception e){
//            log.error("图片转化失败", e);
//        }
//        return null;
//    }


    private static void saveImageToFile(BufferedImage image, String imgType, String filePath) throws Exception {
        File outputFile = new File(filePath);
        ImageIO.write(image, imgType, outputFile);
        log.info("图片已保存到: " + outputFile.getAbsolutePath());
    }

    public String getMd5(String base64Image){

        base64Image = "data:image/png;base64," + base64Image;
        // 仅提取Base64内容，去掉头部
        String base64Data = base64Image.split(",")[1];

        try {
            // 计算MD5值
            String md5 = calculateMD5(base64Data);
            System.out.println("MD5: " + md5);
            return md5;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 从Base64字符串计算MD5值
    private static String calculateMD5(String base64Data) throws NoSuchAlgorithmException {
        // 解码Base64字符串
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        // 计算MD5值
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = md.digest(imageBytes);

        // 转换为16进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
