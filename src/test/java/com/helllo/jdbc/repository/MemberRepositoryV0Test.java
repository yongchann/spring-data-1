package com.helllo.jdbc.repository;

import com.helllo.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;


@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 memberRepository = new MemberRepositoryV0();

    @BeforeEach
    void clear() throws SQLException {
        memberRepository.clear();
    }

    @Test
    void crud() throws SQLException {

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
    }

}