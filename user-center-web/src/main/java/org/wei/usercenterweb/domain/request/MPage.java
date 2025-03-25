package org.wei.usercenterweb.domain.request;

import lombok.Data;

/**
 * user-center-web
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/22
 */
@Data
public class MPage {
    private Integer current = 1;

    private Integer pageSize = 10;
}
