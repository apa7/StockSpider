package top.apa7.stock.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.apa7.utils.DateUtil;

/**
 * 股票实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    private String code;    // 股票编码
    private String name;    // 股票名称
    private Date date;      // 交易日期
    private Double open;    // 开盘价
    private Double close;   // 收盘价
    private Double high;    // 最高价
    private Double low;     // 最低价
    private Double volume;  // 成交量
    private Double amount;  // 成交额

    @Override
    public String toString() {
        return "" + name + "(" + code + ")" +
               ", 日期=" + DateUtil.date2String(date, "yyyy-MM-dd") +
               ", 开盘=" + open +
               ", 收盘=" + close +
               ", 最高=" + high +
               ", 最低=" + low +
               ", 成交量=" + volume + "万" +
               ", 成交额=" + amount + "百万";
    }
}