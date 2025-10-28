package com.springboot.dorm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.springboot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormUtilityBillsMapper;
import com.springboot.dorm.domain.DormUtilityBills;
import com.springboot.dorm.service.IDormUtilityBillsService;
import com.springboot.dorm.algorithm.UtilityBillCalculator;

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
    
    @Autowired
    private UtilityBillCalculator utilityBillCalculator;

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
        
        // 使用阶梯计费计算水电费
        calculateTieredBills(dormUtilityBills);
        
        // 新增记录时，需要从该宿舍的最新记录中继承余额
        inheritBalanceFromLatestRecord(dormUtilityBills);
        
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
        
        // 使用阶梯计费重新计算水电费
        calculateTieredBills(dormUtilityBills);
        
        // 自动缴费逻辑：检查余额是否充足，允许通过业务层控制跳过
        if (!Boolean.TRUE.equals(dormUtilityBills.getSkipAutoPayment())) {
            checkAndAutoPayment(dormUtilityBills);
        } else {
            System.out.println("跳过自动缴费逻辑，保持当前余额与缴费状态不变");
        }
        
        return dormUtilityBillsMapper.updateDormUtilityBills(dormUtilityBills);
    }
    
    /**
     * 使用阶梯计费计算水电费
     * 
     * @param dormUtilityBills 水电费管理对象
     */
    private void calculateTieredBills(DormUtilityBills dormUtilityBills) {
        System.out.println("=== 开始阶梯计费计算 ===");
        
        // 获取用水量和用电量
        BigDecimal waterUsage = dormUtilityBills.getUbWaterConsumption();
        BigDecimal electricityUsage = dormUtilityBills.getUbElectricityConsumption();
        
        System.out.println("用水量: " + waterUsage + " 吨");
        System.out.println("用电量: " + electricityUsage + " 度");
        
        if (waterUsage != null && waterUsage.compareTo(BigDecimal.ZERO) > 0) {
            // 计算阶梯水费
            BigDecimal waterCost = utilityBillCalculator.calculateTieredWaterCost(waterUsage);
            dormUtilityBills.setUbWater(waterCost);
            System.out.println("阶梯水费: " + waterCost + " 元");
        }
        
        if (electricityUsage != null && electricityUsage.compareTo(BigDecimal.ZERO) > 0) {
            // 计算阶梯电费
            BigDecimal electricityCost = utilityBillCalculator.calculateTieredElectricityCost(electricityUsage);
            dormUtilityBills.setUbElectricityRate(electricityCost);
            System.out.println("阶梯电费: " + electricityCost + " 元");
        }
        
        // 计算总费用
        BigDecimal waterCost = dormUtilityBills.getUbWater() != null ? dormUtilityBills.getUbWater() : BigDecimal.ZERO;
        BigDecimal electricityCost = dormUtilityBills.getUbElectricityRate() != null ? dormUtilityBills.getUbElectricityRate() : BigDecimal.ZERO;
        BigDecimal totalCost = waterCost.add(electricityCost);
        
        dormUtilityBills.setUbTotalCost(totalCost);
        System.out.println("总费用: " + totalCost + " 元");
        System.out.println("=== 阶梯计费计算完成 ===");
    }
    
    /**
     * 从该宿舍的最新记录中继承余额
     * 
     * @param dormUtilityBills 新的水电费记录
     */
    private void inheritBalanceFromLatestRecord(DormUtilityBills dormUtilityBills) {
        System.out.println("=== 开始继承余额逻辑 ===");
        System.out.println("新记录宿舍ID: " + dormUtilityBills.getDorId());
        System.out.println("新记录当前余额: " + dormUtilityBills.getUbBalance());
        
        // 如果新记录已经有余额，则不需要继承
        if (dormUtilityBills.getUbBalance() != null && dormUtilityBills.getUbBalance().compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("新记录已有余额，无需继承");
            return;
        }
        
        // 查询该宿舍的最新记录
        DormUtilityBills queryBills = new DormUtilityBills();
        queryBills.setDorId(dormUtilityBills.getDorId());
        List<DormUtilityBills> existingBills = dormUtilityBillsMapper.selectDormUtilityBillsList(queryBills);
        
        if (!existingBills.isEmpty()) {
            // 获取最新记录（假设按创建时间排序，第一条是最新的）
            DormUtilityBills latestBill = existingBills.get(0);
            BigDecimal inheritedBalance = latestBill.getUbBalance() != null ? latestBill.getUbBalance() : BigDecimal.ZERO;
            
            System.out.println("找到最新记录，余额: " + inheritedBalance);
            dormUtilityBills.setUbBalance(inheritedBalance);
            System.out.println("已继承余额: " + inheritedBalance);
        } else {
            System.out.println("未找到该宿舍的历史记录，设置初始余额为0");
            dormUtilityBills.setUbBalance(BigDecimal.ZERO);
        }
        
        System.out.println("=== 余额继承逻辑结束 ===");
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
    
    /**
     * 计算阶梯水电费
     * 
     * @param dormId 宿舍ID
     * @param electricityUsage 用电量
     * @param waterUsage 用水量
     * @param billingStartDate 计费开始日期
     * @param billingEndDate 计费结束日期
     * @param splitMethod 分摊方式
     * @return 计算结果
     */
    @Override
    public UtilityBillCalculator.BillCalculationResult calculateTieredBill(
        Long dormId, BigDecimal electricityUsage, BigDecimal waterUsage, 
        LocalDate billingStartDate, LocalDate billingEndDate, 
        UtilityBillCalculator.SplitMethod splitMethod) {
        
        return utilityBillCalculator.calculateCompleteBill(
            dormId, electricityUsage, waterUsage, 
            billingStartDate, billingEndDate, splitMethod);
    }
}
