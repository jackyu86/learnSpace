package com.github.jackyu86.lambda;

import java.util.Optional;
import java.util.function.Supplier;

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
        System.out.printf(fleet.jobDesc()+ fleet2.jobDesc()+fleet3.jobDesc());
         Optional a = Optional.of(null);
            a.orElseGet(()->{return Optional.of("asdasdas");});
        System.out.printf(a.get().toString());
    }

}
