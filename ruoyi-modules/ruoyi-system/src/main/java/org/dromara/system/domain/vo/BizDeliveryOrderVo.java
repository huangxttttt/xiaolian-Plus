package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 配送货单视图对象 biz_delivery_order
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@ExcelIgnoreUnannotated
public class BizDeliveryOrderVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long deliveryId;

    @ExcelProperty(value = "配送日期")
    private LocalDate deliveryDate;

    private Long routeId;

    @ExcelProperty(value = "配送地")
    private String routeName;

    @ExcelProperty(value = "订单总金额")
    private BigDecimal totalAmount;

    @ExcelProperty(value = "状态")
    private String status;

    @ExcelProperty(value = "创建时间")
    private Date createTime;

    @ExcelProperty(value = "备注")
    private String remark;

    private List<BizCustomerOrderVo> customerOrders;

}
