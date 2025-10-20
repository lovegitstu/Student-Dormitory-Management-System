import request from '@/utils/request'

// 查询宿舍评分列表
export function listContest(query) {
  return request({
    url: '/dormitory/contest/list',
    method: 'get',
    params: query
  })
}

// 查询宿舍评分详细
export function getContest(conId) {
  return request({
    url: '/dormitory/contest/' + conId,
    method: 'get'
  })
}

// 新增宿舍评分
export function addContest(data) {
  return request({
    url: '/dormitory/contest',
    method: 'post',
    data: data
  })
}

// 修改宿舍评分
export function updateContest(data) {
  return request({
    url: '/dormitory/contest',
    method: 'put',
    data: data
  })
}

// 删除宿舍评分
export function delContest(conId) {
  return request({
    url: '/dormitory/contest/' + conId,
    method: 'delete'
  })
}
