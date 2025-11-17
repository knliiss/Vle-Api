CREATE TABLE test_questions (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES task(id) ON DELETE CASCADE,
    question_order INT NOT NULL,
    text TEXT NOT NULL,
    question_type VARCHAR(64) NOT NULL,
    options_json TEXT,
    max_score DOUBLE PRECISION DEFAULT 1.0
);
CREATE INDEX idx_test_questions_task ON test_questions(task_id);

