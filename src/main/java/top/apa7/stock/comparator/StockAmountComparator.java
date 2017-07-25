package top.apa7.stock.comparator;

import java.util.Comparator;
import top.apa7.stock.domain.StockEntity;

/**
 * 成交额降序排列
 * Created by liyi on 2017/7/17.
 * Maintainer:
 */
public class StockAmountComparator implements Comparator<StockEntity> {
    @Override
    public int compare(StockEntity s1, StockEntity s2) {
        return s2.getAmount().compareTo(s1.getAmount());
    }
}
