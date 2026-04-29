package org.dromara.system.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.BizCustomerOrder;
import org.dromara.system.domain.vo.BizCustomerOrderVo;

import java.util.List;

/**
 * 客户订单Mapper接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface BizCustomerOrderMapper extends BaseMapperPlus<BizCustomerOrder, BizCustomerOrderVo> {

    List<BizCustomerOrderVo> selectByDeliveryId(Long deliveryId);

    List<BizCustomerOrderVo> selectByCustomerId(Long customerId);
}
