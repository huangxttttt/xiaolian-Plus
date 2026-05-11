package org.dromara.system.service;

import org.dromara.system.domain.vo.BizCustomerVo;
import org.dromara.system.domain.vo.BizCustomerOrderSummaryVo;
import org.dromara.system.domain.vo.BizCustomerOrderVo;
import org.dromara.system.domain.vo.BizCustomerTopProductVo;
import org.dromara.system.domain.vo.BizRouteCustomerOrderStatsVo;
import org.dromara.system.domain.bo.BizCustomerBo;
import org.dromara.system.domain.bo.BizCustomerQueryBo;
import org.dromara.system.domain.bo.BizCustomerRepaymentBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * 客户档案Service接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface IBizCustomerService {

    /**
     * 查询客户档案
     *
     * @param customerId 主键
     * @return 客户档案
     */
    BizCustomerVo queryById(Long customerId);

    /**
     * 分页查询客户档案列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 客户档案分页列表
     */
    TableDataInfo<BizCustomerVo> queryPageList(BizCustomerQueryBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的客户档案列表
     *
     * @param bo 查询条件
     * @return 客户档案列表
     */
    List<BizCustomerVo> queryList(BizCustomerQueryBo bo);

    /**
     * 查询客户订单记录
     *
     * @param customerId 客户ID
     * @return 客户订单记录
     */
    TableDataInfo<BizCustomerOrderVo> queryCustomerOrders(Long customerId, LocalDate beginDate, LocalDate endDate, PageQuery pageQuery);

    /**
     * 查询客户订单汇总
     *
     * @param customerId 客户ID
     * @return 客户订单汇总
     */
    BizCustomerOrderSummaryVo queryCustomerOrderSummary(Long customerId, LocalDate beginDate, LocalDate endDate);

    /**
     * 查询客户常购商品排行
     *
     * @param customerId 客户ID
     * @return 客户常购商品排行
     */
    List<BizCustomerTopProductVo> queryCustomerTopProducts(Long customerId);

    /**
     * 查询配送地客户订单占比
     *
     * @param routeId 配送地ID
     * @return 客户订单占比列表
     */
    List<BizRouteCustomerOrderStatsVo> queryRouteCustomerOrderStats(Long routeId);

    /**
     * 客户订单还款
     *
     * @param orderId 客户订单ID
     * @param bo 还款信息
     * @return 是否还款成功
     */
    Boolean repayCustomerOrder(Long orderId, BizCustomerRepaymentBo bo);

    /**
     * 新增客户档案
     *
     * @param bo 客户档案
     * @return 是否新增成功
     */
    Boolean insertByBo(BizCustomerBo bo);

    /**
     * 修改客户档案
     *
     * @param bo 客户档案
     * @return 是否修改成功
     */
    Boolean updateByBo(BizCustomerBo bo);

    /**
     * 校验并批量删除客户档案信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
