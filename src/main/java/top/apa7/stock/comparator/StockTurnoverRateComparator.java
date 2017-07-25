package top.apa7.stock.comparator;

import java.util.Comparator;
import top.apa7.stock.domain.StockEntity;

/**
 * 换手率降序排列
 * Created by liyi on 2017/7/17.
 * Maintainer:
 */
public class StockTurnoverRateComparator implements Comparator<StockEntity> {
    @Override
    public int compare(StockEntity s1, StockEntity s2) {
        return s2.getTurnoverRate().compareTo(s1.getTurnoverRate());
    }
}
