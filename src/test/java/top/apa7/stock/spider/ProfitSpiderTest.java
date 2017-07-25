package top.apa7.stock.spider;

import java.util.*;
import org.apache.http.client.utils.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import top.apa7.stock.comparator.StockAmountComparator;
import top.apa7.stock.comparator.StockQuantityRatioComparator;
import top.apa7.stock.comparator.StockTurnoverRateComparator;
import top.apa7.stock.comparator.StockVolumeComparator;
import top.apa7.stock.domain.Stock;
import top.apa7.stock.domain.StockEntity;
import top.apa7.stock.domain.StockProfit;
import top.apa7.utils.*;

public class ProfitSpiderTest {

//    "https://xueqiu.com/v4/stock/quote.json?code=SH600516&_=1500952725971"
//{"SH600516":{"symbol":"SH600516","exchange":"SH","code":"600516","name":"方大炭素","current":"25.22","percentage":"-2.74","change":"-0.71","open":"25.5","high":"26.26","low":"24.42","close":"25.22","last_close":"25.93","high52week":"26.26","low52week":"8.35","volume":"1.40740931E8","lot_volume":"1407409.31","volumeAverage":"108010834","marketCapital":"4.335722473316E10","eps":"0.04","pe_ttm":"333.6789","pe_lyr":"642.8158","beta":"0.0","totalShares":"1719160378","time":"Tue Jul 25 11:18:27 +0800 2017","afterHours":"0.0","afterHoursPct":"0.0","afterHoursChg":"0.0","updateAt":"1493006401023","dividend":"0.022","yield":"0.08","turnover_rate":"8.19%","instOwn":"0.0","rise_stop":"28.52","fall_stop":"23.34","currency_unit":"CNY","amount":"3.564641663E9","net_assets":"3.4401","hasexist":"","has_warrant":"0","type":"11","flag":"1","rest_day":"","amplitude":"7.10%","market_status":"交易中","lot_size":"100","min_order_quantity":"0","max_order_quantity":"0","tick_size":"0.01","kzz_stock_symbol":"","kzz_stock_name":"","kzz_stock_current":"0.0","kzz_convert_price":"0.0","kzz_covert_value":"0.0","kzz_cpr":"0.0","kzz_putback_price":"0.0","kzz_convert_time":"","kzz_redempt_price":"0.0","kzz_straight_price":"0.0","kzz_stock_percent":"","pb":"7.33","benefit_before_tax":"0.0","benefit_after_tax":"0.0","convert_bond_ratio":"","totalissuescale":"","outstandingamt":"","maturitydate":"","remain_year":"","convertrate":"0.0","interestrtmemo":"","release_date":"","circulation":"0.0","par_value":"0.0","due_time":"0.0","value_date":"","due_date":"","publisher":"","redeem_type":"T","issue_type":"1","bond_type":"","warrant":"","sale_rrg":"","rate":"","after_hour_vol":"0","float_shares":"1719160377","float_market_capital":"4.335722470794E10","disnext_pay_date":"","convert_rate":"0.0","volume_ratio":"1.53","percent5m":"-0.08","pankou_ratio":"39.64%","psr":"17.0489"}}

    //分时
    //https://xueqiu.com/stock/forchart/stocklist.json?symbol=SH600516&period=1d&one_min=1&_=1500953507976


    private static final String STOCK_LIST_URL = "http://quote.tool.hexun.com/hqzx/quote.aspx?type=2&market=%d&sorttype=%d&updown=up&page=%d&count=%d&time=193440";
    //    http://quote.tool.hexun.com/hqzx/quote.aspx?type=2&market=0&sorttype=8&updown=up&page=2&count=50&time=193440
    private static final String STOCK_NAME_URL = "http://stockdata.stock.hexun.com/2008/Data/name.ashx?stockid=%s&callback=HXNameData&time=20170101";
    private static final String STOCK_INFO_URL = "http://stockdata.stock.hexun.com/jgcc/%s.shtml";
    private static final String STOCK_DATA_URL = "http://webstock.quote.hermes.hexun.com/a/kline?" +
                                                 "code=%s&start=%s&number=-%d&type=5&callback=callback";
    //code=szse000651&start=20170707150000&number=-600&type=5&callback=callback
    //code=sse600516&start=20170707150000&number=-600&type=5&callback=callback
    private static final String ORDER_COUNT_URL = "http://vol.stock.hexun.com/Charts/Now/Share/info2_stock.ashx?code=%s";
    private static final String PROFIT_URL = "http://stockdata.stock.hexun.com/2009_bgqfpya_%s.shtml";

    private static final String STOCK_QUOTE = "https://xueqiu.com/v4/stock/quote.json?code=%s&_=%d";

    public static final int SORT_CODE = 0;
    public static final int SORT_RANGE = 3;
    public static final int SORT_VOLUME = 8;
    public static final int SORT_AMOUNT = 9;
    public static final int SORT_TURNOVER = 10;
    public static final int SORT_AMPLITUDE = 11;
    public static final int SORT_QUANTITY = 12;

