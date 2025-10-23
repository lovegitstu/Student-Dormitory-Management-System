<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>宿舍管理系统</h1>
      <p>欢迎使用宿舍管理系统，这里是您的数据概览</p>
    </div>

    <!-- 管理员视图 -->
    <div v-if="userRole === 'admin'" class="admin-dashboard">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">🏠</div>
          <div class="stat-content">
            <h3>总宿舍数</h3>
            <p class="stat-number">{{ overviewData.totalDorms || 0 }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🛏️</div>
          <div class="stat-content">
            <h3>总床位数</h3>
            <p class="stat-number">{{ overviewData.totalBeds || 0 }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <h3>入住学生</h3>
            <p class="stat-number">{{ overviewData.occupiedBeds || 0 }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-content">
            <h3>入住率</h3>
            <p class="stat-number">{{ occupancyRate }}%</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🏢</div>
          <div class="stat-content">
            <h3>总楼层数</h3>
            <p class="stat-number">{{ overviewData.totalFloors || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="charts-section">
        <!-- 床位统计饼图 -->
        <div class="chart-card">
          <h3>床位统计</h3>
          <Charts
            chart-id="bedStatsChart"
            :option="bedStatsChartOption"
            height="300px"
          />
        </div>

        <!-- 维修统计柱状图 -->
        <div class="chart-card">
          <h3>维修统计</h3>
          <Charts
            chart-id="repairStatsChart"
            :option="repairStatsChartOption"
            height="300px"
          />
        </div>

        <!-- 申请处理统计 -->
        <div class="chart-card full-width">
          <h3>申请处理统计</h3>
          <Charts
            chart-id="applicationStatsChart"
            :option="applicationStatsChartOption"
            height="400px"
          />
        </div>
      </div>
    </div>

    <!-- 宿管视图 -->
    <div v-else-if="userRole === 'manager'" class="manager-dashboard">
      <div class="manager-stats">
        <div class="stat-card">
          <div class="stat-icon">🏢</div>
          <div class="stat-content">
            <h3>管理楼层</h3>
            <p class="stat-number">{{ managerData.managedFloors || 0 }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🛏️</div>
          <div class="stat-content">
            <h3>管理床位</h3>
            <p class="stat-number">{{ managerData.managedBeds || 0 }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🔧</div>
          <div class="stat-content">
            <h3>待处理维修</h3>
            <p class="stat-number">{{ managerData.pendingRepairs || 0 }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <h3>入住学生</h3>
            <p class="stat-number">{{ managerData.occupiedBeds || 0 }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-content">
            <h3>入住率</h3>
            <p class="stat-number">{{ managerData.occupancyRate || 0 }}%</p>
          </div>
        </div>
      </div>

      <div class="manager-charts">
        <!-- 维修申请处理饼图 -->
        <div class="chart-card">
          <h3>维修申请处理</h3>
          <Charts
            chart-id="managerRepairStatsChart"
            :option="managerRepairStatsChartOption"
            height="300px"
          />
        </div>

        <!-- 本月工作统计雷达图 -->
        <div class="chart-card">
          <h3>本月工作统计</h3>
          <Charts
            chart-id="managerWorkStatsChart"
            :option="managerWorkStatsChartOption"
            height="300px"
          />
        </div>

        <!-- 楼层床位状态柱状图 -->
        <div class="chart-card floor-status-chart">
          <h3>楼层床位状态</h3>
          <Charts
            chart-id="managerFloorStatusChart"
            :option="managerFloorStatusChartOption"
            height="400px"
          />
        </div>
      </div>
    </div>

    <!-- 学生视图 -->
    <div v-else-if="userRole === 'student'" class="student-dashboard">
      <div class="student-stats-grid">
        <div class="stat-card">
          <div class="stat-icon">🏠</div>
          <div class="stat-content">
            <h3>我的宿舍</h3>
            <p class="stat-number">{{ studentInfo.dormNumber || '未分配' }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🛏️</div>
          <div class="stat-content">
            <h3>床位号</h3>
            <p class="stat-number">{{ studentInfo.bedNumber || '未分配' }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🏢</div>
          <div class="stat-content">
            <h3>楼层</h3>
            <p class="stat-number">{{ studentInfo.floor || '未分配' }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">⭐</div>
          <div class="stat-content">
            <h3>综合评分</h3>
            <p class="stat-number">{{ studentInfo.totalScore || 0 }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📝</div>
          <div class="stat-content">
            <h3>申请次数</h3>
            <p class="stat-number">{{ studentInfo.totalApplications || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="student-charts">
        <div class="chart-card">
          <h3>宿舍评分详情</h3>
          <Charts
            chart-id="student-score-radar"
            :width="'100%'"
            :height="'300px'"
            :option="studentScoreRadarOption"
          />
        </div>

        <div class="chart-card">
          <h3>申请统计</h3>
          <Charts
            chart-id="student-application-pie"
            :width="'100%'"
            :height="'300px'"
            :option="studentApplicationChartOption"
          />
        </div>

        <div class="chart-card">
          <div class="chart-container">
            <Charts
              chart-id="student-bills-pie"
              :width="'100%'"
              :height="'350px'"
              :option="studentBillsChartOption"
            />
          </div>
        </div>

        <div class="chart-card">
          <h3>近期活动记录</h3>
          <div class="activity-timeline">
            <div class="activity-item" v-for="activity in studentInfo.recentActivities" :key="activity.id">
              <div class="activity-icon" :class="activity.type">{{ getActivityIcon(activity.type) }}</div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="chart-card">
          <h3>宿舍室友信息</h3>
          <div class="roommate-info">
            <div v-if="studentInfo.roommates && studentInfo.roommates.length > 0" class="roommate-item" v-for="roommate in studentInfo.roommates" :key="roommate.id">
              <div class="roommate-avatar">{{ roommate.name.charAt(0) }}</div>
              <div class="roommate-details">
                <div class="roommate-name">{{ roommate.name }}</div>
                <div class="roommate-bed">床位: {{ roommate.bedNumber }}</div>
              </div>
              <div class="roommate-status" :class="roommate.status">
                {{ roommate.status === 'online' ? '在线' : '离线' }}
              </div>
            </div>
            <div v-else class="no-roommates">
              <div class="no-roommates-icon">🏠</div>
              <div class="no-roommates-text">暂无室友</div>
            </div>
          </div>
        </div>

        <div class="chart-card full-width">
          <h3>评分趋势</h3>
          <Charts
            chart-id="student-score-trend"
            :width="'100%'"
            :height="'400px'"
            :option="studentScoreTrendOption"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAdminStatistics, getManagerStatistics, getStudentStatistics } from '@/api/dormitory/statistics'
import Charts from '@/components/Charts'

export default {
  name: 'Index',
  components: {
    Charts
  },
  data() {
    return {
      userRole: 'admin', // 默认角色，实际应从用户信息获取
      overviewData: {},
      bedStats: [],
      repairStats: [],
      applicationStats: [],
      managerData: {},
      studentInfo: {
        name: '加载中...',
        studentId: '加载中...',
        dormNumber: '加载中...',
        bedNumber: '加载中...',
        floor: '加载中...',
        hygieneScore: 0,
        disciplineScore: 0,
        safetyScore: 0,
        totalScore: 0,
        exchangeApplications: 0,
        comeApplications: 0,
        repairApplications: 0,
        visitRecords: 0,
        billsStats: {
          monthlyAmount: 0,
          paidAmount: 0,
          unpaidAmount: 0
        },
        recentActivities: [],
        roommates: [],
        scoreTrend: []
      }
    }
  },
  computed: {
    occupancyRate() {
      if (this.overviewData.totalBeds && this.overviewData.occupiedBeds) {
        return Math.round((this.overviewData.occupiedBeds / this.overviewData.totalBeds) * 100)
      }
      return 0
    },
    // 床位统计饼图配置
    bedStatsChartOption() {
      return {
        title: {
          text: '床位使用情况',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#2c3e50'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: this.bedStats.map(item => item.type)
        },
        series: [
          {
            name: '床位统计',
            type: 'pie',
            radius: '50%',
            center: ['50%', '60%'],
            data: this.bedStats.map(item => ({
              value: item.count,
              name: item.type,
              itemStyle: {
                color: item.color || '#3498db'
              }
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
    },
    // 维修统计柱状图配置
    repairStatsChartOption() {
      const colors = ['#e74c3c', '#f39c12', '#27ae60']
      return {
        title: {
          text: '维修申请状态',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#2c3e50'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.repairStats.map(item => item.status),
          axisLabel: {
            color: '#7f8c8d'
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            color: '#7f8c8d',
            fontSize: 12
          },
          axisLine: {
            lineStyle: {
              color: '#bdc3c7'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#ecf0f1',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: '数量',
            type: 'bar',
            data: this.repairStats.map((item, index) => ({
              value: item.count,
              itemStyle: {
                color: colors[index] || '#3498db'
              }
            })),
            barWidth: '60%'
          }
        ]
      }
    },
    // 申请处理统计柱状图配置
    applicationStatsChartOption() {
      const categories = this.applicationStats.map(cat => cat.name)
      const statusTypes = ['待审核', '已通过', '已拒绝']
      const colors = ['#f39c12', '#27ae60', '#e74c3c']

      const series = statusTypes.map((status, index) => ({
        name: status,
        type: 'bar',
        data: this.applicationStats.map(cat => {
          const item = cat.items.find(item => item.status === status)
          return item ? item.count : 0
        }),
        itemStyle: {
          color: colors[index]
        }
      }))

      return {
        title: {
          text: '申请处理统计',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#2c3e50'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: statusTypes,
          top: '10%'
        },
        grid: {
          left: '10%',
          right: '10%',
          bottom: '15%',
          top: '30%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: categories,
          axisLabel: {
            color: '#7f8c8d'
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            color: '#7f8c8d'
          }
        },
        series: series
      }
    },
    // 宿管维修申请处理饼图配置
    managerRepairStatsChartOption() {
      return {
        title: {
          text: '维修申请处理状态',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#2c3e50'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: this.managerData.repairStats ? this.managerData.repairStats.map(item => item.status) : []
        },
        series: [
          {
            name: '维修申请',
            type: 'pie',
            radius: '50%',
            center: ['50%', '60%'],
            data: this.managerData.repairStats ? this.managerData.repairStats.map(item => ({
              value: item.count,
              name: item.status,
              itemStyle: {
                color: item.status === '待处理' ? '#e74c3c' :
                       item.status === '处理中' ? '#f39c12' : '#27ae60'
              }
            })) : [],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
    },
    // 宿管工作统计雷达图配置
    managerWorkStatsChartOption() {
      const workData = [
        { name: '巡查次数', value: this.managerData.monthlyInspections || 0, max: 100 },
        { name: '处理维修', value: this.managerData.monthlyRepairs || 0, max: 50 },
        { name: '违规处理', value: this.managerData.monthlyViolations || 0, max: 30 },
        { name: '访客登记', value: this.managerData.monthlyVisitors || 0, max: 200 }
      ]

      return {
        title: {
          text: '本月工作统计',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#2c3e50'
          }
        },
        tooltip: {
          trigger: 'item'
        },
        radar: {
          indicator: workData.map(item => ({
            name: item.name,
            max: item.max
          })),
          center: ['50%', '60%'],
          radius: '60%'
        },
        series: [
          {
            name: '工作统计',
            type: 'radar',
            data: [
              {
                value: workData.map(item => item.value),
                name: '本月工作量',
                itemStyle: {
                  color: '#3498db'
                },
                areaStyle: {
                  color: 'rgba(52, 152, 219, 0.3)'
                }
              }
            ]
          }
        ]
      }
    },
    // 宿舍评分雷达图配置
    studentScoreRadarOption() {
      const indicators = [
        { name: '卫生评分', max: 100 },
        { name: '纪律评分', max: 100 },
        { name: '安全评分', max: 100 }
      ]

      return {
        title: {
          text: '宿舍评分统计',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#2c3e50'
          }
        },
        tooltip: {
          trigger: 'item'
        },
        radar: {
          indicator: indicators,
          center: ['50%', '60%'],
          radius: '70%'
        },
        series: [
          {
            name: '评分统计',
            type: 'radar',
            data: [
              {
                value: [
                  this.studentInfo.hygieneScore || 0,
                  this.studentInfo.disciplineScore || 0,
                  this.studentInfo.safetyScore || 0
                ],
                name: '当前评分',
                itemStyle: {
                  color: '#9b59b6'
                },
                areaStyle: {
                  color: 'rgba(155, 89, 182, 0.2)'
                }
              }
            ]
          }
        ]
      }
    },
    // 学生申请统计饼图配置
    studentApplicationChartOption() {
      const applicationData = [
        { name: '换宿申请', value: this.studentInfo.exchangeApplications || 0, color: '#3498db' },
        { name: '入住申请', value: this.studentInfo.comeApplications || 0, color: '#2ecc71' },
        { name: '维修申请', value: this.studentInfo.repairApplications || 0, color: '#e74c3c' }
      ]

      return {
        title: {
          text: '申请统计',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#2c3e50'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: applicationData.map(item => item.name)
        },
        series: [
          {
            name: '申请统计',
            type: 'pie',
            radius: '50%',
            center: ['50%', '60%'],
            data: applicationData.map(item => ({
              value: item.value,
              name: item.name,
              itemStyle: {
                color: item.color
              }
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
    },
    // 学生水电费统计环形图配置
    studentBillsChartOption() {
      const paidAmount = this.studentInfo.billsStats?.paidAmount || 0
      const unpaidAmount = this.studentInfo.billsStats?.unpaidAmount || 0
      const totalAmount = paidAmount + unpaidAmount

      // 构建数据数组，只包含有值的项
      const chartData = []
      if (paidAmount > 0) {
        chartData.push({
          value: paidAmount,
          name: '已缴费',
          itemStyle: {
            color: '#27ae60'
          }
        })
      }
      if (unpaidAmount > 0) {
        chartData.push({
          value: unpaidAmount,
          name: '未缴费',
          itemStyle: {
            color: '#e74c3c'
          }
        })
      }

      // 如果没有任何费用数据，显示一个占位项
      if (chartData.length === 0) {
        chartData.push({
          value: 1,
          name: '暂无费用',
          itemStyle: {
            color: '#95a5a6'
          }
        })
      }

      return {
        title: {
          text: '水电费统计',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#2c3e50'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: function(params) {
            if (params.name === '暂无费用') {
              return '暂无费用数据'
            }
            return params.seriesName + '<br/>' + params.name + ': ¥' + params.value + ' (' + params.percent + '%)'
          }
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: chartData.map(item => item.name)
        },
        series: [
          {
            name: '费用统计',
            type: 'pie',
            radius: ['30%', '55%'],
            center: ['50%', '50%'],
            data: chartData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label: {
              formatter: function(params) {
                if (params.name === '暂无费用') {
                  return '暂无费用'
                }
                return '¥' + params.value
              }
            }
          }
        ]
      }
    },
    // 学生评分趋势折线图配置
    studentScoreTrendOption() {
      const months = this.studentInfo.scoreTrend?.map(item => item.month) || []
      const hygieneData = this.studentInfo.scoreTrend?.map(item => item.hygiene) || []
      const disciplineData = this.studentInfo.scoreTrend?.map(item => item.discipline) || []
      const safetyData = this.studentInfo.scoreTrend?.map(item => item.safety) || []

      return {
        title: {
          text: '评分趋势',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#2c3e50'
          }
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['卫生评分', '纪律评分', '安全评分'],
          bottom: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: months,
          axisLabel: {
            color: '#7f8c8d'
          }
        },
        yAxis: {
          type: 'value',
          min: 0,
          max: 100,
          axisLabel: {
            color: '#7f8c8d'
          }
        },
        series: [
          {
            name: '卫生评分',
            type: 'line',
            data: hygieneData,
            itemStyle: {
              color: '#3498db'
            },
            smooth: true
          },
          {
            name: '纪律评分',
            type: 'line',
            data: disciplineData,
            itemStyle: {
              color: '#e74c3c'
            },
            smooth: true
          },
          {
            name: '安全评分',
            type: 'line',
            data: safetyData,
            itemStyle: {
              color: '#f39c12'
            },
            smooth: true
          }
        ]
      }
    },
    // 宿管楼层床位状态柱状图配置
    managerFloorStatusChartOption() {
      const floorNames = this.managerData.floorStatus ? this.managerData.floorStatus.map(floor => floor.floorName) : []
      const occupiedData = this.managerData.floorStatus ? this.managerData.floorStatus.map(floor => floor.occupied) : []
      const availableData = this.managerData.floorStatus ? this.managerData.floorStatus.map(floor => floor.available) : []

      return {
        title: {
          text: '楼层床位状态',
          left: 'center',
          textStyle: {
            fontSize: 14,
            color: '#2c3e50'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['已入住', '空闲'],
          top: '8%',
          textStyle: {
            fontSize: 12
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: floorNames,
          axisLabel: {
            color: '#7f8c8d',
            fontSize: 12,
            interval: 0,
            rotate: 0
          },
          axisLine: {
            lineStyle: {
              color: '#bdc3c7'
            }
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            color: '#7f8c8d'
          }
        },
        series: [
          {
            name: '已入住',
            type: 'bar',
            data: occupiedData,
            itemStyle: {
              color: '#27ae60'
            },
            barWidth: '35%',
            label: {
              show: true,
              position: 'top',
              fontSize: 11,
              color: '#2c3e50'
            }
          },
          {
            name: '空闲',
            type: 'bar',
            data: availableData,
            itemStyle: {
              color: '#95a5a6'
            },
            barWidth: '35%',
            label: {
              show: true,
              position: 'top',
              fontSize: 11,
              color: '#2c3e50'
            }
          }
        ]
      }
    }
  },

  mounted() {
    this.getUserRole()
    this.loadDashboardData()
  },
  methods: {
    getUserRole() {
      // 从用户信息或store中获取用户角色
      const userInfo = this.$store.getters && this.$store.getters.userInfo
      console.log('用户信息:', userInfo)

      if (userInfo && userInfo.roles && userInfo.roles.length > 0) {
        // 适配后端返回的角色数据格式
        let role = null

        // 检查roles是字符串数组还是对象数组
        if (typeof userInfo.roles[0] === 'string') {
          // 后端返回的是字符串数组，如 ["man", "admin"]
          role = userInfo.roles[0]
          console.log('角色数据为字符串数组，第一个角色:', role)
        } else if (userInfo.roles[0] && userInfo.roles[0].roleKey) {
          // 前端期望的对象数组格式，如 [{roleKey: "man"}]
          role = userInfo.roles[0].roleKey
          console.log('角色数据为对象数组，第一个角色Key:', role)
        }

        console.log('解析出的用户角色Key:', role)

        if (role === 'admin' || role === 'subadmin') {
          this.userRole = 'admin'
        } else if (role === 'manager' || role === 'man') {
          this.userRole = 'manager'
        } else {
          this.userRole = 'student'
        }
      } else {
        // 如果没有角色信息，默认设置为学生
        console.warn('未找到用户角色信息，默认设置为学生')
        this.userRole = 'student'
      }

      console.log('最终确定的用户角色:', this.userRole)
    },
    async loadDashboardData() {
      try {
        if (this.userRole === 'admin') {
          await this.loadAdminData()
        } else if (this.userRole === 'manager') {
          await this.loadManagerData()
        } else if (this.userRole === 'student') {
          await this.loadStudentData()
        }
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    async loadAdminData() {
      try {
        console.log('开始加载管理员数据...')

        // 调用后端API获取管理员统计数据
        const response = await getAdminStatistics()
        console.log('管理员统计数据:', response)

        if (response && response.data) {
          // 直接从response.data获取数据，后端返回的是扁平化结构
          const data = response.data

          // 映射后端返回的字段到前端期望的结构
          this.overviewData = {
            totalDorms: data.totalRooms || 0,        // 后端返回totalRooms，前端显示为总宿舍数
            totalBeds: data.totalBeds || 0,          // 总床位数
            occupiedBeds: data.occupiedBeds || 0,    // 已入住床位数
            totalStudents: data.totalStudents || 0,  // 总学生数
            occupancyRate: data.occupancyRate || 0,  // 入住率
            totalFloors: data.totalFloors || 0       // 总楼层数
          }

          // 构建床位统计数据
          const totalBeds = data.totalBeds || 0
          const occupiedBeds = data.occupiedBeds || 0
          const availableBeds = totalBeds - occupiedBeds

          this.bedStats = [
            {
              type: '已入住',
              count: occupiedBeds,
              percentage: totalBeds > 0 ? Math.round((occupiedBeds / totalBeds) * 100) : 0,
              color: '#3498db'
            },
            {
              type: '空闲',
              count: availableBeds,
              percentage: totalBeds > 0 ? Math.round((availableBeds / totalBeds) * 100) : 0,
              color: '#95a5a6'
            }
          ]

          // 构建维修统计数据
          this.repairStats = [
            {
              status: '待处理',
              count: data.pendingRepairs || 0
            },
            {
              status: '处理中',
              count: data.processingRepairs || 0
            },
            {
              status: '已完成',
              count: data.completedRepairs || 0
            }
          ]

          // 构建申请处理统计数据
          this.applicationStats = [
            {
              name: '换宿申请',
              items: [
                { status: '待审核', count: data.pendingExchangeApps || 0 },
                { status: '已通过', count: data.approvedExchangeApps || 0 },
                { status: '已拒绝', count: data.rejectedExchangeApps || 0 }
              ]
            },
            {
              name: '入住申请',
              items: [
                { status: '待审核', count: data.pendingComeApps || 0 },
                { status: '已通过', count: data.approvedComeApps || 0 },
                { status: '已拒绝', count: data.rejectedComeApps || 0 }
              ]
            }
          ]


          console.log('处理后的数据:', {
            overviewData: this.overviewData,
            bedStats: this.bedStats,
            repairStats: this.repairStats,
            applicationStats: this.applicationStats
          })
        }
      } catch (error) {
        console.error('加载管理员数据失败:', error)
        // 使用默认数据
        this.overviewData = {
          totalDorms: 0,
          totalBeds: 0,
          occupiedBeds: 0,
          totalStudents: 0,
          occupancyRate: 0,
          totalFloors: 0
        }
        this.bedStats = []
        this.repairStats = []
        this.applicationStats = []
      }
    },
    async loadManagerData() {
      try {
        console.log('开始加载宿管数据...')

        // 调用后端API获取宿管统计数据
        const response = await getManagerStatistics()
        console.log('宿管统计数据:', response)

        if (response && response.data) {
          // 映射后端返回的字段到前端期望的结构
          const data = response.data

          this.managerData = {
            managedFloors: data.managedFloors || 0,
            managedBeds: data.managedBeds || 0,
            pendingRepairs: data.pendingRepairs || 0,
            occupiedBeds: data.occupiedBeds || 0,
            occupancyRate: data.occupancyRate || 0,
            monthlyInspections: data.monthlyInspections || 0,
            monthlyRepairs: data.monthlyRepairs || 0,
            monthlyViolations: data.monthlyViolations || 0,
            monthlyVisitors: data.monthlyVisitors || 0,

            // 维修申请处理统计
            repairStats: [
              {
                status: '待处理',
                count: data.pendingRepairs || 0,
                percentage: this.calculatePercentage(data.pendingRepairs, data.totalRepairs)
              },
              {
                status: '处理中',
                count: data.processingRepairs || 0,
                percentage: this.calculatePercentage(data.processingRepairs, data.totalRepairs)
              },
              {
                status: '已完成',
                count: data.completedRepairs || 0,
                percentage: this.calculatePercentage(data.completedRepairs, data.totalRepairs)
              }
            ],

            // 楼层床位状态
            floorStatus: this.generateFloorStatus(data)
          }

          console.log('处理后的宿管数据:', this.managerData)
        }
      } catch (error) {
        console.error('加载宿管数据失败:', error)
        // 使用默认数据
        this.managerData = {
          managedFloors: 0,
          managedBeds: 0,
          pendingRepairs: 0,
          occupiedBeds: 0,
          occupancyRate: 0,
          monthlyInspections: 0,
          monthlyRepairs: 0,
          monthlyViolations: 0,
          monthlyVisitors: 0,
          repairStats: [],
          floorStatus: []
        }
      }
    },
    async loadStudentData() {
      try {
        console.log('开始加载学生数据...')

        // 调用后端API获取学生统计数据
        const response = await getStudentStatistics()
        console.log('学生统计数据:', response)

        if (response && response.data) {
          const data = response.data
          console.log('处理后端返回数据:', data)

          // 处理学生基本信息
          const studentInfo = data.studentInfo || {}
          const dormFloor = studentInfo.dormFloor || {}
          const dormDormitory = studentInfo.dormDormitory || {}

          // 处理床位信息
          const bedInfo = data.bedInfo || {}

          // 处理评分信息
          const dormScores = data.dormScores || []
          const latestScore = dormScores.length > 0 ? dormScores[0] : {}

          // 处理水电费统计 - 确保有默认值
          const billsStats = data.billsStats || {
            monthlyAmount: 0,
            paidAmount: 0,
            unpaidAmount: 0
          }

          // 映射到前端期望的字段结构
          this.studentInfo = {
            name: studentInfo.stuName || '未知',
            studentId: studentInfo.stuCode || '未知',
            dormNumber: dormDormitory.dorName || '未分配',
            bedNumber: bedInfo.bedCode || '未分配',
            floor: dormFloor.fName || '未分配',
            // 评分信息 - 使用后端返回的真实评分数据
            hygieneScore: latestScore.hygieneScore || latestScore.totalScore || 0,
            disciplineScore: latestScore.disciplineScore || latestScore.totalScore || 0,
            safetyScore: latestScore.safetyScore || latestScore.totalScore || 0,
            totalScore: latestScore.totalScore || 0,
            // 统计信息
            exchangeApplications: data.exchangeApplications || 0,
            comeApplications: data.comeApplications || 0,
            repairApplications: data.repairApplications || 0,
            visitRecords: data.visitRecords || 0,
            // 水电费统计 - 使用安全的默认值
            billsStats: {
              monthlyAmount: Number(billsStats.monthlyAmount) || 0,
              paidAmount: Number(billsStats.paidAmount) || 0,
              unpaidAmount: Math.max(0, Number(billsStats.monthlyAmount || 0) - Number(billsStats.paidAmount || 0)),
              paymentStatus: this.getPaymentStatus(billsStats.paidAmount || 0, billsStats.monthlyAmount || 0)
            },
            // 使用后端返回的真实数据，如果没有则使用空数组
            recentActivities: data.recentActivities || [],
            roommates: data.roommates || [],
            scoreTrend: data.scoreTrend || dormScores.slice(0, 6) || [] // 使用最近6个月的评分记录
          }

          console.log('映射后的学生信息:', this.studentInfo)
        }
      } catch (error) {
        console.error('加载学生数据失败:', error)
        // 使用默认数据
        this.studentInfo = {
          name: '未知',
          studentId: '未知',
          dormNumber: '未分配',
          bedNumber: '未分配',
          floor: '未分配',
          hygieneScore: 0,
          disciplineScore: 0,
          safetyScore: 0,
          totalScore: 0,
          exchangeApplications: 0,
          comeApplications: 0,
          repairApplications: 0,
          visitRecords: 0,
          billsStats: {
            monthlyAmount: 0,
            paidAmount: 0,
            unpaidAmount: 0,
            paymentStatus: 'none'
          },
          recentActivities: [],
          roommates: [],
          scoreTrend: []
        }
      }
    },
    processBedStats(data) {
      if (Array.isArray(data)) {
        const total = data.reduce((sum, item) => sum + (item.count || 0), 0)
        this.bedStats = data.map(item => ({
          type: item.type || item.name,
          count: item.count || 0,
          percentage: total > 0 ? Math.round((item.count / total) * 100) : 0
        }))
      }
    },
    processRepairStats(data) {
      if (Array.isArray(data)) {
        this.repairStats = data.map(item => ({
          status: item.status || item.name,
          count: item.count || 0
        }))
      }
    },

    // 计算百分比的辅助方法
    calculatePercentage(value, total) {
      if (!total || total === 0) return 0
      return Math.round((value / total) * 100)
    },

    // 生成楼层状态数据 - 使用后端返回的真实数据
    generateFloorStatus(data) {
      const floors = []

      // 如果后端返回了楼层占用率数据，直接使用
      if (data.floorOccupancy && Array.isArray(data.floorOccupancy)) {
        return data.floorOccupancy.map(floor => ({
          floorName: floor.floorName || `${floor.floorId}楼`,
          totalBeds: floor.totalBeds || 0,
          occupied: floor.occupiedBeds || 0,
          available: (floor.totalBeds || 0) - (floor.occupiedBeds || 0),
          occupancyRate: Math.round(floor.occupancyRate || 0)
        }))
      }

      // 如果没有详细楼层数据，使用管理的楼层数和床位数
      const floorCount = data.managedFloors || 3
      const totalManagedBeds = data.managedBeds || 0
      const totalOccupiedBeds = data.occupiedBeds || 0

      // 平均分配床位到各楼层
      const bedsPerFloor = Math.floor(totalManagedBeds / floorCount)
      const occupiedPerFloor = Math.floor(totalOccupiedBeds / floorCount)

      for (let i = 1; i <= floorCount; i++) {
        const totalBeds = bedsPerFloor
        const occupied = occupiedPerFloor
        const available = totalBeds - occupied
        const occupancyRate = totalBeds > 0 ? Math.round((occupied / totalBeds) * 100) : 0

        floors.push({
          floorName: `${i}楼`,
          totalBeds,
          occupied,
          available,
          occupancyRate
        })
      }

      return floors
    },

    // 获取支付状态
    getPaymentStatus(paidAmount, totalAmount) {
      if (!totalAmount || totalAmount === 0) return 'none'
      if (paidAmount >= totalAmount) return 'paid'
      if (paidAmount > 0) return 'partial'
      return 'unpaid'
    },

    // 获取支付状态文本
    getPaymentStatusText(status) {
      const statusMap = {
        'paid': '已缴清',
        'partial': '部分缴费',
        'unpaid': '未缴费',
        'none': '无费用'
      }
      return statusMap[status] || '未知状态'
    },

    // 获取活动图标
    getActivityIcon(type) {
      const iconMap = {
        'application': '📝',
        'score': '⭐',
        'visit': '👥',
        'repair': '🔧',
        'bill': '💰'
      }
      return iconMap[type] || '📋'
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 30px;
}

.dashboard-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.dashboard-header p {
  color: #7f8c8d;
  font-size: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
}

.stat-icon {
  font-size: 2.5em;
  margin-right: 15px;
}

.stat-content h3 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 14px;
}

.stat-number {
  font-size: 2em;
  font-weight: bold;
  color: #3498db;
  margin: 0;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  background: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: visible;
}

.chart-card h3 {
  margin-top: 0;
  color: #2c3e50;
}

.simple-chart {
  margin-top: 15px;
}

.chart-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.chart-label {
  width: 80px;
  font-size: 12px;
}

.chart-bar {
  flex: 1;
  height: 20px;
  background-color: #ecf0f1;
  border-radius: 10px;
  margin: 0 10px;
  overflow: hidden;
}

.chart-fill {
  height: 100%;
  background-color: #3498db;
  transition: width 0.3s ease;
}

.chart-value {
  width: 40px;
  text-align: right;
  font-weight: bold;
}

/* 楼层床位状态图表专用样式 */
.floor-status-chart {
  min-height: 480px !important;
  overflow: visible !important;
}

.floor-status-chart h3 {
  margin-bottom: 10px;
  font-size: 14px;
}

.repair-stats {
  margin-top: 15px;
}

.repair-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #ecf0f1;
}

.repair-label {
  color: #2c3e50;
}

.repair-count {
  font-weight: bold;
  color: #e74c3c;
}

.manager-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.student-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.student-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

/* 评分详情样式 */
.score-details {
  margin-top: 15px;
}

.score-item-detailed {
  margin-bottom: 20px;
}

.score-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.score-label {
  color: #2c3e50;
  font-weight: 500;
}

.score-value {
  font-weight: bold;
  color: #3498db;
  font-size: 16px;
}

.score-bar {
  height: 12px;
  background-color: #ecf0f1;
  border-radius: 6px;
  overflow: hidden;
}

.score-fill {
  height: 100%;
  border-radius: 6px;
  transition: width 0.3s ease;
}

.score-fill.hygiene {
  background-color: #27ae60;
}

.score-fill.discipline {
  background-color: #3498db;
}

.score-fill.safety {
  background-color: #e74c3c;
}

/* 申请统计样式 */
.application-overview {
  margin-top: 15px;
}

.app-stat-item {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 10px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #3498db;
}

.app-icon {
  font-size: 2em;
  margin-right: 15px;
}

.app-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.app-name {
  color: #2c3e50;
  font-weight: 500;
  margin-bottom: 5px;
}

.app-count {
  font-size: 1.5em;
  font-weight: bold;
  color: #3498db;
}

/* 水电费统计样式 */
.bills-student {
  margin-top: 15px;
}

.bill-summary {
  margin-bottom: 15px;
}

.bill-total, .bill-paid, .bill-unpaid {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ecf0f1;
}

.payment-status {
  text-align: center;
  padding: 10px;
  border-radius: 6px;
  font-weight: bold;
  color: white;
}

.payment-status.paid {
  background-color: #27ae60;
}

.payment-status.partial {
  background-color: #f39c12;
}

.payment-status.unpaid {
  background-color: #e74c3c;
}

.payment-status.none {
  background-color: #95a5a6;
}

/* 活动记录样式 */
.activity-timeline {
  margin-top: 15px;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ecf0f1;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 1.2em;
  background-color: #f8f9fa;
}

.activity-icon.application {
  background-color: #e3f2fd;
}

.activity-icon.score {
  background-color: #fff3e0;
}

.activity-icon.visit {
  background-color: #f3e5f5;
}

.activity-icon.repair {
  background-color: #ffebee;
}

.activity-icon.bill {
  background-color: #e8f5e8;
}

.activity-content {
  flex: 1;
}

.activity-title {
  color: #2c3e50;
  font-weight: 500;
  margin-bottom: 4px;
}

.activity-time {
  color: #7f8c8d;
  font-size: 12px;
}

/* 室友信息样式 */
.roommate-info {
  margin-top: 15px;
}

.roommate-item {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 10px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.roommate-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #3498db;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.2em;
  margin-right: 15px;
}

.roommate-details {
  flex: 1;
}

.roommate-name {
  color: #2c3e50;
  font-weight: 500;
  margin-bottom: 4px;
}

.roommate-bed {
  color: #7f8c8d;
  font-size: 12px;
}

.roommate-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.roommate-status.online {
  background-color: #d4edda;
  color: #155724;
}

.roommate-status.offline {
  background-color: #f8d7da;
  color: #721c24;
}

/* 暂无室友样式 */
.no-roommates {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #95a5a6;
}

.no-roommates-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.no-roommates-text {
  font-size: 16px;
  font-weight: 500;
}

/* 评分趋势样式 */
.score-trend {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 250px;
  padding: 20px 0;
  border-bottom: 2px solid #ecf0f1;
}

.trend-month {
  font-size: 12px;
  color: #7f8c8d;
  margin-bottom: 10px;
  font-weight: 500;
  text-align: center;
}

.trend-bars {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 2px;
  height: 150px;
  margin-bottom: 10px;
}

.trend-bar.hygiene {
  background-color: #27ae60;
}

.trend-bar.discipline {
  background-color: #3498db;
}

.trend-bar.safety {
  background-color: #e74c3c;
}

.trend-scores {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.trend-scores span {
  font-size: 10px;
  font-weight: bold;
}

.hygiene-score {
  color: #27ae60;
}

.discipline-score {
  color: #3498db;
}

.safety-score {
  color: #e74c3c;
}

/* 宿管样式增强 */
.manager-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.repair-progress {
  margin-top: 15px;
}

.repair-bar {
  flex: 1;
  height: 20px;
  background-color: #ecf0f1;
  border-radius: 10px;
  margin: 0 10px;
  overflow: hidden;
}

.repair-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.3s ease;
}

.repair-fill.status-待处理 {
  background-color: #e74c3c;
}

.repair-fill.status-处理中 {
  background-color: #f39c12;
}

.repair-fill.status-已完成 {
  background-color: #27ae60;
}

.work-stats {
  margin-top: 15px;
}

.work-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ecf0f1;
}

.work-item:last-child {
  border-bottom: none;
}

.work-label {
  color: #2c3e50;
  font-weight: 500;
}

.work-value {
  font-weight: bold;
  color: #3498db;
  font-size: 16px;
}

.floor-status {
  margin-top: 15px;
}

.floor-item {
  margin-bottom: 15px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.floor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.floor-name {
  color: #2c3e50;
  font-weight: bold;
}

.floor-rate {
  color: #3498db;
  font-weight: bold;
}

.floor-beds {
  margin-top: 8px;
}

.bed-stats {
  display: flex;
  gap: 20px;
}

.bed-occupied {
  color: #27ae60;
  font-size: 12px;
}

.bed-available {
  color: #7f8c8d;
  font-size: 12px;
}
</style>





