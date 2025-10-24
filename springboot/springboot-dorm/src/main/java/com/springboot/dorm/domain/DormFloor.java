package com.springboot.dorm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 宿舍楼管理对象 dorm_floor
 * 
 * 
 * @date 2025-09-16
 */
public class DormFloor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 宿舍楼ID */
    private Long fId;

    /** 宿舍楼名 */
    @Excel(name = "宿舍楼名")
    private String fName;

    /** 楼层数 */
    @Excel(name = "楼层数")
    private Long fNumber;

    /** 宿舍楼类型 */
    @Excel(name = "宿舍楼类型")
    private String fType;

    /** 宿舍总数 */
    @Excel(name = "宿舍总数")
    private Long fDormnumber;

    /** 宿舍楼性别 */
    @Excel(name = "宿舍楼性别")
    private String fGender;

    /** 宿舍楼状态 */
    @Excel(name = "宿舍楼状态")
    private String fStatus;

    public void setfId(Long fId) 
    {
        this.fId = fId;
    }

    public Long getfId() 
    {
        return fId;
    }
    public void setfName(String fName) 
    {
        this.fName = fName;
    }

    public String getfName() 
    {
        return fName;
    }
    public void setfNumber(Long fNumber) 
    {
        this.fNumber = fNumber;
    }

    public Long getfNumber() 
    {
        return fNumber;
    }
    public void setfType(String fType) 
    {
        this.fType = fType;
    }

    public String getfType() 
    {
        return fType;
    }
    public void setfDormnumber(Long fDormnumber) 
    {
        this.fDormnumber = fDormnumber;
    }

    public Long getfDormnumber() 
    {
        return fDormnumber;
    }

    public void setfGender(String fGender) {
        this.fGender = fGender;
    }

    public String getfGender() {
        return fGender;
    }

    public void setfStatus(String fStatus) {
        this.fStatus = fStatus;
    }

    public String getfStatus() {
        return fStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("fId", getfId())
            .append("fName", getfName())
            .append("fNumber", getfNumber())
            .append("fType", getfType())
            .append("fDormnumber", getfDormnumber())
            .append("fGender", getfGender())
            .append("fStatus", getfStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
