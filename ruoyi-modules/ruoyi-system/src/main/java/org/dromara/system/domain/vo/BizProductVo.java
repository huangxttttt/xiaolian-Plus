package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.BizProduct;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品管理视图对象 biz_product
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizProduct.class)
public class BizProductVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 大类ID
     */
    private Long categoryId;

    /**
     * 大类名称
     */
    @ExcelProperty(value = "商品大类")
    private String categoryName;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称")
    private String productName;

    /**
     * 商品规格
     */
    @ExcelProperty(value = "商品规格")
    private String specification;

    /**
     * 商品提供商
     */
    @ExcelProperty(value = "商品提供商")
    private String supplier;

    /**
     * 最近销售金额
     */
    @ExcelProperty(value = "最近销售金额")
    private BigDecimal latestSaleAmount;

    /**
     * 销售成本价格
     */
    @ExcelProperty(value = "销售成本价格")
    private BigDecimal latestCostPrice;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

}
