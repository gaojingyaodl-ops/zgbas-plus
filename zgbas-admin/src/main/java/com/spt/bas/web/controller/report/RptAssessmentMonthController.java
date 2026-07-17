package com.spt.bas.web.controller.report;

import com.spt.bas.report.client.remote.IRptAssessmentClient;
import com.spt.bas.report.client.vo.RptAssessmentResultVo;
import com.spt.bas.report.client.vo.RptAssessmentSearch;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务员考核表 controller
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/4/25 15:22
 */

@Controller
@RequestMapping(value = "/rpt/assessmentMonth")
public class RptAssessmentMonthController extends PageController<RptAssessmentResultVo, BaseVo> {

    @Autowired
    private IRptAssessmentClient assessmentClient;
    @Resource
    private WebParamUtils webParamUtils;


    /**
     * 月度考核表页面
     * @param model 视图模型
     * @return 页面地址
     */
    @RequestMapping(value = "")
    public String index(Model model) {
        initDeptAndMatchUser(model);
        return "report/assessmentMonthPage";
    }

    /**
     * 季度考核表页面
     * @param model 视图模型
     * @return 页面地址
     */
    @RequestMapping(value = "/quarterPage")
    public String quarterPage(Model model) {
        initDeptAndMatchUser(model);
        getNextFiveYear(model);
        return "report/assessmentQuarterPage";
    }

    /**
     * 年度考核表页面
     * @param model 视图模型
     * @return 页面地址
     */
    @RequestMapping(value = "/yearPage")
    public String yearPage(Model model) {
        initDeptAndMatchUser(model);
        getNextFiveYear(model);
        return "report/assessmentYearPage";
    }

