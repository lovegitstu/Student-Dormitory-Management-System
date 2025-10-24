package com.springboot.dorm.algorithm;

import com.springboot.dorm.domain.DormBed;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.dorm.domain.DormUtilityBills;
import com.springboot.dorm.mapper.DormBedMapper;
import com.springboot.dorm.mapper.DormStudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 水电费阶梯计算算法
 * 支持阶梯计费和智能分摊
 * 
 * @author system
 * @date 2025-01-21
 */
@Component
public class UtilityBillCalculator {

    @Autowired
    private DormBedMapper dormBedMapper;
    
    @Autowired
    private DormStudentMapper dormStudentMapper;

    // 电费阶梯配置
    private static final ElectricityTier[] ELECTRICITY_TIERS = {
        new ElectricityTier(0, 100, new BigDecimal("0.50")),      // 0-100度：0.50元/度
        new ElectricityTier(100, 200, new BigDecimal("0.60")),    // 100-200度：0.60元/度
        new ElectricityTier(200, 300, new BigDecimal("0.80")),    // 200-300度：0.80元/度
        new ElectricityTier(300, Integer.MAX_VALUE, new BigDecimal("1.00")) // 300度以上：1.00元/度
    };

    // 水费阶梯配置
    private static final WaterTier[] WATER_TIERS = {
        new WaterTier(0, 20, new BigDecimal("2.50")),             // 0-20吨：2.50元/吨
        new WaterTier(20, 40, new BigDecimal("3.00")),            // 20-40吨：3.00元/吨
        new WaterTier(40, 60, new BigDecimal("4.00")),            // 40-60吨：4.00元/吨
        new WaterTier(60, Integer.MAX_VALUE, new BigDecimal("5.00")) // 60吨以上：5.00元/吨
    };

