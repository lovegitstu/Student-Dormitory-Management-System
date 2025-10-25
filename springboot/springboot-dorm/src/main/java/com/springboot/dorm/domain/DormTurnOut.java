package com.springboot.dorm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 离校登记对象 dorm_turn_out
 * 
 *
 * @date 2025-09-26
 */
public class DormTurnOut extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 学生id */
    @Excel(name = "学生id")
    private Long stuId;

    /** 离校原因 */
    @Excel(name = "离校原因")
    private String excuse;

    /** 离校时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "离校时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date toDatetime;

    /** 审批状态 */
    @Excel(name = "审批状态")
    private String approvalStatus;

    /** 状态 (0=正常 1=停用 2=已完成-学生已返校) */
    @Excel(name = "状态")
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

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
    
    public void setExcuse(String excuse) 
    {
        this.excuse = excuse;
    }

    public String getExcuse() 
    {
        return excuse;
    }
    
    public void setToDatetime(Date datetime)
    {
        this.toDatetime = datetime;
    }

    public Date getToDatetime()
    {
        return toDatetime;
    }

    public void setApprovalStatus(String approvalStatus) 
    {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovalStatus() 
    {
        return approvalStatus;
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

    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    public String getRemark() 
    {
        return remark;
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
            .append("excuse", getExcuse())
            .append("toDatetime", getToDatetime())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
