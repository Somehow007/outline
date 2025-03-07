package com.ujs.outline.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *  历史大纲表
 */
@Data
@TableName("old_outline_course")
public class OldOutlineCourse {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer outlineId;
    private Integer courseId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;//更新时间
}
