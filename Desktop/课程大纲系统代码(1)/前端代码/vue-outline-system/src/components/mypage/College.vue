<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-lx-cascades"></i> 学院列表
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <div class="handle-box">
        <el-input v-model="query.college_name" placeholder="学院名" class="handle-input mr10"></el-input>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        <el-button type="primary" icon="el-icon-lx-roundclose" @click="handleReset">重置</el-button>
      </div>

      <div class="handle-box">
        <el-button
            v-if="userState==='20'"
            type="primary"
            icon="el-icon-lx-searchlist"
            class="handle-del mr10"
            @click="countNums"
        >重新计数所有学院课程与大纲</el-button>

        <el-button
            v-if="optionSwitch"
            type="primary"
            icon="el-icon-lx-friendadd"
            class="handle-del mr10"
            @click="handleAdd"
        >添加学院</el-button>

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
      <el-dialog title="编辑" :visible.sync="editVisible" width="30%">
        <el-form ref="form" :model="editParam" label-width="70px">
          <el-form-item label="学院ID">
            <el-input type="number" v-model="editParam.college_id" class="handle-input2 mr10" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="学院名称">
            <el-input v-model="editParam.college_name" placeholder="学院名称" class="handle-input2 mr10"></el-input>
          </el-form-item>
        </el-form>

        <span slot="footer" class="dialog-footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit">确 定</el-button>
            </span>
      </el-dialog>

      <!-- 添加弹出框 -->
      <el-dialog title="添加学院" :visible.sync="addVisible" width="30%">
        <el-form ref="form" :model="addParam" label-width="70px">
          <el-form-item label="学院名称">
            <el-input v-model="addParam.college_name" placeholder="学院名称" class="handle-input2 mr10"></el-input>
          </el-form-item>
        </el-form>

        <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="addCollegeSubmit">确 定</el-button>
            </span>
      </el-dialog>

      <el-table
          :data="tableData"
          border
          class="table"
          ref="multipleTable"
          header-cell-class-name="table-header"
      >

        <el-table-column prop="collegeId" label="ID" min-width="10%" align="center"></el-table-column>
        <el-table-column prop="collegeName" label="学院名" min-width="20%" align="center"></el-table-column>
        <el-table-column prop="courseNum" label="课程总数" min-width="10%" align="center"></el-table-column>
        <el-table-column prop="requireNum" label="待修订大纲课程数" min-width="10%" align="center" v-if="false"></el-table-column>
        <el-table-column label="待修订大纲课程数" min-width="10%" align="center">
          <template slot-scope="scope">
            <el-button
                type="text"
                @click="handleTofix(scope.row.collegeName)"
            >{{scope.row.requireNum}}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="uploadNum" label="提交大纲数" min-width="10%" align="center" v-if="false"></el-table-column>
        <el-table-column label="提交大纲数" min-width="10%" align="center">
          <template slot-scope="scope">
            <el-button
                type="text"
                @click="handleUpload(scope.row.collegeName)"
            >{{scope.row.uploadNum}}</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="12%" align="center" fixed="right"
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
  addCollege,
  countCollegeNums, deleteCollege,
  getCollege,
  getPersonalMessage,
  updateCollege
} from '../../api/index';
import bus from "@/components/common/bus";

export default {
  name: 'college',
  data() {
    return {
      userState: "",
      myCollege:"",
      optionSwitch: false,
      editVisible: false,
      addVisible: false,
      query: {
        page: 1,
        size: 10,
        college_id: "",
        college_name: ""
      },
      editParam:{
        college_id:"",
        college_name:""
      },
      addParam:{
        college_name:""
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
    this.myCollege = "";
    this.getData();
    getPersonalMessage().then(res => {
      if (res.status === 200) {
        this.userReturn=res.data.data;
        this.userState=this.userReturn.userState;
        this.myCollege=this.userReturn.college;
      }
    });
  },
  methods: {

    getData() {
      getCollege(this.query).then(
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
      this.query.college_id = "";
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
    //重新计数
    countNums(){
      countCollegeNums().then(res => {
        this.$message.success('计数完成');
      });
    },
    handleTofix(collegeName) {
      bus.$emit("checkCollegeRequire",collegeName)
      this.$router.push({ path: '/course', query: { collegeName: collegeName }});
    },
    handleUpload(collegeName) {
      bus.$emit("checkOutline",collegeName)
      this.$router.push({ path: '/outline', query: { collegeName: collegeName }});
    },
    //编辑操作
    handleEdit(index, row){
      this.editParam.college_id=this.tableData[index].collegeId;
      this.editParam.college_name=this.tableData[index].collegeName;
      this.editVisible=true;
    },
    //保存编辑
    saveEdit() {
      this.editVisible = false;
      updateCollege(this.editParam.college_id,this.editParam.college_name).then(
          res => {
            this.$message.success("修改成功");
            this.getData();
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
            deleteCollege(this.tableData[index].collegeId).then(
                res => {
                  if(res.data.code===-1){
                    this.$message.error('删除失败,请先清空该学院课程，账户后重试');
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
    //添加学院提交
    addCollegeSubmit(){
      if (this.addParam.college_name === "") {
        this.$message.info("请输入必要信息");
        return;
      }
      this.addVisible = false;
      addCollege(this.addParam.college_name).then(
          res => {
            this.$message.success("添加成功");
            this.getData();
          }, error => {
            this.$message.error("添加失败");
          }
      );
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

.table-td-thumb {
  display: block;
  margin: auto;
  width: 40px;
  height: 40px;
}
</style>
