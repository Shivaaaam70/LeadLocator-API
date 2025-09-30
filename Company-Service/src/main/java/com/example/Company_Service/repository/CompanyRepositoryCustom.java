package com.example.Company_Service.repository;

import com.example.Company_Service.entity.Company;
import java.util.List;

public interface CompanyRepositoryCustom {
    int[][] batchInsertIgnore(List<Company> companies);   // old: insert only
    int[][] batchUpsert(List<Company> companies);         // new: insert or update
}
