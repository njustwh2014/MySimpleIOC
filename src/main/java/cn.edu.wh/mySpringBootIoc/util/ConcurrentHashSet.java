/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, Serializable {


    private static final long serialVersionUID = 1707009781474279092L;
    private static final Object DEFAULT_VALUE=new Object();
    private final ConcurrentHashMap<E,Object> map;
    public ConcurrentHashSet(){
        map=new ConcurrentHashMap<>();
    }

    public ConcurrentHashSet(int initialCapacity){map=new ConcurrentHashMap<>(initialCapacity);}
    public ConcurrentHashSet(int initialCapacity,int loadFactor){map=new ConcurrentHashMap<>(initialCapacity,loadFactor);}

    public Iterator<E> iterator(){return map.keySet().iterator();}

    public int size(){return map.size();}

    public boolean isEmpty(){return map.isEmpty();}

    public boolean contains(Object o){return map.containsKey(o);}

    public boolean add(E e){return map.put(e,DEFAULT_VALUE)==null;}

    public boolean remove(Object o){return map.remove(o)==DEFAULT_VALUE;}

    public void clear(){map.clear();}
}
