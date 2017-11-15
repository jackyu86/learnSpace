package demo.structures.algorithm.hashring.ConsistentHashing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuhaiyang on 2017/11/14.
 */
public class ConsistentHashing {

    public static void main(String[] args) {

        List<Server> shards = new ArrayList<Server>();
        Server s1  = new Server("192.168.1.81",6379);
        Server s2  = new Server("192.168.1.82", 6379);
        Server s3  = new Server("192.168.1.83", 6379);
        Server s4  = new Server("192.168.1.84", 6379);
        Server s5  = new Server("192.168.1.85", 6379);
        Server s6  = new Server("192.168.1.86", 6379);

        shards.add(s1);
        shards.add(s2);
        shards.add(s3);
        shards.add(s4);
        shards.add(s5);
        shards.add(s6);

        Shard shard = new Shard (shards);

//        shard.save("bjsxt","asdfsdfsdf");

//        shard.get("bjsxt");

        System.out.println(shard.getShardInfo("bjsxtlxasdsdf"));
        System.out.println(shard.getShardInfo("bjsxtlxas"));
        System.out.println(shard.getShardInfo("gsdfsd"));
        System.out.println(shard.getShardInfo("asdasd"));
        System.out.println(shard.getShardInfo("hrtdf"));
        System.out.println(shard.getShardInfo("bjsxtlxas"));
        System.out.println(shard.getShardInfo("asdasd"));

//        new Jedis(sha)

    }
}
