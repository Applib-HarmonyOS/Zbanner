package zhuang.zbanner;

import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.ability.fraction.FractionManager;

import java.util.Optional;

/**
 * Created by zhuang on 2017/11/29.
 */

public class ItemInfo {
    public Optional<Fraction> fraction;
    public int position;
    public int prePosition;
    public int nextPosition;
    public int left;

    public  boolean isPre(){
        return fraction.isPresent();
    }
}
