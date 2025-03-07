package com.ujs.outline.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.*;
import com.ujs.outline.mapper.CourseMapper;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.CourseService;
import com.ujs.outline.service.MajorService;
import com.ujs.outline.service.OutlineCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    CollegeService collegeService;
    @Autowired
    MajorService majorService;
    @Autowired
    OutlineCourseService outlineCourseService;

    /**
     * 根据课程代码与年级查询课程
     * @param code 课程代码
     * @param year 年级
     * @return
     */
    public List<Course> selectCourseByCodeAndYear(String code, String year) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        wrapper.eq("year", year);
        return getBaseMapper().selectList(wrapper);
    }

    /**
     * 查询开课学院的待修订课程数与课程总数
     * 0-课程总数 1-待修订大纲课程数
     */
    public Integer[] selectCountWithCollege(String collegeId) {
        Integer[] res = new Integer[2];
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("college_id", collegeId);
        res[0] = getBaseMapper().selectCount(wrapper);
        wrapper.eq("course_state","01");
        res[1] = getBaseMapper().selectCount(wrapper);
        return res;
    }

    /**
     * 查询上课专业的待修订课程数与课程总数
     * 0-课程总数 1-待修订大纲课程数
     */
    public Integer[] selectCountWithMajor(String majorId) {
        Integer[] res = new Integer[2];
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("major_id", majorId);
        res[0] = getBaseMapper().selectCount(wrapper);
        wrapper.eq("course_state","01");
        res[1] = getBaseMapper().selectCount(wrapper);
        return res;
    }


    /**
     * 构造自定义条件查询
     */
    public ResultData selectWithWrapper(long cur,long size,QueryWrapper<Course> wrapper){
        Page<Course> page=new Page<>(cur,size);
        Page<Course> coursePage=getBaseMapper().selectPage(page,wrapper);
        ResultData resultData = new ResultData();
        List<Course> records = coursePage.getRecords();
        for (Course course : records) {
            course.setCollegeId(collegeService.getById(course.getCollegeId()).getCollegeName());
            course.setMajorId(majorService.getById(course.getMajorId()).getMajorName());
            if (course.getCourseState().equals("02") || course.getCourseState().equals("03")) {
                course.setOutlineId(outlineCourseService.getById(course.getCourseId()).getOutlineId());
            }
        }
        resultData.setData(coursePage.getRecords());
        resultData.setCur(coursePage.getCurrent());
        resultData.setTotal(coursePage.getTotal());
        resultData.setSize(coursePage.getSize());
        return resultData;
    }

    public List<Course> selectWithYear(String year){
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("year", year);
        wrapper.ne("course_state","00");
        List<Course> courses = getBaseMapper().selectList(wrapper);
        //构建专业，学院 id-name映射
        Map<String, String> majorMap = new HashMap<>();
        Map<String, String> collegeMap = new HashMap<>();
        List<Major> list = majorService.getBaseMapper().selectList(new QueryWrapper<>());
        for (Major major : list) {
            majorMap.put(major.getMajorId(),major.getMajorName());
        }
        List<College> list2 = collegeService.getBaseMapper().selectList(new QueryWrapper<>());
        for (College college : list2) {
            collegeMap.put(college.getCollegeId(),college.getCollegeName());
        }
        for (Course course : courses) {
            course.setMajorId(majorMap.get(course.getMajorId()));
            course.setCollegeId(collegeMap.get(course.getCollegeId()));
        }
        return courses;
    }

    /**
     * 简单课程类获取
     */
    public ResultData selectSimple(String code, String year,String state,String collegeId) {
        ResultData resultData = new ResultData();
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        wrapper.eq("year", year);
        wrapper.eq("college_id", collegeId);
        wrapper.eq(!state.equals(""),"course_state",state);
        List<Course> list = getBaseMapper().selectList(wrapper);
        List<SimpleCourse> slist = new ArrayList<>();
        for (Course course : list) {
            course.setMajorId(majorService.getById(course.getMajorId()).getMajorName());
            if (course.getCourseState().equals("02") || course.getCourseState().equals("03")) {
                course.setOutlineId(outlineCourseService.getById(course.getCourseId()).getOutlineId());
            }
            SimpleCourse c = new SimpleCourse(course);
            slist.add(c);
        }
        resultData.setSize((long) slist.size());
        resultData.setTotal((long) slist.size());
        resultData.setCur(1L);
        resultData.setData(slist);
        return resultData;
    }

    /**
     * 根据课程大纲分页查询
     */
    public ResultData selectWithOutlineId(long cur,long size,Integer outlineId){
        ResultData resultData = outlineCourseService.selectWithPage(cur, size, outlineId);
        List<OutlineCourse> outlineCourseList=(List<OutlineCourse>) resultData.getData();
        List<Course> courseList = new LinkedList<>();
        for (OutlineCourse outlineCourse : outlineCourseList) {
            Course course = getById(outlineCourse.getCourseId());
            course.setCollegeId(collegeService.getById(course.getCollegeId()).getCollegeName());
            course.setMajorId(majorService.getById(course.getMajorId()).getMajorName());
            courseList.add(course);
        }
        resultData.setData(courseList);
        return resultData;
    }
}
