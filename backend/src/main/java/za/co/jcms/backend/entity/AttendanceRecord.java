package za.co.jcms.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "attendance_records",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_attendance_registration", columnNames = {"registration_id"})
    }
)
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Integer attendanceId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    private Registration registration;

    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "notes", length = 255)
    private String notes;

    public AttendanceRecord() {
    }

    public AttendanceRecord(Registration registration, String status, String notes) {
        this.registration = registration;
        this.status = status != null ? status : "PRESENT";
        this.notes = notes;
        this.markedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.markedAt == null) {
            this.markedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "PRESENT";
        }
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public LocalDateTime getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(LocalDateTime markedAt) {
        this.markedAt = markedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
