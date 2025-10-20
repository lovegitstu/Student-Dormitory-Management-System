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
import com.springboot.dorm.domain.DormDormitory;
import com.springboot.dorm.service.IDormDormitoryService;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;

/**
 * 宿舍信息Controller
 * 
 * 
 * @date 2025-09-16
 */
@RestController
@RequestMapping("/dormitory/dorm")
public class DormDormitoryController extends BaseController
{
    @Autowired
    private IDormDormitoryService dormDormitoryService;

    /**
     * 查询宿舍信息列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:dorm:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormDormitory dormDormitory)
    {
        System.out.println("=== 宿舍信息查询开始 ===");
        System.out.println("查询参数: " + dormDormitory);
        System.out.println("fId参数: " + dormDormitory.getfId());
        
        startPage();
        List<DormDormitory> list = dormDormitoryService.selectDormDormitoryList(dormDormitory);
        
        System.out.println("查询结果数量: " + (list != null ? list.size() : 0));
        if (list != null && !list.isEmpty()) {
            System.out.println("第一条记录: " + list.get(0));
        }
        
        TableDataInfo result = getDataTable(list);
        System.out.println("返回数据结构: " + result);
        System.out.println("=== 宿舍信息查询结束 ===");
        
        return result;
    }

    /**
     * 导出宿舍信息列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:dorm:export')")
    @Log(title = "宿舍信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormDormitory dormDormitory)
    {
        List<DormDormitory> list = dormDormitoryService.selectDormDormitoryList(dormDormitory);
        ExcelUtil<DormDormitory> util = new ExcelUtil<DormDormitory>(DormDormitory.class);
        util.exportExcel(response, list, "宿舍信息数据");
    }

    /**
     * 获取宿舍信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:dorm:query')")
    @GetMapping(value = "/{dorId}")
    public AjaxResult getInfo(@PathVariable("dorId") Long dorId)
    {
        return success(dormDormitoryService.selectDormDormitoryByDorId(dorId));
    }

    /**
     * 新增宿舍信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:dorm:add')")
    @Log(title = "宿舍信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormDormitory dormDormitory)
    {
        return toAjax(dormDormitoryService.insertDormDormitory(dormDormitory));
    }

    /**
     * 修改宿舍信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:dorm:edit')")
    @Log(title = "宿舍信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormDormitory dormDormitory)
    {
        return toAjax(dormDormitoryService.updateDormDormitory(dormDormitory));
    }

    /**
     * 删除宿舍信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:dorm:remove')")
    @Log(title = "宿舍信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{dorIds}")
    public AjaxResult remove(@PathVariable Long[] dorIds)
    {
        return toAjax(dormDormitoryService.deleteDormDormitoryByDorIds(dorIds));
    }
}
