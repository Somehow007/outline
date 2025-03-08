<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-lx-cascades"></i> 大纲列表
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">

      <div class="handle-box">
        <el-input type="number" v-model="query.outline_id" placeholder="大纲ID" class="handle-input mr10"></el-input>
        <el-input v-model="query.file_name" placeholder="文件名" class="handle-input mr10"></el-input>
        <el-input v-model="query.en_name" placeholder="英文名" class="handle-input mr10"></el-input>
        <el-input v-model="query.college_id" placeholder="上传学院" class="handle-input mr10"></el-input>
        <el-input v-model="query.year" placeholder="年份(版本)" class="handle-input mr10"></el-input>
        <el-select v-model="query.outline_state" clearable placeholder="大纲状态" class="handle-select mr10">
          <el-option
              v-for="item in stateOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch" :loading="isLoading">搜索</el-button>
        <el-button type="primary" icon="el-icon-lx-roundclose" @click="handleReset">重置</el-button>
      </div>

      <div class="handle-box">
        <el-button
            v-if="optionSwitch"
            type="primary"
            icon="el-icon-delete"
            class="handle-del mr10"
            @click="delAllSelection"
        >批量删除
        </el-button>
        <el-button
            v-if="userState!==''"
            type="primary"
            icon="el-icon-top"
            class="handle-del mr10"
            @click="handleUpload"
        >上传大纲
        </el-button>
        <el-button
            v-if="userState==='20'"
            type="primary"
            icon="el-icon-lx-down"
            class="handle-del mr10"
            @click="exportVisible=true;"
        >导出课程大纲修订情况统计
        </el-button>
        <div class="float"  v-if="userState==='20'">
          <div class="el-upload__tip" slot="tip">
            操作栏显示
            <el-switch
                v-model="optionSwitch">
            </el-switch>
          </div>
        </div>
      </div>

      <el-table
          :data="tableData"
          border
          class="table"
          ref="multipleTable"
          header-cell-class-name="table-header"
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" min-width="4%" align="center" key="1"
                         v-if="userState==='20'"></el-table-column>
        <el-table-column prop="outlineId" min-width="5%" fixed="left" label="大纲ID" align="center" key="2"></el-table-column>
        <el-table-column prop="fileName" label="文件名" min-width="24%" align="center" fixed="left"
                         key="3"></el-table-column>
        <el-table-column prop="enName" label="英文名称" min-width="16%" align="center" key="4"></el-table-column>
        <el-table-column prop="collegeId" label="上传学院" min-width="12%" align="center" key="5"></el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="13%" align="center" key="6"></el-table-column>
        <el-table-column prop="checkTime" label="审核时间" min-width="13%" align="center" key="7"></el-table-column>
        <el-table-column prop="outlineState" label="状态" align="center" v-if="false" key="8"></el-table-column>
        <el-table-column prop="year" label="年份/版本" min-width="8%" align="center" key="14"></el-table-column>
        <el-table-column prop="connectNum" label="应用课程数" min-width="8%" align="center" key="9" v-if="false"></el-table-column>
        <el-table-column label="应用课程数" min-width="8%" align="center"  key="13">
          <template slot-scope="scope">
            <el-button
                type="text"
                icon="el-icon-search"
                @click="handleCourse(scope.row.outlineId,scope.row.fileName+' 绑定课程')"
            >{{scope.row.connectNum}}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="outline" label="课程大纲" min-width="8%" align="center" fixed="right" key="11">
          <template slot-scope="scope">
            <div v-if="scope.row.outlineState==='10'">
              <el-tag
                  v-if="userState!=='20'&&!(userState==='21'&&myCollege===scope.row.collegeId)"
                  :type="'success'">待审核</el-tag>
              <el-button
                  v-if="userState==='20'||(userState==='21'&&myCollege===scope.row.collegeId)"
                  type="text"
                  icon="el-icon-search"
                  @click="handleCheck(scope.row.outlineId)">审核</el-button>
            </div>
            <div v-if="scope.row.outlineState==='11'">
              <el-button
                  type="text"
                  icon="el-icon-lx-down"
                  @click="downloadOutline2(scope.row.outlineId)"
              >查看</el-button>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" min-width="8%" align="center" fixed="right" v-if="optionSwitch" key="12">
          <template slot-scope="scope">
            <el-button
                type="text"
                icon="el-icon-delete"
                class="red"
                @click="handleDelete(scope.$index)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
            background
            layout="total, prev, pager, next"
            :current-page="courseQuery.page"
            :page-size="courseQuery.size"
            :total="pageTotal"
            @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>

    <!-- 关联课程查询弹出框 -->
    <el-dialog :title="courseTitle" :visible.sync="courseCheckVisible" width="90%">
      <el-table
          :data="courseTableDate"
          border
          class="table"
          header-cell-class-name="table-header"
      >
        <el-table-column prop="name" label="课程名称" min-width="12%" align="center" fixed :key="3"></el-table-column>
        <el-table-column prop="majorId" label="专业名称" min-width="12%" align="center" :key="4"></el-table-column>
        <el-table-column prop="collegeId" label="开课学院" min-width="12%" align="center" :key="5"></el-table-column>
        <el-table-column prop="year" label="年份" min-width="6%" align="center" :key="6"></el-table-column>
        <el-table-column prop="code" label="课程代码" min-width="8%" align="center" :key="7"></el-table-column>
        <el-table-column prop="courseType" label="课程类别" min-width="9%" align="center" :key="8"></el-table-column>
        <el-table-column prop="courseCategory" label="课程性质" min-width="8%" align="center" :key="9"></el-table-column>
        <el-table-column prop="term" label="修读学期" min-width="6%" align="center" :key="10"></el-table-column>
        <el-table-column prop="creditSum" label="总学分" min-width="5%" align="center" :key="11"></el-table-column>
        <el-table-column prop="hourWeek" label="总学时/周数" min-width="5%" align="center" :key="12"></el-table-column>
        <el-table-column prop="hourTeach" label="讲课学时" min-width="5%" align="center" :key="13"></el-table-column>
        <el-table-column prop="hourPractice" label="实验学时" min-width="5%" align="center" :key="14"></el-table-column>
        <el-table-column prop="hourOperation" label="上机学时" min-width="5%" align="center" :key="15"></el-table-column>
        <el-table-column prop="hourOutside" label="其他学时" min-width="5%" align="center" :key="16"></el-table-column>
        <el-table-column prop="testType" label="考核方式" min-width="5%" align="center" :key="17"></el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
            background
            layout="total, prev, pager, next"
            :current-page="query.page"
            :page-size="query.size"
            :total="pageTotal2"
            @current-change="handlePageChange2"
        ></el-pagination>
      </div>
    </el-dialog>

    <!-- 导出课程大纲修订情况统计 -->
    <el-dialog title="导出课程大纲修订情况统计" :visible.sync="exportVisible" width="20%">
      <el-input type="number" v-model="exportYear" placeholder="年级" class="handle-input2 mr10"></el-input>
      <div class="mr10 mt10" align="right">
        <el-button type="primary" @click="exportCountExcel">导出</el-button>
        <el-button type="primary" @click="exportVisible=false">取消</el-button>
      </div>
    </el-dialog>
    <!--上传大纲编辑框-->
    <el-dialog title="上传大纲" :visible.sync="uploadVisible" width="40%">
      <el-upload
          class="upload-demo"
          ref="upload"
          accept=".doc,.docx"
          :limit=1
          :file-list="fileList"
          :action="''"
          :on-change="onChange"
          :show-file-list="true"
          :auto-upload="false"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传doc/docx文件</div>
      </el-upload>
      <div class="mt10">
        英文名  (必填)
        <el-input v-model="enName" placeholder="请输入英文名" class="handle-input-upload"></el-input>
      </div>
      <div class="mt10">
        年份/版本(必填)
        <el-input v-model="year" placeholder="请输入年份/版本" class="handle-input-upload"></el-input>
      </div>
      <div class="mr10 mt10" align="right">
        <el-button type="primary" @click="handleSubmit">提交</el-button>
        <el-button type="primary" @click="handleCancel">取消</el-button>
      </div>
      <div class="mt10">
        <div class="el-upload__tip" slot="tip">同时绑定本学院课程</div>
      </div>
      <div class="mt10">
        <el-switch disabled
            v-model="extendSwitch">
        </el-switch>
      </div>
      <div class="mt10">
        <el-input  type="number" v-model="simpleQuery.year" placeholder="年级(必填)" class="handle-input mr10" v-if="extendSwitch"></el-input>
        <el-input  type="number" v-model="simpleQuery.code" placeholder="课程代码(必填)" class="handle-input mr10" v-if="extendSwitch"></el-input>
        <el-select clearable v-model="simpleQuery.state" placeholder="课程状态" class="handle-select mr10">
          <el-option
              v-for="item in stateOptions2"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSimpleSearch" v-if="extendSwitch" :loading="isLoading2">搜索</el-button>
      </div>
      <div class="mt10">
        <el-table
            v-if="extendSwitch"
            :data="simpleTableData"
            border
            class="table"
            ref="simpleMultipleTable2"
            header-cell-class-name="table-header"
            @selection-change="handleSelectionChange2"
        >
          <el-table-column type="selection" min-width="4%" align="center" :key="1" ></el-table-column>
          <el-table-column prop="courseId" label="ID" align="center" v-if="false" :key="2"></el-table-column>
          <el-table-column prop="name" label="课程名称" min-width="12%" align="center"  :key="3"></el-table-column>
          <el-table-column prop="majorId" label="专业名称" min-width="12%" align="center" :key="4"></el-table-column>
          <el-table-column prop="outlineId" label="课程大纲ID" align="center" v-if="false" :key="18"></el-table-column>
          <el-table-column prop="courseState" label="课程状态" align="center" v-if="false" :key="19"></el-table-column>
          <el-table-column prop="outline" label="课程大纲" min-width="8%" align="center" fixed="right">
            <template slot-scope="scope">
              <div v-if="scope.row.courseState==='00'">
                无
              </div>
              <div v-if="scope.row.courseState==='01'">
                待上传
              </div>
              <div v-if="scope.row.courseState==='02'">
                待审核
              </div>
              <div v-if="scope.row.courseState==='03'">
                <el-button
                    type="text"
                    icon="el-icon-lx-down"
                    @click="downloadOutline2(scope.row.outlineId)"
                >查看</el-button>
              </div>

            </template>

          </el-table-column>
        </el-table>
      </div>

      <div class="mr10 mt10" align="right">
        <el-button type="primary" @click="handleSubmit">提交</el-button>
        <el-button type="primary" @click="handleCancel">取消</el-button>
      </div>
    </el-dialog>
    <!--审核大纲编辑框-->
    <el-dialog title="审核大纲" :visible.sync="checkVisible" width="30%">
      <div align="center">
        <el-button type="primary" @click="downloadOutline()">下载查看</el-button>
      </div>
      <div class="mr10 mt20 " align="center">
        <el-button type="success" @click="handleCheckPass">审核通过</el-button>
        <el-button type="danger" @click="handleCheckFail">审核未通过</el-button>
      </div>
      <div class="mr10 mt10 " align="right">
        <el-button type="primary" @click="checkVisible = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addOutline,
  deleteAllOutline,
  deleteOutline, failOutline, getCourseByOutlineId,
  getOutline,
  getPersonalMessage, getSimpleCourse,
  passOutline
} from '../../api/index';
import Vue from "vue";
import bus from "@/components/common/bus";

