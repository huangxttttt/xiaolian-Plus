package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.*;
import org.dromara.system.domain.bo.BizDeliveryArchiveBo;
import org.dromara.system.domain.bo.BizCustomerOrderBo;
import org.dromara.system.domain.bo.BizCustomerOrderItemBo;
import org.dromara.system.domain.bo.BizDeliveryOrderBo;
import org.dromara.system.domain.vo.BizCustomerOrderItemVo;
import org.dromara.system.domain.vo.BizCustomerOrderVo;
import org.dromara.system.domain.vo.BizDeliveryOrderVo;
import org.dromara.system.mapper.*;
import org.dromara.system.service.IBizDeliveryOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 配送货单Service业务层处理
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizDeliveryOrderServiceImpl implements IBizDeliveryOrderService {

    private final BizDeliveryOrderMapper baseMapper;
    private final BizCustomerOrderMapper customerOrderMapper;
    private final BizCustomerOrderItemMapper itemMapper;
    private final BizRouteMapper routeMapper;
    private final BizCustomerMapper customerMapper;
    private final BizProductMapper productMapper;
    private final BizProductPriceRecordMapper priceRecordMapper;

    @Override
    public BizDeliveryOrderVo queryById(Long deliveryId) {
        BizDeliveryOrderVo vo = baseMapper.selectDeliveryById(deliveryId);
        if (vo == null) {
            return null;
        }
        fillChildren(vo);
        return vo;
    }

    @Override
    public TableDataInfo<BizDeliveryOrderVo> queryPageList(BizDeliveryOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizDeliveryOrder> lqw = buildQueryWrapper(bo);
        Page<BizDeliveryOrderVo> result = baseMapper.selectDeliveryPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<BizDeliveryOrderVo> queryList(BizDeliveryOrderBo bo) {
        LambdaQueryWrapper<BizDeliveryOrder> lqw = buildQueryWrapper(bo);
        return baseMapper.selectDeliveryList(lqw);
    }

    private LambdaQueryWrapper<BizDeliveryOrder> buildQueryWrapper(BizDeliveryOrderBo bo) {
        LambdaQueryWrapper<BizDeliveryOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getRouteId() != null, BizDeliveryOrder::getRouteId, bo.getRouteId());
        lqw.eq(bo.getDeliveryDate() != null, BizDeliveryOrder::getDeliveryDate, bo.getDeliveryDate());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizDeliveryOrder::getStatus, bo.getStatus());
        lqw.orderByDesc(BizDeliveryOrder::getDeliveryDate).orderByDesc(BizDeliveryOrder::getDeliveryId);
        return lqw;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(BizDeliveryOrderBo bo) {
        validHeader(bo);
        BizDeliveryOrder delivery = buildDelivery(bo, "未归档");
        baseMapper.insert(delivery);
        bo.setDeliveryId(delivery.getDeliveryId());
        BigDecimal totalAmount = saveCustomerOrders(delivery.getDeliveryId(), bo);
        delivery.setTotalAmount(totalAmount);
        return baseMapper.updateById(delivery) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(BizDeliveryOrderBo bo) {
        validHeader(bo);
        BizDeliveryOrder old = baseMapper.selectById(bo.getDeliveryId());
        if (old == null) {
            throw new ServiceException("配送货单不存在");
        }
        if ("已归档".equals(old.getStatus())) {
            throw new ServiceException("已归档的配送货单不能修改");
        }
        deleteChildren(List.of(bo.getDeliveryId()));
        BigDecimal totalAmount = saveCustomerOrders(bo.getDeliveryId(), bo);
        BizDeliveryOrder delivery = buildDelivery(bo, StringUtils.blankToDefault(old.getStatus(), "未归档"));
        delivery.setDeliveryId(bo.getDeliveryId());
        delivery.setTotalAmount(totalAmount);
        return baseMapper.updateById(delivery) > 0;
    }

    private BizDeliveryOrder buildDelivery(BizDeliveryOrderBo bo, String status) {
        BizDeliveryOrder delivery = new BizDeliveryOrder();
        delivery.setDeliveryId(bo.getDeliveryId());
        delivery.setDeliveryDate(bo.getDeliveryDate());
        delivery.setRouteId(bo.getRouteId());
        delivery.setStatus(status);
        delivery.setRemark(bo.getRemark());
        delivery.setTotalAmount(BigDecimal.ZERO);
        return delivery;
    }

    private void validHeader(BizDeliveryOrderBo bo) {
        if (routeMapper.selectById(bo.getRouteId()) == null) {
            throw new ServiceException("配送地不存在");
        }
    }

    private BigDecimal saveCustomerOrders(Long deliveryId, BizDeliveryOrderBo bo) {
        BigDecimal deliveryTotal = BigDecimal.ZERO;
        Set<Long> customerIds = new HashSet<>();
        for (BizCustomerOrderBo customerOrderBo : bo.getCustomerOrders()) {
            if (!customerIds.add(customerOrderBo.getCustomerId())) {
                throw new ServiceException("同一次配送中客户不能重复");
            }
            BizCustomer customer = customerMapper.selectById(customerOrderBo.getCustomerId());
            if (customer == null) {
                throw new ServiceException("客户不存在");
            }

            BizCustomerOrder customerOrder = new BizCustomerOrder();
            customerOrder.setDeliveryId(deliveryId);
            customerOrder.setCustomerId(customerOrderBo.getCustomerId());
            customerOrder.setRemark(customerOrderBo.getRemark());
            customerOrder.setTotalAmount(BigDecimal.ZERO);
            customerOrder.setReceivedAmount(BigDecimal.ZERO);
            customerOrder.setUnpaidAmount(BigDecimal.ZERO);
            customerOrderMapper.insert(customerOrder);

            BigDecimal orderTotal = BigDecimal.ZERO;
            for (BizCustomerOrderItemBo itemBo : customerOrderBo.getItems()) {
                BizProduct product = productMapper.selectById(itemBo.getProductId());
                if (product == null) {
                    throw new ServiceException("商品不存在");
                }
                BigDecimal salePrice = itemBo.getSalePrice();
                BigDecimal costPrice = itemBo.getCostPrice() == null ? product.getLatestCostPrice() : itemBo.getCostPrice();
                BigDecimal amount = itemBo.getQuantity().multiply(salePrice);
                BizCustomerOrderItem item = new BizCustomerOrderItem();
                item.setOrderId(customerOrder.getOrderId());
                item.setProductId(product.getProductId());
                item.setProductName(product.getProductName());
                item.setSpecification(product.getSpecification());
                item.setSupplier(product.getSupplier());
                item.setQuantity(itemBo.getQuantity());
                item.setSalePrice(salePrice);
                item.setCostPrice(costPrice);
                item.setAmount(amount);
                item.setRemark(itemBo.getRemark());
                itemMapper.insert(item);

                orderTotal = orderTotal.add(amount);
                syncProductPrice(product, salePrice, costPrice, bo.getDeliveryDate());
            }

            customerOrder.setTotalAmount(orderTotal);
            customerOrderMapper.updateById(customerOrder);
            deliveryTotal = deliveryTotal.add(orderTotal);
        }
        return deliveryTotal;
    }

    private void syncProductPrice(BizProduct product, BigDecimal salePrice, BigDecimal costPrice, LocalDate recordDate) {
        product.setLatestSaleAmount(salePrice);
        product.setLatestCostPrice(costPrice);
        productMapper.updateById(product);

        BizProductPriceRecord record = priceRecordMapper.selectOne(Wrappers.lambdaQuery(BizProductPriceRecord.class)
            .eq(BizProductPriceRecord::getProductId, product.getProductId())
            .eq(BizProductPriceRecord::getRecordDate, recordDate));
        if (record == null) {
            record = new BizProductPriceRecord();
            record.setProductId(product.getProductId());
            record.setRecordDate(recordDate);
            record.setSaleAmount(salePrice);
            record.setCostPrice(costPrice);
            priceRecordMapper.insert(record);
            return;
        }
        record.setSaleAmount(salePrice);
        record.setCostPrice(costPrice);
        priceRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean archiveById(Long deliveryId, BizDeliveryArchiveBo bo) {
        BizDeliveryOrder delivery = baseMapper.selectById(deliveryId);
        if (delivery == null) {
            throw new ServiceException("配送货单不存在");
        }
        if ("已归档".equals(delivery.getStatus())) {
            throw new ServiceException("配送货单已归档");
        }

        List<BizCustomerOrder> orders = customerOrderMapper.selectList(Wrappers.lambdaQuery(BizCustomerOrder.class)
            .eq(BizCustomerOrder::getDeliveryId, deliveryId));
        if (orders.isEmpty()) {
            throw new ServiceException("配送货单没有客户订单，不能归档");
        }

        Map<Long, BigDecimal> receiptMap = new HashMap<>();
        for (BizDeliveryArchiveBo.CustomerReceipt receipt : bo.getReceipts()) {
            BigDecimal old = receiptMap.put(receipt.getOrderId(), receipt.getReceivedAmount());
            if (old != null) {
                throw new ServiceException("客户订单收款信息重复");
            }
        }

        Set<Long> orderIds = orders.stream().map(BizCustomerOrder::getOrderId).collect(Collectors.toSet());
        if (!orderIds.equals(receiptMap.keySet())) {
            throw new ServiceException("客户收款信息与配送货单不匹配");
        }

        for (BizCustomerOrder order : orders) {
            BigDecimal orderAmount = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
            BigDecimal receivedAmount = receiptMap.get(order.getOrderId());
            if (receivedAmount == null || receivedAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("实收金额不能小于0");
            }
            if (receivedAmount.compareTo(orderAmount) > 0) {
                throw new ServiceException("实收金额不能大于订单金额");
            }
        }

        boolean archived = baseMapper.update(null, Wrappers.lambdaUpdate(BizDeliveryOrder.class)
            .eq(BizDeliveryOrder::getDeliveryId, deliveryId)
            .eq(BizDeliveryOrder::getStatus, "未归档")
            .set(BizDeliveryOrder::getStatus, "已归档")) > 0;
        if (!archived) {
            throw new ServiceException("配送货单状态已变化，请刷新后重试");
        }

        for (BizCustomerOrder order : orders) {
            BizCustomer customer = customerMapper.selectById(order.getCustomerId());
            if (customer == null) {
                throw new ServiceException("客户不存在");
            }
            BigDecimal currentDebt = customer.getDebt() == null ? BigDecimal.ZERO : customer.getDebt();
            BigDecimal orderAmount = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
            BigDecimal receivedAmount = receiptMap.get(order.getOrderId());
            BigDecimal unpaidAmount = orderAmount.subtract(receivedAmount);
            order.setReceivedAmount(receivedAmount);
            order.setUnpaidAmount(unpaidAmount);
            customerOrderMapper.updateById(order);
            customer.setDebt(currentDebt.add(unpaidAmount));
            customerMapper.updateById(customer);
        }
        return true;
    }

    private void fillChildren(BizDeliveryOrderVo vo) {
        List<BizCustomerOrderVo> customerOrders = customerOrderMapper.selectByDeliveryId(vo.getDeliveryId());
        if (customerOrders.isEmpty()) {
            vo.setCustomerOrders(customerOrders);
            return;
        }
        List<Long> orderIds = customerOrders.stream().map(BizCustomerOrderVo::getOrderId).toList();
        Map<Long, List<BizCustomerOrderItemVo>> itemMap = itemMapper.selectByOrderIds(orderIds).stream()
            .collect(Collectors.groupingBy(BizCustomerOrderItemVo::getOrderId));
        customerOrders.forEach(order -> order.setItems(itemMap.getOrDefault(order.getOrderId(), List.of())));
        vo.setCustomerOrders(customerOrders);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        long archivedCount = baseMapper.selectCount(Wrappers.lambdaQuery(BizDeliveryOrder.class)
            .in(BizDeliveryOrder::getDeliveryId, ids)
            .eq(BizDeliveryOrder::getStatus, "已归档"));
        if (archivedCount > 0) {
            throw new ServiceException("已归档的配送货单不能删除");
        }
        deleteChildren(ids);
        return baseMapper.deleteByIds(ids) > 0;
    }

    private void deleteChildren(Collection<Long> deliveryIds) {
        List<BizCustomerOrder> orders = customerOrderMapper.selectList(Wrappers.lambdaQuery(BizCustomerOrder.class)
            .in(BizCustomerOrder::getDeliveryId, deliveryIds));
        if (!orders.isEmpty()) {
            List<Long> orderIds = orders.stream().map(BizCustomerOrder::getOrderId).toList();
            itemMapper.delete(Wrappers.lambdaQuery(BizCustomerOrderItem.class).in(BizCustomerOrderItem::getOrderId, orderIds));
            customerOrderMapper.deleteByIds(orderIds);
        }
    }
}
