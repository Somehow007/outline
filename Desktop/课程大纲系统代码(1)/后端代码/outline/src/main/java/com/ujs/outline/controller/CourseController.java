package com.ujs.outline.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.common.ResultObj;
import com.ujs.outline.domain.*;
import com.ujs.outline.domain.enums.CourseCategoryEnum;
import com.ujs.outline.domain.enums.CourseTypeEnum;
import com.ujs.outline.domain.enums.TestTypeEnum;
import com.ujs.outline.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程相关接口
 */
@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    CourseService courseService;
    @Autowired
    CollegeService collegeService;
    @Autowired
    MajorService majorService;
    @Autowired
    OutlineService outlineService;
    @Autowired
    OutlineCourseService outlineCourseService;
    @Autowired
    OldOutlineCourseService oldOutlineCourseService;
    /**
     * 条件、分页查询
     */
    @GetMapping("/")
    public ResultData getAllCourse(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                   @RequestParam(value = "course_id", defaultValue = "-1") Integer courseId,
                                   @RequestParam(value = "code", defaultValue = "") String code,
                                   @RequestParam(value = "name", defaultValue = "") String name,
                                   @RequestParam(value = "course_type", defaultValue = "-1") Integer courseType,
                                   @RequestParam(value = "course_category", defaultValue = "-1") Integer courseCategory,
                                   @RequestParam(value = "major_id", defaultValue = "") String majorId,
                                   @RequestParam(value = "college_id", defaultValue = "") String collegeId,
                                   @RequestParam(value = "term", defaultValue = "-1") Integer term,
                                   @RequestParam(value = "course_state", defaultValue = "") String courseState,
                                   @RequestParam(value = "year", defaultValue = "") String year) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        if (courseId != -1) {
            wrapper.eq("course_id",courseId);
        } else {
            wrapper.eq(!code.equals(""),"code", code);
            wrapper.like(!name.equals(""),"name", name);
            wrapper.eq(courseType!=-1,"course_type", courseType);
            wrapper.eq(courseCategory!=-1,"course_category", courseCategory);
            if (!majorId.equals("")) {
                List<Major> majorList = majorService.getMajorLikeByName(majorId);
                if (!CollectionUtils.isEmpty(majorList)) {
                    wrapper.in("major_id", majorList.stream().map(Major::getMajorId).collect(Collectors.toList()));
                }
//                if (majorList.size() > 0) {
//                    wrapper.and(i -> {
//                        i.like("major_id", majorList.get(0).getMajorId());
//                        for (int j = 1; j < majorList.size(); j++) {
//                            i.or().like("major_id", majorList.get(j).getMajorId());
//                        }
//                    });
//                }
            }
            if (!collegeId.equals("")) {
                List<College> collegeList = collegeService.getCollegeLikeByName(collegeId);
                if (!CollectionUtils.isEmpty(collegeList)) {
                    wrapper.in("college_id", collegeList.stream().map(College::getCollegeId).collect(Collectors.toList()));
                }
//                if (collegeList.size() > 0) {
//                    wrapper.and(i -> {
//                        i.like("college_id", collegeList.get(0).getCollegeId());
//                        for (int j = 1; j < collegeList.size(); j++) {
//                            i.or().like("college_id", collegeList.get(j).getCollegeId());
//                        }
//                    });
//                }
            }
            wrapper.like(term!=-1,"term", term);
            wrapper.like(!courseState.equals(""),"course_state", courseState);
            wrapper.like(!year.equals(""),"year", year);
        }

        return courseService.selectWithWrapper(page, size,wrapper);
    }


    /**
     * 简单查询，用于大纲绑定课程时的选择列表
     */
    @GetMapping("/user/simpleSearch")
    public ResultData getSimpleCourse(@RequestParam(value = "code", defaultValue = "") String code,
                                      @RequestParam(value = "year", defaultValue = "") String year,
                                      @RequestParam(value = "state", defaultValue = "") String state) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(code.equals("")||year.equals("")||user==null)
            return new ResultData();
        return courseService.selectSimple(code, year,state,user.getCollegeId());
    }

    @GetMapping("/searchByOutlineId")
    public ResultData getSimpleCourse(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                      @RequestParam(value = "outline_id") Integer outlineId) {
        if (outlineId == null) {
            return null;
        }
        return courseService.selectWithOutlineId(page, size, outlineId);
    }

    /**
     * 添加课程信息
     */
    @PostMapping("/mainAdmin/add")
    public ResultObj addCourse(
            @RequestParam(value = "code", defaultValue = "") String code,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "college_name", defaultValue = "") String college_name,
            @RequestParam(value = "major_name", defaultValue = "") String major_name,
            @RequestParam(value = "course_type", defaultValue = "-1") Integer course_type,
            @RequestParam(value = "course_category", defaultValue = "-1") Integer course_category,
            @RequestParam(value = "term", defaultValue = "-1") Integer term,
            @RequestParam(value = "course_state", defaultValue = "") String course_state,
            @RequestParam(value = "year", defaultValue = "") String year,
            @RequestParam(value = "credit_sum", defaultValue = "-1.0") Double credit_sum,
            @RequestParam(value = "hour_week", defaultValue = "-1.0") Double hour_week,
            @RequestParam(value = "hour_teach", defaultValue = "-1") Integer hour_teach,
            @RequestParam(value = "hour_practice", defaultValue = "-1") Integer hour_practice,
            @RequestParam(value = "hour_operation", defaultValue = "-1") Integer hour_operation,
            @RequestParam(value = "hour_outside", defaultValue = "-1") Integer hour_outside) {

        if (code.equals("")||name.equals("")||college_name.equals("")||major_name.equals("")) {
            return ResultObj.PARAM_MISS;
        }
        Course course = new Course();
        List<College> colleges = collegeService.getCollegeLikeByName(college_name);
        List<Major> majors = majorService.getMajorLikeByName(major_name);
        if (colleges.size()==0||majors.size()==0) {
            return ResultObj.PARAM_MISS;
        }
        course.setCollegeId(colleges.get(0).getCollegeId());
        course.setMajorId(majors.get(0).getMajorId());
        course.setCode(code);
        course.setName(name);
        course.setCreditSum(credit_sum < 0 ? null : credit_sum);
        course.setHourWeek(hour_week < 0 ? null : hour_week);
        course.setHourTeach(hour_teach < 0 ? null : hour_teach);
        course.setHourPractice(hour_practice < 0 ? null : hour_practice);
        course.setHourOperation(hour_operation < 0 ? null : hour_operation);
        course.setHourOutside(hour_outside < 0 ? null : hour_outside);
        course.setCourseType(CourseTypeEnum.getInstance(course_type));
        course.setCourseCategory(CourseCategoryEnum.getInstance(course_category));
        course.setTestType(TestTypeEnum.EXAM);
        course.setTerm(term);
        course.setCourseState(course_state);
        course.setYear(year);
        boolean flag=courseService.save(course);
        if (!flag) {
            return ResultObj.UPDATE_ERROR;
        }
        collegeService.modifyCourseNum(colleges.get(0).getCollegeId(),1);
        majorService.modifyCourseNum(majors.get(0).getMajorId(),1);

        return ResultObj.UPDATE_SUCCESS;
    }

    /**
     * 修改课程信息
     */
    @PutMapping("/mainAdmin/update")
    public ResultObj updateCourse(
            @RequestParam(value = "course_id", defaultValue = "-1") Integer course_id,
            @RequestParam(value = "code", defaultValue = "") String code,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "course_type", defaultValue = "-1") Integer course_type,
            @RequestParam(value = "course_category", defaultValue = "-1") Integer course_category,
            @RequestParam(value = "term", defaultValue = "-1") Integer term,
            @RequestParam(value = "course_state", defaultValue = "") String course_state,
            @RequestParam(value = "year", defaultValue = "") String year,
            @RequestParam(value = "credit_sum", defaultValue = "-1.0") Double credit_sum,
            @RequestParam(value = "hour_week", defaultValue = "-1.0") Double hour_week,
            @RequestParam(value = "hour_teach", defaultValue = "-1") Integer hour_teach,
            @RequestParam(value = "hour_practice", defaultValue = "-1") Integer hour_practice,
            @RequestParam(value = "hour_operation", defaultValue = "-1") Integer hour_operation,
            @RequestParam(value = "hour_outside", defaultValue = "-1") Integer hour_outside) {

        if (course_id.equals(-1)) {
            return ResultObj.PARAM_MISS;
        }
        Course course = courseService.getById(course_id);
        if (course == null) {
            return ResultObj.PARAM_MISS;
        }

        course.setCode(code);
        course.setName(name);
        course.setCreditSum(credit_sum < 0 ? null : credit_sum);
        course.setHourWeek(hour_week < 0 ? null : hour_week);
        course.setHourTeach(hour_teach < 0 ? null : hour_teach);
        course.setHourPractice(hour_practice < 0 ? null : hour_practice);
        course.setHourOperation(hour_operation < 0 ? null : hour_operation);
        course.setHourOutside(hour_outside < 0 ? null : hour_outside);
        course.setCourseType(CourseTypeEnum.getInstance(course_type));
        course.setCourseCategory(CourseCategoryEnum.getInstance(course_category));
        course.setTerm(term);
        course.setCourseState(course_state);
        course.setYear(year);
        return courseService.saveOrUpdate(course) ? ResultObj.UPDATE_SUCCESS : ResultObj.UPDATE_ERROR;
    }

    /**
     * 删除课程
     */
    private boolean delete(Integer courseId) {
        Course course = courseService.getById(courseId);
        if(course==null)
            return false;
        //修改开课学院与授课专业需求量
        College college = collegeService.getById(course.getCollegeId());
        Major major = majorService.getById(course.getMajorId());
        college.setCourseNum(college.getCourseNum()-1);
        major.setCourseNum(major.getCourseNum()-1);
        if (course.getCourseState().equals("02") || course.getCourseState().equals("03")) {
            college.setRequireNum(college.getRequireNum()+1);
            major.setRequireNum(major.getRequireNum()+1);
            Outline outline = outlineService.getById(course.getOutlineId());
            outline.setConnectNum(outline.getConnectNum()-1);
            outlineService.saveOrUpdate(outline);
            outlineCourseService.removeById(courseId);
            oldOutlineCourseService.removeByCourseId(courseId);
        }
        collegeService.saveOrUpdate(college);
        majorService.saveOrUpdate(major);
        return courseService.removeById(courseId);
    }

    /**
     * 删除单个课程
     */
    @DeleteMapping("/mainAdmin/delete")
    public ResultObj deleteCourse(@RequestParam(value = "course_id") Integer courseId) {
        if (courseId == null) {
            return ResultObj.PARAM_MISS;
        }
        return delete(courseId) ? ResultObj.DELETE_SUCCESS : ResultObj.DELETE_ERROR;
    }

    /**
     * 批量删除课程
     */
    @DeleteMapping("/mainAdmin/deleteAll")
    public ResultObj deleteAllCourse(@RequestParam(value = "course_ids",defaultValue = "") String courseIds) {
        int num=0;
        //获取参数
        if (courseIds.equals("")) {
            return ResultObj.PARAM_MISS;
        }
        String[] strings=courseIds.split("-");
        for (String s : strings) {
            Integer i = Integer.parseInt(s);
            if(delete(i)) num++;
        }
        return num > 0 ? ResultObj.DELETE_SUCCESS : ResultObj.DELETE_ERROR;
    }
}
