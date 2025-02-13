package com.example.HM.Domain.AI.Dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AIRequestDto {
    private String model;
    private List<Message> messages;

    public AIRequestDto(String model, String prompt) {
        this.model = model;
        this.messages =  new ArrayList<>();
        String enhancedPrompt = "다음은 사용자가 제시한 고민입니다. 이 고민에 대해 하나의 해결책을 제시하고, 그 해결책을 선택한 이유를 논리정연하게 설명해주세요. 사용자가 그 고민을 하는 배경과 이유도 고려해서 판단해주세요.\n\n" +
                "고민: " + prompt + "\n" +
                "해결책과 이유:";
        this.messages.add(new Message("system", enhancedPrompt));
    }
}
