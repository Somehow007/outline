package com.ujs.outline;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.common.handler.ExcelHandler;
import com.ujs.outline.domain.College;
import com.ujs.outline.domain.Course;
import com.ujs.outline.domain.Major;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.CourseService;
import com.ujs.outline.service.MajorService;
import com.ujs.outline.service.OutlineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.util.List;
import java.util.Map;

@SpringBootTest
class OutlineApplicationTests {

    @Autowired
    CollegeService collegeService;
    @Autowired
    MajorService majorService;
    @Autowired
    CourseService courseService;
    @Autowired
    OutlineService outlineService;

    @Test
    void contextLoads() {
//        readColleges();
//        readMajors();
//        readCourses();
//        setCodeToFix();
    }

    private void readColleges() {
        String filePath="C:\\Users\\Administrator\\Desktop\\附件1. 2020版培养计划待修订课程汇总表.xlsx";
        File file=new File(filePath);
        List<College> colleges = ExcelHandler.getCollegesFromExcel(file);
        System.out.println(colleges.size());
        for (College college : colleges) {
            collegeService.save(college);
        }
    }
    private void readMajors(){
        String filePath="C:\\Users\\Administrator\\Desktop\\附件1. 2020版培养计划待修订课程汇总表.xlsx";
        File file=new File(filePath);
        Map<String,String> map=ExcelHandler.getMajorsFromExcel(file);
        System.out.println(map.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            QueryWrapper<College> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("college_name",entry.getValue() );
            College college = collegeService.getOne(queryWrapper);
            if (college == null) {
                System.out.println("查询不到学院信息~~~~~~"+entry.getKey()+":"+entry.getValue());
                continue;
            }
            Major major = new Major();
            major.setCollegeId(college.getCollegeId());
            major.setMajorName(entry.getKey());
            //获取ID最值
            QueryWrapper<Major> queryWrapper1=new QueryWrapper<>();
            queryWrapper1.eq("college_id",college.getCollegeId());
            List<Major> list=majorService.list(queryWrapper1);
            String max = "00";
            if (list.size() > 0) {
                for (Major major1 : list) {
                    if (major1.getMajorId().compareTo(max)>0) {
                        max=major1.getMajorId();
                    }
                }
            }
            if (max.equals("00")) {
                max=college.getCollegeId()+"00";
            } else {
                int index = Integer.parseInt(max.substring(2));
                max=college.getCollegeId()+(++index<10?"0"+index:index);
            }
            major.setMajorId(max);
            majorService.save(major);
        }
    }
    private void readCourses(){
        String filePath="C:\\Users\\Administrator\\Desktop\\附件1. 2020版培养计划待修订课程汇总表.xlsx";
        File file=new File(filePath);
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
            //是否重复
            QueryWrapper<Course> wrapper=new QueryWrapper<>();
            wrapper.eq("code", course.getCode());
            wrapper.eq("year",course.getYear());
            wrapper.eq("major_id",course.getMajorId());
            Course one = courseService.getOne(wrapper);
            if (one != null) {
                continue;
            }
            try {
                courseService.save(course);
            } catch (DataIntegrityViolationException e) {
                System.out.println(course);
                e.printStackTrace();
            }

        }
    }

    private void setCodeToFix() {
        String filePath="C:\\Users\\Administrator\\Desktop\\附件1. 2020版培养计划待修订课程汇总表.xlsx";
        File file=new File(filePath);
        List<String> list = ExcelHandler.getCodeToFixFromExcel(file);
        for (String s : list) {
            List<Course> tempList = courseService.selectCourseByCodeAndYear(s, "2020");
            for (Course course : tempList) {
                course.setCourseState("01");
            }
            courseService.saveOrUpdateBatch(tempList);
        }
    }
}
