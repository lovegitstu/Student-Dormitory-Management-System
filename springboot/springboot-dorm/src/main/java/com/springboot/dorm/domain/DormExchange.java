package com.springboot.dorm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 换宿申请对象 dorm_exchange
 * 
 *
 * @date 2025-09-25
 */
public class DormExchange extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 新宿舍楼 */
    @Excel(name = "新宿舍楼")
    private Long fId;

    /** 新宿舍号 */
    @Excel(name = "新宿舍号")
    private Long dorId;

    /** 床位ID */
    @Excel(name = "新床位ID")
    private Long bedId;

    /** 原床位ID */
    @Excel(name = "原床位ID")
    private Long originalBedId;

    /** 原宿舍 */
    @Excel(name = "原宿舍")
    private String dormName;

    /** 学生id */
    private Long stuId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String stuName;

    /** 辅导员意见 */
    @Excel(name = "辅导员意见")
    private Long opinion;

    /** 原因 */
    @Excel(name = "原因")
    private String excuse;

    /** 男女生宿舍 */
    @Excel(name = "男女生宿舍")
    private Long dorType;

    private DormFloor dormFloor;

    public DormFloor getDormFloor() {
        return dormFloor;
    }

    public void setDormFloor(DormFloor dormFloor) {
        this.dormFloor = dormFloor;
    }

    public DormDormitory getDormDormitory() {
        return dormDormitory;
    }

    public void setDormDormitory(DormDormitory dormDormitory) {
        this.dormDormitory = dormDormitory;
    }

    private DormDormitory dormDormitory;


    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setfId(Long fId) 
    {
        this.fId = fId;
    }

    public Long getfId() 
    {
        return fId;
    }
    public void setDorId(Long dorId) 
    {
        this.dorId = dorId;
    }

    public Long getDorId() 
    {
        return dorId;
    }
    public void setBedId(Long bedId) 
    {
        this.bedId = bedId;
    }

    public Long getBedId() 
    {
        return bedId;
    }
    public void setOriginalBedId(Long originalBedId) 
    {
        this.originalBedId = originalBedId;
    }

    public Long getOriginalBedId() 
    {
        return originalBedId;
    }
    public void setDormName(String dormName)
    {
        this.dormName = dormName;
    }

    public String getDormName()
    {
        return dormName;
    }
    public void setStuId(Long stuId) 
    {
        this.stuId = stuId;
    }

    public Long getStuId() 
    {
        return stuId;
    }
    public void setStuName(String stuName) 
    {
        this.stuName = stuName;
    }

    public String getStuName() 
    {
        return stuName;
    }
    public void setOpinion(Long opinion) 
    {
        this.opinion = opinion;
    }

    public Long getOpinion() 
    {
        return opinion;
    }
    public void setExcuse(String excuse) 
    {
        this.excuse = excuse;
    }

    public String getExcuse() 
    {
        return excuse;
    }
    public void setDorType(Long dorType) 
    {
        this.dorType = dorType;
    }

    public Long getDorType() 
    {
        return dorType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("fId", getfId())
            .append("dorId", getDorId())
            .append("bedId", getBedId())
            .append("originalBedId", getOriginalBedId())
            .append("dormName", getDormName())
            .append("stuId", getStuId())
            .append("stuName", getStuName())
            .append("opinion", getOpinion())
            .append("excuse", getExcuse())
            .append("dorType", getDorType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("dormFloor",getDormFloor())
            .append("dormDormitory",getDormDormitory())
            .toString();
    }
}
