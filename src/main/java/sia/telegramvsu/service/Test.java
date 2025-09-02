package sia.telegramvsu.service;

import sia.telegramvsu.model.WeekDay;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        ExcelParser parser = new ExcelParser();
        parser.parseExel();
        var s = parser.getWeekSubjectsTeacher("Иванова Ж. В.");

        System.out.println(s);
    }
}
