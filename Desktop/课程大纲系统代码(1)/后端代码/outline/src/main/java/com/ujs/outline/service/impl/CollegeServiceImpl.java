package com.ujs.outline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.College;
import com.ujs.outline.mapper.CollegeMapper;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.CourseService;
import com.ujs.outline.service.OutlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService {
    @Autowired
    CourseService courseService;
    @Autowired
    OutlineService outlineService;

    public String getMaxCollegeId() {
        return getBaseMapper().getMaxCollegeId();
    }

    public List<College> getCollegeLikeByName(String collegeName) {
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        wrapper.like("college_name", collegeName);
        return getBaseMapper().selectList(wrapper);
    }

    //重新计算所有开课学院的课程总数/待修订课程数/上传大纲数
    public void countAll(){
        List<College> list = getBaseMapper().selectList(new QueryWrapper<>());
        for (College college : list) {
            Integer[] integers = courseService.selectCountWithCollege(college.getCollegeId());
            college.setCourseNum(integers[0]);
            college.setRequireNum(integers[1]);
            college.setUploadNum(outlineService.selectCountWithCollege(college.getCollegeId()));
        }
        saveOrUpdateBatch(list);
    }

    public void modifyCourseNum(String collegeId,int num) {
        College college = getById(collegeId);
        college.setCourseNum(college.getCourseNum()+num);
        saveOrUpdate(college);
    }

    public void modifyRequireNum(String collegeId,int num) {
        College college = getById(collegeId);
        college.setRequireNum(college.getRequireNum()+num);
        saveOrUpdate(college);
    }

    public void modifyUploadNum(String collegeId,int num) {
        College college = getById(collegeId);
        college.setUploadNum(college.getUploadNum()+num);
        saveOrUpdate(college);
    }

    /**
     * 构造自定义条件查询
     */
    public ResultData selectWithWrapper(long cur, long size, QueryWrapper<College> wrapper){
        Page<College> page=new Page<>(cur,size);
        Page<College> collegePage=getBaseMapper().selectPage(page,wrapper);
        ResultData resultData = new ResultData();
        resultData.setData(collegePage.getRecords());
        resultData.setCur(collegePage.getCurrent());
        resultData.setTotal(collegePage.getTotal());
        resultData.setSize(collegePage.getSize());
        return resultData;
    }

    /**
     * 根据学院id获取学院名称
     */
    @Override
    public List<College> getCollegeNameAndId() {
        System.out.println("basemapper实例" + this.getBaseMapper());

        QueryWrapper<College> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("college_id", "college_name");
        return getBaseMapper().selectList(queryWrapper);
    }

}
