<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold;">阶梯水电费计算器</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="resetForm">重置</el-button>
      </div>
      
      <el-form :model="calculatorForm" :rules="rules" ref="calculatorForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宿舍ID" prop="dormId">
              <el-input v-model="calculatorForm.dormId" placeholder="请输入宿舍ID" type="number"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分摊方式" prop="splitMethod">
              <el-select v-model="calculatorForm.splitMethod" placeholder="请选择分摊方式" style="width: 100%">
                <el-option label="按入住人数分摊" value="BY_OCCUPANTS"></el-option>
                <el-option label="按入住天数分摊" value="BY_RESIDENCE_DAYS"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用电量(度)" prop="electricityUsage">
              <el-input v-model="calculatorForm.electricityUsage" placeholder="请输入用电量" type="number" step="0.01"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用水量(吨)" prop="waterUsage">
              <el-input v-model="calculatorForm.waterUsage" placeholder="请输入用水量" type="number" step="0.01"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计费开始日期" prop="billingStartDate">
              <el-date-picker
                v-model="calculatorForm.billingStartDate"
                type="date"
                placeholder="选择开始日期"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计费结束日期" prop="billingEndDate">
              <el-date-picker
                v-model="calculatorForm.billingEndDate"
                type="date"
                placeholder="选择结束日期"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item>
          <el-button type="primary" @click="calculateBill" :loading="calculating">
            <i class="el-icon-s-data"></i> 计算阶梯费用
          </el-button>
          <el-button @click="resetForm">重置表单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 计算结果展示 -->
    <el-card class="box-card" v-if="calculationResult" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold;">计算结果</span>
      </div>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="result-item">
            <div class="result-label">电费总额</div>
            <div class="result-value electricity">¥{{ calculationResult.electricityCost }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="result-item">
            <div class="result-label">水费总额</div>
            <div class="result-value water">¥{{ calculationResult.waterCost }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="result-item">
            <div class="result-label">总费用</div>
            <div class="result-value total">¥{{ calculationResult.totalCost }}</div>
          </div>
        </el-col>
      </el-row>
      
      <el-divider></el-divider>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="result-item">
            <div class="result-label">每人应付电费</div>
            <div class="result-value">¥{{ calculationResult.electricityCostPerPerson }}</div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="result-item">
            <div class="result-label">每人应付水费</div>
            <div class="result-value">¥{{ calculationResult.waterCostPerPerson }}</div>
          </div>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 15px;">
        <el-col :span="24">
          <div class="result-item">
            <div class="result-label">每人应付总费用</div>
            <div class="result-value total-per-person">¥{{ calculationResult.totalCostPerPerson }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 阶梯费率说明 -->
    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold;">阶梯费率说明</span>
      </div>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <h4>电费阶梯费率</h4>
          <el-table :data="electricityTiers" size="small" border>
            <el-table-column prop="tier" label="阶梯" width="80"></el-table-column>
            <el-table-column prop="range" label="用量范围(度)"></el-table-column>
            <el-table-column prop="rate" label="费率(元/度)"></el-table-column>
          </el-table>
        </el-col>
        <el-col :span="12">
          <h4>水费阶梯费率</h4>
          <el-table :data="waterTiers" size="small" border>
            <el-table-column prop="tier" label="阶梯" width="80"></el-table-column>
            <el-table-column prop="range" label="用量范围(吨)"></el-table-column>
            <el-table-column prop="rate" label="费率(元/吨)"></el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { calculateTieredBill } from "@/api/dormitory/bills";

export default {
  name: "TieredCalculator",
  data() {
    return {
      calculating: false,
      calculatorForm: {
        dormId: '',
        electricityUsage: '',
        waterUsage: '',
        billingStartDate: '',
        billingEndDate: '',
        splitMethod: 'BY_OCCUPANTS'
      },
      calculationResult: null,
      rules: {
        dormId: [
          { required: true, message: "请输入宿舍ID", trigger: "blur" },
          { pattern: /^\d+$/, message: "宿舍ID必须是数字", trigger: "blur" }
        ],
        electricityUsage: [
          { required: true, message: "请输入用电量", trigger: "blur" },
          { pattern: /^\d+(\.\d+)?$/, message: "用电量必须是数字", trigger: "blur" }
        ],
        waterUsage: [
          { required: true, message: "请输入用水量", trigger: "blur" },
          { pattern: /^\d+(\.\d+)?$/, message: "用水量必须是数字", trigger: "blur" }
        ],
        billingStartDate: [
          { required: true, message: "请选择计费开始日期", trigger: "change" }
        ],
        billingEndDate: [
          { required: true, message: "请选择计费结束日期", trigger: "change" }
        ],
        splitMethod: [
          { required: true, message: "请选择分摊方式", trigger: "change" }
        ]
      },
      electricityTiers: [
        { tier: '第一阶梯', range: '0-150', rate: '0.56' },
        { tier: '第二阶梯', range: '151-400', rate: '0.61' },
        { tier: '第三阶梯', range: '401-800', rate: '0.86' },
        { tier: '第四阶梯', range: '800以上', rate: '1.26' }
      ],
      waterTiers: [
        { tier: '第一阶梯', range: '0-15', rate: '2.80' },
        { tier: '第二阶梯', range: '16-25', rate: '4.20' },
        { tier: '第三阶梯', range: '26-35', rate: '8.40' },
        { tier: '第四阶梯', range: '35以上', rate: '12.60' }
      ]
    };
  },
  methods: {
    calculateBill() {
      this.$refs["calculatorForm"].validate(valid => {
        if (valid) {
          this.calculating = true;
          
          const params = {
            dormId: parseInt(this.calculatorForm.dormId),
            electricityUsage: parseFloat(this.calculatorForm.electricityUsage),
            waterUsage: parseFloat(this.calculatorForm.waterUsage),
            billingStartDate: this.formatDate(this.calculatorForm.billingStartDate),
            billingEndDate: this.formatDate(this.calculatorForm.billingEndDate),
            splitMethod: this.calculatorForm.splitMethod
          };
          
          calculateTieredBill(params).then(response => {
            this.calculationResult = response.data;
            this.$message.success("计算完成！");
          }).catch(error => {
            console.error("计算失败:", error);
            this.$message.error("计算失败: " + (error.msg || "未知错误"));
          }).finally(() => {
            this.calculating = false;
          });
        }
      });
    },
    resetForm() {
      this.$refs["calculatorForm"].resetFields();
      this.calculationResult = null;
    },
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.result-item {
  text-align: center;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #fafafa;
}

.result-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.result-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.result-value.electricity {
  color: #f56c6c;
}

.result-value.water {
  color: #409eff;
}

.result-value.total {
  color: #67c23a;
}

.result-value.total-per-person {
  color: #e6a23c;
  font-size: 28px;
}

h4 {
  margin-bottom: 10px;
  color: #303133;
}
</style>