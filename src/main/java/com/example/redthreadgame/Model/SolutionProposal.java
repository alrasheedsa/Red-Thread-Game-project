package com.example.redthreadgame.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solution_proposals")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SolutionProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(500) not null")
    private String reason;

    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @Column(columnDefinition = "int not null")
    private Integer rejectCount;

    @Column(columnDefinition = "int not null")
    private Integer acceptCount;

//    @ManyToOne
//    @JoinColumn(name = "game_session_id", referencedColumnName = "id")
//    @JsonIgnore
//    private GameSessionModel gameSession;
//
//    @ManyToOne
//    @JoinColumn(name = "player_id", referencedColumnName = "id")
//    @JsonIgnore
//    private PlayerModel player;
//
//    @ManyToOne
//    @JoinColumn(name = "suspect_id", referencedColumnName = "id")
//    @JsonIgnore
//    private SuspectModel suspect;
}