    private List<StockEntity> listAllStock(int sortType, int page, int count) {
        List<StockEntity> stockEntities = new ArrayList<>();

        Downloader downloader = new Downloader();
        String url = String.format(STOCK_LIST_URL, 0, sortType, page, count);
        String stockListResp = downloader.getHtmlByUrl(url);
        stockListResp = stockListResp.replaceAll("dataArr|=|\\r|\\n", "").replace("'", "\"");
        List<List> stockList = JsonUtil.json2List(stockListResp);
        for (List list : stockList) {
            if (list.size() < 13) {
                continue;
            }
            String code = list.get(0).toString();   //代码
            String name = list.get(1).toString();   //名称
            Double close = (Double) list.get(2);   //最新价
            Double range = (Double) list.get(3);    //涨跌幅
            Double lastClose = (Double) list.get(4);//昨收
            Double open = (Double) list.get(5);     //今开
            Double high = (Double) list.get(6);     //最高
            Double low = (Double) list.get(7);      //最低
            Double volume = Double.valueOf(list.get(8).toString()); //成交量(万)
            Double amount = DoubleUtil.div(Double.valueOf(list.get(9).toString()), 100000D);//成交额
            Double turnoverRate = (Double) list.get(10);//换手率
            Double amplitude = (Double) list.get(11);   //振幅
            Double quantityRatio = (Double) list.get(12);//量比
            StockEntity stockEntity = new StockEntity(code, name, new Date(), open, close, high, low, volume, amount, range, amplitude, turnoverRate, quantityRatio);
            stockEntities.add(stockEntity);
        }
        return stockEntities;
    }

    private List<List<StockProfit>> getRecentProfit(List<String> codes, Date date) {
        Downloader downloader = new Downloader();
        List<List<StockProfit>> list = new ArrayList<>();
        for (String code : codes) {
            String name = getStockName(code);
            List<StockProfit> profits = new ArrayList<>();
            String url = String.format(PROFIT_URL, code);
            String profitContent = downloader.getHtmlByUrl(url);
            Document proficDoc = Jsoup.parse(profitContent);
            Elements profitTrs = proficDoc.select("tr:has(td:contains(分配预案)) ~ tr:has(td:eq(3))");
            if (profitTrs.isEmpty()) {
                continue;
            }
//            for (Element profitTr : profitTrs) {
            Element profitTr = profitTrs.get(0);
            Elements profitTds = profitTr.select("td");
            String publishDateStr = DOMHelper.getText(profitTds.get(0)); // 披露日期
            Date publicDate = DateUtil.string2Date(publishDateStr, "yyyy年MM月dd日");
            String accountDateStr = DOMHelper.getText(profitTds.get(1)); // 会计年度
            Date accountDate = DateUtil.string2Date(accountDateStr, "yyyy年MM月dd日");
            String desc = DOMHelper.getText(profitTds.get(2));        // 分红描述
            String executeDateStr = DOMHelper.getMatches(desc, "\\d{4}-\\d{2}-\\d{2}");
            Date executeDate = DateUtil.string2Date(executeDateStr, "yyyy-MM-dd");// 分红日期
            String status = DOMHelper.getText(profitTds.get(3));      // 实施状况
            if (executeDate != null && executeDate.getMonth() == date.getMonth()) {
                StockProfit profit = new StockProfit(code, name, publicDate, accountDate, executeDate, desc, status);
                profits.add(profit);
                list.add(profits);
            }
//            }
        }
        return list;
    }

    private void getStockInfo(String code, Date date) {

    }

    private List<Stock> listStockData(String code) {
        List<Stock> list = new ArrayList<>();

        Downloader downloader = new Downloader();
        String name = getStockName(code);
        String url = String.format(STOCK_DATA_URL, convertCode(code), DateUtil.date2String(new Date(), "yyyyMMddHHmmss"), 100);
        String stockResp = downloader.getHtmlByUrl(url);
        stockResp = stockResp.substring(9, stockResp.length() - 2);
        Map<String, Object> stockMap = JsonUtil.json2Map(stockResp);
        List<List> stockData = (List<List>) ((List<List>) stockMap.get("Data")).get(0);
        for (List stockDatum : stockData) {
            if (stockDatum.size() < 8) {
                continue;
            }
            String timeStr = DOMHelper.getMatches(stockDatum.get(0).toString(), "^\\d{8}");
            Date time = DateUtil.string2Date(timeStr, "yyyyMMdd");
            Double lastClose = DoubleUtil.div(Double.valueOf(stockDatum.get(1).toString()), 100D);
            Double open = DoubleUtil.div(Double.valueOf(stockDatum.get(2).toString()), 100D);
            Double close = DoubleUtil.div(Double.valueOf(stockDatum.get(3).toString()), 100D);
            Double high = DoubleUtil.div(Double.valueOf(stockDatum.get(4).toString()), 100D);
            Double low = DoubleUtil.div(Double.valueOf(stockDatum.get(5).toString()), 100D);
            Double volume = Double.valueOf(stockDatum.get(6).toString()) / 10000;
            Double amount = DoubleUtil.div(Double.valueOf(stockDatum.get(7).toString()), 1000000D);
            Stock stock = new Stock(code, name, time, open, close, high, low, volume, amount);
            list.add(stock);
        }
        return list;
    }

