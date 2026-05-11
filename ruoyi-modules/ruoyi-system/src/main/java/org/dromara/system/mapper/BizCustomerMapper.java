package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.system.domain.BizCustomer;
import org.dromara.system.domain.vo.BizRouteCustomerOrderStatsVo;
import org.dromara.system.domain.vo.BizCustomerVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户档案Mapper接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface BizCustomerMapper extends BaseMapperPlus<BizCustomer, BizCustomerVo> {

    /**
     * 分页查询客户档案列表
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 客户档案列表
     */
    Page<BizCustomerVo> selectCustomerPage(Page<BizCustomerVo> page, @Param(Constants.WRAPPER) Wrapper<BizCustomer> queryWrapper);

    /**
     * 查询客户档案列表
     *
     * @param queryWrapper 查询条件
     * @return 客户档案列表
     */
    List<BizCustomerVo> selectCustomerList(@Param(Constants.WRAPPER) Wrapper<BizCustomer> queryWrapper);

    /**
     * 查询客户档案详情
     *
     * @param customerId 客户ID
     * @return 客户档案
     */
    BizCustomerVo selectCustomerById(Long customerId);

    /**
     * 查询配送地客户订单占比
     *
     * @param routeId 配送地ID
     * @return 客户订单占比列表
     */
    List<BizRouteCustomerOrderStatsVo> selectRouteCustomerOrderStats(Long routeId);
}
