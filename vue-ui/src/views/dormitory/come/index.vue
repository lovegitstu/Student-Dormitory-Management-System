<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
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
      <el-form-item label="" prop="stuName">
        <el-input v-model="queryParams.stuName" placeholder="请输入学生姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="datetime">
        <el-date-picker clearable v-model="queryParams.datetime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择回校时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="" prop="opinion">
        <el-select v-model="queryParams.opinion" placeholder="审批状态" clearable>
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
          v-hasPermi="['dormitory:come:add']" v-hasRole="['admin', 'subadmin', 'student']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button size="small" type="success" icon="el-icon-edit" @click="handleUpdate" :disabled="single"
          v-hasPermi="['dormitory:come:edit']" v-hasRole="['admin', 'subadmin', 'student']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete" :disabled="multiple"
          v-hasPermi="['dormitory:come:remove']" v-hasRole="['admin', 'subadmin', 'student']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:come:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" border :data="comeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" label="序号" width="55" align="center" />
      <!-- <el-table-column label="id" align="center" prop="id" /> -->
      <!-- <el-table-column label="宿舍楼" align="center" prop="dormFloor.fName" /> -->
      <el-table-column label="宿舍号" align="center" prop="dormDormitory.dorName" />
      <el-table-column label="学生姓名" align="center" prop="stuName" />
      <el-table-column label="回校时间" align="center" prop="datetime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.datetime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="回校原因" align="center" prop="comeType" />
      <el-table-column label="详细说明" align="center" prop="cause" />
      <el-table-column label="审批状态" align="center" prop="opinion">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.approval_status" :value="scope.row.opinion" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="350">
        <template slot-scope="scope">
          <!-- 审批按钮 - 仅在待审批状态显示 -->
          <el-button v-if="scope.row.opinion === '0' || scope.row.opinion === null || scope.row.opinion === ''" size="small" type="success" icon="el-icon-check" 
            @click="handleApprove(scope.row, '1')" v-hasPermi="['dormitory:come:approve']" 
            v-hasRole="['admin', 'subadmin', 'man']">通过</el-button>
          <el-button v-if="scope.row.opinion === '0' || scope.row.opinion === null || scope.row.opinion === ''" size="small" type="danger" icon="el-icon-close" 
            @click="handleApprove(scope.row, '2')" v-hasPermi="['dormitory:come:approve']" 
            v-hasRole="['admin', 'subadmin', 'man']">拒绝</el-button>
          <!-- 调试信息 -->
          <span v-if="scope.row.opinion === '0' || scope.row.opinion === null || scope.row.opinion === ''" style="font-size: 12px; color: #999;">
            [调试] opinion: {{scope.row.opinion}}, 当前角色: {{currentRole}}
          </span>
          
          <el-button v-if="scope.row.opinion === '0' || scope.row.opinion === null || scope.row.opinion === ''" 
            size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['dormitory:come:edit']" v-hasRole="['admin', 'subadmin', 'student']">修改</el-button>
          <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['dormitory:come:remove']" v-hasRole="['admin', 'subadmin', 'student']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改回校申请对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="350px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="宿舍楼" prop="fId">
          <el-select v-model="form.fId" placeholder="请选择宿舍楼" @change="parentSelect('addEditSelect')" :disabled="disabled || currentRole === 'student'">
            <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍号" prop="dorId">
          <el-select v-model="form.dorId" placeholder="请选择宿舍" @change="childSelect('addEditSelect')" :disabled="disabled || currentRole === 'student'">
            <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName"
              :value="item.dorId"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="学生id" prop="stuId">
          <el-input v-model="form.stuId" placeholder="请输入学生id" />
        </el-form-item> -->
        <el-form-item label="学生姓名" prop="stuName">
          <el-input v-model="form.stuName" placeholder="请输入学生姓名" :disabled="disabled || currentRole === 'student'" />
        </el-form-item>
        <el-form-item label="回校时间" prop="datetime">
          <el-date-picker clearable v-model="form.datetime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择回校时间" :disabled="disabled">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="回校原因" prop="comeType">
          <el-input v-model="form.comeType" placeholder="请输入回校原因" :disabled="disabled" />
        </el-form-item>
        <el-form-item label="详细说明" prop="cause">
          <el-input v-model="form.cause" type="textarea" placeholder="请输入详细说明" />
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
import { listCome, getCome, delCome, addCome, updateCome, approveCome } from "@/api/dormitory/come";
import { listDorm } from "@/api/dormitory/dorm";
import { listFloor } from "@/api/dormitory/floor";
import { getInfo } from "@/api/login"

