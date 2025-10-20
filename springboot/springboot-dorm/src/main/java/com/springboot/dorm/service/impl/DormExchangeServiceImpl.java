package com.springboot.dorm.service.impl;

import java.util.List;
import com.springboot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.dorm.mapper.DormExchangeMapper;
import com.springboot.dorm.domain.DormExchange;
import com.springboot.dorm.domain.DormBed;
import com.springboot.dorm.service.IDormExchangeService;
import com.springboot.dorm.service.IDormBedService;

/**
 * 换宿申请Service业务层处理
 * 
 *
 * @date 2025-09-27
 */
@Service
public class DormExchangeServiceImpl implements IDormExchangeService 
{
    @Autowired
    private DormExchangeMapper dormExchangeMapper;
    
    @Autowired
    private IDormBedService dormBedService;

    /**
     * 查询换宿申请
     * 
     * @param id 换宿申请主键
     * @return 换宿申请
     */
    @Override
    public DormExchange selectDormExchangeById(Long id)
    {
        return dormExchangeMapper.selectDormExchangeById(id);
    }

    /**
     * 查询换宿申请列表
     * 
     * @param dormExchange 换宿申请
     * @return 换宿申请
     */
    @Override
    public List<DormExchange> selectDormExchangeList(DormExchange dormExchange)
    {
        return dormExchangeMapper.selectDormExchangeList(dormExchange);
    }

    /**
     * 新增换宿申请
     * 
     * @param dormExchange 换宿申请
     * @return 结果
     */
    @Override
    public int insertDormExchange(DormExchange dormExchange)
    {
        System.out.println("=== 开始新增换宿申请 ===");
        System.out.println("申请信息 - 学生ID: " + dormExchange.getStuId() + 
                         ", 学生姓名: " + dormExchange.getStuName() +
                         ", 原宿舍: " + dormExchange.getDormName() +
                         ", 原床位ID: " + dormExchange.getOriginalBedId() +
                         ", 新楼层ID: " + dormExchange.getfId() +
                         ", 新宿舍ID: " + dormExchange.getDorId() +
                         ", 新床位ID: " + dormExchange.getBedId() +
                         ", 申请原因: " + dormExchange.getExcuse());
        
        dormExchange.setCreateTime(DateUtils.getNowDate());
        
        int result = dormExchangeMapper.insertDormExchange(dormExchange);
        
        System.out.println("插入结果: " + (result > 0 ? "成功" : "失败") + ", 影响行数: " + result);
        System.out.println("=== 换宿申请新增完成 ===");
        
        return result;
    }

    /**
     * 修改换宿申请
     * 
     * @param dormExchange 换宿申请
     * @return 结果
     */
    @Override
    public int updateDormExchange(DormExchange dormExchange)
    {
        dormExchange.setUpdateTime(DateUtils.getNowDate());
        return dormExchangeMapper.updateDormExchange(dormExchange);
    }

    /**
     * 批量删除换宿申请
     * 
     * @param ids 需要删除的换宿申请主键
     * @return 结果
     */
    @Override
    public int deleteDormExchangeByIds(Long[] ids)
    {
        return dormExchangeMapper.deleteDormExchangeByIds(ids);
    }

    /**
     * 删除换宿申请信息
     * 
     * @param id 换宿申请主键
     * @return 结果
     */
    @Override
    public int deleteDormExchangeById(Long id)
    {
        return dormExchangeMapper.deleteDormExchangeById(id);
    }

