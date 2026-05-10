package org.dromara.system.service;

import org.dromara.system.domain.vo.BizDashboardSummaryVo;

/**
 * 首页统计Service接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface IBizDashboardService {

    BizDashboardSummaryVo querySummary(String rankPeriod, String rankMonth);
}
