package com.umbrella.jotiwa.Datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * slaat alleen elk densityth element op.
 * en bezit maximaal capacite elementen.
 */
public class Tail<E> extends ArrayList<E>{
    private final int density;
    private final int maxcapacity;
    private int densityNow = 0;
    public Tail(int capacaty, int density){
        this.maxcapacity = capacaty;
        this.density = density;
    }

    public int getDensity() {
        return density;
    }
    public int getMaxCapactity(){
        return maxcapacity;
    }

    public E add2(E item){
        if (densityNow == 0){
            boolean b =super.add(item);
            if (this.size()>maxcapacity){
                E removed = this.get(this.size()-1);
                this.remove(this.size()-1);
                return removed;
            }else{
                return null;
            }
        }else{
            densityNow = (densityNow + 1) % density;
            return item;
        }
    }

    public E add3(E item){
        if (densityNow == 0){
            boolean b =super.add(item);
            if (this.size()>maxcapacity){
                E removed = this.get(this.size()-1);
                this.remove(this.size()-1);
                return removed;
            }else{
                return null;
            }
        }else{
            densityNow = (densityNow + 1) % density;
            return null;
        }
    }

    public E add2(int postion,E item){
        super.add(postion, item);
        if (this.size()>maxcapacity)
            return this.remove(this.size()-1);
        else
            return null;
    }
    public ArrayList<E> addAll2(List<E> items){
        ArrayList<E> returnValue = new ArrayList<E>();
        for (int i = 0; i < items.size(); i++){
            E notInsertedValue = this.add2(items.get(i));
            if (notInsertedValue != null){
                returnValue.add(notInsertedValue);
            }
        }
        return returnValue;
    }
    public ArrayList<E> addAll3(List<E> items){
        ArrayList<E> returnValue = new ArrayList<E>();
        for (int i = 0; i < items.size(); i++){
            E notInsertedValue = this.add3(items.get(i));
            if (notInsertedValue != null){
                returnValue.add(notInsertedValue);
            }
        }
        return returnValue;
    }
}
