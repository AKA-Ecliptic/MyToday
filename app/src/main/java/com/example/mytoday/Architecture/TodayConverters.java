package com.example.mytoday.Architecture;

import android.util.Log;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TodayConverters {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    @TypeConverter
    public static Date fromString(String value) {
        if( value != null ){
            try {
                return formatter.parse(value);
            } catch (ParseException e) {
                Log.e("DateParseException", "Error Parsing Today's Date");
            }
        }
        return null;
    }

    @TypeConverter
    public static String dateToString(Date date) {
        return date == null ? null : formatter.format(date);
    }
}
