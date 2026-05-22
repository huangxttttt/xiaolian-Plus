package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 客户可带入欠款视图对象
 *
 * @author Lion Li
 * @date 2026-05-22
 */
@Data
public class BizCustomerDebtRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long recordId;

    private Long customerId;

    private String sourceType;

    private Long sourceDeliveryId;

    private Long sourceOrderId;

    private LocalDate sourceDeliveryDate;

    private String sourceRouteName;

    private BigDecimal originalAmount;

    private BigDecimal carriedAmount;

    private BigDecimal remainingAmount;

    private String status;

    private String remark;

}
