package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 首页今日下单视图对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizDashboardTodayOrderVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long customerCount = 0L;

    private Long orderCount = 0L;
}
