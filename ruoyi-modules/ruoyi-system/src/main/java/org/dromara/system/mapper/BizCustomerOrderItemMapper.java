package org.dromara.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.BizCustomerOrderItem;
import org.dromara.system.domain.vo.BizCustomerOrderItemVo;

import java.util.Collection;
import java.util.List;

/**
 * 客户订单明细Mapper接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface BizCustomerOrderItemMapper extends BaseMapperPlus<BizCustomerOrderItem, BizCustomerOrderItemVo> {

    List<BizCustomerOrderItemVo> selectByOrderIds(@Param("orderIds") Collection<Long> orderIds);
}
