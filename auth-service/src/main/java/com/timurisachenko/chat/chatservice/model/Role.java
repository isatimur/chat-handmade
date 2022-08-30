package com.timurisachenko.chat.chatservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    public final static Role USER = new Role("USER");
    public final static Role ADMIN = new Role("ADMIN");
    public final static Role ANONYMOUS = new Role("ANONYMOUS");
    public final static Role SERVICE = new Role("SERVICE");
    public final static Role FACEBOOK_USER = new Role("FACEBOOK_USER");

    private String name;
}

