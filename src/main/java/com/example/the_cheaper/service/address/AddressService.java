package com.example.the_cheaper.service.address;

import com.example.the_cheaper.dto.request.user.UserAddressCreateRequest;
import com.example.the_cheaper.dto.request.user.UserAddressUpdateRequest;
import com.example.the_cheaper.dto.response.user.UserAddressResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.AddressEntity;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.user.UserAddressMapper;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AccountRepository accountRepository;
    private final UserAddressMapper userAddressMapper;

    @Transactional(readOnly = true)
    public List<UserAddressResponse> getUserAddresses(Long accountId) {
        return addressRepository.findByAccountId(accountId).stream()
                .map(userAddressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserAddressResponse getAddressById(Long accountId, Long addressId) {
        AddressEntity address = getAddress(accountId, addressId);
        return userAddressMapper.toResponse(address);
    }

    @Transactional
    public UserAddressResponse createAddress(Long accountId, UserAddressCreateRequest request) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));

        if (request.isDefault()) {
            resetDefaultAddress(accountId);
        } else {
            // Nếu user chưa có địa chỉ nào thì địa chỉ đầu tiên mặc định là default
            List<AddressEntity> currentAddresses = addressRepository.findByAccountId(accountId);
            if (currentAddresses.isEmpty()) {
                request.setDefault(true);
            }
        }

        AddressEntity address = userAddressMapper.toEntity(request);
        address.setAccount(account);
        return userAddressMapper.toResponse(addressRepository.save(address));
    }

    @Transactional
    public UserAddressResponse updateAddress(Long accountId, Long addressId, UserAddressUpdateRequest request) {
        AddressEntity address = getAddress(accountId, addressId);

        if (request.isDefault() && !address.isDefault()) {
            resetDefaultAddress(accountId);
        }

        userAddressMapper.updateEntity(address, request);

        // Đảm bảo phải có ít nhất 1 default address nếu address hiện tại đang là default mà bị update thành false
        if (!request.isDefault() && address.isDefault()) {
             // Logic để chọn address khác làm default có thể phức tạp.
             // Tạm thời bắt buộc giữ default nếu user chỉ có 1 địa chỉ hoặc đây là địa chỉ default duy nhất.
            throw new IllegalArgumentException("Không thể bỏ mặc định địa chỉ này. Vui lòng chọn địa chỉ khác làm mặc định trước.");
        }

        return userAddressMapper.toResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(Long accountId, Long addressId) {
        AddressEntity address = getAddress(accountId, addressId);
        if (address.isDefault()) {
            throw new IllegalArgumentException("Không thể xóa địa chỉ mặc định");
        }
        addressRepository.delete(address);
    }

    @Transactional
    public void setDefaultAddress(Long accountId, Long addressId) {
        AddressEntity address = getAddress(accountId, addressId);
        if (!address.isDefault()) {
            resetDefaultAddress(accountId);
            address.setDefault(true);
            addressRepository.save(address);
        }
    }

    private AddressEntity getAddress(Long accountId, Long addressId) {
        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));
        if (!address.getAccount().getId().equals(accountId)) {
            throw new IllegalArgumentException("Địa chỉ không thuộc về tài khoản này");
        }
        return address;
    }

    private void resetDefaultAddress(Long accountId) {
        addressRepository.findByAccountIdAndIsDefaultTrue(accountId).ifPresent(addr -> {
            addr.setDefault(false);
            addressRepository.save(addr);
        });
    }
}
