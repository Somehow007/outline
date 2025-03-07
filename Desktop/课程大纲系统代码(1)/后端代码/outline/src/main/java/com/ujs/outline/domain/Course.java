package com.ujs.outline.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.outline.domain.enums.CourseCategoryEnum;
import com.ujs.outline.domain.enums.CourseTypeEnum;
import com.ujs.outline.domain.enums.TestTypeEnum;
import lombok.Data;

/**
 * 课程pojo类
 * @author wjy
 */
@Data
@TableName("course")
public class Course {
    @TableId(value = "course_id",type = IdType.AUTO)
    private Integer courseId;//课程编号
    private String code;//课程代码
    private String name;//课程名
    private Double creditSum;//总学分
    private Double hourWeek;//表示总学时(一般课程)或周数(实验实践课程)
    private Integer hourTeach;//讲课学时
    private Integer hourPractice;//实验学时
    private Integer hourOperation;//上机学时
    private Integer hourOutside;//其他学时
    private CourseTypeEnum courseType;//课程类别 0-实验实践环节 1-通识教育必修 2-通识教育选修 3-专业方向必修 4-专业方向选修 5-专业基础必修 6-专业基础选修 7-自主研学
    private CourseCategoryEnum courseCategory;//课程性质 0-必修课 1-选修课 2-校选修课
    private TestTypeEnum testType;//考核方式 0-考核  1-其他
    private Integer term;//建议修读学期
    private String collegeId;//开课学院编号
    private String majorId;//修读专业编号
    private String courseState;//课程状态
    private String year;//年级
    @TableField(exist = false)
    private Integer outlineId;//大纲编号

}
