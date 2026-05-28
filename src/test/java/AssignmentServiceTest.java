package mn.edu.num.assignmentsystem.core.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Lab15 Break the Build test.
 *
 * Энэ test нь CI/CD pipeline тестийн шат үнэхээр алдаа барьж чадаж
 * байгаа эсэхийг шалгах зорилготой зориуд буруу test юм.
 */
public class AssignmentServiceTest {

    @Test
    void shouldFailPipelineForDemo() {
        assertEquals(1, 1);
    }
}