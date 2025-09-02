package com.example.Profile_Service.service.Impl;

import com.example.Profile_Service.entity.Profile;
import com.example.Profile_Service.repository.ProfileRepository;
import com.example.Profile_Service.service.ProfileService;
import com.example.Profile_Service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Profile create(Profile profile) {
        profile.setId(UUID.randomUUID().toString());

        if (profileRepository.findByEmail(profile.getEmail()).isPresent()) {
            throw new ResourceNotFoundException("Email already exists");
        }
        return profileRepository.save(profile);
    }

    @Override
    public List<Profile> getAll() {
        return profileRepository.findAll();
    }

    @Override
    public Profile findById(String id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
    }

    @Override
    public Profile update(String id, Profile updatedProfile) {
        Profile existingProfile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));

        existingProfile.setFirst_name(updatedProfile.getFirst_name());
        existingProfile.setLast_name(updatedProfile.getLast_name());
        existingProfile.setEmail(updatedProfile.getEmail());
        existingProfile.setDesignation(updatedProfile.getDesignation());
        existingProfile.setRole(updatedProfile.getRole());

        return profileRepository.save(existingProfile);
    }

    @Override
    public String delete(String id) {
        Profile existingProfile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));

        profileRepository.delete(existingProfile);
        return "Profile deleted successfully!!!";
    }
}
