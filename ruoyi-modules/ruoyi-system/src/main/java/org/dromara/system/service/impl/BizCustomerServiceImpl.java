package org.dromara.system.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.BizCustomerBo;
import org.dromara.system.domain.bo.BizCustomerQueryBo;
import org.dromara.system.domain.vo.BizCustomerVo;
import org.dromara.system.domain.vo.BizCustomerOrderItemVo;
import org.dromara.system.domain.vo.BizCustomerOrderVo;
import org.dromara.system.domain.BizCustomer;
import org.dromara.system.mapper.BizCustomerOrderItemMapper;
import org.dromara.system.mapper.BizCustomerOrderMapper;
import org.dromara.system.mapper.BizCustomerMapper;
import org.dromara.system.mapper.BizRouteMapper;
import org.dromara.system.service.IBizCustomerService;

import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户档案Service业务层处理
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizCustomerServiceImpl implements IBizCustomerService {

    private final BizCustomerMapper baseMapper;
    private final BizRouteMapper routeMapper;
    private final BizCustomerOrderMapper customerOrderMapper;
    private final BizCustomerOrderItemMapper itemMapper;

    /**
     * 查询客户档案
     *
     * @param customerId 主键
     * @return 客户档案
     */
    @Override
    public BizCustomerVo queryById(Long customerId){
        return baseMapper.selectCustomerById(customerId);
    }

    /**
     * 分页查询客户档案列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 客户档案分页列表
     */
    @Override
    public TableDataInfo<BizCustomerVo> queryPageList(BizCustomerQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizCustomer> lqw = buildQueryWrapper(bo);
        Page<BizCustomerVo> result = baseMapper.selectCustomerPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的客户档案列表
     *
     * @param bo 查询条件
     * @return 客户档案列表
     */
    @Override
    public List<BizCustomerVo> queryList(BizCustomerQueryBo bo) {
        LambdaQueryWrapper<BizCustomer> lqw = buildQueryWrapper(bo);
        return baseMapper.selectCustomerList(lqw);
    }

    @Override
    public List<BizCustomerOrderVo> queryCustomerOrders(Long customerId) {
        if (baseMapper.selectById(customerId) == null) {
            throw new ServiceException("客户不存在");
        }
        List<BizCustomerOrderVo> orders = customerOrderMapper.selectByCustomerId(customerId);
        if (orders.isEmpty()) {
            return orders;
        }
        List<Long> orderIds = orders.stream().map(BizCustomerOrderVo::getOrderId).toList();
        Map<Long, List<BizCustomerOrderItemVo>> itemMap = itemMapper.selectByOrderIds(orderIds).stream()
            .collect(Collectors.groupingBy(BizCustomerOrderItemVo::getOrderId));
        orders.forEach(order -> order.setItems(itemMap.getOrDefault(order.getOrderId(), List.of())));
        return orders;
    }

    private LambdaQueryWrapper<BizCustomer> buildQueryWrapper(BizCustomerQueryBo bo) {
        LambdaQueryWrapper<BizCustomer> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizCustomer::getCustomerId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), BizCustomer::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getAlias()), BizCustomer::getAlias, bo.getAlias());
        lqw.eq(StringUtils.isNotBlank(bo.getPhone()), BizCustomer::getPhone, bo.getPhone());
        lqw.like(StringUtils.isNotBlank(bo.getContactName()), BizCustomer::getContactName, bo.getContactName());
        lqw.eq(bo.getRouteId() != null, BizCustomer::getRouteId, bo.getRouteId());
        return lqw;
    }

    /**
     * 新增客户档案
     *
     * @param bo 客户档案
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizCustomerBo bo) {
        BizCustomer add = MapstructUtils.convert(bo, BizCustomer.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setCustomerId(add.getCustomerId());
        }
        return flag;
    }

    /**
     * 修改客户档案
     *
     * @param bo 客户档案
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizCustomerBo bo) {
        BizCustomer update = MapstructUtils.convert(bo, BizCustomer.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizCustomer entity){
        if (entity.getRouteId() != null && routeMapper.selectById(entity.getRouteId()) == null) {
            throw new ServiceException("配送线路不存在");
        }
    }

    /**
     * 校验并批量删除客户档案信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
