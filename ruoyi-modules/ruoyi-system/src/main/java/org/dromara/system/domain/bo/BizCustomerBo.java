package org.dromara.system.domain.bo;

import org.dromara.system.domain.BizCustomer;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 客户档案业务对象 biz_customer
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizCustomer.class, reverseConvertGenerate = false)
public class BizCustomerBo extends BaseEntity {

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空", groups = { EditGroup.class })
    private Long customerId;

    /**
     * 客户名称
     */
    @NotBlank(message = "客户名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 客户简称
     */
    private String alias;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空", groups = { AddGroup.class, EditGroup.class })
    private String phone;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 配送线路
     */
    @NotNull(message = "配送线路不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long routeId;

    /**
     * 欠款金额
     */
    private BigDecimal debt;

    /**
     * 地图定位
     */
    private String mapLocation;

    /**
     * 地图经度
     */
    private BigDecimal longitude;

    /**
     * 地图纬度
     */
    private BigDecimal latitude;

    /**
     * 地图解析地址
     */
    private String mapAddress;

    /**
     * 地图服务商
     */
    private String mapProvider;

    /**
     * 门面照片
     */
    private String photo;

    /**
     * 备注
     */
    private String remark;


}
