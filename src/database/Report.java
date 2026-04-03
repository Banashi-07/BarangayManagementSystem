package database;

public class Report {
    private int id;
    private String title;           // New: Report title
    private String description;
    private String incidentDate;
    private String status;
    private String settlementDescription;
    private String createdDate;
    private String settledDate;
    private int complainantId;      // Changed from citizenId to complainantId
    private int complaineeId;       // New: ID of the person being complained about
    
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
    
    // Legacy constructor for backward compatibility (optional)
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
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
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
    
    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", incidentDate='" + incidentDate + '\'' +
                ", status='" + status + '\'' +
                ", complainantId=" + complainantId +
                ", complaineeId=" + complaineeId +
                '}';
    }
}