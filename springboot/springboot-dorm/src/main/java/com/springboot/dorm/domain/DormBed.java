package com.springboot.dorm.domain;

import com.springboot.common.core.domain.entity.SysUser;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 床位管理对象 dorm_bed
 * 
 * 
 * @date 2025-09-15
 */
public class DormBed extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 床位ID */
    private Long bedId;

    /** 宿舍 */
    @Excel(name = "宿舍")
    private Long dorId;

    /** 学生Id */
    @Excel(name = "学生Id")
    private Long stuId;

    /** 学生姓名 (通过关联查询获取，不对应数据库字段) */
    @Excel(name = "学生姓名")
    private String stuName;

    public Long getStuId() {
        return stuId;
    }

    public void setStuId(Long stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    /** 床位编号 */
    @Excel(name = "床位编号")
    private String bedCode;

    /** 床位状态 */
    @Excel(name = "床位状态")
    private String bedStatus;

    /** 床位记录id */
    @Excel(name = "床位记录id")
    private Long bedRecordId;

//    private SysUser sysUser;
//
//    public SysUser getSysUser() {
//        return sysUser;
//    }
//
//    public void setSysUser(SysUser sysUser) {
//        this.sysUser = sysUser;
//    }

    private DormDormitory dormDormitory;
    
    private DormStudent dormStudent;

    public DormDormitory getDormDormitory() {
        return dormDormitory;
    }

    public void setDormDormitory(DormDormitory dormDormitory) {
        this.dormDormitory = dormDormitory;
    }
    
    public DormStudent getDormStudent() {
        return dormStudent;
    }

    public void setDormStudent(DormStudent dormStudent) {
        this.dormStudent = dormStudent;
    }

    public void setBedId(Long bedId)
    {
        this.bedId = bedId;
    }

    public Long getBedId() 
    {
        return bedId;
    }
    public void setDorId(Long dorId) 
    {
        this.dorId = dorId;
    }

    public Long getDorId() 
    {
        return dorId;
    }
    public void setBedCode(String bedCode) 
    {
        this.bedCode = bedCode;
    }

    public String getBedCode() 
    {
        return bedCode;
    }
    public void setBedStatus(String bedStatus) 
    {
        this.bedStatus = bedStatus;
    }

    public String getBedStatus() 
    {
        return bedStatus;
    }
    public void setBedRecordId(Long bedRecordId) 
    {
        this.bedRecordId = bedRecordId;
    }

    public Long getBedRecordId() 
    {
        return bedRecordId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("bedId", getBedId())
            .append("dorId", getDorId())
            .append("stuId",getStuId())
            .append("bedCode", getBedCode())
            .append("bedStatus", getBedStatus())
            .append("bedRecordId", getBedRecordId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .append("dormDormitory",getDormDormitory())
//            .append("sysUser",getSysUser())
            .toString();
    }
}
