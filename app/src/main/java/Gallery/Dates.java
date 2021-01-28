package Gallery;

import java.io.File;
import java.util.List;

public class Dates {
    String dateTime;
    String date;
    String time;
    String year;
    String month;
    String day;
    String hours;
    String minutes;
    String seconds;

    public static String getDate(String dateTime) {
        return dateTime.split(",")[0];
    }

    public static String getTime(String dateTime) {
        return dateTime.split(",")[1];
    }

    /////////////////Breaking the date////////////////////

    public static int getYear(String dateTime) {
        String date = getDate(dateTime);
        return Integer.parseInt(date.split(" ")[2]);
    }

    public static int getMonth(String dateTime) {
        String date = getDate(dateTime);
        String monthName = date.split(" ")[1];
        return getNumericMonth(monthName);
    }

    public static int getDay(String dateTime) {
        String date = getDate(dateTime);
        return Integer.parseInt(date.split(" ")[0]);
    }

    /////////////////Breaking the time////////////////////

    public static int getHour(String dateTime) {
        String date = getTime(dateTime);
        return Integer.parseInt(date.split(":")[0]);
    }

    public static int getMinute(String dateTime) {
        String date = getTime(dateTime);
        return Integer.parseInt(date.split(":")[1]);
    }

    public static int getSeconds(String dateTime) {
        String date = getTime(dateTime);
        return Integer.parseInt(date.split(":")[2]);
    }

    private static int getNumericMonth(String monthName) {
        int num;
        switch (monthName) {
            case "Jan":
                return 1;
            case "Fed":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "jun":
                return 6;
            case "jul":
                return 7;
            case "Aug":
                return 8;
            case "Sept":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                num = 11;
            case "Dec":
                num = 12;
        }
        return 0;
    }


    public static File getOldestItem(String fileType) {
        List<Item> items = Items.getTemporaryFiles();
        String currentCompare;
        String dateTime;
        // check which of the items we want to get
        switch (fileType) {
            case "temporary videos":
                items = Items.getTemporaryFiles();
                break;
            case "saved videos":
                items = Items.getSavedFiles();
                break;
            case "images":
                items = Items.getImages();
                break;
        }

        //go through all items
        for (Item item : items) {
            //compare year, if equal continue to month, day, hour, minutes, seconds
            int year = getYear(item.getDate());

        }
        return items.get(0).getFile();
    }

//    public static List<File> getEqualItems(List<Item> items) {
//        int min = getYear(items.get(0).getDate());
//        Item minItem = items.get(0);
//
//        for (Item item : items) {
//            if (getYear(item.getDate()) <= min) {
//                min = getYear(item.getDate());
//                minItem = item;
//            }
//        }
//        for (Item item : items) {
//            if (getYear(item.getDate()) == min) {
//
//            }
//        }
//
//
//    }
}