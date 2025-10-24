<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
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
      <el-form-item label="" prop="dormName" v-hasRole="['admin', 'subadmin', 'man']">
        <el-input v-model="queryParams.dormName" placeholder="请输入宿舍" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="stuName">
        <el-input v-model="queryParams.stuName" placeholder="请输入学生姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="opinion">
        <el-select v-model="queryParams.opinion" placeholder="审核意见" clearable>
          <el-option v-for="dict in dict.type.approval_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="" prop="dorType">
        <el-select v-model="queryParams.dorType" placeholder="请选择男女生宿舍" clearable>
          <el-option v-for="dict in dict.type.dorm_floor_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button size="small" type="primary" icon="el-icon-plus" @click="handleAdd"
          v-hasPermi="['dormitory:exchange:add']" v-hasRole="['admin', 'subadmin', 'student']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button size="small" type="success" icon="el-icon-edit" @click="handleUpdate" :disabled="single"
          v-hasPermi="['dormitory:exchange:edit']" v-hasRole="['admin', 'subadmin', 'student']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete" :disabled="multiple"
          v-hasPermi="['dormitory:exchange:remove']" v-hasRole="['admin', 'subadmin', 'student']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:exchange:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="exchangeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" label="序号" width="55" align="center" />
      <!-- <el-table-column label="id" align="center" prop="id" /> -->

      <el-table-column label="原宿舍" align="center" prop="dormName" />
      <el-table-column label="原床位" align="center" prop="originalBedId" />
      <el-table-column label="学生姓名" align="center" prop="stuName" />
      <el-table-column label="新宿舍楼" align="center" prop="dormFloor.fName" />
      <el-table-column label="新宿舍号" align="center" prop="dormDormitory.dorName" />
      <el-table-column label="新床位" align="center" prop="bedId" />
      <el-table-column label="原因" align="center" prop="excuse" />
      <el-table-column label="审核意见" align="center" prop="opinion">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.approval_status" :value="scope.row.opinion" />
        </template>
      </el-table-column>

      <!-- <el-table-column label="男女生宿舍" align="center" prop="dorType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dorm_floor_type" :value="scope.row.dorType" />
        </template>
      </el-table-column> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="380">
        <template slot-scope="scope">
          <div style="display: flex; flex-wrap: wrap; gap: 8px; justify-content: center; align-items: center; padding: 6px 0;">
            <el-button size="small" type="success" icon="el-icon-check" @click="handleApprove(scope.row, 1)"
              v-hasPermi="['dormitory:exchange:approve']" v-hasRole="['admin', 'subadmin', 'man']"
              v-if="scope.row.opinion === 0"
              style="margin: 0; min-width: 75px;">通过</el-button>
            <el-button size="small" type="danger" icon="el-icon-close" @click="handleApprove(scope.row, 2)"
              v-hasPermi="['dormitory:exchange:approve']" v-hasRole="['admin', 'subadmin', 'man']"
              v-if="scope.row.opinion === 0"
              style="margin: 0; min-width: 75px;">拒绝</el-button>
            <el-button size="small" type="warning" icon="el-icon-s-check" @click="handleUpdate(scope.row)"
              v-hasPermi="['dormitory:exchange:edit']" v-hasRole="['admin', 'subadmin', 'man']"
              style="margin: 0; min-width: 75px;">审核</el-button>
            <el-button v-if="scope.row.opinion === 0" 
              size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              v-hasPermi="['dormitory:exchange:edit']" v-hasRole="['admin', 'subadmin', 'student']"
              style="margin: 0; min-width: 75px;">修改</el-button>
            <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
              v-hasPermi="['dormitory:exchange:remove']" v-hasRole="['admin', 'subadmin', 'student']"
              style="margin: 0; min-width: 75px;">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改换宿申请对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="340px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生姓名" prop="stuName">
          <el-input v-model="form.stuName" placeholder="请输入学生姓名" :disabled="true" />
        </el-form-item>
        <el-form-item label="原宿舍" prop="dormName">
          <el-input v-model="form.dormName" placeholder="请输入原宿舍" :disabled="true" />
        </el-form-item>
        <el-form-item label="原床位" prop="originalBedId">
          <el-input v-model="form.originalBedId" placeholder="原床位ID" :disabled="true" />
        </el-form-item>
        <!-- <el-form-item label="学生id" prop="stuId">
          <el-input v-model="form.stuId" placeholder="请输入学生id" />
        </el-form-item> -->

        <el-form-item label="新宿舍楼" prop="fId" :disabled="disabled">
          <el-select v-model="form.fId" placeholder="请选择宿舍楼" @change="parentSelect('addEditSelect')" :disabled="disabled">
            <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="新宿舍号" prop="dorId">
          <el-select 
            v-model="form.dorId" 
            placeholder="请选择宿舍" 
            @change="handleDormChange" 
            :disabled="disabled"
            clearable
            filterable>
            <el-option 
              v-for="item in dormOptions" 
              :key="`dorm_${item.dorId}`" 
              :label="`${item.dorName} (ID:${item.dorId})`"
              :value="item.dorId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="床位选择" prop="bedId">
          <el-select v-model="form.bedId" placeholder="请选择床位" clearable :disabled="disabled">
            <el-option
              v-for="item in bedOptions"
              :key="item.bedId"
              :label="item.bedCode"
              :value="item.bedId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="换宿原因" prop="excuse">
          <el-input v-model="form.excuse" type="textarea" placeholder="换宿原因" />
        </el-form-item>
        <el-form-item label="审核意见" prop="opinion" v-hasRole="['admin', 'subadmin', 'man']">
          <el-select v-model="form.opinion" placeholder="审核意见">
            <el-option v-for="dict in dict.type.approval_status" :key="dict.value" :label="dict.label"
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
import { listExchange, getExchange, delExchange, addExchange, updateExchange, approveExchange } from "@/api/dormitory/exchange";
import { listFloor } from "@/api/dormitory/floor";
import { listDorm } from "@/api/dormitory/dorm";
import { listBed, checkStudentBed } from "@/api/dormitory/bed";
import { getInfo } from "@/api/login"
import { getStudentByUserId } from "@/api/dormitory/student";

