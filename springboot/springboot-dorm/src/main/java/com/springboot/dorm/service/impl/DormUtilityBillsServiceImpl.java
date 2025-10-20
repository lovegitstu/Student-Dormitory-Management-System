package com.springboot.dorm.service.impl;

import java.math.BigDecimal;
import java.util.List;
import com.springboot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormUtilityBillsMapper;
import com.springboot.dorm.domain.DormUtilityBills;
import com.springboot.dorm.service.IDormUtilityBillsService;

/**
 * 水电费管理Service业务层处理
 * 
 *
 * @date 2025-09-20
 */
@Service
public class DormUtilityBillsServiceImpl implements IDormUtilityBillsService 
{
    @Autowired
    private DormUtilityBillsMapper dormUtilityBillsMapper;

    /**
     * 查询水电费管理
     * 
     * @param ubId 水电费管理主键
     * @return 水电费管理
     */
    @Override
    public DormUtilityBills selectDormUtilityBillsByUbId(Integer ubId)
    {
        return dormUtilityBillsMapper.selectDormUtilityBillsByUbId(ubId);
    }

    /**
     * 查询水电费管理列表
     * 
     * @param dormUtilityBills 水电费管理
     * @return 水电费管理
     */
    @Override
    public List<DormUtilityBills> selectDormUtilityBillsList(DormUtilityBills dormUtilityBills)
    {
        return dormUtilityBillsMapper.selectDormUtilityBillsList(dormUtilityBills);
    }

    /**
     * 新增水电费管理
     * 
     * @param dormUtilityBills 水电费管理
     * @return 结果
     */
    @Override
    public int insertDormUtilityBills(DormUtilityBills dormUtilityBills)
    {
        dormUtilityBills.setCreateTime(DateUtils.getNowDate());
        
        // 自动缴费逻辑：检查余额是否充足
        checkAndAutoPayment(dormUtilityBills);
        
        return dormUtilityBillsMapper.insertDormUtilityBills(dormUtilityBills);
    }

    /**
     * 修改水电费管理
     * 
     * @param dormUtilityBills 水电费管理
     * @return 结果
     */
    @Override
    public int updateDormUtilityBills(DormUtilityBills dormUtilityBills)
    {
        dormUtilityBills.setUpdateTime(DateUtils.getNowDate());
        
        // 自动缴费逻辑：检查余额是否充足
        checkAndAutoPayment(dormUtilityBills);
        
        return dormUtilityBillsMapper.updateDormUtilityBills(dormUtilityBills);
    }
    
    /**
     * 检查并执行自动缴费逻辑
     * 
     * @param dormUtilityBills 水电费管理对象
     */
    private void checkAndAutoPayment(DormUtilityBills dormUtilityBills) {
        System.out.println("=== 进入自动缴费逻辑检查 ===");
        System.out.println("当前余额: " + dormUtilityBills.getUbBalance());
        System.out.println("总费用: " + dormUtilityBills.getUbTotalCost());
        
        // 如果余额和总费用都不为空
        if (dormUtilityBills.getUbBalance() != null && dormUtilityBills.getUbTotalCost() != null) {
            System.out.println("余额和总费用都不为空，开始比较");
            
            // 比较余额和总费用
            int compareResult = dormUtilityBills.getUbBalance().compareTo(dormUtilityBills.getUbTotalCost());
            System.out.println("余额与总费用比较结果: " + compareResult + " (>=0表示余额充足)");
            
            if (compareResult >= 0) {
                System.out.println("=== 余额充足，执行自动扣费 ===");
                // 余额充足，自动标记为已缴费
                dormUtilityBills.setUbPaymentStatus(1);
                
                // 扣除费用，更新余额
                BigDecimal newBalance = dormUtilityBills.getUbBalance().subtract(dormUtilityBills.getUbTotalCost());
                System.out.println("扣费前余额: " + dormUtilityBills.getUbBalance());
                System.out.println("扣除费用: " + dormUtilityBills.getUbTotalCost());
                System.out.println("扣费后余额: " + newBalance);
                
                dormUtilityBills.setUbBalance(newBalance);
                System.out.println("已设置缴费状态为: 1 (已缴费)");
            } else {
                System.out.println("=== 余额不足，标记为未缴费 ===");
                // 余额不足，标记为未缴费
                dormUtilityBills.setUbPaymentStatus(0);
                System.out.println("已设置缴费状态为: 0 (未缴费)");
            }
        } else {
            System.out.println("余额或总费用为空，跳过自动缴费逻辑");
            System.out.println("余额是否为空: " + (dormUtilityBills.getUbBalance() == null));
            System.out.println("总费用是否为空: " + (dormUtilityBills.getUbTotalCost() == null));
            // 如果余额或总费用为空，默认标记为未缴费
            dormUtilityBills.setUbPaymentStatus(0);
        }
        System.out.println("=== 自动缴费逻辑检查结束 ===");
    }

    /**
     * 批量删除水电费管理
     * 
     * @param ubIds 需要删除的水电费管理主键
     * @return 结果
     */
    @Override
    public int deleteDormUtilityBillsByUbIds(Integer[] ubIds)
    {
        return dormUtilityBillsMapper.deleteDormUtilityBillsByUbIds(ubIds);
    }

    /**
     * 删除水电费管理信息
     * 
     * @param ubId 水电费管理主键
     * @return 结果
     */
    @Override
    public int deleteDormUtilityBillsByUbId(Integer ubId)
    {
        return dormUtilityBillsMapper.deleteDormUtilityBillsByUbId(ubId);
    }
}
