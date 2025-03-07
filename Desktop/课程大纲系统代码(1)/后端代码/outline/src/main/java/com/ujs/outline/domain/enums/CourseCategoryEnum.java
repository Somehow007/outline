package com.ujs.outline.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 课程性质 0-必修课 1-选修课 2-校选修课
 */
public enum CourseCategoryEnum {
    COMPULSORY(0,"必修课"),
    ELECTIVE(1,"选修课"),
    PUBLIC_ELECTIVE(2,"校选修课");

    @EnumValue
    private final int code;

    private final String s;

    CourseCategoryEnum(int code, String s) {
        this.code=code;this.s=s;
    }

    public static int getCode(String name) {
        for (CourseCategoryEnum value : values()) {
            if(value.s.equals(name))
                return value.code;
        }
        return -1;
    }

    public static String getName(int code) {
        for (CourseCategoryEnum value : values()) {
            if(value.code==code)
                return value.s;
        }
        return "";
    }

    public static CourseCategoryEnum getInstance(String name) {
        for (CourseCategoryEnum value : values()) {
            if(value.s.equals(name))
                return value;
        }
        return null;
    }

    public static CourseCategoryEnum getInstance(int code) {
        for (CourseCategoryEnum value : values()) {
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
