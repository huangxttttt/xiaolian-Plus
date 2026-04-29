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
import org.dromara.system.domain.bo.BizRouteBo;
import org.dromara.system.domain.vo.BizRouteVo;
import org.dromara.system.service.IBizRouteService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路线管理
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/route")
public class BizRouteController extends BaseController {

    private final IBizRouteService bizRouteService;

    /**
     * 查询路线管理列表
     */
    @SaCheckPermission("system:route:list")
    @GetMapping("/list")
    public TableDataInfo<BizRouteVo> list(BizRouteBo bo, PageQuery pageQuery) {
        return bizRouteService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取路线下拉选项
     */
    @SaCheckPermission("system:route:list")
    @GetMapping("/options")
    public R<List<BizRouteVo>> options() {
        return R.ok(bizRouteService.queryOptions());
    }

    /**
     * 导出路线管理列表
     */
    @SaCheckPermission("system:route:export")
    @Log(title = "路线管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizRouteBo bo, HttpServletResponse response) {
        List<BizRouteVo> list = bizRouteService.queryList(bo);
        ExcelUtil.exportExcel(list, "路线管理", BizRouteVo.class, response);
    }

    /**
     * 获取路线管理详细信息
     *
     * @param routeId 主键
     */
    @SaCheckPermission("system:route:query")
    @GetMapping("/{routeId}")
    public R<BizRouteVo> getInfo(@NotNull(message = "主键不能为空")
                                  @PathVariable Long routeId) {
        return R.ok(bizRouteService.queryById(routeId));
    }

    /**
     * 新增路线管理
     */
    @SaCheckPermission("system:route:add")
    @Log(title = "路线管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizRouteBo bo) {
        return toAjax(bizRouteService.insertByBo(bo));
    }

    /**
     * 修改路线管理
     */
    @SaCheckPermission("system:route:edit")
    @Log(title = "路线管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizRouteBo bo) {
        return toAjax(bizRouteService.updateByBo(bo));
    }

    /**
     * 删除路线管理
     *
     * @param routeIds 主键串
     */
    @SaCheckPermission("system:route:remove")
    @Log(title = "路线管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{routeIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] routeIds) {
        return toAjax(bizRouteService.deleteWithValidByIds(List.of(routeIds), true));
    }
}
