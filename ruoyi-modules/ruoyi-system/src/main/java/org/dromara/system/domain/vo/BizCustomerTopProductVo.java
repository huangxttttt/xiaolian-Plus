package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客户常购商品排行视图对象
 *
 * @author Lion Li
 * @date 2026-05-10
 */
@Data
public class BizCustomerTopProductVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long productId;

    private String productName;

    private String specification;

    private String supplier;

    private BigDecimal totalQuantity;

    private BigDecimal totalAmount;

    private Long orderCount;

}
