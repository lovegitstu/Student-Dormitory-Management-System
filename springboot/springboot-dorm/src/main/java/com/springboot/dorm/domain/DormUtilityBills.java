package com.springboot.dorm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.common.core.domain.entity.SysUser;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 水电费管理对象 dorm_utility_bills
 * 
 * 
 * @date 2025-09-20
 */
public class DormUtilityBills extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer ubId;

    /** 宿舍楼 */
    @Excel(name = "宿舍楼")
    private Long fId;

    /** 宿舍 */
    @Excel(name = "宿舍")
    private Integer dorId;

    /** 费用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "费用时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date ubDatetime;

    /** 水费(元) */
    @Excel(name = "水费(元)")
    private BigDecimal ubWater;

    /** 单位(吨) */
    @Excel(name = "单位(吨)")
    private BigDecimal ubWaterMonad;

    /** 电费(元) */
    @Excel(name = "电费(元)")
    private BigDecimal ubElectricityRate;

    /** 电单位(度) */
    @Excel(name = "电单位(度)")
    private BigDecimal ubElectricityUnits;

    /** 用水量 */
    @Excel(name = "用水量")
    private BigDecimal ubWaterConsumption;

    /** 用电量 */
    @Excel(name = "用电量")
    private BigDecimal ubElectricityConsumption;

    /** 总费用 */
    @Excel(name = "总费用")
    private BigDecimal ubTotalCost;

    /** 余额 */
    @Excel(name = "余额")
    private BigDecimal ubBalance;

    /** 缴费状态(0:未缴费,1:已缴费) */
    @Excel(name = "缴费状态")
    private Integer ubPaymentStatus;

    /** 是否核验 */
    @Excel(name = "是否核验")
    private Integer ubIsnotarize;

    public Integer getUbIsnotarize() {
        return ubIsnotarize;
    }

    public void setUbIsnotarize(Integer ubIsnotarize) {
        this.ubIsnotarize = ubIsnotarize;
    }

    /** 抄表/核算人 */
    @Excel(name = "抄表/核算人")
    private String ubMeterReading;



    /** 联系电话 */
    @Excel(name = "联系电话")
    private Long ubPhone;

//    private SysUser sysUser;
//
//    public SysUser getSysUser() {
//        return sysUser;
//    }
//
//    public void setSysUser(SysUser sysUser) {
//        this.sysUser = sysUser;
//    }

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

    public void setUbId(Integer ubId)
    {
        this.ubId = ubId;
    }

    public Integer getUbId() 
    {
        return ubId;
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
    public void setUbDatetime(Date ubDatetime) 
    {
        this.ubDatetime = ubDatetime;
    }

    public Date getUbDatetime() 
    {
        return ubDatetime;
    }
    public void setUbWater(BigDecimal ubWater) 
    {
        this.ubWater = ubWater;
    }

    public BigDecimal getUbWater() 
    {
        return ubWater;
    }
    public void setUbWaterMonad(BigDecimal ubWaterMonad) 
    {
        this.ubWaterMonad = ubWaterMonad;
    }

    public BigDecimal getUbWaterMonad() 
    {
        return ubWaterMonad;
    }
    public void setUbElectricityRate(BigDecimal ubElectricityRate) 
    {
        this.ubElectricityRate = ubElectricityRate;
    }

    public BigDecimal getUbElectricityRate() 
    {
        return ubElectricityRate;
    }
    public void setUbElectricityUnits(BigDecimal ubElectricityUnits) 
    {
        this.ubElectricityUnits = ubElectricityUnits;
    }

    public BigDecimal getUbElectricityUnits() 
    {
        return ubElectricityUnits;
    }
    public void setUbWaterConsumption(BigDecimal ubWaterConsumption) 
    {
        this.ubWaterConsumption = ubWaterConsumption;
    }

    public BigDecimal getUbWaterConsumption() 
    {
        return ubWaterConsumption;
    }
    public void setUbElectricityConsumption(BigDecimal ubElectricityConsumption) 
    {
        this.ubElectricityConsumption = ubElectricityConsumption;
    }

    public BigDecimal getUbElectricityConsumption() 
    {
        return ubElectricityConsumption;
    }
    public void setUbTotalCost(BigDecimal ubTotalCost) 
    {
        this.ubTotalCost = ubTotalCost;
    }

    public BigDecimal getUbTotalCost() 
    {
        return ubTotalCost;
    }
    public void setUbBalance(BigDecimal ubBalance) 
    {
        this.ubBalance = ubBalance;
    }

    public BigDecimal getUbBalance() 
    {
        return ubBalance;
    }
    public void setUbMeterReading(String ubMeterReading) 
    {
        this.ubMeterReading = ubMeterReading;
    }

    public String getUbMeterReading() 
    {
        return ubMeterReading;
    }
    public void setUbPhone(Long ubPhone) 
    {
        this.ubPhone = ubPhone;
    }

    public Long getUbPhone() 
    {
        return ubPhone;
    }

    public void setUbPaymentStatus(Integer ubPaymentStatus) 
    {
        this.ubPaymentStatus = ubPaymentStatus;
    }

    public Integer getUbPaymentStatus() 
    {
        return ubPaymentStatus;
    }

    // 添加Controller中需要的getter方法
    public Date getUbUsageDate() 
    {
        return ubDatetime;
    }

    public BigDecimal getUbElectricUsage() 
    {
        return ubElectricityConsumption;
    }

    public BigDecimal getUbWaterUsage() 
    {
        return ubWaterConsumption;
    }

    public BigDecimal getUbElectricCost() 
    {
        return ubElectricityRate;
    }

    public BigDecimal getUbWaterCost() 
    {
        return ubWater;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("ubId", getUbId())
            .append("fId", getfId())
            .append("dorId", getDorId())
            .append("ubDatetime", getUbDatetime())
            .append("ubWater", getUbWater())
            .append("ubWaterMonad", getUbWaterMonad())
            .append("ubElectricityRate", getUbElectricityRate())
            .append("ubElectricityUnits", getUbElectricityUnits())
            .append("ubWaterConsumption", getUbWaterConsumption())
            .append("ubElectricityConsumption", getUbElectricityConsumption())
            .append("ubTotalCost", getUbTotalCost())
            .append("ubIsnotarize",getUbIsnotarize())
            .append("remark", getRemark())
            .append("ubMeterReading", getUbMeterReading())
            .append("ubPhone", getUbPhone())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("dormFloor",getDormFloor())
            .append("dormDormitory",getDormDormitory())
//            .append("sysUser",getSysUser())
            .toString();
    }
}
