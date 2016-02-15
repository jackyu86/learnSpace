package com.insightfullogic.java8.examples.chapter6;

import com.insightfullogic.java8.examples.chapter1.Album;
import com.insightfullogic.java8.examples.chapter1.SampleData;
import com.insightfullogic.java8.examples.chapter1.Track;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
public class ArraySum {

    public static void main(String[] ignore) throws IOException, RunnerException {
        final String[] args = {
            ".*ArraySum.*",
            "-wi",
            "5",
            "-i",
            "5"
        };
        
        ArraySum arraySum = new ArraySum();
        arraySum.initAlbums();
        int aa = arraySum.serialArraySum();
        Stream<Object> ss = arraySum.comArray();
        ss.forEach(a -> System.out.println(a));
        System.out.println(aa);
        Main.main(args);
    }

    public List<Album> albums;

    @Setup
    public void initAlbums() {
        int n = Integer.getInteger("arraysum.size", 1000);
        System.out.println(n+"次数");
        albums = IntStream.range(0, n)
                          .mapToObj(i -> SampleData.aLoveSupreme.copy())
                          .collect(toList());
    }

    @GenerateMicroBenchmark
    // BEGIN serial
public int serialArraySum() {
    return albums.stream()
                 .flatMap(Album::getTracks)
                 .mapToInt(Track::getLength)
                 .sum();
}
    // END serial
    
public Stream<Object> comArray(){
	
	return albums.stream().flatMap( al -> Stream.concat(Stream.of(al),al.getTrackList().stream() ));
}

    @GenerateMicroBenchmark
    // BEGIN parallel
public int parallelArraySum() {
    return albums.parallelStream()
                 .flatMap(Album::getTracks)
                 .mapToInt(Track::getLength)
                 .sum();
}
    // END parallel
    
}
