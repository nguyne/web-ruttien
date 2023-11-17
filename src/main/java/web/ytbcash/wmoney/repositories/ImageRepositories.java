package web.ytbcash.wmoney.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import web.ytbcash.wmoney.entity.Image;

import java.util.Date;
import java.util.Optional;

@Repository
public interface ImageRepositories extends JpaRepository<Image, String> {

    Optional<Image> findImageByNameImage(String nameImage);

    @Modifying
    @Transactional
    @Query("DELETE FROM Image i WHERE i.createdAt < :tenMinutesAgo")
    void deleteByCreatedAtBefore(Date tenMinutesAgo);
}
