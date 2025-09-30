package com.example.Company_Service.repository;

import com.example.Company_Service.entity.Company;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    public CompanyRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int[][] batchInsertIgnore(List<Company> companies) {
        String sql = """
            INSERT IGNORE INTO companies (yc_id, name, description, homepage, domain,
                                          locations, team_size, tags, hiring_flag, top_flag, raw_json)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.batchUpdate(sql, companies, 500, (ps, company) -> {
            ps.setString(1, company.getYcId());
            ps.setString(2, company.getName());
            ps.setString(3, company.getDescription());
            ps.setString(4, company.getHomepage());
            ps.setString(5, company.getDomain());
            ps.setString(6, company.getLocations());
            ps.setObject(7, company.getTeamSize());
            ps.setString(8, company.getTags());
            ps.setBoolean(9, company.isHiringFlag());
            ps.setBoolean(10, company.isTopFlag());
            ps.setString(11, company.getRawJson());
        });
    }

    @Override
    public int[][] batchUpsert(List<Company> companies) {
        String sql = """
            INSERT INTO companies (yc_id, name, description, homepage, domain,
                                   locations, team_size, tags, hiring_flag, top_flag, raw_json)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                name = VALUES(name),
                description = VALUES(description),
                homepage = VALUES(homepage),
                domain = VALUES(domain),
                locations = VALUES(locations),
                team_size = VALUES(team_size),
                tags = VALUES(tags),
                hiring_flag = VALUES(hiring_flag),
                top_flag = VALUES(top_flag),
                raw_json = VALUES(raw_json)
            """;

        return jdbcTemplate.batchUpdate(sql, companies, 500, (ps, company) -> {
            ps.setString(1, company.getYcId());
            ps.setString(2, company.getName());
            ps.setString(3, company.getDescription());
            ps.setString(4, company.getHomepage());
            ps.setString(5, company.getDomain());
            ps.setString(6, company.getLocations());
            ps.setObject(7, company.getTeamSize());
            ps.setString(8, company.getTags());
            ps.setBoolean(9, company.isHiringFlag());
            ps.setBoolean(10, company.isTopFlag());
            ps.setString(11, company.getRawJson());
        });
    }
}
