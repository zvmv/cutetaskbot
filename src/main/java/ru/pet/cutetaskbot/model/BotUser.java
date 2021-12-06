package ru.pet.cutetaskbot.model;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;

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
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "performer")
    private Boolean performer;

    @NotNull
    @Column(name = "isadmin")
    private Boolean isAdmin;

    @NotNull
    @Column(name = "state")
    private String state;

    @NotNull
    @Column(name = "chatid")
    private Long chatId;

    public BotUser(){
    }

    public BotUser(Long id, String userName) {
        this.setId(id);
        this.name = userName;
        this.performer = false;
    }
}
