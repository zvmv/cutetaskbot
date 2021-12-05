package ru.pet.cutetaskbot.repository;

import org.springframework.stereotype.Repository;
import ru.pet.cutetaskbot.model.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface BotUserRepository extends JpaRepository<BotUser, Long> {
}
