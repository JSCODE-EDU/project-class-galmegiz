package com.jscode.demoApp.repository;

import com.jscode.demoApp.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;


@Import({MemberRepositoryWithVanillaJpa.class, TestJpaConfig.class})
@ActiveProfiles("test")
@DataJpaTest
public class MemberRepositoryTest {

    @Autowired private MemberRepository memberRepository;
    @Autowired private EntityManager em;

    private Member createMember(){
        Member member = Member.builder()
                .email("som@naver.com")
                .password("sdfsdfasfaf")
                .build();
        return memberRepository.save(member);
    }

    @DisplayName("[C] 중복 멤버 생성 실패 테스트")
    @Test
    public void createMemberFailTest(){
        createMember();
        Assertions.assertThatThrownBy(() -> createMember()).isInstanceOf(PersistenceException.class);
    }

    @DisplayName("[R] Id로 멤버 검색 테스트(멤버 O)")
    @Test
    public void findByIdTest(){
        Member member = createMember();
        Long id = member.getId();

        Member result = memberRepository.findById(id).get();

        System.out.println("result = " + result);
        System.out.println("member = " + member);
        Assertions.assertThat(result).isEqualTo(member);
    }

    @DisplayName("[R] Id로 멤버 검색 테스트(멤버 X)")
    @Test
    public void findByIdFailTest(){
        Assertions.assertThat(memberRepository.findById(2L).isEmpty()).isTrue();
    }
    //Transactional로 rollback되면 delete 쿼리가 실행되지 않아 원하던 테스트 결과와 다를 수 있다.
    @DisplayName("[D] 멤버 삭제 테스트(멤버 O)")
    @Rollback(value = false)
    @Test
    public void deleteMemberTest(){
        Member member = createMember();
        Long id = member.getId();


        memberRepository.delete(member);

        Assertions.assertThat(memberRepository.findById(id).isEmpty()).isTrue();
    }

    @DisplayName("[R] email로 멤버 검색 테스트(멤버 O)")
    @Test
    public void findByEmailTest(){
       /* Member member = createMember();
        Long id = member.getId();
        String email = member.getEmail();
*/
        Member member = Member.builder()
                .email("ssdfsdfm@naver.com")
                .password("sdfsdfasfaf")
                .build();
        memberRepository.save(member);
        String email = member.getEmail();

        Member result = memberRepository.findByEmail(email).get();

        Assertions.assertThat(result.getEmail()).isEqualTo(email);
    }

    @DisplayName("[R] email로 멤버 검색 실패 테스트(멤버 X)")
    @Test
    public void findByEmailFailTest(){
        Optional<Member> result = memberRepository.findByEmail("random");
        Assertions.assertThat(result.isEmpty()).isTrue();
    }

    @DisplayName("[R] email, password로 멤버 검색 테스트")
    @Test
    public void findByEmailAndPasswordTest(){
        Member member = createMember();
        Optional<Member> result = memberRepository.findByEmailAndPassword(member.getEmail(), member.getPassword());
        Assertions.assertThat(result.isPresent()).isTrue();
    }

    @DisplayName("[R] email, password로 멤버 검색 실패 테스트")
    @MethodSource("findByEmailAndPasswordFailTest")
    @ParameterizedTest(name = "[{index}] message :  {2}")
    public void findByEmailAndPasswordFailTest(String email, String password, String message){
        createMember();
        Optional<Member> result = memberRepository.findByEmailAndPassword(email, password);
        Assertions.assertThat(result.isEmpty()).isTrue();
    }

    static Stream<Arguments> findByEmailAndPasswordFailTest(){
        String email = "som@naver.com";
        String password = "sdfsdfasfaf";
        return Stream.of(
                arguments("wrong", password, "아이디 오류"),
                arguments(email, "wrong", "패스워드 오류")
        );
    }





    /* 삭제 쿼리 자체가 발생하지 않음
    @DisplayName("[D] 멤버 삭제 테스트(멤버 X)")
    @Test
    public void deleteMemberFailTest(){
        Assertions.assertThatThrownBy(() -> memberRepository.delete(new Member()))
                .isInstanceOf(RuntimeException.class);
    }
    */


}
