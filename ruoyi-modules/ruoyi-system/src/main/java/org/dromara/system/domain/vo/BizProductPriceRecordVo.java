package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.BizProductPriceRecord;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商品价格记录视图对象 biz_product_price_record
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@AutoMapper(target = BizProductPriceRecord.class)
public class BizProductPriceRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
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
