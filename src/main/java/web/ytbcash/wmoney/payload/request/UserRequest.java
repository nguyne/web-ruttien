package web.ytbcash.wmoney.payload.request;

import lombok.Data;

@Data
public class UserRequest {

    String nameBank;
    String numberBank;
    int money;
    int withdrawal_method;
}
