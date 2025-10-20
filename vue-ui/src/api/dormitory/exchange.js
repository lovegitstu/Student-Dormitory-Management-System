import request from '@/utils/request'

// 查询换宿申请列表
export function listExchange(query) {
  return request({
    url: '/dormitory/exchange/list',
    method: 'get',
    params: query
  })
}

// 查询换宿申请详细
export function getExchange(id) {
  return request({
    url: '/dormitory/exchange/' + id,
    method: 'get'
  })
}

// 新增换宿申请
export function addExchange(data) {
  return request({
    url: '/dormitory/exchange',
    method: 'post',
    data: data
  })
}

// 修改换宿申请
export function updateExchange(data) {
  return request({
    url: '/dormitory/exchange',
    method: 'put',
    data: data
  })
}

// 删除换宿申请
export function delExchange(id) {
  return request({
    url: '/dormitory/exchange/' + id,
    method: 'delete'
  })
}

// 审批换宿申请
export function approveExchange(id, data) {
  return request({
    url: '/dormitory/exchange/approve/' + id,
    method: 'put',
    data: data
  })
}

// 批量审批换宿申请
export function batchApproveExchange(data) {
  return request({
    url: '/dormitory/exchange/approve/batch',
    method: 'put',
    data: data
  })
}
