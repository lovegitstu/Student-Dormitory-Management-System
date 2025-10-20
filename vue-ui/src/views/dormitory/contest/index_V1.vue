<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="" prop="conDatetime">
        <el-date-picker clearable v-model="queryParams.conDatetime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择评比时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="" prop="dorId">
        <el-select v-model="queryParams.dorId" placeholder="请选择宿舍" clearable>
          <!-- <el-option v-for="dict in dict.type.sys_dorm_type" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
          <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName" :value="item.dorId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="conUser">
        <el-input v-model="queryParams.conUser" placeholder="请输入评分人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="conDesignation">
        <el-select v-model="queryParams.conDesignation" placeholder="请选择评比情况" clearable>
          <el-option v-for="dict in dict.type.dormitory_designation" :key="dict.value" :label="dict.label"
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
          v-hasPermi="['dormitory:contest:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['dormitory:contest:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['dormitory:contest:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:contest:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="contestList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" width="55" align="center" label="序号" />
      <!-- <el-table-column label="ID" align="center" prop="conId" /> -->
      <el-table-column label="评比时间" align="center" prop="conDatetime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.conDatetime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="评比宿舍" align="center" prop="dormDormitory.dorName" />
      <el-table-column label="床铺评分(20分)" align="center" prop="conBed" width="80" />
      <el-table-column label="地面评分(20分)" align="center" prop="conFloor" width="80" />
      <el-table-column label="墙壁是否乱画(20分)" align="center" prop="conWell" width="100" />
      <el-table-column label="厕所卫生(20分)" align="center" prop="conToilet" width="80" />
      <el-table-column label="物品摆放(20分)" align="center" prop="conGoods" width="80" />
      <el-table-column label="总分" align="center" prop="conTotal" />
      <el-table-column label="评分人" align="center" prop="conUser" />
      <el-table-column label="评比情况" align="center" prop="conDesignation">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dormitory_designation" :value="scope.row.conDesignation" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="备注" align="center" prop="remark" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right" width="200">
        <template slot-scope="scope">
          <el-button size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['dormitory:contest:edit']">修改</el-button>
          <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['dormitory:contest:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改宿舍评分对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">

        <el-row>
          <el-col :span="12">
            <el-form-item label="评比宿舍" prop="dorId">
              <el-select v-model="form.dorId" placeholder="请选择宿舍">
                <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName"
                  :value="item.dorId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评比时间" prop="conDatetime">
              <el-date-picker clearable v-model="form.conDatetime" type="date" value-format="yyyy-MM-dd"
                placeholder="请选择评比时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="床铺评分" prop="conBed">
              <el-input v-model="form.conBed" placeholder="满分20分" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地面评分" prop="conFloor">
              <el-input v-model="form.conFloor" placeholder="满分20分" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="墙壁乱画" prop="conWell">
              <el-input v-model="form.conWell" placeholder="满分20分" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="厕所卫生" prop="conToilet">
              <el-input v-model="form.conToilet" placeholder="满分20分" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="物品摆放" prop="conGoods">
              <el-input v-model="form.conGoods" placeholder="满分20分" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评比总分" prop="conTotal">
              <el-input v-model="form.conTotal" :disabled="true" placeholder="满分100分" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="评比情况" prop="conDesignation">
              <el-select v-model="form.conDesignation" placeholder="请选择评比情况">
                <el-option v-for="dict in dict.type.dormitory_designation" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评分人" prop="conUser">
              <el-input v-model="form.conUser" placeholder="请输入评分人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listContest, getContest, delContest, addContest, updateContest } from "@/api/dormitory/contest";
import { listDorm } from "@/api/dormitory/dorm";

export default {
  name: "Contest",
  dicts: ['dormitory_designation', 'sys_dorm_type'],
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
      // 宿舍评分表格数据
      contestList: [],
      //
      dormOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        conDatetime: null,
        dorId: null,
        conUser: null,
        conDesignation: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        dorId: [
          { required: true, message: "宿舍不能为空", trigger: "change" },
        ],
        // conBed: [
        //   { max: 100, message: '年龄必须为数字值' }
        // ],
      }
    };
  },
  created () {
    this.getList();
    this.getAllDormList();
  },
  methods: {
    /** 查询宿舍评分列表 */
    getList () {
      this.loading = true;
      listContest(this.queryParams).then(response => {
        this.contestList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getAllDormList () {
      listDorm().then(response => {
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
        conId: null,
        conDatetime: null,
        dorId: null,
        conBed: null,
        conFloor: null,
        conWell: null,
        conToilet: null,
        conGoods: null,
        conTotal: null,
        conUser: null,
        conDesignation: null,
        remark: null
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
      this.ids = selection.map(item => item.conId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      this.open = true;
      this.title = "添加宿舍评分";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const conId = row.conId || this.ids
      getContest(conId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改宿舍评分";
      });
    },
    /** 提交按钮 */
    submitForm () {
      this.form.conTotal = this.form.conBed + this.form.conFloor + this.form.conGoods + this.form.conToilet + this.form.conWell;
      this.$refs["form"].validate(valid => {
        if (valid) {
          // this.form.conTotal = this.form.conBed + this.form.conFloor + this.form.conGoods + this.form.conToilet + this.form.conWell;
          // if (90 <= this.form.conTotal <= 100) {
          //   this.form.conDesignation = "1";
          // }
          if (this.form.conId != null) {
            updateContest(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addContest(this.form).then(response => {
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
      const conIds = row.conId || this.ids;
      this.$modal.confirm('确定删除该宿舍评比信息吗？').then(function () {
        return delContest(conIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/contest/export', {
        ...this.queryParams
      }, `contest_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
