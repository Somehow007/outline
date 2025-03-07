package com.ujs.outline.common;

import lombok.Data;

/**
 *  返回消息对象
 * @author wjy
 */
@Data
public class ResultObj {
    public static final ResultObj  LOGIN_SUCCESS=new ResultObj(Constast.OK, "登陆成功");
    public static final ResultObj  LOGIN_ERROR_PASS=new ResultObj(Constast.ERROR, "登陆失败,用户名或密码不正确");
    public static final ResultObj  LOGIN_ERROR_CODE=new ResultObj(Constast.ERROR, "登陆失败,验证码不正确");

    public static final ResultObj  UNAUTHORIZED_ERROR=new ResultObj(Constast.UNAUTHORIZED, "权限验证失败");

    public static final ResultObj  UPDATE_SUCCESS=new ResultObj(Constast.OK, "更新成功");
    public static final ResultObj  UPDATE_ERROR=new ResultObj(Constast.ERROR, "更新失败");

    public static final ResultObj  ADD_SUCCESS=new ResultObj(Constast.OK, "添加成功");
    public static final ResultObj  ADD_ERROR=new ResultObj(Constast.ERROR, "添加失败");

    public static final ResultObj  OPERATE_SUCCESS=new ResultObj(Constast.OK, "操作成功");
    public static final ResultObj  OPERATE_ERROR=new ResultObj(Constast.ERROR, "操作失败");

    public static final ResultObj  DELETE_SUCCESS=new ResultObj(Constast.OK, "删除成功");
    public static final ResultObj  DELETE_ERROR=new ResultObj(Constast.ERROR, "删除失败");

    public static final ResultObj  RESET_SUCCESS=new ResultObj(Constast.OK, "重置成功");
    public static final ResultObj  RESET_ERROR=new ResultObj(Constast.ERROR, "重置失败");

    public static final ResultObj  DISPATCH_SUCCESS=new ResultObj(Constast.OK, "分配成功");
    public static final ResultObj  DISPATCH_ERROR=new ResultObj(Constast.ERROR, "分配失败");

    public static final ResultObj  UPLOAD_ERROR_TYPE=new ResultObj(Constast.ERROR, "上传文件格式错误");
    public static final ResultObj  UPLOAD_MISS=new ResultObj(Constast.ERROR, "上传文件丢失");
    public static final ResultObj  PARAM_MISS=new ResultObj(Constast.ERROR, "参数丢失");

    private Integer code;
    private String msg;

    public ResultObj() {
    }


    public ResultObj(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
