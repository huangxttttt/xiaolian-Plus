package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 首页趋势点视图对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizDashboardTrendPointVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String label;

    private BigDecimal amount = BigDecimal.ZERO;
}
