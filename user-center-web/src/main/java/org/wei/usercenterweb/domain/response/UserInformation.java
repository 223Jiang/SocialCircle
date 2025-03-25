package org.wei.usercenterweb.domain.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 过滤后的非敏感用户信息
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/5
 */
@Data
public class UserInformation implements Serializable {
    private static final long serialVersionUID = 8575429328933029326L;

    /**
     * id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 账户
     */
    private String userCount;

    /**
     * 邮箱
     */
    private String userEmail;

    /**
     * 用户标签
     */
    private String tags;

    /**
     * 描述
     */
    private String userDescription;

    /**
     * 性别 (0-男，1-女)
     */
    private Integer sex;

    /**
     * 手机号
     */
    private String userPhone;

    /**
     * 用户头像
     */
    private String imageUrl;

    /**
     * 账户状态(0-正常，1-封禁)
     */
    private Integer userStatus;


    /**
     * 是否为管理员（0-普通用户，1-管理员）
     */
    private Integer isAdmin;

    /**
     * 创建时间
     */
    private String createTime;
}
