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
import org.dromara.system.domain.bo.BizProductBo;
import org.dromara.system.domain.bo.BizProductPriceRecordBo;
import org.dromara.system.domain.vo.BizProductPriceRecordVo;
import org.dromara.system.domain.vo.BizProductVo;
import org.dromara.system.service.IBizProductService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/product")
public class BizProductController extends BaseController {

    private final IBizProductService bizProductService;

    /**
     * 查询商品管理列表
     */
    @SaCheckPermission("system:product:list")
    @GetMapping("/list")
    public TableDataInfo<BizProductVo> list(BizProductBo bo, PageQuery pageQuery) {
        return bizProductService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询商品价格历史
     */
    @SaCheckPermission("system:product:query")
    @GetMapping("/priceHistory/{productId}")
    public R<List<BizProductPriceRecordVo>> priceHistory(@NotNull(message = "主键不能为空")
                                                         @PathVariable Long productId,
                                                         BizProductPriceRecordBo bo) {
        bo.setProductId(productId);
        return R.ok(bizProductService.queryPriceHistory(bo));
    }

    /**
     * 导出商品管理列表
     */
    @SaCheckPermission("system:product:export")
    @Log(title = "商品管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizProductBo bo, HttpServletResponse response) {
        List<BizProductVo> list = bizProductService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品管理", BizProductVo.class, response);
    }

    /**
     * 获取商品管理详细信息
     *
     * @param productId 主键
     */
    @SaCheckPermission("system:product:query")
    @GetMapping("/{productId}")
    public R<BizProductVo> getInfo(@NotNull(message = "主键不能为空")
                                   @PathVariable Long productId) {
        return R.ok(bizProductService.queryById(productId));
    }

    /**
     * 新增商品管理
     */
    @SaCheckPermission("system:product:add")
    @Log(title = "商品管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizProductBo bo) {
        return toAjax(bizProductService.insertByBo(bo));
    }

    /**
     * 修改商品管理
     */
    @SaCheckPermission("system:product:edit")
    @Log(title = "商品管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizProductBo bo) {
        return toAjax(bizProductService.updateByBo(bo));
    }

    /**
     * 删除商品管理
     *
     * @param productIds 主键串
     */
    @SaCheckPermission("system:product:remove")
    @Log(title = "商品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] productIds) {
        return toAjax(bizProductService.deleteWithValidByIds(List.of(productIds), true));
    }
}
