package org.dromara.system.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.bo.BizDeliveryArchiveBo;
import org.dromara.system.domain.bo.BizDeliveryOrderBo;
import org.dromara.system.domain.vo.BizDeliveryOrderVo;

import java.util.Collection;
import java.util.List;

/**
 * 配送货单Service接口
 *
 * @author Lion Li
 * @date 2026-04-29
 */
public interface IBizDeliveryOrderService {

    BizDeliveryOrderVo queryById(Long deliveryId);

    TableDataInfo<BizDeliveryOrderVo> queryPageList(BizDeliveryOrderBo bo, PageQuery pageQuery);

    List<BizDeliveryOrderVo> queryList(BizDeliveryOrderBo bo);

    Boolean insertByBo(BizDeliveryOrderBo bo);

    Boolean updateByBo(BizDeliveryOrderBo bo);

    Boolean archiveById(Long deliveryId, BizDeliveryArchiveBo bo);

    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
