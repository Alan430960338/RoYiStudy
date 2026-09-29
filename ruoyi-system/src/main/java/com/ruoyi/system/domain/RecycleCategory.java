package com.ruoyi.system.domain;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 回收品类对象 recycle_category
 * 
 * @author ruoyi
 * @date 2026-09-29
 */
public class RecycleCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 回收品类名称 */
    @Excel(name = "回收品类名称")
    private String name;

    /** 回收品类图片列表 */
    @Excel(name = "回收品类图片列表")
    private String imageFiles;

    /** 回收品类价格最小值 */
    @Excel(name = "回收品类价格最小值")
    private BigDecimal priceMin;

    /** 回收品类价格最大值 */
    @Excel(name = "回收品类价格最大值")
    private BigDecimal priceMax;

    /** 用友专属商品 code */
    @Excel(name = "用友专属商品 code")
    private String yonyouCategoryId;

    /** 好业财商品ID */
    @Excel(name = "好业财商品ID")
    private String yonyouProductId;

    /** 创建人 */
    @Excel(name = "创建人")
    private Long createUser;

    /** 修改人 */
    @Excel(name = "修改人")
    private Long updateUser;

    /** 是否已删除（0：否；id：是） */
    @Excel(name = "是否已删除", readConverterExp = "0=：否；id：是")
    private Long deleted;

    /** 租户ID */
    @Excel(name = "租户ID")
    private Long tenantId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setImageFiles(String imageFiles) 
    {
        this.imageFiles = imageFiles;
    }

    public String getImageFiles() 
    {
        return imageFiles;
    }

    public void setPriceMin(BigDecimal priceMin) 
    {
        this.priceMin = priceMin;
    }

    public BigDecimal getPriceMin() 
    {
        return priceMin;
    }

    public void setPriceMax(BigDecimal priceMax) 
    {
        this.priceMax = priceMax;
    }

    public BigDecimal getPriceMax() 
    {
        return priceMax;
    }

    public void setYonyouCategoryId(String yonyouCategoryId) 
    {
        this.yonyouCategoryId = yonyouCategoryId;
    }

    public String getYonyouCategoryId() 
    {
        return yonyouCategoryId;
    }

    public void setYonyouProductId(String yonyouProductId) 
    {
        this.yonyouProductId = yonyouProductId;
    }

    public String getYonyouProductId() 
    {
        return yonyouProductId;
    }

    public void setCreateUser(Long createUser) 
    {
        this.createUser = createUser;
    }

    public Long getCreateUser() 
    {
        return createUser;
    }

    public void setUpdateUser(Long updateUser) 
    {
        this.updateUser = updateUser;
    }

    public Long getUpdateUser() 
    {
        return updateUser;
    }

    public void setDeleted(Long deleted) 
    {
        this.deleted = deleted;
    }

    public Long getDeleted() 
    {
        return deleted;
    }

    public void setTenantId(Long tenantId) 
    {
        this.tenantId = tenantId;
    }

    public Long getTenantId() 
    {
        return tenantId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("imageFiles", getImageFiles())
            .append("priceMin", getPriceMin())
            .append("priceMax", getPriceMax())
            .append("yonyouCategoryId", getYonyouCategoryId())
            .append("yonyouProductId", getYonyouProductId())
            .append("remark", getRemark())
            .append("createUser", getCreateUser())
            .append("createTime", getCreateTime())
            .append("updateUser", getUpdateUser())
            .append("updateTime", getUpdateTime())
            .append("deleted", getDeleted())
            .append("tenantId", getTenantId())
            .toString();
    }
}
