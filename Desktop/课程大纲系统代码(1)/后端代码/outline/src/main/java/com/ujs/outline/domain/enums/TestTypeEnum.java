package com.ujs.outline.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 考核方式 0-考核  1-其他
 * @author wjy
 */
public enum TestTypeEnum {
    EXAM(0,"考核"),
    OTHER(1,"其他");

    @EnumValue
    private final int code;

    private final String s;

    TestTypeEnum(int code,String s) {
        this.code=code;this.s=s;
    }

    public static int getCode(String name) {
        for (TestTypeEnum value : values()) {
            if(value.s.equals(name))
                return value.code;
        }
        return -1;
    }

    public static String getName(int code) {
        for (TestTypeEnum value : values()) {
            if(value.code==code)
                return value.s;
        }
        return "";
    }

    public static TestTypeEnum getInstance(String name) {
        for (TestTypeEnum value : values()) {
            if(value.s.equals(name))
                return value;
        }
        return null;
    }
    public static TestTypeEnum getInstance(int code) {
        for (TestTypeEnum value : values()) {
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
