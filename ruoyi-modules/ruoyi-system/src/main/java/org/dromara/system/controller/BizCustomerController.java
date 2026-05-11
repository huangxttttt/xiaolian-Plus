package org.dromara.system.controller;

import java.util.List;
import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.system.domain.vo.BizCustomerVo;
import org.dromara.system.domain.vo.BizCustomerOrderSummaryVo;
import org.dromara.system.domain.vo.BizCustomerOrderVo;
import org.dromara.system.domain.vo.BizCustomerTopProductVo;
import org.dromara.system.domain.vo.BizRouteCustomerOrderStatsVo;
import org.dromara.system.domain.bo.BizCustomerBo;
import org.dromara.system.domain.bo.BizCustomerQueryBo;
import org.dromara.system.domain.bo.BizCustomerRepaymentBo;
import org.dromara.system.service.IBizCustomerService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 客户档案
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/customer")
public class BizCustomerController extends BaseController {

    private final IBizCustomerService bizCustomerService;

    /**
     * 查询客户档案列表
     */
    @SaCheckPermission("system:customer:list")
    @GetMapping("/list")
    public TableDataInfo<BizCustomerVo> list(BizCustomerQueryBo bo, PageQuery pageQuery) {
        return bizCustomerService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出客户档案列表
     */
    @SaCheckPermission("system:customer:export")
    @Log(title = "客户档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizCustomerQueryBo bo, HttpServletResponse response) {
        List<BizCustomerVo> list = bizCustomerService.queryList(bo);
        ExcelUtil.exportExcel(list, "客户档案", BizCustomerVo.class, response);
    }

    /**
     * 获取客户档案详细信息
     *
     * @param customerId 主键
     */
    @SaCheckPermission("system:customer:query")
    @GetMapping("/{customerId}")
    public R<BizCustomerVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long customerId) {
        return R.ok(bizCustomerService.queryById(customerId));
    }

    /**
     * 获取客户订单记录
     *
     * @param customerId 客户ID
     */
    @SaCheckPermission("system:customer:query")
    @GetMapping("/{customerId}/orders")
    public TableDataInfo<BizCustomerOrderVo> getOrders(@NotNull(message = "主键不能为空")
                                                       @PathVariable Long customerId,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                       PageQuery pageQuery) {
        return bizCustomerService.queryCustomerOrders(customerId, beginDate, endDate, pageQuery);
    }

    /**
     * 获取客户订单汇总
     *
     * @param customerId 客户ID
     */
    @SaCheckPermission("system:customer:query")
    @GetMapping("/{customerId}/orders/summary")
    public R<BizCustomerOrderSummaryVo> getOrderSummary(@NotNull(message = "主键不能为空")
                                                        @PathVariable Long customerId,
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return R.ok(bizCustomerService.queryCustomerOrderSummary(customerId, beginDate, endDate));
    }

    /**
     * 获取客户常购商品排行
     *
     * @param customerId 客户ID
     */
    @SaCheckPermission("system:customer:query")
    @GetMapping("/{customerId}/top-products")
    public R<List<BizCustomerTopProductVo>> getTopProducts(@NotNull(message = "主键不能为空")
                                                           @PathVariable Long customerId) {
        return R.ok(bizCustomerService.queryCustomerTopProducts(customerId));
    }

    /**
     * 获取配送地客户订单占比
     *
     * @param routeId 配送地ID
     */
    @SaCheckPermission("system:customer:query")
    @GetMapping("/route/{routeId}/order-stats")
    public R<List<BizRouteCustomerOrderStatsVo>> getRouteOrderStats(@NotNull(message = "主键不能为空")
                                                                    @PathVariable Long routeId) {
        return R.ok(bizCustomerService.queryRouteCustomerOrderStats(routeId));
    }

    /**
     * 客户订单还款
     *
     * @param orderId 客户订单ID
     */
    @SaCheckPermission("system:customer:edit")
    @Log(title = "客户订单还款", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/orders/{orderId}/repay")
    public R<Void> repayOrder(@NotNull(message = "主键不能为空")
                              @PathVariable Long orderId,
                              @Validated @RequestBody BizCustomerRepaymentBo bo) {
        return toAjax(bizCustomerService.repayCustomerOrder(orderId, bo));
    }

    /**
     * 新增客户档案
     */
    @SaCheckPermission("system:customer:add")
    @Log(title = "客户档案", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizCustomerBo bo) {
        return toAjax(bizCustomerService.insertByBo(bo));
    }

    /**
     * 修改客户档案
     */
    @SaCheckPermission("system:customer:edit")
    @Log(title = "客户档案", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizCustomerBo bo) {
        return toAjax(bizCustomerService.updateByBo(bo));
    }

    /**
     * 删除客户档案
     *
     * @param customerIds 主键串
     */
    @SaCheckPermission("system:customer:remove")
    @Log(title = "客户档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{customerIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] customerIds) {
        return toAjax(bizCustomerService.deleteWithValidByIds(List.of(customerIds), true));
    }
}
