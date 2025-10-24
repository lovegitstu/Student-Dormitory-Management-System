package com.springboot.dorm.service.impl;

import com.springboot.dorm.algorithm.DormitoryAllocationAlgorithm;
import com.springboot.dorm.algorithm.DormitoryAllocationAlgorithm.AllocationResult;
import com.springboot.dorm.domain.DormBed;
import com.springboot.dorm.domain.DormDormitory;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.dorm.mapper.DormBedMapper;
import com.springboot.dorm.mapper.DormDormitoryMapper;
import com.springboot.dorm.service.IDormBedService;
import com.springboot.dorm.service.IDormStudentService;
import com.springboot.dorm.service.IDormitoryAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 宿舍自动分配服务实现类
 * 
 * @author system
 * @date 2025-01-21
 */
@Service
public class DormitoryAllocationServiceImpl implements IDormitoryAllocationService {

    @Autowired
    private DormitoryAllocationAlgorithm allocationAlgorithm;
    
    @Autowired
    private IDormBedService dormBedService;
    
    @Autowired
    private IDormStudentService dormStudentService;
    
    @Autowired
    private DormBedMapper dormBedMapper;
    
    @Autowired
    private DormDormitoryMapper dormDormitoryMapper;

    /**
     * 自动分配宿舍
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AllocationResult autoAllocateDormitory(DormStudent student) {
        try {
            // 验证学生信息
            if (student == null || student.getStuId() == null) {
                return AllocationResult.failure("学生信息不完整");
            }
            
            // 检查学生是否已有宿舍
            DormStudent existingStudent = dormStudentService.selectDormStudentByStuId(student.getStuId());
            if (existingStudent != null && existingStudent.getDorId() != null) {
                return AllocationResult.failure("学生已分配宿舍，无需重复分配");
            }
            
            // 调用分配算法
            AllocationResult result = allocationAlgorithm.autoAllocate(student);
            
            if (result.isSuccess()) {
                // 记录分配日志
                System.out.println("成功为学生 " + student.getStuName() + " 分配宿舍: " + result.getMessage());
            }
            
            return result;
            
        } catch (Exception e) {
            System.err.println("自动分配宿舍时发生错误: " + e.getMessage());
            return AllocationResult.failure("分配过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 批量自动分配宿舍
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AllocationResult> batchAutoAllocateDormitory(List<DormStudent> students) {
        List<AllocationResult> results = new ArrayList<>();
        
        if (students == null || students.isEmpty()) {
            return results;
        }
        
        try {
            // 过滤已分配宿舍的学生
            List<DormStudent> unallocatedStudents = new ArrayList<>();
            for (DormStudent student : students) {
                DormStudent existingStudent = dormStudentService.selectDormStudentByStuId(student.getStuId());
                if (existingStudent == null || existingStudent.getDorId() == null) {
                    unallocatedStudents.add(student);
                } else {
                    results.add(AllocationResult.failure("学生 " + student.getStuName() + " 已分配宿舍"));
                }
            }
            
            // 批量分配
            List<AllocationResult> batchResults = allocationAlgorithm.batchAutoAllocate(unallocatedStudents);
            results.addAll(batchResults);
            
            // 统计分配结果
            long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
            System.out.println("批量分配完成，成功: " + successCount + "，失败: " + (results.size() - successCount));
            
        } catch (Exception e) {
            System.err.println("批量分配宿舍时发生错误: " + e.getMessage());
            results.add(AllocationResult.failure("批量分配过程中发生错误: " + e.getMessage()));
        }
        
        return results;
    }

    /**
     * 检查分配可行性
     */
    @Override
    public boolean checkAllocationFeasibility(DormStudent student, Long dormId) {
        try {
            if (student == null || dormId == null) {
                return false;
            }
            
            return allocationAlgorithm.checkAllocationFeasibility(student, dormId);
            
        } catch (Exception e) {
            System.err.println("检查分配可行性时发生错误: " + e.getMessage());
            return false;
        }
    }

