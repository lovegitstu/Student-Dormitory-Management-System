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
import com.springboot.dorm.domain.DormRepair;
import com.springboot.dorm.service.IDormRepairService;
import com.springboot.dorm.service.IDormStudentService;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;

/**
 * 维修工单Controller
 * 
 *
 * @date 2025-09-20
 */
@RestController
@RequestMapping("/dormitory/repair")
public class DormRepairController extends BaseController
{
    @Autowired
    private IDormRepairService dormRepairService;
    
    @Autowired
    private IDormStudentService dormStudentService;

    /**
     * 查询维修工单列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:repair:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormRepair dormRepair)
    {
        startPage();
        List<DormRepair> list = dormRepairService.selectDormRepairList(dormRepair);
        return getDataTable(list);
    }

    /**
     * 导出维修工单列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:repair:export')")
    @Log(title = "维修工单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormRepair dormRepair)
    {
        List<DormRepair> list = dormRepairService.selectDormRepairList(dormRepair);
        ExcelUtil<DormRepair> util = new ExcelUtil<DormRepair>(DormRepair.class);
        util.exportExcel(response, list, "维修工单数据");
    }

    /**
     * 获取维修工单详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:repair:query')")
    @GetMapping(value = "/{repairId}")
    public AjaxResult getInfo(@PathVariable("repairId") Integer repairId)
    {
        return success(dormRepairService.selectDormRepairByRepairId(repairId));
    }

    /**
     * 新增维修工单
     */
    @PreAuthorize("@ss.hasPermi('dormitory:repair:add')")
    @Log(title = "维修工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormRepair dormRepair)
    {
        // 自动设置报修人为当前登录用户
        dormRepair.setNickName(getUsername());
        
        // 根据当前用户ID获取学生信息，自动设置宿舍楼和宿舍
        Long userId = getUserId();
        DormStudent student = dormStudentService.selectDormStudentByUserId(userId);
        if (student != null) {
            // 设置宿舍楼ID
            if (student.getfId() != null) {
                dormRepair.setfId(student.getfId());
            }
            // 设置宿舍ID
            if (student.getDorId() != null) {
                dormRepair.setDorId(student.getDorId().intValue());
            }
        }
        
        return toAjax(dormRepairService.insertDormRepair(dormRepair));
    }

    /**
     * 修改维修工单
     */
    @PreAuthorize("@ss.hasPermi('dormitory:repair:edit')")
    @Log(title = "维修工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormRepair dormRepair)
    {
        return toAjax(dormRepairService.updateDormRepair(dormRepair));
    }
    /**
     * 处理维修工单
     */
    @PreAuthorize("@ss.hasPermi('dormitory:repair:approve')")
    @Log(title = "处理维修工单", businessType = BusinessType.UPDATE)
    @PutMapping("/approve")
    public AjaxResult approve(@RequestBody DormRepair dormRepair)
    {
        // 设置处理人为当前登录用户
        dormRepair.setUpdateBy(getUsername());
        return toAjax(dormRepairService.updateDormRepair(dormRepair));
    }

    /**
     * 删除维修工单
     */
    @PreAuthorize("@ss.hasPermi('dormitory:repair:remove')")
    @Log(title = "维修工单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{repairIds}")
    public AjaxResult remove(@PathVariable Integer[] repairIds)
    {
        return toAjax(dormRepairService.deleteDormRepairByRepairIds(repairIds));
    }
}
