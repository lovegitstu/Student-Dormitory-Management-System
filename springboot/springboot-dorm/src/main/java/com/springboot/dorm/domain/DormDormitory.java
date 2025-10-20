package com.springboot.dorm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 宿舍信息对象 dorm_dormitory
 * 
 * 
 * @date 2025-09-16
 */
public class DormDormitory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 宿舍ID */
    private Long dorId;

    /** 宿舍号 */
    @Excel(name = "宿舍号")
    private String dorName;

    /** 宿舍照片 */
    @Excel(name = "宿舍照片")
    private String dorPicture;

    /** 宿舍楼 */
    @Excel(name = "宿舍楼")
    private Long fId;

    /** 宿舍类型 */
    @Excel(name = "宿舍类型")
    private Integer dorType;

    /** 宿舍状态 */
    @Excel(name = "宿舍状态")
    private Integer dorStatus;

    private DormFloor dormFloor;

    public DormFloor getDormFloor() {
        return dormFloor;
    }

    public void setDormFloor(DormFloor dormFloor) {
        this.dormFloor = dormFloor;
    }

    public void setDorId(Long dorId)
    {
        this.dorId = dorId;
    }

    public Long getDorId() 
    {
        return dorId;
    }
    public void setDorName(String dorName) 
    {
        this.dorName = dorName;
    }

    public String getDorName() 
    {
        return dorName;
    }
    public void setDorPicture(String dorPicture) 
    {
        this.dorPicture = dorPicture;
    }

    public String getDorPicture() 
    {
        return dorPicture;
    }
    public void setfId(Long fId) 
    {
        this.fId = fId;
    }

    public Long getfId() 
    {
        return fId;
    }
    public void setDorType(Integer dorType) 
    {
        this.dorType = dorType;
    }

    public Integer getDorType() 
    {
        return dorType;
    }
    public void setDorStatus(Integer dorStatus) 
    {
        this.dorStatus = dorStatus;
    }

    public Integer getDorStatus() 
    {
        return dorStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("dorId", getDorId())
            .append("dorName", getDorName())
            .append("dorPicture", getDorPicture())
            .append("fId", getfId())
            .append("dorType", getDorType())
            .append("dorStatus", getDorStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("dormFloor",getDormFloor())
            .toString();
    }
}
