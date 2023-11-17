package web.ytbcash.wmoney.payload.request;

import lombok.Data;

@Data
public class ExpenseRequest {
    int money;
    int withdrawal_method;
}
