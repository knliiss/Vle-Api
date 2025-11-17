package dev.knalis.vleapi.model.dto.task;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Data
public class TestQuestionDto {
    private Long id;
    @NotNull
    private Long taskId;
    @NotNull
    @Min(0)
    private Integer order;
    @NotBlank
    private String text;
    @NotBlank
    @Pattern(regexp = "SINGLE_CHOICE|MULTIPLE_CHOICE|FREE_TEXT", message = "questionType must be one of SINGLE_CHOICE, MULTIPLE_CHOICE, FREE_TEXT")
    private String questionType;
    private String optionsJson;
    @Positive(message = "maxScore must be > 0")
    private Double maxScore;
}
