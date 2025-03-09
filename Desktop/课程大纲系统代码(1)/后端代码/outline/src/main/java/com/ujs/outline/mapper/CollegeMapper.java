package com.ujs.outline.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ujs.outline.domain.College;
import org.apache.ibatis.annotations.Select;

/**
 * Mapper接口
 * @author wjy
 */

public interface CollegeMapper extends BaseMapper<College> {
    @Select("SELECT max(college_id) FROM college")
    String getMaxCollegeId();
}
