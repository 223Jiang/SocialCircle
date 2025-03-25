package org.wei.usercenterweb.domain.request;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * @author JiangWeiWei
 */
@TableName(value = "team")
@Data
public class TeamCreateRequest {

    private String teamId;

    /**
     * 队伍名称
     */
    @NotBlank(message = "队伍名称不能为空")
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍最大人数
     */
    @NotNull(message = "最大人数不能为空")
    @Min(value = 1, message = "队伍人数至少为1")
    @Max(value = 15, message = "队伍人数不能超过15")
    private Integer maxNum;

    /**
     * 队伍过期时间
     */
    @NotBlank(message = "过期时间不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "时间格式应为 yyyy-MM-dd HH:mm:ss")
    private String expireTime;

    /**
     * 队伍状态：0-公开，1-私有，2-加密
     */
    @NotNull(message = "队伍状态不能为空")
    private Integer status;

    /**
     * 队伍密码（加密类型使用）
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Min(value = 6, message = "密码长度需在6-15位之间")
    @Max(value = 15, message = "密码长度需在6-15位之间")
    private String password;
}