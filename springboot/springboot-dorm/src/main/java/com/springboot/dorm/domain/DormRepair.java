package com.springboot.dorm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 维修工单对象 dorm_repair
 * 
 *
 * @date 2025-09-20
 */
public class DormRepair extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer repairId;

    /** 宿舍楼 */
    @Excel(name = "宿舍楼")
    private Long fId;

    /** 维修宿舍 */
    @Excel(name = "维修宿舍")
    private Integer dorId;

    /** 维修内容类型 */
    @Excel(name = "维修内容类型")
    private String repairContentType;

    /** 维修内容描述 */
    @Excel(name = "维修内容描述")
    private String description;

    /** 维修状态 */
    @Excel(name = "维修状态")
    private String repairStatus;

    /** 维修时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "维修时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date repairDatetime;

    /** 报修人 */
    @Excel(name = "报修人")
    private String nickName;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String phone;

    /** 维修级别 */
    @Excel(name = "维修级别")
    private String repairGrade;

    private DormFloor dormFloor;

    private DormDormitory dormDormitory;

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

    public void setRepairId(Integer repairId)
    {
        this.repairId = repairId;
    }

    public Integer getRepairId() 
    {
        return repairId;
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
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setRepairContentType(String repairContentType) 
    {
        this.repairContentType = repairContentType;
    }

    public String getRepairContentType() 
    {
        return repairContentType;
    }
    public void setRepairStatus(String repairStatus) 
    {
        this.repairStatus = repairStatus;
    }

    public String getRepairStatus() 
    {
        return repairStatus;
    }
    public void setRepairDatetime(Date repairDatetime) 
    {
        this.repairDatetime = repairDatetime;
    }

    public Date getRepairDatetime() 
    {
        return repairDatetime;
    }
    public void setNickName(String nickName) 
    {
        this.nickName = nickName;
    }

    public String getNickName() 
    {
        return nickName;
    }
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }
    public void setRepairGrade(String repairGrade) 
    {
        this.repairGrade = repairGrade;
    }

    public String getRepairGrade() 
    {
        return repairGrade;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("repairId", getRepairId())
            .append("fId", getfId())
            .append("dorId", getDorId())
            .append("repairContentType", getRepairContentType())
            .append("description", getDescription())
            .append("repairStatus", getRepairStatus())
            .append("repairDatetime", getRepairDatetime())
            .append("nickName", getNickName())
            .append("phone", getPhone())
            .append("remark", getRemark())
            .append("repairGrade", getRepairGrade())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("dormFloor",getDormFloor())
            .append("dormDormitory",getDormDormitory())
            .toString();
    }
}
