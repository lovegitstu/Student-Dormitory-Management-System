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
import com.springboot.dorm.domain.DormVisit;
import com.springboot.dorm.service.IDormVisitService;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;

/**
 * 来访人员登记Controller
 * 
 * 
 * @date 2025-09-20
 */
@RestController
@RequestMapping("/dormitory/visit")
public class DormVisitController extends BaseController
{
    @Autowired
    private IDormVisitService dormVisitService;

    /**
     * 查询来访人员登记列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:visit:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormVisit dormVisit)
    {
        startPage();
        List<DormVisit> list = dormVisitService.selectDormVisitList(dormVisit);
        return getDataTable(list);
    }

    /**
     * 导出来访人员登记列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:visit:export')")
    @Log(title = "来访人员登记", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormVisit dormVisit)
    {
        List<DormVisit> list = dormVisitService.selectDormVisitList(dormVisit);
        ExcelUtil<DormVisit> util = new ExcelUtil<DormVisit>(DormVisit.class);
        util.exportExcel(response, list, "来访人员登记数据");
    }

    /**
     * 获取来访人员登记详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:visit:query')")
    @GetMapping(value = "/{visId}")
    public AjaxResult getInfo(@PathVariable("visId") Long visId)
    {
        return success(dormVisitService.selectDormVisitByVisId(visId));
    }

    /**
     * 新增来访人员登记
     */
    @PreAuthorize("@ss.hasPermi('dormitory:visit:add')")
    @Log(title = "来访人员登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormVisit dormVisit)
    {
        return toAjax(dormVisitService.insertDormVisit(dormVisit));
    }

    /**
     * 修改来访人员登记
     */
    @PreAuthorize("@ss.hasPermi('dormitory:visit:edit')")
    @Log(title = "来访人员登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormVisit dormVisit)
    {
        return toAjax(dormVisitService.updateDormVisit(dormVisit));
    }

    /**
     * 删除来访人员登记
     */
    @PreAuthorize("@ss.hasPermi('dormitory:visit:remove')")
    @Log(title = "来访人员登记", businessType = BusinessType.DELETE)
	@DeleteMapping("/{visIds}")
    public AjaxResult remove(@PathVariable Long[] visIds)
    {
        return toAjax(dormVisitService.deleteDormVisitByVisIds(visIds));
    }
}
