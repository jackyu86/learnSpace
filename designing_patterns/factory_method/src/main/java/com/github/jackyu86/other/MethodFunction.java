package com.github.jackyu86.other;

import java.util.ArrayList;
import java.util.List;

import com.github.jackyu86.Captain;
import com.github.jackyu86.Cook;
import com.github.jackyu86.Fleet;
import com.github.jackyu86.Sailor;

/**
 * @Author: jack-yu
 * @Description:
 */
public class MethodFunction {
   public static List<Fleet>  fleet;

    public MethodFunction(/*Optional<List<Fleet>>  fleets*/){
       // this.fleet=fleet;
    }
    private static String createTeam(){
            fleet = new ArrayList<Fleet>();
        StringBuilder sb = new StringBuilder();
            fleet.add(new Captain());
            fleet.add(new Sailor());
            fleet.add(new Cook());

        fleet.stream().forEach((f)->{sb.append(f.jobDesc());});
        return sb.toString();
    }

    public static void main(String[] args) {
        MethodFunction methodFunction =new MethodFunction();
        System.out.println(methodFunction.createTeam());
    }
}