    /**
     * 计算阶梯电费
     */
    public BigDecimal calculateTieredElectricityCost(BigDecimal usage) {
        if (usage == null || usage.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal remainingUsage = usage;

        for (ElectricityTier tier : ELECTRICITY_TIERS) {
            if (remainingUsage.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal tierRange = new BigDecimal(tier.getUpperLimit() - tier.getLowerLimit());
            BigDecimal usageInTier = remainingUsage.min(tierRange);
            
            BigDecimal tierCost = usageInTier.multiply(tier.getRate());
            totalCost = totalCost.add(tierCost);
            
            remainingUsage = remainingUsage.subtract(usageInTier);
        }

        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算阶梯水费
     */
    public BigDecimal calculateTieredWaterCost(BigDecimal usage) {
        if (usage == null || usage.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal remainingUsage = usage;

        for (WaterTier tier : WATER_TIERS) {
            if (remainingUsage.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal tierRange = new BigDecimal(tier.getUpperLimit() - tier.getLowerLimit());
            BigDecimal usageInTier = remainingUsage.min(tierRange);
            
            BigDecimal tierCost = usageInTier.multiply(tier.getRate());
            totalCost = totalCost.add(tierCost);
            
            remainingUsage = remainingUsage.subtract(usageInTier);
        }

        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 按入住人数分摊费用
     */
    public List<BillSplit> splitBillByOccupants(Long dormId, BigDecimal totalAmount, 
                                               LocalDate billingStartDate, LocalDate billingEndDate) {
        List<BillSplit> splits = new ArrayList<>();
        
        try {
            // 获取宿舍所有床位
            DormBed queryBed = new DormBed();
            queryBed.setDorId(dormId);
            List<DormBed> beds = dormBedMapper.selectDormBedList(queryBed);
            
            // 获取入住学生信息
            List<DormStudent> occupants = new ArrayList<>();
            for (DormBed bed : beds) {
                if (bed.getStuId() != null && bed.getBedStatus().equals("1")) {
                    DormStudent student = dormStudentMapper.selectDormStudentByStuId(bed.getStuId());
                    if (student != null && "1".equals(student.getStuStatus())) {
                        occupants.add(student);
                    }
                }
            }

            if (occupants.isEmpty()) {
                return splits;
            }

            // 平均分摊
            BigDecimal perPersonAmount = totalAmount.divide(
                new BigDecimal(occupants.size()), 2, RoundingMode.HALF_UP);

            for (DormStudent student : occupants) {
                BillSplit split = new BillSplit();
                split.setStudentId(student.getStuId());
                split.setStudentName(student.getStuName());
                split.setAmount(perPersonAmount);
                split.setSplitRatio(new BigDecimal("1.00").divide(
                    new BigDecimal(occupants.size()), 4, RoundingMode.HALF_UP));
                splits.add(split);
            }

        } catch (Exception e) {
            System.err.println("按入住人数分摊费用时发生错误：" + e.getMessage());
        }

        return splits;
    }

    /**
     * 按入住天数分摊费用（更精确的分摊方式）
     */
    public List<BillSplit> splitBillByResidenceDays(Long dormId, BigDecimal totalAmount, 
                                                   LocalDate billingStartDate, LocalDate billingEndDate) {
        List<BillSplit> splits = new ArrayList<>();
        
        try {
            // 获取宿舍所有床位
            DormBed queryBed = new DormBed();
            queryBed.setDorId(dormId);
            List<DormBed> beds = dormBedMapper.selectDormBedList(queryBed);
            
            // 计算每个学生的入住天数
            Map<Long, Integer> studentResidenceDays = new HashMap<>();
            int totalResidenceDays = 0;
            
            for (DormBed bed : beds) {
                if (bed.getStuId() != null && bed.getBedStatus().equals("1")) {
                    DormStudent student = dormStudentMapper.selectDormStudentByStuId(bed.getStuId());
                    if (student != null && "1".equals(student.getStuStatus())) {
                        // 计算该学生在计费周期内的入住天数
                        LocalDate moveInDate = student.getStuEnrollmentDate() != null ? 
                            student.getStuEnrollmentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : billingStartDate;
                        LocalDate effectiveStartDate = moveInDate.isAfter(billingStartDate) ? 
                            moveInDate : billingStartDate;
                        
                        int residenceDays = (int) ChronoUnit.DAYS.between(effectiveStartDate, billingEndDate) + 1;
                        residenceDays = Math.max(0, residenceDays);
                        
                        studentResidenceDays.put(student.getStuId(), residenceDays);
                        totalResidenceDays += residenceDays;
                    }
                }
            }

            if (totalResidenceDays == 0) {
                return splits;
            }

            // 按入住天数比例分摊
            for (Map.Entry<Long, Integer> entry : studentResidenceDays.entrySet()) {
                Long studentId = entry.getKey();
                Integer residenceDays = entry.getValue();
                
                DormStudent student = dormStudentMapper.selectDormStudentByStuId(studentId);
                if (student != null) {
                    BigDecimal splitRatio = new BigDecimal(residenceDays)
                        .divide(new BigDecimal(totalResidenceDays), 4, RoundingMode.HALF_UP);
                    BigDecimal amount = totalAmount.multiply(splitRatio)
                        .setScale(2, RoundingMode.HALF_UP);

                    BillSplit split = new BillSplit();
                    split.setStudentId(studentId);
                    split.setStudentName(student.getStuName());
                    split.setAmount(amount);
                    split.setSplitRatio(splitRatio);
                    split.setResidenceDays(residenceDays);
                    splits.add(split);
                }
            }

        } catch (Exception e) {
            System.err.println("按入住天数分摊费用时发生错误：" + e.getMessage());
        }

        return splits;
    }

    /**
     * 计算完整的水电费账单
     */
    public BillCalculationResult calculateCompleteBill(Long dormId, BigDecimal electricityUsage, 
                                                     BigDecimal waterUsage, LocalDate billingStartDate, 
                                                     LocalDate billingEndDate, SplitMethod splitMethod) {
        try {
            // 计算阶梯费用
            BigDecimal electricityCost = calculateTieredElectricityCost(electricityUsage);
            BigDecimal waterCost = calculateTieredWaterCost(waterUsage);
            BigDecimal totalCost = electricityCost.add(waterCost);

            // 分摊费用
            List<BillSplit> splits;
            if (splitMethod == SplitMethod.BY_RESIDENCE_DAYS) {
                splits = splitBillByResidenceDays(dormId, totalCost, billingStartDate, billingEndDate);
            } else {
                splits = splitBillByOccupants(dormId, totalCost, billingStartDate, billingEndDate);
            }

            return new BillCalculationResult(
                electricityUsage, waterUsage, electricityCost, waterCost, totalCost, splits);

        } catch (Exception e) {
            System.err.println("计算完整水电费账单时发生错误：" + e.getMessage());
            return null;
        }
    }

    /**
     * 电费阶梯配置类
     */
    private static class ElectricityTier {
        private int lowerLimit;
        private int upperLimit;
        private BigDecimal rate;

        public ElectricityTier(int lowerLimit, int upperLimit, BigDecimal rate) {
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
            this.rate = rate;
        }

        public int getLowerLimit() { return lowerLimit; }
        public int getUpperLimit() { return upperLimit; }
        public BigDecimal getRate() { return rate; }
    }

    /**
     * 水费阶梯配置类
     */
    private static class WaterTier {
        private int lowerLimit;
        private int upperLimit;
        private BigDecimal rate;

        public WaterTier(int lowerLimit, int upperLimit, BigDecimal rate) {
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
            this.rate = rate;
        }

        public int getLowerLimit() { return lowerLimit; }
        public int getUpperLimit() { return upperLimit; }
        public BigDecimal getRate() { return rate; }
    }

    /**
     * 费用分摊结果类
     */
    public static class BillSplit {
        private Long studentId;
        private String studentName;
        private BigDecimal amount;
        private BigDecimal splitRatio;
        private Integer residenceDays;

        // Getters and Setters
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public BigDecimal getSplitRatio() { return splitRatio; }
        public void setSplitRatio(BigDecimal splitRatio) { this.splitRatio = splitRatio; }
        
        public Integer getResidenceDays() { return residenceDays; }
        public void setResidenceDays(Integer residenceDays) { this.residenceDays = residenceDays; }

        @Override
        public String toString() {
            return "BillSplit{" +
                    "studentId=" + studentId +
                    ", studentName='" + studentName + '\'' +
                    ", amount=" + amount +
                    ", splitRatio=" + splitRatio +
                    ", residenceDays=" + residenceDays +
                    '}';
        }
    }

    /**
     * 账单计算结果类
     */
    public static class BillCalculationResult {
        private BigDecimal electricityUsage;
        private BigDecimal waterUsage;
        private BigDecimal electricityCost;
        private BigDecimal waterCost;
        private BigDecimal totalCost;
        private List<BillSplit> splits;

        public BillCalculationResult(BigDecimal electricityUsage, BigDecimal waterUsage, 
                                   BigDecimal electricityCost, BigDecimal waterCost, 
                                   BigDecimal totalCost, List<BillSplit> splits) {
            this.electricityUsage = electricityUsage;
            this.waterUsage = waterUsage;
            this.electricityCost = electricityCost;
            this.waterCost = waterCost;
            this.totalCost = totalCost;
            this.splits = splits;
        }

        // Getters
        public BigDecimal getElectricityUsage() { return electricityUsage; }
        public BigDecimal getWaterUsage() { return waterUsage; }
        public BigDecimal getElectricityCost() { return electricityCost; }
        public BigDecimal getWaterCost() { return waterCost; }
        public BigDecimal getTotalCost() { return totalCost; }
        public List<BillSplit> getSplits() { return splits; }

        @Override
        public String toString() {
            return "BillCalculationResult{" +
                    "electricityUsage=" + electricityUsage +
                    ", waterUsage=" + waterUsage +
                    ", electricityCost=" + electricityCost +
                    ", waterCost=" + waterCost +
                    ", totalCost=" + totalCost +
                    ", splits=" + splits +
                    '}';
        }
    }

    /**
     * 分摊方式枚举
     */
    public enum SplitMethod {
        BY_OCCUPANTS,      // 按入住人数分摊
        BY_RESIDENCE_DAYS  // 按入住天数分摊
    }
}