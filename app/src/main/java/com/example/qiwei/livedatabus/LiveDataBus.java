package com.example.qiwei.livedatabus;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名称：
 * 类功能：
 * 类作者：qiwei
 * 类日期：2019/6/20.
 **/
public class LiveDataBus {
    private final Map<String, MutableLiveData<Object>> bus;
    private LiveDataBus(){
        bus = new HashMap<>();
    }

    private static class SingletonHolder{
        private static final LiveDataBus DEFAULT_BUS = new LiveDataBus();
    }

    public static LiveDataBus get(){
        return SingletonHolder.DEFAULT_BUS;
    }

    public synchronized <T> MutableLiveData<T> with(String key,Class<T> type){
        if(!bus.containsKey(key)){
            bus.put(key,new BusMutableLiveData<Object>());
        }
        return (MutableLiveData<T>) bus.get(key);
    }

    /**
     * 重写LiveData的实现类的observe方法 在observe方法中进行hook
     * @param <T>
     */
    public static class BusMutableLiveData<T> extends MutableLiveData<T>{

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            super.observe(owner, observer);
            try{
                hook(observer);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void hook(Observer<T> observer) throws Exception{
            //observer.mLastVersion = mVersion;
            //首先获取到LiveData的class
            Class<LiveData> classLiveData = LiveData.class;
            //通过反射获取该class里面的mObserver属性对象
            Field fieldObservers = classLiveData.getDeclaredField("mObservers");
            //设置属性可以被访问
            fieldObservers.setAccessible(true);
            //获取的字段是this这个对象中的值，它的值是一个Map集合
            Object objectObervers = fieldObservers.get(this);
            //获取到map对象的类型
            Class<?> classObervers = objectObervers.getClass();
            //获取到map中所有的get方法
            Method methodGet = classObervers.getDeclaredMethod("get", Object.class);
            //设置get方法可以被访问
            methodGet.setAccessible(true);
            //执行该get方法 传入objectObservers对象，然后传入observer作为key获取值
            Object objectWrapperEntry = methodGet.invoke(objectObervers, observer);
            //定义一个空的Object对象
            Object objectWrapper = null;
            //判断objectWrapperEntry是否是Map.Entry类型
            if(objectWrapperEntry instanceof Map.Entry){
                //如果是 获取值并强转成Object对象
                objectWrapper = ((Map.Entry) objectWrapperEntry).getValue();
            }
            //判断是否为空
            if (objectWrapper == null) {
                throw new NullPointerException("wrapper can not be null!");
            }
            //如果不是空 就得到该Object对象的父类
            Class<?> classObserverWrapper = objectWrapper.getClass().getSuperclass();
            //通过他的父类的class对象 获取到mLastVersion字段
            Field fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion");
            //设置mLastVersion字段可被访问
            fieldLastVersion.setAccessible(true);
            //获取到mVersion值
            Field fieldVersion = classLiveData.getDeclaredField("mVersion");
            //设置该值可以被访问到
            fieldVersion.setAccessible(true);
            //获取fieldVersion对象的类型
            Object objectVersion = fieldVersion.get(this);
            //把mVersion字段的属性值设置给mLastVersion字段
            fieldLastVersion.set(objectWrapper,objectVersion);
        }
    }
}
