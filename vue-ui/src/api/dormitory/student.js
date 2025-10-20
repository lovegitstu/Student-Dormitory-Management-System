import request from '@/utils/request'

// 查询学生信息列表
export function listStudent(query) {
  return request({
    url: '/dormitory/student/list',
    method: 'get',
    params: query
  })
}

// 查询学生信息详细
export function getStudent(stuId) {
  return request({
    url: '/dormitory/student/' + stuId,
    method: 'get'
  })
}

// 通过用户ID查询学生信息
export function getStudentByUserId(userId) {
  return request({
    url: '/dormitory/student/user/' + userId,
    method: 'get'
  })
}

// 新增学生信息
export function addStudent(data) {
  return request({
    url: '/dormitory/student',
    method: 'post',
    data: data
  })
}

// 修改学生信息
export function updateStudent(data) {
  return request({
    url: '/dormitory/student',
    method: 'put',
    data: data
  })
}

// 删除学生信息
export function delStudent(stuId) {
  return request({
    url: '/dormitory/student/' + stuId,
    method: 'delete'
  })
}
