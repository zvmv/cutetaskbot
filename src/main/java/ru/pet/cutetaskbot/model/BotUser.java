package ru.pet.cutetaskbot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "botuser")
public class BotUser {
    @Column
    @Id
    private Long id;
    @Column
    private String name;
    @Column
    @NotNull
    private Boolean performer;

    @NotNull
    private String state;
    @NotNull
    private Long chatId;

    public BotUser(){
    }

    public BotUser(Long id, String userName) {
        this.setId(id);
        this.name = userName;
        this.performer = false;
    }
}
