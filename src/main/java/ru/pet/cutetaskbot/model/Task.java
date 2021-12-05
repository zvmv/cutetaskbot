package ru.pet.cutetaskbot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "task")
@Getter
@Setter
public class Task {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private LocalDate createDate;

    private LocalDate maxDate;

    private LocalDate finishDate;

    @NotNull
    private Boolean finished;

    @ManyToOne
    private BotUser createdBy;

    public Task(){};

    public Task(String description, BotUser botUser) {
        this.description = description;
        this.createDate = LocalDate.now();
        this.finished = false;
        this.createdBy = botUser;
    }
}
