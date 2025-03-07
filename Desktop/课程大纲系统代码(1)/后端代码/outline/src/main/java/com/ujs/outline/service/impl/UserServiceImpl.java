package com.ujs.outline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.controller.UserController;
import com.ujs.outline.domain.User;
import com.ujs.outline.mapper.UserMapper;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService,UserDetailsService {
    @Autowired
    CollegeService collegeService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = getById(s);
        if(user==null)
            throw new UsernameNotFoundException("用户不存在");
        return user;
    }

    /**
     * 构造自定义条件查询
     */
    public ResultData selectWithWrapper(long cur, long size, QueryWrapper<User> wrapper){
        Page<User> page=new Page<>(cur,size);
        Page<User> userPage=getBaseMapper().selectPage(page,wrapper);
        ResultData resultData = new ResultData();
        List<User> records = userPage.getRecords();
        List<UserController.UserReturn> users = new ArrayList<>();
        for (User user : records) {
            UserController.UserReturn userReturn = new UserController.UserReturn();
            userReturn.college=collegeService.getById(user.getCollegeId()).getCollegeName();
            userReturn.userId = user.getUserId();
            userReturn.userState = user.getUserState();
            users.add(userReturn);
        }
        resultData.setData(users);
        resultData.setCur(userPage.getCurrent());
        resultData.setTotal(userPage.getTotal());
        resultData.setSize(userPage.getSize());
        return resultData;
    }
}
