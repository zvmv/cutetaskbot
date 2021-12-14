package ru.pet.cutetaskbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.pet.cutetaskbot.model.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByCreatedByIdAndFinished(Long id, Boolean finished);
    List<Task> findAllByFinished(Boolean finished);
    @Transactional
    void deleteAllByCreatedByIdAndFinished(Long userId, Boolean finished);
    @Transactional
    void deleteAllByFinished(Boolean finished);
}
