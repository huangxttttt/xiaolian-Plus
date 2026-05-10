package org.dromara.system.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 客户档案对象 biz_customer
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_customer")
public class BizCustomer extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 客户ID
     */
    @TableId(value = "customer_id")
    private Long customerId;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 客户简称
     */
    private String alias;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 配送线路
     */
    private Long routeId;

    /**
     * 欠款金额
     */
    private BigDecimal debt;

    /**
     * 客户位置
     */
    private String address;

    /**
     * 地图定位
     */
    private String mapLocation;

    /**
     * 常用商品
     */
    private String commonProducts;

    /**
     * 门面照片
     */
    private String photo;

    /**
     * 备注
     */
    private String remark;


}
