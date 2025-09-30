package com.example.Crawler_Service1.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Jobs {

    @Id
    private String id;

    private String site;
    private String position;
    private String company;

    @Column(name = "url", length = 2000)
    private String url;

    @Column(length = 500)
    private String location;

    @Column(length = 5000)
    private String tags;

    @Lob
    private String description;
    
    private String date;

    private boolean active = true;

    // Constructors
    public Jobs() {}

    public Jobs(String id, String site, String position, String company, String url, String location, String tags, String description, String date, boolean active) {
        this.id = id;
        this.site = site;
        this.position = position;
        this.company = company;
        this.url = url;
        this.location = location;
        this.tags = tags;
        this.description = description;
        this.date = date;
        this.active = active;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getSite() {
        return site;
    }

    public String getPosition() {
        return position;
    }

    public String getCompany() {
        return company;
    }

    public String getUrl() {
        return url;
    }

    public String getLocation() {
        return location;
    }

    public String getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public boolean isActive() {
        return active;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String site;
        private String position;
        private String company;
        private String url;
        private String location;
        private String tags;
        private String description;
        private String date;
        private boolean active = true;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder site(String site) {
            this.site = site;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder tags(String tags) {
            this.tags = tags;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Jobs build() {
            return new Jobs(id, site, position, company, url, location, tags, description, date, active);
        }
    }
}
