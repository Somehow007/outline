package com.ujs.outline.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * outline与course关联表
 */
@Data
@TableName("outline_course")
public class OutlineCourse {
    private Integer outlineId;
    @TableId(value = "course_id")
    private Integer courseId;
}
