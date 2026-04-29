package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.BizRoute;

/**
 * 路线管理业务对象 biz_route
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizRoute.class, reverseConvertGenerate = false)
public class BizRouteBo extends BaseEntity {

    /**
     * 路线ID
     */
    @NotNull(message = "路线ID不能为空", groups = { EditGroup.class })
    private Long routeId;

    /**
     * 路线名称
     */
    @NotBlank(message = "路线名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String routeName;

    /**
     * 路线编码
     */
    private String routeCode;

    /**
     * 显示顺序
     */
    private Integer routeSort;

    /**
     * 备注
     */
    private String remark;

}
