package com.ujs.outline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.Course;
import com.ujs.outline.domain.OutlineCourse;
import com.ujs.outline.mapper.OutlineCourseMapper;
import com.ujs.outline.service.OutlineCourseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutlineCourseServiceImpl extends ServiceImpl<OutlineCourseMapper, OutlineCourse> implements OutlineCourseService {

    public List<OutlineCourse> selectByOutlineId(Integer outlineId) {
        QueryWrapper<OutlineCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("outline_id", outlineId);
        return getBaseMapper().selectList(wrapper);
    }

    /**
     * 分页查询
     */
    public ResultData selectWithPage(long cur, long size, Integer outlineId){
        QueryWrapper<OutlineCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("outline_id", outlineId);
        Page<OutlineCourse> page=new Page<>(cur,size);
        Page<OutlineCourse> outlineCoursePage=getBaseMapper().selectPage(page,wrapper);
        ResultData resultData = new ResultData();
        resultData.setData(outlineCoursePage.getRecords());
        resultData.setCur(outlineCoursePage.getCurrent());
        resultData.setTotal(outlineCoursePage.getTotal());
        resultData.setSize(outlineCoursePage.getSize());
        return resultData;
    }
}
