import request from '@/utils/request'

// 查询宿舍信息列表
export function listDorm(query) {
  return request({
    url: '/dormitory/dorm/list',
    method: 'get',
    params: query
  })
}

// 查询宿舍信息详细
export function getDorm(dorId) {
  return request({
    url: '/dormitory/dorm/' + dorId,
    method: 'get'
  })
}

// 新增宿舍信息
export function addDorm(data) {
  return request({
    url: '/dormitory/dorm',
    method: 'post',
    data: data
  })
}

// 修改宿舍信息
export function updateDorm(data) {
  return request({
    url: '/dormitory/dorm',
    method: 'put',
    data: data
  })
}

// 删除宿舍信息
export function delDorm(dorId) {
  return request({
    url: '/dormitory/dorm/' + dorId,
    method: 'delete'
  })
}
