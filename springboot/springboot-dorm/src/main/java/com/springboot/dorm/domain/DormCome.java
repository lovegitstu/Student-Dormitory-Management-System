package com.springboot.dorm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 回校申请对象 dorm_come
 * 
 *
 * @date 2025-09-25
 */
public class DormCome extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 学生id */
    private Long stuId;

    /** 回校时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "回校时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date datetime;

    /** 原因 */
    @Excel(name = "原因")
    private String cause;

    /** 辅导员意见 */
    @Excel(name = "辅导员意见")
    private String opinion;

    /** 回校原因 */
    @Excel(name = "回校原因")
    private String comeType;

    /** 审批状态 */
    @Excel(name = "审批状态")
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String stuName;

    /** 宿舍楼ID */
    private Long fId;

    /** 宿舍号ID */
    private Long dorId;

    /** 宿舍楼信息 */
    private DormFloor dormFloor;

    /** 宿舍信息 */
    private DormDormitory dormDormitory;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setStuId(Long stuId) 
    {
        this.stuId = stuId;
    }

    public Long getStuId() 
    {
        return stuId;
    }
    public void setDatetime(Date datetime) 
    {
        this.datetime = datetime;
    }

    public Date getDatetime() 
    {
        return datetime;
    }
    public void setCause(String cause) 
    {
        this.cause = cause;
    }

    public String getCause() 
    {
        return cause;
    }
    public void setOpinion(String opinion) 
    {
        this.opinion = opinion;
    }

    public String getOpinion() 
    {
        return opinion;
    }
    public void setComeType(String comeType) 
    {
        this.comeType = comeType;
    }

    public String getComeType() 
    {
        return comeType;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setStuName(String stuName) 
    {
        this.stuName = stuName;
    }

    public String getStuName() 
    {
        return stuName;
    }

    public void setFId(Long fId) 
    {
        this.fId = fId;
    }

    public Long getFId() 
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

    public void setDormFloor(DormFloor dormFloor) 
    {
        this.dormFloor = dormFloor;
    }

    public DormFloor getDormFloor() 
    {
        return dormFloor;
    }

    public void setDormDormitory(DormDormitory dormDormitory) 
    {
        this.dormDormitory = dormDormitory;
    }

    public DormDormitory getDormDormitory() 
    {
        return dormDormitory;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("stuId", getStuId())
            .append("datetime", getDatetime())
            .append("cause", getCause())
            .append("opinion", getOpinion())
            .append("comeType", getComeType())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
