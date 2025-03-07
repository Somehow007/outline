package com.ujs.outline.domain.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 课程类别 0-实验实践环节 1-通识教育必修 2-通识教育选修 3-专业方向必修 4-专业方向选修 5-专业基础必修 6-专业基础选修 7-自主研学
 * @author wjy
 */
public enum CourseTypeEnum {
    PRACTICE(0,"实验实践环节"),
    COMMON_COMPULSORY(1,"通识教育必修"),
    COMMON_ELECTIVE(2,"通识教育选修"),
    MAJOR_COMPULSORY(3,"专业方向必修"),
    MAJOR_ELECTIVE(4,"专业方向选修"),
    BASE_COMPULSORY(5,"专业基础必修"),
    BASE_ELECTIVE(6,"专业基础选修"),
    INDEPENDENT_STUDY(7,"自主研学");

    @EnumValue
    private final int code;

    private final String s;

    CourseTypeEnum(int code, String s) {
        this.code=code;this.s=s;
    }

    public static int getCode(String name) {
        for (CourseTypeEnum value : values()) {
            if(value.s.equals(name))
                return value.code;
        }
        return -1;
    }

    public static String getName(int code) {
        for (CourseTypeEnum value : values()) {
            if(value.code==code)
                return value.s;
        }
        return "";
    }

    public static CourseTypeEnum getInstance(String name) {
        for (CourseTypeEnum value : values()) {
            if(value.s.equals(name))
                return value;
        }
        return null;
    }

    public static CourseTypeEnum getInstance(int code) {
        for (CourseTypeEnum value : values()) {
            if(value.code==code)
                return value;
        }
        return null;
    }
    @Override
    public String toString() {
        return s;
    }
}
