package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.BizProduct;
import org.dromara.system.domain.BizProductPriceRecord;
import org.dromara.system.domain.bo.BizProductBo;
import org.dromara.system.domain.bo.BizProductPriceRecordBo;
import org.dromara.system.domain.vo.BizProductPriceRecordVo;
import org.dromara.system.domain.vo.BizProductVo;
import org.dromara.system.mapper.BizProductCategoryMapper;
import org.dromara.system.mapper.BizProductMapper;
import org.dromara.system.mapper.BizProductPriceRecordMapper;
import org.dromara.system.service.IBizProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * 商品管理Service业务层处理
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizProductServiceImpl implements IBizProductService {

    private final BizProductMapper baseMapper;
    private final BizProductCategoryMapper categoryMapper;
    private final BizProductPriceRecordMapper priceRecordMapper;

    /**
     * 查询商品管理
     *
     * @param productId 主键
     * @return 商品管理
     */
    @Override
    public BizProductVo queryById(Long productId){
        return baseMapper.selectProductById(productId);
    }

    /**
     * 分页查询商品管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商品管理分页列表
     */
    @Override
    public TableDataInfo<BizProductVo> queryPageList(BizProductBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizProduct> lqw = buildQueryWrapper(bo);
        Page<BizProductVo> result = baseMapper.selectProductPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的商品管理列表
     *
     * @param bo 查询条件
     * @return 商品管理列表
     */
    @Override
    public List<BizProductVo> queryList(BizProductBo bo) {
        LambdaQueryWrapper<BizProduct> lqw = buildQueryWrapper(bo);
        return baseMapper.selectProductList(lqw);
    }

    /**
     * 查询商品价格历史
     *
     * @param bo 查询条件
     * @return 商品价格历史
     */
    @Override
    public List<BizProductPriceRecordVo> queryPriceHistory(BizProductPriceRecordBo bo) {
        LambdaQueryWrapper<BizProductPriceRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProductId() != null, BizProductPriceRecord::getProductId, bo.getProductId());
        lqw.ge(bo.getBeginDate() != null, BizProductPriceRecord::getRecordDate, bo.getBeginDate());
        lqw.le(bo.getEndDate() != null, BizProductPriceRecord::getRecordDate, bo.getEndDate());
        lqw.orderByAsc(BizProductPriceRecord::getRecordDate);
        return priceRecordMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizProduct> buildQueryWrapper(BizProductBo bo) {
        LambdaQueryWrapper<BizProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getCategoryId() != null, BizProduct::getCategoryId, bo.getCategoryId());
        lqw.like(StringUtils.isNotBlank(bo.getProductName()), BizProduct::getProductName, bo.getProductName());
        lqw.like(StringUtils.isNotBlank(bo.getSpecification()), BizProduct::getSpecification, bo.getSpecification());
        lqw.like(StringUtils.isNotBlank(bo.getSupplier()), BizProduct::getSupplier, bo.getSupplier());
        lqw.orderByAsc(BizProduct::getProductId);
        return lqw;
    }

    /**
     * 新增商品管理
     *
     * @param bo 商品管理
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizProductBo bo) {
        BizProduct add = MapstructUtils.convert(bo, BizProduct.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setProductId(add.getProductId());
            saveTodayPriceRecord(add);
        }
        return flag;
    }

    /**
     * 修改商品管理
     *
     * @param bo 商品管理
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizProductBo bo) {
        BizProduct update = MapstructUtils.convert(bo, BizProduct.class);
        validEntityBeforeSave(update);
        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
            saveTodayPriceRecord(update);
        }
        return flag;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizProduct entity){
        if (entity.getCategoryId() != null && categoryMapper.selectById(entity.getCategoryId()) == null) {
            throw new ServiceException("商品大类不存在");
        }
    }

    /**
     * 同步当天价格记录，后续折线图按该表读取。
     */
    private void saveTodayPriceRecord(BizProduct product) {
        if (product.getLatestSaleAmount() == null && product.getLatestCostPrice() == null) {
            return;
        }
        LocalDate today = LocalDate.now();
        BizProductPriceRecord record = priceRecordMapper.selectOne(Wrappers.lambdaQuery(BizProductPriceRecord.class)
            .eq(BizProductPriceRecord::getProductId, product.getProductId())
            .eq(BizProductPriceRecord::getRecordDate, today));
        if (record == null) {
            record = new BizProductPriceRecord();
            record.setProductId(product.getProductId());
            record.setRecordDate(today);
            record.setSaleAmount(product.getLatestSaleAmount());
            record.setCostPrice(product.getLatestCostPrice());
            priceRecordMapper.insert(record);
            return;
        }
        record.setSaleAmount(product.getLatestSaleAmount());
        record.setCostPrice(product.getLatestCostPrice());
        priceRecordMapper.updateById(record);
    }

    /**
     * 校验并批量删除商品管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            priceRecordMapper.delete(Wrappers.lambdaQuery(BizProductPriceRecord.class)
                .in(BizProductPriceRecord::getProductId, ids));
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
