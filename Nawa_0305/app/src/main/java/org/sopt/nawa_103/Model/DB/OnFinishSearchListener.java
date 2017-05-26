package org.sopt.nawa_103.Model.DB;

/**
 * Created by jihoon on 2016-01-14.
 */
import org.sopt.nawa_103.Model.DB.Item;

import java.util.List;

public interface OnFinishSearchListener {
    public void onSuccess(List<Item> itemList);
    public void onFail();
}
