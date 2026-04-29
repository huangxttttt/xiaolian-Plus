package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客户订单明细视图对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizCustomerOrderItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long itemId;

    private Long orderId;

    private Long productId;

    private String productName;

    private String specification;

    private String supplier;

    private BigDecimal quantity;

    private BigDecimal salePrice;

    private BigDecimal costPrice;

    private BigDecimal amount;

    private String remark;

}
