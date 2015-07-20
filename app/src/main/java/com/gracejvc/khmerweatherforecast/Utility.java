package com.gracejvc.khmerweatherforecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gracejvc.khmerweatherforecast.data.WeatherContract;

import java.util.Calendar;
import java.util.Date;

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

    static String formatTemperature(Context context, double temperature, boolean isMetric) {
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
//    public static final String DATE_FORMAT = "yyyyMMdd";
    public static enum dayOfWeek{ច័ន្ទ,អង្គារ,ពុធ,ព្រហស្បតិ,សុក្រ,សៅរ៏ ,អាទិត្យ}
    public static enum monthOfYear{មករា ,កុម្ភះ,មិនា ,មេសា ,ឧសភា ,មិថុនា ,កកដ្ដា,សីហា ,កញ្ញា,តុលា ,វិច្ឆិកា,ធ្នួរ}
//   static String getKhmerDate(Date date){
//       String khmerDate=getKhmerDay(date)+ " - " + getKhmerMonth(date)+ " - " + date.getDate()+"th";
//       return khmerDate;
//   }
    static String getKhmerDay(Date date){
        String khmerDay;
        switch (date.getDay()){
            case 1:{khmerDay=dayOfWeek.ច័ន្ទ.toString();break;}
            case 2:{khmerDay=dayOfWeek.អង្គារ.toString();break;}
            case 3:{khmerDay=dayOfWeek.ពុធ .toString();break;}
            case 4:{khmerDay=dayOfWeek.ព្រហស្បតិ .toString();break;}
            case 5:{khmerDay=dayOfWeek.សុក្រ .toString();break;}
            case 6:{khmerDay=dayOfWeek.សៅរ៏  .toString();break;}
            case 7:{khmerDay=dayOfWeek.អាទិត្យ.toString();break;}
            default:{khmerDay= dayOfWeek.អាទិត្យ.toString();}
        }
        return khmerDay;
    }
    static String getKhmerMonth(Date date){
        String khmerMonth;
        switch (date.getMonth()){
            case 0:{khmerMonth= monthOfYear.មករា .toString();break;}
            case 1:{khmerMonth=monthOfYear.កុម្ភះ.toString();break;}
            case 2:{khmerMonth=monthOfYear.មិនា .toString();break;}
            case 3:{khmerMonth=monthOfYear.មេសា .toString();break;}
            case 4:{khmerMonth=monthOfYear.ឧសភា .toString();break;}
            case 5:{khmerMonth=monthOfYear.មិថុនា .toString();break;}
            case 6:{khmerMonth=monthOfYear.កកដ្ដា.toString();break;}
            case 7:{khmerMonth = monthOfYear.សីហា .toString();break;}
            case 8:{khmerMonth= monthOfYear.កញ្ញា.toString();break;}
            case 9:{khmerMonth= monthOfYear.តុលា .toString();break;}
            case 10:{khmerMonth = monthOfYear.វិច្ឆិកា.toString();break;}
            case 11:{khmerMonth = monthOfYear.ធ្នួរ.toString();break;}
            default:{khmerMonth=monthOfYear.ធ្នួរ.toString();}
        }
        return khmerMonth;
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
            return today+", "+ getFormattedMonthDay(context, dateStr);
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
    public static String getDescriptionForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return "អាចមានខ្យល់កន្រាក់";
        } else if (weatherId >= 300 && weatherId <= 321) {
            return "អាចមានភ្លៀងខ្លាំង";
        } else if (weatherId >= 500 && weatherId <= 504) {
            return "អាចមានភ្លៀងធ្លាក់";
        } else if (weatherId == 511) {
            return "ធ្លាក់ព្រិល";
        } else if (weatherId >= 520 && weatherId <= 531) {
            return "អាចមានភ្លៀងធ្លាក់";
        } else if (weatherId >= 600 && weatherId <= 622) {
            return "ធ្លាក់ព្រិល";
        } else if (weatherId >= 701 && weatherId <= 761) {
            return "មេឃចុះអ័ភ្រ";
        } else if (weatherId == 761 || weatherId == 781) {
            return "អាចមានខ្យល់កន្រាក់";
        } else if (weatherId == 800) {
            return "មេឃស្រលាស់";
        } else if (weatherId == 801) {
            return "ពពកតិច";
        } else if (weatherId >= 802 && weatherId <= 804) {
            return "ពពកច្រើន";
        }
        return null;
    }
}
