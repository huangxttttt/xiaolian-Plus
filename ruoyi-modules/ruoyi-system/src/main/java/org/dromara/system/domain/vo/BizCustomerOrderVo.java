package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 客户订单视图对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizCustomerOrderVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderId;

    private Long deliveryId;

    private LocalDate deliveryDate;

    private String routeName;

    private String deliveryStatus;

    private Long customerId;

    private String customerName;

    private String customerAlias;

    private String customerPhone;

    private BigDecimal totalAmount;

    private BigDecimal receivedAmount;

    private BigDecimal unpaidAmount;

    private String remark;

    private List<BizCustomerOrderItemVo> items;

}
