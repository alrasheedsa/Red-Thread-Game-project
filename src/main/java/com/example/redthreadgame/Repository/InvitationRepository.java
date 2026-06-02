package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

    Invitation findInvitationById (Integer id);
}
