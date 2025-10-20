<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="" prop="dorName">
        <el-input v-model="queryParams.dorName" placeholder="请输入宿舍编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="fId">
        <el-select v-model="queryParams.fId" placeholder="请选择宿舍楼" clearable>
          <!-- <el-option v-for="dict in dict.type.sys_yes_no" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
          <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="dorType">
        <el-select v-model="queryParams.dorType" placeholder="请选择宿舍类型" clearable>
          <el-option v-for="dict in dict.type.dorm_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="dorStatus">
        <el-select v-model="queryParams.dorStatus" placeholder="请选择宿舍状态" clearable>
          <el-option v-for="dict in dict.type.dorm_status" :key="dict.value" :label="dict.label"
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
          v-hasPermi="['dormitory:dorm:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['dormitory:dorm:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['dormitory:dorm:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:dorm:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="dormList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" label="序号" width="55" align="center" />
      <!-- <el-table-column label="宿舍ID" align="center" prop="dorId" /> -->
      <el-table-column label="宿舍楼" align="center" prop="dormFloor.fName">
        <!-- <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.fId" />
        </template> -->
      </el-table-column>
      <el-table-column label="宿舍号" align="center" prop="dorName">
        <template slot-scope="scope">
          <router-link :to="'/dormitory/bed/index/' + scope.row.dorId" class="link-type">
            <!-- <router-link :to="'/Student/bed/'" class="link-type"> -->
            <span>{{ scope.row.dorName }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="宿舍照片" align="center" prop="dorPicture" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.dorPicture" :width="50" :height="50" />
        </template>
      </el-table-column>

      <el-table-column label="宿舍类型" align="center" prop="dorType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dorm_type" :value="scope.row.dorType" />
        </template>
      </el-table-column>
      <el-table-column label="宿舍状态" align="center" prop="dorStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dorm_status" :value="scope.row.dorStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250">
        <template slot-scope="scope">
          <el-button size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['dormitory:dorm:edit']">修改</el-button>
          <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['dormitory:dorm:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改宿舍信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :inline="true" :model="form" :rules="rules" label-width="60px">
        <el-form-item label="照片" prop="dorPicture">
          <image-upload v-model="form.dorPicture" />
        </el-form-item>
        <el-form-item label="楼层" prop="fId">
          <el-select v-model="form.fId" placeholder="请选择宿舍楼">
            <!-- <el-option v-for="dict in dict.type.sys_yes_no" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option> -->
            <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="编号" prop="dorName">
          <el-input v-model="form.dorName" placeholder="请输入宿舍号" />
        </el-form-item>


        <el-form-item label="类型" prop="dorType">
          <el-select v-model="form.dorType" placeholder="请选择宿舍类型">
            <el-option v-for="dict in dict.type.dorm_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="dorStatus">
          <el-select v-model="form.dorStatus" placeholder="请选择宿舍状态">
            <el-option v-for="dict in dict.type.dorm_status" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
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
import { listDorm, getDorm, delDorm, addDorm, updateDorm } from "@/api/dormitory/dorm";
import { listFloor } from "@/api/dormitory/floor";

export default {
  name: "Dorm",
  dicts: ['sys_yes_no', 'dorm_type', 'dorm_status'],
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
      // 宿舍信息表格数据
      dormList: [],
      floorOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dorName: null,
        fId: null,
        dorType: null,
        dorStatus: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        dorName: [
          { required: true, message: "宿舍号不能为空", trigger: "blur" }
        ],
        fId: [
          { required: true, message: "宿舍楼不能为空", trigger: "change" }
        ],
      }
    };
  },
  created () {
    this.getList();
    this.getAllFloorList();
  },
  methods: {
    // 获取所有楼层信息列表
    getAllFloorList () {
      listFloor().then(response => {
        this.floorOptions = response.rows;
      })
    },
    /** 查询宿舍信息列表 */
    getList () {
      this.loading = true;
      listDorm(this.queryParams).then(response => {
        this.dormList = response.rows;
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
        dorId: null,
        dorName: null,
        dorPicture: null,
        fId: null,
        dorType: null,
        dorStatus: null,
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
      this.ids = selection.map(item => item.dorId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      this.open = true;
      this.title = "添加宿舍信息";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const dorId = row.dorId || this.ids
      getDorm(dorId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改宿舍信息";
      });
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.dorId != null) {
            updateDorm(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDorm(this.form).then(response => {
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
      const dorIds = row.dorId || this.ids;
      this.$modal.confirm('是否确认删除该宿舍的信息？').then(function () {
        return delDorm(dorIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/dorm/export', {
        ...this.queryParams
      }, `dorm_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
