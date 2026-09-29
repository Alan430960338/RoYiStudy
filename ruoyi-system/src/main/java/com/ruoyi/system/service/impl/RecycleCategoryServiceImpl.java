package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.RecycleCategoryMapper;
import com.ruoyi.system.domain.RecycleCategory;
import com.ruoyi.system.service.IRecycleCategoryService;

/**
 * 回收品类Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-09-29
 */
@Service
public class RecycleCategoryServiceImpl implements IRecycleCategoryService 
{
    @Autowired
    private RecycleCategoryMapper recycleCategoryMapper;

    /**
     * 查询回收品类
     * 
     * @param id 回收品类主键
     * @return 回收品类
     */
    @Override
    public RecycleCategory selectRecycleCategoryById(Long id)
    {
        return recycleCategoryMapper.selectRecycleCategoryById(id);
    }

    /**
     * 查询回收品类列表
     * 
     * @param recycleCategory 回收品类
     * @return 回收品类
     */
    @Override
    public List<RecycleCategory> selectRecycleCategoryList(RecycleCategory recycleCategory)
    {
        return recycleCategoryMapper.selectRecycleCategoryList(recycleCategory);
    }

    /**
     * 新增回收品类
     * 
     * @param recycleCategory 回收品类
     * @return 结果
     */
    @Override
    public int insertRecycleCategory(RecycleCategory recycleCategory)
    {
        recycleCategory.setCreateTime(DateUtils.getNowDate());
        return recycleCategoryMapper.insertRecycleCategory(recycleCategory);
    }

    /**
     * 修改回收品类
     * 
     * @param recycleCategory 回收品类
     * @return 结果
     */
    @Override
    public int updateRecycleCategory(RecycleCategory recycleCategory)
    {
        recycleCategory.setUpdateTime(DateUtils.getNowDate());
        return recycleCategoryMapper.updateRecycleCategory(recycleCategory);
    }

    /**
     * 批量删除回收品类
     * 
     * @param ids 需要删除的回收品类主键
     * @return 结果
     */
    @Override
    public int deleteRecycleCategoryByIds(Long[] ids)
    {
        return recycleCategoryMapper.deleteRecycleCategoryByIds(ids);
    }

    /**
     * 删除回收品类信息
     * 
     * @param id 回收品类主键
     * @return 结果
     */
    @Override
    public int deleteRecycleCategoryById(Long id)
    {
        return recycleCategoryMapper.deleteRecycleCategoryById(id);
    }
}