export default {
  name: 'outline',
  data() {
    return {
      userState: "",
      myCollege: "",
      enName: "",
      year: "",
      exportYear:"",
      isLoading: false,
      isLoading2: false,
      query: {
        page: 1,
        size: 10,
        outline_id: "",
        file_name: "",
        en_name: "",
        college_id: "",
        outline_state: "",
        year:""
      },
      simpleQuery: {
        code: "",
        year: "",
        state:"01"
      },
      courseQuery: {
        page: 1,
        size: 10,
        outline_id: ""
      },
      checkOutlineId: "",
      courseTitle: "",
      stateOptions: [{value: '10', label: '未审核大纲'}, {value: '11', label: '已审核大纲'}],
      stateOptions2: [{value: '00', label: '无需修订大纲'}, {value: '01', label: '待上传大纲'}, {
        value: '02', label: '待审核大纲'
      }, {value: '03', label: '已通过大纲'}],
      fileList: [],
      tableData: [],
      simpleTableData: [],
      courseTableDate: [],
      extendSwitch: true,
      multipleSelection: [],
      simpleMultipleSelection: [],
      courseCheckVisible: false,
      uploadVisible: false,
      exportVisible: false,
      optionSwitch:false,
      checkVisible: false,
      pageTotal: 0,
      pageTotal2: 0,
      form: {},
      idx: -1,
      id: -1
    };
  },
  created() {
    bus.$on('checkOutline2', (queryId) => {
      if (typeof queryId !== "undefined") {
        this.query.college_id = "";
        this.query.outline_id = queryId;
        this.getData();
      }
    });
    bus.$on('checkOutline', (collegeName) => {
      if (typeof collegeName !== "undefined") {
        this.query.outline_id = "";
        this.query.college_id = collegeName;
        this.getData();
      }
    });

    let queryId = this.$route.query.outlineId;
    let collegeName = this.$route.query.collegeName;
    if (typeof queryId !== "undefined") {
      this.query.outline_id = queryId;
      this.$route.query.outlineId = "";
    }
    if (typeof collegeName !== "undefined") {
      this.query.college_id = collegeName;
      this.$route.query.collegeName = "";
    }
    this.userState = "";
    this.myCollege = "";
    this.getData();
    getPersonalMessage().then(res => {
      if (res.status === 200) {
        this.userReturn = res.data.data;
        this.userState = this.userReturn.userState;
        this.myCollege = this.userReturn.college;
      }
    });
  },
  methods: {

    getData() {
      getOutline(this.query).then(
          res => {
            this.isLoading = false;
            this.resultObj = res.data;
            this.tableData = this.resultObj.data;
            this.pageTotal = this.resultObj.total;
          }
      );
    },
    getSimpleData() {
      getSimpleCourse(this.simpleQuery.code,this.simpleQuery.year,this.simpleQuery.state).then(
          res=>{
            this.isLoading2 = false;
            this.simpleTableData=res.data.data;
          }
      );
    },
    getCourseData() {
      getCourseByOutlineId(this.courseQuery).then(
          res => {
            this.resultObj = res.data;
            this.courseTableDate = this.resultObj.data;
            this.pageTotal2 = this.resultObj.total;
          }
      );
    },
    // 触发搜索按钮
    handleSearch() {
      this.isLoading = true;
      this.getData();
    },
    handleSimpleSearch() {
      if (this.simpleQuery.code === "" || this.simpleQuery.year === "") {
        this.$message.info("请输入课程代码与年级后再搜索");
        return;
      }
      this.isLoading2 = true;
      this.getSimpleData();
    },
    // 触发重置按钮
    handleReset() {
      this.query.page = 1;
      this.query.size = 10;
      this.query.outline_id = "";
      this.query.file_name = "";
      this.query.en_name = "";
      this.query.college_id = "";
      this.query.outline_state = "";
      this.query.year = "";
      this.getData();
    },
    // 删除操作
    handleDelete(index, row) {
      // 二次确认删除
      this.$confirm('确定要删除吗？', '提示', {
        type: 'warning'
      })
          .then(() => {
            deleteOutline(this.tableData[index].outlineId).then(
                res=>{
                  this.$message.success('删除成功');
                  this.getData();
                },error=>{
                  this.$message.error('删除失败');
                }
            )
          })
          .catch(() => {
          });
    },
    // 多选操作
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    handleSelectionChange2(val) {
      this.simpleMultipleSelection = val;
    },
    delAllSelection() {
      // 二次确认删除
      this.$confirm('确定要删除吗？', '提示', {
        type: 'warning'
      })
          .then(() => {
            const length = this.multipleSelection.length;
            let ids = "";
            if (length > 0) {
              ids=this.multipleSelection[0].outlineId;
            }
            for (let i = 1; i < length; i++) {
              ids += "-" + this.multipleSelection[i].outlineId;
            }
            deleteAllOutline(ids).then(
                res=>{
                  this.$message.error(`删除成功`);
                  this.handleSearch();
                },error=>{
                  this.$message.error('删除失败');
                }
            )
            this.multipleSelection = [];
          })
          .catch(() => {
          });
    },
    // 分页导航
    handlePageChange(val) {
      this.$set(this.query, 'page', val);
      this.getData();
    },
    //下载课程大纲
    downloadOutline() {
      let a = document.createElement('a');
      a.href = Vue.prototype.$baseUrl+"/outline/download?outline_id="+this.checkOutlineId;
      a.click();
      a.remove();
    },
    //导出课程大纲修订情况统计
    exportCountExcel() {
      this.exportVisible=false
      let a = document.createElement('a');
      a.href = Vue.prototype.$baseUrl+"/excel/exportCountExcel?year="+this.exportYear;
      a.click();
      a.remove();
    },
    downloadOutline2(outlineId) {
      let a = document.createElement('a');
      a.href = Vue.prototype.$baseUrl+"/outline/download?outline_id="+outlineId;
      a.click();
      a.remove();
    },
    //上传课程大纲
    handleUpload() {
      this.uploadVisible = true;
      this.fileList = [];
    },
    onChange(file, fileList) {
      this.fileList = [];
      this.fileList.push(file);
    },
    handleSubmit() {
      if (this.fileList.length === 0) {
        this.$message.error("尚未选择文件")
        return;
      }
      const length = this.simpleMultipleSelection.length;
      if (this.enName === "") {
        this.$message.error("请输入英文名")
        return;
      }
      if (this.year === "") {
        this.$message.error("请输入年份/版本")
        return;
      }
      if (length === 0) {
        this.$message.error("请选择绑定课程")
        return;
      }
      let formData = new FormData();
      formData.append('uploadFile', this.fileList[0].raw);
      formData.append('enName', this.enName);
      formData.append('year', this.year);
      if (this.extendSwitch) {
        let ids = "";
        if (length > 0) {
          ids=this.simpleMultipleSelection[0].courseId;
        }
        for (let i = 1; i < length; i++) {
          ids += "-" + this.simpleMultipleSelection[i].courseId;
        }
        formData.append('courseIds', ids);
      }
      this.$message.info("正在上传,请等待.......");
      addOutline(formData).then(res => {
        this.handleCancel()
        this.$message.success("上传成功")
        this.extendSwitch=false;
        this.simpleQuery.code = "";
        this.simpleQuery.year = "";
        this.simpleTableData = [];
        this.multipleSelection = [];
      })
    },
    handleCancel() {
      this.$refs.upload.clearFiles();
      this.enName = "";
      this.uploadVisible = false;
    },
    handleCourse(outlineId,title) {
      this.courseTitle = title;
      this.courseTableDate = [];
      this.courseQuery.outline_id=outlineId;
      this.getCourseData();
      this.courseCheckVisible=true;
    },
    handlePageChange2(val) {
      this.$set(this.courseQuery, 'page', val);
      this.getCourseData();
    },
    handleCheck(outlineId) {
      this.checkOutlineId = outlineId;
      this.checkVisible = true;
    },
    handleCheckPass() {
      passOutline(this.checkOutlineId).then(
          res=>{
            this.$message.success(`审核完成`);
            this.handleSearch();
          },error=>{
            this.$message.error('审核失败');
          }
      )
      this.checkVisible=false;
    },
    handleCheckFail() {
      failOutline(this.checkOutlineId).then(
          res=>{
            this.$message.success(`审核完成`);
            this.handleSearch();
          },error=>{
            this.$message.error('审核失败');
          }
      )
      this.checkVisible=false;
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
  width: 120px;
  display: inline-block;
}

.handle-input-upload {
  width: 85%;
  display: inline-block;
}

.table {
  width: 100%;
  font-size: 14px;
}

.red {
  color: #ff0000;
}

.mr10 {
  margin-right: 10px;
}

.mt10 {
  margin-top: 10px;
}
.mt20 {
  margin-top: 20px;
}
.float {
  float: right;
}

.table-td-thumb {
  display: block;
  margin: auto;
  width: 40px;
  height: 40px;
}
</style>
