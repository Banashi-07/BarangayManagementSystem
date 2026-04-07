package database;

import java.time.LocalDate;
import java.time.Period;

public class Resident {
    private int id;
    private String name;
    private String sex;
    private String address;
    private String purok;
    private String contact;
    private String birthdate;
    private String civilStatus;
    private String pwd;
    private String registeredDate;

    // Constructors
    public Resident() {}
    
    public Resident(String name, String sex, String address, String purok, 
                    String contact, String birthdate, String civilStatus, String pwd) {
        this.name = name;
        this.sex = sex;
        this.address = address;
        this.purok = purok;
        this.contact = contact;
        this.birthdate = birthdate;
        this.civilStatus = civilStatus;
        this.pwd = pwd;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPurok() { return purok; }
    public void setPurok(String purok) { this.purok = purok; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }
    
    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String civilStatus) { this.civilStatus = civilStatus; }
    
    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }
    
    public String getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(String registeredDate) { this.registeredDate = registeredDate; }
    
    // Helper methods
    public boolean isPwd() {
        return pwd != null && pwd.equalsIgnoreCase("Yes");
    }
    
    public int getAge() {
        if (birthdate == null || birthdate.isBlank()) return 0;
        try {
            LocalDate birth = LocalDate.parse(birthdate);
            return Math.max(Period.between(birth, LocalDate.now()).getYears(), 0);
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return name + " (Age: " + getAge() + ")";
    }
}