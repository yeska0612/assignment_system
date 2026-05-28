CREATE TABLE IF NOT EXISTS assignments (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    student_id VARCHAR(100) NOT NULL,
    course_code VARCHAR(100),
    description VARCHAR(1000),
    submission_date DATE,
    status VARCHAR(50) NOT NULL,
    score DOUBLE PRECISION,
    feedback VARCHAR(1000)
);