    /**
     * 审批换宿申请
     * 
     * @param dormExchange 换宿申请
     * @return 结果
     */
    @Override
    public int approveDormExchange(DormExchange dormExchange)
    {
        System.out.println("=== 开始审批换宿申请 ===");
        System.out.println("申请ID: " + dormExchange.getId() + ", 审批意见: " + dormExchange.getOpinion());
        
        dormExchange.setUpdateTime(DateUtils.getNowDate());
        int result = dormExchangeMapper.updateDormExchange(dormExchange);
        
        // 如果审核通过，需要更新床位分配
        if (result > 0 && dormExchange.getOpinion() != null && dormExchange.getOpinion() == 1) {
            System.out.println("审核通过，开始更新床位分配");
            
            // 获取申请详情
            DormExchange exchangeDetail = dormExchangeMapper.selectDormExchangeById(dormExchange.getId());
            if (exchangeDetail != null) {
                System.out.println("申请详情 - 学生ID: " + exchangeDetail.getStuId() + 
                                 ", 学生姓名: " + exchangeDetail.getStuName() +
                                 ", 申请记录中的原床位ID: " + exchangeDetail.getOriginalBedId() + 
                                 ", 新床位ID: " + exchangeDetail.getBedId());
                
                // 1. 先检查新床位是否可用
                if (exchangeDetail.getBedId() != null) {
                    DormBed targetBed = dormBedService.selectDormBedByBedId(exchangeDetail.getBedId());
                    if (targetBed != null && "1".equals(targetBed.getBedStatus())) {
                        System.out.println("错误：目标床位 " + exchangeDetail.getBedId() + " 已被占用，无法分配");
                        return result;
                    }
                }
                
                // 2. 查找申请学生当前实际入住的床位（只查找该学生的床位）
                List<DormBed> currentBeds = dormBedService.selectDormBedList(new DormBed() {{
                    setStuId(exchangeDetail.getStuId());
                    setBedStatus("1"); // 已占用状态
                }});
                
                System.out.println("申请学生当前实际入住的床位数量: " + (currentBeds != null ? currentBeds.size() : 0));
                
                // 3. 释放申请学生当前所有入住的床位
                if (currentBeds != null && !currentBeds.isEmpty()) {
                    for (DormBed currentBed : currentBeds) {
                        // 确保只释放申请学生自己的床位
                        if (exchangeDetail.getStuId().equals(currentBed.getStuId())) {
                            System.out.println("开始释放申请学生的床位: " + currentBed.getBedId() + 
                                             " (学生ID: " + currentBed.getStuId() + ")");
                            DormBed bedToRelease = new DormBed();
                            bedToRelease.setBedId(currentBed.getBedId());
                            bedToRelease.setStuId(null);
                            bedToRelease.setStuName(null);
                            bedToRelease.setBedStatus("0"); // 设置为空闲
                            bedToRelease.setUpdateTime(DateUtils.getNowDate());
                            
                            int releaseResult = dormBedService.updateDormBed(bedToRelease);
                            System.out.println("释放床位 " + currentBed.getBedId() + " 结果: " + (releaseResult > 0 ? "成功" : "失败"));
                            
                            if (releaseResult <= 0) {
                                System.out.println("释放床位失败，终止换宿流程");
                                return result;
                            }
                        } else {
                            System.out.println("警告：发现不属于申请学生的床位数据，跳过释放 - 床位ID: " + 
                                             currentBed.getBedId() + ", 床位学生ID: " + currentBed.getStuId() + 
                                             ", 申请学生ID: " + exchangeDetail.getStuId());
                        }
                    }
                } else {
                    System.out.println("警告：未找到申请学生当前入住的床位，尝试释放申请记录中的原床位");
                    // 如果没有找到当前入住的床位，尝试释放申请记录中的原床位
                    if (exchangeDetail.getOriginalBedId() != null) {
                        System.out.println("开始释放申请记录中的原床位: " + exchangeDetail.getOriginalBedId());
                        
                        // 先验证原床位确实属于申请学生
                        DormBed originalBedCheck = dormBedService.selectDormBedByBedId(exchangeDetail.getOriginalBedId());
                        if (originalBedCheck != null && exchangeDetail.getStuId().equals(originalBedCheck.getStuId())) {
                            DormBed originalBed = new DormBed();
                            originalBed.setBedId(exchangeDetail.getOriginalBedId());
                            originalBed.setStuId(null);
                            originalBed.setStuName(null);
                            originalBed.setBedStatus("0"); // 设置为空闲
                            originalBed.setUpdateTime(DateUtils.getNowDate());
                            
                            int originalResult = dormBedService.updateDormBed(originalBed);
                            System.out.println("释放申请记录中的原床位结果: " + (originalResult > 0 ? "成功" : "失败"));
                            
                            if (originalResult <= 0) {
                                System.out.println("释放原床位失败，终止换宿流程");
                                return result;
                            }
                        } else {
                            System.out.println("错误：申请记录中的原床位不属于申请学生，无法释放");
                            return result;
                        }
                    }
                }
                
                // 4. 分配新床位给申请学生
                if (exchangeDetail.getBedId() != null) {
                    System.out.println("开始分配新床位: " + exchangeDetail.getBedId() + " 给学生: " + 
                                     exchangeDetail.getStuId() + " (" + exchangeDetail.getStuName() + ")");
                    
                    DormBed newBed = new DormBed();
                    newBed.setBedId(exchangeDetail.getBedId());
                    newBed.setStuId(exchangeDetail.getStuId());
                    // 注意：不设置stuName，因为数据库表中没有该字段，学生姓名通过关联查询获取
                    newBed.setBedStatus("1"); // 设置为已占用
                    newBed.setUpdateTime(DateUtils.getNowDate());
                    
                    int newResult = dormBedService.updateDormBed(newBed);
                    System.out.println("分配新床位结果: " + (newResult > 0 ? "成功" : "失败"));
                    
                    // 验证床位分配后的实际数据
                    if (newResult > 0) {
                        System.out.println("验证床位分配结果...");
                        DormBed verifyBed = dormBedService.selectDormBedByBedId(exchangeDetail.getBedId());
                        if (verifyBed != null) {
                            System.out.println("床位分配验证 - 床位ID: " + verifyBed.getBedId() + 
                                             ", 学生ID: " + verifyBed.getStuId() + 
                                             ", 学生姓名: " + verifyBed.getStuName() + 
                                             ", 床位状态: " + verifyBed.getBedStatus());
                        }
                    }
                    
                    if (newResult <= 0) {
                        System.out.println("分配新床位失败，需要回滚");
                        // 如果新床位分配失败，需要回滚：重新分配原床位给申请学生
                        if (exchangeDetail.getOriginalBedId() != null) {
                            System.out.println("回滚：重新分配原床位给申请学生");
                            DormBed rollbackBed = new DormBed();
                            rollbackBed.setBedId(exchangeDetail.getOriginalBedId());
                            rollbackBed.setStuId(exchangeDetail.getStuId());
                            rollbackBed.setBedStatus("1");
                            rollbackBed.setUpdateTime(DateUtils.getNowDate());
                            dormBedService.updateDormBed(rollbackBed);
                        }
                    }
                }
            }
        }
        
        System.out.println("=== 审批换宿申请完成 ===");
        return result;
    }

