<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-lx-cascades"></i> 课程列表
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <div class="handle-box">
        <el-input v-model="query.name" placeholder="课程名称" class="handle-input mr10"></el-input>
        <el-input v-model="query.major_id" placeholder="专业名称" class="handle-input mr10"></el-input>
        <el-input v-model="query.college_id" placeholder="开课学院" class="handle-input mr10"></el-input>
        <el-input type="number" v-model="query.year" placeholder="年份" class="handle-input mr10"></el-input>
        <el-input type="number" v-model="query.code" placeholder="课程代码" class="handle-input mr10"></el-input>
      </div>

      <div class="handle-box">
        <el-select clearable v-model="query.course_category" placeholder="课程性质" class="handle-select-m mr10">
          <el-option
              v-for="item in categoriesOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
        <el-select clearable v-model="query.course_type" placeholder="课程类别" class="handle-select mr10">
          <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
        <el-select clearable v-model="query.term" placeholder="修读学期" class="handle-select-m mr10">
          <el-option
              v-for="count in 8"
              :key="count"
              :label="count"
              :value="count">
          </el-option>
        </el-select>
        <el-select clearable v-model="query.course_state" placeholder="大纲状态" class="handle-select mr10">
          <el-option
              v-for="item in stateOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch" :loading="isLoading">搜索</el-button>
        <el-button type="primary" icon="el-icon-lx-roundclose" @click="handleReset">重置</el-button>
        <el-button type="primary" @click="handleBatchDownload">批量下载</el-button>
      </div>

      <div class="handle-box" >
        <el-button
            v-if="optionSwitch"
            type="primary"
            icon="el-icon-delete"
            class="handle-del mr10"
            @click="delAllSelection"
        >批量删除</el-button>
        <el-button
            v-if="userState!==''"
            type="primary"
            icon="el-icon-search"
            class="handle-del mr10"
            @click="searchCourseToUpload"
        >查询本学院待上传大纲课程
        </el-button>
        <el-button
            v-if="userState!==''"
            type="primary"
            icon="el-icon-search"
            class="handle-del mr10"
            @click="searchCourseToCheck"
        >查询本学院待审核大纲课程
        </el-button>

        <el-button
            v-if="optionSwitch"
            type="primary"
            icon="el-icon-lx-friendadd"
            class="handle-del mr10"
            @click="handleAdd"
        >添加课程</el-button>
        <el-button
            v-if="optionSwitch"
            type="primary"
            icon="el-icon-lx-friendadd"
            class="handle-del mr10"
            @click="handleAddAll"
        >批量添加课程</el-button>
        <el-button
            v-if="false"
            type="primary"
            icon="el-icon-lx-friendadd"
            class="handle-del mr10"
            @click="handleSetAll"
        >批量设置课程为待修订</el-button>
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
        <el-table-column type="selection" min-width="4%" align="center" :key="1" fixed
                         v-if="userState==='20'"></el-table-column>
        <el-table-column prop="courseId" label="ID" align="center" v-if="false" :key="2"></el-table-column>
        <el-table-column prop="name" label="课程名称" min-width="12%" align="center" fixed :key="3"></el-table-column>
        <el-table-column prop="majorId" label="专业名称" min-width="12%" align="center" :key="4"></el-table-column>
        <el-table-column prop="collegeId" label="开课学院" min-width="12%" align="center" :key="5"></el-table-column>
        <el-table-column prop="year" label="年级" min-width="6%" align="center" :key="6"></el-table-column>
        <el-table-column prop="code" label="课程代码" min-width="8%" align="center" :key="7"></el-table-column>
        <el-table-column prop="courseType" label="课程类别" min-width="8%" align="center" :key="8"></el-table-column>
        <el-table-column prop="courseCategory" label="课程性质" min-width="8%" align="center" :key="9"></el-table-column>
        <el-table-column prop="term" label="修读学期" min-width="6%" align="center" :key="10"></el-table-column>
        <el-table-column prop="creditSum" label="总学分" min-width="5%" align="center" :key="11"></el-table-column>
        <el-table-column prop="hourWeek" label="总学时/周数" min-width="5%" align="center" :key="12"></el-table-column>
        <el-table-column prop="hourTeach" label="讲课学时" min-width="5%" align="center" :key="13"></el-table-column>
        <el-table-column prop="hourPractice" label="实验学时" min-width="5%" align="center" :key="14"></el-table-column>
        <el-table-column prop="hourOperation" label="上机学时" min-width="5%" align="center" :key="15"></el-table-column>
        <el-table-column prop="hourOutside" label="其他学时" min-width="5%" align="center" :key="16"></el-table-column>
        <el-table-column prop="testType" label="考核方式" min-width="5%" align="center" :key="17"></el-table-column>
        <el-table-column prop="outlineId" label="课程大纲ID" align="center" v-if="false" :key="18"></el-table-column>
        <el-table-column prop="courseState" label="课程状态" align="center" v-if="false" :key="19"></el-table-column>
        <el-table-column prop="outline" label="课程大纲" min-width="8%" align="center" fixed="right">
          <template slot-scope="scope">
            <div v-if="scope.row.courseState==='00'">
              无
            </div>
            <div v-if="scope.row.courseState==='01'">
              {{myCollege!==scope.row.collegeId?'待上传':' '}}
              <el-button
                  v-if="myCollege===scope.row.collegeId"
                  type="text"
                  icon="el-icon-lx-upload"
                  @click="handleUpload(scope.row.code,scope.row.year,scope.row.courseId)"
              >上传</el-button>
            </div>
            <div v-if="scope.row.courseState==='02'">
              {{userState!=='20'&&!(userState==='21'&&myCollege===scope.row.collegeId)?'待审核':''}}
              <el-button
                  v-if="userState==='20'||(userState==='21'&&myCollege===scope.row.collegeId)"
                  type="text"
                  icon="el-icon-search"
                  @click="checkOutline(scope.row.outlineId)"
              >审核</el-button>
              <el-button
                  v-if="myCollege===scope.row.collegeId"
                  type="text"
                  icon="el-icon-lx-upload"
                  @click="handleUpload2(scope.row.code,scope.row.year,scope.row.courseId)"
              >修订</el-button>
            </div>
            <div v-if="scope.row.courseState==='03'">
              <el-button
                  type="text"
                  icon="el-icon-lx-down"
                  @click="downloadOutline(scope.row.outlineId)"
              >查看</el-button>
              <el-button
                  v-if="myCollege===scope.row.collegeId"
                  type="text"
                  icon="el-icon-lx-upload"
                  @click="handleUpload3(scope.row.code,scope.row.year,scope.row.courseId)"
              >修订</el-button>
            </div>
            <div v-if="scope.row.courseState!=='00'">
              <el-button
                  type="text"
                  icon="el-icon-search"
                  @click="handleCheck(scope.row.courseId)">历史大纲</el-button>
            </div>
          </template>

        </el-table-column>
        <el-table-column label="操作" min-width="12%" align="center" fixed="right"
                         v-if="optionSwitch">
          <template slot-scope="scope">
            <el-button
                v-if="userState==='20'||myCollege===scope.row.collegeId"
                type="text"
                icon="el-icon-edit"
                @click="handleEdit(scope.$index, scope.row)"
            >编辑
            </el-button>
            <el-button
                v-if="userState==='20'||myCollege===scope.row.collegeId"
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

    <!-- 查看历史大纲弹出框 -->
    <el-dialog title="历史大纲" :visible.sync="checkVisible" width="50%">
      <el-table
          ref="checkTable"
          :data="oldOutlineTableData"
          style="width: 100%">
        <el-table-column prop="outlineId" min-width="5%" fixed="left" label="大纲编号" align="center"></el-table-column>
        <el-table-column prop="fileName" label="文件名" min-width="24%" align="center"></el-table-column>
        <el-table-column prop="enName" label="英文名称" min-width="16%" align="center" key="4"></el-table-column>
        <el-table-column prop="updateTime" label="绑定时间" min-width="16%" align="center" key="5"></el-table-column>
        <el-table-column prop="year" label="年份/版本" min-width="10%" align="center" key="6"></el-table-column>
        <el-table-column prop="outline" label="课程大纲" min-width="8%" align="center" fixed="right" key="11">
          <template slot-scope="scope">
            <el-button
                type="text"
                icon="el-icon-lx-down"
                @click="downloadOutline(scope.row.outlineId)"
            >查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加课程弹出框 -->
    <el-dialog title="添加课程" :visible.sync="addVisible" width="30%">
      <el-form ref="form2" :model="addParam" label-width="70px">
        <el-form-item label="学院名">
          <el-input v-model="addParam.college_name" placeholder="学院名(必填)"
                    class="handle-input2 mr10" :disabled="userState!=='20'"></el-input>
        </el-form-item>
        <el-form-item label="专业名">
          <el-input v-model="addParam.major_name" placeholder="专业名(必填)" class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="课程代码">
          <el-input type="number" v-model="addParam.code" placeholder="课程代码(必填)" class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="addParam.name" placeholder="课程名称(必填)" class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="课程类别">
          <el-select clearable v-model="addParam.course_type" placeholder="课程类别" class="handle-select mr10">
            <el-option
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程性质">
          <el-select clearable v-model="addParam.course_category" placeholder="课程性质" class="handle-select-m mr10">
            <el-option
                v-for="item in categoriesOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="修读学期">
          <el-select clearable v-model="addParam.term" placeholder="修读学期" class="handle-select-m mr10">
            <el-option
                v-for="count in 8"
                :key="count"
                :label="count"
                :value="count">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程状态">
          <el-select clearable v-model="addParam.course_state" placeholder="课程状态" class="handle-select mr10">
            <el-option
                v-for="item in stateOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-input type="number" v-model="addParam.year" placeholder="年级" class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="总学分">
          <el-input type="number" v-model="addParam.credit_sum" placeholder="总学分" class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="总学时/周数">
          <el-input type="number" v-model="addParam.hour_week" placeholder="总学时/周数"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="讲课学时">
          <el-input type="number" v-model="addParam.hour_teach" placeholder="讲课学时"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="实验学时">
          <el-input type="number" v-model="addParam.hour_practice" placeholder="实验学时"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="上机学时">
          <el-input type="number" v-model="addParam.hour_operation" placeholder="上机学时"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="其他学时">
          <el-input type="number" v-model="addParam.hour_outside" placeholder="其他学时"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="addCourseSubmit">确 定</el-button>
            </span>
    </el-dialog>
    <!-- 编辑弹出框 -->
    <el-dialog title="编辑" :visible.sync="editVisible" width="30%">
      <el-form ref="form" :model="editParam" label-width="70px">
        <el-form-item label="课程代码">
          <el-input type="number" v-model="editParam.code" placeholder="课程代码" class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="editParam.name" placeholder="课程名称" class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="课程类别">
          <el-select clearable v-model="editParam.course_type" placeholder="课程类别" class="handle-select mr10">
            <el-option
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程性质">
          <el-select clearable v-model="editParam.course_category" placeholder="课程性质" class="handle-select-m mr10">
            <el-option
                v-for="item in categoriesOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="修读学期">
          <el-select clearable v-model="editParam.term" placeholder="修读学期" class="handle-select-m mr10">
            <el-option
                v-for="count in 8"
                :key="count"
                :label="count"
                :value="count">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程状态">
          <el-select clearable v-model="editParam.course_state" placeholder="课程状态" class="handle-select mr10">
            <el-option
                v-for="item in stateOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-input type="number" v-model="editParam.year" placeholder="年级" class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="总学分">
          <el-input type="number" v-model="editParam.credit_sum" placeholder="总学分"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="总学时/周数">
          <el-input type="number" v-model="editParam.hour_week" placeholder="总学时/周数"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="讲课学时">
          <el-input type="number" v-model="editParam.hour_teach" placeholder="讲课学时"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="实验学时">
          <el-input type="number" v-model="editParam.hour_practice" placeholder="实验学时"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="上机学时">
          <el-input type="number" v-model="editParam.hour_operation" placeholder="上机学时"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
        <el-form-item label="其他学时">
          <el-input type="number" v-model="editParam.hour_outside" placeholder="其他学时"
                    class="handle-input2 mr10"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit">确 定</el-button>
            </span>
    </el-dialog>

    <!--批量添加课程编辑框-->
    <el-dialog title="批量添加课程" :visible.sync="addAllVisible" width="30%">
      <div>
        <el-button type="primary" @click="downloadTemplate()">下载模版</el-button>
      </div>
      <el-upload
          class="upload-demo mt10"
          ref="upload2"
          accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          :limit=1
          :file-list="fileList2"
          :action="''"
          :on-change="onChange2"
          :show-file-list="true"
          :auto-upload="false"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传xlsx/xls文件</div>
      </el-upload>
      <div class="mr10 mt10"  align="right">
        <el-button type="primary" @click="handleSubmit2">提交</el-button>
        <el-button type="primary" @click="handleCancel2">取消</el-button>
      </div>
    </el-dialog>

    <!--批量设置课程为待修订-->
    <el-dialog title="批量设置课程为待修订" :visible.sync="setAllVisible" width="30%">
      <el-upload
          class="upload-demo"
          ref="upload3"
          accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          :limit=1
          :file-list="fileList3"
          :action="''"
          :on-change="onChange3"
          :show-file-list="true"
          :auto-upload="false"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传xlsx/xls文件</div>
        <div class="el-upload__tip" slot="tip">工作簿表名为:</div>
        <div class="el-upload__tip" slot="tip">待修订课程汇总表</div>
        <div class="el-upload__tip" slot="tip">工作簿中列名为:</div>
        <div class="el-upload__tip" slot="tip">课程代码</div>
      </el-upload>
      <el-input type="number" v-model="year" placeholder="年级" class="handle-input2 mr10"></el-input>
      <div class="mr10 mt10" align="right">
        <el-button type="primary" @click="handleSubmit3">提交</el-button>
        <el-button type="primary" @click="handleCancel3">取消</el-button>
      </div>
    </el-dialog>

    <!--上传大纲编辑框-->
    <el-dialog title="上传大纲" :visible.sync="outlineAddVisible" width="50%">
      <div class="mt10">
        <div class="el-upload__tip" slot="tip">从本学院已上传大纲中选择</div>
      </div>
      <div class="mt10">
        <el-switch
            v-model="extendSwitch0">
        </el-switch>
      </div>
      <div class="handle-box mt10" v-if="extendSwitch0">
        <el-input type="number" v-model="outlineQuery.outline_id" placeholder="大纲ID"
                  class="handle-input mr10"></el-input>
        <el-input v-model="outlineQuery.file_name" placeholder="文件名" class="handle-input mr10"></el-input>
        <el-input v-model="outlineQuery.en_name" placeholder="英文名" class="handle-input mr10"></el-input>
        <el-select clearable v-model="outlineQuery.outline_state" placeholder="大纲状态" class="handle-select mr10">
          <el-option
              v-for="item in stateOptions2"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch2" :loading="isLoading2">搜索</el-button>
        <el-button type="primary" icon="el-icon-lx-roundclose" @click="handleReset2">重置</el-button>
      </div>
      <el-table
          v-if="extendSwitch0"
          ref="singleTable"
          :data="outlineTableData"
          highlight-current-row
          @current-change="handleCurrentChange"
          style="width: 100%">
        <el-table-column prop="outlineId" min-width="5%" fixed="left" label="大纲ID" align="center"></el-table-column>
        <el-table-column prop="fileName" label="文件名" min-width="24%" align="center"></el-table-column>
        <el-table-column prop="enName" label="英文名称" min-width="16%" align="center" key="4"></el-table-column>
        <el-table-column prop="outlineState" label="状态" align="center" v-if="false" key="8"></el-table-column>
        <el-table-column prop="outline" label="课程大纲" min-width="8%" align="center" fixed="right" key="11">
          <template slot-scope="scope">
            <div v-if="scope.row.outlineState==='10'">
              待审核
            </div>
            <div v-if="scope.row.outlineState==='11'">
              <el-button
                  type="text"
                  icon="el-icon-lx-down"
                  @click="downloadOutline(scope.row.outlineId)"
              >查看
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
            v-if="extendSwitch0"
            background
            layout="total, prev, pager, next"
            :current-page="outlineQuery.page"
            :page-size="outlineQuery.size"
            :total="pageTotal2"
            @current-change="handlePageChange2"
        ></el-pagination>
      </div>

      <el-upload
          align="center"
          v-if="!extendSwitch0"
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
      <div class="mt10" v-if="!extendSwitch0">
        英文名 (必填)
        <el-input v-model="enName" placeholder="请输入英文名" class="handle-input-upload"></el-input>
      </div>
      <div class="mt10" v-if="!extendSwitch0">
        年份/版本(必填)
        <el-input v-model="version" placeholder="请输入年份/版本" class="handle-input-upload"></el-input>
      </div>
      <div class="mr10 mt10" align="right">
        <el-button type="primary" @click="handleSubmit">提交</el-button>
        <el-button type="primary" @click="handleCancel">取消</el-button>
      </div>

      <div class="mt10">
        <div class="el-upload__tip" slot="tip">同时绑定其他专业相同课程</div>
      </div>
      <div class="mt10">
        <el-switch
            v-model="extendSwitch">
        </el-switch>
      </div>

      <div align="right" v-if="extendSwitch">
        <el-select clearable v-model="simpleQuery.state" placeholder="课程状态" class="handle-select mr10">
          <el-option
              v-for="item in stateOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSimpleSearch" :loading="isLoading3">搜索</el-button>
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
          <el-table-column type="selection" min-width="4%" align="center" :key="1"></el-table-column>
          <el-table-column prop="courseId" label="ID" align="center" v-if="false" :key="2"></el-table-column>
          <el-table-column prop="name" label="课程名称" min-width="12%" align="center" :key="3"></el-table-column>
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
                    @click="downloadOutline(scope.row.outlineId)"
                >查看
                </el-button>
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
  </div>
