package web.ytbcash.wmoney.services;

import web.ytbcash.wmoney.payload.request.BanksRequest;
import web.ytbcash.wmoney.payload.response.BanksResponse;

import java.util.List;

public interface BanksService {

    Boolean createListBanks(List<BanksRequest> requests);

    List<BanksResponse> getAllBanks();

}
