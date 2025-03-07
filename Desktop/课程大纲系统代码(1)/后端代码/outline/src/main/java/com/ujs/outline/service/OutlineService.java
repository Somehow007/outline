package com.ujs.outline.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.Outline;

public interface OutlineService extends IService<Outline> {
    Integer selectCountWithCollege(String collegeId);
    ResultData selectWithWrapper(long cur, long size, QueryWrapper<Outline> wrapper);
    Outline getByFileNameAndCollegeId(String fileName, String collegeId);
    void modifyConnectNum(Integer outlineId, int num);
}
