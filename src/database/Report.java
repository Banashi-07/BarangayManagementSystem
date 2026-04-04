package database;

public class Report {
    private int id;
    private String title;           // Report title
    private String description;
    private String incidentDate;
    private String status;
    private String settlementDescription;
    private String createdDate;
    private String settledDate;
    private int complainantId;      // Changed from citizenId to complainantId
    private int complaineeId;       // ID of the person being complained about
    
    // Constructors
    public Report() {}
    
    // Full constructor with all fields
    public Report(int id, String title, String description, String incidentDate, String status, 
                  String settlementDescription, String createdDate, String settledDate, 
                  int complainantId, int complaineeId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.incidentDate = incidentDate;
        this.status = status;
        this.settlementDescription = settlementDescription;
        this.createdDate = createdDate;
        this.settledDate = settledDate;
        this.complainantId = complainantId;
        this.complaineeId = complaineeId;
    }
    
    // Legacy constructor for backward compatibility
    public Report(int id, String description, String incidentDate, String status, 
                  String settlementDescription, String createdDate, String settledDate, int citizenId) {
        this.id = id;
        this.title = "";
        this.description = description;
        this.incidentDate = incidentDate;
        this.status = status;
        this.settlementDescription = settlementDescription;
        this.createdDate = createdDate;
        this.settledDate = settledDate;
        this.complainantId = citizenId;
        this.complaineeId = -1;
    }
    
    // Simplified constructor
    public Report(String description, String incidentDate, String status) {
        this.description = description;
        this.incidentDate = incidentDate;
        this.status = status;
        this.title = "";
        this.complainantId = -1;
        this.complaineeId = -1;
    }
    
    // Constructor with complainant
    public Report(String description, String incidentDate, String status, int complainantId) {
        this.description = description;
        this.incidentDate = incidentDate;
        this.status = status;
        this.complainantId = complainantId;
        this.complaineeId = -1;
        this.title = "";
    }
    
    // Full constructor without id
    public Report(String title, String description, String incidentDate, String status, 
                  int complainantId, int complaineeId) {
        this.title = title;
        this.description = description;
        this.incidentDate = incidentDate;
        this.status = status;
        this.complainantId = complainantId;
        this.complaineeId = complaineeId;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title != null ? title : "";
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIncidentDate() {
        return incidentDate;
    }
    
    public void setIncidentDate(String incidentDate) {
        this.incidentDate = incidentDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getSettlementDescription() {
        return settlementDescription;
    }
    
    public void setSettlementDescription(String settlementDescription) {
        this.settlementDescription = settlementDescription;
    }
    
    public String getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getSettledDate() {
        return settledDate;
    }
    
    public void setSettledDate(String settledDate) {
        this.settledDate = settledDate;
    }
    
    public int getComplainantId() {
        return complainantId;
    }
    
    public void setComplainantId(int complainantId) {
        this.complainantId = complainantId;
    }
    
    public int getComplaineeId() {
        return complaineeId;
    }
    
    public void setComplaineeId(int complaineeId) {
        this.complaineeId = complaineeId;
    }
    
    // Legacy method for backward compatibility
    @Deprecated
    public int getCitizenId() {
        return complainantId;
    }
    
    @Deprecated
    public void setCitizenId(int citizenId) {
        this.complainantId = citizenId;
    }
    
    // Utility methods
    public boolean isSettled() {
        return "Settled".equalsIgnoreCase(status);
    }
    
    public boolean isPending() {
        return "Pending".equalsIgnoreCase(status);
    }
    
    public boolean isInProgress() {
        return "In Progress".equalsIgnoreCase(status);
    }
    
    public boolean isDismissed() {
        return "Dismissed".equalsIgnoreCase(status);
    }
    
    public boolean hasComplainant() {
        return complainantId > 0;
    }
    
    public boolean hasComplainee() {
        return complaineeId > 0;
    }
    
    public boolean hasTitle() {
        return title != null && !title.trim().isEmpty();
    }
    
    public String getShortDescription(int maxLength) {
        if (description == null) return "";
        if (description.length() <= maxLength) return description;
        return description.substring(0, maxLength) + "...";
    }
    
    public String getFormattedStatus() {
        switch (status != null ? status.toLowerCase() : "") {
            case "pending":
                return "⏳ Pending";
            case "in progress":
                return "🔄 In Progress";
            case "settled":
                return "✅ Settled";
            case "dismissed":
                return "❌ Dismissed";
            default:
                return status != null ? status : "Unknown";
        }
    }
    
    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + (description != null ? description.substring(0, Math.min(50, description.length())) : "null") + '\'' +
                ", incidentDate='" + incidentDate + '\'' +
                ", status='" + status + '\'' +
                ", complainantId=" + complainantId +
                ", complaineeId=" + complaineeId +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Report report = (Report) obj;
        return id == report.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    // Builder pattern for easier object creation
    public static class Builder {
        private int id;
        private String title;
        private String description;
        private String incidentDate;
        private String status = "Pending";
        private String settlementDescription;
        private String createdDate;
        private String settledDate;
        private int complainantId = -1;
        private int complaineeId = -1;
        
        public Builder id(int id) {
            this.id = id;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder incidentDate(String incidentDate) {
            this.incidentDate = incidentDate;
            return this;
        }
        
        public Builder status(String status) {
            this.status = status;
            return this;
        }
        
        public Builder settlementDescription(String settlementDescription) {
            this.settlementDescription = settlementDescription;
            return this;
        }
        
        public Builder createdDate(String createdDate) {
            this.createdDate = createdDate;
            return this;
        }
        
        public Builder settledDate(String settledDate) {
            this.settledDate = settledDate;
            return this;
        }
        
        public Builder complainantId(int complainantId) {
            this.complainantId = complainantId;
            return this;
        }
        
        public Builder complaineeId(int complaineeId) {
            this.complaineeId = complaineeId;
            return this;
        }
        
        public Report build() {
            Report report = new Report();
            report.setId(id);
            report.setTitle(title);
            report.setDescription(description);
            report.setIncidentDate(incidentDate);
            report.setStatus(status);
            report.setSettlementDescription(settlementDescription);
            report.setCreatedDate(createdDate);
            report.setSettledDate(settledDate);
            report.setComplainantId(complainantId);
            report.setComplaineeId(complaineeId);
            return report;
        }
    }
}