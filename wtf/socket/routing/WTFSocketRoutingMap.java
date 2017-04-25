package wtf.socket.routing;

import org.apache.commons.lang.StringUtils;
import wtf.socket.io.term.WTFSocketTerm;
import wtf.socket.routing.item.WTFSocketRoutingDebugItem;
import wtf.socket.routing.item.WTFSocketRoutingFormalItem;
import wtf.socket.routing.item.WTFSocketRoutingItem;
import wtf.socket.routing.item.WTFSocketRoutingTmpItem;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由表
 *
 * Created by zfly on 2017/4/22.
 */
public class WTFSocketRoutingMap {

    private final String name;
    private ConcurrentHashMap<String, WTFSocketRoutingItem> firstKeys = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> secondKeys = new ConcurrentHashMap<>();

    WTFSocketRoutingMap(String name) {
        this.name = name;
    }

    /**
     * 向路由表中注册对象
     * 如果对象地址不为空，则使用地址作为key
     * 如果对象地址为空，则使用ioTag作为key
     *
     * @param term 连接终端
     */
    public void register(WTFSocketTerm term) {
        if (StringUtils.equals(name, "TMP")) {
            register(new WTFSocketRoutingTmpItem(term));
        }
    }


    /**
     * 向路由表中注册对象
     * 如果对象地址不为空，则使用地址作为key
     * 如果对象地址为空，则使用ioTag作为key
     *
     * @param item 路由表对象
     */
    public void register(WTFSocketRoutingItem item) {

        final String first = item.getTerm().getIoTag();
        final String second = item.getAddress();

        // 重复注册
        if (second != null && secondKeys.containsKey(second)) {
            // 关闭原连接
            final String repeatKey = secondKeys.get(second);
            final WTFSocketRoutingItem repeatItem = firstKeys.get(repeatKey);

            if (repeatItem.isCover()) {
                repeatItem.getTerm().close();
                firstKeys.remove(repeatKey);
                secondKeys.put(second, first);
                firstKeys.put(first, item);
            }
        }else {
            if (second != null) {
                secondKeys.put(second, first);
            }
            firstKeys.put(first, item);
        }
    }

    /**
     * 路由表注销对象
     *
     * @param item 路由表对象
     */
    public void unRegister(WTFSocketRoutingItem item) {

        final String first = item.getTerm().getIoTag();
        final String second = StringUtils.equals(name, "TMP") ? null : item.getAddress();

        if (firstKeys.containsKey(first)) {
            firstKeys.remove(first);
        }
        if (second != null && secondKeys.containsKey(second)) {
            secondKeys.remove(second);
        }
    }

    /**
     * 路由表是否包含对象
     *
     * @param key 键
     * @return 是否包含对象
     */
    public boolean contains(String key) {
        return firstKeys.containsKey(key) || secondKeys.containsKey(key);
    }

    /**
     * 获取表中的一个对象
     *
     * @param key 键
     * @return 路由表对象
     */
    public WTFSocketRoutingItem getItem(String key) {
        WTFSocketRoutingItem item = firstKeys.get(key);
        if (item == null && secondKeys.containsKey(key)) {
            item = firstKeys.get(secondKeys.get(key));
        }
        return item;
    }

    /**
     * 将对象移动到另一个路由表
     * 只能有TMP表向FORMAL获取DEBUG表移动
     *
     * @param item 路由表对象
     * @param dst 目的路由表
     */
    public void shift(WTFSocketRoutingItem item, WTFSocketRoutingMap dst) {
        if (!StringUtils.equals(name, "TMP")) {
            return;
        }
        if (StringUtils.equals(dst.name, "FORMAL")) {
            dst.register(new WTFSocketRoutingFormalItem(item));
        }
        unRegister(item);
        if (StringUtils.equals(dst.name, "DEBUG")) {
            dst.register(new WTFSocketRoutingDebugItem(item));
        }
    }

    /**
     * 获取表中的所有对象
     *
     * @return 表中的所有对象
     */
    public Collection<WTFSocketRoutingItem> values() {
        return firstKeys.values();
    }

    /**
     * 获取路由表名字
     *
     * @return 路由表名字
     */
    public String name() {
        return name;
    }
}
