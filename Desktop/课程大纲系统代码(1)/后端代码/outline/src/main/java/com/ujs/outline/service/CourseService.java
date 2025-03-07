package com.ujs.outline.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.Course;

import java.util.List;

public interface CourseService extends IService<Course> {
    List<Course> selectCourseByCodeAndYear(String code, String year);
    Integer[] selectCountWithCollege(String collegeId);
    Integer[] selectCountWithMajor(String majorId);
    ResultData selectWithWrapper(long cur, long size, QueryWrapper<Course> wrapper);
    ResultData selectSimple(String code, String year,String state,String collegeId);
    ResultData selectWithOutlineId(long cur,long size,Integer outlineId);
    List<Course> selectWithYear(String year);
}
