package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 客户欠款带入记录对象 biz_customer_debt_carry
 *
 * @author Lion Li
 * @date 2026-05-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_customer_debt_carry")
public class BizCustomerDebtCarry extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "carry_id")
    private Long carryId;

    private Long recordId;

    private Long customerId;

    private Long targetOrderId;

    private BigDecimal amount;

    private String remark;

}
