package com.example.redthreadgame.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(20) check (status = 'PENDING' or status = 'ACCEPTED' or status = 'REJECTED' or status = 'JOINED')", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "gameSession_id")
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @JsonIgnore // test and decide
    private Player player;
}
