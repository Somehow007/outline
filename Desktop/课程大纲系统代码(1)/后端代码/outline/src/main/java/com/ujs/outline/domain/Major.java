package com.ujs.outline.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 专业pojo类
 * @author wjy
 */
@Data
@TableName("major")
public class Major {
    @TableId("major_id")
    private String majorId;//专业编号
    private String collegeId;//学院编号
    private String majorName;//专业名
    private Integer courseNum;//课程总数
    private Integer requireNum;//待修订大纲课程数

}
