package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.BizRoute;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 路线管理视图对象 biz_route
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizRoute.class)
public class BizRouteVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 路线ID
     */
    private Long routeId;

    /**
     * 路线名称
     */
    @ExcelProperty(value = "路线名称")
    private String routeName;

    /**
     * 路线编码
     */
    @ExcelProperty(value = "路线编码")
    private String routeCode;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序")
    private Integer routeSort;

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
