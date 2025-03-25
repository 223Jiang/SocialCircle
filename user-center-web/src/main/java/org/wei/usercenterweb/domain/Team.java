package org.wei.usercenterweb.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author JiangWeiWei
 * @TableName 队伍表
 */
@TableName(value = "team")
@Data
public class Team implements Serializable {

    private static final long serialVersionUID = 5012751344730369616L;

    /**
     * 队伍ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 队伍名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 队伍描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 当前人数
     */
    @TableField(value = "num")
    private Integer num;

    /**
     * 队伍最大人数
     */
    @TableField(value = "max_num")
    private Integer maxNum;

    /**
     * 队伍过期时间
     */
    @TableField(value = "expire_time")
    private String expireTime;

    /**
     * 队长的用户ID
     */
    @TableField(value = "leader_id")
    private String leaderId;

    /**
     * 队伍状态：0-公开，1-私有，2-加密，3-过期
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 队伍密码（加密类型使用）
     */
    @TableField(value = "password")
    private String password;

    /**
     * 队伍创建时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 队伍信息最后更新时间
     */
    @TableField(value = "update_time")
    private String updateTime;

    /**
     * 删除标识：0-未删除，1-已删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;
}