package com.ujs.outline.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 课程大纲pojo类
 * @author wjy
 */
@Data
@TableName("outline")
public class Outline {
    @TableId(value = "outline_id",type = IdType.AUTO)
    private Integer outlineId;//大纲编号
    private String fileName;//文件名
    private String enName;//英文名
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;//更新时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;//审核时间
    private String collegeId;//上传学院
    private Integer connectNum;//关联课程数量
    private String outlineState;//状态
    private String year;//年份(版本)

}
