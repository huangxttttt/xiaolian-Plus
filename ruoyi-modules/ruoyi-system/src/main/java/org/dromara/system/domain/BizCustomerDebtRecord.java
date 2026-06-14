package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 客户欠款来源记录对象 biz_customer_debt_record
 *
 * @author Lion Li
 * @date 2026-05-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_customer_debt_record")
public class BizCustomerDebtRecord extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id")
    private Long recordId;

    private Long customerId;

    private String sourceType;

    private Long sourceDeliveryId;

    private Long sourceOrderId;

    private BigDecimal originalAmount;

    private BigDecimal carriedAmount;

    private BigDecimal repaidAmount;

    private BigDecimal remainingAmount;

    private String status;

    private String remark;

}
