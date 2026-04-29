package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.BizProduct;

import java.math.BigDecimal;

/**
 * 商品管理业务对象 biz_product
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizProduct.class, reverseConvertGenerate = false)
public class BizProductBo extends BaseEntity {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空", groups = { EditGroup.class })
    private Long productId;

    /**
     * 大类ID
     */
    @NotNull(message = "商品大类不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long categoryId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空", groups = { AddGroup.class, EditGroup.class })
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
