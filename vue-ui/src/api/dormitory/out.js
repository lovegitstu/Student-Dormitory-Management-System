import request from '@/utils/request'

// 查询离校登记列表
export function listOut(query) {
  return request({
    url: '/dormitory/out/list',
    method: 'get',
    params: query
  })
}

// 查询离校登记详细
export function getOut(id) {
  return request({
    url: '/dormitory/out/' + id,
    method: 'get'
  })
}

// 新增离校登记
export function addOut(data) {
  return request({
    url: '/dormitory/out',
    method: 'post',
    data: data
  })
}

// 修改离校登记
export function updateOut(data) {
  return request({
    url: '/dormitory/out',
    method: 'put',
    data: data
  })
}

// 删除离校登记
export function delOut(id) {
  return request({
    url: '/dormitory/out/' + id,
    method: 'delete'
  })
}

// 审批离校申请
export function approveOut(data) {
  return request({
    url: '/dormitory/out/approve',
    method: 'put',
    data: data
  })
}

// 确认学生返校
export function confirmReturn(data) {
  return request({
    url: '/dormitory/out/confirmReturn',
    method: 'put',
    data: data
  })
}
