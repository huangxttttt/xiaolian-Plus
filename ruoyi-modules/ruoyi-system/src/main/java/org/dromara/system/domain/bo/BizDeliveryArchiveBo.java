package org.dromara.system.domain.bo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 配送货单归档收款对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizDeliveryArchiveBo {

    @Valid
    @NotEmpty(message = "客户收款信息不能为空")
    private List<CustomerReceipt> receipts;

    @Data
    public static class CustomerReceipt {

        @NotNull(message = "客户订单ID不能为空")
        private Long orderId;

        @NotNull(message = "实收金额不能为空")
        private BigDecimal receivedAmount;
    }
}
