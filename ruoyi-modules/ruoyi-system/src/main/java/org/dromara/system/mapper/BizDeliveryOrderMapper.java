package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.BizDeliveryOrder;
import org.dromara.system.domain.vo.BizDeliveryOrderVo;

import java.util.List;

/**
 * 配送货单Mapper接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface BizDeliveryOrderMapper extends BaseMapperPlus<BizDeliveryOrder, BizDeliveryOrderVo> {

    Page<BizDeliveryOrderVo> selectDeliveryPage(Page<BizDeliveryOrderVo> page, @Param(Constants.WRAPPER) Wrapper<BizDeliveryOrder> queryWrapper);

    List<BizDeliveryOrderVo> selectDeliveryList(@Param(Constants.WRAPPER) Wrapper<BizDeliveryOrder> queryWrapper);

    BizDeliveryOrderVo selectDeliveryById(Long deliveryId);
}
