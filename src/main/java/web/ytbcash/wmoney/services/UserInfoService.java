package web.ytbcash.wmoney.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import web.ytbcash.wmoney.payload.request.ExpenseRequest;
import web.ytbcash.wmoney.payload.request.ImagerRequest;
import web.ytbcash.wmoney.payload.request.UserRequest;
import web.ytbcash.wmoney.payload.response.CodeResponse;
import web.ytbcash.wmoney.payload.response.ExpenseResponse;
import web.ytbcash.wmoney.payload.response.UserResponse;

import java.io.IOException;

public interface UserInfoService {
    UserResponse createUserInfo(String nameImage, UserRequest request);

    Boolean updateUserInfo(String nameImage, String imageQR);

    Boolean updateStatusUserInfo(String code);

    Boolean updateStatus2nd(String code);

    CodeResponse checkStatusUserInfo(String id);

    Resource getImageQR(String id);

    Resource getImageQR2nd(String id);

    ExpenseResponse expense(ExpenseRequest expenseRequest);

    UserResponse saveImage(MultipartFile file, ImagerRequest request);

    Boolean deleteData(String nameImage);
}
