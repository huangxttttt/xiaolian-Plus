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
import org.dromara.system.domain.bo.BizCustomerRepaymentBo;
import org.dromara.system.domain.vo.BizCustomerVo;
import org.dromara.system.domain.vo.BizCustomerOrderItemVo;
import org.dromara.system.domain.vo.BizCustomerOrderSummaryVo;
import org.dromara.system.domain.vo.BizCustomerOrderVo;
import org.dromara.system.domain.vo.BizCustomerTopProductVo;
import org.dromara.system.domain.vo.BizCustomerDebtRecordVo;
import org.dromara.system.domain.vo.BizRouteCustomerOrderStatsVo;
import org.dromara.system.domain.BizCustomer;
import org.dromara.system.domain.BizCustomerOrder;
import org.dromara.system.domain.BizCustomerDebtCarry;
import org.dromara.system.domain.BizCustomerDebtRecord;
import org.dromara.system.domain.BizDeliveryOrder;
import org.dromara.system.mapper.BizCustomerDebtCarryMapper;
import org.dromara.system.mapper.BizCustomerDebtRecordMapper;
import org.dromara.system.mapper.BizCustomerOrderItemMapper;
import org.dromara.system.mapper.BizCustomerOrderMapper;
import org.dromara.system.mapper.BizCustomerMapper;
import org.dromara.system.mapper.BizDeliveryOrderMapper;
import org.dromara.system.mapper.BizRouteMapper;
import org.dromara.system.service.IBizCustomerService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
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
    private final BizDeliveryOrderMapper deliveryOrderMapper;
    private final BizCustomerDebtRecordMapper debtRecordMapper;
    private final BizCustomerDebtCarryMapper debtCarryMapper;

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
    public TableDataInfo<BizCustomerOrderVo> queryCustomerOrders(Long customerId, LocalDate beginDate, LocalDate endDate, PageQuery pageQuery) {
        if (baseMapper.selectById(customerId) == null) {
            throw new ServiceException("客户不存在");
        }
        Page<BizCustomerOrderVo> result = customerOrderMapper.selectPageByCustomerId(pageQuery.build(), customerId, beginDate, endDate);
        List<BizCustomerOrderVo> orders = result.getRecords();
        if (orders.isEmpty()) {
            return TableDataInfo.build(result);
        }
        List<Long> orderIds = orders.stream().map(BizCustomerOrderVo::getOrderId).toList();
        Map<Long, List<BizCustomerOrderItemVo>> itemMap = itemMapper.selectByOrderIds(orderIds).stream()
            .collect(Collectors.groupingBy(BizCustomerOrderItemVo::getOrderId));
        orders.forEach(order -> order.setItems(itemMap.getOrDefault(order.getOrderId(), List.of())));
        return TableDataInfo.build(result);
    }

    @Override
    public BizCustomerOrderSummaryVo queryCustomerOrderSummary(Long customerId, LocalDate beginDate, LocalDate endDate) {
        if (baseMapper.selectById(customerId) == null) {
            throw new ServiceException("客户不存在");
        }
        return customerOrderMapper.selectSummaryByCustomerId(customerId, beginDate, endDate);
    }

    @Override
    public List<BizCustomerTopProductVo> queryCustomerTopProducts(Long customerId) {
        if (baseMapper.selectById(customerId) == null) {
            throw new ServiceException("客户不存在");
        }
        return itemMapper.selectTopProductsByCustomerId(customerId, 5);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BizCustomerDebtRecordVo> queryAvailableDebtRecords(Long customerId, Long targetOrderId) {
        BizCustomer customer = baseMapper.selectById(customerId);
        if (customer == null) {
            throw new ServiceException("客户不存在");
        }
        return List.of();
    }

    @Override
    public List<BizRouteCustomerOrderStatsVo> queryRouteCustomerOrderStats(Long routeId) {
        if (routeMapper.selectById(routeId) == null) {
            throw new ServiceException("配送线路不存在");
        }
        return baseMapper.selectRouteCustomerOrderStats(routeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean repayCustomerOrder(Long orderId, BizCustomerRepaymentBo bo) {
        BizCustomerOrder order = customerOrderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("客户订单不存在");
        }
        BigDecimal repaymentAmount = bo.getAmount();
        if (repaymentAmount == null || repaymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("还款金额必须大于0");
        }
        BigDecimal unpaidAmount = order.getUnpaidAmount() == null ? BigDecimal.ZERO : order.getUnpaidAmount();
        if (unpaidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("该订单没有欠款");
        }
        if (repaymentAmount.compareTo(unpaidAmount) > 0) {
            throw new ServiceException("还款金额不能大于该单欠款金额");
        }

        BizCustomer customer = baseMapper.selectById(order.getCustomerId());
        if (customer == null) {
            throw new ServiceException("客户不存在");
        }

        BigDecimal receivedAmount = order.getReceivedAmount() == null ? BigDecimal.ZERO : order.getReceivedAmount();
        order.setReceivedAmount(receivedAmount.add(repaymentAmount));
        BigDecimal newUnpaidAmount = unpaidAmount.subtract(repaymentAmount);
        order.setUnpaidAmount(newUnpaidAmount);
        customerOrderMapper.updateById(order);

        refreshCustomerDebtFromUnpaidOrders(customer.getCustomerId());
        return true;
    }

    private void refreshCustomerDebtFromUnpaidOrders(Long customerId) {
        BigDecimal actualDebt = customerOrderMapper.selectList(Wrappers.lambdaQuery(BizCustomerOrder.class)
                .eq(BizCustomerOrder::getCustomerId, customerId)
                .gt(BizCustomerOrder::getUnpaidAmount, BigDecimal.ZERO))
            .stream()
            .map(BizCustomerOrder::getUnpaidAmount)
            .map(this::defaultDecimal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        baseMapper.update(null, Wrappers.lambdaUpdate(BizCustomer.class)
            .eq(BizCustomer::getCustomerId, customerId)
            .set(BizCustomer::getDebt, actualDebt));
    }

    private BigDecimal syncDebtRecords(BizCustomer customer, Long targetOrderId) {
        List<BizCustomerOrder> orders = customerOrderMapper.selectList(Wrappers.lambdaQuery(BizCustomerOrder.class)
            .eq(BizCustomerOrder::getCustomerId, customer.getCustomerId()));
        if (orders.isEmpty()) {
            syncManualDebtRecord(customer, BigDecimal.ZERO);
            return BigDecimal.ZERO;
        }

        List<Long> deliveryIds = orders.stream()
            .map(BizCustomerOrder::getDeliveryId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        Map<Long, BizDeliveryOrder> deliveryMap = deliveryIds.isEmpty() ? Map.of() : deliveryOrderMapper.selectByIds(deliveryIds).stream()
            .collect(Collectors.toMap(BizDeliveryOrder::getDeliveryId, item -> item));

        for (BizCustomerOrder order : orders) {
            if (targetOrderId != null && targetOrderId.equals(order.getOrderId())) {
                continue;
            }
            BizDeliveryOrder delivery = deliveryMap.get(order.getDeliveryId());
            if (delivery == null) {
                continue;
            }
            BigDecimal sourceAmount = "已归档".equals(delivery.getStatus())
                ? defaultDecimal(order.getUnpaidAmount())
                : defaultDecimal(order.getTotalAmount());
            upsertOrderDebtRecord(customer.getCustomerId(), order, delivery, sourceAmount);
        }

        BigDecimal formalRemaining = debtRecordMapper.selectList(Wrappers.lambdaQuery(BizCustomerDebtRecord.class)
                .eq(BizCustomerDebtRecord::getCustomerId, customer.getCustomerId())
                .eq(BizCustomerDebtRecord::getSourceType, "ORDER_UNPAID"))
            .stream()
            .map(BizCustomerDebtRecord::getRemainingAmount)
            .map(this::defaultDecimal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        syncManualDebtRecord(customer, formalRemaining);
        return formalRemaining;
    }

    private void syncManualDebtRecord(BizCustomer customer, BigDecimal formalRemaining) {
        BigDecimal manualAmount = defaultDecimal(customer.getDebt()).subtract(defaultDecimal(formalRemaining));
        if (manualAmount.compareTo(BigDecimal.ZERO) < 0) {
            manualAmount = BigDecimal.ZERO;
        }
        BizCustomerDebtRecord record = debtRecordMapper.selectOne(Wrappers.lambdaQuery(BizCustomerDebtRecord.class)
            .eq(BizCustomerDebtRecord::getCustomerId, customer.getCustomerId())
            .eq(BizCustomerDebtRecord::getSourceType, "MANUAL"));
        if (record == null && manualAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        if (record == null) {
            record = new BizCustomerDebtRecord();
            record.setCustomerId(customer.getCustomerId());
            record.setSourceType("MANUAL");
            record.setCarriedAmount(BigDecimal.ZERO);
            record.setRemark("客户档案手工欠款");
        }
        BigDecimal carriedAmount = sumCarriedAmount(record.getRecordId());
        BigDecimal repaidAmount = defaultDecimal(record.getRepaidAmount());
        record.setOriginalAmount(manualAmount.add(carriedAmount).add(repaidAmount));
        record.setCarriedAmount(carriedAmount);
        record.setRepaidAmount(repaidAmount);
        record.setRemainingAmount(manualAmount);
        record.setStatus(manualAmount.compareTo(BigDecimal.ZERO) > 0 ? "OPEN" : "CLEARED");
        if (record.getRecordId() == null) {
            debtRecordMapper.insert(record);
        } else {
            debtRecordMapper.updateById(record);
        }
    }

    private void upsertOrderDebtRecord(Long customerId, BizCustomerOrder order, BizDeliveryOrder delivery, BigDecimal sourceAmount) {
        sourceAmount = defaultDecimal(sourceAmount);
        BizCustomerDebtRecord record = debtRecordMapper.selectOne(Wrappers.lambdaQuery(BizCustomerDebtRecord.class)
            .eq(BizCustomerDebtRecord::getSourceOrderId, order.getOrderId()));
        if (record == null && sourceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        if (record == null) {
            record = new BizCustomerDebtRecord();
            record.setCustomerId(customerId);
            record.setSourceType("ORDER_UNPAID");
            record.setSourceOrderId(order.getOrderId());
            record.setSourceDeliveryId(order.getDeliveryId());
            record.setCarriedAmount(BigDecimal.ZERO);
            record.setRemark("订单欠款");
        }
        BigDecimal carriedAmount = sumCarriedAmount(record.getRecordId());
        BigDecimal repaidAmount = defaultDecimal(record.getRepaidAmount());
        BigDecimal originalAmount = sourceAmount.max(carriedAmount.add(repaidAmount));
        BigDecimal remainingAmount = originalAmount.subtract(carriedAmount).subtract(repaidAmount);
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }
        record.setOriginalAmount(originalAmount);
        record.setCarriedAmount(carriedAmount);
        record.setRepaidAmount(repaidAmount);
        record.setRemainingAmount(remainingAmount);
        record.setStatus(resolveOrderDebtStatus(record, delivery, remainingAmount, carriedAmount));
        if (record.getRecordId() == null) {
            debtRecordMapper.insert(record);
        } else {
            debtRecordMapper.updateById(record);
        }
    }

    private BizCustomerDebtRecord getOrCreateOrderDebtRecord(BizCustomerOrder order, BigDecimal sourceAmount) {
        BizCustomerDebtRecord record = debtRecordMapper.selectOne(Wrappers.lambdaQuery(BizCustomerDebtRecord.class)
            .eq(BizCustomerDebtRecord::getSourceOrderId, order.getOrderId()));
        if (record != null) {
            return record;
        }
        BizDeliveryOrder delivery = deliveryOrderMapper.selectById(order.getDeliveryId());
        if (delivery == null) {
            throw new ServiceException("配送货单不存在");
        }
        record = new BizCustomerDebtRecord();
        record.setCustomerId(order.getCustomerId());
        record.setSourceType("ORDER_UNPAID");
        record.setSourceDeliveryId(order.getDeliveryId());
        record.setSourceOrderId(order.getOrderId());
        record.setOriginalAmount(sourceAmount);
        record.setCarriedAmount(BigDecimal.ZERO);
        record.setRepaidAmount(BigDecimal.ZERO);
        record.setRemainingAmount(sourceAmount);
        record.setStatus("已归档".equals(delivery.getStatus()) ? "OPEN" : "PENDING");
        record.setRemark("订单欠款");
        debtRecordMapper.insert(record);
        return record;
    }

    private String resolveOrderDebtStatus(BizCustomerDebtRecord record, BigDecimal remainingAmount, BigDecimal carriedAmount) {
        if (!"ORDER_UNPAID".equals(record.getSourceType())) {
            return remainingAmount.compareTo(BigDecimal.ZERO) > 0 ? "OPEN" : "CLEARED";
        }
        BizDeliveryOrder delivery = record.getSourceDeliveryId() == null ? null : deliveryOrderMapper.selectById(record.getSourceDeliveryId());
        return resolveOrderDebtStatus(record, delivery, remainingAmount, carriedAmount);
    }

    private String resolveOrderDebtStatus(BizCustomerDebtRecord record, BizDeliveryOrder delivery, BigDecimal remainingAmount, BigDecimal carriedAmount) {
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            return delivery != null && "已归档".equals(delivery.getStatus()) ? "OPEN" : "PENDING";
        }
        return defaultDecimal(carriedAmount).compareTo(BigDecimal.ZERO) > 0 ? "CARRIED" : "CLEARED";
    }

    private void refreshCustomerDebtFromRecords(Long customerId) {
        BigDecimal debt = debtRecordMapper.selectList(Wrappers.lambdaQuery(BizCustomerDebtRecord.class)
                .eq(BizCustomerDebtRecord::getCustomerId, customerId)
                .ne(BizCustomerDebtRecord::getStatus, "CLEARED"))
            .stream()
            .map(BizCustomerDebtRecord::getRemainingAmount)
            .map(this::defaultDecimal)
            .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BizCustomer customer = new BizCustomer();
        customer.setCustomerId(customerId);
        customer.setDebt(debt);
        baseMapper.updateById(customer);
    }

    private BigDecimal sumCarriedAmount(Long recordId) {
        if (recordId == null) {
            return BigDecimal.ZERO;
        }
        return debtCarryMapper.selectList(Wrappers.lambdaQuery(BizCustomerDebtCarry.class)
                .eq(BizCustomerDebtCarry::getRecordId, recordId))
            .stream()
            .map(BizCustomerDebtCarry::getAmount)
            .map(this::defaultDecimal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private LambdaQueryWrapper<BizCustomer> buildQueryWrapper(BizCustomerQueryBo bo) {
        LambdaQueryWrapper<BizCustomer> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizCustomer::getCustomerId);
        lqw.and(StringUtils.isNotBlank(bo.getKeyword()), wrapper -> wrapper
            .like(BizCustomer::getName, bo.getKeyword())
            .or()
            .like(BizCustomer::getAlias, bo.getKeyword())
            .or()
            .like(BizCustomer::getPhone, bo.getKeyword())
            .or()
            .like(BizCustomer::getContactName, bo.getKeyword()));
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
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(BizCustomerBo bo) {
        normalizeDebt(bo);
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
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(BizCustomerBo bo) {
        normalizeDebt(bo);
        BizCustomer update = MapstructUtils.convert(bo, BizCustomer.class);
        validEntityBeforeSave(update);
        int updatedRows = baseMapper.updateById(update);
        int locationUpdatedRows = baseMapper.update(null, Wrappers.lambdaUpdate(BizCustomer.class)
            .eq(BizCustomer::getCustomerId, bo.getCustomerId())
            .set(BizCustomer::getMapLocation, bo.getMapLocation())
            .set(BizCustomer::getLongitude, bo.getLongitude())
            .set(BizCustomer::getLatitude, bo.getLatitude())
            .set(BizCustomer::getMapAddress, bo.getMapAddress())
            .set(BizCustomer::getMapProvider, bo.getMapProvider()));
        return updatedRows > 0 || locationUpdatedRows > 0;
    }

    private void normalizeDebt(BizCustomerBo bo) {
        if (bo.getDebt() == null) {
            bo.setDebt(BigDecimal.ZERO);
        }
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizCustomer entity){
        if (entity.getRouteId() != null && routeMapper.selectById(entity.getRouteId()) == null) {
            throw new ServiceException("配送线路不存在");
        }
        if (StringUtils.isNotBlank(entity.getName())) {
            LambdaQueryWrapper<BizCustomer> lqw = Wrappers.lambdaQuery();
            lqw.eq(BizCustomer::getName, entity.getName());
            lqw.eq(entity.getRouteId() != null, BizCustomer::getRouteId, entity.getRouteId());
            lqw.isNull(entity.getRouteId() == null, BizCustomer::getRouteId);
            lqw.ne(entity.getCustomerId() != null, BizCustomer::getCustomerId, entity.getCustomerId());
            if (baseMapper.selectCount(lqw) > 0) {
                throw new ServiceException("同一配送线路下客户名称已存在");
            }
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
