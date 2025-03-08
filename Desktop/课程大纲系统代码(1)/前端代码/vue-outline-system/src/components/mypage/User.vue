<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>个人信息</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <div class="form-box">
        <el-form ref="form" label-width="80px">
          <el-form-item label="用户名:">
            <el-tag
                :type="'success'"
            >{{userId}}</el-tag>
          </el-form-item>
          <el-form-item label="学院名:">
            <el-tag
                :type="'success'"
            >{{myCollege}}</el-tag>
          </el-form-item>
          <el-form-item label="权限:">
            <div>
              <el-tag
                  v-if="userState==='20'"
                  :type="'warning'">系统管理员</el-tag>
            </div>
            <div>
              <el-tag
                  v-if="userState==='21'"
                  :type="'success'">学院管理员</el-tag>
            </div>
            <div>
              <el-tag
                  v-if="userState==='22'"
              >教师</el-tag>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-lx-lock" @click="passwordVisible = true">密码修改</el-button>
          </el-form-item>

        </el-form>
      </div>

      <!-- 密码修改弹出框 -->
      <el-dialog title="密码修改" :visible.sync="passwordVisible" width="40%">
        <div class="handle-box">
          <div class="float">
            <div class="el-upload__tip" slot="tip">
              手动输入密码
              <el-switch
                  v-model="passwordSwitch">
              </el-switch>
            </div>
          </div>
        </div>

        <div class="handle-box">
          <div v-if="!passwordSwitch">
            <el-button @click="resetMyPasswordByUserId">重置密码与用户名相同</el-button>
            <el-button @click="resetMyPasswordByRandom">重置密码为八位随机字符</el-button>
          </div>
          <el-form ref="form" :model="passwordParam" label-width="90px" v-if="passwordSwitch">
            <el-form-item label="新密码" >
              <el-input v-model="passwordParam.password1" placeholder="请输入新的密码" class="handle-input2 mr10"></el-input>
            </el-form-item>
            <el-form-item label="确认新密码">
              <el-input v-model="passwordParam.password2" placeholder="重新输入新的密码" class="handle-input2 mr10"></el-input>
            </el-form-item>
          </el-form>
        </div>

        <span slot="footer" class="dialog-footer">
                <el-button @click="passwordVisible = false">取 消</el-button>
                <el-button v-if="passwordSwitch" type="primary" @click="resetMyPassword">确 定</el-button>
            </span>
      </el-dialog>
    </div>

    <div class="crumbs" v-if="userState==='20'">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-lx-cascades"></i> 用户列表
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container" v-if="userState==='20'">
      <div class="handle-box">
        <el-input v-model="query.user_id" placeholder="用户名" class="handle-input mr10"></el-input>
        <el-input v-model="query.college_name" placeholder="学院名" class="handle-input mr10"></el-input>
        <el-select clearable v-model="query.user_state" placeholder="用户权限" class="handle-select-m mr10">
          <el-option
              v-for="item in stateOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        <el-button type="primary" icon="el-icon-lx-roundclose" @click="handleReset">重置</el-button>
      </div>

      <div class="handle-box">
        <el-button
            v-if="optionSwitch"
            type="primary"
            icon="el-icon-lx-friendadd"
            class="handle-del mr10"
            @click="handleAdd"
        >添加用户</el-button>

        <div class="float"  v-if="userState==='20'">
          <div class="el-upload__tip" slot="tip">
            操作栏显示
            <el-switch
                v-model="optionSwitch">
            </el-switch>
          </div>
        </div>
      </div>

      <!-- 编辑弹出框 -->
      <el-dialog title="编辑用户" :visible.sync="editVisible" width="30%">
        <el-form ref="form" :model="editParam" label-width="70px">
          <el-form-item label="用户名">
            <el-input v-model="editParam.user_id" placeholder="请输入用户名" class="handle-input2 mr10" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item>
            <div class="float">
              <div class="el-upload__tip" slot="tip">
                手动输入密码
                <el-switch
                    v-model="passwordSwitch">
                </el-switch>
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <div v-if="!passwordSwitch">
              <el-button @click="resetPasswordByUserId2">重置密码与用户名相同</el-button>
              <el-button @click="resetPasswordByRandom2">重置密码为八位随机字符</el-button>
            </div>
          </el-form-item>
          <div v-if="passwordSwitch">
            <el-form-item label="密码" >
              <el-input v-model="editParam.user_password" placeholder="请输入密码" class="handle-input2 mr10"></el-input>
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="editParam.user_password2" placeholder="重新输入密码" class="handle-input2 mr10"></el-input>
            </el-form-item>
          </div>

          <el-form-item label="学院名">
            <el-input v-model="editParam.college_name" placeholder="请输入学院名" class="handle-input2 mr10"></el-input>
          </el-form-item>
          <el-form-item label="用户权限">
            <el-select clearable v-model="editParam.user_state" placeholder="用户权限" class="handle-select mr10">
              <el-option
                  v-for="item in stateOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <span slot="footer" class="dialog-footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit">确 定</el-button>
            </span>
      </el-dialog>

      <!-- 添加弹出框 -->
      <el-dialog title="添加用户" :visible.sync="addVisible" width="30%">
        <el-form ref="form" :model="addParam" label-width="70px">
          <el-form-item label="用户名">
            <el-input v-model="addParam.user_id" placeholder="请输入用户名" class="handle-input2 mr10"></el-input>
          </el-form-item>
          <el-form-item label="密码" >
            <el-input v-model="addParam.user_password" placeholder="请输入密码" class="handle-input2 mr10"></el-input>
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input v-model="addParam.user_password2" placeholder="重新输入密码" class="handle-input2 mr10"></el-input>
          </el-form-item>
          <el-form-item label="学院名">
            <el-input v-model="addParam.college_name" placeholder="请输入学院名" class="handle-input2 mr10"></el-input>
          </el-form-item>
          <el-form-item label="用户权限">
            <el-select clearable v-model="addParam.user_state" placeholder="用户权限" class="handle-select mr10">
              <el-option
                  v-for="item in stateOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="addUserSubmit">确 定</el-button>
            </span>
      </el-dialog>

      <el-table
          :data="tableData"
          border
          class="table"
          ref="multipleTable"
          header-cell-class-name="table-header"
      >

        <el-table-column prop="userId" label="用户名" min-width="20%" align="center"></el-table-column>
        <el-table-column prop="college" label="学院名" min-width="25%" align="center"></el-table-column>
        <el-table-column prop="userState" label="用户状态" min-width="20%" align="center" v-if="false"></el-table-column>
        <el-table-column prop="state" label="用户权限" min-width="15%" align="center" fixed="right" key="11">
          <template slot-scope="scope">
            <div>
              <el-tag
                  v-if="scope.row.userState==='20'"
                  :type="'warning'">系统管理员</el-tag>
            </div>
            <div>
              <el-tag
                  v-if="scope.row.userState==='21'"
                  :type="'success'">学院管理员</el-tag>
            </div>
            <div>
              <el-tag
                  v-if="scope.row.userState==='22'"
                  >教师</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="20%" align="center" fixed="right"
                         v-if="optionSwitch">
          <template slot-scope="scope">
            <el-button
                v-if="userState==='20'"
                type="text"
                icon="el-icon-edit"
                @click="handleEdit(scope.$index, scope.row)"
            >编辑
            </el-button>
            <el-button
                v-if="userState==='20'"
                type="text"
                icon="el-icon-delete"
                class="red"
                @click="handleDelete(scope.$index, scope.row)"
            >删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
            background
            layout="total, prev, pager, next"
            :current-page="query.page"
            :page-size="query.size"
            :total="pageTotal"
            @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import {
  addUser,
  deleteCollege,
  deleteUser,
  getPersonalMessage,
  getUser,
  resetPassword,
  resetPasswordByRandom,
  resetPasswordByRandomADMIN,
  resetPasswordByUserId,
  resetPasswordByUserIdADMIN,
  updateCollege, updateUser
} from '../../api/index';
import bus from "@/components/common/bus";

