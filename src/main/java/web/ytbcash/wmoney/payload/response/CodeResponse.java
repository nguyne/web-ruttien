package web.ytbcash.wmoney.payload.response;

import lombok.Data;

@Data
public class CodeResponse {
    String code;

    public CodeResponse(String code) {
        this.code = code;
    }
}
