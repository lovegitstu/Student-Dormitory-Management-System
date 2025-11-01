package com.springboot.dorm.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.common.annotation.Log;
import com.springboot.common.core.controller.BaseController;
import com.springboot.common.core.domain.AjaxResult;
import com.springboot.common.enums.BusinessType;
import com.springboot.dorm.domain.DormBed;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.dorm.service.IDormBedService;
import com.springboot.dorm.service.IDormStudentService;
import com.springboot.dorm.service.IDormitoryAllocationService;
import com.springboot.dorm.algorithm.DormitoryAllocationAlgorithm.AllocationResult;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;
import com.springboot.common.utils.SecurityUtils;

/**
 * 床位管理Controller
 * 
 *
 * @date 2025-09-15
 */
@RestController
@RequestMapping("/dormitory/bed")
public class DormBedController extends BaseController
{
    @Autowired
    private IDormBedService dormBedService;

    @Autowired
    private IDormStudentService dormStudentService;

    @Autowired
    private IDormitoryAllocationService dormitoryAllocationService;

    /**
     * 查询床位管理列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormBed dormBed)
    {
        startPage();
        List<DormBed> list = dormBedService.selectDormBedList(dormBed);
        if (isCurrentUserStudent() && list != null) {
            Long currentStuId = getCurrentStudentId();
            for (DormBed bed : list) {
                sanitizeBedForStudent(bed, currentStuId);
            }
        }
        return getDataTable(list);
    }

    /**
     * 导出床位管理列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:export')")
    @Log(title = "床位管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormBed dormBed)
    {
        List<DormBed> list = dormBedService.selectDormBedList(dormBed);
        if (isCurrentUserStudent() && list != null) {
            Long currentStuId = getCurrentStudentId();
            for (DormBed bed : list) {
                sanitizeBedForStudent(bed, currentStuId);
            }
        }
        ExcelUtil<DormBed> util = new ExcelUtil<DormBed>(DormBed.class);
        util.exportExcel(response, list, "床位管理数据");
    }

    /**
     * 获取床位管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:query')")
    @GetMapping(value = "/{bedId}")
    public AjaxResult getInfo(@PathVariable("bedId") Long bedId)
    {
        DormBed bed = dormBedService.selectDormBedByBedId(bedId);
        if (bed != null && isCurrentUserStudent()) {
            sanitizeBedForStudent(bed, getCurrentStudentId());
        }
        return success(bed);
    }

    /**
     * 新增床位管理
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:add')")
    @Log(title = "床位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormBed dormBed)
    {
        return toAjax(dormBedService.insertDormBed(dormBed));
    }

    /**
     * 修改床位管理
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:edit')")
    @Log(title = "床位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormBed dormBed)
    {
        return toAjax(dormBedService.updateDormBed(dormBed));
    }

    /**
     * 删除床位管理
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:remove')")
    @Log(title = "床位管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{bedIds}")
    public AjaxResult remove(@PathVariable Long[] bedIds)
    {
        return toAjax(dormBedService.deleteDormBedByBedIds(bedIds));
    }

    /**
     * 检查学生是否已分配床位
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:query')")
    @GetMapping("/checkStudentBed/{userId}")
    public AjaxResult checkStudentBed(@PathVariable("userId") Long userId)
    {
        System.out.println("=== 开始检查学生床位 ===");
        System.out.println("接收到的用户ID: " + userId);
        System.out.println("用户ID类型: " + userId.getClass().getName());
        
        // 首先根据用户ID查询对应的学生信息
        DormStudent student = dormStudentService.selectDormStudentByUserId(userId);
        if (student == null) {
            System.out.println("未找到用户ID " + userId + " 对应的学生信息");
            AjaxResult result = AjaxResult.success();
            result.put("hasAssignedBed", false);
            result.put("message", "未找到对应的学生信息");
            return result;
        }
        
        System.out.println("找到学生信息 - 学生ID: " + student.getStuId() + ", 学生姓名: " + student.getStuName());
        
        DormBed queryBed = new DormBed();
        queryBed.setStuId(student.getStuId()); // 使用真实的学生ID
        queryBed.setBedStatus("1"); // 只查询已分配的床位
        
        System.out.println("查询条件 - 学生ID: " + queryBed.getStuId() + ", 床位状态: " + queryBed.getBedStatus());
        System.out.println("即将调用 dormBedService.selectDormBedList() 方法");
        
        List<DormBed> bedList = dormBedService.selectDormBedList(queryBed);
        
        System.out.println("查询结果 - 床位列表大小: " + (bedList != null ? bedList.size() : "null"));
        if (bedList != null && !bedList.isEmpty()) {
            DormBed bed = bedList.get(0);
            System.out.println("床位信息 - 床位ID: " + bed.getBedId() + ", 宿舍ID: " + bed.getDorId() + 
                             ", 学生ID: " + bed.getStuId() + ", 学生姓名: " + bed.getStuName() + 
                             ", 床位状态: " + bed.getBedStatus());
        }
        
        boolean hasAssignedBed = !bedList.isEmpty();
        System.out.println("是否已分配床位: " + hasAssignedBed);
        
        AjaxResult result = AjaxResult.success();
        result.put("hasAssignedBed", hasAssignedBed);
        if (hasAssignedBed) {
            result.put("bedInfo", bedList.get(0));
        }
        
        System.out.println("返回结果: " + result.toString());
        System.out.println("=== 床位检查完成 ===");
        
        return result;
    }

    /**
     * 自动分配宿舍
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:edit')")
    @Log(title = "自动分配宿舍", businessType = BusinessType.UPDATE)
    @PostMapping("/autoAllocate/{studentId}")
    public AjaxResult autoAllocate(@PathVariable("studentId") Long studentId)
    {
        // 根据学生ID获取学生信息
        DormStudent student = dormStudentService.selectDormStudentByStuId(studentId);
        if (student == null) {
            return AjaxResult.error("学生不存在");
        }
        
        // 调用自动分配服务
        AllocationResult result = dormitoryAllocationService.autoAllocateDormitory(student);
        
        if (result.isSuccess()) {
            return AjaxResult.success(result.getMessage());
        } else {
            return AjaxResult.error(result.getMessage());
        }
    }

    /**
     * 批量自动分配宿舍
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:edit')")
    @Log(title = "批量自动分配宿舍", businessType = BusinessType.UPDATE)
    @PostMapping("/batchAutoAllocate")
    public AjaxResult batchAutoAllocate(@RequestBody List<Long> studentIds)
    {
        if (studentIds == null || studentIds.isEmpty()) {
            return AjaxResult.error("学生ID列表不能为空");
        }
        
        // 根据学生ID列表获取学生信息
        List<DormStudent> students = new ArrayList<>();
        for (Long studentId : studentIds) {
            DormStudent student = dormStudentService.selectDormStudentByStuId(studentId);
            if (student != null) {
                students.add(student);
            }
        }
        
        if (students.isEmpty()) {
            return AjaxResult.error("没有找到有效的学生信息");
        }
        
        // 调用批量自动分配服务
        List<AllocationResult> results = dormitoryAllocationService.batchAutoAllocateDormitory(students);
        
        // 统计分配结果
        long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        long failureCount = results.size() - successCount;
        
        AjaxResult ajaxResult = AjaxResult.success();
        ajaxResult.put("successCount", successCount);
        ajaxResult.put("failureCount", failureCount);
        ajaxResult.put("results", results);
        ajaxResult.put("message", String.format("分配完成：成功 %d 个，失败 %d 个", successCount, failureCount));
        
        return ajaxResult;
    }

    /**
     * 获取所有未分配宿舍的学生并批量自动分配
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:edit')")
    @Log(title = "批量自动分配所有未分配学生", businessType = BusinessType.UPDATE)
    @PostMapping("/autoAllocateAll")
    public AjaxResult autoAllocateAll()
    {
        // 查询所有未分配宿舍的学生
        DormStudent queryStudent = new DormStudent();
        // 这里需要查询dorId为null的学生，但由于查询条件限制，我们先获取所有学生再过滤
        List<DormStudent> allStudents = dormStudentService.selectDormStudentList(queryStudent);
        
        // 过滤出未分配宿舍的学生
        List<DormStudent> unallocatedStudents = allStudents.stream()
                .filter(student -> student.getDorId() == null)
                .collect(Collectors.toList());
        
        if (unallocatedStudents.isEmpty()) {
            return AjaxResult.success("所有学生都已分配宿舍");
        }
        
        // 调用批量自动分配服务
        List<AllocationResult> results = dormitoryAllocationService.batchAutoAllocateDormitory(unallocatedStudents);
        
        // 统计分配结果
        long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        long failureCount = results.size() - successCount;
        
        AjaxResult ajaxResult = AjaxResult.success();
        ajaxResult.put("totalStudents", unallocatedStudents.size());
        ajaxResult.put("successCount", successCount);
        ajaxResult.put("failureCount", failureCount);
        ajaxResult.put("results", results);
        ajaxResult.put("message", String.format("批量分配完成：共 %d 个学生，成功 %d 个，失败 %d 个", 
                unallocatedStudents.size(), successCount, failureCount));
        
        return ajaxResult;
    }

    private boolean isCurrentUserStudent()
    {
        try {
            return SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "student".equals(role.getRoleName()));
        } catch (Exception ex) {
            return false;
        }
    }

    private DormStudent getCurrentStudent()
    {
        try {
            return dormStudentService.selectDormStudentByUserId(SecurityUtils.getUserId());
        } catch (Exception ex) {
            return null;
        }
    }

    private Long getCurrentStudentId()
    {
        DormStudent current = getCurrentStudent();
        return current != null ? current.getStuId() : null;
    }

    private void sanitizeBedForStudent(DormBed bed, Long currentStuId)
    {
        if (bed == null) {
            return;
        }
        if (currentStuId == null || !Objects.equals(currentStuId, bed.getStuId())) {
            bed.setStuId(null);
            bed.setStuName(null);
            bed.setDormStudent(null);
        }
    }
}
