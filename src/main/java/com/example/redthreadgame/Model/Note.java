package com.example.redthreadgame.Model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(500) not null")
    private String content;

//    @ManyToOne
//    @JoinColumn(name = "game_session_id", referencedColumnName = "id")
//    @JsonIgnore
//    private GameSessionModel gameSession;
//
//    @ManyToOne
//    @JoinColumn(name = "player_id", referencedColumnName = "id")
//    @JsonIgnore
//    private PlayerModel player;
}
