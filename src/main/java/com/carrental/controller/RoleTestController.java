
package com.carrental.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleTestController {

    @GetMapping("/api/customer/test")
    public String customerTest() {
        return "Customer access successful";
    }

    @GetMapping("/api/admin/test")
    public String adminTest() {
        return "Admin access successful";
    }
}