    /**
     * 手动分配宿舍
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AllocationResult manualAllocateDormitory(Long studentId, Long dormId) {
        try {
            // 获取学生信息
            DormStudent student = dormStudentService.selectDormStudentByStuId(studentId);
            if (student == null) {
                return AllocationResult.failure("学生不存在");
            }
            
            // 检查学生是否已有宿舍
            if (student.getDorId() != null) {
                return AllocationResult.failure("学生已分配宿舍，请先释放原宿舍");
            }
            
            // 检查分配可行性
            if (!checkAllocationFeasibility(student, dormId)) {
                return AllocationResult.failure("分配不可行，请检查宿舍状态和学生信息");
            }
            
            // 获取可用床位
            DormBed queryBed = new DormBed();
            queryBed.setDorId(dormId);
            queryBed.setBedStatus("0"); // 空闲状态
            List<DormBed> availableBeds = dormBedService.selectDormBedList(queryBed);
            
            if (availableBeds.isEmpty()) {
                return AllocationResult.failure("宿舍没有可用床位");
            }
            
            // 分配第一个可用床位
            DormBed selectedBed = availableBeds.get(0);
            selectedBed.setStuId(studentId);
            selectedBed.setBedStatus("1"); // 已占用
            
            int result = dormBedService.updateDormBed(selectedBed);
            if (result > 0) {
                DormDormitory dormitory = dormDormitoryMapper.selectDormDormitoryByDorId(dormId);
                String dormName = dormitory != null ? dormitory.getDorName() : "未知宿舍";
                return AllocationResult.success(
                    "成功手动分配到 " + dormName + " 的 " + selectedBed.getBedCode() + " 床位",
                    selectedBed
                );
            } else {
                return AllocationResult.failure("床位分配失败");
            }
            
        } catch (Exception e) {
            System.err.println("手动分配宿舍时发生错误: " + e.getMessage());
            return AllocationResult.failure("分配过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 释放宿舍床位
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseDormitory(Long studentId) {
        try {
            if (studentId == null) {
                return false;
            }
            
            // 查找学生当前床位
            DormBed queryBed = new DormBed();
            queryBed.setStuId(studentId);
            queryBed.setBedStatus("1"); // 已占用状态
            List<DormBed> occupiedBeds = dormBedService.selectDormBedList(queryBed);
            
            if (occupiedBeds.isEmpty()) {
                System.out.println("学生 " + studentId + " 没有分配床位");
                return true; // 没有床位也算释放成功
            }
            
            // 释放所有床位（理论上一个学生只应该有一个床位）
            for (DormBed bed : occupiedBeds) {
                bed.setStuId(null);
                bed.setBedStatus("0"); // 空闲状态
                
                int result = dormBedService.updateDormBed(bed);
                if (result <= 0) {
                    System.err.println("释放床位失败: " + bed.getBedCode());
                    return false;
                }
            }
            
            System.out.println("成功释放学生 " + studentId + " 的宿舍床位");
            return true;
            
        } catch (Exception e) {
            System.err.println("释放宿舍床位时发生错误: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取推荐宿舍列表
     */
    @Override
    public List<RecommendedDormitory> getRecommendedDormitories(DormStudent student, int limit) {
        List<RecommendedDormitory> recommendations = new ArrayList<>();
        
        try {
            if (student == null || limit <= 0) {
                return recommendations;
            }
            
            // 这里可以复用分配算法的逻辑来获取推荐宿舍
            // 为了简化，我们直接调用分配算法获取最佳匹配
            AllocationResult result = allocationAlgorithm.autoAllocate(student);
            
            if (result.isSuccess() && result.getAllocatedBed() != null) {
                DormBed allocatedBed = result.getAllocatedBed();
                DormDormitory dormitory = dormDormitoryMapper.selectDormDormitoryByDorId(allocatedBed.getDorId());
                
                if (dormitory != null) {
                    // 计算可用床位数
                    DormBed queryBed = new DormBed();
                    queryBed.setDorId(dormitory.getDorId());
                    queryBed.setBedStatus("0");
                    List<DormBed> availableBeds = dormBedService.selectDormBedList(queryBed);
                    
                    // 计算同专业学生数
                    // 这里简化处理，实际应该查询该宿舍的学生专业分布
                    int sameMajorCount = 0;
                    
                    RecommendedDormitory recommendation = new RecommendedDormitory(
                        dormitory.getDorId(),
                        dormitory.getDorName(),
                        100, // 最高分
                        "最佳匹配宿舍",
                        availableBeds.size(),
                        sameMajorCount
                    );
                    
                    recommendations.add(recommendation);
                }
            }
            
        } catch (Exception e) {
            System.err.println("获取推荐宿舍时发生错误: " + e.getMessage());
        }
        
        return recommendations;
    }
}