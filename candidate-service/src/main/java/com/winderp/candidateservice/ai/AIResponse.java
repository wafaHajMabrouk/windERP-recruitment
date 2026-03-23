package com.winderp.candidateservice.ai;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIResponse {
    private double score;
    private String decision;
}