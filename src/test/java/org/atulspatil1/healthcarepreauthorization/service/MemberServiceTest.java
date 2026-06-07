package org.atulspatil1.healthcarepreauthorization.service;

import org.atulspatil1.healthcarepreauthorization.dto.request.MemberRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.MemberResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Member;
import org.atulspatil1.healthcarepreauthorization.enums.Gender;
import org.atulspatil1.healthcarepreauthorization.enums.PolicyStatus;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    public void testRegisterMember() {
        // Arrange
        MemberRequestDto request = MemberRequestDto.builder()
                .name("Alice")
                .dob(LocalDate.of(1992, 2, 2))
                .gender(Gender.FEMALE)
                .policyNumber("POL789")
                .policyStatus(PolicyStatus.ACTIVE)
                .email("alice@example.com")
                .phone("1122334455")
                .build();

        Member savedMember = Member.builder()
                .id(1L)
                .memberNumber("MEM-ABCDEFGH")
                .name("Alice")
                .dob(LocalDate.of(1992, 2, 2))
                .gender(Gender.FEMALE)
                .policyNumber("POL789")
                .policyStatus(PolicyStatus.ACTIVE)
                .email("alice@example.com")
                .phone("1122334455")
                .createdAt(LocalDateTime.now())
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // Act
        MemberResponseDto response = memberService.registerMember(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Alice");
        assertThat(response.getMemberNumber()).startsWith("MEM-");

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository, times(1)).save(memberCaptor.capture());
        Member capturedMember = memberCaptor.getValue();
        assertThat(capturedMember.getName()).isEqualTo("Alice");
    }

    @Test
    public void testGetMemberById_Success() {
        // Arrange
        Member member = Member.builder()
                .id(1L)
                .memberNumber("MEM-XYZ")
                .name("Bob")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // Act
        MemberResponseDto response = memberService.getMemberById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Bob");
    }

    @Test
    public void testGetMemberById_NotFound() {
        // Arrange
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> memberService.getMemberById(1L));
    }

    @Test
    public void testGetMemberByPolicyNumber_Success() {
        // Arrange
        Member member = Member.builder()
                .id(1L)
                .memberNumber("MEM-XYZ")
                .name("Charlie")
                .policyNumber("POL999")
                .build();

        when(memberRepository.findByPolicyNumber("POL999")).thenReturn(Optional.of(member));

        // Act
        MemberResponseDto response = memberService.getMemberByPolicyNumber("POL999");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Charlie");
        assertThat(response.getPolicyNumber()).isEqualTo("POL999");
    }

    @Test
    public void testGetMemberByPolicyNumber_NotFound() {
        // Arrange
        when(memberRepository.findByPolicyNumber("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                memberService.getMemberByPolicyNumber("INVALID"));
    }
}