export default {
  name: "Exchange",
  dicts: ['dorm_type', 'dorm_status', 'dorm_floor_type', 'approval_status'],
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
      disabled: false,
      // 总条数
      total: 0,
      // 换宿申请表格数据
      exchangeList: [],
      //宿舍楼选择数据
      floorOptions: [],
      //宿舍号选择数据
      dormOptions: [],
      // 床位选项
      bedOptions: [],
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
        bedId: null,
        dormName: null,
        stuName: null,
        opinion: null,
        dorType: null,
      },
      // 表单参数
      form: {
        bedId: null,
        originalBedId: null
      },
      //选择宿舍楼参数
      selectParams: {
        fId: null,
      },
      // 表单校验
      rules: {
        fId: [
          { required: true, message: "新宿舍楼不能为空", trigger: "change" }
        ],
        dorId: [
          { required: true, message: "新宿舍号不能为空", trigger: "change" }
        ],
        // stuName: [
        //   { required: true, message: "学生姓名不能为空", trigger: "blur" }
        // ],
        excuse: [
          { required: true, message: "原因不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created () {
    console.log("=== Exchange页面 created钩子开始 ===");
    console.log("当前用户信息:", this.$store.state.user);
    console.log("当前用户角色:", this.$store.state.user.roles);
    console.log("当前用户权限:", this.$store.state.user.permissions);
    
    // 检查具体的换宿申请权限
    const permissions = this.$store.state.user.permissions || [];
    console.log("=== 权限检查详情 ===");
    console.log("是否有dormitory:exchange:add权限:", permissions.includes('dormitory:exchange:add'));
    console.log("是否有dormitory:exchange:edit权限:", permissions.includes('dormitory:exchange:edit'));
    console.log("是否有dormitory:exchange:remove权限:", permissions.includes('dormitory:exchange:remove'));
    console.log("是否有dormitory:exchange:approve权限:", permissions.includes('dormitory:exchange:approve'));
    console.log("是否有*:*:*超级权限:", permissions.includes('*:*:*'));
    
    // 检查角色
    const roles = this.$store.state.user.roles || [];
    console.log("=== 角色检查详情 ===");
    console.log("是否有student角色:", roles.includes('student'));
    console.log("是否有admin角色:", roles.includes('admin'));
    console.log("是否有subadmin角色:", roles.includes('subadmin'));
    console.log("是否有man角色:", roles.includes('man'));
    
    this.getList();
    this.getAllFloorList();
    console.log("=== Exchange页面 created钩子结束 ===");
    // if (currentRole == 'teacher') {
    //   this.disabled = true
    //   title = "辅导员审核"
    // }
    // console.log(this.$store.state.user.roles[0])
  },
  methods: {
    //父类选择器
    parentSelect: function (param) {
      console.log("=== parentSelect 开始 ===");
      console.log("参数:", param);
      console.log("当前form.fId:", this.form.fId);
      console.log("当前form.dorId:", this.form.dorId);
      
      if (param === 'querySelect') {
        console.log("这是搜索select")
        this.selectParams.fId = this.queryParams.fId
        // 清空查询条件中的宿舍号
        this.queryParams.dorId = null;
        console.log("查询模式 - 设置selectParams.fId:", this.selectParams.fId);
      }
      if (param === 'addEditSelect') {
        console.log("这是添加或者编辑select")
        console.log("添加/编辑模式 - 清空前form.dorId:", this.form.dorId);
        this.selectParams.fId = this.form.fId
        // 清空表单中的宿舍号
        this.form.dorId = null;
        console.log("添加/编辑模式 - 清空后form.dorId:", this.form.dorId);
        console.log("添加/编辑模式 - 设置selectParams.fId:", this.selectParams.fId);
      }
      console.log("选择参数-fId:" + this.selectParams.fId);
      
      // 先清空宿舍选项
      console.log("清空前dormOptions长度:", this.dormOptions.length);
      this.dormOptions = [];
      console.log("清空后dormOptions长度:", this.dormOptions.length);
      
      if (this.selectParams.fId) {
        console.log("准备发送API请求 - selectParams:", this.selectParams);
        listDorm(this.selectParams).then(response => {
          console.log("API响应成功 - 完整响应:", response);
          console.log("API响应 - rows数据:", response.rows);
          console.log("API响应 - rows类型:", typeof response.rows);
          console.log("API响应 - rows长度:", response.rows ? response.rows.length : 0);
          if (response.rows && response.rows.length > 0) {
            console.log("API响应 - 第一个宿舍数据:", response.rows[0]);
          }
          this.dormOptions = response.rows || [];
          console.log("设置dormOptions后 - 长度:", this.dormOptions.length);
          console.log("设置dormOptions后 - 内容:", this.dormOptions);
          // 详细显示每个宿舍的信息
          this.dormOptions.forEach((dorm, index) => {
            console.log(`宿舍${index + 1}: ID=${dorm.dorId}, 名称=${dorm.dorName}, 类型=${dorm.dorType}`);
          });
        }).catch(error => {
          console.error("获取宿舍列表失败 - 错误详情:", error);
          this.dormOptions = [];
        })
      } else {
        console.log("selectParams.fId为空，不发送API请求");
      }
      console.log("=== parentSelect 结束 ===");
    },
    //子类选择器
    childSelect: function (param) {
      console.log("=== childSelect 开始 ===");
      console.log("参数:", param);
      console.log("当前form.dorId:", this.form.dorId);
      console.log("当前form.dorId类型:", typeof this.form.dorId);
      console.log("当前dormOptions长度:", this.dormOptions.length);
      console.log("当前dormOptions内容:", this.dormOptions);
      
      // 详细显示dormOptions中每个选项的ID和类型
      this.dormOptions.forEach((dorm, index) => {
        console.log(`选项${index + 1}: dorId=${dorm.dorId} (类型:${typeof dorm.dorId}), dorName=${dorm.dorName}`);
      });
      
      if (param === 'querySelect') {
        console.log("查询选择宿舍号:", this.queryParams.dorId)
      }
      if (param === 'addEditSelect') {
        console.log("添加/编辑选择宿舍号:", this.form.dorId)
        // 确保选择的宿舍号被正确保存
        if (this.form.dorId) {
          console.log("宿舍号选择成功:", this.form.dorId)
          // 可以在这里添加额外的验证逻辑
          const selectedDorm = this.dormOptions.find(dorm => dorm.dorId === this.form.dorId)
          if (selectedDorm) {
            console.log("选择的宿舍信息:", selectedDorm)
            console.log("选择的宿舍名称:", selectedDorm.dorName)
            
            // 使用 Vue.set 确保响应式更新
            console.log("使用Vue.set更新form.dorId");
            this.$set(this.form, 'dorId', this.form.dorId);
            
            // 触发下一个tick的更新
            this.$nextTick(() => {
              console.log("nextTick中的form.dorId:", this.form.dorId);
              // 再次强制更新
              this.$forceUpdate();
            });
          } else {
            console.log("警告：在dormOptions中找不到对应的宿舍信息")
            // 尝试类型转换后再查找
            const selectedDormStr = this.dormOptions.find(dorm => dorm.dorId == this.form.dorId)
            if (selectedDormStr) {
              console.log("类型转换后找到的宿舍信息:", selectedDormStr)
            } else {
              console.log("即使类型转换后也找不到对应的宿舍")
            }
          }
        } else {
          console.log("警告：form.dorId为空")
        }
      }
      console.log("=== childSelect 结束 ===");
    },
    getAllFloorList () {
      listFloor().then(response => {
        this.floorOptions = response.rows;
      })
    },
    /** 查询换宿申请列表 */
    getList () {
      this.loading = true;
      getInfo().then(response => {
        if (this.currentRole == 'student') {
          this.queryParams.dormName = response.user.dorName
        }

        listExchange(this.queryParams).then(response => {
          this.exchangeList = response.rows;
          this.total = response.total;
          this.loading = false;
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
        bedId: null,
        originalBedId: null,
        dormName: null,
        stuId: null,
        stuName: null,
        opinion: null,
        excuse: null,
        dorType: null,
        createBy: null,
        createTime: null,
        updateTime: null,
        updateBy: null
      };
      this.disabled = false; // 重置disabled状态
      // 清空宿舍选项，避免缓存问题
      this.dormOptions = [];
      this.bedOptions = []; // 清空床位选项
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
      console.log("=== handleAdd 开始 ===");
      console.log("点击新增按钮时的用户状态:");
      console.log("- 用户角色:", this.$store.state.user.roles);
      console.log("- 用户权限:", this.$store.state.user.permissions);
      
      // 再次检查权限状态
      const permissions = this.$store.state.user.permissions || [];
      const roles = this.$store.state.user.roles || [];
      
      console.log("=== 新增按钮权限验证 ===");
      console.log("需要权限: dormitory:exchange:add");
      console.log("需要角色: admin, subadmin, student 中的任意一个");
      console.log("当前是否有dormitory:exchange:add权限:", permissions.includes('dormitory:exchange:add'));
      console.log("当前是否有student角色:", roles.includes('student'));
      console.log("当前是否有admin角色:", roles.includes('admin'));
      console.log("当前是否有subadmin角色:", roles.includes('subadmin'));
      
      // 手动验证权限和角色
      const hasPermission = permissions.includes('dormitory:exchange:add') || permissions.includes('*:*:*');
      const hasRole = roles.includes('admin') || roles.includes('subadmin') || roles.includes('student');
      
      console.log("权限验证结果:", hasPermission);
      console.log("角色验证结果:", hasRole);
      console.log("最终验证结果:", hasPermission && hasRole);
      
      if (!hasPermission) {
        console.error("权限验证失败: 缺少dormitory:exchange:add权限");
        this.$modal.msgError("您没有新增换宿申请的权限");
        return;
      }
      
      if (!hasRole) {
        console.error("角色验证失败: 不是admin、subadmin或student角色");
        this.$modal.msgError("您的角色无权进行此操作");
        return;
      }
      
      console.log("权限和角色验证通过，继续执行新增操作");
      
      this.reset();
      console.log("reset后form状态:", this.form);
      console.log("reset后dormOptions长度:", this.dormOptions.length);
      this.disabled = false; // 确保新增时下拉框可用
      
      // 自动获取当前用户的宿舍信息并预填充表单
      getInfo().then(response => {
        console.log("=== 用户信息完整数据 ===", response);
        console.log("用户ID:", response.user.userId);
        console.log("用户昵称:", response.user.nickName);
        console.log("用户名:", response.user.userName);
        console.log("用户角色:", response.roles);
        console.log("用户权限:", response.permissions);
        
        // 首先检查学生是否已分配床位
        console.log("=== 即将调用checkStudentBed API ===");
        console.log("传入参数 - userId:", response.user.userId);
        
        checkStudentBed(response.user.userId).then(bedCheckResponse => {
          console.log("=== checkStudentBed API 返回结果 ===");
          console.log("完整响应:", bedCheckResponse);
          console.log("bedCheckResponse.data:", bedCheckResponse.data);
          console.log("bedCheckResponse.hasAssignedBed:", bedCheckResponse.hasAssignedBed);
          console.log("bedCheckResponse.bedInfo:", bedCheckResponse.bedInfo);
          
          // 修正数据访问路径：hasAssignedBed在根级别，不在data下
          const hasAssignedBed = bedCheckResponse.hasAssignedBed || (bedCheckResponse.data && bedCheckResponse.data.hasAssignedBed);
          console.log("最终判断 hasAssignedBed:", hasAssignedBed);
          
          if (!hasAssignedBed) {
            // 学生未分配床位，不允许申请换宿
            console.log("学生未分配床位，显示错误信息");
            this.$modal.msgError("您尚未分配床位，无法申请换宿。请先联系管理员分配床位。");
            return;
          }
          
          console.log("学生已分配床位，继续获取学生详细信息");
          // 学生已分配床位，允许申请换宿，继续获取学生详细信息
          getStudentByUserId(response.user.userId).then(studentResponse => {
            // 设置学生姓名和宿舍信息
            let stuName = response.user.nickName || response.user.userName;
            let dormName = '未分配宿舍';
            let originalBedId = null;
            
            if (studentResponse.data) {
              // 优先使用学生表中的姓名
              if (studentResponse.data.stuName) {
                stuName = studentResponse.data.stuName;
              }
              
              // 从dormDormitory对象获取宿舍信息
              if (studentResponse.data.dormDormitory && 
                  studentResponse.data.dormDormitory.dorName) {
                const dorm = studentResponse.data.dormDormitory;
                const floor = dorm.dormFloor;
                
                // 构建宿舍名称
                if (floor && floor.fName && dorm.dorName) {
                  dormName = `${floor.fName} - ${dorm.dorName}`;
                } else if (dorm.dorName) {
                  dormName = dorm.dorName;
                }
              }
            }
            
            // 从床位检查结果中获取原床位ID
            const bedInfo = bedCheckResponse.bedInfo || (bedCheckResponse.data && bedCheckResponse.data.bedInfo);
            if (bedInfo && bedInfo.bedId) {
              originalBedId = bedInfo.bedId;
            }
            
            this.form = {
              stuName: stuName,
              stuId: studentResponse.data ? studentResponse.data.stuId : response.user.userId,
              dormName: dormName,
              originalBedId: originalBedId,
              fId: null,
              dorId: null,
              bedId: null,
              excuse: ''
            };
            
            // 只有在学生已分配床位时才打开对话框
            this.open = true;
            this.title = "添加换宿申请";
            
          }).catch(error => {
            console.error("获取学生详细信息失败:", error);
            // 如果获取学生详细信息失败，使用基本信息
            this.form = {
              stuName: response.user.nickName || response.user.userName,
              stuId: response.user.userId, // 这里保持使用userId，因为没有获取到学生信息
              dormName: "未分配宿舍",
              originalBedId: null,
              fId: null,
              dorId: null,
              bedId: null,
              excuse: ''
            };
            
            // 只有在学生已分配床位时才打开对话框
            this.open = true;
            this.title = "添加换宿申请";
          });
          
        }).catch(error => {
          console.error("检查床位分配状态失败:", error);
          this.$modal.msgError("检查床位分配状态失败，请稍后重试！");
        });
        
      }).catch(error => {
        console.error("获取用户信息失败:", error);
        this.$modal.msgError("获取用户信息失败，请稍后重试！");
      });
      
      console.log("=== handleAdd 结束 ===");
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const id = row.id || this.ids
      getExchange(id).then(response => {
        this.form = response.data;
        if (this.currentRole == 'teacher' || this.currentRole == 'admin' || this.currentRole == 'sysadmin') {
          this.disabled = true
          this.open = true;
          this.title = "辅导员审核"
          return;
        }
        this.open = true;
        this.title = "修改换宿申请";
      });
      listDorm(this.selectParams).then(response => {
        this.dormOptions = response.rows;
      })
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateExchange(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            getInfo().then(response => {
              this.form.opinion = 0; // 修改为0（待审核）
              this.form.stuName = response.user.nickName
              this.form.dormName = response.user.dorName
              addExchange(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              });
            })
            // console.log(this.form.stuName)
            // console.log(response.user.dorName)

          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete (row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除换宿申请？').then(function () {
        return delExchange(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 审批按钮操作 */
    handleApprove (row, status) {
      const statusText = status === 1 ? '通过' : '拒绝';
      this.$modal.confirm(`是否确认${statusText}该换宿申请？`).then(() => {
        const approveData = {
          id: row.id,
          opinion: status
        };
        return approveExchange(row.id, approveData);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(`审批${statusText}成功`);
      }).catch(() => { });
    },
    // 宿舍选择改变时，获取对应的空闲床位
    handleDormChange(dorId) {
      this.form.bedId = null; // 清空床位选择
      this.bedOptions = []; // 清空床位选项
      if (dorId) {
        // 获取该宿舍的空闲床位
        this.getBedsByDormId(dorId);
      }
    },
    // 获取指定宿舍的空闲床位
    getBedsByDormId(dorId) {
      console.log("=== getBedsByDormId 开始 ===");
      console.log("宿舍ID:", dorId);
      console.log("查询参数:", { dorId: dorId, bedStatus: '0' });
      
      listBed({ dorId: dorId, bedStatus: '0' }).then(response => {
        console.log("床位API响应:", response);
        console.log("床位数据:", response.rows);
        console.log("床位数量:", response.rows ? response.rows.length : 0);
        
        this.bedOptions = response.rows || [];
        console.log("设置bedOptions后:", this.bedOptions);
        
        if (this.bedOptions.length === 0) {
          console.log("警告：该宿舍没有空闲床位");
          this.$message.warning("该宿舍暂无空闲床位");
        } else {
          console.log("成功获取到", this.bedOptions.length, "个空闲床位");
        }
      }).catch(error => {
        console.error("获取床位列表失败:", error);
        this.$message.error("获取床位信息失败，请稍后重试");
        this.bedOptions = [];
      });
      console.log("=== getBedsByDormId 结束 ===");
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/exchange/export', {
        ...this.queryParams
      }, `exchange_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
