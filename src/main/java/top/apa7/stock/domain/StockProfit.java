package top.apa7.stock.domain;

import java.util.Date;
import lombok.Data;
import top.apa7.utils.DateUtil;

/**
 * Created by liyi on 2017/7/7.
 */
@Data
public class StockProfit {
    private String code;        // 股票代码
    private String name;
    private Date publishDate;   // 披露日期
    private Date accountDate;   // 会计日期
    private Date executeDate;   // 分红日期
    private String desc;        // 分配预案
    private String status;      // 实施状况

    public StockProfit(String code, String name, Date publishDate, Date accountDate, Date executeDate, String desc, String status) {
        this.code = code;
        this.name = name;
        this.publishDate = publishDate;
        this.accountDate = accountDate;
        this.executeDate = executeDate;
        this.desc = desc;
        this.status = status;
    }

    @Override
    public String toString() {
        return "分红{" +
               name + "(" + code + ")" +
               ", 披露日期=" + formatDate(publishDate) +
               ", 会计日期=" + formatDate(accountDate) +
               ", 分红日期=" + formatDate(executeDate) +
               ", 分配预案='" + desc + '\'' +
               ", 实施状况='" + status + '\'' +
               '}';
    }

    private String formatDate(Date date) {
        String dateStr = "";
        try {
            dateStr = DateUtil.date2String(date, "yyyy-MM-dd");
        } catch (Exception e) {
        }
        return dateStr;
    }
}
