<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="" prop="fId">
        <el-select v-model="queryParams.fId" placeholder="请选择宿舍楼" @change="parentSelect('querySelect')" clearable>
          <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="dorId">
        <el-select v-model="queryParams.dorId" placeholder="请选择维修宿舍" @change="childSelect('querySelect')" clearable>
          <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName" :value="item.dorId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="repairContentType">
        <el-select v-model="queryParams.repairContentType" placeholder="请选择维修类型" clearable>
          <el-option v-for="dict in dict.type.repair_content_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="description">
        <el-input v-model="queryParams.description" placeholder="请输入维修内容描述" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="repairStatus">
        <el-select v-model="queryParams.repairStatus" placeholder="请选择维修状态" clearable>
          <el-option v-for="dict in dict.type.repair_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="" prop="repairDatetime">
        <el-date-picker clearable v-model="queryParams.repairDatetime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择维修时间">
        </el-date-picker>
      </el-form-item> -->
      <!-- <el-form-item label="" prop="nickName">
        <el-input v-model="queryParams.nickName" placeholder="请输入报修人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入联系方式" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="" prop="repairGrade">
        <el-select v-model="queryParams.repairGrade" placeholder="请选择维修级别" clearable>
          <el-option v-for="dict in dict.type.repair_grade" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['dormitory:repair:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['dormitory:repair:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['dormitory:repair:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:repair:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="repairList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" width="55" label="序号" align="center" />
      <!-- <el-table-column label="ID" align="center" prop="repairId" /> -->
      <el-table-column label="宿舍楼" align="center" prop="dormFloor.fName" />
      <el-table-column label="维修宿舍" align="center" prop="dormDormitory.dorName" />
      <el-table-column label="维修类型" align="center" prop="repairContentType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.repair_content_type" :value="scope.row.repairContentType" />
        </template>
      </el-table-column>
      <el-table-column label="维修内容描述" align="center" prop="description" />
      <el-table-column label="维修状态" align="center" prop="repairStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.repair_status" :value="scope.row.repairStatus" />
        </template>
      </el-table-column>
      <el-table-column label="维修时间" align="center" prop="repairDatetime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.repairDatetime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报修人" align="center" prop="nickName" />
      <el-table-column label="联系方式" align="center" prop="phone" width="110" />
      <el-table-column label="描述" align="center" prop="remark" width="200" />
      <el-table-column label="维修级别" align="center" prop="repairGrade">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.repair_grade" :value="scope.row.repairGrade" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="320">
        <template slot-scope="scope">
          <div style="display: flex; flex-wrap: wrap; gap: 8px; justify-content: center; align-items: center; padding: 6px 0;">
            <el-button size="small" type="warning" icon="el-icon-s-check" @click="handleApprove(scope.row)"
              v-hasPermi="['dormitory:repair:approve']" v-hasRole="['man', 'admin']" v-if="scope.row.repairStatus !== '2'"
              style="margin: 0; min-width: 75px;">处理</el-button>
            <el-button v-if="scope.row.repairStatus === '1'" 
              size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              v-hasPermi="['dormitory:repair:edit']"
              style="margin: 0; min-width: 75px;">修改</el-button>
            <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
              v-hasPermi="['dormitory:repair:remove']"
              style="margin: 0; min-width: 75px;">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改维修工单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="660px" append-to-body>
      <el-form ref="form" :inline="false" :model="form" :rules="rules" label-width="80px">

        <el-row>
          <el-col :span="12">
            <el-form-item label="宿舍楼" prop="fId">
              <el-select v-model="form.fId" placeholder="当前用户宿舍楼" disabled>
                <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维修宿舍" prop="dorId">
              <el-select v-model="form.dorId" placeholder="当前用户宿舍" disabled>
                <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName"
                  :value="item.dorId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="维修类型" prop="repairContentType">
              <el-select v-model="form.repairContentType" placeholder="请选择维修类型">
                <el-option v-for="dict in dict.type.repair_content_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维修级别" prop="repairGrade">
              <el-select v-model="form.repairGrade" placeholder="请选择维修级别">
                <el-option v-for="dict in dict.type.repair_grade" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="维修内容描述" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入维修内容描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="维修时间" prop="repairDatetime">
              <el-date-picker clearable v-model="form.repairDatetime" type="date" value-format="yyyy-MM-dd"
                placeholder="请选择维修时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报修人" prop="nickName">
              <el-input v-model="form.nickName" placeholder="当前登录用户" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系方式" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- <el-form-item label="维修状态" prop="repairStatus">
              <el-select v-model="form.repairStatus" placeholder="请选择维修状态">
                <el-option v-for="dict in dict.type.repair_status" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item> -->
            <el-form-item label="描述" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- <el-form-item label="描述" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item> -->

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRepair, getRepair, delRepair, addRepair, updateRepair, approveRepair } from "@/api/dormitory/repair";
import { listFloor } from "@/api/dormitory/floor";
import { listDorm } from "@/api/dormitory/dorm";

export default {
  name: "Repair",
  dicts: ['repair_grade', 'repair_status', 'repair_content_type'],
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
      // 维修工单表格数据
      repairList: [],
      floorOptions: [],
      dormOptions: [],
      // crruentRoles: this.$store.state.user.roles[0],
      // crruentUser: this.$store.state.user.name,
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
        repairContentType: null,
        description: null,
        repairStatus: null,
        repairDatetime: null,
        nickName: null,
        phone: null,
        repairGrade: null,
      },
      //选择宿舍楼参数
      selectParams: {
        fId: null,
      },
      // 表单参数
      form: {},
      jobData: {},
      // 表单校验
      rules: {
        fId: [
          { required: true, message: "宿舍楼不能为空", trigger: "change" }
        ],
        dorId: [
          { required: true, message: "维修宿舍不能为空", trigger: "change" }
        ],
        repairContentType: [
          { required: true, message: "维修内容类型不能为空", trigger: "change" }
        ],
        description: [
          { required: true, message: "维修内容描述不能为空", trigger: "blur" },
          { min: 2, max: 200, message: "维修内容描述长度在 2 到 200 个字符", trigger: "blur" }
        ],
        // repairStatus: [
        //   { required: true, message: "维修状态不能为空", trigger: "change" }
        // ],
        repairGrade: [
          { required: true, message: "维修级别不能为空", trigger: "change" }
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
    /** 查询维修工单列表 */
    getList () {
      this.loading = true;
      listRepair(this.queryParams).then(response => {
        this.repairList = response.rows;
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
        repairId: null,
        fId: this.$store.state.user.fId || null,
        dorId: this.$store.state.user.dorId || null,
        repairContentType: null,
        description: null,
        repairStatus: null,
        repairDatetime: null,
        nickName: this.$store.state.user.name || null,
        phone: null,
        remark: null,
        repairGrade: null,
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
      this.ids = selection.map(item => item.repairId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      this.open = true;
      this.title = "添加维修工单";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const repairId = row.repairId || this.ids
      getRepair(repairId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改维修工单";
        this.selectParams.fId = this.form.fId
      });
      listDorm(this.selectParams).then(response => {
        this.dormOptions = response.rows;
      })
    },
    //处理报修工单
    handleApprove (row) {
      this.$modal.confirm('确认处理该维修工单吗？').then(() => {
        const repairData = {
          repairId: row.repairId,
          repairStatus: '2' // 设置为已处理状态
        };
        approveRepair(repairData).then(response => {
          this.$modal.msgSuccess("处理成功");
          this.getList();
        });
      }).catch(() => {});
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        // this.form.repairStatus = '1'
        if (valid) {
          if (this.form.repairId != null) {
            updateRepair(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
            console.log(this.form)
          } else {
            this.form.repairStatus = '1'
            addRepair(this.form).then(response => {
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
      const repairIds = row.repairId || this.ids;
      this.$modal.confirm('是否确认删除该维修工单？').then(function () {
        return delRepair(repairIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/repair/export', {
        ...this.queryParams
      }, `repair_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
