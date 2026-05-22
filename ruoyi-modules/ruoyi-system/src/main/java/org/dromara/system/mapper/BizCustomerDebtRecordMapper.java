package org.dromara.system.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.BizCustomerDebtRecord;
import org.dromara.system.domain.vo.BizCustomerDebtRecordVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户欠款来源记录Mapper接口
 *
 * @author Lion Li
 * @date 2026-05-22
 */
public interface BizCustomerDebtRecordMapper extends BaseMapperPlus<BizCustomerDebtRecord, BizCustomerDebtRecordVo> {

    List<BizCustomerDebtRecordVo> selectAvailableByCustomerId(@Param("customerId") Long customerId, @Param("targetOrderId") Long targetOrderId);

}
