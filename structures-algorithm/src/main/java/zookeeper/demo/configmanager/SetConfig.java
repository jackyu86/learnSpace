package zookeeper.demo.configmanager;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

public class SetConfig {

	private static String url = "127.0.0.1:4180";
	private final static String root = "/myConf";
	
	private final static String auth_type = "disgest";
	private final static String auth_passwd = "password";
	
	private final static String mysqlUrl = "127.0.0.1:3306";
	private final static String userName = "root";
	private final static String passWord = "123";
	
	
	public static void main(String[] args) throws Exception {
		
		ZooKeeper zooKeeper = new ZooKeeper(url, 3000, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {

				System.out.println("触发了事件 Path:"+event.getPath()+",type"+event.getType());
			}
		});
		//没连接上
		while(ZooKeeper.States.CONNECTED != zooKeeper.getState()){
			Thread.sleep(3000);
		}
		//zooKeeper.addAuthInfo(auth_type, auth_passwd.getBytes());
		
		if(zooKeeper.exists(root,true)==null){
			zooKeeper.create(root,"root1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		if(zooKeeper.exists("/myConf/mysqlUrl",true)==null){
			zooKeeper.create("/myConf/mysqlUrl",mysqlUrl.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		if(zooKeeper.exists("/myConf/userName",true)==null){
			zooKeeper.create("/myConf/userName",userName.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		if(zooKeeper.exists("/myConf/passWord",true)==null){
			zooKeeper.create("/myConf/passWord",passWord.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		
	}
}

