<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="" prop="dorId">
        <el-select v-model="queryParams.dorId" placeholder="请选择宿舍" clearable>
          <!-- <el-option v-for="dict in dict.type.sys_dorm_type" :key="dict.value" :label="dict.label" :value="dict.value" /> -->
          <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName" :value="item.dorId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="bedCode">
        <el-input v-model="queryParams.bedCode" placeholder="请输入床位编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="bedStatus">
        <el-select v-model="queryParams.bedStatus" placeholder="请选择床位状态" clearable>
          <el-option v-for="dict in dict.type.bed_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="" prop="bedRecordId">
        <el-select v-model="queryParams.bedRecordId" placeholder="请选择床位记录id" clearable>
          <el-option
            v-for="dict in dict.type.sys_bed_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['dormitory:bed:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['dormitory:bed:edit']" v-hasRole="['admin', 'subadmin']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['dormitory:bed:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:bed:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="bedList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" width="55" lable="序号" align="center" />
      <!-- <el-table-column label="床位ID" align="center" prop="bedId" /> -->
      <el-table-column label="宿舍" align="center" prop="dormDormitory.dorName">
        <!-- <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_dorm_type" :value="scope.row.dorId" />
        </template> -->
      </el-table-column>
      <el-table-column label="床位编号" align="center" prop="bedCode" />
      <el-table-column label="床位状态" align="center" prop="bedStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bed_status" :value="scope.row.bedStatus" />
        </template>
      </el-table-column>
      <el-table-column label="入住人" align="center" prop="stuName" />
      <!-- <el-table-column label="备注" align="center" prop="remark" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button size="small" type="warning" icon="el-icon-right" v-hasPermi="['dormitory:bed:edit']"
            @click="handlecheckIn(scope.row)" v-hasRole="['student']">入住</el-button>
          <el-button size="small" type="danger" icon="el-icon-remove" v-hasPermi="['dormitory:bed:edit']"
            @click="handlecheckOut(scope.row)" v-hasRole="['student', 'subadmin']">退宿</el-button>
          <el-button size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['dormitory:bed:edit']" v-hasRole="['admin', 'subadmin']">修改</el-button>
          <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['dormitory:bed:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改床位管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="宿舍" prop="dorId">
          <el-select v-model="form.dorId" :disabled="disabledDorm" placeholder="请选择宿舍">
            <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName"
              :value="item.dorId"></el-option>
            <!-- <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName" :value="item.dorId"></el-option> -->
          </el-select>
        </el-form-item>
        <el-form-item label="床位编号" prop="bedCode">
          <el-input v-model="form.bedCode" placeholder="请输入床位编号" />
        </el-form-item>
        <el-form-item label="床位状态" prop="bedStatus">
          <el-radio-group v-model="form.bedStatus">
            <el-radio v-for="dict in dict.type.bed_status" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- <el-form-item label="床位记录id" prop="bedRecordId">
          <el-select v-model="form.bedRecordId" placeholder="请选择床位记录id">
            <el-option v-for="dict in dict.type.sys_bed_status" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item> -->
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
import { listBed, getBed, delBed, addBed, updateBed } from "@/api/dormitory/bed";
import { listDorm, getDorm } from "@/api/dormitory/dorm";
import { getUserProfile, updateUserProfile } from "@/api/system/user";
import { getStudentByUserId } from "@/api/dormitory/student";

export default {
  name: "Bed",
  dicts: ['dorm_type', 'bed_status'],
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
      // 床位管理表格数据
      bedList: [],
      dormOptions: [],
      btnIsDisabled: false,
      userData: {
        dorName: null,
        nickName: null,
        email: null,
        phonenumber: null
      },
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //
      disabledDorm: false,
      //当前用户
      crruentUser: this.$store.state.user.nickName,
      //当前用户ID
      crruentUserId: this.$store.state.user.userId,
      //当前角色
      crruentRole: this.$store.state.user.roles[0],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dorId: null,
        bedCode: null,
        bedStatus: null,
        bedRecordId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    const dorId = this.$route.params && this.$route.params.dorId;

    // 关键修改：只有当dorId是有效值（不是 undefined 或 字符串 'undefined'）时，才把它作为查询参数
    if (dorId && dorId !== 'undefined') {
      this.queryParams.dorId = dorId;
    }

    // 统一调用 getList 方法来加载列表数据
    this.getList();

    // 这行代码是用来获取所有宿舍列表（用于搜索栏的下拉选择），需要保留
    this.getAllDormList();
  },
  methods: {
    getDormitory (dorId) {
      getDorm(dorId).then(response => {
        this.queryParams.dorId = response.data.dorId;
        this.getList();
      })
    },

    /** 查询床位管理列表 */
    getList () {
      this.loading = true;
      listBed(this.queryParams).then(response => {
        this.bedList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    //获取所有宿舍
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
        bedId: null,
        dorId: null,
        stuId: null,
        stuName: null,
        bedCode: null,
        bedStatus: null,
        bedRecordId: null,
        createTime: null,
        updateTime: null,
        createBy: null,
        updateBy: null,
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
      this.ids = selection.map(item => item.bedId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      const dormId = this.$route.params && this.$route.params.dorId;

      // 关键修改：只有当 dormId 是一个有效的ID时，才去请求宿舍信息
      if (dormId && dormId !== 'undefined') {
        getDorm(dormId).then(response => {
          // 预先填充表单中的宿舍ID
          this.form.dorId = response.data.dorId;
        });
      }

      // 这两行代码移到判断之后，确保弹窗总是能打开
      this.open = true;
      this.title = "添加床位";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const bedId = row.bedId || this.ids
      getBed(bedId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改床位";
      });
    },
    //入住
    handlecheckIn (row) {
      this.reset();
      const bedId = row.bedId || this.ids
      getBed(bedId).then(response => {
        this.form = response.data;
        if (this.form.bedStatus == "1") {
          console.log("该床位已有人")
          this.$modal.msgError("抱歉，该床位已被占用，请选择其他空闲床位！");
        }
        else if (this.form.bedStatus == "3") {
          console.log("该床位有其他原因暂时不能住人")
          this.$modal.msgError("该床位暂时不可入住，如有疑问请联系宿舍管理员！");
        }
        else {
          // 先通过用户ID获取学生信息
          getStudentByUserId(this.crruentUserId).then(studentResponse => {
            if (studentResponse.data && studentResponse.data.stuId) {
              this.form.bedStatus = "1";
              this.form.stuId = studentResponse.data.stuId;
              this.form.stuName = studentResponse.data.stuName; // 设置学生姓名
              
              getUserProfile().then(response => {
                this.userData = response.data
                console.log(this.userData)
                updateUserProfile(this.userData).then(response => {
                })
              })
              
              updateBed(this.form).then(response => {
                this.$modal.msgSuccess("入住成功");
                this.getList();
              }).catch(error => {
                console.error("入住失败:", error);
                this.$modal.msgError("入住失败，请稍后重试或联系管理员！");
              });
            } else {
              this.$modal.msgError("未找到对应的学生信息，请联系管理员！");
            }
          }).catch(error => {
            console.error("获取学生信息失败:", error);
            this.$modal.msgError("获取学生信息失败，请联系管理员！");
          });
        }
      }).catch(error => {
        console.error("获取床位信息失败:", error);
        this.$modal.msgError("获取床位信息失败，请稍后重试！");
      });
    },
    //退宿
    handlecheckOut (row) {
      this.reset();
      const bedId = row.bedId || this.ids
      getBed(bedId).then(response => {
        this.form = response.data;
        
        // 如果是管理员，直接允许退宿
        if (this.crruentRole == 'sysadmin') {
          this.form.bedStatus = "0";
          this.form.stuId = null; // 清空学生ID
          this.form.stuName = null; // 清空学生姓名
          updateBed(this.form).then(response => {
            this.$modal.msgSuccess("退宿成功");
            this.getList();
          }).catch(error => {
            console.error("退宿失败:", error);
            this.$modal.msgError("退宿失败，请稍后重试或联系管理员！");
          });
        } else {
          // 非管理员不允许退宿，只有管理员才能进行退宿操作
          this.$modal.msgError("学生不允许自行退宿，请联系管理员进行退宿操作！");
        }
      }).catch(error => {
        console.error("获取床位信息失败:", error);
        this.$modal.msgError("获取床位信息失败，请稍后重试！");
      });
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.bedId != null) {
            updateBed(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addBed(this.form).then(response => {
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
      const bedIds = row.bedId || this.ids;
      this.$modal.confirm("确定要删除该宿舍的床位信息吗？").then(function () {
        return delBed(bedIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/bed/export', {
        ...this.queryParams
      }, `bed_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
