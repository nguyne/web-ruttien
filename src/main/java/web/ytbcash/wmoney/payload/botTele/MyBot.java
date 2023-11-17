package web.ytbcash.wmoney.payload.botTele;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import web.ytbcash.wmoney.entity.Image;
import web.ytbcash.wmoney.entity.UserInfo;
import web.ytbcash.wmoney.enums.UserState;
import web.ytbcash.wmoney.payload.request.Request2nd;
import web.ytbcash.wmoney.payload.request.UserRequest;
import web.ytbcash.wmoney.repositories.ImageRepositories;
import web.ytbcash.wmoney.repositories.UserInfoRepositories;
import web.ytbcash.wmoney.services.UserInfoService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.*;

public class MyBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.notification.token}")
    private String token;

    @Value("${telegram.bot.notification.name}")
    private String name;

    @Value("${telegram.chatId}")
    private Long ID;

    private String chatIdYour = "";

    private final String ALLOWED_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final SecureRandom random = new SecureRandom();

    private Map<Long, UserState> userStates = new HashMap<>();

    private Map<String, String> usernameToProcess = new HashMap<>();

    private Map<String, UserRequest> userInfo = new HashMap<>();

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserInfoRepositories userInfoRepositories;

    @Autowired
    private ImageRepositories imageRepositories;

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        if(chatId.equals(ID)){
            if(!update.getMessage().hasPhoto()){
                if (update.getMessage().getText().startsWith("/QRCode")) {
                    String[] parts = update.getMessage().getText().split("_");
                    if(parts.length == 3){
                        String code = parts[1];
                        if (userInfo.containsKey(code)) {
                            chatIdYour = code;
                            userStates.put(chatId, UserState.AWAITING_QR_IMAGE_2ND);

                            usernameToProcess.put(chatIdYour, code);
                            sendTextMessage(chatId, "Vui lòng tải ảnh QR của bạn lên cho " + code);
                            return;
                        }else{
                            sendTextMessage(chatId,"Không có yêu cầu QR code nào từ "+code);
                            return;
                        }
                    }
                    if (parts.length == 2) {
                        String code = parts[1];
                        if (userInfo.containsKey(code)) {
                            chatIdYour = code;
                            userStates.put(chatId, UserState.AWAITING_QR_IMAGE);

                            usernameToProcess.put(chatIdYour, code);
                            sendTextMessage(chatId, "Vui lòng tải ảnh QR của bạn lên cho " + code);
                            return;
                        }else{
                            sendTextMessage(chatId,"Không có yêu cầu QR code nào từ "+code);
                            return;
                        }
                    } else {
                        sendTextMessage(chatId, "Sử dụng đúng cú pháp: /QRCode_<code>");
                        return;
                    }
                }
                if(update.getMessage().getText().startsWith("/XacNhanQRCode")){
                    String[] parts = update.getMessage().getText().split("_");
                    if (parts.length == 2) {
                        String code = parts[1];
                        if (userInfo.containsKey(code)) {
                            //update status QR code in Database
                            if(userInfoService.updateStatusUserInfo(code)){
                                sendTextMessage(chatId, "Hoàn thành xác nhận code: "+code);
                            }
                        }else{
                            sendTextMessage(chatId,code +" này không tồn tại.");
                        }
                    }else {
                        sendTextMessage(chatId, "Sử dụng đúng cú pháp: /XacNhanQRCode_<code>");
                    }
                }
                if(update.getMessage().getText().startsWith("/Delete")){
                    String[] parts = update.getMessage().getText().split("_");
                    if (parts.length == 2) {
                        String code = parts[1];
                        if (userInfo.containsKey(code)) {
                            //update status QR code in Database
                            if(userInfoService.deleteData(code)){
                                userInfo.remove(code);
                                usernameToProcess.remove(code);
                                sendTextMessage(chatId, "Hoàn thành xác xóa code: "+code);
                            }
                        }else{
                            sendTextMessage(chatId,code +" này không tồn tại.");
                        }
                    }else {
                        sendTextMessage(chatId, "Sử dụng đúng cú pháp: /Delete_<code>");
                    }
                }
            } else if (userStates.getOrDefault(chatId, UserState.IDLE) == UserState.AWAITING_QR_IMAGE) {
                if (update.hasMessage()) {
                    if (update.getMessage().hasPhoto()) {
                        List<PhotoSize> photos = update.getMessage().getPhoto();
                        PhotoSize photo = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);

                        if (photo != null) {
                            // Lấy đường dẫn đến ảnh
                            String filePath = getFilePath(photo);
                            if (filePath != null) {
                                try {
                                    savePhotoToDirectory(chatId, filePath, chatIdYour);
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    sendTextMessage(chatId, "Đã xảy ra lỗi khi lưu ảnh.");
                                    return;
                                }
                            } else {
                                sendTextMessage(chatId, "Không thể lấy đường dẫn đến ảnh.");
                            }

                            // Chuyển trạng thái của người dùng về IDLE
                            userStates.put(chatId, UserState.IDLE);
                        }
                    } else {
                        // Người dùng gửi tin nhắn khác không phải là ảnh
                        sendTextMessage(chatId, "Vui lòng chỉ gửi ảnh QR.");
                    }
                }
            } else if (userStates.getOrDefault(chatId, UserState.IDLE) == UserState.AWAITING_QR_IMAGE_2ND) {
                if (update.hasMessage()) {
                    if (update.getMessage().hasPhoto()) {
                        List<PhotoSize> photos = update.getMessage().getPhoto();
                        PhotoSize photo = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);

                        if (photo != null) {
                            // Lấy đường dẫn đến ảnh
                            String filePath = getFilePath(photo);
                            if (filePath != null) {
                                try {
                                    savePhotoToDirectory(chatId, filePath, chatIdYour);
                                    userInfoService.updateStatus2nd(chatIdYour);
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    sendTextMessage(chatId, "Đã xảy ra lỗi khi lưu ảnh.");
                                    return;
                                }
                            } else {
                                sendTextMessage(chatId, "Không thể lấy đường dẫn đến ảnh.");
                            }

                            // Chuyển trạng thái của người dùng về IDLE
                            userStates.put(chatId, UserState.IDLE);
                        }
                    } else {
                        // Người dùng gửi tin nhắn khác không phải là ảnh
                        sendTextMessage(chatId, "Vui lòng chỉ gửi ảnh QR.");
                    }
                }
            }
        }
    }

    private void savePhotoToDirectory(Long chatId, String filePath, String chatIdYour) throws IOException {
        InputStream is = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath).openStream();

        String nameImage = usernameToProcess.get(chatIdYour);
        try {
            // Đọc dữ liệu từ InputStream và mã hóa thành chuỗi Base64
            byte[] imageBytes = is.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            if (userInfo.containsKey(nameImage)) {
                if(userInfoService.updateUserInfo(nameImage,base64Image)){
                    sendTextMessage(chatId,"Bạn đã gửi QR code thành công đến "+nameImage+".\nĐợi người dùng gửi ảnh xác nhận.\n\n\n NẾU ĐÃ GỬI ẢNH QUÁ 10P MÀ KHÔNG THẤY "+nameImage+" GỬI BILL THANH TOÁN THÌ NHẤN /Delete_"+nameImage);
                }
                else
                    sendTextMessage(chatId,"Bạn đã gửi QR code không thành công thành công.");
            }else {
                sendTextMessage(chatId,"Không có yêu cầu QR code nào từ "+nameImage);
            }
            // Thực hiện các thao tác khác với chuỗi Base64 như gửi đi hoặc lưu vào cơ sở dữ liệu
        } finally {
            // Đảm bảo đóng InputStream sau khi sử dụng xong
            is.close();
        }
    }

    private String getFilePath(PhotoSize photo) {
        GetFile getFile = new GetFile();
        getFile.setFileId(photo.getFileId());
        try {
            File file = execute(getFile);
            return file.getFilePath();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendNotification(String chatId, UserRequest request) {
        if(request.getMoney() >= 200 && request.getMoney() <= 20000){
            String codeRandom = RandomCode();
            int transactionFees = 0;
            String withdrawal_method ="";
            if(request.getWithdrawal_method() == 1){
                withdrawal_method = "CODE Nhanh";
                transactionFees = 1200000 + (request.getMoney() - 200) * 6000;
            } else if (request.getWithdrawal_method() == 2) {
                withdrawal_method = "CODE Thường";
                transactionFees = 700000 + (request.getMoney() - 200) * 4000;
            } else if (request.getWithdrawal_method() ==3) {
                withdrawal_method = "CODE Chậm";
                transactionFees = 450000 + (request.getMoney() - 200) * 3000;
            }
            String formattedFees = formatCurrency(transactionFees, "vi","VN");
            String formatMoney = formatCurrency(request.getMoney(), "en","US");
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Hiện tại có một yêu cầu QR code với mệnh giá rút tiền là: "+ formatMoney+".\nVới hình thức: "+withdrawal_method+".\nPhí cho giao dịch này là: "+formattedFees+
                    ". Vui lòng gửi QR code theo cú pháp /QRCode_"+codeRandom);

            try {
                userInfo.put(codeRandom,request);
                execute(message);
                return codeRandom;
            } catch (TelegramApiException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void sendNotification2nd(String chatId, Request2nd request2nd) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Yêu cầu QR code Lần 2 vui lòng tạo QR code có mệnh giá là: "+ request2nd.getExpense()+".\nVui lòng gửi QR code theo cú pháp /QRCode_"+request2nd.getCode()+"_Lan2");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public Boolean sendPhotoFromDatabaseToChat(String chatId, String imageId, String idUser) {
        Optional<Image> imageEntityOptional = imageRepositories.findById(imageId);
        Optional <UserInfo> userInfoOptional = userInfoRepositories.findById(idUser);
        if (imageEntityOptional.isPresent() && userInfoOptional.isPresent()) {
            Image imageEntity = imageEntityOptional.get();

            // Giải mã base64
            byte[] decodedImage = Base64.getDecoder().decode(imageEntity.getImage());

            // Gửi ảnh qua Telegram
            try {
                String caption = "Vui lòng kiểm tra ảnh chuyển khoản và xem tiền đã vào tài khoản hay chưa.\nSau đó bấm /XacNhanQRCode_"+userInfoOptional.get().getNameImageQRCode()+"\n\n\n\nNếu đây là lần 2 thì bấm /Delete_"+userInfoOptional.get().getNameImageQRCode();
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(new InputFile(new ByteArrayInputStream(decodedImage), "photo"));
                sendPhoto.setCaption(caption);

                execute(sendPhoto);
                return true;
            } catch (TelegramApiException e) {
                return false;
            }
        }
        return false;
    }
    private String formatCurrency(int amount, String language, String country) {
        // Định dạng số theo tiền tệ
        Locale locale = new Locale(language, country);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        return currencyFormatter.format(amount);
    }

    private String RandomCode(){
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
