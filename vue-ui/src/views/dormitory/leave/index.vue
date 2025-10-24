<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="" prop="kbDatetime">
        <el-date-picker clearable v-model="queryParams.kbDatetime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择留宿时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="" prop="kbStudentname">
        <el-input v-model="queryParams.kbStudentname" placeholder="请输入留宿学生" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="" prop="kbInstructor">
        <el-input
          v-model="queryParams.kbInstructor"
          placeholder="请输入辅导员"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <el-form-item label="" prop="fId" v-hasRole="['admin', 'subadmin']">
        <el-select v-model="queryParams.fId" placeholder="请选择宿舍楼" @change="parentSelect('querySelect')" clearable>
          <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="dorId" v-hasRole="['subadmin', 'admin']">
        <el-select v-model="queryParams.dorId" placeholder="请选择宿舍" @change="childSelect('querySelect')" clearable>
          <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName" :value="item.dorId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="kbOpinion">
        <el-select v-model="queryParams.kbOpinion" placeholder="审核意见" clearable>
          <el-option v-for="dict in dict.type.approval_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button size="small" type="primary" icon="el-icon-plus" @click="handleAdd"
          v-hasPermi="['dormitory:leave:add']" v-hasRole="['admin', 'subadmin', 'student']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button size="small" type="success" icon="el-icon-edit" @click="handleUpdate" :disabled="single"
          v-hasPermi="['dormitory:leave:edit']" v-hasRole="['admin', 'subadmin', 'student']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete" :disabled="multiple"
          v-hasPermi="['dormitory:leave:remove']" v-hasRole="['admin', 'subadmin', 'student']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:leave:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="leaveList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" width="55" label="序号" align="center" />
      <!-- <el-table-column label="留宿id" align="center" prop="kbId" /> -->

      <el-table-column label="留宿时间" align="center" prop="kbDatetime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.kbDatetime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="留宿学生" align="center" prop="kbStudentname" />
      <el-table-column label="留宿原因" align="center" prop="kbReason" />
      <el-table-column label="宿舍楼" align="center" prop="dormFloor.fName" />
      <el-table-column label="宿舍" align="center" prop="dormDormitory.dorName" />
      <el-table-column label="辅导员" align="center" prop="kbInstructor" />
      <el-table-column label="审核" align="center" prop="kbOpinion">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.approval_status" :value="scope.row.kbOpinion" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="380">
        <template slot-scope="scope">
          <div style="display: flex; flex-wrap: wrap; gap: 8px; justify-content: center; align-items: center; padding: 6px 0;">
            <el-button size="small" type="success" icon="el-icon-check" @click="handleApprove(scope.row, 1)"
              v-hasPermi="['dormitory:leave:approve']" v-hasRole="['admin', 'subadmin', 'man']"
              v-if="scope.row.opinion === 0"
              style="margin: 0; min-width: 75px;">通过</el-button>
            <el-button size="small" type="danger" icon="el-icon-close" @click="handleApprove(scope.row, 2)"
              v-hasPermi="['dormitory:leave:approve']" v-hasRole="['admin', 'subadmin', 'man']"
              v-if="scope.row.opinion === 0"
              style="margin: 0; min-width: 75px;">拒绝</el-button>
            <el-button size="small" type="warning" icon="el-icon-s-check" @click="handleUpdate(scope.row)"
              v-hasPermi="['dormitory:leave:edit']" v-hasRole="['admin', 'subadmin', 'man']"
              style="margin: 0; min-width: 75px;">审核</el-button>
            <el-button v-if="scope.row.opinion === 0" 
              size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              v-hasPermi="['dormitory:leave:edit']" v-hasRole="['admin', 'subadmin', 'student']"
              style="margin: 0; min-width: 75px;">修改</el-button>
            <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
              v-hasPermi="['dormitory:leave:remove']" v-hasRole="['admin', 'subadmin', 'student']"
              style="margin: 0; min-width: 75px;">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改留宿申请对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="340px" append-to-body>
      <el-form ref="form" :inline="false" :model="form" :rules="rules" label-width="80px">

        <el-form-item label="留宿时间" prop="kbDatetime">
          <el-date-picker clearable v-model="form.kbDatetime" type="date" value-format="yyyy-MM-dd" placeholder="请选择留宿时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="留宿学生" prop="kbStudentname">
          <el-input v-model="form.kbStudentname" placeholder="请输入留宿学生" />
        </el-form-item>
        <el-form-item label="宿舍楼层" prop="fId">
          <el-select v-model="form.fId" placeholder="请选择宿舍楼" @change="parentSelect('addEditSelect')">
            <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍编号" prop="dorId">
          <el-select v-model="form.dorId" placeholder="请选择宿舍" @change="childSelect('addEditSelect')">
            <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName"
              :value="item.dorId"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="留宿原因" prop="kbReason">
          <el-input v-model="form.kbReason" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="辅导员" prop="kbInstructor" v-hasRole="['admin', 'subadmin', 'man']">
          <el-input v-model="form.kbInstructor" placeholder="请输入辅导员" />
        </el-form-item>
        <!-- <el-form-item label="审核意见" prop="kbOpinion" v-hasRole="['admin', 'subadmin', 'man']">
          <el-select v-model="form.kbOpinion" placeholder="审核意见">
            <el-option v-for="dict in dict.type.approval_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
          <el-radio-group v-model="form.kbOpinion">
            <el-radio v-for="dict in dict.type.approval_status" :key="dict.value" :label="parseInt(dict.value)">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 审核意见 -->
    <el-dialog :title="title" :visible.sync="open1" width="450" append-to-body>
      <el-form ref="form" :inline="false" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="审核意见" prop="kbOpinion" v-hasRole="['admin', 'subadmin', 'man']">
          <el-radio-group v-model="form.kbOpinion">
            <el-radio v-for="dict in dict.type.approval_status" :key="dict.value" :label="parseInt(dict.value)">{{
              dict.label }}</el-radio>
          </el-radio-group>
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
import { listLeave, getLeave, delLeave, addLeave, updateLeave } from "@/api/dormitory/leave";
import { listDorm } from "@/api/dormitory/dorm";
import { listFloor } from "@/api/dormitory/floor";
import { getInfo } from "@/api/login"

