import request from '@/utils/request'

// 查询床位管理列表
export function listBed(query) {
  return request({
    url: '/dormitory/bed/list',
    method: 'get',
    params: query
  })
}

// 查询床位管理详细
export function getBed(bedId) {
  return request({
    url: '/dormitory/bed/' + bedId,
    method: 'get'
  })
}

// 新增床位管理
export function addBed(data) {
  return request({
    url: '/dormitory/bed',
    method: 'post',
    data: data
  })
}

// 修改床位管理
export function updateBed(data) {
  return request({
    url: '/dormitory/bed',
    method: 'put',
    data: data
  })
}

// 删除床位管理
export function delBed(bedId) {
  return request({
    url: '/dormitory/bed/' + bedId,
    method: 'delete'
  })
}

// 检查学生是否已分配床位
export function checkStudentBed(stuId) {
  return request({
    url: '/dormitory/bed/checkStudentBed/' + stuId,
    method: 'get'
  })
}

// 自动分配宿舍
export function autoAllocate(studentId) {
  return request({
    url: '/dormitory/bed/autoAllocate/' + studentId,
    method: 'post'
  })
}

// 批量自动分配宿舍
export function batchAutoAllocate(studentIds) {
  return request({
    url: '/dormitory/bed/batchAutoAllocate',
    method: 'post',
    data: studentIds
  })
}

// 自动分配所有未分配学生
export function autoAllocateAll() {
  return request({
    url: '/dormitory/bed/autoAllocateAll',
    method: 'post'
  })
}
