package com.example.QuantInvestigation.user;

import com.example.QuantInvestigation.exception.BaseException;
import com.example.QuantInvestigation.exception.BaseResponse;
import com.example.QuantInvestigation.token.JwtService;
import com.example.QuantInvestigation.user.dto.*;
import com.example.QuantInvestigation.utils.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UtilService utilService;

    @PostMapping("/join")
    public BaseResponse<String> joinUser(@RequestBody PostJoinReq postJoinReq){
        try{
            return new BaseResponse<>(userService.joinUser(postJoinReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @PostMapping("/login")
    public BaseResponse<String> loginUser(@RequestBody PostLoginReq postLoginReq){
        try{
            return new BaseResponse<>(userService.loginUser(postLoginReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/account-number")
    public BaseResponse<String> getAccountNumber(){
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.getAccountNumber(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/all-refresh")
    public BaseResponse<String> refreshAllTokens(){
        try{
            List<RefreshTokenReq> refreshTokenReqList = utilService.findRefreshTokenReqAllWithValidation();
            userService.refreshToken(refreshTokenReqList);
            return new BaseResponse<>("토큰 갱신이 완료되었습니다.");
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/total-balance")
    public BaseResponse<GetTotalBalanceRes> getTotalBalance(){
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.getTotalBalance(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/item-balance")
    public BaseResponse<GetItemBalanceRes> getItemBalance(){
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.getItemBalance(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
