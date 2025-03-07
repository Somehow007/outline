package com.ujs.outline.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.Major;

import java.util.List;

public interface MajorService extends IService<Major> {
    String getMaxMajorId(QueryWrapper<Major> wrapper);
    List<Major> getMajorLikeByName(String majorName);
    ResultData selectWithWrapper(long cur, long size, QueryWrapper<Major> wrapper);
    void countAll();
    void modifyCourseNum(String majorId, int num);
    void modifyRequireNum(String majorId, int num);
}
