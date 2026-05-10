package org.dromara.system.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.system.domain.BizCustomerOrder;
import org.dromara.system.domain.vo.BizCustomerOrderSummaryVo;
import org.dromara.system.domain.vo.BizCustomerOrderVo;

import java.time.LocalDate;
import java.util.List;

/**
 * 客户订单Mapper接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface BizCustomerOrderMapper extends BaseMapperPlus<BizCustomerOrder, BizCustomerOrderVo> {

    List<BizCustomerOrderVo> selectByDeliveryId(Long deliveryId);

    Page<BizCustomerOrderVo> selectPageByCustomerId(Page<BizCustomerOrderVo> page,
                                                    @Param("customerId") Long customerId,
                                                    @Param("beginDate") LocalDate beginDate,
                                                    @Param("endDate") LocalDate endDate);

    BizCustomerOrderSummaryVo selectSummaryByCustomerId(@Param("customerId") Long customerId,
                                                        @Param("beginDate") LocalDate beginDate,
                                                        @Param("endDate") LocalDate endDate);
}
