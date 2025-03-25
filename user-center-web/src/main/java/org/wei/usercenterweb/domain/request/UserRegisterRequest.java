package org.wei.usercenterweb.domain.request;

import lombok.Data;

/**
 * user-center-web
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/5
 */
@Data
public class UserRegisterRequest {
    private String userCount;

    private String userPassword;

    private String userCheckPassword;
}
