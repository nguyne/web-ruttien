package web.ytbcash.wmoney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.ytbcash.wmoney.payload.botTele.MyBot;
import web.ytbcash.wmoney.payload.request.ExpenseRequest;
import web.ytbcash.wmoney.payload.request.ImagerRequest;
import web.ytbcash.wmoney.payload.request.Request2nd;
import web.ytbcash.wmoney.payload.request.UserRequest;
import web.ytbcash.wmoney.payload.response.UserResponse;
import web.ytbcash.wmoney.services.UserInfoService;

import java.io.IOException;

@CrossOrigin(origins = {"https://ruttienytbcash.online", "https://wmoney-6857ea6bcb17.herokuapp.com"})
@RestController
@RequestMapping(value = "/api/bot")
public class UserController {

    @Value("${telegram.chatId}")
    private String chatId;

    @Autowired
    private MyBot myBot;

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitUsername(@RequestBody UserRequest request, @RequestHeader("Origin") String origin) {

        if (!isOriginAllowed(origin)) {
            return new ResponseEntity<>("Forbidden", HttpStatus.FORBIDDEN);
        }
        //Bot telegram gửi 1 thông báo đến admin để tạo QR code
        String nameImage = myBot.sendNotification(chatId, request);
        return new ResponseEntity<>(userInfoService.createUserInfo(nameImage, request), HttpStatus.CREATED);
    }

    @PostMapping("/submit2nd")
    public ResponseEntity<?> submit2nd(@RequestBody Request2nd request, @RequestHeader("Origin") String origin) {

        if (!isOriginAllowed(origin)) {
            return new ResponseEntity<>("Forbidden", HttpStatus.FORBIDDEN);
        }
        //Bot telegram gửi 1 thông báo đến admin để tạo QR code
        myBot.sendNotification2nd(chatId, request);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }


    @GetMapping("/image/{id}")
    public ResponseEntity<?> getImageById(@PathVariable String id) throws IOException {
        Resource imageQR = userInfoService.getImageQR(id);
        if (imageQR != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageQR);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/image2nd/{id}")
    public ResponseEntity<?> getImage2ndById(@PathVariable String id) throws IOException {
        Resource imageQR = userInfoService.getImageQR2nd(id);
        if (imageQR != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageQR);
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/expense")
    public ResponseEntity<?> expense(@RequestBody ExpenseRequest request, @RequestHeader("Origin") String origin) {
        if (!isOriginAllowed(origin)) {
            return new ResponseEntity<>("Forbidden", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(userInfoService.expense(request), HttpStatus.CREATED);
    }

    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveImage(@RequestParam("file") MultipartFile file, @ModelAttribute("request") ImagerRequest request, @RequestHeader("Origin") String origin) {
        UserResponse response = userInfoService.saveImage(file, request);
        Boolean check;
        if(response != null){
            check = myBot.sendPhotoFromDatabaseToChat(chatId, response.getId(), response.getId2());
        }else {
            check = false;
        }

        return new ResponseEntity<>(check, HttpStatus.CREATED);
    }

    @GetMapping("/code/{id}")
    public ResponseEntity<?> getCode(@PathVariable(value = "id") String id, @RequestHeader("Origin") String origin) {
        return new ResponseEntity<>(userInfoService.checkStatusUserInfo(id), HttpStatus.OK);
    }

    private boolean isOriginAllowed(String origin) {
        return (origin != null && (origin.equals("https://ruttienytbcash.online") || origin.equals("https://wmoney-6857ea6bcb17.herokuapp.com")));
    }

}
