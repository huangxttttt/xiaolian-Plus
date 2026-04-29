package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.BizProductCategory;

/**
 * 商品大类业务对象 biz_product_category
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizProductCategory.class, reverseConvertGenerate = false)
public class BizProductCategoryBo extends BaseEntity {

    /**
     * 大类ID
     */
    @NotNull(message = "大类ID不能为空", groups = { EditGroup.class })
    private Long categoryId;

    /**
     * 大类名称
     */
    @NotBlank(message = "大类名称不能为空", groups = { AddGroup.class, EditGroup.class })
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
