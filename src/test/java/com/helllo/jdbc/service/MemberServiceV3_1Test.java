package com.helllo.jdbc.service;

import com.helllo.jdbc.domain.Member;
import com.helllo.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static com.helllo.jdbc.connection.ConnectionConst.*;

/**
 * 트랜잭션 - 커넥션 파라미터 전달 방식
 */
@Slf4j
class MemberServiceV3_1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_1 memberService;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);
        memberService = new MemberServiceV3_1(new DataSourceTransactionManager(dataSource), memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    void 정상_이체() throws SQLException {
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        memberService.accountTransfer(MEMBER_A, MEMBER_B, 5000);

        Member findMemberA = memberRepository.findById(MEMBER_A);
        Member findMemberB = memberRepository.findById(MEMBER_B);

        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(5000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(15000);
    }

    @Test
    void 이체_오류() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        //when
        log.info("START TX");
        Assertions.assertThatThrownBy(() -> memberService.accountTransfer(MEMBER_A, MEMBER_EX, 5000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member findMemberA = memberRepository.findById(MEMBER_A);
        Member findMemberEx = memberRepository.findById(MEMBER_EX);

        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(10000); // A의 잔고만 줄어들어 비즈니스 로직의 원자성이 깨짐
        Assertions.assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }

}