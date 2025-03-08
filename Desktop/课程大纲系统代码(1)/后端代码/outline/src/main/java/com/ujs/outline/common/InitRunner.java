package com.ujs.outline.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.outline.domain.College;
import com.ujs.outline.domain.User;
import com.ujs.outline.service.CollegeService;
import com.ujs.outline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 启动时的系统任务
 * 用于在User表为空时导入基本账户
 *
 * @author wjy
 */
@Component
public class InitRunner implements CommandLineRunner {
    @Autowired
    UserService userService;
    @Autowired
    CollegeService collegeService;

    @Override
    public void run(String... args) throws Exception {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", "12");
        queryWrapper.eq("user_state", "20");
        if (userService.getOne(queryWrapper) == null) {
            addMainAdmin();
//            addAllUser();
        }
    }

    private void addMainAdmin() {
        User user=new User();
        user.setUserId("mainAdmin");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setUserPassword(encoder.encode("mainAdmin"));
        user.setCollegeId("12");
        user.setUserState("20");
        userService.save(user);

        User user2=new User();
        user2.setUserId("admin");
        user2.setUserPassword(encoder.encode("admin"));
        user2.setCollegeId("10");
        user2.setUserState("21");
        userService.save(user2);

        User user3=new User();
        user3.setUserId("user");
        user3.setUserPassword(encoder.encode("user"));
        user3.setCollegeId("10");
        user3.setUserState("22");
        userService.save(user3);
    }

    //为所有学院生成一个管理员用户与教师用户，并以文件形式保存
    private void addAllUser() throws IOException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        List<College> list = collegeService.getBaseMapper().selectList(new QueryWrapper<>());
        File file=new File(System.getProperty("user.dir") + File.separator +"password.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file);
        writer.write("学院             用户名             密码             权限");
        writer.write(System.getProperty("line.separator"));
        for (College college : list) {
                if(college.getCollegeId().equals("12")){
                    User user=new User();
                    user.setUserId("mainAdmin"+college.getCollegeId());
                    String psw=passRandom();
                    user.setUserPassword(encoder.encode(psw));
                    user.setCollegeId("12");
                    user.setUserState("20");
                    userService.save(user);
                    StringBuilder[] stringBuilders=new StringBuilder[4];
                    stringBuilders[0]=new StringBuilder(college.getCollegeName());
                    stringBuilders[1]=new StringBuilder(user.getUserId());
                    stringBuilders[2]=new StringBuilder(psw);
                    stringBuilders[3]=new StringBuilder("系统管理员");
                    for (StringBuilder stringBuilder : stringBuilders) {
                        for(int i=stringBuilder.length();i<15;i++){
                            stringBuilder.append(" ");
                        }
                        writer.write(stringBuilder.toString());
                    }
                    writer.write(System.getProperty("line.separator"));

                    User user2=new User();
                    user2.setUserId("user"+college.getCollegeId());
                    psw=passRandom();
                    user2.setUserPassword(encoder.encode(psw));
                    user2.setCollegeId("12");
                    user2.setUserState("22");
                    userService.save(user2);
                    stringBuilders[0]=new StringBuilder(college.getCollegeName());
                    stringBuilders[1]=new StringBuilder(user2.getUserId());
                    stringBuilders[2]=new StringBuilder(psw);
                    stringBuilders[3]=new StringBuilder("教师用户");
                    for (StringBuilder stringBuilder : stringBuilders) {
                        for(int i=stringBuilder.length();i<15;i++){
                            stringBuilder.append(" ");
                        }
                        writer.write(stringBuilder.toString());
                    }
                    writer.write(System.getProperty("line.separator"));
                }else{
                    User user=new User();
                    user.setUserId("admin"+college.getCollegeId());
                    String psw=passRandom();
                    user.setUserPassword(encoder.encode(psw));
                    user.setCollegeId(college.getCollegeId());
                    user.setUserState("21");
                    userService.save(user);
                    StringBuilder[] stringBuilders=new StringBuilder[4];
                    stringBuilders[0]=new StringBuilder(college.getCollegeName());
                    stringBuilders[1]=new StringBuilder(user.getUserId());
                    stringBuilders[2]=new StringBuilder(psw);
                    stringBuilders[3]=new StringBuilder("学院管理员");
                    for (StringBuilder stringBuilder : stringBuilders) {
                        for(int i=stringBuilder.length();i<15;i++){
                            stringBuilder.append(" ");
                        }
                        writer.write(stringBuilder.toString());
                    }
                    writer.write(System.getProperty("line.separator"));

                    User user2=new User();
                    user2.setUserId("user"+college.getCollegeId());
                    psw=passRandom();
                    user2.setUserPassword(encoder.encode(psw));
                    user2.setCollegeId(college.getCollegeId());
                    user2.setUserState("22");
                    userService.save(user2);
                    stringBuilders[0]=new StringBuilder(college.getCollegeName());
                    stringBuilders[1]=new StringBuilder(user2.getUserId());
                    stringBuilders[2]=new StringBuilder(psw);
                    stringBuilders[3]=new StringBuilder("教师用户");
                    for (StringBuilder stringBuilder : stringBuilders) {
                        for(int i=stringBuilder.length();i<15;i++){
                            stringBuilder.append(" ");
                        }
                        writer.write(stringBuilder.toString());
                    }
                    writer.write(System.getProperty("line.separator"));
                }
        }
        writer.flush();
    }

    public static String passRandom() {
        StringBuilder textString=new StringBuilder();
        String reference="QWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyuiopasdfghjklzxcvbnm!@#$%^&*()";
        StringBuilder buffer=new StringBuilder(reference);
        for (int i = 0; i < 8; i++) {
            int ran=(int)(Math.random()*72);
            //buffer.charAt()是索引该位置的字符
            textString.append(buffer.charAt(ran));
        }
        return textString.toString();
    }
}
