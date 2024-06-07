package hello.service;

import hello.entity.user.User;
import hello.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;

    public void increasePoint(User user, int point) {
        user.setPoint(user.getPoint() + point);
        userRepository.save(user);
    }
}