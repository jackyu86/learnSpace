package cn.jpush.dubbo.thrift.proxy;

/**
 * Created by yuhaiyang on 2017/10/20.
 */
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.protocol.AbstractProxyProtocol;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.reflect.ReflectRequestor;
import org.apache.avro.ipc.reflect.ReflectResponder;

import java.net.InetSocketAddress;

/**
 * dubbo开始迭代可喜可贺 17-10
 */
public class AvroProtocol extends AbstractProxyProtocol {
    public static final int DEFAULT_PORT = 40881;
    private static final Logger logger = LoggerFactory.getLogger(AvroProtocol.class);

    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    protected <T> Runnable doExport(T impl, Class<T> type, URL url)
            throws RpcException {

        logger.info("impl => " + impl.getClass());
        logger.info("type => " + type.getName());
        logger.info("url => " + url);

        final Server server = new NettyServer(new ReflectResponder(type, impl),
                new InetSocketAddress(url.getHost(), url.getPort()));
        server.start();

        return new Runnable() {
            public void run() {
                try {
                    logger.info("Close Avro Server");
                    server.close();
                } catch (Throwable e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        };
    }

    @Override
    protected <T> T doRefer(Class<T> type, URL url) throws RpcException {

        logger.info("type => " + type.getName());
        logger.info("url => " + url);

        try {
            NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(url.getHost(), url.getPort()));
            T ref = ReflectRequestor.getClient(type, client);
            logger.info("Create Avro Client");
            return ref;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RpcException("Fail to create remoting client for service(" + url + "): " + e.getMessage(), e);
        }
    }

}