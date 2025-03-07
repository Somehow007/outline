package com.ujs.outline.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.outline.domain.OldOutlineCourse;

import java.util.List;

public interface OldOutlineCourseService extends IService<OldOutlineCourse> {
    int removeByCourseId(Integer courseId);
    List<OldOutlineCourse> selectByCourseId(Integer courseId);
    int removeByOutlineId(Integer outlineId);
}
