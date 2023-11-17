package web.ytbcash.wmoney.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.ytbcash.wmoney.entity.Banks;

import java.util.List;

@Repository
public interface BanksRepositories extends JpaRepository<Banks, Integer> {
}
