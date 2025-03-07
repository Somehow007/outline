package com.ujs.outline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.College;
import com.ujs.outline.domain.Outline;
import com.ujs.outline.mapper.OutlineMapper;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.OutlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutlineServiceImpl extends ServiceImpl<OutlineMapper, Outline> implements OutlineService {
    @Autowired
    CollegeService collegeService;
    /**
     * 0-课程总数 1-待修订大纲课程数
     */
    public Integer selectCountWithCollege(String collegeId) {
        QueryWrapper<Outline> wrapper = new QueryWrapper<>();
        wrapper.eq("college_id", collegeId);
        return getBaseMapper().selectCount(wrapper);
    }

    public Outline getByFileNameAndCollegeId(String fileName, String collegeId) {
        QueryWrapper<Outline> wrapper = new QueryWrapper<>();
        wrapper.eq("file_name", fileName);
        wrapper.eq("college_id", collegeId);
        return getBaseMapper().selectList(wrapper).get(0);
    }

    public synchronized void modifyConnectNum(Integer outlineId,int num) {
        Outline outline = getById(outlineId);
        outline.setConnectNum(outline.getConnectNum()+num);
        saveOrUpdate(outline);
    }

    /**
     * 构造自定义条件查询
     */
    public ResultData selectWithWrapper(long cur, long size, QueryWrapper<Outline> wrapper){
        Page<Outline> page=new Page<>(cur,size);
        Page<Outline> outlinePage=getBaseMapper().selectPage(page,wrapper);
        ResultData resultData = new ResultData();
        List<Outline> records = outlinePage.getRecords();
        for (Outline outline : records) {
            String fileName = outline.getFileName();
            int index = fileName.lastIndexOf(".");
            fileName=fileName.substring(0,index-36)+fileName.substring(index);
            outline.setFileName(fileName);
            outline.setCollegeId(collegeService.getById(outline.getCollegeId()).getCollegeName());
        }
        resultData.setData(outlinePage.getRecords());
        resultData.setCur(outlinePage.getCurrent());
        resultData.setTotal(outlinePage.getTotal());
        resultData.setSize(outlinePage.getSize());
        return resultData;
    }
}
