import request from '@/utils/request'

// 查询宿舍楼管理列表
export function listFloor(query) {
  return request({
    url: '/dormitory/floor/list',
    method: 'get',
    params: query
  })
}

// 查询宿舍楼管理详细
export function getFloor(fId) {
  return request({
    url: '/dormitory/floor/' + fId,
    method: 'get'
  })
}

// 新增宿舍楼管理
export function addFloor(data) {
  return request({
    url: '/dormitory/floor',
    method: 'post',
    data: data
  })
}

// 修改宿舍楼管理
export function updateFloor(data) {
  return request({
    url: '/dormitory/floor',
    method: 'put',
    data: data
  })
}

// 删除宿舍楼管理
export function delFloor(fId) {
  return request({
    url: '/dormitory/floor/' + fId,
    method: 'delete'
  })
}
