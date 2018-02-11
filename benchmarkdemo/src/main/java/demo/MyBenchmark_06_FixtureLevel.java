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
public class MyBenchmark_06_FixtureLevel {

    double x;

    //迭代就会产生失败 -> Warmup
    @TearDown(Level.Iteration)
    public void check() {
        assert x > Math.PI : "Nothing changed?";
    }

    @Benchmark
    public void measureRight() {
        x++;
    }

    @Benchmark
    public void measureWrong() {
        double x = 0;
        x++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark_06_FixtureLevel.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .jvmArgs("-ea")
                .shouldFailOnError(false) // switch to "true" to fail the complete run
                .build();

        new Runner(opt).run();
    }
    /*
    * /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java "-javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=61084:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath /Users/yuhaiyang/GitHub/haiyang/learnSpace/benchmarkdemo/target/classes:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar:/Users/yuhaiyang/.m2/repository/net/sf/jopt-simple/jopt-simple/4.6/jopt-simple-4.6.jar:/Users/yuhaiyang/.m2/repository/org/apache/commons/commons-math3/3.2/commons-math3-3.2.jar:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-generator-annprocess/1.20/jmh-generator-annprocess-1.20.jar demo.MyBenchmark_06_FixtureLevel
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.openjdk.jmh.util.Utils (file:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar) to field java.io.PrintStream.charOut
WARNING: Please consider reporting this to the maintainers of org.openjdk.jmh.util.Utils
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -ea
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: demo.MyBenchmark_06_FixtureLevel.measureRight

# Run progress: 0.00% complete, ETA 00:00:20
# Fork: 1 of 1
# Warmup Iteration   1: 277542198.518 ops/s
# Warmup Iteration   2: 275761487.604 ops/s
# Warmup Iteration   3: 283656767.350 ops/s
# Warmup Iteration   4: 282285618.959 ops/s
# Warmup Iteration   5: 287817439.941 ops/s
Iteration   1: 281413880.408 ops/s
Iteration   2: 277081042.898 ops/s
Iteration   3: 268599316.061 ops/s
Iteration   4: 195595862.618 ops/s
Iteration   5: 212049362.292 ops/s


Result "demo.MyBenchmark_06_FixtureLevel.measureRight":
  246947892.855 ±(99.9%) 154261832.499 ops/s [Average]
  (min, avg, max) = (195595862.618, 246947892.855, 281413880.408), stdev = 40061308.021
  CI (99.9%): [92686060.356, 401209725.355] (assumes normal distribution)


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -ea
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: demo.MyBenchmark_06_FixtureLevel.measureWrong

# Run progress: 50.00% complete, ETA 00:00:12
# Fork: 1 of 1
# Warmup Iteration   1: <failure>

java.lang.AssertionError: Nothing changed?
	at demo.MyBenchmark_06_FixtureLevel.check(MyBenchmark_06_FixtureLevel.java:59)
	at demo.generated.MyBenchmark_06_FixtureLevel_measureWrong_jmhTest.measureWrong_Throughput(MyBenchmark_06_FixtureLevel_measureWrong_jmhTest.java:95)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:564)
	at org.openjdk.jmh.runner.BenchmarkHandler$BenchmarkTask.call(BenchmarkHandler.java:453)
	at org.openjdk.jmh.runner.BenchmarkHandler$BenchmarkTask.call(BenchmarkHandler.java:437)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:514)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
	at java.base/java.lang.Thread.run(Thread.java:844)




# Run complete. Total time: 00:00:13

Benchmark                                  Mode  Cnt          Score           Error  Units
MyBenchmark_06_FixtureLevel.measureRight  thrpt    5  246947892.855 ± 154261832.499  ops/s

Process finished with exit code 0

    * */

}
