package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.model.dto.task.TestQuestionDto;
import dev.knalis.vleapi.model.entity.task.TestQuestion;
import dev.knalis.vleapi.model.entity.task.Task;
import org.springframework.stereotype.Component;

@Component
public class TestQuestionMapper {
    public TestQuestionDto toDto(TestQuestion q) {
        if (q == null) return null;
        TestQuestionDto dto = new TestQuestionDto();
        dto.setId(q.getId());
        dto.setTaskId(q.getTask().getId());
        dto.setOrder(q.getOrder());
        dto.setText(q.getText());
        dto.setQuestionType(q.getQuestionType());
        dto.setOptionsJson(q.getOptionsJson());
        dto.setMaxScore(q.getMaxScore());
        return dto;
    }

    public TestQuestion fromCreate(TestQuestionDto dto) {
        TestQuestion q = new TestQuestion();
        Task t = new Task();
        t.setId(dto.getTaskId());
        q.setTask(t);
        q.setOrder(dto.getOrder());
        q.setText(dto.getText());
        q.setQuestionType(dto.getQuestionType());
        q.setOptionsJson(dto.getOptionsJson());
        q.setMaxScore(dto.getMaxScore() == null ? 1.0 : dto.getMaxScore());
        return q;
    }

    public void update(TestQuestion entity, TestQuestionDto dto) {
        if (dto.getOrder() != null) entity.setOrder(dto.getOrder());
        if (dto.getText() != null) entity.setText(dto.getText());
        if (dto.getQuestionType() != null) entity.setQuestionType(dto.getQuestionType());
        if (dto.getOptionsJson() != null) entity.setOptionsJson(dto.getOptionsJson());
        if (dto.getMaxScore() != null) entity.setMaxScore(dto.getMaxScore());
    }
}

