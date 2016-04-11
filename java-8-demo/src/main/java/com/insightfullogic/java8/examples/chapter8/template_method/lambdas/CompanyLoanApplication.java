package com.insightfullogic.java8.examples.chapter8.template_method.lambdas;

import com.insightfullogic.java8.examples.chapter8.template_method.ApplicationDenied;

// BEGIN CompanyLoanApplication
public class CompanyLoanApplication extends LoanApplication {
	

    public CompanyLoanApplication(Company company) {
        super(company::checkIdentity,
              company::checkHistoricalDebt,
              company::checkProfitAndLoss);
    }

    public static void main(String[] args) throws ApplicationDenied {
    	Company company = new Company();
    	CompanyLoanApplication application = new CompanyLoanApplication(company);
    	application.checkLoanApplication();
    	
	}
}
// END CompanyLoanApplication
