<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="" prop="conDatetime">
        <el-date-picker clearable v-model="queryParams.conDatetime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择评比时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="" prop="fId">
        <el-select v-model="queryParams.fId" placeholder="请选择宿舍楼" @change="parentSelect('querySelect')" clearable>
          <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="dorId">
        <el-select v-model="queryParams.dorId" placeholder="请选择宿舍" @change="childSelect('querySelect')" clearable>
          <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName" :value="item.dorId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="conUser">
        <el-input v-model="queryParams.conUser" placeholder="请输入评分人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="conDesignation">
        <el-select v-model="queryParams.conDesignation" placeholder="请选择评比情况" clearable>
          <el-option v-for="dict in dict.type.contest_designation" :key="dict.value" :label="dict.label"
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
      <el-table-column type="index" width="55" label="序号" align="center" />
      <!-- <el-table-column label="ID" align="center" prop="conId" /> -->
      <el-table-column label="评比时间" align="center" prop="conDatetime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.conDatetime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="宿舍楼" align="center" prop="dormFloor.fName" /> -->
      <el-table-column label="宿舍" align="center" prop="dormDormitory.dorName" />
      <el-table-column label="床铺评分(20分)" align="center" prop="conBed" width="80" />
      <el-table-column label="地面评分(20分)" align="center" prop="conFloor" width="80" />
      <el-table-column label="墙壁情况(20分)" align="center" prop="conWell" width="80" />
      <el-table-column label="厕所卫生(20分)" align="center" prop="conToilet" width="80" />
      <el-table-column label="物品摆放(20分)" align="center" prop="conGoods" width="80" />
      <el-table-column label="总分" align="center" prop="conTotal" />
      <el-table-column label="评分人" align="center" prop="conUser" />
      <el-table-column label="评比情况" align="center" prop="conDesignation">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.contest_designation" :value="scope.row.conDesignation" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250">
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
            <el-form-item label="宿舍楼" prop="fId">
              <el-select v-model="form.fId" placeholder="请选择宿舍楼" @change="parentSelect('addEditSelect')">
                <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评比宿舍" prop="dorId">
              <el-select v-model="form.dorId" placeholder="请选择宿舍" @change="childSelect('addEditSelect')">
                <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName"
                  :value="item.dorId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="床铺评分" prop="conBed">
              <el-input v-model="form.conBed" :min="min" :max="max" type="number" placeholder="满分20分" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地面评分" prop="conFloor">
              <el-input v-model="form.conFloor" :min="min" :max="max" type="number" placeholder="满分20分" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="墙壁整洁评分" prop="conWell">
              <el-input v-model="form.conWell" :min="min" :max="max" type="number" placeholder="满分20分" />
            </el-form-item>

          </el-col>
          <el-col :span="12">
            <el-form-item label="厕所卫生" prop="conToilet">
              <el-input v-model="form.conToilet" :min="min" :max="max" type="number" placeholder="满分20分" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="物品摆放" prop="conGoods">
              <el-input v-model="form.conGoods" :min="min" :max="max" type="number" placeholder="满分20分" />
            </el-form-item>

          </el-col>
          <el-col :span="12">
            <el-form-item label="评比总分" prop="conTotal">
              <el-input v-model="form.conTotal" placeholder="满分100分" :disabled="isDisabled" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">

            <el-form-item label="评分人" prop="conUser">
              <el-input v-model="form.conUser" placeholder="当前登录用户" :disabled="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评比情况" prop="conDesignation">
              <el-select v-model="form.conDesignation" placeholder="请选择评比情况" :disabled="true">
                <el-option v-for="dict in dict.type.contest_designation" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="评比时间" prop="conDatetime">
              <el-date-picker clearable v-model="form.conDatetime" type="date" value-format="yyyy-MM-dd"
                placeholder="请选择评比时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">

          </el-col>
        </el-row>

        <el-row>
          <el-col :span="13">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
          <el-col :span="11">

          </el-col>
        </el-row>
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
import { listFloor } from "@/api/dormitory/floor";

