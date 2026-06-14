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
import org.dromara.system.domain.bo.BizCustomerDebtSourceBo;
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
    private final BizCustomerDebtRecordMapper debtRecordMapper;
    private final BizCustomerDebtCarryMapper debtCarryMapper;

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
            customerOrder.setPreviousDebtAmount(BigDecimal.ZERO);
            customerOrder.setRemark(customerOrderBo.getRemark());
            customerOrder.setTotalAmount(BigDecimal.ZERO);
            customerOrder.setReceivableAmount(BigDecimal.ZERO);
            customerOrder.setReceivedAmount(BigDecimal.ZERO);
            customerOrder.setRepaymentAmount(BigDecimal.ZERO);
            customerOrder.setUnpaidAmount(BigDecimal.ZERO);
            customerOrderMapper.insert(customerOrder);

            BigDecimal previousDebtAmount = resolvePreviousDebtAmount(customerOrderBo);
            customerOrder.setPreviousDebtAmount(previousDebtAmount);

            BigDecimal orderTotal = BigDecimal.ZERO;
            List<BizCustomerOrderItemBo> items = customerOrderBo.getItems() == null ? List.of() : customerOrderBo.getItems();
            if (items.isEmpty() && previousDebtAmount.compareTo(BigDecimal.ZERO) == 0) {
                throw new ServiceException(customer.getName() + " 未添加商品或欠款");
            }
            for (BizCustomerOrderItemBo itemBo : items) {
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

            customerOrder.setTotalAmount(orderTotal.add(previousDebtAmount));
            customerOrder.setReceivableAmount(orderTotal.add(previousDebtAmount));
            customerOrderMapper.updateById(customerOrder);
            deliveryTotal = deliveryTotal.add(orderTotal).add(previousDebtAmount);
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

        Map<Long, BizDeliveryArchiveBo.CustomerReceipt> receiptMap = new HashMap<>();
        for (BizDeliveryArchiveBo.CustomerReceipt receipt : bo.getReceipts()) {
            BizDeliveryArchiveBo.CustomerReceipt old = receiptMap.put(receipt.getOrderId(), receipt);
            if (old != null) {
                throw new ServiceException("客户订单收款信息重复");
            }
        }

        Set<Long> orderIds = orders.stream().map(BizCustomerOrder::getOrderId).collect(Collectors.toSet());
        if (!orderIds.equals(receiptMap.keySet())) {
            throw new ServiceException("客户收款信息与配送货单不匹配");
        }

        for (BizCustomerOrder order : orders) {
            BizDeliveryArchiveBo.CustomerReceipt receipt = receiptMap.get(order.getOrderId());
            BigDecimal receivableAmount = receipt.getReceivableAmount();
            BigDecimal receivedAmount = receipt.getReceivedAmount();
            BigDecimal repaymentAmount = receipt.getRepaymentAmount() == null ? BigDecimal.ZERO : receipt.getRepaymentAmount();
            if (receivableAmount == null || receivableAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("应收金额不能小于0");
            }
            if (receivedAmount == null || receivedAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("实收金额不能小于0");
            }
            if (receivedAmount.compareTo(receivableAmount) > 0) {
                throw new ServiceException("实收金额不能大于应收金额");
            }
            if (repaymentAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("还款金额不能小于0");
            }
        }

        boolean archived = baseMapper.update(null, Wrappers.lambdaUpdate(BizDeliveryOrder.class)
            .eq(BizDeliveryOrder::getDeliveryId, deliveryId)
            .eq(BizDeliveryOrder::getStatus, "未归档")
            .set(BizDeliveryOrder::getStatus, "已归档")) > 0;
        if (!archived) {
            throw new ServiceException("配送货单状态已变化，请刷新后重试");
        }
        delivery.setStatus("已归档");

        for (BizCustomerOrder order : orders) {
            BizCustomer customer = customerMapper.selectById(order.getCustomerId());
            if (customer == null) {
                throw new ServiceException("客户不存在");
            }
            BizDeliveryArchiveBo.CustomerReceipt receipt = receiptMap.get(order.getOrderId());
            BigDecimal receivableAmount = receipt.getReceivableAmount();
            BigDecimal receivedAmount = receipt.getReceivedAmount();
            BigDecimal repaymentAmount = receipt.getRepaymentAmount() == null ? BigDecimal.ZERO : receipt.getRepaymentAmount();
            BigDecimal unpaidAmount = receivableAmount.subtract(receivedAmount);
            BigDecimal previousDebtAmount = defaultDecimal(order.getPreviousDebtAmount());
            BigDecimal newDebt = defaultDecimal(customer.getDebt())
                .subtract(previousDebtAmount)
                .subtract(repaymentAmount)
                .add(unpaidAmount);
            customer.setDebt(newDebt.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newDebt);
            customerMapper.updateById(customer);
            order.setReceivableAmount(receivableAmount);
            order.setReceivedAmount(receivedAmount);
            order.setRepaymentAmount(repaymentAmount);
            order.setUnpaidAmount(unpaidAmount);
            customerOrderMapper.updateById(order);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer recalculateArchivedCost(Long deliveryId) {
        BizDeliveryOrder delivery = baseMapper.selectById(deliveryId);
        if (delivery == null) {
            throw new ServiceException("配送货单不存在");
        }
        if (!"已归档".equals(delivery.getStatus())) {
            throw new ServiceException("只有已归档的配送货单才能重算成本");
        }

        List<BizCustomerOrder> orders = customerOrderMapper.selectList(Wrappers.lambdaQuery(BizCustomerOrder.class)
            .eq(BizCustomerOrder::getDeliveryId, deliveryId));
        if (orders.isEmpty()) {
            return 0;
        }

        List<Long> orderIds = orders.stream().map(BizCustomerOrder::getOrderId).toList();
        List<BizCustomerOrderItem> items = itemMapper.selectList(Wrappers.lambdaQuery(BizCustomerOrderItem.class)
            .in(BizCustomerOrderItem::getOrderId, orderIds));
        List<BizCustomerOrderItem> zeroCostItems = items.stream()
            .filter(item -> item.getProductId() != null)
            .filter(item -> item.getCostPrice() == null || item.getCostPrice().compareTo(BigDecimal.ZERO) <= 0)
            .toList();
        if (zeroCostItems.isEmpty()) {
            return 0;
        }

        List<Long> productIds = zeroCostItems.stream()
            .map(BizCustomerOrderItem::getProductId)
            .distinct()
            .toList();
        Map<Long, BizProduct> productMap = productMapper.selectByIds(productIds).stream()
            .collect(Collectors.toMap(BizProduct::getProductId, product -> product));

        int updated = 0;
        for (BizCustomerOrderItem item : zeroCostItems) {
            BizProduct product = productMap.get(item.getProductId());
            if (product == null || product.getLatestCostPrice() == null || product.getLatestCostPrice().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BizCustomerOrderItem update = new BizCustomerOrderItem();
            update.setItemId(item.getItemId());
            update.setCostPrice(product.getLatestCostPrice());
            updated += itemMapper.updateById(update);
        }
        return updated;
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
        Map<Long, List<BizCustomerDebtSourceBo>> debtSourceMap = debtCarryMapper.selectList(Wrappers.lambdaQuery(BizCustomerDebtCarry.class)
                .in(BizCustomerDebtCarry::getTargetOrderId, orderIds))
            .stream()
            .collect(Collectors.groupingBy(BizCustomerDebtCarry::getTargetOrderId, Collectors.mapping(carry -> {
                BizCustomerDebtSourceBo source = new BizCustomerDebtSourceBo();
                source.setRecordId(carry.getRecordId());
                source.setAmount(defaultDecimal(carry.getAmount()));
                return source;
            }, Collectors.toList())));
        customerOrders.forEach(order -> {
            order.setItems(itemMap.getOrDefault(order.getOrderId(), List.of()));
            order.setPreviousDebtSources(debtSourceMap.getOrDefault(order.getOrderId(), List.of()));
        });
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

    private BigDecimal resolvePreviousDebtAmount(BizCustomerOrderBo customerOrderBo) {
        List<BizCustomerDebtSourceBo> sources = customerOrderBo.getPreviousDebtSources() == null ? List.of() : customerOrderBo.getPreviousDebtSources();
        if (sources.isEmpty()) {
            BigDecimal fallbackAmount = customerOrderBo.getPreviousDebtAmount() == null ? BigDecimal.ZERO : customerOrderBo.getPreviousDebtAmount();
            if (fallbackAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("客户欠款金额不能小于0");
            }
            return fallbackAmount;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (BizCustomerDebtSourceBo source : sources) {
            BigDecimal amount = source.getAmount();
            amount = amount == null ? BigDecimal.ZERO : amount;
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("客户欠款金额不能小于0");
            }
            total = total.add(amount);
        }
        return total;
    }

    private void releaseCarriedDebtRecords(Collection<Long> deliveryIds) {
        // 单字段欠款模式下不再维护欠款来源/带入记录。
    }

    private BigDecimal upsertArchivedOrderDebtRecord(BizCustomerOrder order, BizDeliveryOrder delivery, BigDecimal unpaidAmount) {
        return upsertOrderDebtRecord(order, delivery, unpaidAmount);
    }

    private BigDecimal upsertOrderDebtRecord(BizCustomerOrder order, BizDeliveryOrder delivery, BigDecimal sourceAmount) {
        BigDecimal amount = defaultDecimal(sourceAmount);
        BizCustomerDebtRecord record = debtRecordMapper.selectOne(Wrappers.lambdaQuery(BizCustomerDebtRecord.class)
            .eq(BizCustomerDebtRecord::getSourceOrderId, order.getOrderId()));
        if (record == null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (record == null) {
            record = new BizCustomerDebtRecord();
            record.setCustomerId(order.getCustomerId());
            record.setSourceType("ORDER_UNPAID");
            record.setSourceDeliveryId(delivery.getDeliveryId());
            record.setSourceOrderId(order.getOrderId());
            record.setCarriedAmount(BigDecimal.ZERO);
            record.setRemark("订单欠款");
        }
        BigDecimal carriedAmount = record.getRecordId() == null ? BigDecimal.ZERO : debtCarryMapper.selectList(Wrappers.lambdaQuery(BizCustomerDebtCarry.class)
                    .eq(BizCustomerDebtCarry::getRecordId, record.getRecordId()))
                .stream()
                .map(BizCustomerDebtCarry::getAmount)
                .map(this::defaultDecimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal repaidAmount = defaultDecimal(record.getRepaidAmount());
        BigDecimal originalAmount = amount.max(carriedAmount.add(repaidAmount));
        BigDecimal remainingAmount = originalAmount.subtract(carriedAmount).subtract(repaidAmount);
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }
        record.setCustomerId(order.getCustomerId());
        record.setSourceDeliveryId(delivery.getDeliveryId());
        record.setSourceOrderId(order.getOrderId());
        record.setOriginalAmount(originalAmount);
        record.setCarriedAmount(carriedAmount);
        record.setRepaidAmount(repaidAmount);
        record.setRemainingAmount(remainingAmount);
        record.setStatus(resolveDebtStatus(record, delivery, remainingAmount, carriedAmount));
        if (record.getRecordId() == null) {
            debtRecordMapper.insert(record);
        } else {
            debtRecordMapper.updateById(record);
        }
        return remainingAmount;
    }

    private void repayDebtRecords(Long customerId, BigDecimal repaymentAmount, Long currentOrderId) {
        BigDecimal leftAmount = defaultDecimal(repaymentAmount);
        if (customerId == null || leftAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        List<BizCustomerDebtRecord> records = debtRecordMapper.selectList(Wrappers.lambdaQuery(BizCustomerDebtRecord.class)
                .eq(BizCustomerDebtRecord::getCustomerId, customerId)
                .in(BizCustomerDebtRecord::getStatus, List.of("OPEN", "PENDING"))
                .gt(BizCustomerDebtRecord::getRemainingAmount, BigDecimal.ZERO)
                .orderByAsc(BizCustomerDebtRecord::getSourceDeliveryId)
                .orderByAsc(BizCustomerDebtRecord::getRecordId))
            .stream()
            .filter(record -> currentOrderId == null || record.getSourceOrderId() == null || !currentOrderId.equals(record.getSourceOrderId()))
            .toList();
        for (BizCustomerDebtRecord record : records) {
            if (leftAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal remainingAmount = defaultDecimal(record.getRemainingAmount());
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal repayAmount = leftAmount.min(remainingAmount);
            BigDecimal newRemaining = remainingAmount.subtract(repayAmount);
            record.setRepaidAmount(defaultDecimal(record.getRepaidAmount()).add(repayAmount));
            record.setRemainingAmount(newRemaining);
            record.setStatus(newRemaining.compareTo(BigDecimal.ZERO) > 0 ? record.getStatus() : "CLEARED");
            debtRecordMapper.updateById(record);
            leftAmount = leftAmount.subtract(repayAmount);
        }
        if (leftAmount.compareTo(BigDecimal.ZERO) > 0) {
            throw new ServiceException("还款金额大于可还历史欠款金额");
        }
    }

    private BigDecimal sumRepayableDebt(Long customerId, Long currentOrderId) {
        if (customerId == null) {
            return BigDecimal.ZERO;
        }
        return debtRecordMapper.selectList(Wrappers.lambdaQuery(BizCustomerDebtRecord.class)
                .eq(BizCustomerDebtRecord::getCustomerId, customerId)
                .in(BizCustomerDebtRecord::getStatus, List.of("OPEN", "PENDING"))
                .gt(BizCustomerDebtRecord::getRemainingAmount, BigDecimal.ZERO))
            .stream()
            .filter(record -> currentOrderId == null || record.getSourceOrderId() == null || !currentOrderId.equals(record.getSourceOrderId()))
            .map(BizCustomerDebtRecord::getRemainingAmount)
            .map(this::defaultDecimal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String resolveDebtStatus(BizCustomerDebtRecord record, BigDecimal remainingAmount, BigDecimal carriedAmount) {
        if (!"ORDER_UNPAID".equals(record.getSourceType())) {
            return remainingAmount.compareTo(BigDecimal.ZERO) > 0 ? "OPEN" : "CLEARED";
        }
        BizDeliveryOrder delivery = record.getSourceDeliveryId() == null ? null : baseMapper.selectById(record.getSourceDeliveryId());
        return resolveDebtStatus(record, delivery, remainingAmount, carriedAmount);
    }

    private String resolveDebtStatus(BizCustomerDebtRecord record, BizDeliveryOrder delivery, BigDecimal remainingAmount, BigDecimal carriedAmount) {
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            return delivery != null && "已归档".equals(delivery.getStatus()) ? "OPEN" : "PENDING";
        }
        return defaultDecimal(carriedAmount).compareTo(BigDecimal.ZERO) > 0 ? "CARRIED" : "CLEARED";
    }

    private BizDeliveryOrder buildDebtDelivery(Long deliveryId, String status) {
        BizDeliveryOrder delivery = new BizDeliveryOrder();
        delivery.setDeliveryId(deliveryId);
        delivery.setStatus(status);
        return delivery;
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
        customerMapper.updateById(customer);
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void deleteChildren(Collection<Long> deliveryIds) {
        List<BizCustomerOrder> orders = customerOrderMapper.selectList(Wrappers.lambdaQuery(BizCustomerOrder.class)
            .in(BizCustomerOrder::getDeliveryId, deliveryIds));
        if (!orders.isEmpty()) {
            List<Long> orderIds = orders.stream().map(BizCustomerOrder::getOrderId).toList();
            deleteSourceDebtRecords(orderIds);
            itemMapper.delete(Wrappers.lambdaQuery(BizCustomerOrderItem.class).in(BizCustomerOrderItem::getOrderId, orderIds));
            customerOrderMapper.deleteByIds(orderIds);
        }
    }

    private void deleteSourceDebtRecords(Collection<Long> sourceOrderIds) {
        // 单字段欠款模式下不再维护欠款来源/带入记录。
    }
}
