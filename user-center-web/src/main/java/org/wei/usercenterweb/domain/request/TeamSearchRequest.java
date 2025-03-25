package org.wei.usercenterweb.domain.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author JiangWeiWei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamSearchRequest extends MPage {
    @ApiModelProperty("队伍名称关键词")
    private String nameKeyword;

    @ApiModelProperty("队伍状态（0-公开，1-私有，2-加密）")
    private Integer status;

    @ApiModelProperty("成员数量")
    private Integer members;

    @ApiModelProperty("是否仅查询可加入的队伍（未过期且未满员）")
    private Boolean joinable;
}