package com.insightfullogic.java8.examples.chapter8.template_method.traditional;

import com.insightfullogic.java8.examples.chapter8.template_method.ApplicationDenied;

public class CompanyLoanApplication extends LoanApplication {

    @Override
    protected void checkIdentity() {

    }

    @Override
    protected void checkIncomeHistory() {

    }

    @Override
    protected void checkCreditHistory() {

    }

	@Override
	protected void callback() throws ApplicationDenied {
		// TODO Auto-generated method stub
		
	}

}
