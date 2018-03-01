package demo;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @Author: jack-yu
 * @Description:
 */
public class MyBenchmark_02_desc {
    public MyBenchmark_02_desc() {
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)//计算时间单位内测试数量
    @OutputTimeUnit(TimeUnit.SECONDS)//秒
    public void measureThroughput() throws InterruptedException {
        //毫秒 搭配Throughput  OutputTimeUnit更适合使用秒
        TimeUnit.MILLISECONDS.sleep(100);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public  void measureAvgTime() throws InterruptedException {
        TimeUnit.MICROSECONDS.sleep(100);
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureSamples() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureSingleShot() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureMultiple() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureAll() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark_02_desc.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    /*
    *
    * /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java "-javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath /Users/yuhaiyang/GitHub/haiyang/learnSpace/benchmarkdemo/target/classes:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar:/Users/yuhaiyang/.m2/repository/net/sf/jopt-simple/jopt-simple/4.6/jopt-simple-4.6.jar:/Users/yuhaiyang/.m2/repository/org/apache/commons/commons-math3/3.2/commons-math3-3.2.jar:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-generator-annprocess/1.20/jmh-generator-annprocess-1.20.jar demo.MyBenchmark_2_desc
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.openjdk.jmh.util.Utils (file:/Users/yuhaiyang/.m2/repository/org/openjdk/jmh/jmh-core/1.20/jmh-core-1.20.jar) to field java.io.PrintStream.charOut
WARNING: Please consider reporting this to the maintainers of org.openjdk.jmh.util.Utils
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, 1 s each
# Measurement: 2 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: demo.MyBenchmark_2_desc.measureAll

# Run progress: 0.00% complete, ETA 00:00:36
# Fork: 1 of 1
# Warmup Iteration   1: ≈ 10⁻⁵ ops/us
# Warmup Iteration   2: ≈ 10⁻⁵ ops/us
Iteration   1: ≈ 10⁻⁵ ops/us
Iteration   2: ≈ 10⁻⁵ ops/us


Result "demo.MyBenchmark_2_desc.measureAll":
  ≈ 10⁻⁵ ops/us


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, 1 s each
# Measurement: 2 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: demo.MyBenchmark_2_desc.measureMultiple

# Run progress: 11.11% complete, ETA 00:00:56
# Fork: 1 of 1
# Warmup Iteration   1: ≈ 10⁻⁵ ops/us
# Warmup Iteration   2: ≈ 10⁻⁵ ops/us
Iteration   1: ≈ 10⁻⁵ ops/us
Iteration   2: ≈ 10⁻⁵ ops/us


Result "demo.MyBenchmark_2_desc.measureMultiple":
  ≈ 10⁻⁵ ops/us


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, 1 s each
# Measurement: 2 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: demo.MyBenchmark_2_desc.measureThroughput

# Run progress: 22.21% complete, ETA 00:00:43
# Fork: 1 of 1
# Warmup Iteration   1: 9.664 ops/s
# Warmup Iteration   2: 9.657 ops/s
Iteration   1: 9.582 ops/s
Iteration   2: 9.595 ops/s


Result "demo.MyBenchmark_2_desc.measureThroughput":
  9.589 ops/s


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, 1 s each
# Measurement: 2 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: demo.MyBenchmark_2_desc.measureAll

# Run progress: 33.32% complete, ETA 00:00:35
# Fork: 1 of 1
# Warmup Iteration   1: 104321.026 us/op
# Warmup Iteration   2: 103648.611 us/op
Iteration   1: 103895.918 us/op
Iteration   2: 103644.022 us/op


Result "demo.MyBenchmark_2_desc.measureAll":
  103769.970 us/op


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, 1 s each
# Measurement: 2 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: demo.MyBenchmark_2_desc.measureAvgTime

# Run progress: 44.43% complete, ETA 00:00:28
# Fork: 1 of 1
# Warmup Iteration   1: 1287.873 us/op
# Warmup Iteration   2: 1299.332 us/op
Iteration   1: 1306.204 us/op
Iteration   2: 1312.958 us/op


Result "demo.MyBenchmark_2_desc.measureAvgTime":
  1309.581 us/op


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, 1 s each
# Measurement: 2 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: demo.MyBenchmark_2_desc.measureMultiple

# Run progress: 55.54% complete, ETA 00:00:22
# Fork: 1 of 1
# Warmup Iteration   1: 103252.051 us/op
# Warmup Iteration   2: 103289.174 us/op
Iteration   1: 103674.203 us/op
Iteration   2: 104241.156 us/op


Result "demo.MyBenchmark_2_desc.measureMultiple":
  103957.680 us/op


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, 1 s each
# Measurement: 2 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Sampling time
# Benchmark: demo.MyBenchmark_2_desc.measureAll

# Run progress: 66.64% complete, ETA 00:00:16
# Fork: 1 of 1
# Warmup Iteration   1: 103546.880 ±(99.9%) 2044.477 us/op
# Warmup Iteration   2: 103743.488 ±(99.9%) 2040.739 us/op
Iteration   1: 104241.562 ±(99.9%) 626.992 us/op
                 measureAll·p0.00:   103153.664 us/op
                 measureAll·p0.50:   104398.848 us/op
                 measureAll·p0.90:   104464.384 us/op
                 measureAll·p0.95:   104464.384 us/op
                 measureAll·p0.99:   104464.384 us/op
                 measureAll·p0.999:  104464.384 us/op
                 measureAll·p0.9999: 104464.384 us/op
                 measureAll·p1.00:   104464.384 us/op

Iteration   2: 103992.525 ±(99.9%) 1655.837 us/op
                 measureAll·p0.00:   100925.440 us/op
                 measureAll·p0.50:   104333.312 us/op
                 measureAll·p0.90:   104464.384 us/op
                 measureAll·p0.95:   104464.384 us/op
                 measureAll·p0.99:   104464.384 us/op
                 measureAll·p0.999:  104464.384 us/op
                 measureAll·p0.9999: 104464.384 us/op
                 measureAll·p1.00:   104464.384 us/op



Result "demo.MyBenchmark_2_desc.measureAll":
  N = 20
  mean = 104117.043 ±(99.9%) 708.650 us/op

  Histogram, us/op:
    [100000.000, 100500.000) = 0
    [100500.000, 101000.000) = 1
    [101000.000, 101500.000) = 0
    [101500.000, 102000.000) = 0
    [102000.000, 102500.000) = 0
    [102500.000, 103000.000) = 0
    [103000.000, 103500.000) = 1
    [103500.000, 104000.000) = 2
    [104000.000, 104500.000) = 16

  Percentiles, us/op:
      p(0.0000) = 100925.440 us/op
     p(50.0000) = 104333.312 us/op
     p(90.0000) = 104464.384 us/op
     p(95.0000) = 104464.384 us/op
     p(99.0000) = 104464.384 us/op
     p(99.9000) = 104464.384 us/op
     p(99.9900) = 104464.384 us/op
     p(99.9990) = 104464.384 us/op
     p(99.9999) = 104464.384 us/op
    p(100.0000) = 104464.384 us/op


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, 1 s each
# Measurement: 2 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Sampling time
# Benchmark: demo.MyBenchmark_2_desc.measureMultiple

# Run progress: 77.75% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 103350.272 ±(99.9%) 1780.399 us/op
# Warmup Iteration   2: 103795.917 ±(99.9%) 888.421 us/op
Iteration   1: 102432.768 ±(99.9%) 2486.038 us/op
                 measureMultiple·p0.00:   100007.936 us/op
                 measureMultiple·p0.50:   103219.200 us/op
                 measureMultiple·p0.90:   103809.024 us/op
                 measureMultiple·p0.95:   103809.024 us/op
                 measureMultiple·p0.99:   103809.024 us/op
                 measureMultiple·p0.999:  103809.024 us/op
                 measureMultiple·p0.9999: 103809.024 us/op
                 measureMultiple·p1.00:   103809.024 us/op

Iteration   2: 103769.702 ±(99.9%) 1615.423 us/op
                 measureMultiple·p0.00:   101056.512 us/op
                 measureMultiple·p0.50:   104136.704 us/op
                 measureMultiple·p0.90:   104464.384 us/op
                 measureMultiple·p0.95:   104464.384 us/op
                 measureMultiple·p0.99:   104464.384 us/op
                 measureMultiple·p0.999:  104464.384 us/op
                 measureMultiple·p0.9999: 104464.384 us/op
                 measureMultiple·p1.00:   104464.384 us/op



Result "demo.MyBenchmark_2_desc.measureMultiple":
  N = 20
  mean = 103101.235 ±(99.9%) 1314.628 us/op

  Histogram, us/op:
    [100000.000, 100500.000) = 3
    [100500.000, 101000.000) = 0
    [101000.000, 101500.000) = 1
    [101500.000, 102000.000) = 0
    [102000.000, 102500.000) = 0
    [102500.000, 103000.000) = 2
    [103000.000, 103500.000) = 2
    [103500.000, 104000.000) = 7
    [104000.000, 104500.000) = 5

  Percentiles, us/op:
      p(0.0000) = 100007.936 us/op
     p(50.0000) = 103743.488 us/op
     p(90.0000) = 104464.384 us/op
     p(95.0000) = 104464.384 us/op
     p(99.0000) = 104464.384 us/op
     p(99.9000) = 104464.384 us/op
     p(99.9900) = 104464.384 us/op
     p(99.9990) = 104464.384 us/op
     p(99.9999) = 104464.384 us/op
    p(100.0000) = 104464.384 us/op


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, 1 s each
# Measurement: 2 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Sampling time
# Benchmark: demo.MyBenchmark_2_desc.measureSamples

# Run progress: 88.86% complete, ETA 00:00:05
# Fork: 1 of 1
# Warmup Iteration   1: 103061.914 ±(99.9%) 1714.990 us/op
# Warmup Iteration   2: 103310.950 ±(99.9%) 1900.246 us/op
Iteration   1: 104136.704 ±(99.9%) 1167.682 us/op
                 measureSamples·p0.00:   101974.016 us/op
                 measureSamples·p0.50:   104333.312 us/op
                 measureSamples·p0.90:   104582.349 us/op
                 measureSamples·p0.95:   104595.456 us/op
                 measureSamples·p0.99:   104595.456 us/op
                 measureSamples·p0.999:  104595.456 us/op
                 measureSamples·p0.9999: 104595.456 us/op
                 measureSamples·p1.00:   104595.456 us/op

Iteration   2: 104071.168 ±(99.9%) 1010.433 us/op
                 measureSamples·p0.00:   102629.376 us/op
                 measureSamples·p0.50:   104136.704 us/op
                 measureSamples·p0.90:   105067.315 us/op
                 measureSamples·p0.95:   105119.744 us/op
                 measureSamples·p0.99:   105119.744 us/op
                 measureSamples·p0.999:  105119.744 us/op
                 measureSamples·p0.9999: 105119.744 us/op
                 measureSamples·p1.00:   105119.744 us/op



Result "demo.MyBenchmark_2_desc.measureSamples":
  N = 20
  mean = 104103.936 ±(99.9%) 611.114 us/op

  Histogram, us/op:
    [101000.000, 101500.000) = 0
    [101500.000, 102000.000) = 1
    [102000.000, 102500.000) = 0
    [102500.000, 103000.000) = 1
    [103000.000, 103500.000) = 0
    [103500.000, 104000.000) = 3
    [104000.000, 104500.000) = 12
    [104500.000, 105000.000) = 2
    [105000.000, 105500.000) = 1

  Percentiles, us/op:
      p(0.0000) = 101974.016 us/op
     p(50.0000) = 104333.312 us/op
     p(90.0000) = 104595.456 us/op
     p(95.0000) = 105093.530 us/op
     p(99.0000) = 105119.744 us/op
     p(99.9000) = 105119.744 us/op
     p(99.9900) = 105119.744 us/op
     p(99.9990) = 105119.744 us/op
     p(99.9999) = 105119.744 us/op
    p(100.0000) = 105119.744 us/op


# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, single-shot each
# Measurement: 2 iterations, single-shot each
# Timeout: 10 min per iteration
# Threads: 1 thread
# Benchmark mode: Single shot invocation time
# Benchmark: demo.MyBenchmark_2_desc.measureAll

# Run progress: 99.97% complete, ETA 00:00:00
# Fork: 1 of 1
# Warmup Iteration   1: 104109.318 us/op
# Warmup Iteration   2: 103675.827 us/op
Iteration   1: 102707.241 us/op
Iteration   2: 101249.186 us/op



# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, single-shot each
# Measurement: 2 iterations, single-shot each
# Timeout: 10 min per iteration
# Threads: 1 thread
# Benchmark mode: Single shot invocation time
# Benchmark: demo.MyBenchmark_2_desc.measureMultiple

# Run progress: 99.98% complete, ETA 00:00:00
# Fork: 1 of 1
# Warmup Iteration   1: 104214.702 us/op
# Warmup Iteration   2: 103594.379 us/op
Iteration   1: 103507.020 us/op
Iteration   2: 103596.903 us/op



# JMH version: 1.20
# VM version: JDK 9.0.4, VM 9.0.4+11
# VM invoker: /Library/Java/JavaVirtualMachines/jdk-9.0.4.jdk/Contents/Home/bin/java
# VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49654:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 2 iterations, single-shot each
# Measurement: 2 iterations, single-shot each
# Timeout: 10 min per iteration
# Threads: 1 thread
# Benchmark mode: Single shot invocation time
# Benchmark: demo.MyBenchmark_2_desc.measureSingleShot

# Run progress: 99.99% complete, ETA 00:00:00
# Fork: 1 of 1
# Warmup Iteration   1: 104153.153 us/op
# Warmup Iteration   2: 104950.472 us/op
Iteration   1: 102492.880 us/op
Iteration   2: 106927.383 us/op



# Run complete. Total time: 00:00:53

Benchmark                                                     Mode  Cnt       Score      Error   Units
MyBenchmark_2_desc.measureAll                                thrpt    2      ≈ 10⁻⁵             ops/us
MyBenchmark_2_desc.measureMultiple                           thrpt    2      ≈ 10⁻⁵             ops/us
MyBenchmark_2_desc.measureThroughput                         thrpt    2       9.589              ops/s
MyBenchmark_2_desc.measureAll                                 avgt    2  103769.970              us/op
MyBenchmark_2_desc.measureAvgTime                             avgt    2    1309.581              us/op
MyBenchmark_2_desc.measureMultiple                            avgt    2  103957.680              us/op
MyBenchmark_2_desc.measureAll                               sample   20  104117.043 ±  708.650   us/op
MyBenchmark_2_desc.measureAll:measureAll·p0.00              sample       100925.440              us/op
MyBenchmark_2_desc.measureAll:measureAll·p0.50              sample       104333.312              us/op
MyBenchmark_2_desc.measureAll:measureAll·p0.90              sample       104464.384              us/op
MyBenchmark_2_desc.measureAll:measureAll·p0.95              sample       104464.384              us/op
MyBenchmark_2_desc.measureAll:measureAll·p0.99              sample       104464.384              us/op
MyBenchmark_2_desc.measureAll:measureAll·p0.999             sample       104464.384              us/op
MyBenchmark_2_desc.measureAll:measureAll·p0.9999            sample       104464.384              us/op
MyBenchmark_2_desc.measureAll:measureAll·p1.00              sample       104464.384              us/op
MyBenchmark_2_desc.measureMultiple                          sample   20  103101.235 ± 1314.628   us/op
MyBenchmark_2_desc.measureMultiple:measureMultiple·p0.00    sample       100007.936              us/op
MyBenchmark_2_desc.measureMultiple:measureMultiple·p0.50    sample       103743.488              us/op
MyBenchmark_2_desc.measureMultiple:measureMultiple·p0.90    sample       104464.384              us/op
MyBenchmark_2_desc.measureMultiple:measureMultiple·p0.95    sample       104464.384              us/op
MyBenchmark_2_desc.measureMultiple:measureMultiple·p0.99    sample       104464.384              us/op
MyBenchmark_2_desc.measureMultiple:measureMultiple·p0.999   sample       104464.384              us/op
MyBenchmark_2_desc.measureMultiple:measureMultiple·p0.9999  sample       104464.384              us/op
MyBenchmark_2_desc.measureMultiple:measureMultiple·p1.00    sample       104464.384              us/op
MyBenchmark_2_desc.measureSamples                           sample   20  104103.936 ±  611.114   us/op
MyBenchmark_2_desc.measureSamples:measureSamples·p0.00      sample       101974.016              us/op
MyBenchmark_2_desc.measureSamples:measureSamples·p0.50      sample       104333.312              us/op
MyBenchmark_2_desc.measureSamples:measureSamples·p0.90      sample       104595.456              us/op
MyBenchmark_2_desc.measureSamples:measureSamples·p0.95      sample       105093.530              us/op
MyBenchmark_2_desc.measureSamples:measureSamples·p0.99      sample       105119.744              us/op
MyBenchmark_2_desc.measureSamples:measureSamples·p0.999     sample       105119.744              us/op
MyBenchmark_2_desc.measureSamples:measureSamples·p0.9999    sample       105119.744              us/op
MyBenchmark_2_desc.measureSamples:measureSamples·p1.00      sample       105119.744              us/op
MyBenchmark_2_desc.measureAll                                   ss    2  101978.214              us/op
MyBenchmark_2_desc.measureMultiple                              ss    2  103551.962              us/op
MyBenchmark_2_desc.measureSingleShot                            ss    2  104710.132              us/op

Process finished with exit code 0
*/
}
