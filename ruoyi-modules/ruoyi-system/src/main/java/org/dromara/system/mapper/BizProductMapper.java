package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.BizProduct;
import org.dromara.system.domain.vo.BizProductVo;

import java.util.List;

/**
 * 商品管理Mapper接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface BizProductMapper extends BaseMapperPlus<BizProduct, BizProductVo> {

    /**
     * 分页查询商品管理列表
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 商品管理列表
     */
    Page<BizProductVo> selectProductPage(Page<BizProductVo> page, @Param(Constants.WRAPPER) Wrapper<BizProduct> queryWrapper);

    /**
     * 查询商品管理列表
     *
     * @param queryWrapper 查询条件
     * @return 商品管理列表
     */
    List<BizProductVo> selectProductList(@Param(Constants.WRAPPER) Wrapper<BizProduct> queryWrapper);

    /**
     * 查询商品详情
     *
     * @param productId 商品ID
     * @return 商品管理
     */
    BizProductVo selectProductById(Long productId);
}
