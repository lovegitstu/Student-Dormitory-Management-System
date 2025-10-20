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
import com.springboot.dorm.domain.DormFloor;
import com.springboot.dorm.service.IDormFloorService;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;

/**
 * 宿舍楼管理Controller
 * 
 *
 * @date 2025-09-16
 */
@RestController
@RequestMapping("/dormitory/floor")
public class DormFloorController extends BaseController
{
    @Autowired
    private IDormFloorService dormFloorService;

    /**
     * 查询宿舍楼管理列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:floor:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormFloor dormFloor)
    {
        startPage();
        List<DormFloor> list = dormFloorService.selectDormFloorList(dormFloor);
        return getDataTable(list);
    }

    /**
     * 导出宿舍楼管理列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:floor:export')")
    @Log(title = "宿舍楼管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormFloor dormFloor)
    {
        List<DormFloor> list = dormFloorService.selectDormFloorList(dormFloor);
        ExcelUtil<DormFloor> util = new ExcelUtil<DormFloor>(DormFloor.class);
        util.exportExcel(response, list, "宿舍楼管理数据");
    }

    /**
     * 获取宿舍楼管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:floor:query')")
    @GetMapping(value = "/{fId}")
    public AjaxResult getInfo(@PathVariable("fId") Long fId)
    {
        return success(dormFloorService.selectDormFloorByFId(fId));
    }

    /**
     * 新增宿舍楼管理
     */
    @PreAuthorize("@ss.hasPermi('dormitory:floor:add')")
    @Log(title = "宿舍楼管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormFloor dormFloor)
    {
        return toAjax(dormFloorService.insertDormFloor(dormFloor));
    }

    /**
     * 修改宿舍楼管理
     */
    @PreAuthorize("@ss.hasPermi('dormitory:floor:edit')")
    @Log(title = "宿舍楼管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormFloor dormFloor)
    {
        return toAjax(dormFloorService.updateDormFloor(dormFloor));
    }

    /**
     * 删除宿舍楼管理
     */
    @PreAuthorize("@ss.hasPermi('dormitory:floor:remove')")
    @Log(title = "宿舍楼管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{fIds}")
    public AjaxResult remove(@PathVariable Long[] fIds)
    {
        return toAjax(dormFloorService.deleteDormFloorByFIds(fIds));
    }
}