export default {
  name: "Leave",
  dicts: ['sys_yes_no', 'dorm_status', 'approval_status'],
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
      // 留宿申请表格数据
      leaveList: [],
      //宿舍楼选择数据
      floorOptions: [],
      //宿舍号选择数据
      dormOptions: [],
      //当前用户角色
      currentRole: this.$store.state.user.roles[0],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      open1: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        kbReason: null,
        kbDatetime: null,
        kbStudentname: null,
        kbInstructor: null,
        fId: null,
        dorId: null,
        kbOpinion: null,
      },
      // 表单参数
      form: {},
      //选择宿舍楼参数
      selectParams: {
        fId: null,
      },
      // 表单校验
      rules: {
        kbReason: [
          { required: true, message: "留宿原因不能为空", trigger: "blur" }
        ],
        kbDatetime: [
          { required: true, message: "留宿时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created () {
    this.getList();
    this.getAllFloorList();
  },
  methods: {
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
        // console.log(this.queryParams.dorId)
      }
      if (param === 'addEditSelect') {
        // console.log(this.form.dorId)
      }
    },
    /** 查询留宿申请列表 */
    getList () {
      this.loading = true;
      getInfo().then(response => {
        if (this.currentRole == 'student') {
          this.queryParams.dorId = response.user.dorId
        }
        listLeave(this.queryParams).then(response => {
          this.leaveList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      })
      // listLeave(this.queryParams).then(response => {
      //   this.leaveList = response.rows;
      //   this.total = response.total;
      //   this.loading = false;
      // });
    },
    getAllFloorList () {
      listFloor().then(response => {
        this.floorOptions = response.rows;
      })
    },
    // 取消按钮
    cancel () {
      this.open = false;
      this.open1 = false;
      this.reset();
    },
    // 表单重置
    reset () {
      this.form = {
        kbId: null,
        kbReason: null,
        kbDatetime: null,
        kbStudentname: null,
        kbStudentid: null,
        kbInstructor: null,
        fId: null,
        dorId: null,
        kbOpinion: null,
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
      this.ids = selection.map(item => item.kbId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      this.open = true;
      this.title = "添加留宿申请";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const kbId = row.kbId || this.ids
      getLeave(kbId).then(response => {
        this.form = response.data;
        if (!(this.currentRole == 'teacher')) {
          // this.disabled = true
          this.open = true;
          this.title = "修改留宿申请";
          // this.open1 = true;
          // this.title = "辅导员审核"
          return;
        }
        // this.open = true;
        // this.title = "修改留宿申请";
        this.open1 = true;
        this.title = "辅导员审核"
      });
      listDorm(this.selectParams).then(response => {
        this.dormOptions = response.rows;
      })
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.kbId != null) {
            getInfo().then(response => {
              if (this.currentRole == 'teacher' || this.currentRole == 'admin' || this.currentRole == 'sysadmin') {
                this.form.kbInstructor = response.user.nickName
                updateLeave(this.form).then(response => {
                  this.$modal.msgSuccess("修改成功");
                  this.open = false;
                  this.open1 = false;
                  this.getList();
                });
              }
            })
            // updateLeave(this.form).then(response => {
            //   this.$modal.msgSuccess("修改成功");
            //   this.open = false;
            //   this.getList();
            // });
          } else {
            this.form.kbOpinion = '4'
            addLeave(this.form).then(response => {
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
      const kbIds = row.kbId || this.ids;
      this.$modal.confirm('是否确认删除该留宿申请？').then(function () {
        return delLeave(kbIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/leave/export', {
        ...this.queryParams
      }, `leave_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
