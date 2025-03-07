package com.ujs.outline.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学院pojo类
 * @author wjy
 */
@Data
@TableName("college")
public class College {
    @TableId(value = "college_id")
    private String collegeId;//学院编号
    private String collegeName;//学院名
    private Integer courseNum;//课程总数
    private Integer requireNum;//待修订大纲课程数
    private Integer uploadNum;//已上传大纲数

}
