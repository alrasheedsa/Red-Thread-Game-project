package com.example.redthreadgame.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(20) check (status = 'PENDING' or status = 'ACCEPTED' or status = 'REJECTED')", nullable = false)
    private String status;

//    @ManyToOne
//    @JsonIgnore
//    @JoinColumn(name = "gameSession_id")
//    private GameSession gameSession;

//    @ManyToMany(mappedBy = "invitations")
//    @JsonIgnore
//    private Set<Player> players;
}
