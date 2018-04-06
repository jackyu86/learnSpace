package other;

import java.util.HashMap;

/**
 * @Author: jack-yu
 * @Description:
 */
public class LRU<K, V> {
    // private int currentCacheSize;
    private int cacheCapcity;
    private HashMap<K, CacheNode> caches;
    private CacheNode head;
    private CacheNode tail;

    public LRU(int size) {

        //this.currentCacheSize = 0;
        this.cacheCapcity = size;
        //初始化HashMap 大小
        caches = new HashMap<K, CacheNode>(cacheCapcity);
    }

    class CacheNode<K, N> {
        private CacheNode pre;
        private CacheNode next;
        K key;
        N value;

        public CacheNode() {

        }
    }

    public void put(K key, V value) {
        CacheNode cacheNode = caches.get(key);
        //新加入缓存
        if (cacheNode == null) {
            if (caches.size() >= cacheCapcity) {
                caches.remove(this.tail.key);
                removeTail();
            }
            cacheNode = new CacheNode();
            cacheNode.key = key;
        }
        cacheNode.value = value;
        move2Head(cacheNode);
        caches.put(key, cacheNode);
        //toString();
    }

    public Object get(K k) {
        CacheNode node = caches.get(k);
        if (node == null) {
            return null;
        }
        move2Head(node);
        return node.value;
    }

    public CacheNode remove(K key) {
        CacheNode node = caches.get(key);
        if (node != null) {
            if (node.pre != null) {
                node.pre.next = node.next;
            }
            if (node.next != null) {
                node.next.pre = node.pre;
            }
            if (node == head) {
                head = node.next;
            }
            if (node == tail) {
                tail = node.pre;
            }
        }

        return caches.remove(key);
    }

    //移动到头部
    private void move2Head(CacheNode cacheNode) {
        //是否head
        if (this.head == cacheNode) {
            return;
        }
        //处理有下一个节点
        if (cacheNode.next != null) {
            cacheNode.next.pre = cacheNode.pre;
        }
        //处理有上一个节点
        if (cacheNode.pre != null) {
            cacheNode.pre.next = cacheNode.next;
        }
        if (cacheNode == this.tail) {
            tail = tail.pre;
        }
        if (this.head == null || this.tail == null) {
            this.head = this.tail = cacheNode;
            return;
        }
        cacheNode.next = this.head;
        this.head.pre = cacheNode;
        this.head = cacheNode;
        this.head.pre = null;
    }

    //清理尾部
    private void removeTail() {
        if (this.tail != null) {
            this.tail = this.tail.pre;
            if (this.tail == null) {
                //唯一一个了
                this.head = null;
            } else {
                this.tail.next = null;
            }
        }
    }

    public void clear() {
        head = null;
        tail = null;
        caches.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        CacheNode node = head;
        while (node != null) {
            sb.append(String.format("%s:%s ", node.key, node.value));
            node = node.next;
        }

        return sb.toString();
    }

    public static void main(String[] args) {

        LRU<Integer, String> lru = new LRU<Integer, String>(3);

        lru.put(1, "a");    // 1:a
        System.out.println(lru.toString());
        lru.put(2, "b");    // 2:b 1:a
        System.out.println(lru.toString());
        lru.put(3, "c");    // 3:c 2:b 1:a
        System.out.println(lru.toString());
        lru.put(4, "d");    // 4:d 3:c 2:b
        System.out.println(lru.toString());
        lru.put(1, "aa");   // 1:aa 4:d 3:c
        System.out.println(lru.toString());
        lru.put(2, "bb");   // 2:bb 1:aa 4:d
        System.out.println(lru.toString());
        lru.put(5, "e");    // 5:e 2:bb 1:aa
        System.out.println(lru.toString());
        lru.get(1);         // 1:aa 5:e 2:bb
        System.out.println(lru.toString());
        lru.remove(11);     // 1:aa 5:e 2:bb
        System.out.println(lru.toString());
        lru.remove(1);      //5:e 2:bb
        System.out.println(lru.toString());
        lru.put(1, "aaa");  //1:aaa 5:e 2:bb
        System.out.println(lru.toString());
    }
}
