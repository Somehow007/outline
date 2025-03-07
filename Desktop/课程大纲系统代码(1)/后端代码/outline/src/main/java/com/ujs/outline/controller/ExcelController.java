package com.ujs.outline.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.outline.common.ResultObj;
import com.ujs.outline.common.handler.ExcelHandler;
import com.ujs.outline.domain.*;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.CourseService;
import com.ujs.outline.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * Excel上传相关接口
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {
    @Autowired
    CollegeService collegeService;
    @Autowired
    MajorService majorService;
    @Autowired
    CourseService courseService;

    /**
     * 管理员通过Excel添加培养计划课程
     */
    @RequestMapping(value = "/addCourse", method = RequestMethod.POST)
    public ResultObj addCourse(MultipartFile uploadFile) throws Exception {

        if (uploadFile == null) {//上传文件丢失
            return ResultObj.UPLOAD_MISS;
        }
        String suffix = uploadFile.getOriginalFilename().substring(
                uploadFile.getOriginalFilename().lastIndexOf("."));

        if (!(suffix.equals(".xlsx") || suffix.equals(".xls"))) {
            return ResultObj.UPLOAD_ERROR_TYPE;
        }
        //临时文件创建在tempFile文件夹中
        String filepath = System.getProperty("user.dir") + File.separator + "tempFile";
        File dir = new File(filepath);
        if(!dir.exists()&&!dir.isDirectory())
            dir.mkdirs();
        String filename = UUID.randomUUID().toString() + suffix;
        File file = new File(filepath + filename);
        uploadFile.transferTo(file);

        List<Course> list = ExcelHandler.getCoursesFromExcel(file);
        System.out.println(list.size());
        for (Course course : list) {
            //绑定学院
            QueryWrapper<College> collegeWrapper=new QueryWrapper<>();
            collegeWrapper.eq("college_name", course.getCollegeId());
            College college = collegeService.getOne(collegeWrapper);
            if (college == null) {
                System.out.println("不存在的学院:"+course.getCollegeId());
                continue;
            }
            course.setCollegeId(college.getCollegeId());
            //绑定专业
            QueryWrapper<Major> majorWrapper=new QueryWrapper<>();
            majorWrapper.eq("major_name", course.getMajorId());
            Major major = majorService.getOne(majorWrapper);
            if (major == null) {
                System.out.println("不存在的专业:"+course.getMajorId());
                continue;
            }
            course.setMajorId(major.getMajorId());
            try {
                courseService.save(course);
            } catch (DataIntegrityViolationException e) {
                System.out.println(course);
                e.printStackTrace();
            }
        }
        if(file.exists()&& file.isFile())
            file.delete();

        collegeService.countAll();
        majorService.countAll();
        return ResultObj.ADD_SUCCESS;
    }

    /**
     * @deprecated
     * 管理员通过Excel设置待修订课程,同时更新各学院，专业待修订课程数
     */
    @RequestMapping(value = "/setCourseToFix", method = RequestMethod.POST)
    public ResultObj setCourseToFix(MultipartFile uploadFile,@RequestParam(value = "year") String year) throws Exception {

        if (year == null || year.equals("")) {
            return ResultObj.PARAM_MISS;
        }
        if (uploadFile == null) {//上传文件丢失
            return ResultObj.UPLOAD_MISS;
        }
        String suffix = uploadFile.getOriginalFilename().substring(
                uploadFile.getOriginalFilename().lastIndexOf("."));

        if (!(suffix.equals(".xlsx") || suffix.equals(".xls"))) {
            return ResultObj.UPLOAD_ERROR_TYPE;
        }
        //临时文件创建在tempFile文件夹中
        String filepath = System.getProperty("user.dir") + File.separator + "tempFile";
        File dir = new File(filepath);
        if(!dir.exists()&&!dir.isDirectory())
            dir.mkdirs();
        String filename = UUID.randomUUID().toString() + suffix;
        File file = new File(filepath + filename);
        uploadFile.transferTo(file);

        List<String> list = ExcelHandler.getCodeToFixFromExcel(file);
        for (String s : list) {
            List<Course> tempList = courseService.selectCourseByCodeAndYear(s, year);
            for (Course course : tempList) {
                if (course.getCourseState().equals("00"))
                    course.setCourseState("01");
            }
            courseService.saveOrUpdateBatch(tempList);
        }
        if(file.exists()&& file.isFile())
            file.delete();

        collegeService.countAll();
        majorService.countAll();
        return ResultObj.ADD_SUCCESS;
    }

    /**
     * 下载大纲
     */
    @RequestMapping(value = "/downloadTemplate", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadTemplate() {
        //获取大纲文件
        String filepath = System.getProperty("user.dir") + File.separator + "tempFile";
        File file = new File(filepath + File.separator + "培养计划课程设置表.xlsx");

        //返回BOS文件流
        String filename = "培养计划课程设置表.xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment",
                new String(filename.getBytes(), StandardCharsets.ISO_8859_1));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            FileInputStream fis = new FileInputStream(file);
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bos.toByteArray(), headers, HttpStatus.CREATED);
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseEntity;
    }

    /**
     * 导出课程大纲修订情况统计
     */
    @RequestMapping(value = "exportCountExcel", method = RequestMethod.GET)
    public ResponseEntity<byte[]> exportCountExcel(@RequestParam(value = "year") String year) throws Exception {
        String filename = year+"年级课程大纲修订情况统计.xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment",
                new String(filename.getBytes(), StandardCharsets.ISO_8859_1));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        List<Course> courses = courseService.selectWithYear(year);

        ByteArrayOutputStream bos = ExcelHandler.exportCountExcel(courses);

        return new ResponseEntity<byte[]>(bos.toByteArray(), headers, HttpStatus.CREATED);
    }

}
