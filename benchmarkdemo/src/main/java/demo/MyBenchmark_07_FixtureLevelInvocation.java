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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MyBenchmark_07_FixtureLevelInvocation {


    @State(Scope.Benchmark)
    public static class NormalState {
        ExecutorService service;

        @Setup(Level.Trial)
        public void up() {
            service = Executors.newCachedThreadPool();
        }

        @TearDown(Level.Trial)
        public void down() {
            service.shutdown();
        }

    }

    /*
     * This is the *extension* of the basic state, which also
     * has the Level.Invocation fixture method, sleeping for some time.
     */

    public static class LaggingState extends NormalState {
        public static final int SLEEP_TIME = Integer.getInteger("sleepTime", 10);

        @Setup(Level.Invocation)
        public void lag() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);
        }
    }

    /*
     * This allows us to formulate the task: measure the task turnaround in
     * "hot" mode when we are not sleeping between the submits, and "cold" mode,
     * when we are sleeping.
     */

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public double measureHot(NormalState e, final Scratch s) throws ExecutionException, InterruptedException {
        return e.service.submit(new Task(s)).get();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public double measureCold(LaggingState e, final Scratch s) throws ExecutionException, InterruptedException {
        return e.service.submit(new Task(s)).get();
    }

    /*
     * This is our scratch state which will handle the work.
     */

    @State(Scope.Thread)
    public static class Scratch {
        private double p;
        public double doWork() {
            p = Math.log(p);
            return p;
        }
    }

    public static class Task implements Callable<Double> {
        private Scratch s;

        public Task(Scratch s) {
            this.s = s;
        }

        @Override
        public Double call() {
            return s.doWork();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark_07_FixtureLevelInvocation.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
    /**
     * /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java "-javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=61585:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath /Users/yuhaiyang/GitHub/haiyang/learnSpace/benchmarkdemo/target/classes:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar:/Users/yuhaiyang/.m2/repository/net/sf/jopt-simple/jopt-simple/4.6/jopt-simple-4.6.jar:/Users/yuhaiyang/.m2/repository/org/apache/commons/commons-math3/3.2/commons-math3-3.2.jar:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-generator-annprocess/1.20/jmh-generator-annprocess-1.20.jar demo.MyBenchmark_07_FixtureLevelInvocation
     WARNING: An illegal reflective access operation has occurred
     WARNING: Illegal reflective access by org.openjdk.jmh.util.Utils (file:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar) to field java.io.PrintStream.charOut
     WARNING: Please consider reporting this to the maintainers of org.openjdk.jmh.util.Utils
     WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
     WARNING: All illegal access operations will be denied in a future release
     # JMH version: 1.20
     # VM version: JDK 9.0.4, VM 9.0.4+11
     # VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
     # VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=61585:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
     # Warmup: 5 iterations, 1 s each
     # Measurement: 5 iterations, 1 s each
     # Timeout: 10 min per iteration
     # Threads: 1 thread, will synchronize iterations
     # Benchmark mode: Average time, time/op
     # Benchmark: demo.MyBenchmark_07_FixtureLevelInvocation.measureCold

     # Run progress: 0.00% complete, ETA 00:00:20
     # Fork: 1 of 1
     # Warmup Iteration   1: 229.399 us/op
     # Warmup Iteration   2: 155.159 us/op
     # Warmup Iteration   3: 160.598 us/op
     # Warmup Iteration   4: 206.990 us/op
     # Warmup Iteration   5: 153.036 us/op
     Iteration   1: 82.212 us/op
     Iteration   2: 85.354 us/op
     Iteration   3: 84.791 us/op
     Iteration   4: 114.659 us/op
     Iteration   5: 107.261 us/op


     Result "demo.MyBenchmark_07_FixtureLevelInvocation.measureCold":
     94.855 ±(99.9%) 57.680 us/op [Average]
     (min, avg, max) = (82.212, 94.855, 114.659), stdev = 14.979
     CI (99.9%): [37.175, 152.535] (assumes normal distribution)


     # JMH version: 1.20
     # VM version: JDK 9.0.4, VM 9.0.4+11
     # VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
     # VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=61585:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
     # Warmup: 5 iterations, 1 s each
     # Measurement: 5 iterations, 1 s each
     # Timeout: 10 min per iteration
     # Threads: 1 thread, will synchronize iterations
     # Benchmark mode: Average time, time/op
     # Benchmark: demo.MyBenchmark_07_FixtureLevelInvocation.measureHot

     # Run progress: 50.00% complete, ETA 00:00:13
     # Fork: 1 of 1
     # Warmup Iteration   1: 19.832 us/op
     # Warmup Iteration   2: 14.778 us/op
     # Warmup Iteration   3: 20.004 us/op
     # Warmup Iteration   4: 15.604 us/op
     # Warmup Iteration   5: 16.617 us/op
     Iteration   1: 16.651 us/op
     Iteration   2: 15.461 us/op
     Iteration   3: 14.762 us/op
     Iteration   4: 16.486 us/op
     Iteration   5: 16.553 us/op


     Result "demo.MyBenchmark_07_FixtureLevelInvocation.measureHot":
     15.982 ±(99.9%) 3.214 us/op [Average]
     (min, avg, max) = (14.762, 15.982, 16.651), stdev = 0.835
     CI (99.9%): [12.768, 19.197] (assumes normal distribution)


     # Run complete. Total time: 00:00:24

     Benchmark                                          Mode  Cnt   Score    Error  Units
     MyBenchmark_07_FixtureLevelInvocation.measureCold  avgt    5  94.855 ± 57.680  us/op
     MyBenchmark_07_FixtureLevelInvocation.measureHot   avgt    5  15.982 ±  3.214  us/op

     Process finished with exit code 0

     * */

}
