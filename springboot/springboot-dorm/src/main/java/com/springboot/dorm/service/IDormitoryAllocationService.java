package com.springboot.dorm.service;

import com.springboot.dorm.algorithm.DormitoryAllocationAlgorithm.AllocationResult;
import com.springboot.dorm.domain.DormStudent;

import java.util.List;

/**
 * 宿舍自动分配服务接口
 * 
 * @author system
 * @date 2025-01-21
 */
public interface IDormitoryAllocationService {
    
    /**
     * 自动分配宿舍
     * 
     * @param student 学生信息
     * @return 分配结果
     */
    AllocationResult autoAllocateDormitory(DormStudent student);
    
    /**
     * 批量自动分配宿舍
     * 
     * @param students 学生列表
     * @return 分配结果列表
     */
    List<AllocationResult> batchAutoAllocateDormitory(List<DormStudent> students);
    
    /**
     * 检查分配可行性
     * 
     * @param student 学生信息
     * @param dormId 宿舍ID
     * @return 是否可行
     */
    boolean checkAllocationFeasibility(DormStudent student, Long dormId);
    
    /**
     * 手动分配宿舍
     * 
     * @param studentId 学生ID
     * @param dormId 宿舍ID
     * @return 分配结果
     */
    AllocationResult manualAllocateDormitory(Long studentId, Long dormId);
    
    /**
     * 释放宿舍床位
     * 
     * @param studentId 学生ID
     * @return 操作结果
     */
    boolean releaseDormitory(Long studentId);
    
    /**
     * 获取推荐宿舍列表
     * 
     * @param student 学生信息
     * @param limit 推荐数量限制
     * @return 推荐宿舍列表
     */
    List<RecommendedDormitory> getRecommendedDormitories(DormStudent student, int limit);
    
    /**
     * 推荐宿舍信息类
     */
    class RecommendedDormitory {
        private Long dormId;
        private String dormName;
        private int score;
        private String reason;
        private int availableBeds;
        private int sameMajorCount;
        
        public RecommendedDormitory(Long dormId, String dormName, int score, String reason, 
                                  int availableBeds, int sameMajorCount) {
            this.dormId = dormId;
            this.dormName = dormName;
            this.score = score;
            this.reason = reason;
            this.availableBeds = availableBeds;
            this.sameMajorCount = sameMajorCount;
        }
        
        // Getters and Setters
        public Long getDormId() { return dormId; }
        public void setDormId(Long dormId) { this.dormId = dormId; }
        
        public String getDormName() { return dormName; }
        public void setDormName(String dormName) { this.dormName = dormName; }
        
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public int getAvailableBeds() { return availableBeds; }
        public void setAvailableBeds(int availableBeds) { this.availableBeds = availableBeds; }
        
        public int getSameMajorCount() { return sameMajorCount; }
        public void setSameMajorCount(int sameMajorCount) { this.sameMajorCount = sameMajorCount; }
        
        @Override
        public String toString() {
            return "RecommendedDormitory{" +
                    "dormId=" + dormId +
                    ", dormName='" + dormName + '\'' +
                    ", score=" + score +
                    ", reason='" + reason + '\'' +
                    ", availableBeds=" + availableBeds +
                    ", sameMajorCount=" + sameMajorCount +
                    '}';
        }
    }
}