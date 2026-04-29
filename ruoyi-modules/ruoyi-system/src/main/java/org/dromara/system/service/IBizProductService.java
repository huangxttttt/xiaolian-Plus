package org.dromara.system.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.bo.BizProductBo;
import org.dromara.system.domain.bo.BizProductPriceRecordBo;
import org.dromara.system.domain.vo.BizProductPriceRecordVo;
import org.dromara.system.domain.vo.BizProductVo;

import java.util.Collection;
import java.util.List;

/**
 * 商品管理Service接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface IBizProductService {

    /**
     * 查询商品管理
     *
     * @param productId 主键
     * @return 商品管理
     */
    BizProductVo queryById(Long productId);

    /**
     * 分页查询商品管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商品管理分页列表
     */
    TableDataInfo<BizProductVo> queryPageList(BizProductBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的商品管理列表
     *
     * @param bo 查询条件
     * @return 商品管理列表
     */
    List<BizProductVo> queryList(BizProductBo bo);

    /**
     * 查询商品价格历史
     *
     * @param bo 查询条件
     * @return 商品价格历史
     */
    List<BizProductPriceRecordVo> queryPriceHistory(BizProductPriceRecordBo bo);

    /**
     * 新增商品管理
     *
     * @param bo 商品管理
     * @return 是否新增成功
     */
    Boolean insertByBo(BizProductBo bo);

    /**
     * 修改商品管理
     *
     * @param bo 商品管理
     * @return 是否修改成功
     */
    Boolean updateByBo(BizProductBo bo);

    /**
     * 校验并批量删除商品管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
