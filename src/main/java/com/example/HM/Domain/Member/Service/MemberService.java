package com.example.HM.Domain.Member.Service;

import com.example.HM.Domain.Member.DTO.MemberDTO;
import com.example.HM.Domain.Member.Entity.MemberEntity;
import com.example.HM.Domain.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Map<String, String> emailVerificationCodes = new HashMap<>();

    // 회원가입
    public void save(MemberDTO memberDTO) {
        System.out.println("Saving member: " + memberDTO); // DTO 데이터 확인
        if (memberRepository.findByEmail(memberDTO.getEmail()).isPresent()) {
            System.out.println("이미 존재하는 이메일: " + memberDTO.getEmail());
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        memberEntity.setPassword(passwordEncoder.encode(memberDTO.getPassword())); // 비밀번호 암호화
        memberRepository.save(memberEntity);
        System.out.println("Member saved successfully!");
    }

    // 이메일 중복 확인
    public boolean checkEmailDuplicate(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    // 로그인 처리
    public boolean login(String email, String password) {
        return memberRepository.findByEmail(email)
                .filter(memberEntity -> passwordEncoder.matches(password, memberEntity.getPassword())) // 비밀번호 비교
                .isPresent();
    }
}
