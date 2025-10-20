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
import com.springboot.dorm.domain.DormKeepBack;
import com.springboot.dorm.service.IDormKeepBackService;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;

/**
 * 留宿申请Controller
 * 
 *
 * @date 2025-09-26
 */
@RestController
@RequestMapping("/dormitory/leave")
public class DormKeepBackController extends BaseController
{
    @Autowired
    private IDormKeepBackService dormKeepBackService;

    /**
     * 查询留宿申请列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:leave:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormKeepBack dormKeepBack)
    {
        startPage();
        List<DormKeepBack> list = dormKeepBackService.selectDormKeepBackList(dormKeepBack);
        return getDataTable(list);
    }

    /**
     * 导出留宿申请列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:leave:export')")
    @Log(title = "留宿申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormKeepBack dormKeepBack)
    {
        List<DormKeepBack> list = dormKeepBackService.selectDormKeepBackList(dormKeepBack);
        ExcelUtil<DormKeepBack> util = new ExcelUtil<DormKeepBack>(DormKeepBack.class);
        util.exportExcel(response, list, "留宿申请数据");
    }

    /**
     * 获取留宿申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:leave:query')")
    @GetMapping(value = "/{kbId}")
    public AjaxResult getInfo(@PathVariable("kbId") Integer kbId)
    {
        return success(dormKeepBackService.selectDormKeepBackByKbId(kbId));
    }

    /**
     * 新增留宿申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:leave:add')")
    @Log(title = "留宿申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormKeepBack dormKeepBack)
    {
        return toAjax(dormKeepBackService.insertDormKeepBack(dormKeepBack));
    }

    /**
     * 修改留宿申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:leave:edit')")
    @Log(title = "留宿申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormKeepBack dormKeepBack)
    {
        return toAjax(dormKeepBackService.updateDormKeepBack(dormKeepBack));
    }

    /**
     * 删除留宿申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:leave:remove')")
    @Log(title = "留宿申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{kbIds}")
    public AjaxResult remove(@PathVariable Integer[] kbIds)
    {
        return toAjax(dormKeepBackService.deleteDormKeepBackByKbIds(kbIds));
    }
}
