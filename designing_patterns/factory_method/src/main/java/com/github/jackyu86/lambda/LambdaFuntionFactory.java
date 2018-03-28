package com.github.jackyu86.lambda;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.jackyu86.Fleet;

/**
 * @Author: jack-yu
 * @Description: supplier  function 工厂
 */
public class LambdaFuntionFactory {

    public static Supplier<Fleet> getFleet(FleetEnum fleetEnum){
       return fleetEnum.getConstructor();
    }

    public static void main(String[] args) {
       Fleet fleet = LambdaFuntionFactory.getFleet(FleetEnum.CAPTAIN).get();
        Fleet fleet2 = LambdaFuntionFactory.getFleet(FleetEnum.SAILOR).get();
        Fleet fleet3 = LambdaFuntionFactory.getFleet(FleetEnum.COOK).get();
        System.out.println(fleet.jobDesc()+ fleet2.jobDesc()+fleet3.jobDesc());
         Optional a = Optional.ofNullable(null);
        Optional b = (Optional) a.orElseGet(()->{return Optional.of("asdasdas");});
        System.out.println(b.get());
        //System.out.printf(a.get().toString());
    }

}
