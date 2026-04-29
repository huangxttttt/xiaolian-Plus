package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.BizProductCategory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 商品大类视图对象 biz_product_category
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizProductCategory.class)
public class BizProductCategoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 大类ID
     */
    private Long categoryId;

    /**
     * 大类名称
     */
    @ExcelProperty(value = "大类名称")
    private String categoryName;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序")
    private Integer categorySort;

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
