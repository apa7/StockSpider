package top.apa7.stock.domain;

import lombok.Data;

@Data
public class StockAnnounceInfo {

    private int id;
    private String code; //股票代码
    private String name;//股票简称
    private String publicDate; //股票发布日期
    private String diveDate; //除权除息日
    private String registerDate; //股权登记日
    private String giveMoneyDate; //现金红利发放日

    private String DividendsMoney; //现金分红的钱数
    private String stockDate; //查询股价的日期
    private String stockMoney; //stockDate那天股票价格
    private String dividendRate; //分红率
}
