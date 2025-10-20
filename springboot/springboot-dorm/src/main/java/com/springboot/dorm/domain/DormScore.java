package com.springboot.dorm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 宿舍评分对象 dorm_score
 * 
 *
 * @date 2025-09-18
 */
public class DormScore extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评分ID */
    private Long scoreId;

    /** 学生ID */
    @Excel(name = "学生ID")
    private Long studentId;

    /** 宿舍ID */
    @Excel(name = "宿舍ID")
    private Long dormId;

    /** 评分时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "评分时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date scoreDate;

    /** 卫生评分 */
    @Excel(name = "卫生评分")
    private BigDecimal hygieneScore;

    /** 纪律评分 */
    @Excel(name = "纪律评分")
    private BigDecimal disciplineScore;

    /** 安全评分 */
    @Excel(name = "安全评分")
    private BigDecimal safetyScore;

    /** 总分 */
    @Excel(name = "总分")
    private BigDecimal totalScore;

    /** 评分人 */
    @Excel(name = "评分人")
    private String scorer;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    public void setScoreId(Long scoreId) 
    {
        this.scoreId = scoreId;
    }

    public Long getScoreId() 
    {
        return scoreId;
    }
    
    public void setStudentId(Long studentId) 
    {
        this.studentId = studentId;
    }

    public Long getStudentId() 
    {
        return studentId;
    }
    
    public void setDormId(Long dormId) 
    {
        this.dormId = dormId;
    }

    public Long getDormId() 
    {
        return dormId;
    }
    
    public void setScoreDate(Date scoreDate) 
    {
        this.scoreDate = scoreDate;
    }

    public Date getScoreDate() 
    {
        return scoreDate;
    }
    
    public void setHygieneScore(BigDecimal hygieneScore) 
    {
        this.hygieneScore = hygieneScore;
    }

    public BigDecimal getHygieneScore() 
    {
        return hygieneScore;
    }
    
    public void setDisciplineScore(BigDecimal disciplineScore) 
    {
        this.disciplineScore = disciplineScore;
    }

    public BigDecimal getDisciplineScore() 
    {
        return disciplineScore;
    }
    
    public void setSafetyScore(BigDecimal safetyScore) 
    {
        this.safetyScore = safetyScore;
    }

    public BigDecimal getSafetyScore() 
    {
        return safetyScore;
    }
    
    public void setTotalScore(BigDecimal totalScore) 
    {
        this.totalScore = totalScore;
    }

    public BigDecimal getTotalScore() 
    {
        return totalScore;
    }
    
    public void setScorer(String scorer) 
    {
        this.scorer = scorer;
    }

    public String getScorer() 
    {
        return scorer;
    }
    
    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("scoreId", getScoreId())
            .append("studentId", getStudentId())
            .append("dormId", getDormId())
            .append("scoreDate", getScoreDate())
            .append("hygieneScore", getHygieneScore())
            .append("disciplineScore", getDisciplineScore())
            .append("safetyScore", getSafetyScore())
            .append("totalScore", getTotalScore())
            .append("scorer", getScorer())
            .append("remarks", getRemarks())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}