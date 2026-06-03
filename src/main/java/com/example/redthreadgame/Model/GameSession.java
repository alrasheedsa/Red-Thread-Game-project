package com.example.redthreadgame.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "game_session")
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

    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case sessionCase;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Player owner;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    private Set<Hint> hints;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    private Set<Note> notes;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Invitation> invitations;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    private Set<SolutionProposal> solutionProposals;
}
