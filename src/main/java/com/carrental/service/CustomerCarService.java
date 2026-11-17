
package com.carrental.service;

import com.carrental.dto.CustomerCarResponse;
import java.util.List;

public interface CustomerCarService {

    List<CustomerCarResponse> getAvailableCars();

    CustomerCarResponse getAvailableCarById(Long id);
}
