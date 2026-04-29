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
import org.dromara.system.domain.BizCustomer;
import org.dromara.system.domain.BizRoute;
import org.dromara.system.domain.bo.BizRouteBo;
import org.dromara.system.domain.vo.BizRouteVo;
import org.dromara.system.mapper.BizCustomerMapper;
import org.dromara.system.mapper.BizRouteMapper;
import org.dromara.system.service.IBizRouteService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 路线管理Service业务层处理
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizRouteServiceImpl implements IBizRouteService {

    private final BizRouteMapper baseMapper;
    private final BizCustomerMapper customerMapper;

    /**
     * 查询路线管理
     *
     * @param routeId 主键
     * @return 路线管理
     */
    @Override
    public BizRouteVo queryById(Long routeId){
        return baseMapper.selectVoById(routeId);
    }

    /**
     * 分页查询路线管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 路线管理分页列表
     */
    @Override
    public TableDataInfo<BizRouteVo> queryPageList(BizRouteBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizRoute> lqw = buildQueryWrapper(bo);
        Page<BizRouteVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的路线管理列表
     *
     * @param bo 查询条件
     * @return 路线管理列表
     */
    @Override
    public List<BizRouteVo> queryList(BizRouteBo bo) {
        LambdaQueryWrapper<BizRoute> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询路线下拉选项
     *
     * @return 路线列表
     */
    @Override
    public List<BizRouteVo> queryOptions() {
        return baseMapper.selectVoList(Wrappers.lambdaQuery(BizRoute.class)
            .orderByAsc(BizRoute::getRouteSort)
            .orderByAsc(BizRoute::getRouteId));
    }

    private LambdaQueryWrapper<BizRoute> buildQueryWrapper(BizRouteBo bo) {
        LambdaQueryWrapper<BizRoute> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getRouteName()), BizRoute::getRouteName, bo.getRouteName());
        lqw.like(StringUtils.isNotBlank(bo.getRouteCode()), BizRoute::getRouteCode, bo.getRouteCode());
        lqw.orderByAsc(BizRoute::getRouteSort).orderByAsc(BizRoute::getRouteId);
        return lqw;
    }

    /**
     * 新增路线管理
     *
     * @param bo 路线管理
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizRouteBo bo) {
        BizRoute add = MapstructUtils.convert(bo, BizRoute.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setRouteId(add.getRouteId());
        }
        return flag;
    }

    /**
     * 修改路线管理
     *
     * @param bo 路线管理
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizRouteBo bo) {
        BizRoute update = MapstructUtils.convert(bo, BizRoute.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizRoute entity){
        boolean nameExists = baseMapper.exists(Wrappers.lambdaQuery(BizRoute.class)
            .eq(BizRoute::getRouteName, entity.getRouteName())
            .ne(ObjectUtil.isNotNull(entity.getRouteId()), BizRoute::getRouteId, entity.getRouteId()));
        if (nameExists) {
            throw new ServiceException("路线名称已存在");
        }
        if (StringUtils.isNotBlank(entity.getRouteCode())) {
            boolean codeExists = baseMapper.exists(Wrappers.lambdaQuery(BizRoute.class)
                .eq(BizRoute::getRouteCode, entity.getRouteCode())
                .ne(ObjectUtil.isNotNull(entity.getRouteId()), BizRoute::getRouteId, entity.getRouteId()));
            if (codeExists) {
                throw new ServiceException("路线编码已存在");
            }
        }
    }

    /**
     * 校验并批量删除路线管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            long count = customerMapper.selectCount(Wrappers.lambdaQuery(BizCustomer.class)
                .in(BizCustomer::getRouteId, ids));
            if (count > 0) {
                throw new ServiceException("路线已被客户档案使用，不能删除");
            }
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
