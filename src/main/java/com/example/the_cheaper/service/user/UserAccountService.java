package com.example.the_cheaper.service.user;

import com.example.the_cheaper.dto.request.user.UserUpdateProfileRequest;
import com.example.the_cheaper.dto.response.user.UserAccountResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.user.UserAccountMapper;
import com.example.the_cheaper.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final AccountRepository accountRepository;
    private final UserAccountMapper userAccountMapper;

    @Transactional(readOnly = true)
    public UserAccountResponse getAccountDetail(Long accountId) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));
        return userAccountMapper.toResponse(account);
    }

    @Transactional
    public UserAccountResponse updateProfile(Long accountId, UserUpdateProfileRequest request) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));
        
        account.updateProfile(request.getName(), request.getPhone());
        account = accountRepository.save(account);
        
        return userAccountMapper.toResponse(account);
    }
}
