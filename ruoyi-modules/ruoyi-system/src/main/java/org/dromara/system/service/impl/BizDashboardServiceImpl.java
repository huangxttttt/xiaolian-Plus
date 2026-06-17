package org.dromara.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.system.domain.vo.BizDashboardMetricVo;
import org.dromara.system.domain.vo.BizDashboardProfitMetricVo;
import org.dromara.system.domain.vo.BizDashboardSummaryVo;
import org.dromara.system.domain.vo.BizDashboardTrendPointVo;
import org.dromara.system.mapper.BizDashboardMapper;
import org.dromara.system.service.IBizDashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public BizDashboardSummaryVo querySummary(String rankPeriod, String rankMonth, String rankYear, String metricMonth, String metricYear) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        YearMonth currentMonth = YearMonth.from(today);
        YearMonth selectedMetricMonth = parseRankMonth(metricMonth, currentMonth);
        LocalDate monthStart = selectedMetricMonth.atDay(1);
        LocalDate nextMonthStart = monthStart.plusMonths(1);
        LocalDate lastMonthStart = monthStart.minusMonths(1);
        LocalDate monthTrendEnd = selectedMetricMonth.equals(currentMonth) ? tomorrow : nextMonthStart;
        Year currentYear = Year.from(today);
        Year selectedMetricYear = parseMetricYear(metricYear, currentYear);
        LocalDate yearStart = selectedMetricYear.atDay(1);
        LocalDate nextYearStart = yearStart.plusYears(1);
        LocalDate lastYearStart = yearStart.minusYears(1);
        LocalDate yearTrendEnd = selectedMetricYear.equals(currentYear) ? currentMonth.plusMonths(1).atDay(1) : nextYearStart;

        BizDashboardSummaryVo summary = new BizDashboardSummaryVo();
        summary.setTodaySales(buildMetric(today, tomorrow, yesterday, today, today.minusDays(6), tomorrow, false));
        summary.setMonthSales(buildMetric(monthStart, nextMonthStart, lastMonthStart, monthStart, monthStart, monthTrendEnd, false));
        summary.setYearSales(buildMetric(yearStart, nextYearStart, lastYearStart, yearStart, yearStart, yearTrendEnd, true));
        summary.getTodayOrders().setCustomerCount(defaultLong(dashboardMapper.selectDistinctCustomerCountAllStatus(today, tomorrow)));
        summary.getTodayOrders().setOrderCount(defaultLong(dashboardMapper.selectCustomerOrderCountAllStatus(today, tomorrow)));
        summary.setTodayProfit(defaultProfitMetric(dashboardMapper.selectProfitMetric(today, tomorrow)));
        BizDashboardProfitMetricVo monthProfit = defaultProfitMetric(dashboardMapper.selectProfitMetric(monthStart, nextMonthStart));
        monthProfit.setTrend(fillDailyTrend(monthStart, monthTrendEnd, dashboardMapper.selectDailyProfitTrend(monthStart, monthTrendEnd)));
        summary.setMonthProfit(monthProfit);
        BizDashboardProfitMetricVo yearProfit = defaultProfitMetric(dashboardMapper.selectProfitMetric(yearStart, nextYearStart));
        yearProfit.setTrend(fillMonthlyTrend(yearStart, yearTrendEnd, dashboardMapper.selectMonthlyProfitTrend(yearStart, yearTrendEnd)));
        summary.setYearProfit(yearProfit);

        YearMonth selectedRankMonth = parseRankMonth(rankMonth, YearMonth.from(today));
        Year selectedRankYear = parseMetricYear(rankYear, currentYear);
        LocalDate rankMonthStart = selectedRankMonth.atDay(1);
        LocalDate rankNextMonthStart = selectedRankMonth.plusMonths(1).atDay(1);
        LocalDate rankYearStart = selectedRankYear.atDay(1);
        LocalDate rankNextYearStart = selectedRankYear.plusYears(1).atDay(1);
        LocalDate rankBeginDate = "year".equals(rankPeriod) ? rankYearStart : rankMonthStart;
        LocalDate rankEndDate = "year".equals(rankPeriod) ? rankNextYearStart : rankNextMonthStart;
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

    private Year parseMetricYear(String metricYear, Year defaultYear) {
        if (metricYear == null || metricYear.isBlank()) {
            return defaultYear;
        }
        try {
            return Year.parse(metricYear);
        } catch (DateTimeParseException e) {
            return defaultYear;
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
        List<BizDashboardTrendPointVo> trend = monthlyTrend
            ? dashboardMapper.selectMonthlySalesTrend(trendBeginDate, trendEndDate)
            : dashboardMapper.selectDailySalesTrend(trendBeginDate, trendEndDate);
        metric.setTrend(monthlyTrend
            ? fillMonthlyTrend(trendBeginDate, trendEndDate, trend)
            : fillDailyTrend(trendBeginDate, trendEndDate, trend));
        return metric;
    }

    private List<BizDashboardTrendPointVo> fillDailyTrend(
        LocalDate beginDate, LocalDate endDate,
        List<BizDashboardTrendPointVo> source) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        Map<String, BigDecimal> amountByLabel = toTrendMap(source);
        List<BizDashboardTrendPointVo> result = new ArrayList<>();
        for (LocalDate date = beginDate; date.isBefore(endDate); date = date.plusDays(1)) {
            result.add(createTrendPoint(date.format(formatter), amountByLabel));
        }
        return result;
    }

    private List<BizDashboardTrendPointVo> fillMonthlyTrend(
        LocalDate beginDate, LocalDate endDate,
        List<BizDashboardTrendPointVo> source) {
        Map<String, BigDecimal> amountByLabel = toTrendMap(source);
        List<BizDashboardTrendPointVo> result = new ArrayList<>();
        YearMonth endMonth = YearMonth.from(endDate.minusDays(1));
        for (YearMonth month = YearMonth.from(beginDate); !month.isAfter(endMonth); month = month.plusMonths(1)) {
            result.add(createTrendPoint(month.toString(), amountByLabel));
        }
        return result;
    }

    private Map<String, BigDecimal> toTrendMap(List<BizDashboardTrendPointVo> source) {
        Map<String, BigDecimal> result = new HashMap<>();
        if (source != null) {
            source.forEach(item -> result.put(item.getLabel(), defaultDecimal(item.getAmount())));
        }
        return result;
    }

    private BizDashboardTrendPointVo createTrendPoint(
        String label, Map<String, BigDecimal> amountByLabel) {
        BizDashboardTrendPointVo point = new BizDashboardTrendPointVo();
        point.setLabel(label);
        point.setAmount(amountByLabel.getOrDefault(label, BigDecimal.ZERO));
        return point;
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
