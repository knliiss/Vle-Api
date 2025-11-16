package dev.knalis.vleapi.controller.version.v1.app;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "App", description = "Application-level endpoints")
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class AppController {
    
    @GetMapping("/health")
    @Operation(summary = "Health check endpoint", description = "Returns a simple message indicating that the application is running.")
    @ApiResponse(responseCode = "200", description = "Application is healthy", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
            examples = {@ExampleObject(value = "{\n  \"detail\": \"Application is running\"\n}")}))
    public ResponseEntity<ProblemDetail> healthCheck() {
        ProblemDetail detail = ProblemDetail.forStatus(200);
        detail.setDetail("Application is running");
        return ResponseEntity.ok(detail);
    }
    
    
}
