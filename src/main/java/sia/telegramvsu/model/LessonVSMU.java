package sia.telegramvsu.model;

import lombok.Data;

import java.util.List;

@Data
public class LessonVSMU {
    private String time;
    private String name;
    private List<Integer> groups;

    @Override
    public String toString() {
        return   time + ' ' +
                 name;
    }
}
