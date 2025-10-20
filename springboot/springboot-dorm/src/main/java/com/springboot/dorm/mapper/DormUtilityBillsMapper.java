package com.springboot.dorm.mapper;

import java.util.List;
import java.util.Map;
import com.springboot.dorm.domain.DormUtilityBills;

/**
 * 水电费管理Mapper接口
 * 
 * 
 * @date 2025-09-20
 */
public interface DormUtilityBillsMapper 
{
    /**
     * 查询水电费管理
     * 
     * @param ubId 水电费管理主键
     * @return 水电费管理
     */
    public DormUtilityBills selectDormUtilityBillsByUbId(Integer ubId);

    /**
     * 查询水电费管理列表
     * 
     * @param dormUtilityBills 水电费管理
     * @return 水电费管理集合
     */
    public List<DormUtilityBills> selectDormUtilityBillsList(DormUtilityBills dormUtilityBills);

    /**
     * 新增水电费管理
     * 
     * @param dormUtilityBills 水电费管理
     * @return 结果
     */
    public int insertDormUtilityBills(DormUtilityBills dormUtilityBills);

    /**
     * 修改水电费管理
     * 
     * @param dormUtilityBills 水电费管理
     * @return 结果
     */
    public int updateDormUtilityBills(DormUtilityBills dormUtilityBills);

    /**
     * 删除水电费管理
     * 
     * @param ubId 水电费管理主键
     * @return 结果
     */
    public int deleteDormUtilityBillsByUbId(Integer ubId);

    /**
     * 批量删除水电费管理
     * 
     * @param ubIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormUtilityBillsByUbIds(Integer[] ubIds);

    /**
     * 查询总金额
     * 
     * @return 总金额
     */
    public Double selectTotalAmount();

    /**
     * 查询已缴费金额
     * 
     * @return 已缴费金额
     */
    public Double selectPaidAmount();

    /**
     * 查询未缴费金额
     * 
     * @return 未缴费金额
     */
    public Double selectUnpaidAmount();

    /**
     * 查询月度账单统计
     * 
     * @return 月度账单统计
     */
    public List<Map<String, Object>> selectMonthlyBills();

    /**
     * 查询各楼层账单统计
     * 
     * @return 楼层账单统计
     */
    public List<Map<String, Object>> selectBillsByFloor();

    /**
     * 根据学生ID查询总金额
     * 
     * @param studentId 学生ID
     * @return 总金额
     */
    public Double selectTotalAmountByStudentId(Long studentId);

    /**
     * 根据学生ID查询已缴费金额
     * 
     * @param studentId 学生ID
     * @return 已缴费金额
     */
    public Double selectPaidAmountByStudentId(Long studentId);

    /**
     * 根据学生ID统计未缴费账单数
     * 
     * @param studentId 学生ID
     * @return 未缴费账单数
     */
    public Integer countUnpaidByStudentId(Long studentId);
}
