package demo.structures.algorithm.hashring.ConsistentHashing;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by yuhaiyang on 2017/11/14.
 * 封装
 */
public class Shard {
    //虚拟节点
    private TreeMap<Long,Server> nodes;
    //屋里节点
    private List<Server> shards;
    //虚拟节点数量
    private final int NODE_NOS=100;

    public Shard(List<Server> shards) {
        super();
        this.shards = shards;
        init();
    }

    //初始化
    private void init(){
        //初始化一致性hash环
        nodes = new TreeMap<Long, Server>();
        for(int i =0; i<shards.size(); i++){
            final Server serverInfo = shards.get(i);
            for(int n=0;n<NODE_NOS;n++){
                nodes.put(md5hash(serverInfo.toString()+n),serverInfo);
            }
        }
    }

    public void remove(Server server){
        for (int i = 0; i < NODE_NOS; i++){
            nodes.remove(server.toString()+i);
        }

    }

    public Server getShardInfo(String key) {
        SortedMap<Long, Server> tail = nodes.tailMap(md5hash(key)); // 沿环的顺时针找到一个虚拟节点
        if (tail.size() == 0) {
            return nodes.get(nodes.firstKey());
        }
        return tail.get(tail.firstKey()); // 返回该虚拟节点对应的真实机器节点的信息
    }

    /**
     * md5 hash
     *
     * */

    private Long md5hash(String key){
        MessageDigest md5 = null;
        if (md5 == null) {
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("no md5 algrithm found");
            }
        }

        md5.reset();
        md5.update(key.getBytes());
        byte[] bKey = md5.digest();
        //具体的哈希函数实现细节--每个字节 & 0xFF 再移位
        long result = ((long) (bKey[3] & 0xFF) << 24)
                | ((long) (bKey[2] & 0xFF) << 16
                | ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF));
        return result & 0xffffffffL;
    }



    /**
     * hash ring
     *  MurMurHash算法，是非加密HASH算法，性能很高，
     *  比传统的CRC32,MD5，SHA-1（这两个算法都是加密HASH算法，复杂度本身就很高，带来的性能上的损害也不可避免）
     *  等HASH算法要快很多，而且据说这个算法的碰撞率很低.
     *  http://murmurhash.googlepages.com/
     */
    private Long hash(String key){
        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
        int seed =0x1234ABCD;
        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        long m = 0xc6a4a7935bd1e995L;
        int r = 47;
        long h = seed ^ (buf.remaining()*m);
        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(
                    ByteOrder.LITTLE_ENDIAN);
            // for big-endian version, do this first:
            // finish.position(8-buf.remaining());
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }


}
