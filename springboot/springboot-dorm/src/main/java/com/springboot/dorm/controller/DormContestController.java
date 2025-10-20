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
import com.springboot.dorm.domain.DormContest;
import com.springboot.dorm.service.IDormContestService;
import com.springboot.common.utils.poi.ExcelUtil;
import com.springboot.common.core.page.TableDataInfo;

/**
 * 宿舍评分Controller
 * 
 *
 * @date 2025-09-18
 */
@RestController
@RequestMapping("/dormitory/contest")
public class DormContestController extends BaseController
{
    @Autowired
    private IDormContestService dormContestService;

    /**
     * 查询宿舍评分列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:contest:list')")
    @GetMapping("/list")
    public TableDataInfo list(DormContest dormContest)
    {
        startPage();
        List<DormContest> list = dormContestService.selectDormContestList(dormContest);
        return getDataTable(list);
    }

    /**
     * 导出宿舍评分列表
     */
    @PreAuthorize("@ss.hasPermi('dormitory:contest:export')")
    @Log(title = "宿舍评分", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DormContest dormContest)
    {
        List<DormContest> list = dormContestService.selectDormContestList(dormContest);
        ExcelUtil<DormContest> util = new ExcelUtil<DormContest>(DormContest.class);
        util.exportExcel(response, list, "宿舍评分数据");
    }

    /**
     * 获取宿舍评分详细信息
     */
    @PreAuthorize("@ss.hasPermi('dormitory:contest:query')")
    @GetMapping(value = "/{conId}")
    public AjaxResult getInfo(@PathVariable("conId") Integer conId)
    {
        return success(dormContestService.selectDormContestByConId(conId));
    }

    /**
     * 新增宿舍评分
     */
    @PreAuthorize("@ss.hasPermi('dormitory:contest:add')")
    @Log(title = "宿舍评分", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DormContest dormContest)
    {
        // 自动设置评分人为当前登录用户
        dormContest.setConUser(getUsername());
        return toAjax(dormContestService.insertDormContest(dormContest));
    }

    /**
     * 修改宿舍评分
     */
    @PreAuthorize("@ss.hasPermi('dormitory:contest:edit')")
    @Log(title = "宿舍评分", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DormContest dormContest)
    {
        // 自动设置评分人为当前登录用户
        dormContest.setConUser(getUsername());
        return toAjax(dormContestService.updateDormContest(dormContest));
    }

    /**
     * 删除宿舍评分
     */
    @PreAuthorize("@ss.hasPermi('dormitory:contest:remove')")
    @Log(title = "宿舍评分", businessType = BusinessType.DELETE)
	@DeleteMapping("/{conIds}")
    public AjaxResult remove(@PathVariable Integer[] conIds)
    {
        return toAjax(dormContestService.deleteDormContestByConIds(conIds));
    }
}
