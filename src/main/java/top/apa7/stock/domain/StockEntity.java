package top.apa7.stock.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by liyi on 2017/7/7.
 * Maintainer:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockEntity extends Stock{
    private Double range;           // 涨跌幅
    private Double amplitude;       // 振幅
    private Double turnoverRate;    // 换手率
    private Double quantityRatio;   // 量比

    public StockEntity(String code, String name, Date date,
                       Double open, Double close, Double high, Double low,
                       Double volume, Double amount, Double range, Double amplitude,
                       Double turnoverRate, Double quantityRatio) {
        super(code, name, date, open, close, high, low, volume, amount);
        this.range = range;
        this.turnoverRate = turnoverRate;
        this.quantityRatio = quantityRatio;
        this.amplitude = amplitude;
    }

    @Override
    public String toString() {
        return super.toString() +
               ", 涨跌幅=" + range +
               ", 振幅" + amplitude +
               ", 换手率=" + turnoverRate +
               ", 量比=" + quantityRatio +
               "}";
    }

}
