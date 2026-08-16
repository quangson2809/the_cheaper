package com.example.the_cheaper.service.user;

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
        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        return userAddressMapper.toResponse(address);
    }

    @Transactional
    public UserAddressResponse createAddress(Long accountId, UserAddressCreateRequest request) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));

        AddressEntity address = new AddressEntity();
        address.setHomeNumber(request.getHomeNumber());
        address.setStreet(request.getStreet());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        if(request.isDefault()) {
            resetDefaultAddress(accountId);
            address.setDefault(true);
        }

        address.setAccount(account);

        return userAddressMapper.toResponse(addressRepository.save(address));
    }

    @Transactional
    public UserAddressResponse updateAddress(Long accountId, Long addressId, UserAddressUpdateRequest request) {
        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        if (request.isDefault() && !address.isDefault()) {
            resetDefaultAddress(accountId);
        }

        if(request.getHomeNumber() != null ) {
            address.setHomeNumber(request.getHomeNumber());
        }
        if (request.getStreet() != null) {
            address.setStreet(request.getStreet());
        }
        if(request.getDistrict() != null) {
            address.setDistrict(request.getDistrict());
        }
        if(request.getCity() != null) {
            address.setCity(request.getCity());
        }

        // Đảm bảo phải có ít nhất 1 default address nếu address hiện tại đang là default mà bị update thành false
        if (!request.isDefault() && address.isDefault()) {
             // Tạm thời bắt buộc giữ default nếu user chỉ có 1 địa chỉ hoặc đây là địa chỉ default duy nhất.
            throw new IllegalArgumentException("Không thể bỏ mặc định địa chỉ này. Vui lòng chọn địa chỉ khác làm mặc định trước.");
        }

        return userAddressMapper.toResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(Long accountId, Long addressId) {
        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        if (address.isDefault()) {
            throw new IllegalArgumentException("Không thể xóa địa chỉ mặc định");
        }

        addressRepository.delete(address);
    }

    @Transactional
    public void setDefaultAddress(Long accountId, Long addressId) {
        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        if (!address.isDefault()) {
            resetDefaultAddress(accountId);
            address.setDefault(true);
            addressRepository.save(address);
        }
    }

    private void resetDefaultAddress(Long accountId) {
        addressRepository.findByAccountIdAndIsDefaultTrue(accountId).ifPresent(addr -> {
            addr.setDefault(false);
            addressRepository.save(addr);
        });
    }
}
