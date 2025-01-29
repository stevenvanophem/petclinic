package be.envano.petclinic.speciality;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SpecialtyTest {

    @Test
    @DisplayName("A specialty name can't have more than 80 chars")
    void testNameLength() {
        assertThatThrownBy(() -> Specialty.Name.fromString("a".repeat(81)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("specialty name cannot be longer than 80 characters");
    }

    @Test
    @DisplayName("A specialty name can't be null")
    void testNameNull() {
        assertThatThrownBy(() -> Specialty.Name.fromString(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("specialty name cannot be null");
    }

    @Test
    @DisplayName("A specialty name can't be blank")
    void testNameBlank() {
        assertThatThrownBy(() -> Specialty.Name.fromString("  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("specialty name cannot be blank");
    }

}