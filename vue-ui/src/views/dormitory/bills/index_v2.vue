<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="宿舍楼" prop="fId">
        <el-select v-model="queryParams.fId" placeholder="请选择宿舍楼" clearable>
          <el-option
            v-for="dict in dict.type.sys_student_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="宿舍" prop="dorId">
        <el-select v-model="queryParams.dorId" placeholder="请选择宿舍" clearable>
          <el-option
            v-for="dict in dict.type.sys_repairs_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="费用时间" prop="ubDatetime">
        <el-date-picker clearable
          v-model="queryParams.ubDatetime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择费用时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="抄表/核算人" prop="ubMeterReading">
        <el-input
          v-model="queryParams.ubMeterReading"
          placeholder="请输入抄表/核算人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="ubPhone">
        <el-input
          v-model="queryParams.ubPhone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['dormitory:bills:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['dormitory:bills:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['dormitory:bills:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dormitory:bills:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="billsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="ubId" />
      <el-table-column label="宿舍楼" align="center" prop="fId">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_student_status" :value="scope.row.fId"/>
        </template>
      </el-table-column>
      <el-table-column label="宿舍" align="center" prop="dorId">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_repairs_status" :value="scope.row.dorId"/>
        </template>
      </el-table-column>
      <el-table-column label="费用时间" align="center" prop="ubDatetime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.ubDatetime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="水费(元)" align="center" prop="ubWater" />
      <el-table-column label="单位(吨)" align="center" prop="ubWaterMonad" />
      <el-table-column label="电费(元)" align="center" prop="ubElectricityRate" />
      <el-table-column label="电单位(度)" align="center" prop="ubElectricityUnits" />
      <el-table-column label="用水量" align="center" prop="ubWaterConsumption" />
      <el-table-column label="用电量" align="center" prop="ubElectricityConsumption" />
      <el-table-column label="总费用" align="center" prop="ubTotalCost" />
      <el-table-column label="描述" align="center" prop="remark" />
      <el-table-column label="抄表/核算人" align="center" prop="ubMeterReading" />
      <el-table-column label="联系电话" align="center" prop="ubPhone" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dormitory:bills:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dormitory:bills:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改水电费管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="宿舍楼" prop="fId">
          <el-select v-model="form.fId" placeholder="请选择宿舍楼">
            <el-option
              v-for="dict in dict.type.sys_student_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍" prop="dorId">
          <el-select v-model="form.dorId" placeholder="请选择宿舍">
            <el-option
              v-for="dict in dict.type.sys_repairs_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="费用时间" prop="ubDatetime">
          <el-date-picker clearable
            v-model="form.ubDatetime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择费用时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="水费(元)" prop="ubWater">
          <el-input v-model="form.ubWater" placeholder="请输入水费(元)" />
        </el-form-item>
        <el-form-item label="单位(吨)" prop="ubWaterMonad">
          <el-input v-model="form.ubWaterMonad" placeholder="请输入单位(吨)" />
        </el-form-item>
        <el-form-item label="电费(元)" prop="ubElectricityRate">
          <el-input v-model="form.ubElectricityRate" placeholder="请输入电费(元)" />
        </el-form-item>
        <el-form-item label="电单位(度)" prop="ubElectricityUnits">
          <el-input v-model="form.ubElectricityUnits" placeholder="请输入电单位(度)" />
        </el-form-item>
        <el-form-item label="用水量" prop="ubWaterConsumption">
          <el-input v-model="form.ubWaterConsumption" placeholder="请输入用水量" />
        </el-form-item>
        <el-form-item label="用电量" prop="ubElectricityConsumption">
          <el-input v-model="form.ubElectricityConsumption" placeholder="请输入用电量" />
        </el-form-item>
        <el-form-item label="总费用" prop="ubTotalCost">
          <el-input v-model="form.ubTotalCost" placeholder="请输入总费用" />
        </el-form-item>
        <el-form-item label="描述" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="抄表/核算人" prop="ubMeterReading">
          <el-input v-model="form.ubMeterReading" placeholder="请输入抄表/核算人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="ubPhone">
          <el-input v-model="form.ubPhone" placeholder="请输入联系电话" />
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
import { listBills, getBills, delBills, addBills, updateBills } from "@/api/dormitory/bills";

export default {
  name: "Bills",
  dicts: ['sys_repairs_status', 'sys_student_status'],
  data() {
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
      // 水电费管理表格数据
      billsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fId: null,
        dorId: null,
        ubDatetime: null,
        ubMeterReading: null,
        ubPhone: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fId: [
          { required: true, message: "宿舍楼不能为空", trigger: "change" }
        ],
        dorId: [
          { required: true, message: "宿舍不能为空", trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询水电费管理列表 */
    getList() {
      this.loading = true;
      listBills(this.queryParams).then(response => {
        this.billsList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        ubId: null,
        fId: null,
        dorId: null,
        ubDatetime: null,
        ubWater: null,
        ubWaterMonad: null,
        ubElectricityRate: null,
        ubElectricityUnits: null,
        ubWaterConsumption: null,
        ubElectricityConsumption: null,
        ubTotalCost: null,
        remark: null,
        ubMeterReading: null,
        ubPhone: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.ubId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加水电费管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const ubId = row.ubId || this.ids
      getBills(ubId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改水电费管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.ubId != null) {
            updateBills(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addBills(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ubIds = row.ubId || this.ids;
      this.$modal.confirm('是否确认删除水电费管理编号为"' + ubIds + '"的数据项？').then(function() {
        return delBills(ubIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('dormitory/bills/export', {
        ...this.queryParams
      }, `bills_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
