package web.ytbcash.wmoney.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import web.ytbcash.wmoney.entity.UserInfo;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserInfoRepositories extends JpaRepository<UserInfo, String> {

    Optional<UserInfo> findUserInfoByNameImageQRCode(String nameImage);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserInfo u WHERE u.createdAt < :tenMinutesAgo")
    void deleteByCreatedAtBefore(Date tenMinutesAgo);
}
