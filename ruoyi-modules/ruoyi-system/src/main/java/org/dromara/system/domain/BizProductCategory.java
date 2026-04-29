package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 商品大类对象 biz_product_category
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_product_category")
public class BizProductCategory extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 大类ID
     */
    @TableId(value = "category_id")
    private Long categoryId;

    /**
     * 大类名称
     */
    private String categoryName;

    /**
     * 显示顺序
     */
    private Integer categorySort;

    /**
     * 备注
     */
    private String remark;

}
