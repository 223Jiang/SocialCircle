package org.wei.usercenterweb.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author JiangWeiWei
 * @TableName 队伍成员表
 */
@TableName(value = "team_member")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamMember implements Serializable {

    private static final long serialVersionUID = 6793635410730187458L;


    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 队伍ID
     */
    @TableField(value = "team_id")
    private String teamId;

    /**
     * 加入队伍时间
     */
    @TableField(value = "join_time")
    private String joinTime;

    /**
     * 是否为队长：0-否，1-是
     */
    @TableField(value = "is_leader")
    private Integer isLeader;

    /**
     * 删除标识：0-未删除，1-已删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;
}