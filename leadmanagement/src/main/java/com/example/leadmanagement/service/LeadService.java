package com.example.leadmanagement.service;

import com.example.leadmanagement.entity.Lead;
import com.example.leadmanagement.exception.EmailAlreadyExistsException;
import com.example.leadmanagement.exception.ResourceNotFoundException;
import com.example.leadmanagement.repository.LeadRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Lead createLead(Lead lead) {
        if (leadRepository.existsByEmail(lead.getEmail())) {
            throw new EmailAlreadyExistsException("This email already exists!");
        }
        return leadRepository.save(lead);
    }

    public long getLeadCount(String broughtBy) {
        return leadRepository.countByBroughtBy(broughtBy);
    }
    // read by id
    public Lead getLeadById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with " + id));
    }
    //  Read all
    public List<Lead> getAllLead() {
        return leadRepository.findAll();
    }

    // update
    public Lead updateLead(long id , Lead leadDetails) {
        Lead existingLead = getLeadById(id);

        if(!existingLead.getEmail().equals(leadDetails.getEmail()) &&
                leadRepository.existsByEmail(leadDetails.getEmail())) {
            throw new EmailAlreadyExistsException("This email already exists!");
        }
        existingLead.setName(leadDetails.getName());
        existingLead.setEmail(leadDetails.getEmail());
        existingLead.setLocation(leadDetails.getLocation());
        existingLead.setBroughtBy(leadDetails.getBroughtBy());
        existingLead.setDescription(leadDetails.getDescription());
        existingLead.setRequirement(leadDetails.getRequirement());


        return leadRepository.save(existingLead);
    }


    // Delete
    public void deleteLead(Long id) {
        Lead existingLead = getLeadById((id));
        leadRepository.delete((existingLead));
    }
}
