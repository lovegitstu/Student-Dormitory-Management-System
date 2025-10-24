package com.springboot.dorm.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springboot.common.utils.DateUtils;
import com.springboot.dorm.mapper.DormBedMapper;
import com.springboot.dorm.mapper.DormDormitoryMapper;
import com.springboot.dorm.domain.DormBed;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.dorm.domain.DormDormitory;
import com.springboot.dorm.service.IDormBedService;
import com.springboot.dorm.service.IDormStudentService;
import com.springboot.dorm.algorithm.DormitoryAllocationAlgorithm;
import com.springboot.dorm.algorithm.DormitoryAllocationAlgorithm.AllocationResult;

/**
 * 床位管理Service业务层处理
 * 
 *
 * @date 2025-09-15
 */
@Service
public class DormBedServiceImpl implements IDormBedService 
{
    @Autowired
    private DormBedMapper dormBedMapper;
    
    @Autowired
    private DormDormitoryMapper dormDormitoryMapper;
    
    @Autowired
    private IDormStudentService dormStudentService;
    
    @Autowired
    private DormitoryAllocationAlgorithm allocationAlgorithm;

    /**
     * 查询床位管理
     * 
     * @param bedId 床位管理主键
     * @return 床位管理
     */
    @Override
    public DormBed selectDormBedByBedId(Long bedId)
    {
        return dormBedMapper.selectDormBedByBedId(bedId);
    }

    /**
     * 查询床位管理列表
     * 
     * @param dormBed 床位管理
     * @return 床位管理
     */
    @Override
    public List<DormBed> selectDormBedList(DormBed dormBed)
    {
        System.out.println("=== DormBedServiceImpl.selectDormBedList 开始 ===");
        System.out.println("查询参数 - stuId: " + dormBed.getStuId() + ", bedStatus: " + dormBed.getBedStatus());
        System.out.println("查询参数 - dorId: " + dormBed.getDorId() + ", bedCode: " + dormBed.getBedCode());
        
        List<DormBed> result = dormBedMapper.selectDormBedList(dormBed);
        
        System.out.println("查询结果数量: " + (result != null ? result.size() : "null"));
        if (result != null && !result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                DormBed bed = result.get(i);
                System.out.println("结果[" + i + "] - bedId: " + bed.getBedId() + 
                                 ", dorId: " + bed.getDorId() + 
                                 ", stuId: " + bed.getStuId() + 
                                 ", stuName: " + bed.getStuName() + 
                                 ", bedCode: " + bed.getBedCode() + 
                                 ", bedStatus: " + bed.getBedStatus());
            }
        }
        System.out.println("=== DormBedServiceImpl.selectDormBedList 结束 ===");
        