export default {
  name: "Come",
  dicts: ['approval_status'],
  data () {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      disabled: false,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 回校申请表格数据
      comeList: [],
      floorOptions: [],
      //宿舍号选择数据
      dormOptions: [],
      // 弹出层标题
      title: "",
      //当前用户角色
      currentRole: this.$store.state.user.roles[0],
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fId: null,
        dorId: null,
        stuName: null,
        datetime: null,
        opinion: null,
        comeType: null
      },
      // 表单参数
      form: {},
      //选择宿舍楼参数
      selectParams: {
        fId: null,
      },
      // 表单校验
      rules: {
        fId: [
          { required: true, message: "宿舍楼不能为空", trigger: "change" }
        ],
        dorId: [
          { required: true, message: "宿舍号不能为空", trigger: "change" }
        ],
        stuName: [
          { required: true, message: "学生姓名不能为空", trigger: "blur" }
        ],
        datetime: [
          { required: true, message: "回校时间不能为空", trigger: "blur" }
        ],
        cause: [
          { required: true, message: "原因不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created () {
    this.getList();
    this.getAllFloorList();
    // this.getAllDormList();
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
    getAllDormList () {
      listDorm().then(response => {
        this.dormOptions = response.rows
      })
    },
    getAllFloorList () {
      listFloor().then(response => {
        this.floorOptions = response.rows
      })
    },
    /** 查询回校申请列表 */
    getList () {
      this.loading = true;
      getInfo().then(response => {
        this.queryParams.dorId = response.user.dorId
        listCome(this.queryParams).then(response => {
          this.comeList = response.rows;
          this.total = response.total;
          this.loading = false;
          
          // 添加调试信息
          console.log('回校申请列表数据:', response.rows);
          console.log('当前用户角色:', this.currentRole);
          console.log('用户权限:', this.$store.getters.permissions);
          console.log('用户角色列表:', this.$store.getters.roles);
        });
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
        id: null,
        fId: null,
        dorId: null,
        stuId: null,
        stuName: null,
        datetime: null,
        cause: null,
        opinion: null,
        comeType: null
      };
      this.disabled = false;
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      // 自动获取当前登录学生信息
      this.loadCurrentUserInfo();
      this.open = true;
      this.title = "添加回校申请";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const id = row.id || this.ids
      getCome(id).then(response => {
        this.form = response.data;
        
        // 如果有宿舍楼信息，设置宿舍楼选项并选中
        if (this.form.fId) {
          this.selectParams.fId = this.form.fId;
          listDorm(this.selectParams).then(dormResponse => {
            this.dormOptions = dormResponse.rows;
          });
        }
        
        if (this.currentRole == 'teacher' || this.currentRole == 'admin' || this.currentRole == 'sysadmin') {
          this.disabled = true
          this.open = true;
          this.title = "辅导员审核"
          return;
        }
        // this.form = response.data;
        this.open = true;
        this.title = "修改回校申请";
      });
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCome(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCome(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除该回校申请？').then(function () {
        return delCome(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/come/export', {
        ...this.queryParams
      }, `come_${new Date().getTime()}.xlsx`)
    },
    /** 加载当前用户信息 */
    loadCurrentUserInfo () {
      // 获取当前登录用户信息
      getInfo().then(response => {
        const user = response.user;
        if (user) {
          // 设置学生姓名
          this.form.stuName = user.nickName || user.userName;
          // 设置宿舍楼ID
          if (user.fId) {
            this.form.fId = user.fId;
            // 根据宿舍楼ID获取宿舍列表
            this.selectParams.fId = user.fId;
            listDorm(this.selectParams).then(dormResponse => {
              this.dormOptions = dormResponse.rows;
              // 设置宿舍号
              if (user.dorId) {
                this.form.dorId = user.dorId;
              }
            });
          }
        }
      }).catch(error => {
        console.error('获取用户信息失败:', error);
      });
    },
    /** 审批回校申请 */
    handleApprove(row, status) {
      console.log('审批按钮点击 - 行数据:', row);
      console.log('审批状态:', status);
      console.log('当前用户角色:', this.currentRole);
      console.log('用户权限:', this.$store.getters.permissions);
      
      const statusText = status === '1' ? '通过' : '拒绝';
      this.$modal.confirm(`是否确认${statusText}该回校申请？`).then(() => {
        return this.approveCome(row.id, status);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(`审批${statusText}成功`);
      }).catch(() => {});
    },
    /** 调用审批接口 */
    approveCome(id, status) {
      return approveCome({
        id: id,
        opinion: status
      });
    }
  }
};
</script>
