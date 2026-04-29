package org.dromara.system.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.bo.BizRouteBo;
import org.dromara.system.domain.vo.BizRouteVo;

import java.util.Collection;
import java.util.List;

/**
 * 路线管理Service接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface IBizRouteService {

    /**
     * 查询路线管理
     *
     * @param routeId 主键
     * @return 路线管理
     */
    BizRouteVo queryById(Long routeId);

    /**
     * 分页查询路线管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 路线管理分页列表
     */
    TableDataInfo<BizRouteVo> queryPageList(BizRouteBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的路线管理列表
     *
     * @param bo 查询条件
     * @return 路线管理列表
     */
    List<BizRouteVo> queryList(BizRouteBo bo);

    /**
     * 查询路线下拉选项
     *
     * @return 路线列表
     */
    List<BizRouteVo> queryOptions();

    /**
     * 新增路线管理
     *
     * @param bo 路线管理
     * @return 是否新增成功
     */
    Boolean insertByBo(BizRouteBo bo);

    /**
     * 修改路线管理
     *
     * @param bo 路线管理
     * @return 是否修改成功
     */
    Boolean updateByBo(BizRouteBo bo);

    /**
     * 校验并批量删除路线管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
