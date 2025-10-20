package com.springboot.dorm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 留宿申请对象 dorm_keep_back
 * 
 * 
 * @date 2025-09-26
 */
public class DormKeepBack extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 留宿id */
    private Integer kbId;

    /** 留宿原因 */
    @Excel(name = "留宿原因")
    private String kbReason;

    /** 留宿时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "留宿时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date kbDatetime;

    /** 留宿学生 */
    @Excel(name = "留宿学生")
    private String kbStudentname;

    /** 学生id */
    private Integer kbStudentid;

    /** 辅导员 */
    @Excel(name = "辅导员")
    private String kbInstructor;

    /** 宿舍楼 */
    @Excel(name = "宿舍楼")
    private Integer fId;

    /** 宿舍id */
    @Excel(name = "宿舍id")
    private Integer dorId;

    /** 辅导员意见 */
    @Excel(name = "辅导员意见")
    private String kbOpinion;

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

    public void setKbId(Integer kbId)
    {
        this.kbId = kbId;
    }

    public Integer getKbId() 
    {
        return kbId;
    }
    public void setKbReason(String kbReason) 
    {
        this.kbReason = kbReason;
    }

    public String getKbReason() 
    {
        return kbReason;
    }
    public void setKbDatetime(Date kbDatetime) 
    {
        this.kbDatetime = kbDatetime;
    }

    public Date getKbDatetime() 
    {
        return kbDatetime;
    }
    public void setKbStudentname(String kbStudentname) 
    {
        this.kbStudentname = kbStudentname;
    }

    public String getKbStudentname() 
    {
        return kbStudentname;
    }
    public void setKbStudentid(Integer kbStudentid) 
    {
        this.kbStudentid = kbStudentid;
    }

    public Integer getKbStudentid() 
    {
        return kbStudentid;
    }
    public void setKbInstructor(String kbInstructor) 
    {
        this.kbInstructor = kbInstructor;
    }

    public String getKbInstructor() 
    {
        return kbInstructor;
    }
    public void setfId(Integer fId) 
    {
        this.fId = fId;
    }

    public Integer getfId() 
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
    public void setKbOpinion(String kbOpinion) 
    {
        this.kbOpinion = kbOpinion;
    }

    public String getKbOpinion() 
    {
        return kbOpinion;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("kbId", getKbId())
            .append("kbReason", getKbReason())
            .append("kbDatetime", getKbDatetime())
            .append("kbStudentname", getKbStudentname())
            .append("kbStudentid", getKbStudentid())
            .append("kbInstructor", getKbInstructor())
            .append("fId", getfId())
            .append("dorId", getDorId())
            .append("kbOpinion", getKbOpinion())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("dormDormitory",getDormDormitory())
            .append("dormFloor",getDormFloor())
            .toString();
    }
}
