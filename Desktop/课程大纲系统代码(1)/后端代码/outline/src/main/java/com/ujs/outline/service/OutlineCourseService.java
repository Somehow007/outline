package com.ujs.outline.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.OutlineCourse;

import java.util.List;

public interface OutlineCourseService extends IService<OutlineCourse> {
    List<OutlineCourse> selectByOutlineId(Integer outlineId);
    ResultData selectWithPage(long cur, long size, Integer outlineId);
}
