package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 配送地客户订单占比视图对象
 *
 * @author Lion Li
 * @date 2026-05-11
 */
@Data
public class BizRouteCustomerOrderStatsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long customerId;

    private String customerName;

    private Long orderCount;

    private Long routeOrderCount;

    private BigDecimal percentage;

}
