package com.springboot.dorm.controller;

import java.util.List;
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
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;

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

    /**
     * 查询床位管理列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:bed:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormBed dormBed)
    {
        startPage();
        List<DormBed> list = dormBedService.selectDormBedList(dormBed);
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
        return success(dormBedService.selectDormBedByBedId(bedId));
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
}
