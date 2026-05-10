package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页统计视图对象
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
public class BizDashboardSummaryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private BizDashboardMetricVo todaySales = new BizDashboardMetricVo();

    private BizDashboardMetricVo monthSales = new BizDashboardMetricVo();

    private BizDashboardMetricVo yearSales = new BizDashboardMetricVo();

    private BizDashboardTodayOrderVo todayOrders = new BizDashboardTodayOrderVo();

    private BizDashboardProfitMetricVo todayProfit = new BizDashboardProfitMetricVo();

    private BizDashboardProfitMetricVo monthProfit = new BizDashboardProfitMetricVo();

    private BizDashboardProfitMetricVo yearProfit = new BizDashboardProfitMetricVo();

    private List<BizDashboardCustomerRankVo> customerRanks = new ArrayList<>();

    private List<BizDashboardProductRankVo> productRanks = new ArrayList<>();

    private List<BizDashboardProfitRankVo> customerProfitRanks = new ArrayList<>();

    private List<BizDashboardProfitRankVo> productProfitRanks = new ArrayList<>();

    private List<BizDashboardProfitRankVo> routeProfitRanks = new ArrayList<>();
}
