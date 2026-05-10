package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客户订单汇总视图对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizCustomerOrderSummaryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderCount;

    private BigDecimal totalAmount;

}
