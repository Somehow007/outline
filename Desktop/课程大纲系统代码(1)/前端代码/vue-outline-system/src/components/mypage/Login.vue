<template>
  <div class="login-wrap">
    <div class="ms-login">
      <div class="ms-title">课程教学大纲管理系统</div>
      <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
        <el-form-item prop="username">
          <el-input v-model="param.user_id" placeholder="请输入用户名">
            <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              type="password"
              placeholder="请输入密码"
              v-model="param.user_password"
              @keyup.enter.native="submitForm()"
          >
            <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="16">
            <el-form-item prop="imageCode">
              <el-input v-model="code" placeholder="请输入验证码">
                <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="imageCode">
              <img :src="codeUrl" @click="changeCode()" alt="加载中"
                id="verifyCode" width="95" height="32"/>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="login-btn">
          <el-button type="primary" @click="submitForm()">登录</el-button>
        </div>
        <div class="login-btn">
          <el-button type="primary" @click="touristLogin()">游客登录</el-button>
        </div>
<!--        <p class="login-tips">Tips : </p>-->
<!--        <p class="login-tips">系统管理员帐号密码mainAdmin </p>-->
<!--        <p class="login-tips">学院管理员帐号密码admin </p>-->
<!--        <p class="login-tips">教师帐号密码user </p>-->
      </el-form>
    </div>
  </div>
</template>

<script>
import {loginByAccount} from "@/api";

export default {
  data: function() {
    return {
      codeUrl:this.$baseUrl+'/code/image',
      failObj:{
        msg:''
      },
      code:'',
      param: {
        user_id: '',
        user_password: '',
      },
      rules: {
        user_id: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        user_password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      },
    };
  },
  methods: {
    submitForm() {
      this.$refs.login.validate(valid => {
        if (valid) {
          loginByAccount(JSON.stringify(this.param),this.code)
              .then(res => {
                this.$message.success('登录成功');
                this.$router.push('/');
              },error => {
                let msg=localStorage.getItem("errorMsg");
                this.$message.error(msg);
                this.changeCode();
              })
          return true;
        } else {
          this.$message.error('请输入账号和密码');
          return false;
        }
      });
    },
    touristLogin(){
      this.$message.success('游客登录');
      this.$router.push('/');
    },
    changeCode(){
      let num = Math.ceil(Math.random() * 10); // 生成一个随机数（防止缓存）
      this.codeUrl = this.$baseUrl+'/code/image?' + num;
    }
  },
};
</script>

<style scoped>
.login-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  background-image: url(../../assets/img/login-bg.jpg);
  background-size: 100%;
}
.ms-title {
  width: 100%;
  line-height: 50px;
  text-align: center;
  font-size: 20px;
  color: #fff;
  border-bottom: 1px solid #ddd;
}
.ms-login {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 350px;
  margin: -190px 0 0 -175px;
  border-radius: 5px;
  background: rgba(255, 255, 255, 0.3);
  overflow: hidden;
}
.ms-content {
  padding: 30px 30px;
}
.login-btn {
  text-align: center;
}
.login-btn button {
  width: 100%;
  height: 36px;
  margin-bottom: 10px;
}
.login-tips {
  font-size: 12px;
  line-height: 30px;
  color: #fff;
}
</style>