package com.springboot.dorm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 宿舍评分对象 dorm_contest
 * 
 *
 * @date 2025-09-18
 */
public class DormContest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer conId;

    /** 评比时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "评比时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date conDatetime;

    /** 宿舍楼 */
    @Excel(name = "宿舍楼")
    private Long fId;

    /** 宿舍 */
    @Excel(name = "宿舍")
    private Long dorId;

    /** 床铺评分 */
    @Excel(name = "床铺评分")
    private BigDecimal conBed;

    /** 地面评分 */
    @Excel(name = "地面评分")
    private BigDecimal conFloor;

    /** 墙壁是否乱画 */
    @Excel(name = "墙壁是否乱画")
    private BigDecimal conWell;

    /** 厕所卫生 */
    @Excel(name = "厕所卫生")
    private BigDecimal conToilet;

    /** 物品摆放 */
    @Excel(name = "物品摆放")
    private BigDecimal conGoods;

    /** 总分 */
    @Excel(name = "总分")
    private BigDecimal conTotal;

    /** 评分人 */
    @Excel(name = "评分人")
    private String conUser;

    /** 评比情况 */
    @Excel(name = "评比情况")
    private String conDesignation;

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

    public void setConId(Integer conId)
    {
        this.conId = conId;
    }

    public Integer getConId() 
    {
        return conId;
    }
    public void setConDatetime(Date conDatetime) 
    {
        this.conDatetime = conDatetime;
    }

    public Date getConDatetime() 
    {
        return conDatetime;
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
    public void setConBed(BigDecimal conBed) 
    {
        this.conBed = conBed;
    }

    public BigDecimal getConBed() 
    {
        return conBed;
    }
    public void setConFloor(BigDecimal conFloor) 
    {
        this.conFloor = conFloor;
    }

    public BigDecimal getConFloor() 
    {
        return conFloor;
    }
    public void setConWell(BigDecimal conWell) 
    {
        this.conWell = conWell;
    }

    public BigDecimal getConWell() 
    {
        return conWell;
    }
    public void setConToilet(BigDecimal conToilet) 
    {
        this.conToilet = conToilet;
    }

    public BigDecimal getConToilet() 
    {
        return conToilet;
    }
    public void setConGoods(BigDecimal conGoods) 
    {
        this.conGoods = conGoods;
    }

    public BigDecimal getConGoods() 
    {
        return conGoods;
    }
    public void setConTotal(BigDecimal conTotal) 
    {
        this.conTotal = conTotal;
    }

    public BigDecimal getConTotal() 
    {
        return conTotal;
    }
    public void setConUser(String conUser) 
    {
        this.conUser = conUser;
    }

    public String getConUser() 
    {
        return conUser;
    }
    public void setConDesignation(String conDesignation) 
    {
        this.conDesignation = conDesignation;
    }

    public String getConDesignation() 
    {
        return conDesignation;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("conId", getConId())
            .append("conDatetime", getConDatetime())
            .append("fId", getfId())
            .append("dorId", getDorId())
            .append("conBed", getConBed())
            .append("conFloor", getConFloor())
            .append("conWell", getConWell())
            .append("conToilet", getConToilet())
            .append("conGoods", getConGoods())
            .append("conTotal", getConTotal())
            .append("conUser", getConUser())
            .append("conDesignation", getConDesignation())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("dormFloor",getDormFloor())
            .append("dormDormitory",getDormDormitory())
            .toString();
    }
}
