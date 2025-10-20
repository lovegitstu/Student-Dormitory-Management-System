import request from '@/utils/request'

// 查询维修工单列表
export function listRepair(query) {
  return request({
    url: '/dormitory/repair/list',
    method: 'get',
    params: query
  })
}

// 查询维修工单详细
export function getRepair(repairId) {
  return request({
    url: '/dormitory/repair/' + repairId,
    method: 'get'
  })
}

// 新增维修工单
export function addRepair(data) {
  return request({
    url: '/dormitory/repair',
    method: 'post',
    data: data
  })
}

// 修改维修工单
export function updateRepair(data) {
  return request({
    url: '/dormitory/repair',
    method: 'put',
    data: data
  })
}

// 处理维修工单
export function approveRepair(data) {
  return request({
    url: '/dormitory/repair/approve',
    method: 'put',
    data: data
  })
}

// 删除维修工单
export function delRepair(repairId) {
  return request({
    url: '/dormitory/repair/' + repairId,
    method: 'delete'
  })
}
