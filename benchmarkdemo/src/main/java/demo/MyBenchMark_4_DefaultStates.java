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
@State(Scope.Thread)
public class MyBenchMark_4_DefaultStates {
    double x = Math.PI;

    @Benchmark
    public void defaultStates(){
        x++;
    }


    public static void main(String[] args) throws RunnerException {
        Options ops = new OptionsBuilder()
                .include(MyBenchMark_4_DefaultStates.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();
        new Runner(ops).run();
    }

    /*
    * /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java "-javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=59297:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath /Users/yuhaiyang/GitHub/haiyang/learnSpace/benchmarkdemo/target/classes:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar:/Users/yuhaiyang/.m2/repository/net/sf/jopt-simple/jopt-simple/4.6/jopt-simple-4.6.jar:/Users/yuhaiyang/.m2/repository/org/apache/commons/commons-math3/3.2/commons-math3-3.2.jar:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-generator-annprocess/1.20/jmh-generator-annprocess-1.20.jar demo.MyBenchMark_DefaultStates
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.openjdk.jmh.util.Utils (file:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar) to field java.io.PrintStream.charOut
WARNING: Please consider reporting this to the maintainers of org.openjdk.jmh.util.Utils
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=59297:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: demo.MyBenchMark_DefaultStates.defaultStates

# Run progress: 0.00% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 279160484.865 ops/s
# Warmup Iteration   2: 274852729.406 ops/s
# Warmup Iteration   3: 276387867.134 ops/s
# Warmup Iteration   4: 284749608.286 ops/s
# Warmup Iteration   5: 270933386.609 ops/s
Iteration   1: 282321450.703 ops/s
Iteration   2: 280397028.291 ops/s
Iteration   3: 286416936.283 ops/s
Iteration   4: 289565996.073 ops/s
Iteration   5: 279461733.048 ops/s


Result "demo.MyBenchMark_DefaultStates.defaultStates":
  283632628.880 ±(99.9%) 16398260.632 ops/s [Average]
  (min, avg, max) = (279461733.048, 283632628.880, 289565996.073), stdev = 4258576.211
  CI (99.9%): [267234368.248, 300030889.512] (assumes normal distribution)


# Run complete. Total time: 00:00:12

Benchmark                                 Mode  Cnt          Score          Error  Units
MyBenchMark_DefaultStates.defaultStates  thrpt    5  283632628.880 ± 16398260.632  ops/s

Process finished with exit code 0

    * */

}
