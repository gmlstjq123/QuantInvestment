package com.example.QuantInvestigation.utils;

import com.example.QuantInvestigation.error_log.ErrorLogRepository;
import com.example.QuantInvestigation.exception.BaseException;
import com.example.QuantInvestigation.exception.BaseResponseStatus;
import com.example.QuantInvestigation.user.User;
import com.example.QuantInvestigation.user.UserRepository;
import com.example.QuantInvestigation.user.dto.GetErrorLogReq;
import com.example.QuantInvestigation.user.dto.GetErrorLogRes;
import com.example.QuantInvestigation.user.dto.RefreshTokenReq;
import com.example.QuantInvestigation.user.user_option.UserOption;
import com.example.QuantInvestigation.user.user_option.UserOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.QuantInvestigation.exception.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UtilService {

    private final UserRepository userRepository;
    private final ErrorLogRepository errorLogRepository;
    private final UserOptionRepository userOptionRepository;

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

    public List<GetErrorLogReq> findErrorLogsByUserIdWithValidation(Long userId) throws BaseException {
        return errorLogRepository.findGetErrorLogReq(userId);
    }

    public Boolean findIsRunningByUserIdWithValidation(Long userId) throws BaseException {
        return userOptionRepository.findIsRunningByUserId(userId)
                .orElseThrow(() -> new BaseException(DATABASE_ERROR));
    }

    public UserOption findUserOptionByUserIdWithValidation(Long userId) throws BaseException {
        return userOptionRepository.findUserOptionByUserId(userId)
                .orElseThrow(() -> new BaseException(DATABASE_ERROR));
    }
}
