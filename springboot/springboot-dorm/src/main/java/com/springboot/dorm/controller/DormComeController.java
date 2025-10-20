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
import com.springboot.dorm.domain.DormCome;
import com.springboot.dorm.service.IDormComeService;
import com.springboot.dorm.service.IDormStudentService;
import com.springboot.dorm.domain.DormStudent;
import com.springboot.common.core.page.TableDataInfo;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;

/**
 * 回校申请Controller
 * 
 * 
 * @date 2025-09-25
 */
@RestController
@RequestMapping("/dormitory/come")
public class DormComeController extends BaseController
{
    @Autowired
    private IDormComeService dormComeService;

    @Autowired
    private IDormStudentService dormStudentService;

    /**
     * 查询回校申请列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:come:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormCome dormCome)
    {
        startPage();
        List<DormCome> list = dormComeService.selectDormComeList(dormCome);
        return getDataTable(list);
    }

    /**
     * 导出回校申请列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:come:export')")
    @Log(title = "回校申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormCome dormCome)
    {
        List<DormCome> list = dormComeService.selectDormComeList(dormCome);
        ExcelUtil<DormCome> util = new ExcelUtil<DormCome>(DormCome.class);
        util.exportExcel(response, list, "回校申请数据");
    }

    /**
     * 获取回校申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:come:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dormComeService.selectDormComeById(id));
    }

    /**
     * 新增回校申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:come:add')")
    @Log(title = "回校申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormCome dormCome)
    {
        // 根据当前用户ID获取学生信息，自动设置stuId
        Long userId = getUserId();
        DormStudent student = dormStudentService.selectDormStudentByUserId(userId);
        if (student != null) {
            dormCome.setStuId(student.getStuId());
        }
        
        return toAjax(dormComeService.insertDormCome(dormCome));
    }

    /**
     * 修改回校申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:come:edit')")
    @Log(title = "回校申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormCome dormCome)
    {
        return toAjax(dormComeService.updateDormCome(dormCome));
    }

    /**
     * 删除回校申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:come:remove')")
    @Log(title = "回校申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dormComeService.deleteDormComeByIds(ids));
    }

    /**
     * 审批回校申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:come:approve')")
    @Log(title = "回校申请审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve")
    public AjaxResult approve(@RequestBody DormCome dormCome)
    {
        // 只更新审批状态
        DormCome updateEntity = new DormCome();
        updateEntity.setId(dormCome.getId());
        updateEntity.setOpinion(dormCome.getOpinion());
        return toAjax(dormComeService.updateDormCome(updateEntity));
    }
}
