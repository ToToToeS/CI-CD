package shatilo.springtgbot.model;

import lombok.Data;

@Data
public class LessonVSU {
    private String number;
    private String time;
    private String subject;
    private String lector;
    private String auditorium;

    @Override
    public String toString() {
        return "Lesson{" +
                "number='" + number + '\'' +
                ", time='" + time + '\'' +
                ", subject='" + subject + '\'' +
                ", lector='" + lector + '\'' +
                ", auditorium='" + auditorium + '\'' +
                '}';
    }
}
