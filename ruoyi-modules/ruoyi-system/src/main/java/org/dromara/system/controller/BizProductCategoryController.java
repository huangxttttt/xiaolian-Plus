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
import org.dromara.system.domain.bo.BizProductCategoryBo;
import org.dromara.system.domain.vo.BizProductCategoryVo;
import org.dromara.system.service.IBizProductCategoryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品大类
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/product/category")
public class BizProductCategoryController extends BaseController {

    private final IBizProductCategoryService bizProductCategoryService;

    /**
     * 查询商品大类列表
     */
    @SaCheckPermission("system:productCategory:list")
    @GetMapping("/list")
    public TableDataInfo<BizProductCategoryVo> list(BizProductCategoryBo bo, PageQuery pageQuery) {
        return bizProductCategoryService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取商品大类下拉选项
     */
    @SaCheckPermission("system:productCategory:list")
    @GetMapping("/options")
    public R<List<BizProductCategoryVo>> options() {
        return R.ok(bizProductCategoryService.queryOptions());
    }

    /**
     * 导出商品大类列表
     */
    @SaCheckPermission("system:productCategory:export")
    @Log(title = "商品大类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizProductCategoryBo bo, HttpServletResponse response) {
        List<BizProductCategoryVo> list = bizProductCategoryService.queryList(bo);
        ExcelUtil.exportExcel(list, "商品大类", BizProductCategoryVo.class, response);
    }

    /**
     * 获取商品大类详细信息
     *
     * @param categoryId 主键
     */
    @SaCheckPermission("system:productCategory:query")
    @GetMapping("/{categoryId}")
    public R<BizProductCategoryVo> getInfo(@NotNull(message = "主键不能为空")
                                           @PathVariable Long categoryId) {
        return R.ok(bizProductCategoryService.queryById(categoryId));
    }

    /**
     * 新增商品大类
     */
    @SaCheckPermission("system:productCategory:add")
    @Log(title = "商品大类", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizProductCategoryBo bo) {
        return toAjax(bizProductCategoryService.insertByBo(bo));
    }

    /**
     * 修改商品大类
     */
    @SaCheckPermission("system:productCategory:edit")
    @Log(title = "商品大类", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizProductCategoryBo bo) {
        return toAjax(bizProductCategoryService.updateByBo(bo));
    }

    /**
     * 删除商品大类
     *
     * @param categoryIds 主键串
     */
    @SaCheckPermission("system:productCategory:remove")
    @Log(title = "商品大类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] categoryIds) {
        return toAjax(bizProductCategoryService.deleteWithValidByIds(List.of(categoryIds), true));
    }
}
