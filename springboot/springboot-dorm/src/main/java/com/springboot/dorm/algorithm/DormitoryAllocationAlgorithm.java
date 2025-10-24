package com.springboot.dorm.algorithm;

import com.springboot.dorm.domain.DormBed;
import com.springboot.dorm.domain.DormDormitory;
import com.springboot.dorm.domain.DormFloor;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.dorm.mapper.DormBedMapper;
import com.springboot.dorm.mapper.DormDormitoryMapper;
import com.springboot.dorm.mapper.DormFloorMapper;
import com.springboot.dorm.mapper.DormStudentMapper;
import com.springboot.common.core.domain.entity.SysUser;
import com.springboot.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 宿舍自动分配算法
 * 优先级：同性别 > 同学院 > 床位空闲度
 * 
 * @author system
 * @date 2025-01-21
 */
@Component
public class DormitoryAllocationAlgorithm {

    @Autowired
    private DormFloorMapper dormFloorMapper;
    
    @Autowired
    private DormDormitoryMapper dormDormitoryMapper;
    
    @Autowired
    private DormBedMapper dormBedMapper;
    
    @Autowired
    private DormStudentMapper dormStudentMapper;
    
    @Autowired
    private ISysUserService userService;

    /**
     * 自动分配宿舍算法
     * 优先级：同性别 > 同学院 > 床位空闲度
     */
    public AllocationResult autoAllocate(DormStudent student) {
        try {
            // 1. 参数验证
            if (student == null || student.getStuSex() == null || student.getUserId() == null) {
                return AllocationResult.failure("学生信息不完整，无法进行分配");
            }

            // 2. 筛选同性别宿舍楼
            List<DormFloor> availableFloors = getFloorsByGender(student.getStuSex());
            if (availableFloors.isEmpty()) {
                return AllocationResult.failure("没有找到适合的宿舍楼");
            }

            // 3. 查找可用宿舍（未满员）
            List<DormitoryScore> availableDorms = getAvailableDormitories(availableFloors, student);
            if (availableDorms.isEmpty()) {
                return AllocationResult.failure("没有可用的宿舍床位");
            }

            // 4. 按优先级排序
            availableDorms.sort((d1, d2) -> Integer.compare(d2.getTotalScore(), d1.getTotalScore()));

            // 5. 分配最优宿舍
            DormitoryScore bestDorm = availableDorms.get(0);
            return allocateBed(student, bestDorm.getDormitory());

        } catch (Exception e) {
            return AllocationResult.failure("分配过程中发生错误：" + e.getMessage());
        }
    }

    /**
     * 批量自动分配宿舍
     */
    public List<AllocationResult> batchAutoAllocate(List<DormStudent> students) {
        List<AllocationResult> results = new ArrayList<>();
        
        // 按性别和学院分组，优化分配效果
        Map<String, List<DormStudent>> groupedStudents = students.stream()
            .filter(s -> s.getUserId() != null) // 过滤掉没有用户ID的学生
            .collect(Collectors.groupingBy(s -> s.getStuSex() + "_" + getUserDeptId(s.getUserId())));

        for (List<DormStudent> group : groupedStudents.values()) {
            for (DormStudent student : group) {
                AllocationResult result = autoAllocate(student);
                results.add(result);
                
                // 如果分配失败，记录日志但继续处理下一个学生
                if (!result.isSuccess()) {
                    System.out.println("学生 " + student.getStuName() + " 分配失败：" + result.getMessage());
                }
            }
        }
        
        return results;
    }

