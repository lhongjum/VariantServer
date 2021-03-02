package cn.granitech.business.service;

import cn.granitech.variantorm.metadata.fieldtype.FieldTypes;
import cn.granitech.web.pojo.TableHeaderColumn;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2021/1/20 17:09
 * @Version: 1.0
 */

public class TableHeaderMapping {

    private static final Map<String, TableHeaderColumn> HEADER_TYPE_MAP = new HashMap<>();

    static {
        HEADER_TYPE_MAP.put(FieldTypes.BOOLEAN_TYPE_NAME,   new TableHeaderColumn("100", "center"));
        HEADER_TYPE_MAP.put(FieldTypes.INTEGER_TYPE_NAME,   new TableHeaderColumn("120", "right"));
        HEADER_TYPE_MAP.put(FieldTypes.DECIMAL_TYPE_NAME,   new TableHeaderColumn("120", "right"));
        HEADER_TYPE_MAP.put(FieldTypes.PERCENT_TYPE_NAME,   new TableHeaderColumn("120", "right"));
        HEADER_TYPE_MAP.put(FieldTypes.MONEY_TYPE_NAME,     new TableHeaderColumn("120", "right"));
        HEADER_TYPE_MAP.put(FieldTypes.TEXT_TYPE_NAME,      new TableHeaderColumn("120", "left"));
        HEADER_TYPE_MAP.put(FieldTypes.TEXT_AREA_TYPE_NAME, new TableHeaderColumn("200", "left"));
        HEADER_TYPE_MAP.put(FieldTypes.OPTION_TYPE_NAME,    new TableHeaderColumn("120", "left"));
        HEADER_TYPE_MAP.put(FieldTypes.TAG_TYPE_NAME,       new TableHeaderColumn("160", "left"));
        HEADER_TYPE_MAP.put(FieldTypes.DATE_TYPE_NAME,      new TableHeaderColumn("100", "left"));
        HEADER_TYPE_MAP.put(FieldTypes.DATE_TIME_TYPE_NAME, new TableHeaderColumn("160", "left"));
        HEADER_TYPE_MAP.put(FieldTypes.REFERENCE_TYPE_NAME, new TableHeaderColumn("120", "left"));
    }

    public static TableHeaderColumn getHeaderColumn(String fieldType) {
        return HEADER_TYPE_MAP.get(fieldType);
    }

    public static void setHeaderColumnDefaultProps(String fieldType, TableHeaderColumn thc) {
        if (HEADER_TYPE_MAP.containsKey(fieldType)) {
            TableHeaderColumn defaultThc = HEADER_TYPE_MAP.get(fieldType);
            thc.setWidth( defaultThc.getWidth() );
            thc.setAlign( defaultThc.getAlign() );
        }
    }

}
