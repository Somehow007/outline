package com.ujs.outline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.Major;
import com.ujs.outline.mapper.MajorMapper;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.CourseService;
import com.ujs.outline.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {
    @Autowired
    CourseService courseService;
    @Autowired
    CollegeService collegeService;

    public String getMaxMajorId(QueryWrapper<Major> wrapper) {
        return getBaseMapper().getMaxMajorId(wrapper);
    }

    public List<Major> getMajorLikeByName(String majorName) {
        QueryWrapper<Major> wrapper = new QueryWrapper<>();
        wrapper.like("major_name", majorName);
        return getBaseMapper().selectList(wrapper);
    }

    //重新计算所有专业的课程总数/待修订课程数
    public void countAll(){
        List<Major> list = getBaseMapper().selectList(new QueryWrapper<>());
        for (Major major : list) {
            Integer[] integers = courseService.selectCountWithMajor(major.getMajorId());
            major.setCourseNum(integers[0]);
            major.setRequireNum(integers[1]);
        }
        saveOrUpdateBatch(list);
    }

    public void modifyCourseNum(String majorId, int num) {
        Major major=getById(majorId);
        major.setCourseNum(major.getCourseNum()+num);
        saveOrUpdate(major);
    }

    public void modifyRequireNum(String majorId, int num) {
        Major major=getById(majorId);
        major.setRequireNum(major.getRequireNum()+num);
        saveOrUpdate(major);
    }

    /**
     * 构造自定义条件查询
     */
    public ResultData selectWithWrapper(long cur, long size, QueryWrapper<Major> wrapper){
        Page<Major> page=new Page<>(cur,size);
        Page<Major> majorPage=getBaseMapper().selectPage(page,wrapper);
        ResultData resultData = new ResultData();
        List<Major> records = majorPage.getRecords();
        for (Major major : records) {
            major.setCollegeId(collegeService.getById(major.getCollegeId()).getCollegeName());
        }
        resultData.setData(majorPage.getRecords());
        resultData.setCur(majorPage.getCurrent());
        resultData.setTotal(majorPage.getTotal());
        resultData.setSize(majorPage.getSize());
        return resultData;
    }
}
