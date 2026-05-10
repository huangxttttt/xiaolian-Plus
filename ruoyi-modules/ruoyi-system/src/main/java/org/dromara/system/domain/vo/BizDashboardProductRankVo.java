package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 首页商品排行视图对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizDashboardProductRankVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String productName;

    private String specification;

    private Long orderCount;

    private BigDecimal quantity;

    private BigDecimal amount;

    private BigDecimal percent;
}
