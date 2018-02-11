/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package demo;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
public class MyBenchmark_05_StateFixtures {

    double x;


    /*
     * Ok, let's prepare our benchmark:
     * 执行开始
     */

    @Setup
    public void prepare() {
        x = Math.PI;
    }

    /*
     * And, check the benchmark went fine afterwards:
     * 执行结束
     */

    @TearDown
    public void check() {
        assert x > Math.PI : "Nothing changed?";
    }

    /*
     * This method obviously does the right thing, incrementing the field x
     * in the benchmark state. check() will never fail this way, because
     * we are always guaranteed to have at least one benchmark call.
     */

    @Benchmark
    public void measureRight() {
        x++;
    }

    /*
     * This method, however, will fail the check(), because we deliberately
     * have the "typo", and increment only the local variable. This should
     * not pass the check, and JMH will fail the run.
     */

    @Benchmark
    public void measureWrong() {
        double x = 0;
        x++;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark_05_StateFixtures.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .jvmArgs("-ea")
                .build();

        new Runner(opt).run();
    }
//  /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java "-javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=60325:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath /Users/yuhaiyang/GitHub/haiyang/learnSpace/benchmarkdemo/target/classes:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar:/Users/yuhaiyang/.m2/repository/net/sf/jopt-simple/jopt-simple/4.6/jopt-simple-4.6.jar:/Users/yuhaiyang/.m2/repository/org/apache/commons/commons-math3/3.2/commons-math3-3.2.jar:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-generator-annprocess/1.20/jmh-generator-annprocess-1.20.jar demo.MyBenchmark_05_StateFixtures
//    WARNING: An illegal reflective access operation has occurred
//    WARNING: Illegal reflective access by org.openjdk.jmh.util.Utils (file:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar) to field java.io.PrintStream.charOut
//    WARNING: Please consider reporting this to the maintainers of org.openjdk.jmh.util.Utils
//    WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
//    WARNING: All illegal access operations will be denied in a future release
//# JMH version: 1.20
//            # VM version: JDK 9.0.4, VM 9.0.4+11
//            # VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
//# VM options: -ea
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Throughput, ops/time
//# Benchmark: demo.MyBenchmark_05_StateFixtures.measureRight
//
//# Run progress: 0.00% complete, ETA 00:00:20
//            # Fork: 1 of 1
//            # Warmup Iteration   1: 68274939.673 ops/s
//# Warmup Iteration   2: 59306975.125 ops/s
//# Warmup Iteration   3: 60941477.846 ops/s
//# Warmup Iteration   4: 79084429.564 ops/s
//# Warmup Iteration   5: 140418585.008 ops/s
//    Iteration   1: 248221498.663 ops/s
//    Iteration   2: 285365473.317 ops/s
//    Iteration   3: 279779806.187 ops/s
//    Iteration   4: 254197815.915 ops/s
//    Iteration   5: 263669472.162 ops/s
//
//
//    Result "demo.MyBenchmark_05_StateFixtures.measureRight":
//            266246813.249 ±(99.9%) 61652346.280 ops/s [Average]
//            (min, avg, max) = (248221498.663, 266246813.249, 285365473.317), stdev = 16010918.544
//    CI (99.9%): [204594466.968, 327899159.529] (assumes normal distribution)
//
//
//            # JMH version: 1.20
//            # VM version: JDK 9.0.4, VM 9.0.4+11
//            # VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
//# VM options: -ea
//# Warmup: 5 iterations, 1 s each
//# Measurement: 5 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Throughput, ops/time
//# Benchmark: demo.MyBenchmark_05_StateFixtures.measureWrong
//
//# Run progress: 50.00% complete, ETA 00:00:13
//            # Fork: 1 of 1
//            # Warmup Iteration   1: 2385393374.478 ops/s
//# Warmup Iteration   2: 2474775313.290 ops/s
//# Warmup Iteration   3: 2480452772.240 ops/s
//# Warmup Iteration   4: 2445125848.048 ops/s
//# Warmup Iteration   5: 1706605873.474 ops/s
//    Iteration   1: 2309625954.390 ops/s
//    Iteration   2: 2357016080.276 ops/s
//    Iteration   3: 2380493422.064 ops/s
//    Iteration   4: 2378485929.918 ops/s
//    Iteration   5: <failure>
//
//    java.lang.AssertionError: Nothing changed?
//    at demo.MyBenchmark_05_StateFixtures.check(MyBenchmark_05_StateFixtures.java:60)
//    at demo.generated.MyBenchmark_05_StateFixtures_measureWrong_jmhTest.measureWrong_Throughput(MyBenchmark_05_StateFixtures_measureWrong_jmhTest.java:97)
//    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
//    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//    at java.base/java.lang.reflect.Method.invoke(Method.java:564)
//    at org.openjdk.jmh.runner.BenchmarkHandler$BenchmarkTask.call(BenchmarkHandler.java:453)
//    at org.openjdk.jmh.runner.BenchmarkHandler$BenchmarkTask.call(BenchmarkHandler.java:437)
//    at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
//    at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:514)
//    at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
//    at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
//    at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
//    at java.base/java.lang.Thread.run(Thread.java:844)
//
//
//
//
//    Result "demo.MyBenchmark_05_StateFixtures.measureWrong":
//            2356405346.662 ±(99.9%) 212901777.480 ops/s [Average]
//            (min, avg, max) = (2309625954.390, 2356405346.662, 2380493422.064), stdev = 32946785.733
//    CI (99.9%): [2143503569.182, 2569307124.141] (assumes normal distribution)
//
//
//            # Run complete. Total time: 00:00:24
//
//    Benchmark                                   Mode  Cnt           Score           Error  Units
//    MyBenchmark_05_StateFixtures.measureRight  thrpt    5   266246813.249 ±  61652346.280  ops/s
//    MyBenchmark_05_StateFixtures.measureWrong  thrpt    4  2356405346.662 ± 212901777.480  ops/s
//
//    Process finished with exit code 0


}
