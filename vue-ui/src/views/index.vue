<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>宿舍管理系统</h1>
      <p>欢迎使用宿舍管理系统，这里是您的数据概览</p>
    </div>

    <!-- 管理员视图 -->
    <div v-if="userRole === 'admin'" class="admin-dashboard">
      <!-- 顶部核心指标卡片 -->
      <div class="admin-stats-row">
        <div class="admin-stat-card" v-for="card in systemStatCards" :key="card.key">
          <div class="admin-stat-header">
            <div class="admin-stat-icon" :style="{ background: card.gradient }">
              <i :class="card.icon"></i>
            </div>
            <span class="admin-stat-trend" :class="card.trend">
              <i :class="card.trendIcon"></i>
              {{ card.trendText }}
            </span>
          </div>
          <div class="admin-stat-body">
            <h4 class="admin-stat-title">{{ card.title }}</h4>
            <p class="admin-stat-value">{{ card.value }}</p>
            <p class="admin-stat-desc">{{ card.subtext }}</p>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="admin-charts-container">
        <!-- 顶部大图表 -->
        <div class="chart-wrapper chart-full">
          <div class="chart-header-modern">
            <div class="chart-title-group">
              <h3 class="chart-title-main">系统请求趋势</h3>
              <span class="chart-subtitle">近7日数据监控</span>
            </div>
            <div class="chart-metrics">
              <div class="metric-item">
                <span class="metric-label">今日</span>
                <span class="metric-value primary">{{ formatNumber(systemStats.requestsToday) }}</span>
              </div>
              <div class="metric-item">
                <span class="metric-label">7日</span>
                <span class="metric-value">{{ formatNumber(systemStats.requests7Days) }}</span>
              </div>
            </div>
          </div>
          <Charts
            key="admin-request-trend-chart"
            chart-id="requestTrendChart"
            :option="requestTrendOption"
            height="360px"
          />
        </div>

        <!-- 底部两个并排图表 -->
        <div class="chart-wrapper chart-half">
          <div class="chart-header-modern">
            <div class="chart-title-group">
              <h3 class="chart-title-main">登录趋势</h3>
              <span class="chart-subtitle">成功/失败对比</span>
            </div>
            <div class="chart-metrics compact">
              <div class="metric-item success">
                <i class="el-icon-success"></i>
                <span class="metric-value">{{ formatNumber(systemStats.loginSuccessToday) }}</span>
              </div>
              <div class="metric-item danger">
                <i class="el-icon-error"></i>
                <span class="metric-value">{{ formatNumber(systemStats.loginFailToday) }}</span>
              </div>
            </div>
          </div>
          <Charts
            key="admin-login-trend-chart"
            chart-id="loginTrendChart"
            :option="loginTrendOption"
            height="300px"
          />
        </div>

        <div class="chart-wrapper chart-half">
          <div class="chart-header-modern">
            <div class="chart-title-group">
              <h3 class="chart-title-main">热门功能</h3>
              <span class="chart-subtitle">访问量TOP排行</span>
            </div>
          </div>
          <Charts
            key="admin-top-module-chart"
            chart-id="topModuleChart"
            :option="topModuleOption"
            height="300px"
          />
        </div>
      </div>

      <!-- 底部洞察面板 -->
      <div class="admin-insights-panel">
        <div class="insight-card" v-for="(insight, index) in systemInsights" :key="index">
          <div class="insight-icon-wrapper" :class="insight.type">
            <i :class="insight.icon"></i>
          </div>
          <div class="insight-details">
            <h4 class="insight-label">{{ insight.label }}</h4>
            <p class="insight-main-value">{{ insight.value }}</p>
            <p class="insight-sub-info">{{ insight.subInfo }}</p>
          </div>
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
            <p class="stat-subtext">共 {{ managerData.managedRooms || 0 }} 间宿舍</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">🛏️</div>
          <div class="stat-content">
            <h3>床位总数</h3>
            <p class="stat-number">{{ managerData.managedBeds || 0 }}</p>
            <p class="stat-subtext">空闲 {{ (managerData.managedBeds || 0) - (managerData.occupiedBeds || 0) }} 张</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <h3>已入住床位</h3>
            <p class="stat-number">{{ managerData.occupiedBeds || 0 }}</p>
            <p class="stat-subtext">占用率 {{ managerData.occupancyRate || 0 }}%</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">🔧</div>
          <div class="stat-content">
            <h3>待处理维修</h3>
            <p class="stat-number">{{ managerData.pendingRepairs || 0 }}</p>
            <p class="stat-subtext">本月完成 {{ managerData.monthlyRepairs || 0 }} 次</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">🚪</div>
          <div class="stat-content">
            <h3>待处理访客</h3>
            <p class="stat-number">{{ managerData.pendingVisits || 0 }}</p>
            <p class="stat-subtext">本月处理 {{ managerData.monthlyVisitors || 0 }} 次</p>
          </div>
        </div>
      </div>

      <div class="manager-charts">
        <div class="chart-card">
          <h3>楼层床位状态</h3>
          <div class="floor-status">
            <div class="floor-item" v-for="floor in managerData.floorStatus" :key="floor.floorName">
              <div class="floor-header">
                <div class="floor-name">{{ floor.floorName }}</div>
                <div class="floor-rate">占用 {{ floor.occupancyRate || 0 }}%</div>
              </div>
              <div class="floor-beds">
                <span class="bed-occupied">已入住: {{ floor.occupied || 0 }}</span>
                <span class="bed-available">空闲: {{ floor.available || 0 }}</span>
              </div>
            </div>
            <div v-if="!managerData.floorStatus || managerData.floorStatus.length === 0" class="no-data">暂无楼层数据</div>
          </div>
        </div>

        <div class="chart-card">
          <h3>维修申请状态</h3>
          <Charts key="manager-repair-chart" chart-id="managerRepairChart" :option="managerRepairStatsChartOption" height="360px" />
        </div>

        <div class="chart-card">
          <h3>本月工作统计</h3>
          <Charts key="manager-work-chart" chart-id="managerWorkChart" :option="managerWorkStatsChartOption" height="360px" />
        </div>
      </div>
    </div>

    <!-- 学生视图 -->
    <div v-else class="student-dashboard">
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
            key="student-score-radar-chart"
            chart-id="student-score-radar"
            :width="'100%'"
            :height="'300px'"
            :option="studentScoreRadarOption"
          />
        </div>

        <div class="chart-card">
          <h3>申请统计</h3>
          <Charts
            key="student-application-pie-chart"
            chart-id="student-application-pie"
            :width="'100%'"
            :height="'300px'"
            :option="studentApplicationChartOption"
          />
        </div>

        <div class="chart-card">
          <h3>水电费统计</h3>
          <Charts
            key="student-bills-pie-chart"
            chart-id="student-bills-pie"
            :width="'100%'"
            :height="'300px'"
            :option="studentBillsChartOption"
          />
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
            <div v-if="studentInfo.roommates && studentInfo.roommates.length > 0">
              <div class="roommate-item" v-for="roommate in studentInfo.roommates" :key="roommate.id">
                <div class="roommate-avatar">{{ roommate.name.charAt(0) }}</div>
                <div class="roommate-details">
                  <div class="roommate-name">{{ roommate.name }}</div>
                  <div class="roommate-bed">床位: {{ roommate.bedNumber }}</div>
                </div>
                <div class="roommate-status" :class="roommate.status">
                  {{ roommate.status === 'online' ? '在线' : '离线' }}
                </div>
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
            key="student-score-trend-chart"
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
      systemStats: {
        requestsToday: 0,
        requestsYesterday: 0,
        requests7Days: 0,
        requests30Days: 0,
        errorRequestsToday: 0,
        avgLatencyToday: 0,
        activeOperatorsToday: 0,
        loginSuccessToday: 0,
        loginFailToday: 0,
        newUsersToday: 0,
        newUsersThisMonth: 0,
        currentUsers: 0
      },
      requestTrend: [],
      loginTrend: [],
      topModules: [],
      recentErrors: [],
      recentFailedLogins: [],
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
    systemStatCards() {
      const stats = this.systemStats || {};
      const format = this.formatNumber;
      const growth = this.calculateGrowth(stats.requestsToday, stats.requestsYesterday);
      const loginSuccessRate = stats.loginSuccessToday + stats.loginFailToday > 0
        ? Math.round((stats.loginSuccessToday / (stats.loginSuccessToday + stats.loginFailToday)) * 100)
        : 0;

      return [
        {
          key: 'requestsToday',
          title: '今日请求',
          value: format(stats.requestsToday),
          subtext: `较昨日 ${growth >= 0 ? '+' : ''}${growth}%`,
          icon: 'el-icon-s-data',
          gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          trend: growth >= 0 ? 'up' : 'down',
          trendIcon: growth >= 0 ? 'el-icon-top' : 'el-icon-bottom',
          trendText: growth >= 0 ? '增长' : '下降'
        },
        {
          key: 'loginSuccess',
          title: '登录成功率',
          value: `${loginSuccessRate}%`,
          subtext: `今日成功 ${format(stats.loginSuccessToday)} 次`,
          icon: 'el-icon-user-solid',
          gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
          trend: loginSuccessRate >= 95 ? 'up' : 'stable',
          trendIcon: loginSuccessRate >= 95 ? 'el-icon-check' : 'el-icon-minus',
          trendText: loginSuccessRate >= 95 ? '优秀' : '正常'
        },
        {
          key: 'activeUsers',
          title: '活跃用户',
          value: format(stats.activeOperatorsToday),
          subtext: `系统总用户 ${format(stats.currentUsers)}`,
          icon: 'el-icon-s-custom',
          gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
          trend: 'up',
          trendIcon: 'el-icon-star-on',
          trendText: '在线'
        },
        {
          key: 'avgLatency',
          title: '响应时间',
          value: `${format(stats.avgLatencyToday)}ms`,
          subtext: `异常 ${format(stats.errorRequestsToday)} 次`,
          icon: 'el-icon-lightning',
          gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
          trend: stats.avgLatencyToday < 100 ? 'up' : 'stable',
          trendIcon: stats.avgLatencyToday < 100 ? 'el-icon-success' : 'el-icon-warning',
          trendText: stats.avgLatencyToday < 100 ? '快速' : '一般'
        }
      ];
    },

    systemInsights() {
      const stats = this.systemStats || {};
      const format = this.formatNumber;

      return [
        {
          type: 'primary',
          icon: 'el-icon-data-analysis',
          label: '近7日请求总量',
          value: format(stats.requests7Days),
          subInfo: `30日累计 ${format(stats.requests30Days)}`
        },
        {
          type: 'success',
          icon: 'el-icon-message-solid',
          label: '今日登录成功',
          value: format(stats.loginSuccessToday),
          subInfo: `失败 ${format(stats.loginFailToday)} 次`
        },
        {
          type: 'warning',
          icon: 'el-icon-time',
          label: '平均响应耗时',
          value: `${format(stats.avgLatencyToday)} ms`,
          subInfo: `异常请求 ${format(stats.errorRequestsToday)}`
        },
        {
          type: 'info',
          icon: 'el-icon-user',
          label: '新增用户',
          value: format(stats.newUsersToday),
          subInfo: `本月新增 ${format(stats.newUsersThisMonth)}`
        }
      ];
    },

    requestTrendOption() {
      const dates = this.requestTrend.map(item => this.extractString(item, ['stat_date', 'statDate', 'statDateStr', 'date', 'day', 'label', 'name']));
      const totals = this.requestTrend.map(item => this.extractNumber(item, ['total', 'totalRequests', 'requestCount', 'count', 'value', 'totalCount']));
  const errors = this.requestTrend.map(item => this.extractNumber(item, ['errors', 'errorCount', 'exceptionCount', 'errorRequests', 'failCount', 'errorTotal']));
      return {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['请求总量', '异常次数']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '请求总量',
            type: 'line',
            smooth: true,
            data: totals,
            areaStyle: {
              color: 'rgba(64,158,255,0.2)'
            },
            lineStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '异常次数',
            type: 'line',
            smooth: true,
            data: errors,
            areaStyle: {
              color: 'rgba(245,108,108,0.15)'
            },
            lineStyle: {
              color: '#F56C6C'
            }
          }
        ]
      };
    },
    loginTrendOption() {
  const dates = this.loginTrend.map(item => this.extractString(item, ['stat_date', 'statDate', 'statDateStr', 'date', 'day', 'label', 'name']));
  const success = this.loginTrend.map(item => this.extractNumber(item, ['success_count', 'successCount', 'success', 'loginSuccess', 'successTotal', 'successes']));
  const fail = this.loginTrend.map(item => this.extractNumber(item, ['fail_count', 'failCount', 'fail', 'loginFail', 'error', 'failure', 'failureCount', 'failTotal']));
      return {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['成功', '失败']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '成功',
            type: 'line',
            smooth: true,
            data: success,
            lineStyle: {
              color: '#67C23A'
            }
          },
          {
            name: '失败',
            type: 'line',
            smooth: true,
            data: fail,
            lineStyle: {
              color: '#E6A23C'
            }
          }
        ]
      };
    },
    topModuleOption() {
  const names = this.topModules.map(item => this.extractString(item, ['module_name', 'moduleName', 'moduleKey', 'moduleCode', 'name', 'title']));
  const totals = this.topModules.map(item => this.extractNumber(item, ['total', 'totalRequests', 'requestCount', 'count', 'visits', 'hits']));
  const errors = this.topModules.map(item => this.extractNumber(item, ['errors', 'errorCount', 'exceptionCount', 'failCount', 'errorHits']));
      return {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['请求次数', '异常次数']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '8%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: names,
          axisLabel: {
            interval: 0,
            rotate: 20
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '请求次数',
            type: 'bar',
            data: totals,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '异常次数',
            type: 'bar',
            data: errors,
            itemStyle: {
              color: '#F56C6C'
            }
          }
        ]
      };
    },
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
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'horizontal',
          left: 'center',
          bottom: '5%',
          data: this.managerData.repairStats ? this.managerData.repairStats.map(item => item.status) : [],
          itemGap: 20
        },
        series: [
          {
            name: '维修申请',
            type: 'pie',
            radius: ['35%', '55%'],
            center: ['50%', '45%'],
            avoidLabelOverlap: true,
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}: {c}',
              fontSize: 11,
              color: '#666'
            },
            labelLine: {
              show: true,
              length: 8,
              length2: 5,
              smooth: true
            },
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
    // 宿管工作统计柱状图配置
    managerWorkStatsChartOption() {
      const workData = [
        { name: '完成维修', value: this.managerData.monthlyRepairs || 0, color: '#67c23a' },
        { name: '处理访客', value: this.managerData.monthlyVisitors || 0, color: '#409eff' },
        { name: '待处理维修', value: this.managerData.pendingRepairs || 0, color: '#f56c6c' },
        { name: '待处理访客', value: this.managerData.pendingVisits || 0, color: '#909399' }
      ]

      return {
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
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: workData.map(item => item.name),
          axisLabel: {
            rotate: 0,
            fontSize: 12
          }
        },
        yAxis: {
          type: 'value',
          name: '数量'
        },
        series: [
          {
            name: '工作量',
            type: 'bar',
            data: workData.map(item => ({
              value: item.value,
              itemStyle: {
                color: item.color
              }
            })),
            barWidth: '50%',
            label: {
              show: true,
              position: 'top',
              fontSize: 12,
              fontWeight: 'bold'
            }
          }
        ]
      }
    },
    // 学生视图专用图表配置
    studentScoreRadarOption() {
      const indicators = [
        { name: '卫生评分', max: 100 },
        { name: '纪律评分', max: 100 },
        { name: '安全评分', max: 100 }
      ]

      return {
        tooltip: {
          trigger: 'item'
        },
        radar: {
          indicator: indicators,
          center: ['50%', '55%'],
          radius: '65%',
          splitNumber: 4,
          shape: 'polygon',
          name: {
            textStyle: {
              color: '#333',
              fontSize: 14,
              fontWeight: 500
            }
          },
          splitArea: {
            show: true,
            areaStyle: {
              color: ['rgba(114, 172, 209, 0.05)', 'rgba(114, 172, 209, 0.1)',
                      'rgba(114, 172, 209, 0.15)', 'rgba(114, 172, 209, 0.2)']
            }
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(114, 172, 209, 0.5)'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(114, 172, 209, 0.5)'
            }
          }
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
                  color: 'rgba(155, 89, 182, 0.3)'
                },
                lineStyle: {
                  width: 2,
                  color: '#9b59b6'
                },
                label: {
                  show: true,
                  formatter: function(params) {
                    return params.value
                  },
                  fontSize: 12,
                  color: '#9b59b6',
                  fontWeight: 'bold'
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
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'horizontal',
          left: 'center',
          bottom: '5%',
          data: applicationData.map(item => item.name),
          itemGap: 20
        },
        series: [
          {
            name: '申请统计',
            type: 'pie',
            radius: ['35%', '55%'],
            center: ['50%', '45%'],
            avoidLabelOverlap: true,
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}: {c}',
              fontSize: 11,
              color: '#666'
            },
            labelLine: {
              show: true,
              length: 8,
              length2: 5,
              smooth: true
            },
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
          orient: 'horizontal',
          left: 'center',
          bottom: '5%',
          data: chartData.map(item => item.name),
          itemGap: 20
        },
        series: [
          {
            name: '费用统计',
            type: 'pie',
            radius: ['35%', '55%'],
            center: ['50%', '45%'],
            data: chartData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label: {
              show: true,
              position: 'outside',
              formatter: function(params) {
                if (params.name === '暂无费用') {
                  return '暂无费用'
                }
                return params.name + ': ¥' + params.value
              },
              fontSize: 11,
              color: '#666'
            },
            labelLine: {
              show: true,
              length: 8,
              length2: 5,
              smooth: true
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
          // 直接从response.data获取数据，后端返回系统级统计的扁平结构
          const data = response.data

          // 映射后端系统级指标到前端
          this.systemStats = {
            requestsToday: data.requestsToday || 0,
            requestsYesterday: data.requestsYesterday || 0,
            requests7Days: data.requests7Days || 0,
            requests30Days: data.requests30Days || 0,
            errorRequestsToday: data.errorRequestsToday || 0,
            avgLatencyToday: data.avgLatencyToday || 0,
            activeOperatorsToday: data.activeOperatorsToday || 0,
            loginSuccessToday: data.loginSuccessToday || 0,
            loginFailToday: data.loginFailToday || 0,
            newUsersToday: data.newUsersToday || 0,
            newUsersThisMonth: data.newUsersThisMonth || 0,
            currentUsers: data.currentUsers || 0
          }

          // 趋势与明细列表
          this.requestTrend = Array.isArray(data.requestTrend) ? data.requestTrend : []
          this.loginTrend = Array.isArray(data.loginTrend) ? data.loginTrend : []
          this.topModules = Array.isArray(data.topModules) ? data.topModules : []
          this.recentErrors = Array.isArray(data.recentErrors) ? data.recentErrors : []
          this.recentFailedLogins = Array.isArray(data.recentFailedLogins) ? data.recentFailedLogins : []

          console.log('管理员系统级统计数据:', {
            systemStats: this.systemStats,
            requestTrend: this.requestTrend,
            loginTrend: this.loginTrend,
            topModules: this.topModules
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
        console.log('宿管统计数据原始响应:', response)

        if (response && response.data) {
          // 映射后端返回的字段到前端期望的结构
          const data = response.data
          console.log('宿管统计数据解析:', data)

          // 处理维修申请统计数据
          const repairStats = []
          if (data.pendingRepairs !== undefined || data.processingRepairs !== undefined || data.completedRepairs !== undefined) {
            const totalRepairs = (data.pendingRepairs || 0) + (data.processingRepairs || 0) + (data.completedRepairs || 0)

            repairStats.push({
              status: '待处理',
              count: data.pendingRepairs || 0,
              percentage: this.calculatePercentage(data.pendingRepairs || 0, totalRepairs)
            })
            repairStats.push({
              status: '处理中',
              count: data.processingRepairs || 0,
              percentage: this.calculatePercentage(data.processingRepairs || 0, totalRepairs)
            })
            repairStats.push({
              status: '已完成',
              count: data.completedRepairs || 0,
              percentage: this.calculatePercentage(data.completedRepairs || 0, totalRepairs)
            })
          }

          // 处理楼层状态数据
          let floorStatus = []
          if (data.floorOccupancy && Array.isArray(data.floorOccupancy)) {
            floorStatus = data.floorOccupancy.map(floor => ({
              floorName: floor.floorName || `${floor.floorId}楼`,
              totalBeds: floor.totalBeds || 0,
              occupied: floor.occupiedBeds || 0,
              available: (floor.totalBeds || 0) - (floor.occupiedBeds || 0),
              occupancyRate: Math.round(floor.occupancyRate || 0)
            }))
          } else if (data.managedFloors) {
            // 如果没有详细楼层数据，生成默认楼层状态
            const floorCount = data.managedFloors || 0
            const totalBeds = data.managedBeds || 0
            const occupiedBeds = data.occupiedBeds || 0
            const bedsPerFloor = floorCount > 0 ? Math.floor(totalBeds / floorCount) : 0
            const occupiedPerFloor = floorCount > 0 ? Math.floor(occupiedBeds / floorCount) : 0

            for (let i = 1; i <= floorCount; i++) {
              const occupied = occupiedPerFloor
              const total = bedsPerFloor
              floorStatus.push({
                floorName: `${i}楼`,
                totalBeds: total,
                occupied: occupied,
                available: total - occupied,
                occupancyRate: total > 0 ? Math.round((occupied / total) * 100) : 0
              })
            }
          }

          // 从 monthlyStats 中提取数据
          const monthlyStats = data.monthlyStats || {}

          this.managerData = {
            managedFloors: data.managedFloors || 0,
            managedBeds: data.managedBeds || 0,
            managedRooms: data.managedRooms || 0,
            pendingRepairs: data.pendingRepairs || 0,
            pendingVisits: data.pendingVisits || 0,
            occupiedBeds: data.occupiedBeds || 0,
            occupancyRate: Math.round(data.occupancyRate || 0),
            // 从 monthlyStats 中提取每月统计数据
            monthlyInspections: 0, // 后端暂无此字段
            monthlyRepairs: monthlyStats.completedRepairs || 0,
            monthlyViolations: 0, // 后端暂无此字段
            monthlyVisitors: monthlyStats.processedVisits || 0,
            newStudents: monthlyStats.newStudents || 0,
            repairStats: repairStats,
            floorStatus: floorStatus
          }

          console.log('处理后的宿管数据:', this.managerData)
        }
      } catch (error) {
        console.error('加载宿管数据失败:', error)
        // 使用默认数据
        this.managerData = {
          managedFloors: 0,
          managedBeds: 0,
          managedRooms: 0,
          pendingRepairs: 0,
          pendingVisits: 0,
          occupiedBeds: 0,
          occupancyRate: 0,
          monthlyInspections: 0,
          monthlyRepairs: 0,
          monthlyViolations: 0,
          monthlyVisitors: 0,
          newStudents: 0,
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
    },
    extractNumber(item, candidates) {
      if (!item || !Array.isArray(candidates)) {
        return 0
      }
      for (const key of candidates) {
        if (key && Object.prototype.hasOwnProperty.call(item, key) && item[key] !== undefined && item[key] !== null) {
          const value = Number(item[key])
          if (!Number.isNaN(value)) {
            return value
          }
        }
      }
      return 0
    },
    extractString(item, candidates) {
      if (!item || !Array.isArray(candidates)) {
        return ''
      }
      for (const key of candidates) {
        if (key && Object.prototype.hasOwnProperty.call(item, key) && item[key]) {
          return String(item[key])
        }
      }
      return ''
    },
    formatNumber(value) {
      const num = Number(value)
      if (Number.isNaN(num) || !Number.isFinite(num)) {
        return '0'
      }
      if (Math.abs(num) >= 10000) {
        return `${(num / 10000).toFixed(1)}万`
      }
      return num.toLocaleString('zh-CN')
    },
    calculateGrowth(current, previous) {
      const curr = Number(current) || 0
      const prev = Number(previous) || 0
      if (prev === 0) {
        return curr === 0 ? 0 : 100
      }
      const diff = ((curr - prev) / prev) * 100
      return Number(diff.toFixed(1))
    },
    calculatePercentage(value, total) {
      if (!total || total === 0) return 0
      return Math.round((value / total) * 100)
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

.admin-dashboard .admin-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
  align-items: stretch;
}

.admin-dashboard .stat-card.summary {
  min-height: 140px;
  padding: 18px 22px;
}

.admin-dashboard .stat-card.summary .stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
}

.admin-dashboard .chart-card {
  background: white;
  padding: 18px 20px 10px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
}

.admin-dashboard .chart-card.wide {
  grid-column: 1 / -1;
}

.admin-dashboard .chart-card.medium {
  min-height: 380px;
}

.admin-dashboard .chart-card .card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}

.admin-dashboard .chart-card .card-desc {
  margin: 6px 0 0;
  color: #909399;
  font-size: 13px;
}

.admin-dashboard .card-stat {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  background: #f5f7fa;
  padding: 12px 16px;
  border-radius: 10px;
  min-width: 120px;
}

.admin-dashboard .card-stat.success {
  background: rgba(103, 194, 58, 0.12);
  color: #67c23a;
}

.admin-dashboard .card-stat.danger {
  background: rgba(245, 108, 108, 0.12);
  color: #f56c6c;
}

.admin-dashboard .card-stat .label {
  font-size: 13px;
  color: inherit;
  opacity: 0.8;
}

.admin-dashboard .card-stat .value {
  font-size: 20px;
  font-weight: 600;
}

.admin-dashboard .stat-card.insights {
  grid-column: span 1;
  flex-direction: column;
  padding: 24px 26px;
  gap: 20px;
}

.admin-dashboard .insight-item {
  display: flex;
  align-items: center;
  gap: 14px;
}

.admin-dashboard .insight-icon {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
}

.admin-dashboard .insight-icon.success {
  background: linear-gradient(135deg, #67c23a, #52b51f);
}

.admin-dashboard .insight-icon.warning {
  background: linear-gradient(135deg, #e6a23c, #f2c078);
}

.admin-dashboard .insight-icon.info {
  background: linear-gradient(135deg, #409eff, #66b1ff);
}

.admin-dashboard .insight-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.admin-dashboard .insight-title {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.admin-dashboard .insight-value {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.admin-dashboard .insight-sub {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

@media (max-width: 1200px) {
  .admin-dashboard .chart-card.medium,
  .admin-dashboard .stat-card.insights {
    grid-column: 1 / -1;
  }
}

/* ========== 管理员统计界面 - 全新设计 ========== */
.admin-dashboard {
  max-width: 1600px;
  margin: 0 auto;
}

/* 顶部统计卡片行 */
.admin-stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.admin-stat-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.admin-stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.admin-stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  pointer-events: none;
}

.admin-stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.admin-stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.admin-stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.admin-stat-trend.up {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.admin-stat-trend.down {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.admin-stat-trend.stable {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.admin-stat-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.admin-stat-title {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
  margin: 0;
}

.admin-stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin: 0;
  line-height: 1;
}

.admin-stat-desc {
  font-size: 13px;
  color: #c0c4cc;
  margin: 0;
}

/* 图表容器布局 */
.admin-charts-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.admin-dashboard .chart-full {
  grid-column: 1 / -1; /* 占据整行 */
}

.admin-dashboard .chart-half {
  grid-column: span 1; /* 占据一半宽度 */
}

.admin-dashboard .chart-wrapper {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.admin-dashboard .chart-wrapper:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.admin-dashboard .chart-header-modern {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f5f7fa;
}

.admin-dashboard .chart-title-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.admin-dashboard .chart-title-main {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.admin-dashboard .chart-subtitle {
  font-size: 13px;
  color: #909399;
}

.admin-dashboard .chart-metrics {
  display: flex;
  gap: 20px;
  align-items: center;
}

.admin-dashboard .chart-metrics.compact {
  gap: 12px;
}

.admin-dashboard .metric-item {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.admin-dashboard .metric-item.success {
  flex-direction: row;
  align-items: center;
  gap: 6px;
  color: #67c23a;
}

.admin-dashboard .metric-item.danger {
  flex-direction: row;
  align-items: center;
  gap: 6px;
  color: #f56c6c;
}

.admin-dashboard .metric-label {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.admin-dashboard .metric-value {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}

.admin-dashboard .metric-value.primary {
  color: #409eff;
}

/* 底部洞察面板 */
.admin-insights-panel {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.insight-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.insight-card::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff, #67c23a);
}

.insight-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.insight-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #ffffff;
  flex-shrink: 0;
}

.insight-icon-wrapper.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.insight-icon-wrapper.success {
  background: linear-gradient(135deg, #67c23a 0%, #52b51f 100%);
}

.insight-icon-wrapper.warning {
  background: linear-gradient(135deg, #e6a23c 0%, #f39c12 100%);
}

.insight-icon-wrapper.info {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.insight-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.insight-label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
  margin: 0;
}

.insight-main-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
  margin: 0;
  line-height: 1;
}

.insight-sub-info {
  font-size: 13px;
  color: #c0c4cc;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .admin-charts-container {
    grid-template-columns: 1fr;
  }
  
  .chart-half {
    grid-column: 1;
  }
}

@media (max-width: 768px) {
  .admin-stats-row {
    grid-template-columns: 1fr;
  }
  
  .admin-insights-panel {
    grid-template-columns: 1fr;
  }
  
  .chart-header-modern {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .chart-metrics {
    margin-top: 12px;
  }
}

/* ========== 旧版管理员样式(保留兼容) ========== */
.admin-dashboard .admin-grid {
  display: none; /* 隐藏旧版布局 */
}

.stat-icon {
  font-size: 2.5em;
  margin-right: 15px;
}

/* ========== 宿管统计界面 ========== */
.manager-dashboard {
  max-width: 1600px;
  margin: 0 auto;
}

.manager-header {
  margin-bottom: 24px;
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
}

.manager-header h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.manager-header .muted {
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 15px;
}

.manager-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.manager-dashboard .stat-card {
  align-items: flex-start;
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.manager-dashboard .stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.manager-dashboard .stat-subtext {
  margin: 4px 0 0;
  font-size: 13px;
  color: #909399;
}

.manager-dashboard .floor-status .no-data {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
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
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chart-card h3 {
  margin-top: 0;
  margin-bottom: 15px;
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
  margin-top: 20px;
}

.student-charts .chart-card {
  min-height: 350px;
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





