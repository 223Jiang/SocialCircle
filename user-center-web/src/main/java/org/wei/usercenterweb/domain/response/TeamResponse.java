package org.wei.usercenterweb.domain.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * user-center-web
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/22
 */
@Data
public class TeamResponse implements Serializable {

    private static final long serialVersionUID = -3517835156850982069L;

    /**
     * 队伍ID
     */
    private String id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 当前人数
     */
    private Integer num;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     * 队伍过期时间
     */
    private String expireTime;

    /**
     * 队长的用户ID
     */
    private String leaderId;

    /**
     * 队长名
     */
    private String leaderName;


    /**
     * 队伍状态：0-公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 队伍创建时间
     */
    private String createTime;

    /**
     * 队伍信息最后更新时间
     */
    private String updateTime;

    /**
     * 队伍用户
     */
    private List<UserInformation> users;
}
