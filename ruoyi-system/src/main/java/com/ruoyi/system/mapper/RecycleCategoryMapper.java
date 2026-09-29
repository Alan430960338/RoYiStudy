package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.RecycleCategory;

/**
 * 回收品类Mapper接口
 * 
 * @author ruoyi
 * @date 2026-09-29
 */
public interface RecycleCategoryMapper 
{
    /**
     * 查询回收品类
     * 
     * @param id 回收品类主键
     * @return 回收品类
     */
    public RecycleCategory selectRecycleCategoryById(Long id);

    /**
     * 查询回收品类列表
     * 
     * @param recycleCategory 回收品类
     * @return 回收品类集合
     */
    public List<RecycleCategory> selectRecycleCategoryList(RecycleCategory recycleCategory);

    /**
     * 新增回收品类
     * 
     * @param recycleCategory 回收品类
     * @return 结果
     */
    public int insertRecycleCategory(RecycleCategory recycleCategory);

    /**
     * 修改回收品类
     * 
     * @param recycleCategory 回收品类
     * @return 结果
     */
    public int updateRecycleCategory(RecycleCategory recycleCategory);

    /**
     * 删除回收品类
     * 
     * @param id 回收品类主键
     * @return 结果
     */
    public int deleteRecycleCategoryById(Long id);

    /**
     * 批量删除回收品类
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRecycleCategoryByIds(Long[] ids);
}
