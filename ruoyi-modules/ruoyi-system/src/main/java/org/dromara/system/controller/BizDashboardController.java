package org.dromara.system.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.system.domain.vo.BizDashboardSummaryVo;
import org.dromara.system.service.IBizDashboardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页统计
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dashboard")
public class BizDashboardController {

    private final IBizDashboardService dashboardService;

    @GetMapping("/summary")
    public R<BizDashboardSummaryVo> summary(@RequestParam(defaultValue = "month") String rankPeriod) {
        return R.ok(dashboardService.querySummary(rankPeriod));
    }
}
