package org.atulspatil1.healthcarepreauthorization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atulspatil1.healthcarepreauthorization.dto.request.MemberRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.MemberResponseDto;
import org.atulspatil1.healthcarepreauthorization.enums.Gender;
import org.atulspatil1.healthcarepreauthorization.enums.PolicyStatus;
import org.atulspatil1.healthcarepreauthorization.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    public void testRegisterMember() throws Exception {
        MemberRequestDto request = MemberRequestDto.builder()
                .name("Alice")
                .dob(LocalDate.of(1992, 2, 2))
                .gender(Gender.FEMALE)
                .policyNumber("POL789")
                .policyStatus(PolicyStatus.ACTIVE)
                .email("alice@example.com")
                .phone("1122334455")
                .build();

        MemberResponseDto response = MemberResponseDto.builder()
                .id(1L)
                .memberNumber("MEM-ABC")
                .name("Alice")
                .policyNumber("POL789")
                .build();

        when(memberService.registerMember(any(MemberRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.memberNumber").value("MEM-ABC"));
    }

    @Test
    public void testGetMember() throws Exception {
        MemberResponseDto response = MemberResponseDto.builder()
                .id(1L)
                .memberNumber("MEM-ABC")
                .name("Bob")
                .build();

        when(memberService.getMemberById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"));
    }

    @Test
    public void testLookupByPolicy() throws Exception {
        MemberResponseDto response = MemberResponseDto.builder()
                .id(1L)
                .memberNumber("MEM-ABC")
                .name("Charlie")
                .policyNumber("POL999")
                .build();

        when(memberService.getMemberByPolicyNumber(eq("POL999"))).thenReturn(response);

        mockMvc.perform(get("/api/v1/members")
                .param("policyNumber", "POL999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Charlie"));
    }
}
