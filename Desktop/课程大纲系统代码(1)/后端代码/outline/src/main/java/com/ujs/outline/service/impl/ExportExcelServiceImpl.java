package com.ujs.outline.service.impl;

import com.ujs.outline.domain.College;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.ExportExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExportExcelServiceImpl implements ExportExcelService {
    private final JdbcTemplate jdbcTemplate;
    private final CollegeService collegeService;
    private Map<String, String> collegeMap;

    @Autowired
    public ExportExcelServiceImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.collegeService = new CollegeServiceImpl();
    }

    private void init() {
        collegeMap = collegeService.getCollegeNameAndId()
                .stream()
                .collect(Collectors.toMap(
                        College::getCollegeId,
                        College::getCollegeName
                ));

    }
    // 预处理学院id和名称

    @Override
    public void exportUserToExcel(HttpServletResponse response){
        init();
        // 设置响应头
        setupResponseHeaders(response);

        // 创建Excel工作簿
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // 查询数据并填充Excel
            jdbcTemplate.query("SELECT user_id, college_id, user_state FROM user", (ResultSetExtractor<Void>) rs -> {
                createHeaderRow(sheet, workbook);
                int rowNum = 1;

                while (rs.next()) {
                    populateDataRow(rs, sheet.createRow(rowNum++));
                }
                return null;
            });

            // 自动调整列宽
            autoSizeColumns(sheet);

            // 写入响应流
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new DataAccessException("文件生成失败", e) {};
        }
    }

    // 设置HTTP 响应头，浏览器进行下载
    private void setupResponseHeaders(HttpServletResponse response) {
        String fileName = "users_export_" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    }

    private void createHeaderRow(Sheet sheet, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"用户名", "学院", "身份"};

        CellStyle headerStyle = createHeaderCellStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    // 样式
    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void populateDataRow(ResultSet rs, Row row) throws SQLException {
        int col = 0;
        String userState =rs.wasNull()  ? "UNKNOWN" : rs.getString("user_state");
        // 处理身份
        switch (userState) {
            case "UNKNOWN" :
                userState = "未定义身份";
                break;
            case "20":
                userState = "系统管理员";
                break;
            case "21":
                userState = "学院管理员";
                break;
            case "22":
                userState = "教师";
                break;
            default:
                userState = "异常状态(" + userState + ")";
                break;
        }
        // 处理学院名称

        row.createCell(col++).setCellValue(rs.getString("user_id"));
        row.createCell(col++).setCellValue(collegeMap.getOrDefault(rs.getString("college_id"),
                "未知学院"));
        row.createCell(col++).setCellValue(userState);
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
