package com.helllo.jdbc.repository;

import com.helllo.jdbc.connection.ConnectionConst;
import com.helllo.jdbc.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static com.helllo.jdbc.connection.ConnectionConst.*;


@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 memberRepository;

    @BeforeEach
    void clear() throws SQLException {
        // DataSource-DriverManager
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
//        memberRepository = new MemberRepositoryV1(dataSource);

        // DataSource-HikariCP
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("myPool");

        memberRepository = new MemberRepositoryV1(dataSource);
        memberRepository.clear();
    }

    @Test
    void crud() throws SQLException, InterruptedException {

        // create
        Member member = new Member("memberV0", 100);
        Member save = memberRepository.save(member);
        Assertions.assertThat(save).isNotNull();

        // read
        Member findMember = memberRepository.findById(member.getMemberId());
        log.info("findMember={}", findMember.toString());
        Assertions.assertThat(findMember).isEqualTo(member);

        // update
        memberRepository.update(member.getMemberId(), 300);
        Member updateMember = memberRepository.findById(member.getMemberId());
        Assertions.assertThat(updateMember.getMoney()).isEqualTo(300);

        // delete
        memberRepository.clear();
        Assertions.assertThatThrownBy(() -> memberRepository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

        Thread.sleep(1000);

    }
}