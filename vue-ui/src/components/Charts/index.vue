<template>
  <div :id="chartId" :style="{ width: width, height: height }"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Charts',
  props: {
    chartId: {
      type: String,
      required: true
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    option: {
      type: Object,
      required: true
    },
    theme: {
      type: String,
      default: 'default'
    }
  },
  data() {
    return {
      chart: null
    }
  },
  mounted() {
    this.initChart()
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
    }
  },
  watch: {
    option: {
      handler(newOption) {
        if (this.chart) {
          this.chart.setOption(newOption, true)
        }
      },
      deep: true
    }
  },
  methods: {
    initChart() {
      const chartDom = document.getElementById(this.chartId)
      if (chartDom) {
        this.chart = echarts.init(chartDom, this.theme)
        this.chart.setOption(this.option)
        
        // 监听窗口大小变化
        window.addEventListener('resize', this.handleResize)
      }
    },
    handleResize() {
      if (this.chart) {
        this.chart.resize()
      }
    },
    getChart() {
      return this.chart
    }
  }
}
</script>

<style scoped>
/* 图表容器样式 */
</style>