package org.dromara.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.BizDeliveryArchiveBo;
import org.dromara.system.domain.bo.BizDeliveryOrderBo;
import org.dromara.system.domain.vo.BizDeliveryOrderVo;
import org.dromara.system.service.IBizDeliveryOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配送货单
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/deliveryOrder")
public class BizDeliveryOrderController extends BaseController {

    private final IBizDeliveryOrderService bizDeliveryOrderService;

    @SaCheckPermission("system:deliveryOrder:list")
    @GetMapping("/list")
    public TableDataInfo<BizDeliveryOrderVo> list(BizDeliveryOrderBo bo, PageQuery pageQuery) {
        return bizDeliveryOrderService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("system:deliveryOrder:export")
    @Log(title = "配送货单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizDeliveryOrderBo bo, HttpServletResponse response) {
        List<BizDeliveryOrderVo> list = bizDeliveryOrderService.queryList(bo);
        ExcelUtil.exportExcel(list, "配送货单", BizDeliveryOrderVo.class, response);
    }

    @SaCheckPermission("system:deliveryOrder:query")
    @GetMapping("/{deliveryId}")
    public R<BizDeliveryOrderVo> getInfo(@NotNull(message = "主键不能为空")
                                         @PathVariable Long deliveryId) {
        return R.ok(bizDeliveryOrderService.queryById(deliveryId));
    }

    @SaCheckPermission("system:deliveryOrder:add")
    @Log(title = "配送货单", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizDeliveryOrderBo bo) {
        return toAjax(bizDeliveryOrderService.insertByBo(bo));
    }

    @SaCheckPermission("system:deliveryOrder:edit")
    @Log(title = "配送货单", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizDeliveryOrderBo bo) {
        return toAjax(bizDeliveryOrderService.updateByBo(bo));
    }

    @SaCheckPermission("system:deliveryOrder:edit")
    @Log(title = "配送货单归档", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{deliveryId}/archive")
    public R<Void> archive(@NotNull(message = "主键不能为空")
                           @PathVariable Long deliveryId,
                           @Validated @RequestBody BizDeliveryArchiveBo bo) {
        return toAjax(bizDeliveryOrderService.archiveById(deliveryId, bo));
    }

    @SaCheckPermission("system:deliveryOrder:remove")
    @Log(title = "配送货单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deliveryIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] deliveryIds) {
        return toAjax(bizDeliveryOrderService.deleteWithValidByIds(List.of(deliveryIds), true));
    }
}
