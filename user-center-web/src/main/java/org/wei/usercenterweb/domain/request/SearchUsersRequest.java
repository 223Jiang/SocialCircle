package org.wei.usercenterweb.domain.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * user-center-web
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/7
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchUsersRequest extends MPage {
    private Integer userStatus;

    private String userName;

    private String isAdmin;

    private String startTime;

    private String endTime;
}