    /**
     * 批量审批换宿申请
     * 
     * @param dormExchanges 换宿申请集合
     * @return 结果
     */
    @Override
    public int batchApproveDormExchange(List<DormExchange> dormExchanges)
    {
        int result = 0;
        for (DormExchange dormExchange : dormExchanges) {
            dormExchange.setUpdateTime(DateUtils.getNowDate());
            int updateResult = dormExchangeMapper.updateDormExchange(dormExchange);
            
            // 如果审核通过，需要更新床位分配
            if (updateResult > 0 && dormExchange.getOpinion() != null && dormExchange.getOpinion() == 1) {
                // 获取申请详情
                DormExchange exchangeDetail = dormExchangeMapper.selectDormExchangeById(dormExchange.getId());
                if (exchangeDetail != null) {
                    // 1. 释放原床位
                    if (exchangeDetail.getOriginalBedId() != null) {
                        DormBed originalBed = new DormBed();
                        originalBed.setBedId(exchangeDetail.getOriginalBedId());
                        originalBed.setStuId(null);
                        originalBed.setStuName(null);
                        originalBed.setBedStatus("0"); // 设置为空闲
                        dormBedService.updateDormBed(originalBed);
                    }
                    
                    // 2. 分配新床位
                    if (exchangeDetail.getBedId() != null) {
                        // 重要：使用当前循环中的dormExchange的学生ID，而不是exchangeDetail中可能错误的学生ID
                        // 因为exchangeDetail是从数据库查询的历史记录，可能包含错误的学生ID
                        Long correctStuId = dormExchange.getStuId() != null ? dormExchange.getStuId() : exchangeDetail.getStuId();
                        
                        DormBed newBed = new DormBed();
                        newBed.setBedId(exchangeDetail.getBedId());
                        newBed.setStuId(correctStuId);
                        // 注意：不设置stuName，因为数据库表中没有该字段，学生姓名通过关联查询获取
                        newBed.setBedStatus("1"); // 设置为已占用
                        dormBedService.updateDormBed(newBed);
                    }
                }
            }
            
            result += updateResult;
        }
        return result;
    }
}
