package org.wei.usercenterweb.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author JiangWeiWei
 * @TableName 用户表
 */
@TableName(value = "user")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 6561717631145254526L;

    /**
     * id
     */
    @TableId
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
     * 密码
     */
    private String userPassword;

    /**
     * 邮箱
     */
    private String userEmail;

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
     * 用户标签
     */
    private String tags;

    /**
     * 描述
     */
    private String userDescription;

    /**
     * 账户状态(0-正常，1-封禁)
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 删除状态(0-未删除，1-删除)
     */
    private Integer isDelete;

    /**
     * 是否为管理员 (0-普通用户，1-管理员)
     */
    private Integer isAdmin;

    @TableField(exist = false)
    private Double similarity;
}