package org.dromara.system.domain.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户订单带入欠款来源对象
 *
 * @author Lion Li
 * @date 2026-05-22
 */
@Data
public class BizCustomerDebtSourceBo {

    @NotNull(message = "欠款来源不能为空")
    private Long recordId;

    private BigDecimal amount;

}
