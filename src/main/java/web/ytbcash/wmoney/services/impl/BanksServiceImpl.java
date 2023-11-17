package web.ytbcash.wmoney.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ytbcash.wmoney.entity.Banks;
import web.ytbcash.wmoney.payload.request.BanksRequest;
import web.ytbcash.wmoney.payload.response.BanksResponse;
import web.ytbcash.wmoney.repositories.BanksRepositories;
import web.ytbcash.wmoney.services.BanksService;

import java.util.ArrayList;
import java.util.List;

@Service
public class BanksServiceImpl implements BanksService {

    @Autowired
    private BanksRepositories banksRepositories;

    @Override
    public Boolean createListBanks(List<BanksRequest> requests) {

        try{
            List<Banks> banks = new ArrayList<>();
            for(BanksRequest item : requests ){
                Banks bank = new Banks();
                bank.setId(item.getId());
                bank.setNameBank(item.getShortName());
                bank.setLogo(item.getLogo());
                banks.add(bank);
            }
            banksRepositories.saveAll(banks);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public List<BanksResponse> getAllBanks() {

        List<Banks> banks = banksRepositories.findAll();
        return setupBanksResponse(banks);
    }

    private List<BanksResponse> setupBanksResponse(List<Banks> banks){
        List<BanksResponse> responses = new ArrayList<>();
        for (Banks item : banks) {
            BanksResponse response = new BanksResponse();
            response.setId(item.getId());
            response.setNameBank(item.getNameBank());
            response.setLogo(item.getLogo());
            responses.add(response);
        }
        return responses;
    }
}
