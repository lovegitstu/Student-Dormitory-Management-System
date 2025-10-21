package com.springboot.dorm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 学生信息对象 dorm_student
 * 
 * 
 * @date 2025-09-16
 */
public class DormStudent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 学生ID */
    private Long stuId;

    /** 学号 */
    @Excel(name = "学号")
    private Integer stuCode;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String stuName;

    /** 年龄 */
    @Excel(name = "年龄")
    private Long stuAge;

    /** 性别 */
    @Excel(name = "性别")
    private String stuSex;

    /** 照片 */
    @Excel(name = "照片")
    private String stuPhoto;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String stuPhone;

    /** 专业 */
    @Excel(name = "专业")
    private String stuMajor;

    /** 宿舍楼 */
    @Excel(name = "宿舍楼")
    private Long fId;

    /** 宿舍号 */
    @Excel(name = "宿舍号")
    private Long dorId;

    /** 状态 */
    @Excel(name = "状态")
    private String stuStatus;

    /** 记录状态 */
    private String status;

    /** 删除标志 */
    private String delFlag;

    private DormFloor dormFloor;
    private DormDormitory dormDormitory;
    /** 关联用户ID */
    private Long userId;
    /** 床位编号 */
    private String bedCode;

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

    public void setStuId(Long stuId)
    {
        this.stuId = stuId;
    }

    public Long getStuId() 
    {
        return stuId;
    }
    public void setStuCode(Integer stuCode) 
    {
        this.stuCode = stuCode;
    }

    public Integer getStuCode() 
    {
        return stuCode;
    }
    public void setStuName(String stuName) 
    {
        this.stuName = stuName;
    }

    public String getStuName() 
    {
        return stuName;
    }
    public void setStuAge(Long stuAge) 
    {
        this.stuAge = stuAge;
    }

    public Long getStuAge() 
    {
        return stuAge;
    }
    public void setStuSex(String stuSex) 
    {
        this.stuSex = stuSex;
    }

    public String getStuSex() 
    {
        return stuSex;
    }
    public void setStuPhoto(String stuPhoto) 
    {
        this.stuPhoto = stuPhoto;
    }

    public String getStuPhoto() 
    {
        return stuPhoto;
    }
    public void setStuPhone(String stuPhone) 
    {
        this.stuPhone = stuPhone;
    }

    public String getStuPhone() 
    {
        return stuPhone;
    }
    public void setStuMajor(String stuMajor) 
    {
        this.stuMajor = stuMajor;
    }

    public String getStuMajor() 
    {
        return stuMajor;
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
    public void setStuStatus(String stuStatus) 
    {
        this.stuStatus = stuStatus;
    }

    public String getStuStatus() 
    {
        return stuStatus;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBedCode() {
        return bedCode;
    }

    public void setBedCode(String bedCode) {
        this.bedCode = bedCode;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("stuId", getStuId())
            .append("stuCode", getStuCode())
            .append("stuName", getStuName())
            .append("stuAge", getStuAge())
            .append("stuSex", getStuSex())
            .append("stuPhoto", getStuPhoto())
            .append("stuPhone", getStuPhone())
            .append("stuMajor", getStuMajor())
            .append("fId", getfId())
            .append("dorId", getDorId())
            .append("stuStatus", getStuStatus())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("dormFloor",getDormFloor())
            .append("dormDormitory",getDormDormitory())
            .append("userId", getUserId())
            .append("bedCode", getBedCode())
            .toString();
    }
}
