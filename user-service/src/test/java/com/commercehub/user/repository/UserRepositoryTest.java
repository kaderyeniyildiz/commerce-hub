package com.commercehub.user.repository;

import com.commercehub.user.entity.User;
import com.commercehub.user.enums.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:user-repository-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    private User testUser;

    @BeforeEach
    public void setUp() {
        // Initialize test data before each test method
        testUser = userRepository.save(user("ada@example.com", "Ada", "Lovelace", "+61111111111", UserStatus.ACTIVE));
        userRepository.save(user("grace@example.com", "Grace", "Hopper", "+61222222222", UserStatus.ACTIVE));
        userRepository.save(user("ada.inactive@example.com", "Ada", "Lovelace", "+61111111111", UserStatus.INACTIVE));

    }

    @AfterEach
    public void tearDown() {
        // Release test data after each test method
       // userRepository.delete(testUser);
        userRepository.deleteAll();
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void givenExistingEmail_whenFindByEmail_thenReturnMatchingUser() {
        Optional<User> result = userRepository.findByEmail("ada@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testUser.getId());
        assertThat(result.get().getFirstName()).isEqualTo("Ada");
    }

    @Test
    void givenNonExistingEmail_whenFindByEmail_thenReturnEmpty() {
        assertThat(userRepository.findByEmail("missing@example.com")).isEmpty();
    }

    @Test
    void givenMultipleSearchCriteria_whenSearchUsers_thenReturnMatchingUsers() {
        Page<User> result = userRepository.searchUsersByCriteria(
                "Ada", "Lovelace", null, "+61111111111", UserStatus.ACTIVE, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).extracting(User::getEmail).containsExactly("ada@example.com");
    }

    @Test
    void givenNullSearchCriteria_whenSearchUsers_thenReturnPagedResults() {
        Page<User> result = userRepository.searchUsersByCriteria(null, null, null, null, null, PageRequest.of(0, 1));

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalPages()).isEqualTo(3);
    }

    private User user(String email, String firstName, String lastName, String phoneNumber, UserStatus status) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword("encoded-password");
        user.setPhoneNumber(phoneNumber);
        user.setStatus(status);
        return user;
    }
}
