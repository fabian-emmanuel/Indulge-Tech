package com.indulgetech.models.users.token;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static constants.SchemaConstant.TABLE_TOKEN_BLACKLIST;

@Entity
@Getter
@Setter
@Table(name = TABLE_TOKEN_BLACKLIST)
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "mediumtext")
    private String token;
}