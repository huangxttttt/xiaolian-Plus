package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 首页毛利排行视图对象
 *
 * @author Lion Li
 * @date 2026-04-30
 */
@Data
public class BizDashboardProfitRankVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    private String subName;

    private Long orderCount;

    private BigDecimal quantity;

    private BigDecimal salesAmount;

    private BigDecimal costAmount;

    private BigDecimal profitAmount;

    private BigDecimal profitRate;

    private BigDecimal percent;
}
