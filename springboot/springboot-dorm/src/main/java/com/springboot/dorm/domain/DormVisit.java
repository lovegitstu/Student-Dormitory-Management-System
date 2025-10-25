package com.springboot.dorm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 来访人员登记对象 dorm_visit
 * 
 *
 * @date 2025-09-20
 */
public class DormVisit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 来访ID */
    private Long visId;

    /** 来访人员 */
    @Excel(name = "来访人员")
    private String visName;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String visPhone;

    /** 来访类型 */
    @Excel(name = "来访类型")
    private Integer visType;

    /** 被访人 */
    @Excel(name = "被访人")
    private String visInterviewee;

    /** 来访事由 */
    @Excel(name = "来访事由")
    private String visCause;

    /** 宿舍楼 */
    @Excel(name = "宿舍楼")
    private Long fId;

    /** 来访宿舍ID */
    @Excel(name = "来访宿舍ID")
    private Integer dorId;

    /** 来访时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "来访时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date visDatetime;

    /** 申请学生ID */
    @Excel(name = "申请学生ID")
    private Long stuId;

    /** 审批状态 (0=待审批 1=已通过 2=已拒绝) */
    @Excel(name = "审批状态")
    private String approvalStatus;

    /** 审批意见 */
    @Excel(name = "审批意见")
    private String approvalOpinion;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date approvalTime;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approvalBy;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String stuName;

    private DormFloor dormFloor;

    private  DormDormitory dormDormitory;

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

    public void setVisId(Long visId)
    {
        this.visId = visId;
    }

    public Long getVisId() 
    {
        return visId;
    }
    public void setVisName(String visName) 
    {
        this.visName = visName;
    }

    public String getVisName() 
    {
        return visName;
    }
    public void setVisPhone(String visPhone) 
    {
        this.visPhone = visPhone;
    }

    public String getVisPhone() 
    {
        return visPhone;
    }
    public void setVisType(Integer visType) 
    {
        this.visType = visType;
    }

    public Integer getVisType() 
    {
        return visType;
    }
    public void setVisInterviewee(String visInterviewee) 
    {
        this.visInterviewee = visInterviewee;
    }

    public String getVisInterviewee() 
    {
        return visInterviewee;
    }
    public void setVisCause(String visCause) 
    {
        this.visCause = visCause;
    }

    public String getVisCause() 
    {
        return visCause;
    }
    public void setfId(Long fId) 
    {
        this.fId = fId;
    }

    public Long getfId() 
    {
        return fId;
    }
    public void setDorId(Integer dorId) 
    {
        this.dorId = dorId;
    }

    public Integer getDorId() 
    {
        return dorId;
    }
    public void setVisDatetime(Date visDatetime) 
    {
        this.visDatetime = visDatetime;
    }

    public Date getVisDatetime() 
    {
        return visDatetime;
    }

    public void setStuId(Long stuId) 
    {
        this.stuId = stuId;
    }

    public Long getStuId() 
    {
        return stuId;
    }

    public void setApprovalStatus(String approvalStatus) 
    {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovalStatus() 
    {
        return approvalStatus;
    }

    public void setApprovalOpinion(String approvalOpinion) 
    {
        this.approvalOpinion = approvalOpinion;
    }

    public String getApprovalOpinion() 
    {
        return approvalOpinion;
    }

    public void setApprovalTime(Date approvalTime) 
    {
        this.approvalTime = approvalTime;
    }

    public Date getApprovalTime() 
    {
        return approvalTime;
    }

    public void setApprovalBy(String approvalBy) 
    {
        this.approvalBy = approvalBy;
    }

    public String getApprovalBy() 
    {
        return approvalBy;
    }

    public void setStuName(String stuName) 
    {
        this.stuName = stuName;
    }

    public String getStuName() 
    {
        return stuName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("visId", getVisId())
            .append("visName", getVisName())
            .append("visPhone", getVisPhone())
            .append("visType", getVisType())
            .append("visInterviewee", getVisInterviewee())
            .append("visCause", getVisCause())
            .append("fId", getfId())
            .append("dorId", getDorId())
            .append("visDatetime", getVisDatetime())
            .append("stuId", getStuId())
            .append("approvalStatus", getApprovalStatus())
            .append("approvalOpinion", getApprovalOpinion())
            .append("approvalTime", getApprovalTime())
            .append("approvalBy", getApprovalBy())
            .append("stuName", getStuName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("dormFloor",getDormFloor())
            .append("dormDormitory",getDormDormitory())
            .toString();
    }
}
