package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商品价格记录对象 biz_product_price_record
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_product_price_record")
public class BizProductPriceRecord extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "record_id")
    private Long recordId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 销售金额
     */
    private BigDecimal saleAmount;

    /**
     * 销售成本价格
     */
    private BigDecimal costPrice;

}