</template>

<script>

import {
  addAllCourse,
  addAssignOutline, addCourse,
  addOutline, deleteAllCourse, deleteCourse,
  getCourse, getOldOutlines,
  getOutline,
  getPersonalMessage,
  getSimpleCourse, setAllCourse, updateCourse
} from "@/api";
import Vue from "vue";
import bus from "@/components/common/bus";

export default {
  name: 'course',
  data() {
    return {
      year: "",
      userState: "",
      myCollege: "",
      enName: "",
      version: "",
      isLoading: false,
      isLoading2: false,
      isLoading3: false,
      query: {
        page: 1,
        size: 10,
        course_id: "",
        code: "",
        name: "",
        course_type: "",
        course_category: "",
        outline_id: "",
        major_id: "",
        college_id: "",
        term: "",
        course_state: "",
        year: ""
      },
      simpleQuery: {
        code: "",
        year: "",
        state: "01"
      },
      outlineQuery: {
        page: 1,
        size: 10,
        outline_id: "",
        file_name: "",
        en_name: "",
        college_id: "",
        outline_state: ""
      },
      editParam: {
        course_id: "",
        code: "",
        name: "",
        course_type: "",
        course_category: "",
        term: "",
        course_state: "",
        year: "",
        credit_sum: "",
        hour_week: "",
        hour_teach: "",
        hour_practice: "",
        hour_operation: "",
        hour_outside: ""
      },
      addParam: {
        code: "",
        name: "",
        college_name: "",
        major_name: "",
        course_type: "",
        course_category: "",
        term: "",
        course_state: "",
        year: "",
        credit_sum: "",
        hour_week: "",
        hour_teach: "",
        hour_practice: "",
        hour_operation: "",
        hour_outside: ""
      },
      uploadParam: {
        outline_id: "",
        courseIds: []
      },
      curCourseId: "",
      typeOptions: [{value: '0', label: '实验实践环节'}, {value: '1', label: '通识教育必修'}, {
        value: '2',
        label: '通识教育选修'
      }, {value: '3', label: '专业方向必修'},
        {value: '4', label: '专业方向选修'}, {value: '5', label: '专业基础必修'}, {value: '6', label: '专业基础选修'}, {
          value: '7',
          label: '自主研学'
        }],
      categoriesOptions: [{value: '0', label: '必修课'}, {value: '1', label: '选修课'}, {value: '2', label: '校选修课'}],
      stateOptions: [{value: '00', label: '无需修订大纲'}, {value: '01', label: '待上传大纲'}, {
        value: '02', label: '待审核大纲'
      }, {value: '03', label: '已通过大纲'}],
      stateOptions2: [{value: '10', label: '未审核大纲'}, {value: '11', label: '已审核大纲'}],
      tableData: [],
      outlineTableData: [],
      oldOutlineTableData: [],
      fileList: [],
      fileList2: [],
      fileList3: [],
      simpleTableData: [],
      multipleSelection: [],
      simpleMultipleSelection: [],
      delList: [],
      checkVisible: false,
      addAllVisible: false,
      setAllVisible: false,
      extendSwitch0: false,
      extendSwitch: true,
      optionSwitch: false,
      editVisible: false,
      addVisible: false,
      outlineAddVisible: false,
      pageTotal: 0,
      pageTotal2: 0,
      form: {},
      idx: -1,
      id: -1
    };
  },
  created() {
    bus.$on('checkCollegeRequire', (collegeName) => {
      if (typeof collegeName !== "undefined") {
        this.query.major_id = "";
        this.query.college_id = collegeName;
        this.query.course_state = "01"
        this.getData();
      }
    });
    bus.$on('checkMajorRequire', (majorName) => {
      if (typeof majorName !== "undefined") {
        this.query.college_id = "";
        this.query.major_id = majorName;
        this.query.course_state = "01"
        this.getData();
      }
    });

    let collegeName = this.$route.query.collegeName;
    let majorName = this.$route.query.majorName;
    if (typeof collegeName !== "undefined") {
      this.query.college_id = collegeName;
      this.query.course_state = "01"
      this.$route.query.collegeName = "";
    }
    if (typeof majorName !== "undefined") {
      this.query.major_id = majorName;
      this.query.course_state = "01"
      this.$route.query.majorName = "";
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
      getCourse(this.query).then(
          res => {
            this.isLoading = false;
            this.resultObj = res.data;
            this.tableData = this.resultObj.data;
            this.pageTotal = this.resultObj.total;
          }
      );
    },
    getOutlineData() {
      this.outlineQuery.college_id=this.myCollege;
      getOutline(this.outlineQuery).then(
          res => {
            this.isLoading2 = false;
            this.resultObj = res.data;
            this.outlineTableData = this.resultObj.data;
            this.pageTotal2 = this.resultObj.total;
          }
      );
    },
    getSimpleData() {
      getSimpleCourse(this.simpleQuery.code, this.simpleQuery.year,this.simpleQuery.state).then(
          res => {
            this.simpleTableData = res.data.data;
            this.isLoading3=false;
          }
      );
    },

    handleCurrentChange(val) {
      this.currentRow = val;
    },
    // 触发搜索按钮
    handleSearch() {
      this.isLoading = true;
      this.getData();
    },
    handleSearch2() {
      this.isLoading2 = true;
      this.getOutlineData();
    },
    //触发批量下载按钮
    handleBatchDownload() {
      //查询参数转换
      let keyValuePairs = [];
      for (let key in this.query) {
        if (this.query.hasOwnProperty(key)) {
          const value = this.query[key];
          const encodedKey = encodeURIComponent(key);
          const encodedValue = encodeURIComponent(value);
          keyValuePairs.push(`${encodedKey}=${encodedValue}`);
        }
      }

      let params = keyValuePairs.join('&');

      let a = document.createElement('a');
      a.href = Vue.prototype.$baseUrl + "/outline/batchDownload?" + params;
      a.click();
      a.remove();
    },
    // 触发重置按钮
    handleReset() {
      this.query.page = 1;
      this.query.size = 10;
      this.query.course_id = "";
      this.query.code = "";
      this.query.name = "";
      this.query.course_type = "";
      this.query.course_category = "";
      this.query.outline_id = "";
      this.query.major_id = "";
      this.query.college_id = "";
      this.query.term = "";
      this.query.course_state = "";
      this.query.year = "";
      this.getData();
    },
    handleReset2() {
      this.outlineQuery.page = 1;
      this.outlineQuery.size = 10;
      this.outlineQuery.outline_id = "";
      this.outlineQuery.file_name = "";
      this.outlineQuery.en_name = "";
      this.outlineQuery.outline_state = "";
      this.getData();
    },
    // 删除操作
    handleDelete(index, row) {
      // 二次确认删除
      this.$confirm('确定要删除吗？', '提示', {
        type: 'warning'
      })
          .then(() => {
            deleteCourse(this.tableData[index].courseId).then(
                res => {
                  this.$message.success('删除成功');
                  this.handleSearch();
                }, error => {
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
            let length = this.multipleSelection.length;
            let ids = "";
            if (length > 0) {
              ids = this.multipleSelection[0].courseId;
            }
            for (let i = 1; i < length; i++) {
              ids += "-" + this.multipleSelection[i].courseId;
            }
            deleteAllCourse(ids).then(
                res => {
                  this.$message.error(`删除成功`);
                  this.handleSearch();
                }, error => {
                  this.$message.error('删除失败');
                }
            )
            this.multipleSelection = [];
          })
          .catch(() => {
          });
    },
    // 编辑操作
    handleEdit(index, row) {
      this.idx = index;
      this.editParam.course_id = this.tableData[index].courseId;
      this.editParam.code = this.tableData[index].code;
      this.editParam.name = this.tableData[index].name;
      this.typeOptions.forEach((value, index2) => {
        if (this.typeOptions[index2].label === this.tableData[index].courseType) {
          this.editParam.course_type = this.typeOptions[index2].value;
        }
      });
      this.categoriesOptions.forEach((value, index2) => {
        if (this.categoriesOptions[index2].label === this.tableData[index].courseCategory) {
          this.editParam.course_category = this.categoriesOptions[index2].value;
        }
      });
      this.editParam.term = this.tableData[index].term;
      this.editParam.course_state = this.tableData[index].courseState;
      this.editParam.year = this.tableData[index].year;
      this.editParam.credit_sum = this.tableData[index].creditSum;
      this.editParam.hour_week = this.tableData[index].hourWeek;
      this.editParam.hour_teach = this.tableData[index].hourTeach;
      this.editParam.hour_practice = this.tableData[index].hourPractice;
      this.editParam.hour_operation = this.tableData[index].hourOperation;
      this.editParam.hour_outside = this.tableData[index].hourOutside;
      this.editVisible = true;
    },
    // 保存编辑
    saveEdit() {
      this.editVisible = false;
      updateCourse(this.editParam).then(
          res => {
            this.$message.success("修改成功");
            this.getData();
          }, error => {
            this.$message.error("修改失败");
          }
      );
    },
    // 分页导航
    handlePageChange(val) {
      this.$set(this.query, 'page', val);
      this.getData();
    },
    handlePageChange2(val) {
      this.$set(this.outlineQuery, 'page', val);
      this.getOutlineData();
    },
    //下载课程大纲
    downloadOutline(outlineId) {
      let a = document.createElement('a');
      a.href = Vue.prototype.$baseUrl + "/outline/download?outline_id=" + outlineId;
      a.click();
      a.remove();
    },
    //下载批量添加课程模版
    downloadTemplate() {
      let a = document.createElement('a');
      a.href = Vue.prototype.$baseUrl + "/excel/downloadTemplate";
      a.click();
      a.remove();
    },
    //查询本学院待审核大纲
    searchCourseToCheck() {
      this.query.college_id = this.myCollege;
      this.query.course_state = "02";
      this.getData();
    },
    //查询本学院待上传大纲
    searchCourseToUpload() {
      this.query.college_id = this.myCollege;
      this.query.course_state = "01";
      this.getData();
    },
    //前往审核大纲
    checkOutline(outlineId) {
      bus.$emit("checkOutline2", outlineId);
      this.$router.push({path: '/outline', query: {outlineId: outlineId}});
    },
    //上传课程大纲
    handleUpload(code, year, courseId) {
      this.curCourseId = courseId;
      this.simpleQuery.code = code;
      this.simpleQuery.year = year;
      this.simpleQuery.state="01";
      this.fileList = [];
      this.outlineAddVisible = true;
      this.getOutlineData();
      this.getSimpleData();
    },
    handleUpload2(code, year, courseId) {
      this.curCourseId = courseId;
      this.simpleQuery.code = code;
      this.simpleQuery.year = year;
      this.simpleQuery.state="02";
      this.fileList = [];
      this.outlineAddVisible = true;
      this.getOutlineData();
      this.getSimpleData();
    },
    handleUpload3(code, year, courseId) {
      this.curCourseId = courseId;
      this.simpleQuery.code = code;
      this.simpleQuery.year = year;
      this.simpleQuery.state="03";
      this.fileList = [];
      this.outlineAddVisible = true;
      this.getOutlineData();
      this.getSimpleData();
    },
    onChange(file, fileList) {
      this.fileList = [];
      this.fileList.push(file);
    },
    onChange2(file, fileList) {
      this.fileList2 = [];
      this.fileList2.push(file);
    },
    onChange3(file, fileList) {
      this.fileList3 = [];
      this.fileList3.push(file);
    },
    handleSimpleSearch(){
      this.isLoading3=true;
      this.getSimpleData();
    },
    handleSubmit() {
      if (this.extendSwitch0 === true) {
        if (typeof this.currentRow === 'undefined') {
          this.$message.error("尚未选择大纲");
          return;
        }
        this.uploadParam.outline_id = this.currentRow.outlineId;
        this.uploadParam.courseIds = [];
        if (this.extendSwitch) {
          let length = this.simpleMultipleSelection.length;
          if (length === 0) {
            this.$message.error("尚未选择课程");
            return;
          }
          for (let i = 0; i < length; i++) {
            this.uploadParam.courseIds.push(this.simpleMultipleSelection[i].courseId);
          }
        } else {
          this.uploadParam.courseIds.push(this.curCourseId);
        }
        addAssignOutline(JSON.stringify(this.uploadParam)).then(
            res => {
              this.$message.success("绑定成功");
              this.getData();
            }
        );
      } else {
        if (this.fileList.length === 0) {
          this.$message.error("尚未选择文件");
          return;
        }
        let length = this.simpleMultipleSelection.length;
        if (this.enName === "") {
          this.$message.error("请输入英文名")
          return;
        }
        if (this.version === "") {
          this.$message.error("请输入年份/版本")
          return;
        }
        if (this.extendSwitch&&length === 0) {
          this.$message.error("请选择绑定课程")
          return;
        }

        let formData = new FormData();
        formData.append('uploadFile', this.fileList[0].raw);
        formData.append('enName', this.enName);
        formData.append('year', this.version);
        if (this.extendSwitch) {
          let ids = "";
          if (length > 0) {
            ids = this.simpleMultipleSelection[0].courseId;
          }
          for (let i = 1; i < length; i++) {
            ids += "-" + this.simpleMultipleSelection[i].courseId;
          }
          formData.append('courseIds', ids);
        } else {
          formData.append('courseIds', this.curCourseId);
        }
        this.$message.info("正在上传,请等待.......");
        addOutline(formData).then(res => {
          this.handleCancel()
          this.$message.success("上传成功")
          this.extendSwitch = false;
          this.simpleQuery.code = "";
          this.simpleQuery.year = "";
          this.simpleTableData = [];
          this.multipleSelection = [];
          this.getData();
        })
      }
      this.outlineAddVisible = false;
    },
    handleSubmit2() {
        if (this.fileList2.length === 0) {
          this.$message.error("尚未选择文件");
          return;
        }
        let formData = new FormData();
        formData.append('uploadFile', this.fileList2[0].raw);
        this.$message.info("正在上传,请等待.......");
        addAllCourse(formData).then(res => {
          this.handleCancel2()
          this.$message.success("上传成功")
        })
      this.addAllVisible = false;
    },
    handleSubmit3() {
      if (this.fileList3.length === 0) {
        this.$message.error("尚未选择文件");
        return;
      }
      let formData = new FormData();
      formData.append('uploadFile', this.fileList3[0].raw);
      formData.append('year', this.year);
      this.$message.info("正在上传,请等待.......");
      setAllCourse(formData).then(res => {
        this.handleCancel3()
        this.$message.success("上传成功")
      })
      this.setAllVisible = false;
    },
    handleCancel() {
      if (this.extendSwitch0 === false) {
        this.$refs.upload.clearFiles();
        this.enName = "";
      }
      this.outlineAddVisible = false;
    },
    handleCancel2() {
      this.$refs.upload2.clearFiles();
      this.addAllVisible = false;
    },
    handleCancel3() {
      this.$refs.upload3.clearFiles();
      this.setAllVisible = false;
    },
    handleAdd(){
      this.addVisible=true;
    },
    addCourseSubmit(){
      if (this.addParam.code === "" || this.addParam.name === "" || this.addParam.college_name === "" || this.major_name === "") {
        this.$message.info("请输入必要信息");
        return;
      }
      this.addVisible = false;
      addCourse(this.addParam).then(
          res => {
            this.$message.success("添加成功");
            this.getData();
          }, error => {
            this.$message.error("添加失败");
          }
      );
    },
    handleAddAll(){
      this.fileList2 = [];
      this.addAllVisible = true;
    },
    handleSetAll(){
      this.fileList3 = [];
      this.setAllVisible = true;
    },
    handleCheck(courseId) {
      this.checkVisible = true;
      getOldOutlines(courseId).then(
          res => {
            this.resultObj = res.data;
            this.oldOutlineTableData = this.resultObj.data;
          }
      );
    }
  },
};
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.float {
  float: right;
}

.handle-select {
  width: 140px;
}

.handle-select-s {
  width: 70px;
}

.handle-select-m {
  width: 100px;
}

.handle-input {
  width: 120px;
  display: inline-block;
}

.handle-input2 {
  width: 60%;
  display: inline-block;
}

.handle-input-m {
  width: 100px;
  display: inline-block;
}

.handle-input-s {
  width: 70px;
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

.table-td-thumb {
  display: block;
  margin: auto;
  width: 40px;
  height: 40px;
}
</style>
