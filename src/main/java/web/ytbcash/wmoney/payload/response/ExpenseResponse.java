package web.ytbcash.wmoney.payload.response;

import lombok.Data;

@Data
public class ExpenseResponse {
    String expense;

    public ExpenseResponse(String expense) {
        this.expense = expense;
    }
}
