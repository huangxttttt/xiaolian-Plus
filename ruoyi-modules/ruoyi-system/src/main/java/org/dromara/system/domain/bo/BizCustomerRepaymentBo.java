package org.dromara.system.domain.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户订单还款对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizCustomerRepaymentBo {

    @NotNull(message = "还款金额不能为空")
    private BigDecimal amount;
}
