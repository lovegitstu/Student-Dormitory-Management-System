<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="" prop="visName">
        <el-input v-model="queryParams.visName" placeholder="请输入来访人员" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="联系方式" prop="visPhone">
        <el-input v-model="queryParams.visPhone" placeholder="请输入联系方式" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="" prop="visType">
        <el-select v-model="queryParams.visType" placeholder="请选择来访类型" clearable>
          <el-option v-for="dict in dict.type.visit_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
        <el-form-item v-if="!isStudentRole" label="" prop="visInterviewee">
          <el-input v-model="queryParams.visInterviewee" placeholder="请输入被访人" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item v-else label="" prop="visInterviewee">
          <el-input v-model="queryParams.visInterviewee" placeholder="被访人" disabled />
        </el-form-item>
        <el-form-item v-if="!isStudentRole" label="" prop="fId">
          <el-select v-model="queryParams.fId" placeholder="请选择宿舍楼" @change="parentSelect('querySelect')" clearable>
            <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isStudentRole" label="" prop="dorId">
          <el-select v-model="queryParams.dorId" placeholder="请选择来访宿舍" @change="childSelect('querySelect')" clearable>
            <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName" :value="item.dorId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isStudentRole" label="" prop="approvalStatus">
          <el-select v-model="queryParams.approvalStatus" placeholder="请选择审批状态" clearable>
            <el-option label="待审批" value="0" />
            <el-option label="已通过" value="1" />
            <el-option label="已拒绝" value="2" />
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
          v-hasPermi="['dormitory:visit:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['dormitory:visit:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['dormitory:visit:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:visit:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="visitList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" width="55" label="序号" align="center" />
      <!-- <el-table-column label="来访ID" align="center" prop="visId" /> -->
      <el-table-column label="来访人员" align="center" prop="visName" />
      <el-table-column label="联系方式" align="center" prop="visPhone" width="110" />
      <el-table-column label="来访类型" align="center" prop="visType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.visit_type" :value="scope.row.visType" />
        </template>
      </el-table-column>
      <el-table-column label="被访人" align="center" prop="visInterviewee" />
      <el-table-column label="来访事由" align="center" prop="visCause" width="180" />
      <el-table-column label="宿舍楼" align="center" prop="dormFloor.fName" />
      <el-table-column label="来访宿舍" align="center" prop="dormDormitory.dorName" />
      <el-table-column label="审批状态" align="center" prop="approvalStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.approvalStatus === '0'" type="warning">待审批</el-tag>
          <el-tag v-else-if="scope.row.approvalStatus === '1'" type="success">已通过</el-tag>
          <el-tag v-else-if="scope.row.approvalStatus === '2'" type="danger">已拒绝</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审批时间" align="center" prop="approvalTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.approvalTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="300">
        <template slot-scope="scope">
          <el-button v-if="scope.row.approvalStatus === '0'" size="small" type="primary" icon="el-icon-check" 
            @click="handleApprove(scope.row, '1')" v-hasPermi="['dormitory:visit:approve']">通过</el-button>
          <el-button v-if="scope.row.approvalStatus === '0'" size="small" type="danger" icon="el-icon-close" 
            @click="handleApprove(scope.row, '2')" v-hasPermi="['dormitory:visit:approve']">拒绝</el-button>
          <el-button size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['dormitory:visit:edit']">修改</el-button>
          <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['dormitory:visit:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改来访人员登记对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="350px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="来访人员" prop="visName">
          <el-input v-model="form.visName" placeholder="请输入来访人员" />
        </el-form-item>
        <el-form-item label="联系方式" prop="visPhone">
          <el-input v-model="form.visPhone" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="来访类型" prop="visType">
          <el-select v-model="form.visType" placeholder="请选择来访类型">
            <el-option v-for="dict in dict.type.visit_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍楼" prop="fId">
          <el-select v-model="form.fId" placeholder="请选择宿舍楼" @change="parentSelect('addEditSelect')" disabled>
            <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="来访宿舍" prop="dorId">
          <el-select v-model="form.dorId" placeholder="请选择来访宿舍" @change="childSelect('addEditSelect')" disabled>
            <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName"
              :value="item.dorId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="被访人" prop="visInterviewee">
          <el-input v-model="form.visInterviewee" placeholder="请输入被访人" disabled />
        </el-form-item>
        <el-form-item label="来访时间" prop="visDatetime">
          <el-date-picker clearable v-model="form.visDatetime" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择来访时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="来访事由" prop="visCause">
          <el-input v-model="form.visCause" type="textarea" placeholder="请输入内容" />
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
import { listVisit, getVisit, delVisit, addVisit, updateVisit, approveVisit } from "@/api/dormitory/visit";
import { listFloor } from "@/api/dormitory/floor";
import { listDorm } from "@/api/dormitory/dorm";
import { getStudentByUserId } from "@/api/dormitory/student";

export default {
  name: "Visit",
  dicts: ['visit_type', 'sys_level', 'repair_status'],
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
      // 来访人员登记表格数据
      visitList: [],
      floorOptions: [],
      dormOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        visName: null,
        visPhone: null,
        visType: null,
        visInterviewee: null,
        fId: null,
        dorId: null,
        visDatetime: null,
        approvalStatus: null,
        stuId: null
      },
      //选择宿舍楼参数
      selectParams: {
        fId: null,
      },
      // 表单参数
      form: {},
      // 当前用户的学生信息
      currentStudentInfo: null,
      // 表单校验
      rules: {
      }
    };
  },
  computed: {
    isStudentRole () {
      const roles = (this.$store && this.$store.state && this.$store.state.user && this.$store.state.user.roles) || [];
      return roles.includes('student');
    }
  },
  created () {
    this.initializePage();
  },
  methods: {
    async initializePage () {
      if (this.isStudentRole) {
        await this.getCurrentUserStudentInfo();
      }
      this.getAllFloorList();
      this.getList();
    },
    // 获取当前用户的学生信息
    getCurrentUserStudentInfo() {
      const currentUserId = this.$store.state.user.userId;
      if (!currentUserId) {
        return Promise.resolve();
      }
      return getStudentByUserId(currentUserId).then(response => {
        if (response.data) {
          this.currentStudentInfo = response.data;
          this.queryParams.visInterviewee = response.data.stuName || '';
          this.queryParams.stuId = response.data.stuId || null;
          if (response.data.fId) {
            this.queryParams.fId = response.data.fId;
          }
          if (response.data.dorId) {
            this.queryParams.dorId = response.data.dorId;
          }
        }
      }).catch(error => {
        console.error('获取当前用户学生信息失败:', error);
      });
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
        // console.log(response.rows[0].fId)
      })
    },
    /** 查询来访人员登记列表 */
    getList () {
      if (this.isStudentRole && this.currentStudentInfo) {
        this.queryParams.visInterviewee = this.currentStudentInfo.stuName;
        this.queryParams.stuId = this.currentStudentInfo.stuId;
      }
      this.loading = true;
      listVisit(this.queryParams).then(response => {
        this.visitList = response.rows;
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
        visId: null,
        visName: null,
        visPhone: null,
        visType: null,
        visInterviewee: null,
        visCause: null,
        fId: null,
        dorId: null,
        visDatetime: null,
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
      if (this.isStudentRole && this.currentStudentInfo) {
        this.queryParams.visInterviewee = this.currentStudentInfo.stuName;
        this.queryParams.fId = this.currentStudentInfo.fId;
        this.queryParams.dorId = this.currentStudentInfo.dorId;
        this.queryParams.stuId = this.currentStudentInfo.stuId;
      }
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange (selection) {
      this.ids = selection.map(item => item.visId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      
      // 自动填充当前用户的宿舍信息
      if (this.currentStudentInfo) {
        this.form.fId = this.currentStudentInfo.fId;
        this.form.dorId = this.currentStudentInfo.dorId;
        
        // 自动获取宿舍列表
        if (this.currentStudentInfo.fId) {
          this.selectParams.fId = this.currentStudentInfo.fId;
          listDorm(this.selectParams).then(response => {
            this.dormOptions = response.rows;
          });
        }
        
        // 自动填充被访人信息（当前用户姓名）
        if (this.currentStudentInfo.stuName) {
          this.form.visInterviewee = this.currentStudentInfo.stuName;
        }
      }
      
      this.open = true;
      this.title = "添加来访人员登记";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const visId = row.visId || this.ids
      getVisit(visId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改来访人员登记";
        this.selectParams.fId = this.form.fId
      });
      listDorm(this.selectParams).then(response => {
        this.dormOptions = response.rows;
      })
    },
    /** 审批按钮操作 */
    handleApprove (row, status) {
      const statusText = status === '1' ? '通过' : '拒绝';
      this.$modal.prompt(`请输入${statusText}意见`, "审批确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        inputPlaceholder: `请输入${statusText}意见`,
        inputType: 'textarea'
      }).then(({ value }) => {
        const approvalData = {
          visId: row.visId,
          approvalStatus: status,
          approvalOpinion: value || `${statusText}访客申请`
        };
        
        // 调用审批API
         approveVisit(approvalData).then(response => {
           if (response.code === 200) {
             this.$modal.msgSuccess(`${statusText}成功`);
             this.getList();
           } else {
             this.$modal.msgError(response.msg || `${statusText}失败`);
           }
         }).catch(() => {
           this.$modal.msgError(`${statusText}失败`);
         });
      }).catch(() => {
        this.$modal.msgInfo("已取消审批");
      });
    },
     /** 提交按钮 */
     submitForm () {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.visId != null) {
              updateVisit(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addVisit(this.form).then(response => {
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
      const visIds = row.visId || this.ids;
      this.$modal.confirm('是否确认删除该访客记录？').then(function () {
        return delVisit(visIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/visit/export', {
        ...this.queryParams
      }, `visit_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
