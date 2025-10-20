import request from '@/utils/request'

// 查询回校申请列表
export function listCome(query) {
  return request({
    url: '/dormitory/come/list',
    method: 'get',
    params: query
  })
}

// 查询回校申请详细
export function getCome(id) {
  return request({
    url: '/dormitory/come/' + id,
    method: 'get'
  })
}

// 新增回校申请
export function addCome(data) {
  return request({
    url: '/dormitory/come',
    method: 'post',
    data: data
  })
}

// 修改回校申请
export function updateCome(data) {
  return request({
    url: '/dormitory/come',
    method: 'put',
    data: data
  })
}

// 删除回校申请
export function delCome(id) {
  return request({
    url: '/dormitory/come/' + id,
    method: 'delete'
  })
}

// 审批回校申请
export function approveCome(data) {
  return request({
    url: '/dormitory/come/approve',
    method: 'put',
    data: data
  })
}
