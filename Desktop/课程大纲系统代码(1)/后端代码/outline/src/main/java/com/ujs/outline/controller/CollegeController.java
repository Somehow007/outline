package com.ujs.outline.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.common.ResultObj;
import com.ujs.outline.domain.College;
import com.ujs.outline.domain.Major;
import com.ujs.outline.domain.User;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.MajorService;
import com.ujs.outline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college")
public class CollegeController {
    @Autowired
    CollegeService collegeService;
    @Autowired
    UserService userService;
    @Autowired
    MajorService majorService;

    @GetMapping("/mainAdmin/countNums")
    public ResultObj countNums(){
        collegeService.countAll();
        return ResultObj.UPDATE_SUCCESS;
    }

    /**
     * 条件、分页查询
     */
    @GetMapping("/")
    public ResultData getAllCollege(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                   @RequestParam(value = "college_id", defaultValue = "") String collegeId,
                                   @RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        if (!collegeId.equals("") ) {
            wrapper.eq("college_id", collegeId);
        } else {
            wrapper.like(!collegeName.equals(""), "college_name", collegeName);
        }
        return collegeService.selectWithWrapper(page, size, wrapper);
    }

    /**
     * 添加学院
     */
    @PostMapping("/mainAdmin/add")
    public ResultObj addCollege(@RequestParam(value = "college_name",defaultValue = "") String college_name) {
        if(college_name.equals("")) return ResultObj.PARAM_MISS;
        College college = new College();
        college.setCourseNum(0);
        college.setUploadNum(0);
        college.setRequireNum(0);
        college.setCollegeName(college_name);
        int id = Integer.parseInt(collegeService.getMaxCollegeId()) + 1;
        String newId=String.valueOf(id);
        college.setCollegeId(newId);
        return collegeService.save(college)?ResultObj.ADD_SUCCESS:ResultObj.ADD_ERROR;
    }

    /**
     * 删除学院
     */
    @DeleteMapping("/mainAdmin/delete")
    public ResultObj deleteCollege(@RequestParam(value = "college_id", defaultValue = "") String college_id) {
        if(college_id.equals("")) return ResultObj.PARAM_MISS;
        College college = collegeService.getById(college_id);
        if(college==null||college.getCourseNum()!=0||college.getUploadNum()!=0) return ResultObj.DELETE_ERROR;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", college.getCollegeId());
        List<User> list = userService.getBaseMapper().selectList(queryWrapper);
        if(list.size()>0) return ResultObj.DELETE_ERROR;

        QueryWrapper<Major> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("college_id", college.getCollegeId());
        List<Major> list1 = majorService.getBaseMapper().selectList(queryWrapper1);
        if(list1.size()>0) return ResultObj.DELETE_ERROR;

        return collegeService.removeById(college.getCollegeId())?ResultObj.DELETE_SUCCESS:ResultObj.DELETE_ERROR;
    }

    /**
     * 修改学院
     */
    @PutMapping("/mainAdmin/update")
    public ResultObj updateCollege(
            @RequestParam(value = "college_id",defaultValue = "") String college_id,
            @RequestParam(value = "college_name", defaultValue = "") String college_name) {
        if(college_id.equals("")||college_name.equals("")) return ResultObj.PARAM_MISS;
        College college = collegeService.getById(college_id);
        if(college==null) return ResultObj.PARAM_MISS;

        college.setCollegeName(college_name);
        return collegeService.saveOrUpdate(college)?ResultObj.UPDATE_SUCCESS:ResultObj.UPDATE_ERROR;
    }
}
