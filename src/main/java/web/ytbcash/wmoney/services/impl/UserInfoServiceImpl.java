package web.ytbcash.wmoney.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import web.ytbcash.wmoney.entity.Image;
import web.ytbcash.wmoney.entity.UserInfo;
import web.ytbcash.wmoney.payload.request.ExpenseRequest;
import web.ytbcash.wmoney.payload.request.ImagerRequest;
import web.ytbcash.wmoney.payload.request.UserRequest;
import web.ytbcash.wmoney.payload.response.CodeResponse;
import web.ytbcash.wmoney.payload.response.ExpenseResponse;
import web.ytbcash.wmoney.payload.response.UserResponse;
import web.ytbcash.wmoney.repositories.ImageRepositories;
import web.ytbcash.wmoney.repositories.UserInfoRepositories;
import web.ytbcash.wmoney.services.UserInfoService;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepositories userInfoRepositories;

    @Autowired
    private ImageRepositories imageRepositories;


    @Override
    public UserResponse createUserInfo(String nameImage, UserRequest request) {
        if(nameImage != null){
            boolean nameBank = checkRequest(request.getNameBank());
            boolean numberBank = checkRequest(request.getNumberBank());
            if(nameBank || numberBank){
                return null;
            }
            try{
                UserInfo userInfo = new UserInfo();
                userInfo.setNameBank(request.getNameBank());
                userInfo.setNumberBank(request.getNumberBank());
                userInfo.setNameImageQRCode(nameImage);
                userInfo.setStatus(false);
                userInfo.setStatus2nd(false);
                UserInfo createUserInfo = userInfoRepositories.save(userInfo);
                UserResponse response = new UserResponse();
                response.setId(createUserInfo.getInfoId());
                return response;
            }catch (Exception e){
                return null;
            }
        }
        else {
            UserResponse response = new UserResponse();
            response.setId(null);
            return response;
        }
    }

    @Override
    public Boolean updateUserInfo(String nameImage, String imageQR) {

        try{
           Optional<UserInfo> userInfo = userInfoRepositories.findUserInfoByNameImageQRCode(nameImage);
            userInfo.get().setImageQRCode(imageQR);
            userInfoRepositories.save(userInfo.get());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Boolean updateStatusUserInfo(String code) {
        try{
            Optional<UserInfo> userInfo = userInfoRepositories.findUserInfoByNameImageQRCode(code);
            userInfo.get().setStatus(true);
            userInfoRepositories.save(userInfo.get());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Boolean updateStatus2nd(String code) {
        try{
            Optional<UserInfo> userInfo = userInfoRepositories.findUserInfoByNameImageQRCode(code);
            userInfo.get().setStatus2nd(true);
            userInfoRepositories.save(userInfo.get());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public CodeResponse checkStatusUserInfo(String id) {
        Optional<UserInfo> userInfoOptional = userInfoRepositories.findById(id);
        if(userInfoOptional.isPresent()){
            if(userInfoOptional.get().getStatus()){
                return new CodeResponse(userInfoOptional.get().getNameImageQRCode());
            }
        }
        return new CodeResponse(null);
    }

    @Override
    public Resource getImageQR(String id) {
        try{
            UserInfo imageQR = userInfoRepositories.findById(id).orElse(null);
            if(imageQR.getImageQRCode().isEmpty()){
                return null;
            }
            String base64Data = imageQR.getImageQRCode();
            byte[] imageData = Base64.getDecoder().decode(base64Data);
            return new ByteArrayResource(imageData);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public Resource getImageQR2nd(String id) {
        try{
            UserInfo imageQR = userInfoRepositories.findById(id).orElse(null);
            if(imageQR.getStatus2nd()){
                String base64Data = imageQR.getImageQRCode();
                byte[] imageData = Base64.getDecoder().decode(base64Data);
                return new ByteArrayResource(imageData);
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public ExpenseResponse expense(ExpenseRequest expenseRequest) {
        if(expenseRequest.getWithdrawal_method() == 1){
            int expense = 1200000 + (expenseRequest.getMoney() - 200) * 6000;

            return new ExpenseResponse(formatCurrency(expense,"vi","VN"));
        } else if (expenseRequest.getWithdrawal_method() == 2) {
            int expense = 700000 + (expenseRequest.getMoney() - 200) * 3500;
            return new ExpenseResponse(formatCurrency(expense,"vi","VN"));
        } else if (expenseRequest.getWithdrawal_method() ==3) {
            int expense = 450000 + (expenseRequest.getMoney() - 200) * 2250;
            return new ExpenseResponse(formatCurrency(expense,"vi","VN"));
        }
        return null;
    }

    @Override
    public UserResponse saveImage(MultipartFile file, ImagerRequest request) {
        try {
            byte[] imageBytes = file.getBytes();
            String base64EncodedImage = Base64.getEncoder().encodeToString(imageBytes);
            UserInfo userInfo = userInfoRepositories.findById(request.getId()).orElse(null);
            Optional<Image> imageCheck = imageRepositories.findImageByNameImage(userInfo.getNameImageQRCode());
            if(imageCheck.isPresent()){
                imageCheck.get().setImage(base64EncodedImage);
                Image imageUpdate = imageRepositories.save(imageCheck.get());
                UserResponse response = new UserResponse();
                response.setId(imageUpdate.getImageId());
                response.setId2(userInfo.getInfoId());
                return response;
            }else {
                Image image = new Image();
                image.setNameImage(userInfo.getNameImageQRCode());
                image.setImage(base64EncodedImage);
                Image imageCreate = imageRepositories.save(image);
                UserResponse response = new UserResponse();
                response.setId(imageCreate.getImageId());
                response.setId2(userInfo.getInfoId());
                return response;
            }
        }catch (IOException e){
            return null;
        }
    }

    @Override
    public Boolean deleteData(String nameImage) {
        try {
            Optional<Image> image = imageRepositories.findImageByNameImage(nameImage);
            Optional<UserInfo> userInfo = userInfoRepositories.findUserInfoByNameImageQRCode(nameImage);
            if(image.isPresent()){
                imageRepositories.delete(image.get());
            }
            if(userInfo.isPresent()){
                userInfoRepositories.delete(userInfo.get());
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private String formatCurrency(int amount, String language, String country) {
        // Định dạng số theo tiền tệ
        Locale locale = new Locale(language, country);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        return currencyFormatter.format(amount);
    }

    public boolean checkRequest(String input) {
        String regex = ".*[\\/\\?\\.;\\(\\)\\*].*";
        return Pattern.matches(regex, input);
    }
}
