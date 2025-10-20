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
import com.springboot.dorm.domain.DormExchange;
import com.springboot.dorm.service.IDormExchangeService;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;
import com.springboot.common.utils.SecurityUtils;

/**
 * 换宿申请Controller
 * 
 * 
 * @date 2025-09-27
 */
@RestController
@RequestMapping("/dormitory/exchange")
public class DormExchangeController extends BaseController
{
    @Autowired
    private IDormExchangeService dormExchangeService;

    /**
     * 查询换宿申请列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:exchange:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormExchange dormExchange)
    {
        System.out.println("=== 换宿申请列表查询权限调试 ===");
        System.out.println("当前用户: " + SecurityUtils.getUsername());
        System.out.println("当前用户角色: " + SecurityUtils.getLoginUser().getUser().getRoles());
        System.out.println("当前用户权限: " + SecurityUtils.getLoginUser().getPermissions());
        System.out.println("=== 权限调试结束 ===");
        
        startPage();
        List<DormExchange> list = dormExchangeService.selectDormExchangeList(dormExchange);
        return getDataTable(list);
    }

    /**
     * 导出换宿申请列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:exchange:export')")
    @Log(title = "换宿申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormExchange dormExchange)
    {
        List<DormExchange> list = dormExchangeService.selectDormExchangeList(dormExchange);
        ExcelUtil<DormExchange> util = new ExcelUtil<DormExchange>(DormExchange.class);
        util.exportExcel(response, list, "换宿申请数据");
    }

    /**
     * 获取换宿申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:exchange:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dormExchangeService.selectDormExchangeById(id));
    }

    /**
     * 新增换宿申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:exchange:add')")
    @Log(title = "换宿申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormExchange dormExchange)
    {
        System.out.println("=== 新增换宿申请权限调试 ===");
        System.out.println("当前用户: " + SecurityUtils.getUsername());
        System.out.println("当前用户ID: " + SecurityUtils.getUserId());
        System.out.println("当前用户角色: " + SecurityUtils.getLoginUser().getUser().getRoles());
        System.out.println("当前用户权限: " + SecurityUtils.getLoginUser().getPermissions());
        System.out.println("是否有dormitory:exchange:add权限: " + SecurityUtils.getLoginUser().getPermissions().contains("dormitory:exchange:add"));
        System.out.println("请求数据: " + dormExchange.toString());
        System.out.println("=== 权限调试结束 ===");
        
        try {
            int result = dormExchangeService.insertDormExchange(dormExchange);
            System.out.println("控制器处理结果: " + (result > 0 ? "成功" : "失败"));
            return toAjax(result);
        } catch (Exception e) {
            System.out.println("控制器处理异常: " + e.getMessage());
            e.printStackTrace();
            return AjaxResult.error("新增换宿申请失败: " + e.getMessage());
        }
    }

    /**
     * 修改换宿申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:exchange:edit')")
    @Log(title = "换宿申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormExchange dormExchange)
    {
        System.out.println("=== 修改换宿申请权限调试 ===");
        System.out.println("当前用户: " + SecurityUtils.getUsername());
        System.out.println("当前用户角色: " + SecurityUtils.getLoginUser().getUser().getRoles());
        System.out.println("当前用户权限: " + SecurityUtils.getLoginUser().getPermissions());
        System.out.println("是否有dormitory:exchange:edit权限: " + SecurityUtils.getLoginUser().getPermissions().contains("dormitory:exchange:edit"));
        System.out.println("=== 权限调试结束 ===");
        
        return toAjax(dormExchangeService.updateDormExchange(dormExchange));
    }

    /**
     * 删除换宿申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:exchange:remove')")
    @Log(title = "换宿申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        System.out.println("=== 删除换宿申请权限调试 ===");
        System.out.println("当前用户: " + SecurityUtils.getUsername());
        System.out.println("当前用户角色: " + SecurityUtils.getLoginUser().getUser().getRoles());
        System.out.println("当前用户权限: " + SecurityUtils.getLoginUser().getPermissions());
        System.out.println("是否有dormitory:exchange:remove权限: " + SecurityUtils.getLoginUser().getPermissions().contains("dormitory:exchange:remove"));
        System.out.println("=== 权限调试结束 ===");
        
        return toAjax(dormExchangeService.deleteDormExchangeByIds(ids));
    }

    /**
     * 审批换宿申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:exchange:approve')")
    @Log(title = "换宿申请审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{id}")
    public AjaxResult approve(@PathVariable("id") Long id, @RequestBody DormExchange dormExchange)
    {
        dormExchange.setId(id);
        return toAjax(dormExchangeService.approveDormExchange(dormExchange));
    }

    /**
     * 批量审批换宿申请
     */
    @PreAuthorize("@ss.hasPermi('dormitory:exchange:approve')")
    @Log(title = "换宿申请批量审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/batch")
    public AjaxResult batchApprove(@RequestBody List<DormExchange> dormExchanges)
    {
        return toAjax(dormExchangeService.batchApproveDormExchange(dormExchanges));
    }
}
