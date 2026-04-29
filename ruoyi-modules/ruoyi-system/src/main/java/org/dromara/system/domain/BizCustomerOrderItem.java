package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 客户订单明细对象 biz_customer_order_item
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_customer_order_item")
public class BizCustomerOrderItem extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "item_id")
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
