package com.example.Onboard_Service.service;
import com.example.Onboard_Service.entity.Onboard;
import com.example.Onboard_Service.exception.DuplicateEmailException;
import com.example.Onboard_Service.repository.OnboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OnboardService {

    @Autowired
    private OnboardRepository onboardRepository;

    public Onboard create(Onboard onboard){
        if(onboardRepository.findByEmail(onboard.getEmail()).isPresent()){
            throw new DuplicateEmailException("Onboarding candidate is already present");
        }
        return onboardRepository.save(onboard);
    }

    public List<Onboard> getAll(){
        return onboardRepository.findAll();
    }

    public Onboard update(Long id, Onboard updatedOnboard) {
        Onboard existingOnboard = onboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Onboard candidate not found with id: " + id));

        existingOnboard.setName(updatedOnboard.getName());
        existingOnboard.setEmail(updatedOnboard.getEmail());
        existingOnboard.setPhoneNo(updatedOnboard.getPhoneNo());
        existingOnboard.setLocation(updatedOnboard.getLocation());
        existingOnboard.setRole(updatedOnboard.getRole());
        existingOnboard.setWorkplaceType(updatedOnboard.getWorkplaceType());
        existingOnboard.setEmploymentType(updatedOnboard.getEmploymentType());
        existingOnboard.setOnboarded_By(updatedOnboard.getOnboarded_By());
        existingOnboard.setOnboard_company_name(updatedOnboard.getOnboard_company_name());
        existingOnboard.setResourceAvailability(updatedOnboard.getResourceAvailability());
        existingOnboard.setStatus(updatedOnboard.getStatus());
        existingOnboard.setExperience(updatedOnboard.getExperience());
        existingOnboard.setSkills(updatedOnboard.getSkills());

        return onboardRepository.save(existingOnboard);
    }


    public Void delete(Long id){
        onboardRepository.deleteById(id);
        return null;
    }

}
