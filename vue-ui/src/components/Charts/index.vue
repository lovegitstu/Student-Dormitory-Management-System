<template>
  <!-- 使用 ref 获取容器，避免依赖全局 id 导致的冲突 -->
  <div :id="chartId || null" ref="chartContainer" :style="{ width: width, height: height }"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Charts',
  props: {
    // chartId 可选，保留以兼容现有用法，但组件内部使用 ref 挂载
    chartId: {
      type: String,
      required: false
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
    this.destroyChart()
  },
  beforeUnmount() {
    // Vue 3 compatibility if used
    this.destroyChart()
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
      // 使用 ref 获取当前组件容器，确保在同一组件实例里挂载
      const chartDom = this.$refs.chartContainer
      if (!chartDom) return

      // 如果已经存在 chart 实例, 先销毁
      if (this.chart) {
        try { this.chart.dispose() } catch (e) { /* ignore */ }
        this.chart = null
      }

      this.chart = echarts.init(chartDom, this.theme)
      if (this.option) {
        this.chart.setOption(this.option)
      }

      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize)
    },
    destroyChart() {
      if (this.chart) {
        try {
          this.chart.dispose()
        } catch (e) {
          // ignore dispose errors
        }
        this.chart = null
      }
      window.removeEventListener('resize', this.handleResize)
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
/* 图表容器样式 (如果需要可以在这里添加默认样式) */
</style>