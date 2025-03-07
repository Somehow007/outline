package com.ujs.outline.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 简单大纲类
 */
@Data
public class SimpleOutline {
    private Integer outlineId;//大纲编号
    private String fileName;//文件名
    private String enName;//英文名
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;//绑定时间
    private String year;//年份(版本)

    public SimpleOutline() {

    }

    public SimpleOutline(Outline outline) {
        String SfileName = outline.getFileName();
        int index = SfileName.lastIndexOf(".");
        SfileName=SfileName.substring(0,index-36)+SfileName.substring(index);
        outlineId = outline.getOutlineId();
        fileName = SfileName;
        enName = outline.getEnName();
        year = outline.getYear();
    }
}
