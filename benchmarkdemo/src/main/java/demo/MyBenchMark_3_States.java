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
public class MyBenchMark_3_States {

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
                .include(MyBenchMark_3_States.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .threads(4)
                .forks(1)
                .build();
        new Runner(ops).run();
    }
    /*
    * /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java "-javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=58633:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath /Users/yuhaiyang/GitHub/haiyang/learnSpace/benchmarkdemo/target/classes:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar:/Users/yuhaiyang/.m2/repository/net/sf/jopt-simple/jopt-simple/4.6/jopt-simple-4.6.jar:/Users/yuhaiyang/.m2/repository/org/apache/commons/commons-math3/3.2/commons-math3-3.2.jar:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-generator-annprocess/1.20/jmh-generator-annprocess-1.20.jar demo.MyBenchMark_States
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.openjdk.jmh.util.Utils (file:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar) to field java.io.PrintStream.charOut
WARNING: Please consider reporting this to the maintainers of org.openjdk.jmh.util.Utils
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=58633:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: demo.MyBenchMark_States.measureShared

# Run progress: 0.00% complete, ETA 00:00:20
# Fork: 1 of 1
# Warmup Iteration   1: 57861593.614 ops/s
# Warmup Iteration   2: 46551065.608 ops/s
# Warmup Iteration   3: 46115585.035 ops/s
# Warmup Iteration   4: 46238620.611 ops/s
# Warmup Iteration   5: 45922052.761 ops/s
Iteration   1: 45751102.696 ops/s
Iteration   2: 46135904.679 ops/s
Iteration   3: 45576189.405 ops/s
Iteration   4: 46310832.662 ops/s
Iteration   5: 46090579.400 ops/s


Result "demo.MyBenchMark_States.measureShared":
  45972921.768 ±(99.9%) 1157102.143 ops/s [Average]q
  (min, avg, max) = (45576189.405, 45972921.768, 46310832.662), stdev = 300495.752
  CI (99.9%): [44815819.626, 47130023.911] (assumes normal distribution)


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=58633:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: demo.MyBenchMark_States.measureUnshared

# Run progress: 50.00% complete, ETA 00:00:12
# Fork: 1 of 1
# Warmup Iteration   1: 255057840.740 ops/s
# Warmup Iteration   2: 279020927.028 ops/s
# Warmup Iteration   3: 278086775.446 ops/s
# Warmup Iteration   4: 275720360.826 ops/s
# Warmup Iteration   5: 257076567.343 ops/s
Iteration   1: 273778180.153 ops/s
Iteration   2: 260086824.621 ops/s
Iteration   3: 278492479.410 ops/s
Iteration   4: 278301067.695 ops/s
Iteration   5: 280049820.806 ops/s


Result "demo.MyBenchMark_States.measureUnshared":
  274141674.537 ±(99.9%) 31566308.650 ops/s [Average]
  (min, avg, max) = (260086824.621, 274141674.537, 280049820.806), stdev = 8197670.113
  CI (99.9%): [242575365.887, 305707983.187] (assumes normal distribution)


# Run complete. Total time: 00:00:24

Benchmark                            Mode  Cnt          Score          Error  Units
MyBenchMark_States.measureShared    thrpt    5   45972921.768 ±  1157102.143  ops/s
MyBenchMark_States.measureUnshared  thrpt    5  274141674.537 ± 31566308.650  ops/s

Process finished with exit code 0

    *
    * */

}
