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

    @GetMapping("/error-log")
    public BaseResponse<List<GetErrorLogRes>> getErrorLogs() {
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.getErrorLogs(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @PatchMapping("/is-running")
    public BaseResponse<String> patchIsRunning() {
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.patchIsRunning(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @PatchMapping("/divisions")
    public BaseResponse<String> patchDivisions(@RequestParam("div") Integer div) {
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.patchDivisions(userId, div));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("buy-shares")
    public BaseResponse<List<GetBuySharesRes>> getBuyShares() {
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.getBuyShares(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 안정성 검증을 위한 테스트 호출 (매수, 매도)
     */
    @PostMapping("/buy-order")
    public BaseResponse<String> buyOrder(@RequestBody BuyOrderReq buyOrderReq){
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.buyOrder(userId, buyOrderReq.getTicker(), buyOrderReq.getPurchasePrice(), buyOrderReq.getQty()));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @PostMapping("/sell-order")
    public BaseResponse<String> sellOrder(@RequestBody SellOrderReq sellOrderReq){
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.sellOrder(userId, sellOrderReq.getTicker(), sellOrderReq.getSellingPrice(), sellOrderReq.getQty(), false));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}