package be.envano.petclinic.pet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PetTest {

    @Test
    @DisplayName("A pet name can't be blank")
    void testNameBlank() {
        assertThatThrownBy(() -> Pet.Name.fromString(" "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Pet name cannot be blank.");
    }

    @Test
    @DisplayName("A pet id must be positive")
    void testIdPositive() {
        assertThatThrownBy(() -> Pet.Id.fromLong(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("pet id must be positive");
    }

}
