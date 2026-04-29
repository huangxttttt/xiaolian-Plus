package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.BizProductPriceRecord;

import java.time.LocalDate;

/**
 * 商品价格记录查询对象 biz_product_price_record
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@AutoMapper(target = BizProductPriceRecord.class, reverseConvertGenerate = false)
public class BizProductPriceRecordBo {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 开始日期
     */
    private LocalDate beginDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

}
