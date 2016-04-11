package com.insightfullogic.java8.examples.chapter8.template_method.traditional;

import com.insightfullogic.java8.examples.chapter8.template_method.ApplicationDenied;

public class PersonalLoanApplication extends LoanApplication {

    @Override
    protected void checkIdentity() {
    	System.out.println("aaaa");
    }

    @Override
    protected void checkIncomeHistory() {
    	System.out.println("aaaa");
    }

    @Override
    protected void checkCreditHistory() {
    	System.out.println("aaaa");
    }

	@Override
	protected void callback() throws ApplicationDenied {
		// TODO Auto-generated method stub
		
	}

}
