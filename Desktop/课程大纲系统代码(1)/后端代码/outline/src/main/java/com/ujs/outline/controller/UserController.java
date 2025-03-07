package com.ujs.outline.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.outline.common.Constast;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.common.ResultObj;
import com.ujs.outline.domain.College;
import com.ujs.outline.domain.Major;
import com.ujs.outline.domain.User;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户基本接口
 * @author wjy
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    CollegeService collegeService;

    static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/")
    public ResultData getPersonalMessage() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            UserReturn userReturn=new UserReturn();
            userReturn.userId=user.getUserId();
            userReturn.userState=user.getUserState();
            College college = collegeService.getById(user.getCollegeId());
            if (college != null) {
                userReturn.college=college.getCollegeName();
            }
            long i=1;
            return new ResultData(i,i,i,userReturn);
        }
        return new ResultData();
    }

    /**
     * 条件、分页查询
     */
    @GetMapping("/mainAdmin/getUser")
    public ResultData getAllUser(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                    @RequestParam(value = "user_id", defaultValue = "") String userId,
                                    @RequestParam(value = "user_state", defaultValue = "") String userState,
                                    @RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(!userId.equals(""),"user_id",userId);
        wrapper.like(!userState.equals(""),"user_state",userState);
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

        return userService.selectWithWrapper(page, size, wrapper);
    }

    /**
     * 重置密码
     */
    @PutMapping("/user/resetPassword")
    public ResultObj resetPassword(@RequestParam(value = "password" ,defaultValue = "") String password) {
        if(password.equals("")) return ResultObj.PARAM_MISS;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setUserPassword(encoder.encode(password));
        return userService.saveOrUpdate(user)?ResultObj.UPDATE_SUCCESS:ResultObj.UPDATE_ERROR;
    }
    /**
     * 重置密码与用户名相同
     */
    @PutMapping("/user/resetPasswordByUserId")
    public ResultObj resetPasswordByUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user==null)  return ResultObj.UPDATE_ERROR;
        user.setUserPassword(encoder.encode(user.getUserId()));
        return userService.saveOrUpdate(user)?ResultObj.UPDATE_SUCCESS:ResultObj.UPDATE_ERROR;
    }

    /**
     * 生成随机八位密码
     */
    @PutMapping("/user/resetPasswordByRandom")
    public ResultObj resetPasswordByRandom() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user==null)  return ResultObj.UPDATE_ERROR;
        String newPassword=passRandom();
        user.setUserPassword(encoder.encode(newPassword));
        ResultObj resultObj = new ResultObj();
        resultObj.setCode(Constast.OK);
        resultObj.setMsg(newPassword);
        return userService.saveOrUpdate(user)?resultObj:ResultObj.UPDATE_ERROR;
    }


    /**
     * 添加用户
     */
    @PostMapping("/mainAdmin/add")
    public ResultObj addUser(@RequestParam(value = "user_id", defaultValue = "") String userId,
                                @RequestParam(value = "user_state", defaultValue = "") String userState,
                                @RequestParam(value = "user_password", defaultValue = "") String userPassword,
                                @RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        if(userId.equals("")||userState.equals("")
                ||collegeName.equals("")||userPassword.equals("")) return ResultObj.PARAM_MISS;
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        wrapper.eq("college_name", collegeName);
        College college = collegeService.getOne(wrapper);
        if(college==null) return ResultObj.ADD_ERROR;
        if(!(userState.equals("21")||userState.equals("22"))) return ResultObj.ADD_ERROR;
        User user = new User();
        user.setUserId(userId);
        user.setUserPassword(encoder.encode(userPassword));
        user.setCollegeId(college.getCollegeId());
        user.setUserState(userState);

        return userService.save(user) ? ResultObj.ADD_SUCCESS : ResultObj.ADD_ERROR;
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/mainAdmin/delete")
    public ResultObj deleteUser(@RequestParam(value = "user_id", defaultValue = "") String userId) {
        if(userId.equals("")) return ResultObj.PARAM_MISS;
        User user = userService.getById(userId);
        if(user.getUserState().equals("20"))    return ResultObj.DELETE_ERROR;

        return userService.removeById(userId)?ResultObj.DELETE_SUCCESS:ResultObj.DELETE_ERROR;
    }

    /**
     * 修改用户
     */
    @PutMapping("/mainAdmin/update")
    public ResultObj updateUser(@RequestParam(value = "user_id", defaultValue = "") String userId,
                                @RequestParam(value = "user_state", defaultValue = "") String userState,
                                @RequestParam(value = "user_password", defaultValue = "") String userPassword,
                                @RequestParam(value = "college_name", defaultValue = "") String collegeName
                                ) {
        if(userId.equals("")||userState.equals("") ||collegeName.equals("")) return ResultObj.PARAM_MISS;
        User user = userService.getById(userId);
        if(user==null)  return ResultObj.UPDATE_ERROR;
        if (!userPassword.equals("")) {
            if(userPassword.length()<8) return ResultObj.UPDATE_ERROR;
            user.setUserPassword(encoder.encode(userPassword));
        }
        if(!(userState.equals("21")||userState.equals("22")))   return ResultObj.UPDATE_ERROR;
        user.setUserState(userState);
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        wrapper.eq("college_name", collegeName);
        College college = collegeService.getOne(wrapper);
        if(college==null) return ResultObj.ADD_ERROR;
        user.setCollegeId(college.getCollegeId());
        return userService.saveOrUpdate(user)?ResultObj.UPDATE_SUCCESS:ResultObj.UPDATE_ERROR;
    }

    /**
     * 重置密码与用户名相同
     */
    @PutMapping("/mainAdmin/resetPasswordByUserId")
    public ResultObj resetPasswordByUserId(@RequestParam(value = "user_id" ,defaultValue = "") String userId) {
        if(userId.equals("")) return ResultObj.PARAM_MISS;
        User user = userService.getById(userId);
        if(user==null)  return ResultObj.UPDATE_ERROR;
        user.setUserPassword(encoder.encode(user.getUserId()));
        return userService.saveOrUpdate(user)?ResultObj.UPDATE_SUCCESS:ResultObj.UPDATE_ERROR;
    }

    /**
     * 生成随机八位密码
     */
    @PutMapping("/mainAdmin/resetPasswordByRandom")
    public ResultObj resetPasswordByRandom(@RequestParam(value = "user_id" ,defaultValue = "") String userId) {
        if(userId.equals("")) return ResultObj.PARAM_MISS;
        User user = userService.getById(userId);
        if(user==null)  return ResultObj.UPDATE_ERROR;
        String newPassword=passRandom();
        user.setUserPassword(encoder.encode(newPassword));
        ResultObj resultObj = new ResultObj();
        resultObj.setCode(Constast.OK);
        resultObj.setMsg(newPassword);
        return userService.saveOrUpdate(user)?resultObj:ResultObj.UPDATE_ERROR;
    }

    public static String passRandom() {
        StringBuilder textString=new StringBuilder();
        String reference="QWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyuiopasdfghjklzxcvbnm!@#$%^&*()";
        StringBuilder buffer=new StringBuilder(reference);
        for (int i = 0; i < 8; i++) {
            int ran=(int)(Math.random()*72);
            //buffer.charAt()是索引该位置的字符
            textString.append(buffer.charAt(ran));
        }
        return textString.toString();
    }

    public static class UserReturn{
        public String userId;//用户ID
        public String college;//学院编号
        public String userState;//用户权限
    }
}
