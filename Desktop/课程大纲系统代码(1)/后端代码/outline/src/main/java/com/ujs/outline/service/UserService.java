package com.ujs.outline.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.domain.User;

public interface UserService extends IService<User> {
    ResultData selectWithWrapper(long cur, long size, QueryWrapper<User> wrapper);
}
