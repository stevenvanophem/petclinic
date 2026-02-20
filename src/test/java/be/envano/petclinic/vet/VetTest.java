package be.envano.petclinic.vet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class VetTest {

    @Test
    @DisplayName("A vet first name can't be blank")
    void testFirstNameBlank() {
        assertThatThrownBy(() -> Vet.Name.fromStrings(" ", "Carter"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Vet first name cannot be blank.");
    }

    @Test
    @DisplayName("A vet last name can't be blank")
    void testLastNameBlank() {
        assertThatThrownBy(() -> Vet.Name.fromStrings("James", " "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Vet last name cannot be blank.");
    }

    @Test
    @DisplayName("A vet id must be positive")
    void testIdPositive() {
        assertThatThrownBy(() -> Vet.Id.fromLong(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("vet id must be positive");
    }

}
