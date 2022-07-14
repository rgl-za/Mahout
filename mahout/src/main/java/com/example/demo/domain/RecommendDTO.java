package com.example.demo.domain;

import lombok.Data;

@Data
public class RecommendDTO {
    private int userNo;
    private int plantKeyId;
    private int prefer;
    private String userId;
}
