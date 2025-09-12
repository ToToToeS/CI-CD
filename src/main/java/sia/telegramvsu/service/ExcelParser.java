package sia.telegramvsu.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sia.telegramvsu.model.LessonVSU;
import sia.telegramvsu.model.WeekDay;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

import static sia.telegramvsu.service.ApachePoi.parseExcelWithMergedCells;

@Slf4j
@Data
@Component
public class ExcelParser {

    @Value("${path.excel}")
    private String filePath;

    final int ROWS_BETWEEN_LESSONS = 3;
    final int TIME_COLUMN_NUMBER = 2;
    final int GROUPS_ROW_NUMBER = 13;

    private Map<String, Map<WeekDay,List<LessonVSU>>> schedule;

    public void parseExel() throws IOException {
        Workbook workbook;
        FileInputStream fis;
        Map<String, Map<WeekDay,List<LessonVSU>>> schedule = new HashMap<>();


        try {
            File folder = new File(filePath);
            File[] files = folder.listFiles();
            for (var file : files) {

                if (file.getName().contains(".xlsx")) {

                    WeekDay dayNow = null;
                    List<List<String>> list = parseExcelWithMergedCells(file);



                    for (int indexGroup = 3; indexGroup < list.get(GROUPS_ROW_NUMBER).size(); indexGroup++) {
                        Map<WeekDay,List<LessonVSU>> dayLessonVSUMap = new HashMap<>();

                        for (int i = 15; i < list.size()-4; i+=ROWS_BETWEEN_LESSONS) {
                            while (list.get(i).get(0).isEmpty()) {
                                if (i < list.size()-4) i++;
                                else break;
                            }
                            final int finalI = i;

                            var dayNowOptional = Arrays.stream(WeekDay.values())
                                    .filter(weekDay -> weekDay.dayString.equals(list.get(finalI).get(0)))
                                    .findFirst();

                            if (dayNowOptional.isPresent()) {
                                dayNow = dayNowOptional.get();
                            } else continue;


                            LessonVSU lesson = new LessonVSU();
                            lesson.setNumber(list.get(i).get(TIME_COLUMN_NUMBER));
                            lesson.setTime(list.get(i + 1).get(TIME_COLUMN_NUMBER));

                            lesson.setSubject(list.get(i).get(indexGroup));
                            lesson.setLector(list.get(i + 1).get(indexGroup));
                            lesson.setAuditorium(list.get(i + 2).get(indexGroup));

                            if (!dayLessonVSUMap.containsKey(dayNow)) {
                                dayLessonVSUMap.put(dayNow, new ArrayList<>());
                            }

                            dayLessonVSUMap.get(dayNow).add(lesson);
                        }

                        schedule.put(list.get(GROUPS_ROW_NUMBER).get(indexGroup).trim(), dayLessonVSUMap);
                    }

                }
            }
            this.schedule=schedule;
            log.info("Excel file parsed successfully");
        }catch (IOException e){
            log.error("Error parse exel" + e.getMessage());
        }


    }

    public String getDaySubjectsStudent(WeekDay weekDay, String groupName) {
        return formatLessons(schedule.get(groupName).get(weekDay), weekDay);
    }

    public String getWeekSubjectsStudent(String groupName) {
        StringBuilder sb = new StringBuilder();
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        for (WeekDay weekDay : WeekDay.values()) {
            sb.append(getDaySubjectsStudent(weekDay, groupName))
            .append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        }
        return sb.toString();
    }

    public String getDaySubjectsTeacher(WeekDay weekDay, String nameTeacher) {
        List<LessonVSU> lessons = new ArrayList<>();
        Set<String> groups = schedule.keySet();

        for (String group : groups) {
            List<LessonVSU> dayLessons = schedule.get(group).get(weekDay);
            if (dayLessons == null) continue;
            for (LessonVSU lesson : dayLessons) {
                if (lesson.getLector().equals(nameTeacher)) {
                    if (!lessons.isEmpty() &&lessons.get(lessons.size() -1).getNumber().equals(lesson.getNumber())) {
                        lessons.get(lessons.size() -1).setLector(lessons.get(lessons.size() -1).getLector() + ", " + group);
                    }else {
                        LessonVSU cloneLesson = lesson.clone();
                        cloneLesson.setLector(group);
                        lessons.add(cloneLesson);
                    }


                }
            }
        }

        Collections.sort(lessons, new Comparator<LessonVSU>() {
            @Override
            public int compare(LessonVSU o1, LessonVSU o2) {
                return o1.getNumber().compareTo(o2.getNumber());
            }
        });

        return formatLessons(lessons, weekDay);
    }

    public String getWeekSubjectsTeacher(String nameTeacher) {
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        for (WeekDay weekDay : WeekDay.values()) {
            sb.append(getDaySubjectsTeacher(weekDay, nameTeacher))
                    .append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        }
        return sb.toString();
    }

    private String formatLessons(List<LessonVSU> lessons, WeekDay weekDay) {
       StringBuilder sb = new StringBuilder();

        sb.append("""
                <u><b>%s:</b></u>
                
                """.formatted(weekDay.dayString));
        for (LessonVSU lesson : lessons) {
            if (!lesson.getSubject().isEmpty()) {
                sb.append("""
                        <b>№%s</b> %s
                          <b>%s</b>
                          %s
                          %s
                        
                        """.formatted(lesson.getNumber(), lesson.getTime(), lesson.getSubject(), lesson.getLector(), lesson.getAuditorium()));
            }
        }
        return sb.toString();
    }

    public String getGroupHowInSchedule(String group) {
        var groupOptional = schedule.keySet().stream().filter((groupStream) -> groupStream.equalsIgnoreCase(group)).findFirst();

        return groupOptional.orElse(null);
    }

    public String getTeacherHowInSchedule(String lector) {
        Optional<LessonVSU> lessonOptional = schedule.values().stream()
                .flatMap(dayLessons -> dayLessons.values().stream()) //Map<String, Map<WeekDay,List<LessonVSU>>>
                .flatMap(lessons -> lessons.stream())
                .filter(lessonVSU -> lessonVSU.getLector().trim().equals(lector))
                .findFirst();

        return lessonOptional.map(LessonVSU::getLector).orElse(null);
    }
}