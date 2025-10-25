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
import com.springboot.dorm.domain.DormTurnOut;
import com.springboot.dorm.service.IDormTurnOutService;
import com.springboot.dorm.service.IDormStudentService;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;

/**
 * 离校登记Controller
 * 
 *
 * @date 2025-09-26
 */
@RestController
@RequestMapping("/dormitory/out")
public class DormTurnOutController extends BaseController
{
    @Autowired
    private IDormTurnOutService dormTurnOutService;

    @Autowired
    private IDormStudentService dormStudentService;

    /**
     * 查询离校登记列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:out:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormTurnOut dormTurnOut)
    {
        startPage();
        List<DormTurnOut> list = dormTurnOutService.selectDormTurnOutList(dormTurnOut);
        return getDataTable(list);
    }

    /**
     * 导出离校登记列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:out:export')")
    @Log(title = "离校登记", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormTurnOut dormTurnOut)
    {
        List<DormTurnOut> list = dormTurnOutService.selectDormTurnOutList(dormTurnOut);
        ExcelUtil<DormTurnOut> util = new ExcelUtil<DormTurnOut>(DormTurnOut.class);
        util.exportExcel(response, list, "离校登记数据");
    }

    /**
     * 获取离校登记详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:out:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dormTurnOutService.selectDormTurnOutById(id));
    }

    /**
     * 新增离校登记
     */
    @PreAuthorize("@ss.hasPermi('dormitory:out:add')")
    @Log(title = "离校登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormTurnOut dormTurnOut)
    {
        // 根据当前用户ID获取学生信息并设置stuId
        Long currentUserId = getUserId();
        DormStudent student = dormStudentService.selectDormStudentByUserId(currentUserId);
        if (student != null) {
            dormTurnOut.setStuId(student.getStuId());
        }
        return toAjax(dormTurnOutService.insertDormTurnOut(dormTurnOut));
    }

    /**
     * 修改离校登记
     */
    @PreAuthorize("@ss.hasPermi('dormitory:out:edit')")
    @Log(title = "离校登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormTurnOut dormTurnOut)
    {
        return toAjax(dormTurnOutService.updateDormTurnOut(dormTurnOut));
    }

    /**
     * 删除离校登记
     */
    @PreAuthorize("@ss.hasPermi('dormitory:out:remove')")
    @Log(title = "离校登记", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dormTurnOutService.deleteDormTurnOutByIds(ids));
    }

    /**
     * 审批离校申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:out:approve')")
    @Log(title = "离校申请审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve")
    public AjaxResult approve(@RequestBody DormTurnOut dormTurnOut)
    {
        // 只更新审批状态
        DormTurnOut updateEntity = new DormTurnOut();
        updateEntity.setId(dormTurnOut.getId());
        updateEntity.setApprovalStatus(dormTurnOut.getApprovalStatus());
        return toAjax(dormTurnOutService.updateDormTurnOut(updateEntity));
    }

    /**
     * 确认学生返校
     */
    @PreAuthorize("@ss.hasPermi('dormitory:out:confirmReturn')")
    @Log(title = "学生返校确认", businessType = BusinessType.UPDATE)
    @PutMapping("/confirmReturn")
    public AjaxResult confirmReturn(@RequestBody DormTurnOut dormTurnOut)
    {
        // 更新审批状态为已完成（学生已返校）
        DormTurnOut updateEntity = new DormTurnOut();
        updateEntity.setId(dormTurnOut.getId());
        updateEntity.setApprovalStatus("3"); // 3表示已完成（学生已返校）
        return toAjax(dormTurnOutService.updateDormTurnOut(updateEntity));
    }
}
