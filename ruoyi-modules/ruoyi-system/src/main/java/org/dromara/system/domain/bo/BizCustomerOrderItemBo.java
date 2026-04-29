package org.dromara.system.domain.bo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;

import java.math.BigDecimal;

/**
 * 客户订单明细业务对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizCustomerOrderItemBo {

    private Long itemId;

    @NotNull(message = "商品不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productId;

    @NotNull(message = "数量不能为空", groups = { AddGroup.class, EditGroup.class })
    @DecimalMin(value = "0.01", message = "数量必须大于0", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal quantity;

    @NotNull(message = "销售价格不能为空", groups = { AddGroup.class, EditGroup.class })
    @DecimalMin(value = "0.00", message = "销售价格不能小于0", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal salePrice;

    @DecimalMin(value = "0.00", message = "成本价格不能小于0", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal costPrice;

    private String remark;

}
