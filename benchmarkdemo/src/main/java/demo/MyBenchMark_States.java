package demo;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @Author: jack-yu
 * @Description:
 */
public class MyBenchMark_States {

    @State(Scope.Benchmark)
    public static class BenchmarkState{
        volatile double x = Math.PI;
    }

    @State(Scope.Thread)
    public static class ThreadState{
        volatile double x = Math.PI;
    }

    @Benchmark
    public void measureUnshared(ThreadState threadState){
        threadState.x++;
    }
    @Benchmark
    public void measureShared(BenchmarkState benchmarkState){
        benchmarkState.x++;
    }


    public static void main(String[] args) throws RunnerException {
        Options ops = new OptionsBuilder()
                .include(MyBenchMark_States.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .threads(4)
                .forks(1)
                .build();
        new Runner(ops).run();
    }

}
