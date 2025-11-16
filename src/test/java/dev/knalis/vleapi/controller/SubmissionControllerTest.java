package dev.knalis.vleapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import dev.knalis.vleapi.model.document.TestSubmissionDoc;
import dev.knalis.vleapi.service.intrf.CourseService;
import dev.knalis.vleapi.service.intrf.SubmissionService;
import dev.knalis.vleapi.service.intrf.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubmissionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubmissionService submissionService;

    @MockBean
    private TaskService taskService;

    @MockBean
    private CourseService courseService;

    @Test
    public void gradeFileSubmission_returnsDto() throws Exception {
        FileSubmissionDoc doc = new FileSubmissionDoc();
        doc.setId("sub1");
        doc.setTaskId(2L);
        doc.setUserId(3L);
        doc.setSubmitted(LocalDateTime.now());
        doc.setGrade(4.5);

        when(submissionService.gradeFileSubmission("sub1", 4.5)).thenReturn(doc);

        mockMvc.perform(post("/api/v1/submissions/file/sub1/grade?grade=4.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("sub1"))
                .andExpect(jsonPath("$.grade").value(4.5));
    }

    @Test
    public void getTaskGrade_returnsDouble() throws Exception {
        when(taskService.getGrade(5L, 6L)).thenReturn(3.3);

        mockMvc.perform(get("/api/v1/submissions/task/5/grade?userId=6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3.3));
    }
}
