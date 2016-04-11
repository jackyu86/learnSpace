package com.insightfullogic.java8.examples.chapter8.template_method.lambdas;

import com.insightfullogic.java8.examples.chapter8.template_method.ApplicationDenied;

// BEGIN LoanApplication
public class LoanApplication {

    private final Criteria identity;
    private final Criteria creditHistory;
    private final Criteria incomeHistory;
    
    public LoanApplication(Criteria identity,
            Criteria creditHistory) {
    	this.identity = identity;
    	this.creditHistory = creditHistory;
    	this.incomeHistory=null;
    }

    public LoanApplication(Criteria identity,
                           Criteria creditHistory,
                           Criteria incomeHistory) {

        this.identity = identity;
        this.creditHistory = creditHistory;
        this.incomeHistory = incomeHistory;
    }

    public void checkLoanApplication() throws ApplicationDenied {
        if(this.identity!=null){
        this.identity.check();
        }
        
        if(this.creditHistory!=null){
        this.creditHistory.check();
        }
        
        if(this.incomeHistory!=null){
        	this.incomeHistory.check();
        }
        
        reportFindings();
    }

    private void reportFindings() {
// END LoanApplication

    }

}
