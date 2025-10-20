<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px" v-if="!isStudent">
      <el-form-item label="" prop="stuCode">
        <el-input v-model="queryParams.stuCode" placeholder="请输入学号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="stuName">
        <el-input v-model="queryParams.stuName" placeholder="请输入学生姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="stuSex">
        <el-select v-model="queryParams.stuSex" placeholder="请选择性别" clearable>
          <el-option v-for="dict in dict.type.student_sex" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="stuPhone">
        <el-input v-model="queryParams.stuPhone" placeholder="请输入联系方式" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="stuMajor">
        <el-input v-model="queryParams.stuMajor" placeholder="请输入专业" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="fId">
        <el-select v-model="queryParams.fId" placeholder="请选择宿舍楼" @change="parentSelect('querySelect')" clearable>
          <!-- <el-option v-for="dict in dict.type.sys_dorm_type" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
          <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="dorId">
        <el-select v-model="queryParams.dorId" placeholder="请选择宿舍号" @change="childSelect('querySelect')" clearable>
          <!-- <el-option v-for="dict in dict.type.sys_level" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
          <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName" :value="item.dorId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="stuStatus">
        <el-select v-model="queryParams.stuStatus" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.student_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5" v-if="!isStudent && !isDormManager">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['dormitory:student:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5" v-if="!isStudent && !isDormManager">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['dormitory:student:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5" v-if="!isStudent && !isDormManager">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['dormitory:student:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5" v-if="!isStudent && !isDormManager">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:student:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="studentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" width="55" label="序号" align="center" />
      <!-- <el-table-column label="学生ID" align="center" prop="stuId" /> -->
      <el-table-column label="照片" align="center" prop="stuPhoto" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.stuPhoto" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="学号" align="center" prop="stuCode" />
      <el-table-column label="姓名" align="center" prop="stuName" />
      <!-- <el-table-column label="年龄" align="center" prop="stuAge" /> -->
      <el-table-column label="性别" align="center" prop="stuSex">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.student_sex" :value="scope.row.stuSex" />
        </template>
      </el-table-column>
      <el-table-column label="电话" align="center" width="110" prop="stuPhone" />
      <el-table-column label="专业" align="center" prop="stuMajor" />
      <!-- <el-table-column label="宿舍楼" align="center" prop="fId">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_dorm_type" :value="scope.row.fId" />
        </template>
      </el-table-column> -->
      <el-table-column label="宿舍" align="center" prop="dormDormitory.dorName">
        <!-- <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_level" :value="scope.row.dorId" />
        </template> -->
      </el-table-column>
      <el-table-column label="状态" align="center" prop="stuStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.student_status" :value="scope.row.stuStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250">
        <template slot-scope="scope">
          <el-button v-if="(!isStudent && !isDormManager) || (isStudent && scope.row.stuCode === currentUser.user.userName)" size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['dormitory:student:edit']">修改</el-button>
          <el-button v-if="!isStudent && !isDormManager" size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['dormitory:student:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改学生信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form :inline="true" ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生照片" prop="stuPhoto">
          <image-upload v-model="form.stuPhoto" />
        </el-form-item>
        <el-form-item label="学生编号" prop="stuCode">
          <el-input v-model="form.stuCode" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="学生姓名" prop="stuName">
          <el-input v-model="form.stuName" placeholder="请输入学生姓名" />
        </el-form-item>
        <el-form-item label="学生年龄" prop="stuAge">
          <el-input v-model="form.stuAge" placeholder="请输入年龄" />
        </el-form-item>
        <el-form-item label="学生性别" prop="stuSex">
          <el-radio-group v-model="form.stuSex">
            <el-radio v-for="dict in dict.type.student_sex" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="联系方式" prop="stuPhone">
          <el-input v-model="form.stuPhone" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="宿舍楼" prop="fId">
          <el-select v-model="form.fId" placeholder="请选择宿舍楼" @change="parentSelect('addEditSelect')">
            <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="学生专业" prop="stuMajor">
          <el-input v-model="form.stuMajor" placeholder="请输入专业" />
        </el-form-item>

        <el-form-item label="宿舍号" prop="dorId">
          <el-select v-model="form.dorId" placeholder="请选择宿舍号" @change="childSelect('addEditSelect')">
            <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName"
              :value="item.dorId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="学生状态" prop="stuStatus">
          <el-select v-model="form.stuStatus" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.student_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStudent, getStudent, delStudent, addStudent, updateStudent } from "@/api/dormitory/student";
import { listFloor } from "@/api/dormitory/floor";
import { listDorm } from "@/api/dormitory/dorm";

export default {
  name: "Student",
  dicts: ['dorm_type', 'student_sex', 'sys_level', 'student_status'],
  data () {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 学生信息表格数据
      studentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 楼层信息选项
      floorOptions: [],
      // 宿舍信息选项
      dormOptions: [],
      // 是否为学生角色
      isStudent: false,
      // 是否为宿管角色
      isDormManager: false,
      // 当前用户信息
      currentUser: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        stuCode: null,
        stuName: null,
        stuSex: null,
        stuPhone: null,
        stuMajor: null,
        fId: null,
        dorId: null,
        stuStatus: null,
      },
      //选择宿舍楼参数
      selectParams: {
        fId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        stuCode: [
          { required: true, message: "学号不能为空", trigger: "blur" }
        ],
        stuStatus: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ],
      }
    };
  },
  created () {
    this.checkUserRole();
    this.getList();
    this.getAllFloorList();
    // this.getAllDormList();
  },
  methods: {
    // 检查用户角色
    checkUserRole() {
      // 获取当前用户信息
      this.currentUser = this.$store.state.user;
      // 检查是否为学生角色
      if (this.currentUser && this.currentUser.roles) {
        this.isStudent = this.currentUser.roles.some(role => role.roleKey === 'student');
        this.isDormManager = this.currentUser.roles.some(role => role.roleKey === 'man');
      }
      // 如果是学生，设置查询参数只查询自己的信息
      if (this.isStudent && this.currentUser.user) {
        this.queryParams.stuCode = this.currentUser.user.userName;
      }
    },
    //父类选择器
    parentSelect: function (param) {
      if (param === 'querySelect') {
        console.log("这是搜索select")
        this.selectParams.fId = this.queryParams.fId
      }
      if (param === 'addEditSelect') {
        console.log("这是添加或者编辑select")
        this.selectParams.fId = this.form.fId
      }
      console.log("选择参数-fId:" + this.selectParams.fId);
      listDorm(this.selectParams).then(response => {
        this.dormOptions = response.rows;
        this.form.dorId = response.rows.dorId
      })
    },
    //子类选择器
    childSelect: function (param) {
      if (param === 'querySelect') {
        console.log(this.queryParams.dorId)
      }
      if (param === 'addEditSelect') {
        console.log(this.form.dorId)
      }
    },
    /** 查询学生信息列表 */
    getList () {
      this.loading = true;
      listStudent(this.queryParams).then(response => {
        this.studentList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    //获取所有楼层信息列表
    getAllFloorList () {
      listFloor().then(response => {
        this.floorOptions = response.rows;
        // console.log(response.rows[0].fId)
      })
    },
    //获取所有宿舍信息列表
    getAllDormList () {
      listDorm(this.queryParams).then(response => {
        this.dormOptions = response.rows;
      })
    },
    // 取消按钮
    cancel () {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset () {
      this.form = {
        stuId: null,
        stuCode: null,
        stuName: null,
        stuAge: null,
        stuSex: null,
        stuPhoto: null,
        stuPhone: null,
        stuMajor: null,
        fId: null,
        dorId: null,
        stuStatus: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery () {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery () {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange (selection) {
      this.ids = selection.map(item => item.stuId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      this.open = true;
      this.title = "添加学生信息";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const stuId = row.stuId || this.ids
      // 如果是学生角色，只能修改自己的信息
      if (this.isStudent && row && row.stuCode !== this.currentUser.user.userName) {
        this.$modal.msgError("您只能修改自己的信息");
        return;
      }
      getStudent(stuId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改学生信息";
        this.selectParams.fId = this.form.fId
      });
      listDorm(this.selectParams).then(response => {
        this.dormOptions = response.rows;
      })
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.stuId != null) {
            updateStudent(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addStudent(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete (row) {
      const stuIds = row.stuId || this.ids;
      this.$modal.confirm('是否确认删除该学生信息？').then(function () {
        return delStudent(stuIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/student/export', {
        ...this.queryParams
      }, `student_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
