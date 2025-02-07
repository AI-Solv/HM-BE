package com.example.HM.Domain.Member.Service;

import com.example.HM.Domain.Member.DTO.MemberDTO;
import com.example.HM.Domain.Member.Entity.MemberEntity;
import com.example.HM.Domain.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 이메일 인증번호 저장 (이메일 → 인증번호)
    private final Map<String, String> emailVerificationCodes = new HashMap<>();
    // 인증번호 유효시간 저장 (이메일 → 유효기간)
    private final Map<String, LocalDateTime> verificationCodeExpiry = new HashMap<>();
    // 인증된 이메일 저장 (이메일 → 인증 여부)
    private final Map<String, Boolean> verifiedEmails = new HashMap<>();

    /**
     * 🔹 이메일 인증번호 생성 & 전송
     */
    public void sendEmailVerificationCode(String email) {
        String code = generateVerificationCode();
        emailVerificationCodes.put(email, code);
        verificationCodeExpiry.put(email, LocalDateTime.now().plusMinutes(5)); // 5분 후 만료
        log.info("✅ 인증번호 생성 - 이메일: {} | 인증번호: {}", email, code);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("회원가입 인증번호");
            message.setText("회원가입을 위한 인증번호: " + code + "\n\n📌 인증번호는 5분 동안 유효합니다.");
            mailSender.send(message);
            log.info("📩 인증번호 이메일 전송 완료 - {}", email);
        } catch (Exception e) {
            log.error("❌ 이메일 전송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 전송 실패");
        }
    }

    /**
     * 🔹 이메일 인증번호 확인
     */
    public boolean verifyEmailCode(String email, String code) {
        // 1️⃣ 인증번호가 존재하는지 확인
        if (!emailVerificationCodes.containsKey(email)) {
            return false;
        }

        // 2️⃣ 인증번호 유효시간이 초과되었는지 확인
        if (verificationCodeExpiry.get(email).isBefore(LocalDateTime.now())) {
            emailVerificationCodes.remove(email);
            verificationCodeExpiry.remove(email);
            return false;
        }

        // 3️⃣ 입력한 인증번호가 올바른지 확인
        if (emailVerificationCodes.get(email).equals(code)) {
            verifiedEmails.put(email, true);
            emailVerificationCodes.remove(email);
            verificationCodeExpiry.remove(email);
            return true;
        }

        return false;
    }

    /**
     * 🔹 회원가입 (이메일 인증 필수)
     */
    public void save(MemberDTO memberDTO) {
        log.info("회원가입 시도: {}", memberDTO.getEmail());

        // 1️⃣ 이메일 인증 여부 확인
        if (!verifiedEmails.getOrDefault(memberDTO.getEmail(), false)) {
            log.warn("이메일 인증되지 않음: {}", memberDTO.getEmail());
            throw new IllegalArgumentException("이메일 인증을 먼저 진행해주세요.");
        }

        // 2️⃣ 이메일 중복 체크
        Optional<MemberEntity> existingUser = memberRepository.findByEmail(memberDTO.getEmail());
        if (existingUser.isPresent()) {
            log.warn("이미 존재하는 이메일: {}", memberDTO.getEmail());
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 3️⃣ 비밀번호 암호화 후 저장
        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        memberEntity.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        memberRepository.save(memberEntity);

        // 4️⃣ 인증 성공한 이메일 정보 제거
        verifiedEmails.remove(memberDTO.getEmail());
        log.info("회원가입 성공: {}", memberDTO.getEmail());
    }

    /**
     * 🔹 이메일 중복 확인
     */
    public boolean checkEmailDuplicate(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    /**
     * 🔹 로그인 처리 (비밀번호 비교)
     */
    public boolean login(String email, String password) {
        Optional<MemberEntity> user = memberRepository.findByEmail(email);
        return user.filter(memberEntity -> passwordEncoder.matches(password, memberEntity.getPassword())).isPresent();
    }

    /**
     * 🔹 인증번호 생성 (6자리)
     */
    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}
