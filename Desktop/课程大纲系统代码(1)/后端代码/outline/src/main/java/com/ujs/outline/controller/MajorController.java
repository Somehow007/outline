package com.ujs.outline.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.common.ResultObj;
import com.ujs.outline.domain.College;
import com.ujs.outline.domain.Major;
import com.ujs.outline.domain.User;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/major")
public class MajorController {
    @Autowired
    MajorService majorService;
    @Autowired
    CollegeService collegeService;

    @GetMapping("/mainAdmin/countNums")
    public ResultObj countNums(){
        majorService.countAll();
        return ResultObj.UPDATE_SUCCESS;
    }

    /**
     * 条件、分页查询
     */
    @GetMapping("/")
    public ResultData getAllCollege(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                    @RequestParam(value = "major_id", defaultValue = "") String majorId,
                                    @RequestParam(value = "major_name", defaultValue = "") String majorName,
                                    @RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        QueryWrapper<Major> wrapper = new QueryWrapper<>();
        if (!majorId.equals("") ) {
            wrapper.eq("major_id", majorId);
        } else {
            wrapper.like(!majorName.equals(""), "major_name", majorName);
            if (!collegeName.equals("")) {
                List<College> collegeList = collegeService.getCollegeLikeByName(collegeName);
                if (collegeList.size() > 0) {
                    wrapper.and(i -> {
                        i.like("college_id", collegeList.get(0).getCollegeId());
                        for (int j = 1; j < collegeList.size(); j++) {
                            i.or().like("college_id", collegeList.get(j).getCollegeId());
                        }
                    });
                }
            }
        }
        return majorService.selectWithWrapper(page, size, wrapper);
    }

    /**
     * 添加专业
     */
    @PostMapping("/mainAdmin/add")
    public ResultObj addCollege(@RequestParam(value = "major_name",defaultValue = "") String major_name,
                                @RequestParam(value = "college_name",defaultValue = "") String college_name) {
        if(college_name.equals("")||major_name.equals("")) return ResultObj.PARAM_MISS;
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        wrapper.eq("college_name", college_name);
        College college = collegeService.getOne(wrapper);
        if(college==null) return ResultObj.ADD_ERROR;
        Major major = new Major();
        major.setRequireNum(0);
        major.setCourseNum(0);
        major.setCollegeId(college.getCollegeId());
        major.setMajorName(major_name);
        QueryWrapper<Major> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("college_id", college.getCollegeId());
        int id = Integer.parseInt(majorService.getMaxMajorId(wrapper1)) + 1;
        String newId = String.valueOf(id);
        int i=newId.length();
        while(i<4){
            newId="0"+newId;
            i++;
        }
        major.setMajorId(newId);
        return majorService.save(major) ? ResultObj.ADD_SUCCESS : ResultObj.ADD_ERROR;
    }

    /**
     * 删除专业
     */
    @DeleteMapping("/mainAdmin/delete")
    public ResultObj deleteCollege(@RequestParam(value = "major_id", defaultValue = "") String major_id) {
        if(major_id.equals("")) return ResultObj.PARAM_MISS;
        Major major = majorService.getById(major_id);
        if(major==null||major.getCourseNum()!=0) return ResultObj.DELETE_ERROR;

        return majorService.removeById(major_id)?ResultObj.DELETE_SUCCESS:ResultObj.DELETE_ERROR;
    }

    /**
     * 修改专业
     */
    @PutMapping("/mainAdmin/update")
    public ResultObj updateMajor(
            @RequestParam(value = "major_id",defaultValue = "") String major_id,
            @RequestParam(value = "major_name",defaultValue = "") String major_name) {
        if(major_id.equals("")||major_name.equals("")) return ResultObj.PARAM_MISS;
        Major major = majorService.getById(major_id);
        if(major==null) return ResultObj.PARAM_MISS;

        major.setMajorName(major_name);
        return majorService.saveOrUpdate(major) ? ResultObj.UPDATE_SUCCESS : ResultObj.UPDATE_ERROR;
    }
}