export default {
  name: 'user',
  data() {
    return {
      userId: "",
      userState: "",
      myCollege: "",
      optionSwitch: false,
      passwordSwitch: false,
      passwordSwitch2: false,
      editVisible: false,
      passwordVisible: false,
      addVisible: false,
      stateOptions: [{value: '20', label: '系统管理员'}, {value: '21', label: '学院管理员'}, {value: '22', label: '教师'}],
      passwordParam:{
        password1:"",
        password2:""
      },
      query: {
        page: 1,
        size: 10,
        user_id: "",
        user_state: "",
        college_name: ""
      },
      editParam: {
        user_id: "",
        user_password: "",
        user_password2: "",
        user_state: "",
        college_name: ""
      },
      addParam: {
        user_id: "",
        user_password: "",
        user_password2: "",
        user_state: "",
        college_name: ""
      },
      tableData: [],
      pageTotal: 0,
      form: {},
      idx: -1,
      id: -1
    };
  },
  created() {

    this.userState = "";
    this.userId = "";
    this.myCollege = "";
    getPersonalMessage().then(res => {
      if (res.status === 200) {
        this.userReturn=res.data.data;
        this.userId=this.userReturn.userId;
        this.userState=this.userReturn.userState;
        this.myCollege=this.userReturn.college;
        if (this.userState === "20") {
          this.getData();
        }
      }
    });
  },
  methods: {

    getData() {
      getUser(this.query).then(
          res => {
            this.resultObj = res.data;
            this.tableData = this.resultObj.data;
            this.pageTotal = this.resultObj.total;
          }
      );
    },
    handleReset() {
      this.query.page = 1;
      this.query.size = 10;
      this.query.user_id = "";
      this.query.user_state = "";
      this.query.college_name = "";
      this.getData();
    },
    // 触发搜索按钮
    handleSearch() {
      this.$set(this.query, 'page', 1);
      this.getData();
    },
    // 分页导航
    handlePageChange(val) {
      this.$set(this.query, 'page', val);
      this.getData();
    },
    //编辑操作
    handleEdit(index, row){
      this.passwordSwitch2=false;
      this.editParam.user_id=this.tableData[index].userId;
      this.editParam.college_name=this.tableData[index].college;
      this.editParam.user_state = this.tableData[index].userState;
      this.editVisible=true;
    },
    //保存编辑
    saveEdit() {
      this.editVisible = false;
      if (this.passwordSwitch2 === false) {
        this.editParam.user_password="";
      }else{
        if (this.editParam.user_password !== this.editParam.user_password2) {
          this.$message.error("两次输入密码不一致");
          return;
        }
        if (this.editParam.user_password.length < 8) {
          this.$message.error("密码至少为八位字符");
          return;
        }
      }
      updateUser(this.editParam.user_id,this.editParam.user_state,this.editParam.user_password,this.editParam.college_name).then(
          res => {
            if(res.data.code===-1){
              this.$message.error('修改失败');
            }else{
              this.$message.success('修改成功');
              this.getData();
            }
          }, error => {
            this.$message.error("修改失败");
          }
      );
    },
    // 删除操作
    handleDelete(index, row) {
      // 二次确认删除
      this.$confirm('确定要删除吗？', '提示', {
        type: 'warning'
      })
          .then(() => {
            deleteUser(this.tableData[index].userId).then(
                res => {
                  if(res.data.code===-1){
                    this.$message.error('删除失败');
                  }else{
                    this.$message.success('删除成功');
                    this.handleSearch();
                  }
                }, error => {
                  this.$message.error('删除失败');
                }
            )
          })
          .catch(() => {
          });
    },
    handleAdd(){
      this.addVisible=true;
    },
    //添加用户提交
    addUserSubmit(){
      if (this.addParam.user_id === ""||this.addParam.college_name === ""
        ||this.addParam.user_state === "") {
        this.$message.info("请输入必要信息");
        return;
      }
      if (this.addParam.user_password !== this.addParam.user_password2) {
        this.$message.error("两次输入密码不一致");
        return;
      }
      if (this.addParam.user_password.length < 8) {
        this.$message.error("密码至少为八位字符");
        return;
      }
      this.addVisible = false;
      addUser(this.addParam).then(
          res => {
            if(res.data.code===-1){
              this.$message.error('添加失败');
            }else{
              this.$message.success('添加成功');
              this.getData();
            }
          }, error => {
            this.$message.error("添加失败");
          }
      );
    },
    //重置自身密码与用户名相同
    resetMyPasswordByUserId(){
      resetPasswordByUserId().then(
          res => {
            if(res.data.code===-1){
              this.$message.error('重置失败');
            }else{
              this.$message.success('重置成功');
            }
          },error => {
            this.$message.error("重置失败");
          }
      );
      this.passwordVisible=false;
    },
    //重置自身密码为八位随机字符
    resetMyPasswordByRandom(){
      resetPasswordByRandom().then(
          res => {
            if(res.data.code===-1){
              this.$message.error('重置失败');
            }else{
              this.$message({duration:2000,
                            message:'重置成功,新密码为:'+res.data.msg,
                            type:"success"});
            }
          },error => {
            this.$message.error("重置失败");
          }
      );
      this.passwordVisible=false;
    },
    //手动重置自身密码
    resetMyPassword(){
      if (this.passwordParam.password1 !== this.passwordParam.password2) {
        this.$message.error("两次输入密码不一致");
        return;
      }
      if (this.passwordParam.password1.length < 8) {
        this.$message.error("密码至少为八位字符");
        return;
      }
      resetPassword(this.passwordParam.password1).then(
          res => {
            if(res.data.code===-1){
              this.$message.error('重置失败');
            }else{
              this.$message.success('重置成功');
            }
          },error => {
            this.$message.error("重置失败");
          }
      )
      this.passwordVisible=false;
    },
    resetPasswordByUserId2(){
      resetPasswordByUserIdADMIN(this.editParam.user_id).then(
          res => {
            if(res.data.code===-1){
              this.$message.error('重置失败');
            }else{
              this.$message.success('重置成功');
            }
          },error => {
            this.$message.error("重置失败");
          }
      );
      this.editVisible=false;
    },
    resetPasswordByRandom2(){
      resetPasswordByRandomADMIN(this.editParam.user_id).then(
          res => {
            if(res.data.code===-1){
              this.$message.error('重置失败');
            }else{
              this.$message({duration:2000,
                message:'重置成功,新密码为:'+res.data.msg,
                type:"success"});
            }
          },error => {
            this.$message.error("重置失败");
          }
      );
      this.editVisible=false;
    }
  }
};
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-select {
  width: 120px;
}

.handle-input {
  width: 300px;
  display: inline-block;
}

.table {
  width: 100%;
  font-size: 14px;
}

.float {
  float: right;
}

.red {
  color: #ff0000;
}

.mr10 {
  margin-right: 10px;
}

.mt10{
  margin-top: 10px;
}

.mb10{
  margin-bottom: 10px;
}

.table-td-thumb {
  display: block;
  margin: auto;
  width: 40px;
  height: 40px;
}
</style>
