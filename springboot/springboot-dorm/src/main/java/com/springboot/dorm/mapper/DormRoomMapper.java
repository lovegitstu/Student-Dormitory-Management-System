package com.springboot.dorm.mapper;

import java.util.List;
import java.util.Map;
import com.springboot.dorm.domain.DormRoom;

/**
 * 宿舍房间Mapper接口
 * 
 * 
 * @date 2025-09-16
 */
public interface DormRoomMapper 
{
    /**
     * 查询宿舍房间
     * 
     * @param roomId 宿舍房间主键
     * @return 宿舍房间
     */
    public DormRoom selectDormRoomByRoomId(Long roomId);

    /**
     * 查询宿舍房间列表
     * 
     * @param dormRoom 宿舍房间
     * @return 宿舍房间集合
     */
    public List<DormRoom> selectDormRoomList(DormRoom dormRoom);

    /**
     * 新增宿舍房间
     * 
     * @param dormRoom 宿舍房间
     * @return 结果
     */
    public int insertDormRoom(DormRoom dormRoom);

    /**
     * 修改宿舍房间
     * 
     * @param dormRoom 宿舍房间
     * @return 结果
     */
    public int updateDormRoom(DormRoom dormRoom);

    /**
     * 删除宿舍房间
     * 
     * @param roomId 宿舍房间主键
     * @return 结果
     */
    public int deleteDormRoomByRoomId(Long roomId);

    /**
     * 批量删除宿舍房间
     * 
     * @param roomIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDormRoomByRoomIds(Long[] roomIds);

    /**
     * 统计房间总数
     * 
     * @return 房间总数
     */
    int countTotalRooms();

    /**
     * 统计指定楼层的房间数量
     * 
     * @param floorIds 楼层ID列表
     * @return 房间数量
     */
    int countByFloorIds(List<Long> floorIds);

    /**
     * 根据房间类型查询入住率统计
     * 
     * @return 入住率统计
     */
    List<Map<String, Object>> selectOccupancyByRoomType();
}