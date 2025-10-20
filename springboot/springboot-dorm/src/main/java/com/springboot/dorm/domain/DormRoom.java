package com.springboot.dorm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.springboot.common.annotation.Excel;
import com.springboot.common.core.domain.BaseEntity;

/**
 * 房间信息对象 dorm_room
 * 
 *
 * @date 2025-09-16
 */
public class DormRoom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 房间ID */
    private Long roomId;

    /** 房间号 */
    @Excel(name = "房间号")
    private String roomNumber;

    /** 楼层ID */
    @Excel(name = "楼层ID")
    private Long floorId;

    /** 房间类型 */
    @Excel(name = "房间类型")
    private String roomType;

    /** 房间状态 */
    @Excel(name = "房间状态")
    private String roomStatus;

    /** 最大容量 */
    @Excel(name = "最大容量")
    private Integer maxCapacity;

    /** 当前入住人数 */
    @Excel(name = "当前入住人数")
    private Integer currentOccupancy;

    public void setRoomId(Long roomId) 
    {
        this.roomId = roomId;
    }

    public Long getRoomId() 
    {
        return roomId;
    }
    
    public void setRoomNumber(String roomNumber) 
    {
        this.roomNumber = roomNumber;
    }

    public String getRoomNumber() 
    {
        return roomNumber;
    }
    
    public void setFloorId(Long floorId) 
    {
        this.floorId = floorId;
    }

    public Long getFloorId() 
    {
        return floorId;
    }
    
    public void setRoomType(String roomType) 
    {
        this.roomType = roomType;
    }

    public String getRoomType() 
    {
        return roomType;
    }
    
    public void setRoomStatus(String roomStatus) 
    {
        this.roomStatus = roomStatus;
    }

    public String getRoomStatus() 
    {
        return roomStatus;
    }
    
    public void setMaxCapacity(Integer maxCapacity) 
    {
        this.maxCapacity = maxCapacity;
    }

    public Integer getMaxCapacity() 
    {
        return maxCapacity;
    }
    
    public void setCurrentOccupancy(Integer currentOccupancy) 
    {
        this.currentOccupancy = currentOccupancy;
    }

    public Integer getCurrentOccupancy() 
    {
        return currentOccupancy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("roomId", getRoomId())
            .append("roomNumber", getRoomNumber())
            .append("floorId", getFloorId())
            .append("roomType", getRoomType())
            .append("roomStatus", getRoomStatus())
            .append("maxCapacity", getMaxCapacity())
            .append("currentOccupancy", getCurrentOccupancy())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}