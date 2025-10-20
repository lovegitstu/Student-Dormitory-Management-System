import request from '@/utils/request'

// 查询留宿申请列表
export function listLeave(query) {
  return request({
    url: '/dormitory/leave/list',
    method: 'get',
    params: query
  })
}

// 查询留宿申请详细
export function getLeave(kbId) {
  return request({
    url: '/dormitory/leave/' + kbId,
    method: 'get'
  })
}

// 新增留宿申请
export function addLeave(data) {
  return request({
    url: '/dormitory/leave',
    method: 'post',
    data: data
  })
}

// 修改留宿申请
export function updateLeave(data) {
  return request({
    url: '/dormitory/leave',
    method: 'put',
    data: data
  })
}

// 删除留宿申请
export function delLeave(kbId) {
  return request({
    url: '/dormitory/leave/' + kbId,
    method: 'delete'
  })
}
