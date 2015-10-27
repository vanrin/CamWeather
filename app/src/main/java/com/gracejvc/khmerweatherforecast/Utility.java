package com.gracejvc.khmerweatherforecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gracejvc.khmerweatherforecast.data.WeatherContract;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Chhea Vanrin on 7/15/2015.
 */
public class Utility {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }
    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

   public static String formatTemperature(Context context, double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return context.getString(R.string.format_temperature, temp);
    }
//    static String formatDate(String dateString) {
//        Date date = WeatherContract.getDateFromDb(dateString);
//        return getKhmerDate(date);
//    }
    public  enum DayOfWeek{
    MONDAY("ចន្ទ"),TUESDAY("អង្គារ"),WEDNESDAY("ពុធ"),THURSDAY("ព្រហស្បត្ត៏"),FRIDAY("សុក្រ"),SATURDAY("សៅរ៏​"),SUNDAY("អាទិត្យ");
    private final String dayName;

    DayOfWeek(String dayName) {
        this.dayName = dayName;
    }

    public String getDayName() {
        return dayName;
    }
}
    public enum MonthOfYear{
        JANUARY("មករា"),
        FEBRUARY("កុម្ភះ"),
        MARCH("មិនា"),
        APRIL("មេសា"),
        MAY("ឩសភា"),
        JUNE("មិថុនា"),
        JULY("កក្កដា"),
        AUGUST("សីហា"),
        SEPTEMBER("កញ្ញា"),
        OCTOBER("តុលា"),
        NOVEMBER("វិច្ឆិកា"),
        DECEMBER("ធ្នូរ");
        private final String monthName;

        MonthOfYear(String monthName) {
            this.monthName = monthName;
        }

        public String getMonthName() {
            return monthName;
        }
    }
