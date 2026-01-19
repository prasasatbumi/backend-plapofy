package com.finprov.loan.event;

import com.finprov.loan.entity.Loan;
import com.finprov.loan.entity.LoanStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LoanStatusChangedEvent extends ApplicationEvent {
    private final Loan loan;
    private final LoanStatus status;

    public LoanStatusChangedEvent(Object source, Loan loan, LoanStatus status) {
        super(source);
        this.loan = loan;
        this.status = status;
    }
}
