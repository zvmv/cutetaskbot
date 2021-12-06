package ru.pet.cutetaskbot.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

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
    @Column(name = "id")
    Long id;

    @NotNull
    @NotBlank
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "createdate")
    private LocalDate createDate;

    @Column(name = "maxdate")
    private LocalDate maxDate;

    @Column(name = "finishdate")
    private LocalDate finishDate;

    @NotNull
    @Column(name = "finished")
    private Boolean finished;

    @ManyToOne
    @JoinColumn(name = "createdby")
    private BotUser createdBy;

    public Task(){};

    public Task(String description, BotUser botUser) {
        this.description = description;
        this.createDate = LocalDate.now();
        this.finished = false;
        this.createdBy = botUser;
    }
}
