package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 路线管理对象 biz_route
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_route")
public class BizRoute extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 路线ID
     */
    @TableId(value = "route_id")
    private Long routeId;

    /**
     * 路线名称
     */
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
