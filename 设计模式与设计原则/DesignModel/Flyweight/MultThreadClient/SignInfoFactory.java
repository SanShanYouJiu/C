package DesignMode.DesignModel.Flyweight.MultThreadClient;

import java.util.HashMap;

/**
 * Created by han on 2017/3/13.
 */
public class SignInfoFactory {


    private static HashMap<String, SignInfo> pool = new HashMap<String, SignInfo>();

    @Deprecated
    public static SignInfo getSignInfo() {
        return new SignInfo();
    }

    //从池中获取对象
    public  synchronized   static SignInfo getSignInfo(String key) {
        SignInfo result = null;
        if (!pool.containsKey(key)) {
            System.out.println(key + "-----建立对象 并放置到池中");
            result = new SignInfo();
            pool.put(key, result);
        }else {
            result = pool.get(key);
            System.out.println(key + "----直接从池中取得");
        }
        return result;
    }
}
