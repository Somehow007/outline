package com.ujs.outline.common.handler;
import com.ujs.outline.domain.enums.CourseCategoryEnum;
import com.ujs.outline.domain.enums.CourseTypeEnum;
import com.ujs.outline.domain.enums.TestTypeEnum;

import com.ujs.outline.domain.College;
import com.ujs.outline.domain.Course;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Excel处理类
 * @author wjy
 */
public class ExcelHandler {

    /**
     * 读取待修订课程代码列表
     */
    public static List<String> getCodeToFixFromExcel(File file) {
        List<String> result=new ArrayList<>();
        Workbook workbook = null;
        try {
            workbook = load(file);
            result = readCodeRequireFix(workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static List<Course> getCoursesFromExcel(File file) {
        List<Course> result=new ArrayList<>();
        Workbook workbook = null;
        try {
            workbook = load(file);
            result = readCoursesForList(workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static List<College> getCollegesFromExcel(File file) {
        List<College> result=new ArrayList<>();
        Workbook workbook = null;
        try {
            workbook = load(file);
            result = readCollegesForList(workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static Map<String,String> getMajorsFromExcel(File file) {
        Map<String,String> result=new HashMap<>();
        Workbook workbook = null;
        try {
            workbook = load(file);
            result = readMajorsForList(workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * 读取文件并转换为工作簿
     *
     * @param file 待读取的Excel文件(要求xlsx、xls后缀)
     * @return 工作簿
     * @throws Exception 各种读取失败异常
     */
    private static Workbook load(File file) throws Exception {
        if (file == null)
            throw new Exception("文件不能为空");
        int index = file.getName().lastIndexOf(".");
        if (index == -1)
            throw new Exception("无法加载此文件");
        String suffix = file.getName().substring(index + 1);
        Workbook workbook;
        FileInputStream fis = new FileInputStream(file);
        if (suffix.equals("xlsx")) {
            workbook = new XSSFWorkbook(fis);
        } else if (suffix.equals("xls")) {
            workbook = new HSSFWorkbook(fis);
        } else {
            throw new Exception("无法加载此文件");
        }
        fis.close();
        return workbook;
    }

    /**
     * 临时读取学院列表
     * @deprecated
     * @param workbook
     * @return colleges
     */
    private static List<College> readCollegesForList(Workbook workbook){
        List<College> list=new ArrayList<>();
        final String sheetString="各学院修订大纲门数";//学院 工作簿名
        final String colString="开课学院名称";//学院 列名
        int indexOfSheet = 0;
        for (; indexOfSheet < workbook.getNumberOfSheets(); indexOfSheet++) {
            if (workbook.getSheetName(indexOfSheet).contains(sheetString)) break;
        }
        if(indexOfSheet< workbook.getNumberOfSheets()){
            //读取工作表
            Sheet sheet=workbook.getSheetAt(indexOfSheet);
            Row row0=sheet.getRow(0);
            int college_index=-1;
            for (int i = 0; i < row0.getLastCellNum(); i++) {
                Cell cell = row0.getCell(i);
                if (cell != null) {
                    String s=(String)getCellValue(cell);
                    if (s != null&&s.contains(colString)) {
                        college_index=i;break;
                    }
                }
            }
            if (college_index>=0){
                int id=0;
                for (int i = 1; i < sheet.getLastRowNum(); i++) {
                    Row row=sheet.getRow(i);
                    Cell cell = row.getCell(college_index);
                    if (cell != null) {
                        String s=(String)getCellValue(cell);
                        if (s != null&&!s.equals("")) {
                            College college=new College();
                            college.setCollegeName(s);
                            college.setCollegeId(id<10?"0"+id:String.valueOf(id));
                            id++;
                            list.add(college);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 临时读取专业列表
     * @deprecated
     * @param workbook
     * @return key-专业名 value-学院名
     */
    private static Map<String,String> readMajorsForList(Workbook workbook){
        Map<String,String> map=new HashMap<>();
        final String sheetString="专业表";//专业表 工作簿名
        final String colString="学院";//学院 列名
        final String colString1="专业";//专业 列名
        int indexOfSheet = 0;
        for (; indexOfSheet < workbook.getNumberOfSheets(); indexOfSheet++) {
            if (workbook.getSheetName(indexOfSheet).contains(sheetString)) break;
        }
        if(indexOfSheet< workbook.getNumberOfSheets()){
            //读取工作表
            Sheet sheet=workbook.getSheetAt(indexOfSheet);
            Row row0=sheet.getRow(0);
            int college_index=-1;
            int major_index=-1;
            for (int i = 0; i < row0.getLastCellNum(); i++) {
                Cell cell = row0.getCell(i);
                if (cell != null) {
                    String s=(String)getCellValue(cell);
                    if (s != null) {
                        if(s.contains(colString)) {
                            college_index = i;
                        } else if (s.contains(colString1)) {
                            major_index= i;
                        }
                    }
                }
            }
            if (college_index>=0&&major_index>=0){
                for (int i = 1; i < sheet.getLastRowNum(); i++) {
                    Row row=sheet.getRow(i);
                    Cell cell = row.getCell(college_index);
                    Cell cell1=row.getCell(major_index);
                    if (cell != null&&cell1!=null) {
                        String s=(String)getCellValue(cell);
                        String s1=(String)getCellValue(cell1);
                        if (s != null&&s1!=null) {
                            map.put(s1,s);
                        }
                    }
                }
            }
        }
        return map;
    }


    /**
     * @deprecated
     * 读取待修订课程代码列表
     */
    private static List<String> readCodeRequireFix(Workbook workbook){
        List<String> list = new ArrayList<>();
        final String sheetString="待修订课程汇总表";
        final String colName="课程代码";
        int indexOfSheet = 0;
        int indexOfCol=-1;
        for (; indexOfSheet < workbook.getNumberOfSheets(); indexOfSheet++) {
            if (workbook.getSheetName(indexOfSheet).contains(sheetString)) break;
        }
        if (indexOfSheet < workbook.getNumberOfSheets()) {
            //读取工作表
            Sheet sheet=workbook.getSheetAt(indexOfSheet);
            Row row0=sheet.getRow(0);
            //绑定列
            for (int i = 0; i < row0.getLastCellNum(); i++) {
                Cell cell = row0.getCell(i);
                if (cell != null) {
                    String s=(String)getCellValue(cell);
                    if (s != null&&s.equals(colName)) {
                        indexOfCol=i;
                    }
                }
            }
            if (indexOfCol>=0){
                for (int i = 1; i < sheet.getLastRowNum(); i++) {
                    Row row=sheet.getRow(i);
                    Cell cell = row.getCell(indexOfCol);
                    if (cell != null) {
                        String s=(String)getCellValue(cell);
                        if (s != null) {
                            list.add(s);
                        }
                    }
                }
            }
        }

        return list;
    }

    /**
     * 读取课程列表
     */
    private static List<Course> readCoursesForList(Workbook workbook){
        List<Course> list=new ArrayList<>();
        final String sheetString="培养计划课程设置表";//学院 工作簿名
        //各列 列名
        final String[] colNames = {"专业名称","年级","课程代码","课程名称","学分","总学时","周学时"
                                ,"讲课学时","实验学时","上机学时","其他学时","课程性质","考核方式","开课学院","建议修读学期","课程类别","课程大纲"};
        int[] colIndex=new int[17];
        Arrays.fill(colIndex,-1);
        int indexOfSheet = 0;
        for (; indexOfSheet < workbook.getNumberOfSheets(); indexOfSheet++) {
            if (workbook.getSheetName(indexOfSheet).contains(sheetString)) break;
        }
        if(indexOfSheet< workbook.getNumberOfSheets()){
            //读取工作表
            Sheet sheet=workbook.getSheetAt(indexOfSheet);
            Row row0=sheet.getRow(0);
            //绑定列
            for (int i = 0; i < row0.getLastCellNum(); i++) {
                Cell cell = row0.getCell(i);
                if (cell != null) {
                    String s=(String)getCellValue(cell);
                    if (s != null) {
                        for (int j = 0; j < colNames.length; j++) {
                            if (colNames[j].equals(s)) {
                                colIndex[j]=i;break;
                            }
                        }
                    }
                }
            }
            boolean flag=true;
            for (int index : colIndex) {
                if (index == -1) {
                    flag=false;break;
                }
            }
            if (flag){
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row=sheet.getRow(i);
                    Course course=new Course();
                    for (int j = 0; j <= 16; j++) {
                        Cell cell = row.getCell(colIndex[j]);
                        //"专业名称","年级","课程代码","课程名称","学分","总学时","周学时"
                        //,"讲课学时","实验学时","上机学时","其他学时","课程性质","考核方式","开课学院","建议修读学期","课程类别"，“课程大纲”
                        if (cell != null) {
                            Object obj = getCellValue(cell);
                            switch (j) {
                                case 0:course.setMajorId((String) obj);break;
                                case 1:
                                    String year=String.valueOf(obj);
                                    if(year!=null&&year.contains(".")){
                                        year=year.substring(0,year.indexOf('.'));
                                    }
                                    course.setYear(year);break;
                                case 2:course.setCode((String)obj);break;
                                case 3:course.setName((String)obj);break;
                                case 4:course.setCreditSum((Double) obj);break;
                                case 5:
                                    try {
                                        course.setHourWeek((Double) obj);
                                    } catch (ClassCastException e) {
                                        switch (obj.getClass().getSimpleName()) {
                                            case "String":
                                                String s=(String) obj;
                                                if (!s.equals("")) {
                                                    course.setHourWeek(Double.parseDouble(s));
                                                }
                                                break;
                                            default:e.printStackTrace();
                                        }
                                    }
                                    break;
                                case 6:break;
                                case 7:
                                    try {
                                        course.setHourTeach((Integer) obj);
                                    } catch (ClassCastException e) {
                                        switch (obj.getClass().getSimpleName()) {
                                            case "Double":
                                                Double d=(Double) obj;
                                                course.setHourTeach(d.intValue());
                                                break;
                                            case "String":
                                                String s=(String) obj;
                                                if (!s.equals("")) {
                                                    course.setHourTeach(Integer.valueOf(s));
                                                }
                                                break;
                                            default:e.printStackTrace();
                                        }
                                    }
                                    break;
                                case 8:
                                    try {
                                        course.setHourPractice((Integer) obj);
                                    } catch (ClassCastException e) {
                                        switch (obj.getClass().getSimpleName()) {
                                            case "Double":
                                                Double d=(Double) obj;
                                                course.setHourPractice(d.intValue());
                                                break;
                                            case "String":
                                                String s=(String) obj;
                                                if (!s.equals("")) {
                                                    course.setHourPractice(Integer.valueOf(s));
                                                }
                                                break;
                                            default:e.printStackTrace();
                                        }
                                    }
                                    break;
                                case 9:
                                    try {
                                        course.setHourOperation((Integer) obj);
                                    } catch (ClassCastException e) {
                                        switch (obj.getClass().getSimpleName()) {
                                            case "Double":
                                                Double d=(Double) obj;
                                                course.setHourOperation(d.intValue());
                                                break;
                                            case "String":
                                                String s=(String) obj;
                                                if (!s.equals("")) {
                                                    course.setHourOperation(Integer.valueOf(s));
                                                }
                                                break;
                                            default:e.printStackTrace();
                                        }
                                    }
                                    break;
                                case 10:
                                    try {
                                        course.setHourOutside((Integer) obj);
                                    } catch (ClassCastException e) {
                                        switch (obj.getClass().getSimpleName()) {
                                            case "Double":
                                                Double d=(Double) obj;
                                                course.setHourOutside(d.intValue());
                                                break;
                                            case "String":
                                                String s=(String) obj;
                                                if (!s.equals("")) {
                                                    course.setHourOutside(Integer.valueOf(s));
                                                }
                                                break;
                                            default:e.printStackTrace();
                                        }
                                    }
                                    break;
                                case 11:course.setCourseCategory(CourseCategoryEnum.getInstance((String)obj));break;
                                case 12:course.setTestType(TestTypeEnum.getInstance((String)obj));
                                case 13:course.setCollegeId((String)obj);break;
                                case 14:
                                    try {
                                        course.setTerm((Integer) obj);
                                    } catch (ClassCastException e) {
                                        course.setTerm(((Double)obj).intValue());
                                    }
                                    break;
                                case 15:course.setCourseType(CourseTypeEnum.getInstance((String)obj));break;
                                case 16:
                                    String state=String.valueOf(obj);
                                    if(state.equals("待修订")){
                                        course.setCourseState("01");
                                    }else {
                                        course.setCourseState("00");
                                    }
                            }
                        }
                    }
                    list.add(course);
                }
            }
        }
        return list;
    }

    public static ByteArrayOutputStream exportCountExcel(List<Course> courseList) throws Exception{
        String filepath = System.getProperty("user.dir") + File.separator + "tempFile";
        File file = new File(filepath + File.separator + "课程大纲修订情况统计.xlsx");
        Workbook workbook = load(file);
        Sheet sheetToFix = workbook.getSheetAt(0);
        Sheet sheetIsFixed = workbook.getSheetAt(1);
        int toFixIndex=1;
        int isFixedIndex=1;
        for (Course course : courseList) {
            Row row;
            if (course.getCourseState().equals("01")) {
                row = sheetToFix.createRow(toFixIndex++);
            } else {
                row = sheetIsFixed.createRow(isFixedIndex++);
            }
            Cell[] cells = new Cell[16];
            for (int k = 0; k < 16; k++) {
                cells[k] = row.createCell(k);
            }
            if (course.getMajorId() != null)
                cells[0].setCellValue(course.getMajorId());
            cells[1].setCellValue(course.getYear());
            cells[2].setCellValue(course.getCode());
            cells[3].setCellValue(course.getName());
            if (course.getCreditSum() != null)
                cells[4].setCellValue(course.getCreditSum());
            if (course.getHourWeek() != null)
                cells[5].setCellValue(course.getHourWeek());
            if (course.getHourTeach() != null)
                cells[6].setCellValue(course.getHourTeach());
            if (course.getHourPractice() != null)
                cells[7].setCellValue(course.getHourPractice());
            if (course.getHourOperation() != null)
                cells[8].setCellValue(course.getHourOperation());
            if (course.getHourOutside() != null)
                cells[9].setCellValue(course.getHourOutside());
            if (course.getCourseCategory() != null)
                cells[10].setCellValue(course.getCourseCategory().toString());
            if (course.getTestType() != null)
                cells[11].setCellValue(course.getTestType().toString());
            if (course.getCollegeId() != null)
                cells[12].setCellValue(course.getCollegeId());
            cells[13].setCellValue(course.getTerm());
            if (course.getCourseType() != null)
                cells[14].setCellValue(course.getCourseType().toString());
            cells[15].setCellValue(course.getCourseState().equals("01") ? "待修订" : "已修订");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos;
    }

    /**
     * 将cell中的内容读取并返回object对象
     *
     * @param cell Excel单元格
     * @return object 单元格内容
     */
    private static Object getCellValue(Cell cell) {
        // 1. 获取到单元格 的数据类型
        CellType cellType = cell.getCellType();
        // 2. 根据单元格数据类型获取数据
        Object value = null;
        switch (cellType) {
            case STRING: // 字符串类型
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:// boolean类型
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:// 数字类型(包含日期和普通数字)
                // 日期：
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                } else {
                    // 数字
                    value = cell.getNumericCellValue();
                }
                break;
            case FORMULA: // 公式类型
                value = cell.getCellFormula();
                break;
            default:
                break;
        }
        return value;
    }
}
