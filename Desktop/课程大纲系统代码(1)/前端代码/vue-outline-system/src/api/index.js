import request from '../utils/request';

//用户登录
export const loginByAccount = (account,imageCode) =>{
    return request({
        url:'/loginByAccount',
        method:'post',
        data:account,
        headers:{
            'Content-Type':'application/json'
        },
        params:{
            imageCode:imageCode
        }
    });
};
//用户退出
export const logout =() => {
    return request({
        url:'/logout',
        method:'get',
    });
};
//查询用户个人信息
export const getPersonalMessage = () => {
    return request({
        url:'/user/',
        method:'get'
    })
};
//条件查询课程
export const getCourse =(param) => {
    return request({
        url:'/course/',
        method:'get',
        params:param
    });
};
//简单查询课程
export const getSimpleCourse =(code,year,state) => {
    return request({
        url:'/course/user/simpleSearch',
        method:'get',
        params: {
            code:code,
            year:year,
            state:state
        }
    });
};
//根据大纲编号查询课程
export const getCourseByOutlineId =(param) => {
    return request({
        url:'/course/searchByOutlineId',
        method:'get',
        params: param
    });
};
//上传大纲
export const addOutline =(formdata) => {
    return request({
        url:'/outline/user/uploadOutline',
        method:'post',
        data:formdata,
        // 告诉jQuery不要去处理发送的数据，用于对data参数进行序列化处理 这里必须false
        processData : false,
        // 告诉jQuery不要去设置Content-Type请求头
        contentType : false,
    });
};
//上传excel批量添加课程
export const addAllCourse =(formdata) => {
    return request({
        url:'/excel/addCourse',
        method:'post',
        data:formdata,
        // 告诉jQuery不要去处理发送的数据，用于对data参数进行序列化处理 这里必须false
        processData : false,
        // 告诉jQuery不要去设置Content-Type请求头
        contentType : false,
    });
};
//上传excel批量设置课程为待修订大纲
export const setAllCourse =(formdata) => {
    return request({
        url:'/excel/setCourseToFix',
        method:'post',
        data:formdata,
        // 告诉jQuery不要去处理发送的数据，用于对data参数进行序列化处理 这里必须false
        processData : false,
        // 告诉jQuery不要去设置Content-Type请求头
        contentType : false,
    });
};
//重新计数所有学院nums
export const countCollegeNums =() => {
    return request({
        url:'/college/mainAdmin/countNums',
        method:'get',
    });
};
//重新计数所有专业nums
export const countMajorNums =() => {
    return request({
        url:'/major/mainAdmin/countNums',
        method:'get',
    });
};
//条件查询大纲
export const getOutline =(param) => {
    return request({
        url:'/outline/',
        method:'get',
        params:param
    });
};