        return result;
    }

    /**
     * 新增床位管理
     * 
     * @param dormBed 床位管理
     * @return 结果
     */
    @Override
    public int insertDormBed(DormBed dormBed)
    {
        dormBed.setCreateTime(DateUtils.getNowDate());
        return dormBedMapper.insertDormBed(dormBed);
    }

    /**
     * 修改床位管理
     * 
     * @param dormBed 床位管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDormBed(DormBed dormBed)
    {
        // 数据一致性验证
        if (!validateBedAllocation(dormBed)) {
            throw new RuntimeException("床位分配数据验证失败");
        }
        
        dormBed.setUpdateTime(DateUtils.getNowDate());
        
        // 获取原床位信息
        DormBed oldBed = dormBedMapper.selectDormBedByBedId(dormBed.getBedId());
        if (oldBed == null) {
            throw new RuntimeException("床位不存在，无法更新");
        }
        
        // 更新床位信息
        int result = dormBedMapper.updateDormBed(dormBed);
        
        // 如果床位分配发生变化，需要同步更新学生表
        if (result > 0) {
            // 处理原学生的宿舍信息清空
            if (oldBed.getStuId() != null && 
                (dormBed.getStuId() == null || !oldBed.getStuId().equals(dormBed.getStuId()))) {
                clearStudentDormInfo(oldBed.getStuId());
            }
            
            // 处理新学生的宿舍信息更新
            if (dormBed.getStuId() != null) {
                updateStudentDormInfo(dormBed.getStuId(), dormBed.getDorId());
            }
            
            // 自动更新宿舍状态
            updateDormitoryStatus(dormBed.getDorId());
            
            // 验证更新后的数据一致性
            if (!verifyDataConsistency(dormBed)) {
                throw new RuntimeException("数据一致性验证失败，事务回滚");
            }
        }
        
        return result;
    }

    /**
     * 批量删除床位管理
     * 
     * @param bedIds 需要删除的床位管理主键
     * @return 结果
     */
    @Override
    public int deleteDormBedByBedIds(Long[] bedIds)
    {
        return dormBedMapper.deleteDormBedByBedIds(bedIds);
    }

    /**
     * 删除床位管理信息
     * 
     * @param bedId 床位管理主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDormBedByBedId(Long bedId)
    {
        // 获取床位信息
        DormBed bed = dormBedMapper.selectDormBedByBedId(bedId);
        if (bed == null) {
            throw new RuntimeException("床位不存在，无法删除");
        }
        
        // 如果床位有学生，先清空学生的宿舍信息
        if (bed.getStuId() != null) {
            clearStudentDormInfo(bed.getStuId());
        }
        
        return dormBedMapper.deleteDormBedByBedId(bedId);
    }
    
    /**
     * 更新学生的宿舍信息
     * 
     * @param stuId 学生ID
     * @param dorId 宿舍ID
     */
    @Transactional(rollbackFor = Exception.class)
    private void updateStudentDormInfo(Long stuId, Long dorId) {
        if (stuId == null || dorId == null) {
            throw new RuntimeException("学生ID或宿舍ID不能为空");
        }
        
        // 获取宿舍信息
        DormDormitory dormitory = dormDormitoryMapper.selectDormDormitoryByDorId(dorId);
        if (dormitory == null) {
            throw new RuntimeException("宿舍不存在，ID: " + dorId);
        }
        
        // 获取学生信息
        DormStudent student = dormStudentService.selectDormStudentByStuId(stuId);
        if (student == null) {
            throw new RuntimeException("学生不存在，ID: " + stuId);
        }
        
        // 创建一个新的学生对象，只包含需要更新的字段
        DormStudent updateStudent = new DormStudent();
        updateStudent.setStuId(stuId);
        updateStudent.setDorId(dorId);
        updateStudent.setfId(dormitory.getfId());
        updateStudent.setStuStatus("1"); // 入住后激活学生状态
        
        // 保存更新 - 更新宿舍相关信息和学生状态
        int result = dormStudentService.updateDormStudent(updateStudent);
        if (result <= 0) {
            throw new RuntimeException("更新学生宿舍信息失败");
        }
    }
    
    /**
     * 清空学生的宿舍信息
     * 
     * @param stuId 学生ID
     */
    @Transactional(rollbackFor = Exception.class)
    private void clearStudentDormInfo(Long stuId) {
        if (stuId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        
        // 获取学生信息
        DormStudent student = dormStudentService.selectDormStudentByStuId(stuId);
        if (student == null) {
            throw new RuntimeException("学生不存在，ID: " + stuId);
        }
        
        // 创建一个新的学生对象，只包含需要更新的字段
        DormStudent updateStudent = new DormStudent();
        updateStudent.setStuId(stuId);
        updateStudent.setDorId(null);
        updateStudent.setfId(null);
        updateStudent.setStuStatus("0"); // 退宿后设置为未激活状态
        
        // 保存更新 - 清空宿舍相关信息并更新学生状态
        int result = dormStudentService.updateDormStudent(updateStudent);
        if (result <= 0) {
            throw new RuntimeException("清空学生宿舍信息失败");
        }
    }
    
    /**
     * 验证床位分配数据
     * 
     * @param dormBed 床位信息
     * @return 验证结果
     */
    private boolean validateBedAllocation(DormBed dormBed) {
        if (dormBed == null) {
            return false;
        }
        
        // 如果分配了学生，验证学生是否存在且未被其他床位占用
        if (dormBed.getStuId() != null) {
            DormStudent student = dormStudentService.selectDormStudentByStuId(dormBed.getStuId());
            if (student == null) {
                return false;
            }
            
            // 检查该学生是否已被其他床位占用
            DormBed queryBed = new DormBed();
            queryBed.setStuId(dormBed.getStuId());
            queryBed.setBedStatus("1"); // 已占用状态
            List<DormBed> existingBeds = dormBedMapper.selectDormBedList(queryBed);
            
            // 如果找到其他床位占用了该学生，且不是当前床位，则验证失败
            for (DormBed existingBed : existingBeds) {
                if (!existingBed.getBedId().equals(dormBed.getBedId())) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * 验证数据一致性
     * 
     * @param dormBed 床位信息
     * @return 验证结果
     */
    private boolean verifyDataConsistency(DormBed dormBed) {
        if (dormBed == null) {
            return false;
        }
        
        // 验证床位-学生关联的一致性
        if (dormBed.getStuId() != null) {
            DormStudent student = dormStudentService.selectDormStudentByStuId(dormBed.getStuId());
            if (student == null) {
                return false;
            }
            
            // 验证学生的宿舍信息是否与床位一致
            if (!dormBed.getDorId().equals(student.getDorId())) {
                return false;
            }
            
            // 验证床位状态是否正确
            if (!"1".equals(dormBed.getBedStatus())) {
                return false;
            }
        } else {
            // 如果没有学生，床位状态应该是空闲
            if (!"0".equals(dormBed.getBedStatus())) {
                return false;
            }
        }
        
        return true;
    }
    
    // ========== 智能分配功能实现 ==========
    
    /**
     * 智能分配床位给学生
     * 
     * @param student 学生信息
     * @return 分配结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AllocationResult smartAllocateBed(DormStudent student) {
        if (student == null || student.getStuId() == null) {
            return AllocationResult.failure("学生信息不能为空");
        }
        
        // 检查学生是否已经分配了床位
        if (checkBedAllocationConflict(null, student.getStuId())) {
            return AllocationResult.failure("学生已经分配了床位");
        }
        
        // 获取可用床位列表
        List<DormBed> availableBeds = getAvailableBedsForStudent(student);
        if (availableBeds.isEmpty()) {
            return AllocationResult.failure("没有可用的床位");
        }
        
        // 使用分配算法选择最佳床位
        DormBed selectedBed = selectOptimalBed(student, availableBeds);
        if (selectedBed == null) {
            return AllocationResult.failure("无法找到合适的床位");
        }
        
        // 执行床位分配
        selectedBed.setStuId(student.getStuId());
        selectedBed.setBedStatus("1"); // 设置为已占用
        selectedBed.setUpdateTime(DateUtils.getNowDate());
        
        int result = updateDormBed(selectedBed);
        if (result > 0) {
            return AllocationResult.success("床位分配成功", selectedBed);
        } else {
            return AllocationResult.failure("床位分配失败");
        }
    }
    
    /**
     * 批量智能分配床位
     * 
     * @param students 学生列表
     * @return 分配结果列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AllocationResult> batchSmartAllocateBeds(List<DormStudent> students) {
        List<AllocationResult> results = new ArrayList<>();
        
        if (students == null || students.isEmpty()) {
            return results;
        }
        
        // 按优先级排序学生（可以根据年级、专业等排序）
        List<DormStudent> sortedStudents = students.stream()
                .sorted(Comparator.comparing(DormStudent::getStuGrade, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(DormStudent::getStuMajor, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        
        // 逐个分配床位
        for (DormStudent student : sortedStudents) {
            AllocationResult result = smartAllocateBed(student);
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * 获取可用床位列表（按优先级排序）
     * 
     * @param student 学生信息
     * @return 可用床位列表
     */
    @Override
    public List<DormBed> getAvailableBedsForStudent(DormStudent student) {
        // 查询所有空闲床位
        DormBed queryBed = new DormBed();
        queryBed.setBedStatus("0"); // 空闲状态
        List<DormBed> availableBeds = dormBedMapper.selectDormBedList(queryBed);
        
        if (availableBeds.isEmpty()) {
            return availableBeds;
        }
        
        // 根据学生信息过滤和排序床位
        return availableBeds.stream()
                .filter(bed -> isGenderCompatible(bed, student))
                .sorted((bed1, bed2) -> {
                    // 优先级排序：同专业 > 同年级 > 同楼层 > 床位号
                    int priority1 = calculateBedPriority(bed1, student);
                    int priority2 = calculateBedPriority(bed2, student);
                    
                    if (priority1 != priority2) {
                        return Integer.compare(priority2, priority1); // 高优先级在前
                    }
                    
                    // 优先级相同时按床位号排序
                    return bed1.getBedId().compareTo(bed2.getBedId());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 检查床位分配冲突
     * 
     * @param bedId 床位ID
     * @param studentId 学生ID
     * @return 是否存在冲突
     */
    @Override
    public boolean checkBedAllocationConflict(Long bedId, Long studentId) {
        if (studentId == null) {
            return false;
        }
        
        // 检查学生是否已被其他床位占用
        DormBed queryBed = new DormBed();
        queryBed.setStuId(studentId);
        queryBed.setBedStatus("1"); // 已占用状态
        List<DormBed> existingBeds = dormBedMapper.selectDormBedList(queryBed);
        
        // 如果指定了床位ID，排除该床位
        if (bedId != null) {
            existingBeds = existingBeds.stream()
                    .filter(bed -> !bed.getBedId().equals(bedId))
                    .collect(Collectors.toList());
        }
        
        return !existingBeds.isEmpty();
    }
    
    /**
     * 获取床位利用率统计
     * 
     * @param dormId 宿舍ID（可选，为null时统计全部）
     * @return 床位利用率信息
     */
    @Override
    public BedUtilizationStats getBedUtilizationStats(Long dormId) {
        DormBed queryBed = new DormBed();
        if (dormId != null) {
            queryBed.setDorId(dormId);
        }
        
        // 查询所有床位
        List<DormBed> allBeds = dormBedMapper.selectDormBedList(queryBed);
        int totalBeds = allBeds.size();
        
        // 统计已占用床位
        int occupiedBeds = (int) allBeds.stream()
                .filter(bed -> "1".equals(bed.getBedStatus()))
                .count();
        
        return new BedUtilizationStats(totalBeds, occupiedBeds);
    }
    
    // ========== 私有辅助方法 ==========
    
    /**
     * 检查性别兼容性
     * 
     * @param bed 床位信息
     * @param student 学生信息
     * @return 是否兼容
     */
    private boolean isGenderCompatible(DormBed bed, DormStudent student) {
        if (bed.getDorId() == null || student.getStuGender() == null) {
            return true; // 信息不完整时默认兼容
        }
        
        // 获取宿舍信息
        DormDormitory dormitory = dormDormitoryMapper.selectDormDormitoryByDorId(bed.getDorId());
        if (dormitory == null) {
            return true;
        }
        
        // 检查宿舍类型是否与学生性别匹配
        Integer dormTypeInt = dormitory.getDorType();
        String studentGender = student.getStuGender();
        
        // 根据宿舍类型判断性别兼容性
        // 假设：1-男宿，2-女宿，3-混合宿舍
        if (dormTypeInt != null) {
            if (dormTypeInt == 1 && "男".equals(studentGender)) {
                return true;
            }
            if (dormTypeInt == 2 && "女".equals(studentGender)) {
                return true;
            }
            if (dormTypeInt == 3) {
                return true; // 混合宿舍兼容所有性别
            }
        }
        
        return false; // 默认不兼容
    }
    
    /**
     * 计算床位优先级
     * 
     * @param bed 床位信息
     * @param student 学生信息
     * @return 优先级分数（越高越优先）
     */
    private int calculateBedPriority(DormBed bed, DormStudent student) {
        int priority = 0;
        
        if (bed.getDorId() == null) {
            return priority;
        }
        
        // 获取宿舍信息
        DormDormitory dormitory = dormDormitoryMapper.selectDormDormitoryByDorId(bed.getDorId());
        if (dormitory == null) {
            return priority;
        }
        
        // 检查是否有同专业的室友
        if (hasSameMajorRoommate(bed, student)) {
            priority += 100;
        }
        
        // 检查是否有同年级的室友
        if (hasSameGradeRoommate(bed, student)) {
            priority += 50;
        }
        
        // 楼层优先级（低楼层优先）
        if (dormitory.getfId() != null) {
            priority += Math.max(0, 10 - dormitory.getfId().intValue());
        }
        
        return priority;
    }
    
    /**
     * 检查是否有同专业室友
     * 
     * @param bed 床位信息
     * @param student 学生信息
     * @return 是否有同专业室友
     */
    private boolean hasSameMajorRoommate(DormBed bed, DormStudent student) {
        if (bed.getDorId() == null || student.getStuMajor() == null) {
            return false;
        }
        
        // 查询同宿舍的其他床位
        DormBed queryBed = new DormBed();
        queryBed.setDorId(bed.getDorId());
        queryBed.setBedStatus("1"); // 已占用
        List<DormBed> occupiedBeds = dormBedMapper.selectDormBedList(queryBed);
        
        // 检查室友专业
        for (DormBed occupiedBed : occupiedBeds) {
            if (occupiedBed.getStuId() != null) {
                DormStudent roommate = dormStudentService.selectDormStudentByStuId(occupiedBed.getStuId());
                if (roommate != null && student.getStuMajor().equals(roommate.getStuMajor())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 检查是否有同年级室友
     * 
     * @param bed 床位信息
     * @param student 学生信息
     * @return 是否有同年级室友
     */
    private boolean hasSameGradeRoommate(DormBed bed, DormStudent student) {
        if (bed.getDorId() == null || student.getStuGrade() == null) {
            return false;
        }
        
        // 查询同宿舍的其他床位
        DormBed queryBed = new DormBed();
        queryBed.setDorId(bed.getDorId());
        queryBed.setBedStatus("1"); // 已占用
        List<DormBed> occupiedBeds = dormBedMapper.selectDormBedList(queryBed);
        
        // 检查室友年级
        for (DormBed occupiedBed : occupiedBeds) {
            if (occupiedBed.getStuId() != null) {
                DormStudent roommate = dormStudentService.selectDormStudentByStuId(occupiedBed.getStuId());
                if (roommate != null && student.getStuGrade().equals(roommate.getStuGrade())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 选择最优床位
     * 
     * @param student 学生信息
     * @param availableBeds 可用床位列表
     * @return 最优床位
     */
    private DormBed selectOptimalBed(DormStudent student, List<DormBed> availableBeds) {
        if (availableBeds == null || availableBeds.isEmpty()) {
            return null;
        }
        
        // 如果只有一个床位，直接返回
        if (availableBeds.size() == 1) {
            return availableBeds.get(0);
        }
        
        // 按优先级排序后返回第一个
        return availableBeds.stream()
                .max(Comparator.comparingInt(bed -> calculateBedPriority(bed, student)))
                .orElse(null);
    }
    
    /**
     * 自动更新宿舍状态
     * 当宿舍内所有床位都空闲时，将宿舍状态设为空闲
     * 当宿舍内有床位被占用时，将宿舍状态设为已占用
     * 
     * @param dorId 宿舍ID
     */
    @Transactional(rollbackFor = Exception.class)
    private void updateDormitoryStatus(Long dorId) {
        if (dorId == null) {
            return;
        }
        
        // 查询该宿舍的所有床位
        DormBed queryBed = new DormBed();
        queryBed.setDorId(dorId);
        List<DormBed> bedList = dormBedMapper.selectDormBedList(queryBed);
        
        if (bedList == null || bedList.isEmpty()) {
            return;
        }
        
        // 检查是否有床位被占用
        boolean hasOccupiedBed = bedList.stream()
                .anyMatch(bed -> "1".equals(bed.getBedStatus()) && bed.getStuId() != null);
        
        // 获取当前宿舍信息
        DormDormitory dormitory = dormDormitoryMapper.selectDormDormitoryByDorId(dorId);
        if (dormitory == null) {
            return;
        }
        
        // 根据床位占用情况更新宿舍状态
        Integer newStatus = hasOccupiedBed ? 1 : 0; // 1-已占用，0-空闲
        
        // 只有当状态发生变化时才更新
        if (!newStatus.equals(dormitory.getDorStatus())) {
            DormDormitory updateDormitory = new DormDormitory();
            updateDormitory.setDorId(dorId);
            updateDormitory.setDorStatus(newStatus);
            updateDormitory.setUpdateTime(DateUtils.getNowDate());
            
            dormDormitoryMapper.updateDormDormitory(updateDormitory);
        }
    }
}
