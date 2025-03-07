package com.ujs.outline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.outline.domain.OldOutlineCourse;
import com.ujs.outline.mapper.OldOutlineCourseMapper;
import com.ujs.outline.service.OldOutlineCourseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OldOutlineCourseServiceImpl extends ServiceImpl<OldOutlineCourseMapper, OldOutlineCourse> implements OldOutlineCourseService {
    public int removeByCourseId(Integer courseId) {
        QueryWrapper<OldOutlineCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        return getBaseMapper().delete(wrapper);
    }

    public int removeByOutlineId(Integer outlineId) {
        QueryWrapper<OldOutlineCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("outline_id", outlineId);
        return getBaseMapper().delete(wrapper);
    }

    public List<OldOutlineCourse> selectByCourseId(Integer courseId) {
        QueryWrapper<OldOutlineCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        return getBaseMapper().selectList(wrapper);
    }
}
