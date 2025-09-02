package com.example.Profile_Service.controller;

import com.example.Profile_Service.entity.Profile;
import com.example.Profile_Service.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PostMapping("/create")
    public ResponseEntity<?> createProfile(@RequestBody Profile profile){
        Profile profile1 = profileService.create(profile);

        Map<String,Object> response=Map.of(
                "message","Profile created successfully!!",
                "data",profile1
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllProfiles(){
        List<Profile> all = profileService.getAll();

        Map<String,Object> profiles=Map.of(
                "message","Profile received successfully!!",
                "data",all
        );
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable String id){
        Profile getById = profileService.findById(id);

        Map<String,Object> response1=Map.of(
                "message","Profile received successfully!!",
                "data",getById
        );
        return ResponseEntity.ok(response1);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable String id, @RequestBody Profile updatedProfile){
        Profile update = profileService.update(id, updatedProfile);

        Map<String,Object> updates=Map.of(
                "message","Profile update successfully!!",
                "data", update
        );
        return ResponseEntity.status(HttpStatus.FOUND).body(updates);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable String id){
        String delete = profileService.delete(id);

        Map<String,Object> deletes=Map.of(
                "message","Profile deleted successfully",
                "data",delete
        );
        return ResponseEntity.status(HttpStatus.GONE).body(deletes);
    }
}
