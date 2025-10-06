package com.example.leadmanagement.service;

import com.example.leadmanagement.entity.Lead;
import com.example.leadmanagement.exception.EmailAlreadyExistsException;
import com.example.leadmanagement.exception.ResourceNotFoundException;
import com.example.leadmanagement.repository.LeadRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Lead createLead(Lead lead) {
        lead.setId(UUID.randomUUID().toString());

        if (leadRepository.existsByEmail(lead.getEmail())) {
            throw new EmailAlreadyExistsException("This email already exists!");
        }
        return leadRepository.save(lead);
    }

    public long getLeadCount(String broughtBy) {
        return leadRepository.countByBroughtBy(broughtBy);
    }

    // read by id
    public Lead getLeadById(String id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with " + id));
    }

    //  Read all
    public List<Lead> getAllLead() {
        return leadRepository.findAll();
    }

    // update
    public Lead updateLead(String id , Lead leadDetails) {
       Lead exisitingLead=leadRepository.findById(id).orElseThrow(()->new RuntimeException("Lead is not found with: "+id));

       exisitingLead.setName(leadDetails.getName());
       exisitingLead.setEmail(leadDetails.getEmail());
       exisitingLead.setLocation(leadDetails.getLocation());
       exisitingLead.setNote(leadDetails.getNote());
       exisitingLead.setRequirement(leadDetails.getRequirement());
       exisitingLead.setStatus(leadDetails.getStatus());
       exisitingLead.setContact(leadDetails.getContact());
       exisitingLead.setBroughtBy(leadDetails.getBroughtBy());



        return leadRepository.save(exisitingLead);
    }


    // Delete
    public void deleteLead(String id) {
        Lead existingLead = getLeadById((id));
        leadRepository.delete((existingLead));
    }
}
