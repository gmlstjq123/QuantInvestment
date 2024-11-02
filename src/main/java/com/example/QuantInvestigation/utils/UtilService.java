package com.example.QuantInvestigation.utils;

import com.example.QuantInvestigation.exception.BaseException;
import com.example.QuantInvestigation.exception.BaseResponseStatus;
import com.example.QuantInvestigation.user.User;
import com.example.QuantInvestigation.user.UserRepository;
import com.example.QuantInvestigation.user.dto.RefreshTokenReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.QuantInvestigation.exception.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UtilService {
    private final UserRepository userRepository;

    public User findByUserIdWithValidation(Long userId) throws BaseException {
        return userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new BaseException(NONE_EXIST_USER));
    }

    public User findByIdWithValidation(String id) throws BaseException {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new BaseException(POST_USERS_NONE_EXISTS_ID));
    }

    public String findTokenByUserIdWithValidation(Long userId) throws BaseException {
        return userRepository.findAccessTokenByUserId(userId)
                .orElseThrow(() -> new BaseException(INVALID_USER_JWT));
    }

    public String findAppKeyByUserIdWithValidation(Long userId) throws BaseException {
        return userRepository.findAppKeyByUserId(userId)
                .orElseThrow(() -> new BaseException(POST_USERS_NONE_EXISTS_ID));
    }

    public String findAppSecretByUserIdWithValidation(Long userId) throws BaseException {
        return userRepository.findAppSecretByUserId(userId)
                .orElseThrow(() -> new BaseException(POST_USERS_NONE_EXISTS_ID));
    }

    public String findAccountNumByUserIdWithValidation(Long userId) throws BaseException {
        return userRepository.findAccountNumByUserId(userId)
                .orElseThrow(() -> new BaseException(POST_USERS_NONE_EXISTS_ID));
    }

    public List<RefreshTokenReq> findRefreshTokenReqAllWithValidation() throws BaseException {
        return userRepository.findRefreshTokenReq()
                .orElseThrow(() -> new BaseException(DATABASE_ERROR));
    }

}
