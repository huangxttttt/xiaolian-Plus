package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 首页客户排行视图对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizDashboardCustomerRankVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String customerName;

    private String routeName;

    private Long orderCount;

    private BigDecimal amount;

    private BigDecimal percent;
}
