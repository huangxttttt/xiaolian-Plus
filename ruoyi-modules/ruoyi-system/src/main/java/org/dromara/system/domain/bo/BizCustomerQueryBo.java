package org.dromara.system.domain.bo;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户档案查询对象 biz_customer
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizCustomerQueryBo extends BaseEntity {

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

}