//删除单个大纲
export const deleteOutline = (outlineId) => {
    return request({
        url:'/outline/mainAdmin/delete',
        method:'delete',
        params:{
            outline_id:outlineId
        }
    });
};
//删除单个课程
export const deleteCourse = (course_id) => {
    return request({
        url:'/course/mainAdmin/delete',
        method:'delete',
        params:{
            course_id:course_id
        }
    });
};
//批量删除大纲
export const deleteAllOutline = (outlineIds) => {
    return request({
        url:'/outline/mainAdmin/deleteAll',
        method:'delete',
        params:{
            outline_ids:outlineIds
        }
    });
};
//批量删除课程
export const deleteAllCourse = (course_ids) => {
    return request({
        url:'/course/mainAdmin/deleteAll',
        method:'delete',
        params:{
            course_ids:course_ids
        }
    });
};
//大纲审核通过
export const passOutline =(outlineId) => {
    return request({
        url:'/outline/admin/passOutline',
        method:'post',
        params: {
            outline_id:outlineId
        }
    });
};
//大纲审核未通过
export const failOutline =(outlineId) => {
    return request({
        url:'/outline/admin/failOutline',
        method:'post',
        params: {
            outline_id:outlineId
        }
    });
};
//条件查询学院
export const getCollege =(param) => {
    return request({
        url:'/college/',
        method:'get',
        params:param
    });
};
//条件查询专业
export const getMajor =(param) => {
    return request({
        url:'/major/',
        method:'get',
        params:param
    });
};
//(添加)绑定大纲与课程的一对多关系
export const addAssignOutline = (param) =>{
    return request({
        url:'/outline/user/addAssignOutline',
        method:'post',
        data:param,
        headers:{
            'Content-Type':'application/json'
        }
    });
};
//查询历史大纲列表
export const getOldOutlines =(course_id) => {
    return request({
        url:'/outline/searchForOldOutline',
        method:'get',
        params:{
            course_id:course_id
        }
    });
};
//修改课程信息
export const updateCourse =(param) => {
    return request({
        url:'/course/mainAdmin/update',
        method:'put',
        params:param
    });
};
//添加课程信息
export const addCourse =(param) => {
    return request({
        url:'/course/mainAdmin/add',
        method:'post',
        params:param
    });
};
//添加学院信息
export const addCollege = (college_name) => {
    return request({
        url:'/college/mainAdmin/add',
        method:'post',
        params:{
            college_name:college_name
        }
    });
};
//添加专业信息
export const addMajor = (major_name,college_name) => {
    return request({
        url:'/major/mainAdmin/add',
        method:'post',
        params:{
            major_name:major_name,
            college_name:college_name
        }
    });
};
//删除学院
export const deleteCollege = (college_id) => {
    return request({
        url:'/college/mainAdmin/delete',
        method:'delete',
        params:{
            college_id:college_id
        }
    });
};
//删除专业
export const deleteMajor = (major_id) => {
    return request({
        url:'/major/mainAdmin/delete',
        method:'delete',
        params:{
            major_id:major_id
        }
    });
};
//修改学院
export const updateCollege = (college_id,college_name) => {
    return request({
        url:'/college/mainAdmin/update',
        method:'put',
        params:{
            college_id:college_id,
            college_name:college_name
        }
    });
};
//修改专业
export const updateMajor = (major_id,major_name) => {
    return request({
        url:'/major/mainAdmin/update',
        method:'put',
        params:{
            major_id:major_id,
            major_name:major_name
        }
    });
};
//条件查询用户
export const getUser =(param) => {
    return request({
        url:'/user/mainAdmin/getUser',
        method:'get',
        params:param
    });
};
//重置个人密码
export const resetPassword =(password) => {
    return request({
        url:'/user/user/resetPassword',
        method:'put',
        params:{
            password:password
        }
    });
};
//重置密码与用户名相同
export const resetPasswordByUserId =() => {
    return request({
        url:'/user/user/resetPasswordByUserId',
        method:'put'
    });
};
//生成八位随机密码
export const resetPasswordByRandom =() => {
    return request({
        url:'/user/user/resetPasswordByRandom',
        method:'put'
    });
};
//添加用户
export const addUser =(param) => {
    return request({
        url:'/user/mainAdmin/add',
        method:'post',
        params:param
    });
};
//删除用户
export const deleteUser = (user_id) => {
    return request({
        url:'/user/mainAdmin/delete',
        method:'delete',
        params:{
            user_id:user_id
        }
    });
};
//修改用户
export const updateUser = (user_id,user_state,user_password,college_name) => {
    return request({
        url:'/user/mainAdmin/update',
        method:'put',
        params:{
            user_id:user_id,
            user_state:user_state,
            user_password:user_password,
            college_name:college_name
        }
    });
};
//重置密码与用户名相同
export const resetPasswordByUserIdADMIN =(user_id) => {
    return request({
        url:'/user/mainAdmin/resetPasswordByUserId',
        method:'put',
        params:{
            user_id:user_id
        }
    });
};
//生成八位随机密码
export const resetPasswordByRandomADMIN =(user_id) => {
    return request({
        url:'/user/mainAdmin/resetPasswordByRandom',
        method:'put',
        params:{
            user_id:user_id
        }
    });
};