    private String getStockName(String code) {
        Downloader downloader = new Downloader();
        String nameResp = downloader.getHtmlByUrl(String.format(STOCK_NAME_URL, code)); // HXNameData([{code:'600516', name:'方大炭素'}]);
        if (nameResp.contains("script")) {
            System.out.println("错误, 找不到该代码");
        }
        String name = DOMHelper.getMatches(nameResp, "(?<=name:').*?(?=('))");
        return name;
    }

    private void getOrderCount() {

    }

    private String convertCode(String code) {
        if (code.isEmpty()) {
            return "";
        }
        String head = code.substring(0, 1);
        switch (head) {
            case "0":
                code = "szse" + code;
                break;
            case "6":
                code = "sse" + code;
                break;
            default:
                break;
        }
        return code;
    }

    @Test
    public void test1() {
        List<String> list = Arrays.asList("000659", "022659", "010659");
        Date date = DateUtils.parseDate("2017-07-05", new String[]{"yyyy-MM-dd"});
        List<List<StockProfit>> recentProfit = getRecentProfit(list, date);
        System.out.println(recentProfit);
    }

    @Test
    public void test2() {
//        List<Stock> stockEntities = listStockData("000651");
//        List<StockEntity> list = listAllStock(3, 1, 3000);
//        List<StockEntity> list = listAllStock(9, 1, 1000);// 成交额
        int count = 400;
        String basePath = "D:\\stock复盘";
        String todayPath = "\\" + DateUtil.date2String(new Date(), "yyyy") + "\\"
                           + DateUtil.date2String(new Date(), "MM.dd") + "\\"
                           + DateUtil.date2String(new Date(), "yyyyMMdd");
        List<StockEntity> list = listAllStock(3, 1, count);// 成交量
        String dateStr = DateUtil.date2String(new Date(), "yyyyMMdd");
        FileUtil.write(basePath + todayPath + "-Top" + count + ".txt", format(list.toString()));
        Collections.sort(list, new StockAmountComparator());
        FileUtil.write(basePath + todayPath + "-Top" + count + "成交额DESC.txt", format(list.toString()));
        Collections.sort(list, new StockVolumeComparator());
        FileUtil.write(basePath + todayPath + "-Top" + count + "成交量DESC.txt", format(list.toString()));
        Collections.sort(list, new StockQuantityRatioComparator());
        FileUtil.write(basePath + todayPath + "-Top" + count + "量比DESC.txt", format(list.toString()));
        Collections.sort(list, new StockTurnoverRateComparator());
        FileUtil.write(basePath + todayPath + "-Top" + count + "换手率DESC.txt", format(list.toString()));
//        List<String> codes = new ArrayList<>();
//        for (StockEntity stockEntity : list) {
//            String code = stockEntity.getCode();
//            codes.add(code);
//        }
//        List<List<StockProfit>> recentProfit = getRecentProfit(codes, new Date());
//        System.out.println(recentProfit);
    }

    private String format(String content) {
        return content.replace("},", "},\n");
    }

    @Test
    public void test3() {
        Downloader downloader = new Downloader();
        downloader.setCookie("aliyungf_tc=AQAAAOm3MAO09AoA9B4Ot/maR5cYNeXN; s=f019yigngy; xq_a_token=2880deefb7281e7f67ff701178e9a5f38b56d642; xqat=2880deefb7281e7f67ff701178e9a5f38b56d642; xq_r_token=c2c61ed7772eeb02385d9f86671e4a08bb6cb4b4; xq_token_expire=Sat%20Aug%2019%202017%2011%3A10%3A46%20GMT%2B0800%20(CST); xq_is_login=1; u=1710314673; bid=7150fa172f51d186e6e1cb54c3505ac0_j5j09ead; Hm_lvt_1db88642e346389874251b5a1eded6e3=1500950014; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1500954416; __utmt=1; __utma=1.144360642.1500950014.1500950014.1500954416.2; __utmb=1.1.10.1500954416; __utmc=1; __utmz=1.1500950014.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
        String code = "SH600516";
        String quoteUrl = String.format(STOCK_QUOTE, code, DateUtil.string2Date("2017-07-24 11:30", "yyyy-MM-dd HH:mm").getTime());
        String quoteResp = downloader.getHtmlByUrl(quoteUrl);
//        1500953507976
//        https://xueqiu.com/v4/stock/quote.json?code=SH600516&_=1500953400000
        Map<String, Object> quoteJson = JsonUtil.json2Map(quoteResp);
        Map quoteMap = (Map) quoteJson.get(code);
        System.out.println(quoteMap.toString().replace(",", ",\n"));
    }
}