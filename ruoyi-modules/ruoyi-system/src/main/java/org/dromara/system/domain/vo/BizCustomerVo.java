package org.dromara.system.domain.vo;

import org.dromara.system.domain.BizCustomer;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 客户档案视图对象 biz_customer
 *
 * @author Lion Li
 * @date 2026-04-29
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizCustomer.class)
public class BizCustomerVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 客户ID
     */
    @ExcelProperty(value = "客户ID")
    private Long customerId;

    /**
     * 客户名称
     */
    @ExcelProperty(value = "客户名称")
    private String name;

    /**
     * 客户简称
     */
    @ExcelProperty(value = "客户简称")
    private String alias;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String phone;

    /**
     * 联系人
     */
    @ExcelProperty(value = "联系人")
    private String contactName;

    /**
     * 配送线路ID
     */
    private Long routeId;

    /**
     * 配送线路
     */
    @ExcelProperty(value = "配送线路")
    private String routeName;

    /**
     * 欠款金额
     */
    @ExcelProperty(value = "欠款金额")
    private BigDecimal debt;

    /**
     * 地图定位
     */
    @ExcelProperty(value = "地图定位")
    private String mapLocation;

    /**
     * 地图经度
     */
    @ExcelProperty(value = "地图经度")
    private BigDecimal longitude;

    /**
     * 地图纬度
     */
    @ExcelProperty(value = "地图纬度")
    private BigDecimal latitude;

    /**
     * 地图解析地址
     */
    @ExcelProperty(value = "地图解析地址")
    private String mapAddress;

    /**
     * 地图服务商
     */
    private String mapProvider;

    /**
     * 门面照片
     */
    @ExcelProperty(value = "门面照片")
    private String photo;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
