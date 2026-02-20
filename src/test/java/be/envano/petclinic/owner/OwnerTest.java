package be.envano.petclinic.owner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class OwnerTest {

    @Test
    @DisplayName("An owner first name can't be blank")
    void testFirstNameBlank() {
        assertThatThrownBy(() -> Owner.Name.fromStrings(" ", OwnerTestFactory.GeorgeFranklin.LAST_NAME))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Owner first name cannot be blank.");
    }

    @Test
    @DisplayName("An owner last name can't be blank")
    void testLastNameBlank() {
        assertThatThrownBy(() -> Owner.Name.fromStrings(OwnerTestFactory.GeorgeFranklin.FIRST_NAME, " "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Owner last name cannot be blank.");
    }

    @Test
    @DisplayName("An owner id must be positive")
    void testIdPositive() {
        assertThatThrownBy(() -> Owner.Id.fromLong(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("owner id must be positive");
    }

}

