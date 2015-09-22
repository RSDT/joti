package com.umbrella.jotiwa.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stesi on 20-9-2015.
 */
public abstract class ItemList<T, K> extends ArrayList<T> {

    public ItemList() {
        itemOptions = new ArrayList<>();
    }

    private List<K> itemOptions;

    public void setItemOptions(List<K> itemOptions) {
        this.itemOptions = itemOptions;
    }

    public List<K> getItemOptions() {
        return itemOptions;
    }

    @Override
    public void clear() {
        if(super.size() > 0) super.clear();
    }
}
