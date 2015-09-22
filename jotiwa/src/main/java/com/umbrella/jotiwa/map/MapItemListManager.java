package com.umbrella.jotiwa.map;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stesi on 18-9-2015.
 */
public class MapItemListManager<T extends ItemList> {

    private Map<String, T> collections = new HashMap<>();

    private boolean migrated = false;

    public boolean isMigrated() {
        return migrated;
    }

    public T newItem(String id, T t)
    {
        return collections.put(id, t);
    }

    public T getItem(String id)
    {
        return collections.get(id);
    }

    public Map<String, T> getCollections() {
        return collections;
    }

    public void migrate(MapItemListManager<T> target)
    {
        String[] array = new String[this.collections.size()];
        this.collections.keySet().toArray(array);
        for(int i = 0; i < array.length; i++)
        {
            T t = this.getItem(array[i]);
            target.newItem(array[i], t);
        }
        this.migrated = true;
    }


}