package com.example.redthreadgame.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CaseOut {
    private Integer id;
    private String title;
    private String scenario;
    private String difficulty;
    private String status;


    //content cases
    private List<WitnessOut> witnesses;
    private List<SuspectOut> suspects;
    private List<EvidenceOut> evidences;

}
