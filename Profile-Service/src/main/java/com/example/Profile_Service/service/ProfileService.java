package com.example.Profile_Service.service;

import com.example.Profile_Service.entity.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProfileService {

    //create profile
    Profile create(Profile profile);

    //find all profile
    List<Profile> getAll();

    //find by id
    Profile findById(String id);

    //update profile
    Profile update(String id, Profile updatedProfile);

    //delete profile
    String delete(String id);


}
