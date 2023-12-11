package com.example.project3.repository.main;


import com.example.project3.entity.main.Clothes;
import com.example.project3.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothesRepository extends JpaRepository<Clothes,Long> {

    // Member와 관련된 Clothes 조회 메서드 추가
    List<Clothes> findByMember(Member member);
}
