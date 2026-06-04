package com.example.redthreadgame.Controller;


import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.QuestionIn;
import com.example.redthreadgame.DTO.IN.WitnessIn;
import com.example.redthreadgame.Service.WitnessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/witnesses")
@RequiredArgsConstructor
public class WitnessController {

    private final WitnessService witnessService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllWitnesses() {
        return ResponseEntity.status(200).body(witnessService.getAllWitnesses());
    }

    @PostMapping("/add/{caseId}")
    public ResponseEntity<?> addWitness(@PathVariable Integer caseId, @RequestBody @Valid WitnessIn dto) {
        witnessService.addWitness(caseId, dto);
        return ResponseEntity.status(200).body(witnessService.getAllWitnesses());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateWitness(@PathVariable Integer id, @RequestBody @Valid WitnessIn dto) {
        witnessService.updateWitness(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Witness updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteWitness(@PathVariable Integer id) {
        witnessService.deleteWitness(id);
        return ResponseEntity.status(200).body(new ApiResponse("Witness deleted successfully"));
    }
     @GetMapping("/case/{caseId}")
     public ResponseEntity<?> getWitnessesDetails(@PathVariable Integer caseId) {
         return ResponseEntity.status(200).body(witnessService.getWitnessesDetails(caseId));
     }

    @PostMapping("/ask/{witnessId}")
    public ResponseEntity<?> askWitness(@PathVariable Integer witnessId, @RequestBody @Valid QuestionIn dto) {
        return ResponseEntity.status(200).body(witnessService.askWitness(witnessId, dto));
    }
}
