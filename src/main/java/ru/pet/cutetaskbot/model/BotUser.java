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
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String userName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "firstname")
    private String firstName;


    @NotNull
    @Column(name = "performer")
    private Boolean performer;

    @NotNull
    @Column(name = "admin")
    private Boolean admin;

    @NotNull
    @Column(name = "state")
    private String state;

    @NotNull
    @Column(name = "chatid")
    private Long chatId;

    public BotUser(){
        this.performer = false;
        this.admin = false;
        this.state = "changeContactsMenu";
        this.name = "unnamed";
    }

    public BotUser(Long id, String userName) {
        this.setId(id);
        this.name = userName;
        this.performer = false;
        this.admin = false;
    }
}
