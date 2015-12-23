package com.parsleyj.tool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Giuseppe on 22/12/15.
 */
public class THeapMemory {
    private Map<Integer, TObject> heap = new HashMap<Integer, TObject>();

    public void put(Integer id, TObject obj){
        heap.put(id, obj);
    }

    public TObject get(Integer id){
        return heap.get(id);
    }

    /**
     * Removes an object from the heap memory.
     * @param id the id of the object
     * @return true if the object with specified id was in memory, false otherwise.
     */
    public boolean delete(Integer id){
        if(heap.containsKey(id)){
            heap.remove(id);
            return true;
        }else{
            return false;
        }
    }

    //TODO: reference counting / garbage collection
}
