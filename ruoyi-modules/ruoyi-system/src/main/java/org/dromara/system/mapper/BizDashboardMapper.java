package org.dromara.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.system.domain.vo.BizDashboardCustomerRankVo;
import org.dromara.system.domain.vo.BizDashboardProfitMetricVo;
import org.dromara.system.domain.vo.BizDashboardProfitRankVo;
import org.dromara.system.domain.vo.BizDashboardProductRankVo;
import org.dromara.system.domain.vo.BizDashboardTrendPointVo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 首页统计Mapper接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface BizDashboardMapper {

    BigDecimal selectSalesAmount(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate);

    BigDecimal selectSalesAmountByStatus(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate, @Param("status") String status);

    Long selectCustomerOrderCount(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate);

    Long selectDistinctCustomerCount(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate);

    Long selectCustomerOrderCountAllStatus(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate);

    Long selectDistinctCustomerCountAllStatus(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate);

    List<BizDashboardTrendPointVo> selectDailySalesTrend(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate);

    List<BizDashboardTrendPointVo> selectMonthlySalesTrend(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate);

    List<BizDashboardCustomerRankVo> selectCustomerRanks(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate, @Param("totalAmount") BigDecimal totalAmount);

    List<BizDashboardProductRankVo> selectProductRanks(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate, @Param("totalQuantity") BigDecimal totalQuantity);

    BigDecimal selectProductTotalQuantity(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate);

    BizDashboardProfitMetricVo selectProfitMetric(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate);

    List<BizDashboardProfitRankVo> selectCustomerProfitRanks(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate, @Param("totalProfit") BigDecimal totalProfit);

    List<BizDashboardProfitRankVo> selectProductProfitRanks(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate, @Param("totalProfit") BigDecimal totalProfit);

    List<BizDashboardProfitRankVo> selectRouteProfitRanks(@Param("beginDate") LocalDate beginDate, @Param("endDate") LocalDate endDate, @Param("totalProfit") BigDecimal totalProfit);
}
