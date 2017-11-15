package demo.structures.algorithm.hashring.ConsistentHashing;

/**
 * Created by yuhaiyang on 2017/11/14.
 * 封装节点信息
 */
public class Server {
    private String ip;
    private String name;
    private String passwd;
    private int port;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    @Override
    public String toString() {
        return this.ip+"--"+this.port;
    }
}
