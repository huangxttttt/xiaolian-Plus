package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 首页盈利概览视图对象
 *
 * @author Lion Li
 * @date 2026-04-30
 */
@Data
public class BizDashboardProfitMetricVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private BigDecimal salesAmount = BigDecimal.ZERO;

    private BigDecimal costAmount = BigDecimal.ZERO;

    private BigDecimal profitAmount = BigDecimal.ZERO;

    private BigDecimal profitRate = BigDecimal.ZERO;
}
