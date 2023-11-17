package web.ytbcash.wmoney.payload.request;

import lombok.Data;

@Data
public class BanksRequest {

    int id;
    String shortName;
    String logo;
}
