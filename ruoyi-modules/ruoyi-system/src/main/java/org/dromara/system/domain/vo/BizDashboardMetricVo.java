package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页指标视图对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizDashboardMetricVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private BigDecimal amount = BigDecimal.ZERO;

    private BigDecimal previousAmount = BigDecimal.ZERO;

    private BigDecimal archivedAmount = BigDecimal.ZERO;

    private BigDecimal unarchivedAmount = BigDecimal.ZERO;

    private Long orderCount = 0L;

    private Long customerCount = 0L;

    private List<BizDashboardTrendPointVo> trend = new ArrayList<>();
}
