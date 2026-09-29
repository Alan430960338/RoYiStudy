package com.ruoyi.system.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.RecycleCategory;
import com.ruoyi.system.service.IRecycleCategoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 回收品类Controller
 * 
 * @author ruoyi
 * @date 2026-09-29
 */
@RestController
@RequestMapping("/system/category")
public class RecycleCategoryController extends BaseController
{
    @Autowired
    private IRecycleCategoryService recycleCategoryService;

    /**
     * 查询回收品类列表
     */
    @PreAuthorize("@ss.hasPermi('system:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(RecycleCategory recycleCategory)
    {
        startPage();
        List<RecycleCategory> list = recycleCategoryService.selectRecycleCategoryList(recycleCategory);
        return getDataTable(list);
    }

    /**
     * 导出回收品类列表
     */
    @PreAuthorize("@ss.hasPermi('system:category:export')")
    @Log(title = "回收品类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RecycleCategory recycleCategory)
    {
        List<RecycleCategory> list = recycleCategoryService.selectRecycleCategoryList(recycleCategory);
        ExcelUtil<RecycleCategory> util = new ExcelUtil<RecycleCategory>(RecycleCategory.class);
        util.exportExcel(response, list, "回收品类数据");
    }

    /**
     * 获取回收品类详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:category:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(recycleCategoryService.selectRecycleCategoryById(id));
    }

    /**
     * 新增回收品类
     */
    @PreAuthorize("@ss.hasPermi('system:category:add')")
    @Log(title = "回收品类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RecycleCategory recycleCategory)
    {
        return toAjax(recycleCategoryService.insertRecycleCategory(recycleCategory));
    }

    /**
     * 修改回收品类
     */
    @PreAuthorize("@ss.hasPermi('system:category:edit')")
    @Log(title = "回收品类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RecycleCategory recycleCategory)
    {
        return toAjax(recycleCategoryService.updateRecycleCategory(recycleCategory));
    }

    /**
     * 删除回收品类
     */
    @PreAuthorize("@ss.hasPermi('system:category:remove')")
    @Log(title = "回收品类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(recycleCategoryService.deleteRecycleCategoryByIds(ids));
    }
}
