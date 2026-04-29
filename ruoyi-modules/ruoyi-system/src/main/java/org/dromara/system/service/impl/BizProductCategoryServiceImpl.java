package org.dromara.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
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
import org.dromara.system.domain.BizProductCategory;
import org.dromara.system.domain.bo.BizProductCategoryBo;
import org.dromara.system.domain.vo.BizProductCategoryVo;
import org.dromara.system.mapper.BizProductCategoryMapper;
import org.dromara.system.mapper.BizProductMapper;
import org.dromara.system.service.IBizProductCategoryService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 商品大类Service业务层处理
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizProductCategoryServiceImpl implements IBizProductCategoryService {

    private final BizProductCategoryMapper baseMapper;
    private final BizProductMapper productMapper;

    /**
     * 查询商品大类
     *
     * @param categoryId 主键
     * @return 商品大类
     */
    @Override
    public BizProductCategoryVo queryById(Long categoryId){
        return baseMapper.selectVoById(categoryId);
    }

    /**
     * 分页查询商品大类列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商品大类分页列表
     */
    @Override
    public TableDataInfo<BizProductCategoryVo> queryPageList(BizProductCategoryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizProductCategory> lqw = buildQueryWrapper(bo);
        Page<BizProductCategoryVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的商品大类列表
     *
     * @param bo 查询条件
     * @return 商品大类列表
     */
    @Override
    public List<BizProductCategoryVo> queryList(BizProductCategoryBo bo) {
        LambdaQueryWrapper<BizProductCategory> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询商品大类下拉选项
     *
     * @return 商品大类列表
     */
    @Override
    public List<BizProductCategoryVo> queryOptions() {
        return baseMapper.selectVoList(Wrappers.lambdaQuery(BizProductCategory.class)
            .orderByAsc(BizProductCategory::getCategorySort)
            .orderByAsc(BizProductCategory::getCategoryId));
    }

    private LambdaQueryWrapper<BizProductCategory> buildQueryWrapper(BizProductCategoryBo bo) {
        LambdaQueryWrapper<BizProductCategory> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getCategoryName()), BizProductCategory::getCategoryName, bo.getCategoryName());
        lqw.orderByAsc(BizProductCategory::getCategorySort).orderByAsc(BizProductCategory::getCategoryId);
        return lqw;
    }

    /**
     * 新增商品大类
     *
     * @param bo 商品大类
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizProductCategoryBo bo) {
        BizProductCategory add = MapstructUtils.convert(bo, BizProductCategory.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setCategoryId(add.getCategoryId());
        }
        return flag;
    }

    /**
     * 修改商品大类
     *
     * @param bo 商品大类
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizProductCategoryBo bo) {
        BizProductCategory update = MapstructUtils.convert(bo, BizProductCategory.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizProductCategory entity){
        boolean nameExists = baseMapper.exists(Wrappers.lambdaQuery(BizProductCategory.class)
            .eq(BizProductCategory::getCategoryName, entity.getCategoryName())
            .ne(ObjectUtil.isNotNull(entity.getCategoryId()), BizProductCategory::getCategoryId, entity.getCategoryId()));
        if (nameExists) {
            throw new ServiceException("大类名称已存在");
        }
    }

    /**
     * 校验并批量删除商品大类信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            long count = productMapper.selectCount(Wrappers.lambdaQuery(BizProduct.class)
                .in(BizProduct::getCategoryId, ids));
            if (count > 0) {
                throw new ServiceException("商品大类已被商品使用，不能删除");
            }
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
