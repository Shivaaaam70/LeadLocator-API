package com.example.Onboard_Service.controller;

import com.example.Onboard_Service.entity.ApiResponse;
import com.example.Onboard_Service.entity.Onboard;
import com.example.Onboard_Service.service.OnboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/onboard")
public class OnboardController {

    @Autowired
    private OnboardService onboardService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Onboard>> createOnboard(@RequestBody Onboard onboard){
        Onboard onboard1= onboardService.create(onboard);
        ApiResponse<Onboard> response1=new ApiResponse<>("Onboarding created successfully!!", onboard1);
        return ResponseEntity.status(HttpStatus.CREATED).body(response1);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Onboard>>> getAllOnboards(){
        List<Onboard> onboardList=onboardService.getAll();
        ApiResponse<List<Onboard>> response3 =new ApiResponse<>("Getting all onboardings",onboardList);
        return ResponseEntity.ok(response3);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Onboard>> updateOnboard(@PathVariable Long id,@RequestBody Onboard updatedOnboard){
        Onboard update=onboardService.update(id,updatedOnboard);
        ApiResponse<Onboard> response2=new ApiResponse<>("Onboard updated successfully",update);
        return ResponseEntity.status(HttpStatus.FOUND).body(response2);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Onboard>> deleteOnboard(@PathVariable Long id){
        Void deleteOnboard=onboardService.delete(id);
        ApiResponse<Onboard> response=new ApiResponse("Onboard deleted successfully",deleteOnboard);
        return ResponseEntity.ok(response);
    }


}
