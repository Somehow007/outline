package com.ujs.outline.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ujs.outline.domain.Major;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Mapper接口
 * @author wjy
 */
public interface MajorMapper extends BaseMapper<Major> {
    @Select("SELECT MAX(major_id) FROM major ${ew.customSqlSegment}")
    String getMaxMajorId(@Param(Constants.WRAPPER) QueryWrapper<Major> wrapper);
}
