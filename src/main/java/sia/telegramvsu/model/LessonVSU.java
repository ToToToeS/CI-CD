package sia.telegramvsu.model;

import lombok.Data;

import java.util.Comparator;

@Data
public class LessonVSU implements Cloneable{
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

    @Override
    public LessonVSU clone() {
        LessonVSU clone = new LessonVSU();

        clone.setNumber(this.getNumber());
        clone.setTime(this.getTime());
        clone.setSubject(this.getSubject());
        clone.setLector(this.getLector());
        clone.setAuditorium(this.getAuditorium());

        return clone;
    }
}
