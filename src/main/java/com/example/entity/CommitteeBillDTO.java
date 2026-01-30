package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommitteeBillDTO {
    private String batchName;
    private Long committeeId;
    private String chairmanName;
    private double billAmount;

}
