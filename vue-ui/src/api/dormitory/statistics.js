import request from '@/utils/request'

// 获取管理员统计数据
export function getAdminStatistics() {
  return request({
    url: '/dormitory/statistics/admin',
    method: 'get'
  })
}

// 获取宿管统计数据
export function getManagerStatistics() {
  return request({
    url: '/dormitory/statistics/manager',
    method: 'get'
  })
}

// 获取学生统计数据
export function getStudentStatistics() {
  return request({
    url: '/dormitory/statistics/student',
    method: 'get'
  })
}

// 获取宿舍入住率统计
export function getOccupancyStatistics() {
  return request({
    url: '/dormitory/statistics/occupancy',
    method: 'get'
  })
}

// 获取申请处理统计
export function getApplicationStatistics() {
  return request({
    url: '/dormitory/statistics/application',
    method: 'get'
  })
}

// 获取报修处理统计
export function getRepairStatistics() {
  return request({
    url: '/dormitory/statistics/repair',
    method: 'get'
  })
}

// 获取水电费统计
export function getBillsStatistics() {
  return request({
    url: '/dormitory/statistics/bills',
    method: 'get'
  })
}