    /**
     * 检查分配可行性
     */
    public boolean checkAllocationFeasibility(DormStudent student, Long dormId) {
        try {
            // 1. 检查宿舍是否存在
            DormDormitory dormitory = dormDormitoryMapper.selectDormDormitoryByDorId(dormId);
            if (dormitory == null) {
                return false;
            }

            // 2. 检查性别匹配
            DormFloor floor = dormFloorMapper.selectDormFloorByFId(dormitory.getfId());
            if (floor == null || !student.getStuSex().equals(floor.getfGender())) {
                return false;
            }

            // 3. 检查床位可用性
            List<DormBed> availableBeds = getAvailableBeds(dormId);
            if (availableBeds.isEmpty()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据性别筛选宿舍楼
     */
    private List<DormFloor> getFloorsByGender(String gender) {
        DormFloor queryFloor = new DormFloor();
        queryFloor.setfGender(gender);
        queryFloor.setfStatus("0"); // 正常状态
        return dormFloorMapper.selectDormFloorList(queryFloor);
    }

    /**
     * 获取可用宿舍并计算评分
     */
    private List<DormitoryScore> getAvailableDormitories(List<DormFloor> floors, DormStudent student) {
        List<DormitoryScore> scoredDorms = new ArrayList<>();

        for (DormFloor floor : floors) {
            // 查询该楼层的所有宿舍
            DormDormitory queryDorm = new DormDormitory();
            queryDorm.setfId(floor.getfId());
            queryDorm.setDorStatus(0); // 正常状态
            List<DormDormitory> dormitories = dormDormitoryMapper.selectDormDormitoryList(queryDorm);

            for (DormDormitory dormitory : dormitories) {
                // 检查是否有可用床位
                List<DormBed> availableBeds = getAvailableBeds(dormitory.getDorId());
                if (!availableBeds.isEmpty()) {
                    int score = calculateDormitoryScore(dormitory, student);
                    scoredDorms.add(new DormitoryScore(dormitory, score));
                }
            }
        }

        return scoredDorms;
    }

    /**
     * 计算宿舍评分
     */
    private int calculateDormitoryScore(DormDormitory dormitory, DormStudent student) {
        int score = 0;

        // 1. 同学院学生数量权重（权重：3）
        int sameCollegeCount = getSameCollegeCount(dormitory, getUserDeptId(student.getUserId()));
        score += sameCollegeCount * 3;

        // 2. 空闲床位权重（权重：2）
        int availableBedCount = getAvailableBeds(dormitory.getDorId()).size();
        score += availableBedCount * 2;

        // 3. 宿舍容量利用率权重（权重：1）
        int totalBeds = getTotalBeds(dormitory.getDorId());
        int occupiedBeds = totalBeds - availableBedCount;
        if (totalBeds > 0) {
            int utilizationRate = (occupiedBeds * 100) / totalBeds;
            // 50%-80%利用率最佳，给予额外分数
            if (utilizationRate >= 50 && utilizationRate <= 80) {
                score += 5;
            }
        }

        return score;
    }

    /**
     * 获取宿舍中同学院学生数量
     */
    private int getSameCollegeCount(DormDormitory dormitory, Long deptId) {
        if (deptId == null) {
            return 0;
        }
        
        List<DormBed> beds = dormBedMapper.selectDormBedList(new DormBed() {{
             setDorId(dormitory.getDorId());
         }});

        int count = 0;
        for (DormBed bed : beds) {
            if (bed.getStuId() != null) {
                DormStudent student = dormStudentMapper.selectDormStudentByStuId(bed.getStuId());
                if (student != null && student.getUserId() != null) {
                    Long studentDeptId = getUserDeptId(student.getUserId());
                    if (deptId.equals(studentDeptId)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * 根据用户ID获取部门ID
     */
    private Long getUserDeptId(Long userId) {
        if (userId == null) {
            return null;
        }
        
        try {
            SysUser user = userService.selectUserById(userId);
            return user != null ? user.getDeptId() : null;
        } catch (Exception e) {
            System.err.println("获取用户部门信息失败，用户ID: " + userId + ", 错误: " + e.getMessage());
            return null;
        }
    }

    /**
     * 获取可用床位
     */
    private List<DormBed> getAvailableBeds(Long dormId) {
        DormBed queryBed = new DormBed();
        queryBed.setDorId(dormId);
        queryBed.setBedStatus("0"); // 空闲状态
        return dormBedMapper.selectDormBedList(queryBed);
    }

    /**
     * 获取宿舍总床位数
     */
    private int getTotalBeds(Long dormId) {
        DormBed queryBed = new DormBed();
        queryBed.setDorId(dormId);
        List<DormBed> allBeds = dormBedMapper.selectDormBedList(queryBed);
        return allBeds.size();
    }

    /**
     * 分配床位给学生
     */
    private AllocationResult allocateBed(DormStudent student, DormDormitory dormitory) {
        try {
            // 获取可用床位
            List<DormBed> availableBeds = getAvailableBeds(dormitory.getDorId());
            if (availableBeds.isEmpty()) {
                return AllocationResult.failure("宿舍 " + dormitory.getDorName() + " 没有可用床位");
            }

            // 选择第一个可用床位
            DormBed selectedBed = availableBeds.get(0);
            
            // 更新床位状态
            selectedBed.setStuId(student.getStuId());
            selectedBed.setBedStatus("1"); // 已占用
            int bedUpdateResult = dormBedMapper.updateDormBed(selectedBed);

            if (bedUpdateResult > 0) {
                // 更新学生宿舍信息
                student.setDorId(dormitory.getDorId());
                student.setfId(dormitory.getfId());
                student.setStuStatus("1"); // 激活学生状态
                dormStudentMapper.updateDormStudent(student);

                return AllocationResult.success(
                    "成功分配到 " + dormitory.getDorName() + " 的 " + selectedBed.getBedCode() + " 床位",
                    selectedBed
                );
            } else {
                return AllocationResult.failure("床位分配失败");
            }

        } catch (Exception e) {
            return AllocationResult.failure("分配过程中发生错误：" + e.getMessage());
        }
    }

    /**
     * 宿舍评分内部类
     */
    private static class DormitoryScore {
        private DormDormitory dormitory;
        private int totalScore;

        public DormitoryScore(DormDormitory dormitory, int totalScore) {
            this.dormitory = dormitory;
            this.totalScore = totalScore;
        }

        public DormDormitory getDormitory() {
            return dormitory;
        }

        public int getTotalScore() {
            return totalScore;
        }
    }

    /**
     * 分配结果类
     */
    public static class AllocationResult {
        private boolean success;
        private String message;
        private DormBed allocatedBed;

        private AllocationResult(boolean success, String message, DormBed allocatedBed) {
            this.success = success;
            this.message = message;
            this.allocatedBed = allocatedBed;
        }

        public static AllocationResult success(String message, DormBed bed) {
            return new AllocationResult(true, message, bed);
        }

        public static AllocationResult failure(String message) {
            return new AllocationResult(false, message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public DormBed getAllocatedBed() {
            return allocatedBed;
        }

        @Override
        public String toString() {
            return "AllocationResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", allocatedBed=" + (allocatedBed != null ? allocatedBed.getBedCode() : "null") +
                    '}';
        }
    }
}