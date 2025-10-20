<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="" prop="fName">
        <el-input v-model="queryParams.fName" placeholder="请输入宿舍楼名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="" prop="fNumber">
        <el-input v-model="queryParams.fNumber" placeholder="请输入楼层" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="" prop="fType">
        <el-select v-model="queryParams.fType" placeholder="请选择宿舍楼类型" clearable>
          <el-option v-for="dict in dict.type.dorm_floor_type" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['dormitory:floor:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['dormitory:floor:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['dormitory:floor:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:floor:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="floorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" width="55" label="序号" align="center" />
      <!-- <el-table-column label="宿舍楼ID" align="center" prop="fId" /> -->
      <el-table-column label="宿舍楼名" align="center" prop="fName" />
      <el-table-column label="楼层数" align="center" prop="fNumber" />
      <el-table-column label="类型" align="center" prop="fType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dorm_floor_type" :value="scope.row.fType" />
        </template>
      </el-table-column>
      <el-table-column label="宿舍总数" align="center" prop="fDormnumber" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250">
        <template slot-scope="scope">
          <el-button size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['dormitory:floor:edit']">修改</el-button>
          <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['dormitory:floor:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改宿舍楼管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="300px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="宿舍楼名" prop="fName">
          <el-input v-model="form.fName" placeholder="请输入宿舍楼名" />
        </el-form-item>
        <el-form-item label="楼层数" prop="fNumber">
          <el-input v-model="form.fNumber" placeholder="请输入楼层数" />
        </el-form-item>
        <el-form-item label="宿舍类型" prop="fType">
          <el-select v-model="form.fType" placeholder="请选择宿舍楼类型">
            <el-option v-for="dict in dict.type.dorm_floor_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍总数" prop="fDormnumber">
          <el-input v-model="form.fDormnumber" placeholder="请输入宿舍总数" />
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
import { listFloor, getFloor, delFloor, addFloor, updateFloor } from "@/api/dormitory/floor";

export default {
  name: "Floor",
  dicts: ['dorm_floor_type'],
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
      // 宿舍楼管理表格数据
      floorList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fName: null,
        fNumber: null,
        fType: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fName: [
          { required: true, message: "宿舍楼号不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created () {
    this.getList();
  },
  methods: {
    /** 查询宿舍楼管理列表 */
    getList () {
      this.loading = true;
      listFloor(this.queryParams).then(response => {
        this.floorList = response.rows;
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
        fId: null,
        fName: null,
        fNumber: null,
        fType: null,
        fDormnumber: null,
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
      this.ids = selection.map(item => item.fId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      this.open = true;
      this.title = "添加宿舍楼";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const fId = row.fId || this.ids
      getFloor(fId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改宿舍楼";
      });
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.fId != null) {
            updateFloor(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addFloor(this.form).then(response => {
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
      const fIds = row.fId || this.ids;
      this.$modal.confirm('是否确认删除该宿舍楼的信息？').then(function () {
        return delFloor(fIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/floor/export', {
        ...this.queryParams
      }, `floor_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
