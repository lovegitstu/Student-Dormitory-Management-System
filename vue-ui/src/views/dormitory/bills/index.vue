<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="" prop="fId" v-hasRole="['admin', 'subadmin', 'man']">
        <el-select v-model="queryParams.fId" placeholder="请选择楼栋" clearable @change="parentSelect('querySelect')">
          <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId" />
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="dorId" v-hasRole="['subadmin', 'admin', 'man']">
        <el-select v-model="queryParams.dorId" placeholder="请选择宿舍" @change="childSelect('querySelect')" clearable>
          <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName" :value="item.dorId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="ubDatetime">
        <el-date-picker clearable v-model="queryParams.ubDatetime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择费用时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="" prop="ubTotalCost">
        <el-input v-model="queryParams.ubTotalCost" placeholder="请输入总费用" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="ubMeterReading">
        <el-input v-model="queryParams.ubMeterReading" placeholder="请输入抄表/核算人" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 学生寝室余额显示卡片 -->
    <div v-hasRole="['student']" class="mb8">
      <el-card class="balance-card" shadow="hover">
        <div slot="header" class="clearfix">
          <span style="font-weight: bold; color: #409EFF;">
            <i class="el-icon-wallet"></i> 我的寝室余额
          </span>
          <el-button style="float: right; padding: 3px 0" type="text" @click="refreshBalance">
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
        <div v-if="balanceLoading" style="text-align: center; padding: 20px;">
          <i class="el-icon-loading"></i> 加载中...
        </div>
        <div v-else-if="balanceInfo" class="balance-content">
          <div class="balance-info">
            <div class="balance-item">
              <span class="label">寝室号：</span>
              <span class="value">{{ balanceInfo.dormName }}</span>
            </div>
            <div class="balance-item">
              <span class="label">当前余额：</span>
              <span class="value balance-amount" :class="{ 'low-balance': balanceInfo.balance < 50 }">
                ¥{{ balanceInfo.balance }}
              </span>
            </div>
            <div class="balance-item">
              <span class="label">账单日期：</span>
              <span class="value">{{ parseTime(balanceInfo.billDate, '{y}-{m}-{d}') }}</span>
            </div>
          </div>
          <div class="balance-actions">
            <el-button type="primary" icon="el-icon-money" @click="handleQuickRecharge">
              快速充值
            </el-button>
            <el-button type="success" icon="el-icon-view" @click="handleViewBillDetails">
              查看详情
            </el-button>
          </div>
        </div>
        <div v-else class="no-balance-info">
          <i class="el-icon-warning-outline"></i>
          <p>暂无寝室信息或余额数据</p>
          <el-button type="primary" size="small" @click="refreshBalance">重新加载</el-button>
        </div>
      </el-card>
    </div>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['dormitory:bills:add']" v-hasRole="['subadmin', 'admin', 'man']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['dormitory:bills:edit']" v-hasRole="['subadmin', 'admin', 'man']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['dormitory:bills:remove']" v-hasRole="['subadmin', 'admin', 'man']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['dormitory:bills:export']" v-hasRole="['subadmin', 'admin', 'man']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border v-loading="loading" :data="billsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" lable="序号" width="55" align="center" />
      <!-- <el-table-column label="ID" align="center" prop="ubId" /> -->
      <!-- <el-table-column label="宿舍楼" align="center" prop="dormFloor.fName" /> -->
      <el-table-column label="宿舍号" align="center" prop="dormDormitory.dorName" />
      <el-table-column label="费用时间" align="center" prop="ubDatetime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.ubDatetime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用水量" align="center" prop="ubWaterConsumption" />
      <el-table-column label="水费(元)" align="center" prop="ubWater" />
      <!-- <el-table-column label="单位(吨)" align="center" prop="ubWaterMonad" /> -->
      <el-table-column label="用电量" align="center" prop="ubElectricityConsumption" />
      <el-table-column label="电费(元)" align="center" prop="ubElectricityRate" />
      <!-- <el-table-column label="电单位(度)" align="center" prop="ubElectricityUnits" /> -->
      <el-table-column label="总费用" align="center" prop="ubTotalCost" />
      <el-table-column label="缴费状态" align="center" prop="ubPaymentStatus" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.ubPaymentStatus === 1 ? 'success' : 'danger'" size="mini">
            {{ scope.row.ubPaymentStatus === 1 ? '已缴费' : '未缴费' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="核算人" align="center" prop="ubMeterReading" />
      <el-table-column label="联系电话" align="center" prop="ubPhone" width="110" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="320">
        <template slot-scope="scope">
          <el-button size="small" type="warning" icon="el-icon-view" @click="handleDetails(scope.row)"
            v-hasPermi="['dormitory:bills:edit']">详情</el-button>
          <el-button size="small" type="success" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['dormitory:bills:edit']" v-hasRole="['subadmin', 'admin', 'man']">修改</el-button>
          <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['dormitory:bills:remove']" v-hasRole="['subadmin', 'admin', 'man']">删除</el-button>
          <el-button size="small" type="primary" icon="el-icon-money" @click="handlePayment(scope.row)"
            v-hasPermi="['dormitory:bills:recharge']" v-hasRole="['student']" 
            v-if="scope.row.ubPaymentStatus === 0">缴费</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改水电费管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="宿舍楼" prop="fId">
          <el-select v-model="form.fId" placeholder="请选择宿舍楼" @change="parentSelect('addEditSelect')">
            <el-option v-for="item in floorOptions" :key="item.fId" :label="item.fName" :value="item.fId"></el-option>
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="宿舍" prop="dorId">
              <el-select v-model="form.dorId" placeholder="请选择宿舍" @change="childSelect('addEditSelect')">
                <el-option v-for="item in dormOptions" :key="item.dorId" :label="item.dorName"
                  :value="item.dorId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用时间" prop="ubDatetime">
              <el-date-picker clearable v-model="form.ubDatetime" type="date" value-format="yyyy-MM-dd"
                placeholder="请选择费用时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用水量" prop="ubWaterConsumption">
              <el-input v-model="form.ubWaterConsumption" placeholder="请输入用水量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用电量" prop="ubElectricityConsumption">
              <el-input v-model="form.ubElectricityConsumption" placeholder="请输入用电量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="元/吨" prop="ubWaterMonad">
              <el-input v-model="form.ubWaterMonad" clearable placeholder="请输入单位(吨)" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="元/度" prop="ubElectricityUnits">
              <el-input v-model="form.ubElectricityUnits" clearable placeholder="请输入电单位(度)" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="水费(元)" prop="ubWater">
              <el-input v-model="form.ubWater" :disabled="true" placeholder="请输入水费(元)" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电费(元)" prop="ubElectricityRate">
              <el-input v-model="form.ubElectricityRate" :disabled="true" placeholder="请输入电费(元)" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="总费用" prop="ubTotalCost">
              <el-input v-model="form.ubTotalCost" :disabled="true" placeholder="请输入总费用" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="核算人" prop="ubMeterReading">
              <el-input v-model="form.ubMeterReading" placeholder="请输入抄表/核算人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="电话" prop="ubPhone">
              <el-input v-model="form.ubPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="描述" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入描述" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 水电费详细 -->
    <el-dialog title="水电费详细" :visible.sync="openDetails" width="420px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px" size="mini">
        <el-row>
          <el-col :span="12">
            <el-form-item label="宿舍楼：">{{ fName }}</el-form-item>
            <el-form-item label="元/吨：">{{ form.ubWaterMonad }}</el-form-item>
            <el-form-item label="用水量：">{{ form.ubWaterConsumption }}</el-form-item>
            <el-form-item label="水费：">{{ form.ubWater }}</el-form-item>
            <el-form-item label="核算人：">{{ form.ubMeterReading }}</el-form-item>
            <el-form-item label="总费用：">{{ form.ubTotalCost }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="宿舍号：">{{ dorName }} </el-form-item>
            <el-form-item label="元/度：">{{ form.ubElectricityUnits }}</el-form-item>
            <el-form-item label="用电量：">{{ form.ubElectricityConsumption }}</el-form-item>
            <el-form-item label="电费：">{{ form.ubElectricityRate }}</el-form-item>
            <el-form-item label="联系方式：">{{ form.ubPhone }}</el-form-item>
            <el-form-item label="其他描述：">{{ form.remark }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="">如有疑问？请致电:{{ form.ubPhone }} 咨询</el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="success" :disabled="isDisabledOKBtn" @click="submitOK" v-hasPermi="['dormitory:bills:edit']" v-hasRole="['student']">{{
          btnText }}</el-button>
        <el-button type="primary" @click="handleRechargeFromDetails" v-hasRole="['student']">充值</el-button>
        <el-button @click="openDetails = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 快速充值弹窗 -->
    <el-dialog title="快速充值" :visible.sync="quickRechargeDialogVisible" width="450px" append-to-body>
      <div style="text-align: center;">
        <div v-if="balanceInfo" style="margin-bottom: 20px; padding: 15px; background-color: #f5f7fa; border-radius: 8px;">
          <h4 style="margin: 0 0 10px 0; color: #409EFF;">
            <i class="el-icon-home"></i> {{ balanceInfo.dormName }}
          </h4>
          <p style="margin: 0; color: #666;">
            当前余额：<span style="font-weight: bold; color: #E6A23C;">¥{{ balanceInfo.balance }}</span>
          </p>
        </div>
        
        <div style="margin: 20px 0;">
          <h4>选择充值金额</h4>
          <div style="display: flex; flex-wrap: wrap; gap: 10px; justify-content: center; margin: 15px 0;">
            <el-button 
              v-for="amount in quickAmounts" 
              :key="amount" 
              :type="quickRechargeAmount === amount ? 'primary' : 'default'"
              @click="selectQuickAmount(amount)"
              style="min-width: 80px;">
              ¥{{ amount }}
            </el-button>
          </div>
          
          <div style="margin: 20px 0;">
            <span style="margin-right: 10px;">自定义金额：</span>
            <el-input-number
              v-model="quickRechargeAmount"
              :min="1"
              :max="10000"
              :precision="2"
              style="width: 150px;">
            </el-input-number>
          </div>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="quickRechargeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmQuickRecharge" :disabled="!quickRechargeAmount || quickRechargeAmount <= 0">
          确认充值 ¥{{ quickRechargeAmount }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 充值金额选择弹窗 -->
    <el-dialog title="选择充值金额" :visible.sync="rechargeDialogVisible" width="400px" append-to-body>
      <div style="text-align: center;">
        <h4>请选择充值金额</h4>
        <div style="margin: 20px 0;">
          <p><strong>宿舍：</strong>{{ currentRechargeRow ? currentRechargeRow.dormDormitory.dorName : '' }}</p>
          <p><strong>当前欠费：</strong>¥{{ currentRechargeRow ? currentRechargeRow.ubTotalCost : 0 }}</p>
        </div>
        
        <!-- 快捷金额选择 -->
        <div style="margin: 20px 0;">
          <el-button 
            v-for="amount in [50, 100, 200, 500]" 
            :key="amount" 
            @click="rechargeAmount = amount" 
            :type="rechargeAmount === amount ? 'primary' : 'default'"
            style="margin: 5px;">
            ¥{{ amount }}
          </el-button>
        </div>
        
        <!-- 自定义金额输入 -->
        <div style="margin: 20px 0;">
          <el-input 
            v-model.number="rechargeAmount" 
            placeholder="请输入充值金额" 
            type="number" 
            :min="1" 
            :max="10000"
            style="width: 200px;">
            <template slot="prepend">¥</template>
          </el-input>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="rechargeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmRecharge" :disabled="!rechargeAmount || rechargeAmount <= 0">确认充值</el-button>
      </div>
    </el-dialog>

    <!-- 付款码弹窗 -->
    <el-dialog title="微信付款码" :visible.sync="paymentDialogVisible" width="500px" append-to-body>
      <div style="text-align: center;">
        <h3>请使用微信扫码付款</h3>
        <div style="margin: 20px 0;">
          <p><strong>订单号：</strong>{{ paymentData.ubId }}</p>
          <p><strong>宿舍：</strong>{{ paymentData.dormInfo }}</p>
          <p><strong>金额：</strong>¥{{ paymentData.amount }}</p>
        </div>
        
        <div style="margin: 30px 0; padding: 20px; border: 2px dashed #ddd; background-color: #f9f9f9;">
          <div style="display: flex; justify-content: center; align-items: center; min-height: 300px;">
            <!-- 显示付款码图片 -->
            <div v-if="!imageLoadError">
              <img :src="paymentData.paymentCode" alt="微信付款码" style="max-width: 280px; max-height: 280px; border: 1px solid #eee; border-radius: 8px;" @error="handleImageError" />
            </div>
            <!-- 图片加载失败时的备用显示 -->
            <div v-else style="text-align: center; color: #999;">
              <i class="el-icon-picture-outline" style="font-size: 48px; margin-bottom: 10px;"></i>
              <p>付款码加载失败</p>
              <p style="font-size: 12px;">请检查网络连接或联系管理员</p>
              <el-button size="mini" type="primary" @click="retryLoadImage">重新加载</el-button>
            </div>
          </div>
        </div>
        
        <p style="color: #666; font-size: 14px;">
          支付完成后，请点击下方按钮确认
        </p>
        
        <!-- 支付完成确认按钮 -->
        <div style="margin-top: 20px;">
          <el-button type="success" size="medium" @click="confirmPaymentComplete" :loading="confirmingPayment">
            <i class="el-icon-check"></i> 我已完成支付
          </el-button>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="closePaymentDialog">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBills, getBills, delBills, addBills, updateBills, rechargeBills, payBill, getMyDormBalance, rechargeMyDorm } from "@/api/dormitory/bills";
import { listDorm } from "@/api/dormitory/dorm";
import { listFloor } from "@/api/dormitory/floor";
import { getInfo } from "@/api/login"

export default {
  name: "Bills",
  dicts: ['dorm_type'],
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
      // 水电费管理表格数据
      billsList: [],
      //宿舍楼选择数据
      floorOptions: [],
      //宿舍号选择数据
      dormOptions: [],
      //宿舍楼名
      fName: '',
      crruentUserDorId: null,
      //宿舍名
      dorName: '',
      //详情页面按钮显示文本
      btnText: '确认无误',
      //是否启用详情确认按钮
      isDisabledOKBtn: false,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //是否显示弹出详情页面
      openDetails: false,
      // 是否显示付款码弹窗
      paymentDialogVisible: false,
      // 付款码数据
      paymentData: {},
      // 图片加载错误状态
      imageLoadError: false,
      // 支付确认中状态
      confirmingPayment: false,
      // 是否显示充值金额选择弹窗
      rechargeDialogVisible: false,
      // 充值金额
      rechargeAmount: '',
      // 当前充值的行数据
      currentRechargeRow: null,
      // 寝室余额信息
      balanceInfo: null,
      // 余额加载状态
      balanceLoading: false,
      // 是否显示快速充值弹窗
      quickRechargeDialogVisible: false,
      // 快速充值金额
      quickRechargeAmount: null,
      // 快速充值金额选项
      quickAmounts: [50, 100, 200, 500, 1000],
      currentRechargeRow: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fId: null,
        dorId: null,
        ubDatetime: null,
        ubTotalCost: null,
        ubMeterReading: null,
      },
      //选择宿舍楼参数
      selectParams: {
        fId: null,
      },
      // 表单参数
      form: {
      },
      // 表单校验
      rules: {
        dorId: [
          { required: true, message: "宿舍编号不能为空", trigger: "blur" },
        ],
        ubPhone: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ]
      }
    };
  },
  created () {
    console.log("=== 组件初始化开始 ===");
    console.log("开始加载水电费列表数据");
    this.getList();
    console.log("开始加载宿舍楼列表数据");
    this.getAllFloorList();
    // 如果是学生角色，加载寝室余额信息
    console.log("开始加载余额信息");
    this.loadBalanceInfo();
    console.log("=== 组件初始化完成 ===");
  },
  methods: {
    /** 确认充值 */
    confirmRecharge() {
      if (!this.rechargeAmount || this.rechargeAmount <= 0) {
        this.$modal.msgWarning('请输入有效的充值金额');
        return;
      }
      
      const ubId = this.currentRechargeRow.ubId;
      console.log("=== 充值流程开始 ===");
      console.log("开始充值，ubId:", ubId);
      console.log("充值金额:", this.rechargeAmount);
      console.log("当前行数据:", this.currentRechargeRow);
      
      // 关闭金额选择弹窗
      this.rechargeDialogVisible = false;
      
      // 调用充值接口
      rechargeBills(ubId, this.rechargeAmount).then((response) => {
        console.log("=== 充值接口返回完整数据 ===");
        console.log("response:", response);
        console.log("response.code:", response.code);
        console.log("response.msg:", response.msg);
        console.log("response.data:", response.data);
        
        if (response.code === 200) {
          console.log("=== 接口调用成功 ===");
          console.log('检查 response.data:', response.data);
          console.log('检查 response.data.paymentCode:', response.data ? response.data.paymentCode : '无data字段');
          
          // 检查是否有付款码数据
          if (response.data && response.data.paymentCode) {
            console.log('=== 付款码数据检查成功 ===');
            console.log('付款码数据:', response.data);
            this.showPaymentDialog(response.data);
          } else {
            console.log('\n === 付款码数据检查失败 ===');
            console.log('\n 未收到付款码数据:', response.data);
            this.$modal.msgWarning('付款码生成失败，请重试');
          }
        } else {
          console.error("=== 接口调用失败 ===");
          console.error("错误代码:", response.code);
          console.error("错误信息:", response.msg);
          this.$modal.msgError(response.msg || "充值请求失败");
        }
      }).catch((error) => {
        console.error("=== 充值请求异常 ===");
        console.error("充值请求失败:", error);
        console.error("错误详情:", error.response);
        this.$modal.msgError("充值请求失败");
      });
    },

    //父类选择器
    parentSelect: function (param) {
      console.log("=== parentSelect 开始 ===");
      console.log("参数:", param);
      
      if (param === 'querySelect') {
        console.log("这是搜索select")
        this.selectParams.fId = this.queryParams.fId
        // 清空查询条件中的宿舍号
        this.queryParams.dorId = null;
        console.log("查询模式 - 设置selectParams.fId:", this.selectParams.fId);
      }
      if (param === 'addEditSelect') {
        console.log("这是添加或者编辑select")
        this.selectParams.fId = this.form.fId
        // 清空表单中的宿舍号
        this.form.dorId = null;
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
          console.log("API响应 - rows长度:", response.rows ? response.rows.length : 0);
          if (response.rows && response.rows.length > 0) {
            console.log("API响应 - 第一个宿舍数据:", response.rows[0]);
          }
          this.dormOptions = response.rows || [];
          console.log("设置dormOptions后 - 长度:", this.dormOptions.length);
          console.log("设置dormOptions后 - 内容:", this.dormOptions);
        }).catch(error => {
          console.error("获取宿舍列表失败 - 错误详情:", error);
          this.dormOptions = [];
          this.$modal.msgError("获取宿舍列表失败，请检查权限或联系管理员");
        })
      } else {
        console.log("selectParams.fId为空，不发送API请求");
      }
      console.log("=== parentSelect 结束 ===");
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
    /** 查询水电费管理列表 */
    getList () {
      this.loading = true;
      getInfo().then(response => {
        this.queryParams.dorId = response.user.dorId;
        listBills(this.queryParams).then(response => {
          this.billsList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      })
    },
    getAllFloorList () {
      console.log("=== 开始加载宿舍楼列表 ===");
      listFloor().then(response => {
        console.log("宿舍楼列表API响应成功:", response);
        console.log("宿舍楼数据rows:", response.rows);
        console.log("宿舍楼数据长度:", response.rows ? response.rows.length : 0);
        this.floorOptions = response.rows;
        console.log("设置floorOptions后:", this.floorOptions);
        console.log("=== 宿舍楼列表加载完成 ===");
      }).catch(error => {
        console.error("=== 加载宿舍楼列表失败详细信息 ===");
        console.error("错误对象类型:", typeof error);
        console.error("错误对象:", error);
        console.error("错误对象的所有属性:", Object.keys(error));
        console.error("错误对象的所有属性值:", Object.values(error));
        
        // 检查是否是网络错误
        if (error.code === 'NETWORK_ERROR' || error.message === 'Network Error') {
          console.error("网络连接错误");
          this.$modal.msgError("网络连接失败，请检查网络连接");
        }
        // 检查是否是超时错误
        else if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
          console.error("请求超时");
          this.$modal.msgError("请求超时，请稍后重试");
        }
        // 检查是否是HTTP响应错误
        else if (error.response) {
          console.error("HTTP响应错误:");
          console.error("响应状态:", error.response.status);
          console.error("响应数据:", error.response.data);
          console.error("响应头:", error.response.headers);
          this.$modal.msgError(`服务器错误 (${error.response.status}): ${error.response.data?.msg || '未知错误'}`);
        }
        // 检查是否是请求配置错误
        else if (error.request) {
          console.error("请求配置错误:", error.request);
          this.$modal.msgError("请求发送失败，请检查服务器连接");
        }
        // 其他未知错误
        else {
          console.error("未知错误类型");
          console.error("错误消息:", error.message || '无错误消息');
          console.error("错误堆栈:", error.stack || '无堆栈信息');
          this.$modal.msgError(`加载失败: ${error.message || '未知错误'}`);
        }
        
        this.floorOptions = [];
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
        ubId: null,
        dorId: null,
        ubDatetime: null,
        ubWater: null,
        ubWaterMonad: 3.45,
        ubElectricityRate: null,
        ubElectricityUnits: 0.52,
        ubWaterConsumption: null,
        ubElectricityConsumption: null,
        ubTotalCost: null,
        isDisabledOKBtn: null,
        remark: null,
        ubMeterReading: null,
        ubPhone: null,
        // dormFloor: [],
        // dormDormitory: []
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
      this.ids = selection.map(item => item.ubId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    //详情水电费信息
    handleDetails (row) {
      this.reset();
      const ubId = row.ubId || this.ids
      getBills(ubId).then(response => {
        this.form = response.data;
        this.openDetails = true;
        this.selectParams.fId = this.form.fId
        this.fName = this.form.dormFloor.fName
        this.dorName = this.form.dormDormitory.dorName
        if (this.form.ubIsnotarize == 1) {
          this.btnText = "已确认";
          this.isDisabledOKBtn = true;
          // this.form.ubIsnotarize == 1
        }
        if (this.form.ubIsnotarize == 0) {
          this.btnText = "确认无误";
          this.isDisabledOKBtn = false;
          // this.form.ubIsnotarize = 0
        }
        console.log(this.form.ubIsnotarize)
      });
      listDorm(this.selectParams).then(response => {
        this.dormOptions = response.rows;
      })
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      this.open = true;
      this.title = "添加水电费信息";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const ubId = row.ubId || this.ids
      getBills(ubId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改水电费信息";
        this.selectParams.fId = this.form.fId
      });
      listDorm(this.selectParams).then(response => {
        this.dormOptions = response.rows;
      })
    },
    submitOK () {
      this.form.ubIsnotarize = 1;
      this.isDisabledOKBtn = true;
      this.btnText = "已确认";
      if (this.form.ubId != null) {
        updateBills(this.form).then(response => {
          this.$modal.msgSuccess("水电费账单确认完成");
          this.open = false;
          this.getList();
        });
      }
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          //电费=用电量*单价
          let ubWater = parseFloat(this.form.ubWaterConsumption) * parseFloat(this.form.ubWaterMonad);
          this.form.ubWater = ubWater;
          //电费=用电量*单价
          let ubElectricityRate = parseFloat(this.form.ubElectricityConsumption) * parseFloat(this.form.ubElectricityUnits)
          this.form.ubElectricityRate = ubElectricityRate;
          //总费用=水费+电费
          let ubTotalCost = parseFloat(this.form.ubWater) + parseFloat(this.form.ubElectricityRate);
          this.form.ubTotalCost = ubTotalCost;
          // this.openDetails = false;
          if (this.form.ubId != null) {
            updateBills(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            this.form.ubIsnotarize = 0;
            this.isDisabledOKBtn = false;
            this.btnText = "确认无误";
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
    handleDelete (row) {
      const ubIds = row.ubId || this.ids;
      this.$modal.confirm('确定要删除该宿舍的水电费信息吗？').then(function () {
        return delBills(ubIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('dormitory/bills/export', {
        ...this.queryParams
      }, `bills_${new Date().getTime()}.xlsx`)
    },
    /** 充值按钮操作 */
    handleRecharge (row) {
      // 显示充值金额选择弹窗
      this.currentRechargeRow = row;
      this.rechargeDialogVisible = true;
      this.rechargeAmount = '';
    },
    /** 从详情页面充值 */
    handleRechargeFromDetails () {
      this.handleRecharge({ ubId: this.form.ubId });
    },
    /** 学生缴费按钮操作 */
    handlePayment(row) {
      // 直接从宿舍余额扣费
      this.payBill(row.ubId, row.ubTotalCost);
    },
    /** 学生直接缴费 - 从宿舍余额扣除费用 */
    payBill(ubId, totalCost) {
      this.$modal.confirm('确认缴费 ¥' + totalCost + ' 吗？将从宿舍余额中扣除。').then(() => {
        return payBill(ubId);
      }).then((response) => {
        if (response.code === 200) {
          this.$modal.msgSuccess("缴费成功！已扣除 ¥" + response.data.paidAmount + "，剩余余额 ¥" + response.data.newBalance);
          this.getList();
        } else {
          // 检查是否是余额不足的情况
           if (response.data && response.data.shortfall) {
             this.$modal.confirm('余额不足，还需充值 ¥' + response.data.shortfall + '。是否立即充值？').then(() => {
               // 设置充值金额为不足的金额，并显示充值弹窗
               this.currentRechargeRow = { ubId: response.data.ubId };
               this.rechargeAmount = response.data.shortfall;
               this.rechargeDialogVisible = true;
             }).catch(() => {});
           } else {
             this.$modal.msgError(response.msg || "缴费失败");
           }
        }
      }).catch(() => {});
    },
     /** 从详情页面充值 */
    handleRechargeFromDetails () {
      this.handleRecharge({ ubId: this.form.ubId });
    },
    /** 显示付款码弹窗 ;*/
    showPaymentDialog(data) {
      console.log("=== 显示付款码弹窗开始 ===");
      console.log("接收到的数据:", data);
      console.log("数据类型:", typeof data);
      console.log("数据是否为null:", data === null);
      console.log("数据是否为undefined:", data === undefined);
      
      if (data && data.paymentCode) {
        console.log("付款码URL:", data.paymentCode);
        console.log("付款码URL类型:", typeof data.paymentCode);
        console.log("付款码URL长度:", data.paymentCode.length);
        console.log("付款码URL是否以http开头:", data.paymentCode.startsWith('http'));
      } else {
        console.error("付款码数据异常:", data);
      }
      
      this.paymentData = data;
      this.imageLoadError = false; // 重置图片加载错误状态
      console.log("设置paymentData完成:", this.paymentData);
      console.log("imageLoadError状态:", this.imageLoadError);
      
      this.paymentDialogVisible = true;
      console.log("弹窗显示状态设置为:", this.paymentDialogVisible);
      
      // 延迟检查DOM元素
      this.$nextTick(() => {
        console.log("=== DOM更新后检查 ===");
        const imgElement = document.querySelector('.payment-dialog img');
        if (imgElement) {
          console.log("找到图片元素:", imgElement);
          console.log("图片src属性:", imgElement.src);
          console.log("图片complete状态:", imgElement.complete);
          console.log("图片naturalWidth:", imgElement.naturalWidth);
          console.log("图片naturalHeight:", imgElement.naturalHeight);
        } else {
          console.warn("未找到图片元素");
        }
      });
    },
    /** 关闭付款码弹窗 */
    closePaymentDialog() {
      console.log("=== 关闭付款码弹窗 ===");
      this.paymentDialogVisible = false;
      this.paymentData = {};
      this.imageLoadError = false; // 重置图片加载错误状态
      console.log("弹窗已关闭，数据已清空");
    },
    /** 处理图片加载错误 */
    handleImageError(event) {
      console.error("=== 付款码图片加载失败详细信息 ===");
      console.error("错误事件:", event);
      console.error("错误类型:", event.type);
      console.error("目标元素:", event.target);
      console.error("图片src:", event.target.src);
      console.error("图片完整URL:", event.target.src);
      console.error("当前paymentData:", this.paymentData);
      console.error("paymentData.paymentCode:", this.paymentData.paymentCode);
      
      // 检查网络连接
      if (navigator.onLine) {
        console.log("网络连接正常");
      } else {
        console.error("网络连接异常");
      }
      
      // 尝试直接访问图片URL
      if (this.paymentData.paymentCode) {
        console.log("尝试通过fetch检查图片URL可访问性:", this.paymentData.paymentCode);
        fetch(this.paymentData.paymentCode, { method: 'HEAD' })
          .then(response => {
            console.log("图片URL访问测试结果:", response.status, response.statusText);
            if (response.ok) {
              console.log("图片URL可访问，但加载失败可能是其他原因");
            } else {
              console.error("图片URL不可访问:", response.status);
            }
          })
          .catch(error => {
            console.error("图片URL访问测试失败:", error);
          });
      }
      
      this.imageLoadError = true; // 设置图片加载错误状态
      this.$modal.msgError("付款码图片加载失败，请检查网络连接或联系管理员");
    },
    /** 重新加载图片 */
    retryLoadImage() {
      console.log("=== 重新加载图片 ===");
      console.log("当前paymentData:", this.paymentData);
      console.log("重置前imageLoadError状态:", this.imageLoadError);
      
      this.imageLoadError = false; // 重置错误状态，重新尝试加载图片
      console.log("重置后imageLoadError状态:", this.imageLoadError);
      
      this.$message.info("正在重新加载付款码...");
      
      // 强制重新渲染图片
      this.$nextTick(() => {
        const imgElement = document.querySelector('.payment-dialog img');
        if (imgElement && this.paymentData.paymentCode) {
          console.log("强制重新加载图片:", this.paymentData.paymentCode);
          imgElement.src = this.paymentData.paymentCode + '?t=' + Date.now();
        }
      });
    },
    /** 确认支付完成 */
    confirmPaymentComplete() {
      this.confirmingPayment = true;
      
      // 模拟支付确认过程
      setTimeout(() => {
        this.confirmingPayment = false;
        this.paymentDialogVisible = false;
        
        // 显示成功消息
        this.$modal.msgSuccess("支付确认成功，账单状态将更新");
        
        // 刷新账单列表和余额信息
        this.getList();
        this.loadBalanceInfo();
      }, 1500);
    },
    /** 加载寝室余额信息 */
    loadBalanceInfo() {
      // 检查用户角色，只有学生才显示余额信息
      const userRoles = this.$store.getters.roles;
      if (!userRoles.includes('student')) {
        return;
      }
      
      this.balanceLoading = true;
      getMyDormBalance().then(response => {
        if (response.code === 200 && response.data) {
          this.balanceInfo = {
            dormName: response.data.dormName,
            balance: response.data.balance,
            billDate: response.data.usageDate, // 修复：使用usageDate作为账单日期
            ubId: response.data.ubId
          };
        } else {
          this.balanceInfo = null;
        }
      }).catch(error => {
        console.error('加载余额信息失败:', error);
        this.balanceInfo = null;
      }).finally(() => {
        this.balanceLoading = false;
      });
    },
    /** 刷新余额信息 */
    refreshBalance() {
      this.loadBalanceInfo();
    },
    /** 快速充值 */
    handleQuickRecharge() {
      if (!this.balanceInfo) {
        this.$modal.msgWarning('无法获取寝室信息，请刷新页面重试');
        return;
      }
      this.quickRechargeAmount = null;
      this.quickRechargeDialogVisible = true;
    },
    /** 查看账单详情 */
    handleViewBillDetails() {
      if (this.balanceInfo && this.balanceInfo.ubId) {
        // 查找对应的账单行数据
        const billRow = this.billsList.find(bill => bill.ubId === this.balanceInfo.ubId);
        if (billRow) {
          this.handleDetails(billRow);
        } else {
          this.$modal.msgWarning('无法找到对应的账单详情');
        }
      }
    },
    /** 选择快速充值金额 */
    selectQuickAmount(amount) {
      this.quickRechargeAmount = amount;
    },
    /** 确认快速充值 */
    confirmQuickRecharge() {
      if (!this.quickRechargeAmount || this.quickRechargeAmount <= 0) {
        this.$modal.msgWarning('请选择或输入有效的充值金额');
        return;
      }
      
      console.log("=== 快速充值流程开始 ===");
      console.log("充值金额:", this.quickRechargeAmount);
      
      // 关闭快速充值弹窗
      this.quickRechargeDialogVisible = false;
      
      // 调用自动充值接口
      rechargeMyDorm(this.quickRechargeAmount).then((response) => {
        console.log("=== 快速充值接口返回数据 ===");
        console.log("response:", response);
        
        if (response.code === 200) {
          console.log("=== 快速充值接口调用成功 ===");
          
          // 检查是否有付款码数据
          if (response.data && response.data.paymentCode) {
            console.log('=== 付款码数据检查成功 ===');
            this.showPaymentDialog(response.data);
          } else {
            console.log('=== 付款码数据检查失败 ===');
            this.$modal.msgWarning('付款码生成失败，请重试');
          }
        } else {
          console.error("=== 快速充值接口调用失败 ===");
          this.$modal.msgError(response.msg || "充值请求失败");
        }
      }).catch((error) => {
        console.error("=== 快速充值请求异常 ===");
        console.error("充值请求失败:", error);
        this.$modal.msgError("充值请求失败");
      });
    }
  }
};
</script>

<style scoped>
.balance-card {
  border-radius: 8px;
  margin-bottom: 15px;
}

.balance-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 20px;
}

.balance-info {
  flex: 1;
  min-width: 300px;
}

.balance-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.balance-item:last-child {
  border-bottom: none;
}

.balance-item .label {
  color: #666;
  font-weight: normal;
  font-size: 14px;
}

.balance-item .value {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.balance-amount {
  font-size: 20px;
  color: #67C23A;
  font-weight: 600;
}

.balance-amount.low-balance {
  color: #F56C6C;
}

.balance-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 120px;
  align-items: stretch;
}

.balance-actions .el-button {
  width: 100%;
}

.no-balance-info {
  text-align: center;
  color: #999;
  padding: 30px 20px;
}

.no-balance-info i {
  font-size: 48px;
  margin-bottom: 15px;
  color: #ddd;
}

.no-balance-info p {
  margin: 10px 0;
  font-size: 14px;
}

@media (max-width: 768px) {
  .balance-content {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }
  
  .balance-actions {
    flex-direction: row;
    justify-content: center;
    gap: 15px;
  }
  
  .balance-actions .el-button {
    flex: 1;
    max-width: 150px;
  }
}
</style>
