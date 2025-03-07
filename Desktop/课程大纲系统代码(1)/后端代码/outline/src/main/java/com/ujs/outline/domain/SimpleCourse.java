package com.ujs.outline.domain;


import lombok.Data;

/**
 * 简单课程pojo
 */
@Data
public class SimpleCourse {
    private Integer courseId;//课程编号
    private String name;//课程名
    private String majorId;//修读专业编号
    private String courseState;//课程状态
    private Integer outlineId;//大纲编号

    public SimpleCourse() {
    }

    public SimpleCourse(Course course) {
        courseId = course.getCourseId();
        name = course.getName();
        majorId = course.getMajorId();
        courseState = course.getCourseState();
        outlineId = course.getOutlineId();
    }
}
