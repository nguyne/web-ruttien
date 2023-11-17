package web.ytbcash.wmoney.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import web.ytbcash.wmoney.repositories.ImageRepositories;
import web.ytbcash.wmoney.repositories.UserInfoRepositories;

import java.util.Date;
@Component
public class DataCleanupScheduler {

    @Autowired
    private ImageRepositories imageRepository;

    @Autowired
    private UserInfoRepositories userInfoRepository;

    @Scheduled(cron = "0 0 16 * * ?")
    @Transactional
    public void cleanupData() {
        imageRepository.deleteAll();

        userInfoRepository.deleteAll();
    }

    @Scheduled(fixedRate = 600000)
    public void cleanupRowData() {
        Date now = new Date();
        Date tenMinutesAgo = new Date(now.getTime() - 600000);
        //imageRepository.deleteByCreatedAtBefore(tenMinutesAgo);
        userInfoRepository.deleteByCreatedAtBefore(tenMinutesAgo);
    }

    @Scheduled(fixedRate = 600000)
    public void cleanupRowDataImage() {
        Date now = new Date();
        Date tenMinutesAgo = new Date(now.getTime() - 600000);
        imageRepository.deleteByCreatedAtBefore(tenMinutesAgo);
        //userInfoRepository.deleteByCreatedAtBefore(tenMinutesAgo);
    }
}