//    public static final String DATE_FORMAT = "yyyyMMdd";
//   static String getKhmerDate(Date date){
//       String khmerDate=getKhmerDay(date)+ " - " + getKhmerMonth(date)+ " - " + date.getDate()+"th";
//       return khmerDate;
//   }
    static String getKhmerDay(Date date){
        String khmerDay;
        switch (date.getDay()){
            case 1:{khmerDay=DayOfWeek.MONDAY.getDayName();break;}
            case 2:{khmerDay=DayOfWeek.TUESDAY.getDayName();break;}
            case 3:{khmerDay=DayOfWeek.WEDNESDAY.getDayName();break;}
            case 4:{khmerDay=DayOfWeek.THURSDAY.getDayName();break;}
            case 5:{khmerDay=DayOfWeek.FRIDAY.getDayName();break;}
            case 6:{khmerDay=DayOfWeek.SATURDAY.getDayName();break;}
            case 7:{khmerDay=DayOfWeek.SUNDAY.getDayName();break;}
            default:{khmerDay= DayOfWeek.SUNDAY.getDayName();}
        }
        return khmerDay;
    }
    static String getKhmerMonth(Date date){
        String khmerMonth;
        switch (date.getMonth()){
            case 0:{khmerMonth= MonthOfYear.JANUARY.getMonthName();break;}
            case 1:{khmerMonth=MonthOfYear.FEBRUARY.getMonthName();break;}
            case 2:{khmerMonth=MonthOfYear.MARCH.getMonthName();break;}
            case 3:{khmerMonth=MonthOfYear.APRIL.getMonthName();break;}
            case 4:{khmerMonth=MonthOfYear.MAY.getMonthName();break;}
            case 5:{khmerMonth=MonthOfYear.JUNE.getMonthName();break;}
            case 6:{khmerMonth=MonthOfYear.JULY.getMonthName();break;}
            case 7:{khmerMonth =MonthOfYear.AUGUST.getMonthName();break;}
            case 8:{khmerMonth=MonthOfYear.SEPTEMBER.getMonthName();break;}
            case 9:{khmerMonth=MonthOfYear.OCTOBER.getMonthName();break;}
            case 10:{khmerMonth =MonthOfYear.NOVEMBER.getMonthName();break;}
            case 11:{khmerMonth =MonthOfYear.DECEMBER.getMonthName();break;}
            default:{khmerMonth=MonthOfYear.DECEMBER.getMonthName();}
        }
        return khmerMonth;
    }
    public static String getKhmerDate(Context context,String date){
        Date inputDate = WeatherContract.getDateFromDb(date);
        return  getKhmerDay(inputDate) + " "  + getFormattedMonthDay(context,date);
    }
    public static String getFriendlyDayString(Context context, String dateStr) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"

        Date todayDate = new Date();
        String todayStr = WeatherContract.getDbDateString(todayDate);
        Date inputDate = WeatherContract.getDateFromDb(dateStr);
        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (todayStr.equals(dateStr)) {
            String today = context.getString(R.string.today);
            return today+" "+ getFormattedMonthDay(context, dateStr);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(todayDate);
            cal.add(Calendar.DATE, 7);
            String weekFutureString = WeatherContract.getDbDateString(cal.getTime());

            if (dateStr.compareTo(weekFutureString) < 0) {
                // If the input date is less than a week in the future, just return the day name.
                return getDayName(context, dateStr);
            } else {
                // Otherwise, use the form "Mon Jun 3"

                return getKhmerDay(inputDate)  + " "+ getKhmerMonth(inputDate)+ " " + inputDate.getDate();
            }
        }
    }
    public static String getDayName(Context context, String dateStr) {

        try {
            Date inputDate = WeatherContract.getDateFromDb(dateStr);
            Date todayDate = new Date();
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.
            if (WeatherContract.getDbDateString(todayDate).equals(dateStr)) {
                return context.getString(R.string.today);
            } else {
                // If the date is set for tomorrow, the format is "Tomorrow".
                Calendar cal = Calendar.getInstance();
                cal.setTime(todayDate);
                cal.add(Calendar.DATE, 1);
                Date tomorrowDate = cal.getTime();
                if (WeatherContract.getDbDateString(tomorrowDate).equals(
                        dateStr)) {
                    return context.getString(R.string.tomorrow);
                } else {
                    // Otherwise, the format is just the day of the week (e.g "Wednesday".

                    return "ថ្ងៃ"+getKhmerDay(inputDate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // It couldn't process the date correctly.
            return "";
        }
    }
    public static String getFormattedMonthDay(Context context, String dateStr) {
        try {
            Date inputDate = WeatherContract.getDateFromDb(dateStr);
            String monthDayString = getKhmerMonth(inputDate)+ " " + inputDate.getDate();
            return monthDayString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getFormattedWind(Context context, float windSpeed, float degrees) {
        int windFormat;
        if (Utility.isMetric(context)) {
            windFormat = R.string.format_wind_kmh;
        } else {
            windFormat = R.string.format_wind_mph;
            windSpeed = .621371192237334f * windSpeed;
        }

        // From wind direction in degrees, determine compass direction as a string (e.g NW)
        // You know what's fun, writing really long if/else statements with tons of possible
        // conditions.  Seriously, try it!
        String direction = "Unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction =context.getString(R.string.N);
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction =context.getString(R.string.NE);
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction =context.getString(R.string.E);
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = context.getString(R.string.SE);
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction =context.getString(R.string.S);
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction =context.getString(R.string.SW);
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction =context.getString(R.string.W);
        } else if (degrees >= 292.5 || degrees < 22.5) {
            direction =context.getString(R.string.NW);
        }
        return String.format(context.getString(windFormat), windSpeed, direction);
    }
    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding image. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_rain;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }
    public static String getDescriptionForWeatherCondition(Context context,int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        int stringId;
        if (weatherId >= 200 && weatherId <= 232) {
            stringId = R.string.condition_2xx;
        } else if (weatherId >= 300 && weatherId <= 321) {
            stringId = R.string.condition_3xx;
        } else switch(weatherId) {
            case 500:
                stringId = R.string.condition_500;
                break;
            case 501:
                stringId = R.string.condition_501;
                break;
            case 502:
                stringId = R.string.condition_502;
                break;
            case 503:
                stringId = R.string.condition_503;
                break;
            case 504:
                stringId = R.string.condition_504;
                break;
            case 511:
                stringId = R.string.condition_511;
                break;
            case 520:
                stringId = R.string.condition_520;
                break;
            case 531:
                stringId = R.string.condition_531;
                break;
            case 600:
                stringId = R.string.condition_600;
                break;
            case 601:
                stringId = R.string.condition_601;
                break;
            case 602:
                stringId = R.string.condition_602;
                break;
            case 611:
                stringId = R.string.condition_611;
                break;
            case 612:
                stringId = R.string.condition_612;
                break;
            case 615:
                stringId = R.string.condition_615;
                break;
            case 616:
                stringId = R.string.condition_616;
                break;
            case 620:
                stringId = R.string.condition_620;
                break;
            case 621:
                stringId = R.string.condition_621;
                break;
            case 622:
                stringId = R.string.condition_622;
                break;
            case 701:
                stringId = R.string.condition_701;
                break;
            case 711:
                stringId = R.string.condition_711;
                break;
            case 721:
                stringId = R.string.condition_721;
                break;
            case 731:
                stringId = R.string.condition_731;
                break;
            case 741:
                stringId = R.string.condition_741;
                break;
            case 751:
                stringId = R.string.condition_751;
                break;
            case 761:
                stringId = R.string.condition_761;
                break;
            case 762:
                stringId = R.string.condition_762;
                break;
            case 771:
                stringId = R.string.condition_771;
                break;
            case 781:
                stringId = R.string.condition_781;
                break;
            case 800:
                stringId = R.string.condition_800;
                break;
            case 801:
                stringId = R.string.condition_801;
                break;
            case 802:
                stringId = R.string.condition_802;
                break;
            case 803:
                stringId = R.string.condition_803;
                break;
            case 804:
                stringId = R.string.condition_804;
                break;
            case 900:
                stringId = R.string.condition_900;
                break;
            case 901:
                stringId = R.string.condition_901;
                break;
            case 902:
                stringId = R.string.condition_902;
                break;
            case 903:
                stringId = R.string.condition_903;
                break;
            case 904:
                stringId = R.string.condition_904;
                break;
            case 905:
                stringId = R.string.condition_905;
                break;
            case 906:
                stringId = R.string.condition_906;
                break;
            case 951:
                stringId = R.string.condition_951;
                break;
            case 952:
                stringId = R.string.condition_952;
                break;
            case 953:
                stringId = R.string.condition_953;
                break;
            case 954:
                stringId = R.string.condition_954;
                break;
            case 955:
                stringId = R.string.condition_955;
                break;
            case 956:
                stringId = R.string.condition_956;
                break;
            case 957:
                stringId = R.string.condition_957;
                break;
            case 958:
                stringId = R.string.condition_958;
                break;
            case 959:
                stringId = R.string.condition_959;
                break;
            case 960:
                stringId = R.string.condition_960;
                break;
            case 961:
                stringId = R.string.condition_961;
                break;
            case 962:
                stringId = R.string.condition_962;
                break;
            default:
                return context.getString(R.string.condition_unknown, weatherId);
        }
        return context.getString(stringId);
    }

    /**
     * Helper method to provide the art urls according to the weather condition id returned
     * by the OpenWeatherMap call.
     *
     * @param context Context to use for retrieving the URL format
     * @param weatherId from OpenWeatherMap API response
     * @return url for the corresponding weather artwork. null if no relation is found.
     */
    public static String getArtUrlForWeatherCondition(Context context, int weatherId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String formatArtUrl = prefs.getString(context.getString(R.string.pref_art_pack_key),
                context.getString(R.string.pref_art_pack_sunshine));

        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return String.format(Locale.US, formatArtUrl, "storm");
        } else if (weatherId >= 300 && weatherId <= 321) {
            return String.format(Locale.US, formatArtUrl, "light_rain");
        } else if (weatherId >= 500 && weatherId <= 504) {
            return String.format(Locale.US, formatArtUrl, "rain");
        } else if (weatherId == 511) {
            return String.format(Locale.US, formatArtUrl, "snow");
        } else if (weatherId >= 520 && weatherId <= 531) {
            return String.format(Locale.US, formatArtUrl, "rain");
        } else if (weatherId >= 600 && weatherId <= 622) {
            return String.format(Locale.US, formatArtUrl, "snow");
        } else if (weatherId >= 701 && weatherId <= 761) {
            return String.format(Locale.US, formatArtUrl, "fog");
        } else if (weatherId == 761 || weatherId == 781) {
            return String.format(Locale.US, formatArtUrl, "storm");
        } else if (weatherId == 800) {
            return String.format(Locale.US, formatArtUrl, "clear");
        } else if (weatherId == 801) {
            return String.format(Locale.US, formatArtUrl, "light_clouds");
        } else if (weatherId >= 802 && weatherId <= 804) {
            return String.format(Locale.US, formatArtUrl, "clouds");
        }
        return null;
    }

    public static String getNotificationDescription(Context context,int weatherId){
        if (weatherId >= 200 && weatherId <= 232) {
            return context.getString(R.string.Notify_Storm);
        } else if (weatherId >= 300 && weatherId <= 321) {
            return context.getString(R.string.Notify_Rain);
        } else if (weatherId >= 500 && weatherId <= 504) {
            return context.getString(R.string.Notify_Rain);
        } else if (weatherId == 511) {
            return context.getString(R.string.Notify_Snow);
        } else if (weatherId >= 520 && weatherId <= 531) {
            return context.getString(R.string.Notify_Rain);
        } else if (weatherId >= 600 && weatherId <= 622) {
            return context.getString(R.string.Notify_Snow);
        } else if (weatherId >= 701 && weatherId <= 761) {
            return context.getString(R.string.Notify_Fog);
        } else if (weatherId == 761 || weatherId == 781) {
            return context.getString(R.string.Notify_Storm);
        } else if (weatherId == 800) {
            return context.getString(R.string.Notify_Clear);
        } else if (weatherId == 801) {
            return context.getString(R.string.Notify_Cloudy);
        } else if (weatherId >= 802 && weatherId <= 804) {
            return context.getString(R.string.Notify_Cloudy);
        }
        return null;
    }
}
