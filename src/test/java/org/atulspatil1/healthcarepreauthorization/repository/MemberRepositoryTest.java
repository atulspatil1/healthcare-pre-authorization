package org.atulspatil1.healthcarepreauthorization.repository;

import org.atulspatil1.healthcarepreauthorization.entity.Member;
import org.atulspatil1.healthcarepreauthorization.enums.Gender;
import org.atulspatil1.healthcarepreauthorization.enums.PolicyStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testSaveAndFindMember() {
        // Arrange
        Member member = Member.builder()
                .memberNumber("MEM123")
                .name("John Doe")
                .dob(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .policyNumber("POL123")
                .policyStatus(PolicyStatus.ACTIVE)
                .email("john.doe@example.com")
                .phone("1234567890")
                .createdAt(LocalDateTime.now())
                .createdBy("system")
                .build();

        // Act
        Member savedMember = memberRepository.save(member);
        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());

        // Assert
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("John Doe");
        assertThat(foundMember.get().getPolicyNumber()).isEqualTo("POL123");
    }

    @Test
    public void testFindByPolicyNumber() {
        // Arrange
        Member member = Member.builder()
                .memberNumber("MEM456")
                .name("Jane Smith")
                .dob(LocalDate.of(1985, 5, 15))
                .gender(Gender.FEMALE)
                .policyNumber("POL456")
                .policyStatus(PolicyStatus.ACTIVE)
                .email("jane.smith@example.com")
                .phone("0987654321")
                .createdAt(LocalDateTime.now())
                .createdBy("system")
                .build();
        memberRepository.save(member);

        // Act
        Optional<Member> foundMember = memberRepository.findByPolicyNumber("POL456");

        // Assert
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("Jane Smith");
    }
}
