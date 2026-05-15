package org.dromara.system.domain.bo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户订单业务对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizCustomerOrderBo {

    private Long orderId;

    @NotNull(message = "客户不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long customerId;

    private BigDecimal previousDebtAmount;

    private String remark;

    @Valid
    private List<BizCustomerOrderItemBo> items;

}
