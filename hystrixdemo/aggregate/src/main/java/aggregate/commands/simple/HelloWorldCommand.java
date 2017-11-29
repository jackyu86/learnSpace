package aggregate.commands.simple;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPool;
import com.netflix.hystrix.HystrixThreadPoolKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldCommand extends HystrixCommand<String> {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldCommand.class);

    private final String name;

    public HelloWorldCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("default"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("demo"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("demoPool")));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        logger.info("HelloWorld Command Invoked");
        return "Hello " + name;
    }

    @Override
    protected String getFallback() {
        return super.getFallback();
    }
}
