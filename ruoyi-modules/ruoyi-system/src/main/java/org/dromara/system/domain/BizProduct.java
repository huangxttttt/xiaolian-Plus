package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 商品管理对象 biz_product
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_product")
public class BizProduct extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId(value = "product_id")
    private Long productId;

    /**
     * 大类ID
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品规格
     */
    private String specification;

    /**
     * 商品提供商
     */
    private String supplier;

    /**
     * 最近销售金额
     */
    private BigDecimal latestSaleAmount;

    /**
     * 销售成本价格
     */
    private BigDecimal latestCostPrice;

    /**
     * 备注
     */
    private String remark;

}
