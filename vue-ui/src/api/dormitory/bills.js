import request from '@/utils/request'

// 查询水电费管理列表
export function listBills(query) {
  return request({
    url: '/dormitory/bills/list',
    method: 'get',
    params: query
  })
}

// 查询水电费管理详细
export function getBills(ubId) {
  return request({
    url: '/dormitory/bills/' + ubId,
    method: 'get'
  })
}

// 新增水电费管理
export function addBills(data) {
  return request({
    url: '/dormitory/bills',
    method: 'post',
    data: data
  })
}

// 修改水电费管理
export function updateBills(data) {
  return request({
    url: '/dormitory/bills',
    method: 'put',
    data: data
  })
}

// 删除水电费管理
export function delBills(ubId) {
  return request({
    url: '/dormitory/bills/' + ubId,
    method: 'delete'
  })
}

// 学生充值功能
export function rechargeBills(ubId, amount) {
  return request({
    url: '/dormitory/bills/recharge/' + ubId,
    method: 'post',
    data: {
      amount: amount
    }
  })
}

// 学生直接缴费功能 - 从宿舍余额扣除费用
export function payBill(ubId) {
  return request({
    url: '/dormitory/bills/payBill/' + ubId,
    method: 'post'
  })
}

// 获取我的寝室余额
export function getMyDormBalance() {
  return request({
    url: '/dormitory/bills/myDormBalance',
    method: 'get'
  })
}

// 自动充值我的寝室
export function rechargeMyDorm(amount) {
  return request({
    url: '/dormitory/bills/rechargeMyDorm',
    method: 'post',
    data: {
      amount: amount
    }
  })
}
