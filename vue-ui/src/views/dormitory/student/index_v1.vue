<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="" prop="stuCode">
        <el-input v-model="queryParams.stuCode" placeholder="请输入学号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="" prop="stuName">
        <el-input v-model="queryParams.stuName" placeholder="姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="年龄" prop="stuAge">
        <el-input
          v-model="queryParams.stuAge"
          placeholder="请输入年龄"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <el-form-item label="" prop="stuSex">
        <el-select v-model="queryParams.stuSex" placeholder="性别" clearable>
          <el-option v-for="dict in dict.type.sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="stuPhone">
        <el-input v-model="queryParams.stuPhone" placeholder="联系方式" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="stuMajor">
        <el-input v-model="queryParams.stuMajor" placeholder="专业" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="stuDorm">
        <el-input v-model="queryParams.stuDorm" placeholder="宿舍号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="stuStatus">
        <el-select v-model="queryParams.stuStatus" placeholder="状态" clearable>
          <el-option v-for="dict in dict.type.sys_student_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['dormitory:student:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['dormitory:student:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['dormitory:student:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:student:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="studentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" width="55" label="序号" align="center" />
      <!-- <el-table-column label="学生ID" align="center" prop="stuId" /> -->
      <el-table-column label="学号" align="center" prop="stuCode" />
      <el-table-column label="照片" align="center" prop="stuPhoto">
        <template slot-scope="scope">
          <image-preview :src="scope.row.stuPhoto" :width="60" :height="60" />
        </template>
      </el-table-column>
      <el-table-column label="姓名" align="center" prop="stuName" />
      <!-- <el-table-column label="年龄" align="center" prop="stuAge" /> -->
      <el-table-column label="性别" align="center" prop="stuSex">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_user_sex" :value="scope.row.stuSex" />
        </template>
      </el-table-column>
      <el-table-column label="电话" align="center" prop="stuPhone" width="110" />
      <el-table-column label="专业" align="center" prop="stuMajor" />
      <el-table-column label="宿舍" align="center" prop="stuDorm" />
      <el-table-column label="状态" align="center" prop="stuStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_student_status" :value="scope.row.stuStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right" width="200">
        <template slot-scope="scope">
          <el-button size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['dormitory:student:edit']">修改</el-button>
          <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['dormitory:student:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改学生信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form :inline="true" ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学号" prop="stuCode">
          <el-input v-model="form.stuCode" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="stuName">
          <el-input v-model="form.stuName" placeholder="请输入学生姓名" />
        </el-form-item>
        <el-form-item label="年龄" prop="stuAge">
          <el-input v-model="form.stuAge" placeholder="请输入年龄" />
        </el-form-item>
        <el-form-item label="性别" prop="stuSex">
          <el-radio-group v-model="form.stuSex">
            <el-radio v-for="dict in dict.type.sys_user_sex" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="电话" prop="stuPhone">
          <el-input v-model="form.stuPhone" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="专业" prop="stuMajor">
          <el-input v-model="form.stuMajor" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="照片" prop="stuPhoto">
          <image-upload v-model="form.stuPhoto" />
        </el-form-item>
        <el-form-item label="宿舍" prop="stuDorm">
          <el-input v-model="form.stuDorm" placeholder="请输入宿舍号" />
        </el-form-item>
        <el-form-item label="状态" prop="stuStatus">
          <el-select v-model="form.stuStatus" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.sys_student_status" :key="dict.value" :label="dict.label"
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

export default {
  name: "Student",
  dicts: ['sys_user_sex', 'sys_student_status'],
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
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        stuCode: null,
        stuName: null,
        stuAge: null,
        stuSex: null,
        stuPhoto: null,
        stuPhone: null,
        stuMajor: null,
        stuDorm: null,
        stuStatus: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        stuCode: [
          { required: true, message: "学号不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created () {
    this.getList();
  },
  methods: {
    /** 查询学生信息列表 */
    getList () {
      this.loading = true;
      listStudent(this.queryParams).then(response => {
        this.studentList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
        stuDorm: null,
        stuStatus: null
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
      getStudent(stuId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改学生信息";
      });
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
      this.$modal.confirm('确定删除学生 "' + row.stuName + '" 的信息？').then(function () {
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
