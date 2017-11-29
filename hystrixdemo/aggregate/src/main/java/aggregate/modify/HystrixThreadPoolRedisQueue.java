package aggregate.modify;

import java.util.concurrent.ThreadPoolExecutor;

import com.netflix.hystrix.HystrixThreadPool;
import rx.Scheduler;
import rx.functions.Func0;

/**
 * Created by yuhaiyang on 2017/11/14.
 */
public  class HystrixThreadPoolRedisQueue implements HystrixThreadPool {
    @Override
    public ThreadPoolExecutor getExecutor() {
        return null;
    }

    @Override
    public Scheduler getScheduler() {
        return null;
    }

    @Override
    public Scheduler getScheduler(Func0<Boolean> shouldInterruptThread) {
        return null;
    }

    @Override
    public void markThreadExecution() {

    }

    @Override
    public void markThreadCompletion() {

    }

    @Override
    public void markThreadRejection() {

    }

    @Override
    public boolean isQueueSpaceAvailable() {
        return false;
    }
}