export default {
  name: "Contest",
  dicts: ['contest_designation', 'dorm_type', 'student_status'],
  data () {
    return {
      min: 0,
      max: 20,
      //选择宿舍楼
      floorOptions: [],
      //选择所有宿舍
      dormOptions: [],
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
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //是否禁用输入框
      isDisabled: true,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        conDatetime: null,
        fId: null,
        dorId: null,
        conUser: null,
        conDesignation: null,
      },
      //选择宿舍楼参数
      selectParams: {
        fId: null,
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

  created () {
    this.getList();
    this.getAllFloorList();
  },
  methods: {
    getDormList (fId) {
      this.selectParams.fId = fId;
      listDorm(this.form).then(response => {
        this.dormOptions = response.rows;
      })
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
    //获取所有楼层信息列表
    getAllFloorList () {
      listFloor().then(response => {
        this.floorOptions = response.rows;
      })
    },
    //获取所有宿舍信息列表
    getAllDormList () {
      listDorm().then(response => {
        this.dormOptions = response.rows;
      })
    },
    /** 查询宿舍评分列表 */
    getList () {
      this.loading = true;
      listContest(this.queryParams).then(response => {
        this.contestList = response.rows;
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
        conId: null,
        conDatetime: null,
        fId: null,
        dorId: null,
        conBed: null,
        conFloor: null,
        conWell: null,
        conToilet: null,
        conGoods: null,
        conTotal: null,
        conUser: this.$store.state.user.name, // 自动设置为当前登录用户
        conDesignation: null,
        remark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
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
        console.log("修改：" + this.form.fId)
        this.selectParams.fId = this.form.fId
      });
      listDorm(this.selectParams).then(response => {
        this.dormOptions = response.rows;
      })
    },
    /** 提交按钮 */
    submitForm () {
      // let conBed = parseInt(this.form.conBed);
      // let conFloor = parseInt(this.form.conFloor);
      // let conGoods = parseInt(this.form.conGoods);
      // let conToilet = parseInt(this.form.conToilet);
      // let conWell = parseInt(this.form.conWell);
      // let totalPoints = conBed + conFloor + conGoods + conToilet + conWell;
      // this.form.conTotal = totalPoints
      // if (90 <= totalPoints <= 100) {
      //   this.form.conDesignation = "1";
      // }
      // else if (80 <= totalPoints < 90) {
      //   this.form.conDesignation = "2";
      // }
      // else if (60 <= totalPoints < 80) {
      //   this.form.conDesignation = "3";
      // }
      // else if (totalPoints < 60) {
      //   this.form.conDesignation = "4";
      // }
      this.$refs["form"].validate(valid => {
        if (valid) {
          let conBed = parseInt(this.form.conBed);
          let conFloor = parseInt(this.form.conFloor);
          let conGoods = parseInt(this.form.conGoods);
          let conToilet = parseInt(this.form.conToilet);
          let conWell = parseInt(this.form.conWell);
          let totalPoints = conBed + conFloor + conGoods + conToilet + conWell;
          this.form.conTotal = totalPoints
          if (totalPoints >= 90 && totalPoints <= 100) {
            this.form.conDesignation = "1";
          }
          if (totalPoints >= 80 && totalPoints < 90) {
            this.form.conDesignation = "2";
          }
          if (totalPoints >= 60 && totalPoints < 80) {
            this.form.conDesignation = "3";
          }
          if (totalPoints < 60) {
            this.form.conDesignation = "4";
          }
          console.log(totalPoints)
          console.log(this.form.conDesignation)
          if (this.form.conId != null) {
            updateContest(this.form).then(response => {
              console.log(this.form)
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
      this.$modal.confirm('是否确认删除该宿舍的评分项？').then(function () {
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
