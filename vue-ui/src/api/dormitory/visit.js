import request from '@/utils/request'

// 查询来访人员登记列表
export function listVisit(query) {
  return request({
    url: '/dormitory/visit/list',
    method: 'get',
    params: query
  })
}

// 查询来访人员登记详细
export function getVisit(visId) {
  return request({
    url: '/dormitory/visit/' + visId,
    method: 'get'
  })
}

// 新增来访人员登记
export function addVisit(data) {
  return request({
    url: '/dormitory/visit',
    method: 'post',
    data: data
  })
}

// 修改来访人员登记
export function updateVisit(data) {
  return request({
    url: '/dormitory/visit',
    method: 'put',
    data: data
  })
}

// 删除来访人员登记
export function delVisit(visId) {
  return request({
    url: '/dormitory/visit/' + visId,
    method: 'delete'
  })
}
