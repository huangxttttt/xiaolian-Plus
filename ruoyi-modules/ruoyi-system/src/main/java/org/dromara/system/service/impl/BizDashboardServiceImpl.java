package org.dromara.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.system.domain.vo.BizDashboardMetricVo;
import org.dromara.system.domain.vo.BizDashboardProfitMetricVo;
import org.dromara.system.domain.vo.BizDashboardSummaryVo;
import org.dromara.system.mapper.BizDashboardMapper;
import org.dromara.system.service.IBizDashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

/**
 * 首页统计Service业务层处理
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@RequiredArgsConstructor
@Service
public class BizDashboardServiceImpl implements IBizDashboardService {

    private final BizDashboardMapper dashboardMapper;

    @Override
    public BizDashboardSummaryVo querySummary(String rankPeriod, String rankMonth) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate nextMonthStart = monthStart.plusMonths(1);
        LocalDate lastMonthStart = monthStart.minusMonths(1);
        LocalDate yearStart = today.withDayOfYear(1);
        LocalDate nextYearStart = yearStart.plusYears(1);
        LocalDate lastYearStart = yearStart.minusYears(1);

        BizDashboardSummaryVo summary = new BizDashboardSummaryVo();
        summary.setTodaySales(buildMetric(today, tomorrow, yesterday, today, today.minusDays(6), tomorrow, false));
        summary.setMonthSales(buildMetric(monthStart, nextMonthStart, lastMonthStart, monthStart, monthStart, tomorrow, false));
        summary.setYearSales(buildMetric(yearStart, nextYearStart, lastYearStart, yearStart, yearStart, nextYearStart, true));
        summary.getTodayOrders().setCustomerCount(defaultLong(dashboardMapper.selectDistinctCustomerCountAllStatus(today, tomorrow)));
        summary.getTodayOrders().setOrderCount(defaultLong(dashboardMapper.selectCustomerOrderCountAllStatus(today, tomorrow)));
        summary.setTodayProfit(defaultProfitMetric(dashboardMapper.selectProfitMetric(today, tomorrow)));
        summary.setMonthProfit(defaultProfitMetric(dashboardMapper.selectProfitMetric(monthStart, nextMonthStart)));
        summary.setYearProfit(defaultProfitMetric(dashboardMapper.selectProfitMetric(yearStart, nextYearStart)));

        YearMonth selectedRankMonth = parseRankMonth(rankMonth, YearMonth.from(today));
        LocalDate rankMonthStart = selectedRankMonth.atDay(1);
        LocalDate rankNextMonthStart = selectedRankMonth.plusMonths(1).atDay(1);
        LocalDate rankBeginDate = "year".equals(rankPeriod) ? yearStart : rankMonthStart;
        LocalDate rankEndDate = "year".equals(rankPeriod) ? nextYearStart : rankNextMonthStart;
        BigDecimal rankAmount = defaultDecimal(dashboardMapper.selectSalesAmount(rankBeginDate, rankEndDate));
        BigDecimal totalQuantity = defaultDecimal(dashboardMapper.selectProductTotalQuantity(rankBeginDate, rankEndDate));
        BigDecimal totalProfit = defaultDecimal(defaultProfitMetric(dashboardMapper.selectProfitMetric(rankBeginDate, rankEndDate)).getProfitAmount());
        summary.setCustomerRanks(dashboardMapper.selectCustomerRanks(rankBeginDate, rankEndDate, rankAmount));
        summary.setProductRanks(dashboardMapper.selectProductRanks(rankBeginDate, rankEndDate, totalQuantity));
        summary.setCustomerProfitRanks(dashboardMapper.selectCustomerProfitRanks(rankBeginDate, rankEndDate, totalProfit));
        summary.setProductProfitRanks(dashboardMapper.selectProductProfitRanks(rankBeginDate, rankEndDate, totalProfit));
        summary.setRouteProfitRanks(dashboardMapper.selectRouteProfitRanks(rankBeginDate, rankEndDate, totalProfit));
        return summary;
    }

    private YearMonth parseRankMonth(String rankMonth, YearMonth defaultMonth) {
        if (rankMonth == null || rankMonth.isBlank()) {
            return defaultMonth;
        }
        try {
            return YearMonth.parse(rankMonth);
        } catch (DateTimeParseException e) {
            return defaultMonth;
        }
    }

    private BizDashboardMetricVo buildMetric(LocalDate beginDate, LocalDate endDate, LocalDate previousBeginDate,
                                             LocalDate previousEndDate, LocalDate trendBeginDate, LocalDate trendEndDate,
                                             boolean monthlyTrend) {
        BizDashboardMetricVo metric = new BizDashboardMetricVo();
        metric.setAmount(defaultDecimal(dashboardMapper.selectSalesAmount(beginDate, endDate)));
        metric.setPreviousAmount(defaultDecimal(dashboardMapper.selectSalesAmount(previousBeginDate, previousEndDate)));
        metric.setArchivedAmount(defaultDecimal(dashboardMapper.selectSalesAmountByStatus(beginDate, endDate, "已归档")));
        metric.setUnarchivedAmount(defaultDecimal(dashboardMapper.selectSalesAmountByStatus(beginDate, endDate, "未归档")));
        metric.setOrderCount(defaultLong(dashboardMapper.selectCustomerOrderCount(beginDate, endDate)));
        metric.setCustomerCount(defaultLong(dashboardMapper.selectDistinctCustomerCount(beginDate, endDate)));
        metric.setTrend(monthlyTrend
            ? dashboardMapper.selectMonthlySalesTrend(trendBeginDate, trendEndDate)
            : dashboardMapper.selectDailySalesTrend(trendBeginDate, trendEndDate));
        return metric;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BizDashboardProfitMetricVo defaultProfitMetric(BizDashboardProfitMetricVo value) {
        return value == null ? new BizDashboardProfitMetricVo() : value;
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }
}
