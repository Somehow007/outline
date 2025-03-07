package com.ujs.outline.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.College;

import java.util.List;

public interface CollegeService extends IService<College> {
    String getMaxCollegeId();
    List<College> getCollegeLikeByName(String collegeName);
    void countAll();
    ResultData selectWithWrapper(long cur, long size, QueryWrapper<College> wrapper);
    void modifyCourseNum(String collegeId,int num);
    void modifyRequireNum(String collegeId,int num);
    void modifyUploadNum(String collegeId,int num);
}
