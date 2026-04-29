package org.dromara.system.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.bo.BizProductCategoryBo;
import org.dromara.system.domain.vo.BizProductCategoryVo;

import java.util.Collection;
import java.util.List;

/**
 * 商品大类Service接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface IBizProductCategoryService {

    /**
     * 查询商品大类
     *
     * @param categoryId 主键
     * @return 商品大类
     */
    BizProductCategoryVo queryById(Long categoryId);

    /**
     * 分页查询商品大类列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商品大类分页列表
     */
    TableDataInfo<BizProductCategoryVo> queryPageList(BizProductCategoryBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的商品大类列表
     *
     * @param bo 查询条件
     * @return 商品大类列表
     */
    List<BizProductCategoryVo> queryList(BizProductCategoryBo bo);

    /**
     * 查询商品大类下拉选项
     *
     * @return 商品大类列表
     */
    List<BizProductCategoryVo> queryOptions();

    /**
     * 新增商品大类
     *
     * @param bo 商品大类
     * @return 是否新增成功
     */
    Boolean insertByBo(BizProductCategoryBo bo);

    /**
     * 修改商品大类
     *
     * @param bo 商品大类
     * @return 是否修改成功
     */
    Boolean updateByBo(BizProductCategoryBo bo);

    /**
     * 校验并批量删除商品大类信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
