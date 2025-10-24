package com.springboot.dorm.service;

import java.util.List;
import com.springboot.dorm.domain.DormBed;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.dorm.algorithm.DormitoryAllocationAlgorithm.AllocationResult;

/**
 * 床位管理Service接口
 * 
 *
 * @date 2025-09-15
 */
public interface IDormBedService 
{
    /**
     * 查询床位管理
     * 
     * @param bedId 床位管理主键
     * @return 床位管理
     */
    public DormBed selectDormBedByBedId(Long bedId);

    /**
     * 查询床位管理列表
     * 
     * @param dormBed 床位管理
     * @return 床位管理集合
     */
    public List<DormBed> selectDormBedList(DormBed dormBed);

    /**
     * 新增床位管理
     * 
     * @param dormBed 床位管理
     * @return 结果
     */
    public int insertDormBed(DormBed dormBed);

    /**
     * 修改床位管理
     * 
     * @param dormBed 床位管理
     * @return 结果
     */
    public int updateDormBed(DormBed dormBed);

    /**
     * 批量删除床位管理
     * 
     * @param bedIds 需要删除的床位管理主键集合
     * @return 结果
     */
    public int deleteDormBedByBedIds(Long[] bedIds);

    /**
     * 删除床位管理信息
     * 
     * @param bedId 床位管理主键
     * @return 结果
     */
    public int deleteDormBedByBedId(Long bedId);
    
    // ========== 智能分配功能 ==========
    
    /**
     * 智能分配床位给学生
     * 
     * @param student 学生信息
     * @return 分配结果
     */
    public AllocationResult smartAllocateBed(DormStudent student);
    
    /**
     * 批量智能分配床位
     * 
     * @param students 学生列表
     * @return 分配结果列表
     */
    public List<AllocationResult> batchSmartAllocateBeds(List<DormStudent> students);
    
    /**
     * 获取可用床位列表（按优先级排序）
     * 
     * @param student 学生信息
     * @return 可用床位列表
     */
    public List<DormBed> getAvailableBedsForStudent(DormStudent student);
    
    /**
     * 检查床位分配冲突
     * 
     * @param bedId 床位ID
     * @param studentId 学生ID
     * @return 是否存在冲突
     */
    public boolean checkBedAllocationConflict(Long bedId, Long studentId);
    
    /**
     * 获取床位利用率统计
     * 
     * @param dormId 宿舍ID（可选，为null时统计全部）
     * @return 床位利用率信息
     */
    public BedUtilizationStats getBedUtilizationStats(Long dormId);
    
    /**
     * 床位利用率统计信息类
     */
    public static class BedUtilizationStats {
        private int totalBeds;
        private int occupiedBeds;
        private int availableBeds;
        private double utilizationRate;
        
        public BedUtilizationStats(int totalBeds, int occupiedBeds) {
            this.totalBeds = totalBeds;
            this.occupiedBeds = occupiedBeds;
            this.availableBeds = totalBeds - occupiedBeds;
            this.utilizationRate = totalBeds > 0 ? (double) occupiedBeds / totalBeds * 100 : 0;
        }
        
        // Getters and Setters
        public int getTotalBeds() { return totalBeds; }
        public void setTotalBeds(int totalBeds) { this.totalBeds = totalBeds; }
        
        public int getOccupiedBeds() { return occupiedBeds; }
        public void setOccupiedBeds(int occupiedBeds) { this.occupiedBeds = occupiedBeds; }
        
        public int getAvailableBeds() { return availableBeds; }
        public void setAvailableBeds(int availableBeds) { this.availableBeds = availableBeds; }
        
        public double getUtilizationRate() { return utilizationRate; }
        public void setUtilizationRate(double utilizationRate) { this.utilizationRate = utilizationRate; }
        
        @Override
        public String toString() {
            return "BedUtilizationStats{" +
                    "totalBeds=" + totalBeds +
                    ", occupiedBeds=" + occupiedBeds +
                    ", availableBeds=" + availableBeds +
                    ", utilizationRate=" + String.format("%.2f", utilizationRate) + "%" +
                    '}';
        }
    }
}