    /**
     * 查询业务员月度考核信息
     * @param searchVo 查询条件
     * @param response 返回结果
     */
    @RequestMapping(value = "/selectAssessmentMonth")
    public void selectAssessmentMonth(RptAssessmentSearch searchVo, HttpServletResponse response){
        formatYearAndMonth(searchVo);
        PageDown<RptAssessmentResultVo> page = assessmentClient.selectAssessmentMonth(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    /**
     * 查询业务员季度考核信息
     * @param searchVo 查询条件
     * @param response 返回结果
     */
    @RequestMapping(value = "/assessmentQuarter")
    public void selectAssessmentQuarter(RptAssessmentSearch searchVo, HttpServletResponse response){
        // 如果没有季度，则默认为当前月份所在的季度
        initQuarter(searchVo);
        // 如果没有年，默认选当前年
        initYear(searchVo);
        PageDown<RptAssessmentResultVo> page = assessmentClient.selectAssessmentQuarter(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    /**
     * 如果没有季度，则默认为当前月份所在的季度
     * @param searchVo 查询参数
     */
    private void initQuarter(RptAssessmentSearch searchVo) {
        if(searchVo.getQuarter() == null){
            Integer quarter = getQuarter();
            searchVo.setQuarter(quarter);
        }
    }

    /**
     * 获取当前季度
     * @return 返回当前季度
     */
    private Integer getQuarter() {
        int nowMonth = LocalDate.now().getMonthValue();
        int quarter;
        if (nowMonth <= 3) {
            quarter = 1;
        } else if (nowMonth <= 6) {
            quarter = 2;
        } else if (nowMonth <= 9) {
            quarter = 3;
        } else {
            quarter = 4;
        }
        return quarter;
    }

    /**
     * 查询业务员年度考核信息
     * @param searchVo 查询条件
     * @param response 返回结果
     */
    @RequestMapping(value = "/assessmentYear")
    public void selectAssessmentYear(RptAssessmentSearch searchVo, HttpServletResponse response){
        // 如果没有年，默认选当前年
        initYear(searchVo);
        PageDown<RptAssessmentResultVo> page = assessmentClient.selectAssessmentYear(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    /**
     * 如果没有年，默认选当前年
     * @param searchVo 查询参数
     */
    private void initYear(RptAssessmentSearch searchVo) {
        if(searchVo.getYear() ==null){
            searchVo.setYear(LocalDate.now().getYear());
        }
    }


    /**
     * 格式化月份
     * @param searchVo 查询参数
     */
    private void formatYearAndMonth(RptAssessmentSearch searchVo) {
        if(StringUtils.isBlank(searchVo.getYearAndMonth())){
            // 默认查询上个月
            LocalDate lastDate = LocalDate.now().plusMonths(-1);
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM");
            searchVo.setYearAndMonth(lastDate.format(pattern));
        }else{
            // 主要是格式化时间
            String[] split = searchVo.getYearAndMonth().split("-");
            if(Integer.parseInt(split[1]) < 10){
                searchVo.setYearAndMonth(split[0] + "-0" + split[1]);
            }
        }
    }

    /**
     * 业务员月度考核表导出为 Excel
     * @param searchVo 查询参数
     * @param request 请求
     * @param response 响应
     * @throws ApplicationException 异常
     */
    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(RptAssessmentSearch searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException {
        initSearch(searchVo, request);
        formatYearAndMonth(searchVo);
        int batchSize = 500;
        searchVo.setRows(batchSize);

        PageDown<RptAssessmentResultVo> page = assessmentClient.selectAssessmentMonth(searchVo);
        String title = "业务员月度考核表";

        String[] titles = new String[] { "姓名", "事业部", "入职时间","月份", "赊销额(元)", "赊销利润(元)", "代采额(元)", "代采利润(元)", "利润合计(元)"};
        String[] attrs = new String[] {"userName", "deptName", "entryDate", "createdDate",
                "sellMoney", "sellMoneyProfit", "buyMoney", "buyMoneyProfit", "sumProfitMoney"};
        int[] widths = new int[] { 15, 25, 15, 15, 15, 15, 15, 15,15};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        // 创建表头
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
        int start = 0;
        while (page != null && page.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle,
                    DateOperator.FORMAT_STR);
            if (page.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = assessmentClient.selectAssessmentMonth(searchVo);
                start += batchSize;
            } else {
                page = null;
            }
        }
        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 业务员年度考核表导出为 Excel
     * @param searchVo 查询参数
     * @param request 请求
     * @param response 响应
     * @throws ApplicationException 异常
     */
    @RequestMapping(value = "/exportExcelYear")
    @ResponseBody
    public void exportExcelYear(RptAssessmentSearch searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException {
        initSearch(searchVo, request);
        initYear(searchVo);
        int batchSize = 500;
        searchVo.setRows(batchSize);

        PageDown<RptAssessmentResultVo> page = assessmentClient.selectAssessmentYear(searchVo);
        String title = "业务员年度考核表";

        String[] titles = new String[] { "姓名", "事业部", "入职时间","年度", "赊销额(元)","月平均赊销额(元)", "赊销利润(元)",
                "月平均赊销利润(元)","代采额(元)","月平均代采额(元)", "代采利润(元)","月平均代采利润(元)", "利润合计(元)","月平均利润合计(元)"};
        String[] attrs = new String[] {"userName", "deptName", "entryDate", "year","sellMoney","sellMoneyAverage", "sellMoneyProfit",
                "sellMoneyProfitAverage", "buyMoney","buyMoneyAverage", "buyMoneyProfit", "buyMoneyProfitAverage","sumProfitMoney","sumProfitMoneyAverage"};
        int[] widths = new int[] { 15, 25, 15, 15, 15, 15, 15, 20,15,15,15,20,15,20};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        // 创建表头
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
        int start = 0;
        while (page != null && page.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle,
                    DateOperator.FORMAT_STR);
            if (page.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = assessmentClient.selectAssessmentYear(searchVo);
                start += batchSize;
            } else {
                page = null;
            }
        }
        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 业务员季度考核表导出为 Excel
     * @param searchVo 查询参数
     * @param request 请求
     * @param response 响应
     * @throws ApplicationException 异常
     */
    @RequestMapping(value = "/exportExcelQuarter")
    @ResponseBody
    public void exportExcelQuarter(RptAssessmentSearch searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException {
        initSearch(searchVo, request);
        initQuarter(searchVo);
        // 如果没有年，默认选当前年
        initYear(searchVo);
        int batchSize = 500;
        searchVo.setRows(batchSize);

        PageDown<RptAssessmentResultVo> page = assessmentClient.selectAssessmentQuarter(searchVo);
        String title = "业务员季度考核表";

        String[] titles = new String[] { "姓名", "事业部", "入职时间","季度", "赊销额(元)","月平均赊销额(元)", "赊销利润(元)",
                "月平均赊销利润(元)","代采额(元)","月平均代采额(元)", "代采利润(元)","月平均代采利润(元)", "利润合计(元)","月平均利润合计(元)"};
        String[] attrs = new String[] {"userName", "deptName", "entryDate", "quarter","sellMoney","sellMoneyAverage", "sellMoneyProfit",
                "sellMoneyProfitAverage", "buyMoney","buyMoneyAverage", "buyMoneyProfit", "buyMoneyProfitAverage","sumProfitMoney","sumProfitMoneyAverage"};
        int[] widths = new int[] { 15, 25, 15, 15, 15, 15, 15, 20,15,15,15,20,15,20};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        // 创建表头
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
        int start = 0;
        while (page != null && page.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle,
                    DateOperator.FORMAT_STR);
            if (page.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = assessmentClient.selectAssessmentQuarter(searchVo);
                start += batchSize;
            } else {
                page = null;
            }
        }
        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public BaseClient<RptAssessmentResultVo> getService() {
        return assessmentClient;
    }

    /**
     * 初始化部门信息和业务员树
     * @param model 视图模型
     */
    private void initDeptAndMatchUser(Model model) {
        EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true, true);
        // 获取业务员树
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
    }

    /**
     * 获取近五年的年份
     * @param model 视图模型
     */
    private void getNextFiveYear(Model model){
        LocalDate now = LocalDate.now();
        List<Map<String, String>> res = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            Map<String, String> yearMap = new LinkedHashMap<>();
            String year = String.valueOf(now.plusYears(i * (-1)).getYear());
            yearMap.put("year",year);
            yearMap.put("yearName",year + "年");
            res.add(yearMap);
        }
        model.addAttribute("nextYear",JsonUtil.obj2Json(res));
    }


}
