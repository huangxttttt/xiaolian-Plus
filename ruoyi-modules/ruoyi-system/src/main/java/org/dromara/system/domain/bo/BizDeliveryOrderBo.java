package org.dromara.system.domain.bo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * 配送货单业务对象 biz_delivery_order
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizDeliveryOrderBo extends BaseEntity {

    @NotNull(message = "配送货单ID不能为空", groups = { EditGroup.class })
    private Long deliveryId;

    @NotNull(message = "配送日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private LocalDate deliveryDate;

    @NotNull(message = "配送地不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long routeId;

    private String status;

    private String remark;

    @Valid
    @NotEmpty(message = "客户订单不能为空", groups = { AddGroup.class, EditGroup.class })
    private List<BizCustomerOrderBo> customerOrders;

}
