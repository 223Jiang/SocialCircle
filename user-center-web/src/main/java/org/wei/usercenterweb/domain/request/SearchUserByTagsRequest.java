package org.wei.usercenterweb.domain.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * user-center-web
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchUserByTagsRequest extends MPage {
    private List<String> tags;
}
