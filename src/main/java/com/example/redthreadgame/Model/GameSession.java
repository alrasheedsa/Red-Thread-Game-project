package com.example.redthreadgame.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints = "players_count > 0 AND players_count < 7")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(20) check (status = 'PENDING' or status = 'IN_PROGRESS' or status = 'COMPLETED')", nullable = false)
    private String status;

    @Column(columnDefinition = "boolean", nullable = false)
    private Boolean isPrivate;

    @Column(columnDefinition = "int", nullable = false)
    private Integer playersCount;

    @Column(columnDefinition = "int default 0", insertable = false)
    private Integer questionsCount;

    @Column(columnDefinition = "int")
    private Integer score;

    @Column(columnDefinition = "datetime")
    private LocalDateTime startedAt;

    @Column(columnDefinition = "datetime")
    private LocalDateTime endedAt;

//    @OneToMany(mappedBy = "game_session", cascade = CascadeType.ALL)
//    private Set<Hint> hints;
//
//    @OneToMany(mappedBy = "game_session", cascade = CascadeType.ALL)
//    private Set<Note> notes;
//
//    @OneToMany(mappedBy = "game_session", cascade = CascadeType.ALL)
//    private Set<SolutionProposal> solutionProposals;
}
