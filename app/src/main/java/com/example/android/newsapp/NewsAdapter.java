package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.android.newsapp.R.id.date;
import static com.example.android.newsapp.R.id.section;


/**
 * Created by sarahaldowihy on 9/12/2017 AD.
 */

public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(Context context, ArrayList<News> newses) {
        super(context, 0, newses);
    }

    /**
     * Returns a list item view that display information about the News at given position
     * in the list of News
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View listItemView = convertView;

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
            viewHolder = new ViewHolder(listItemView);
            listItemView.setTag(viewHolder);
        } else {
            /**
             * get tag from given view
             */
            viewHolder = (ViewHolder) listItemView.getTag();

        }
        //Find the news at the given position in the list of news
        News currentNews = getItem(position);
        /**
         * get the values
         */
        String title = currentNews.getTitle();
        String author = currentNews.getAuthor();
        String section = currentNews.getSection();
        //Format the date string (i.e. "May 5,2017")
        String date = formatDate(currentNews.getDate());

        viewHolder.titleHolder.setText(title);
        viewHolder.authorHolder.setText(author);
        viewHolder.dataHolder.setText(date);
        viewHolder.sectionHolder.setText(section);

        //Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string (i.e "May 5,2017")
     */
    private String formatDate(String dateToformat) {
        try {
            // parse date.
            SimpleDateFormat parserDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = parserDate.parse(dateToformat);
            // Set Time Zone to UTC
            TimeZone utc = TimeZone.getTimeZone("UTC");
            //Create the formatted date string (i.e "May 5,2017")
            SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy", Locale.ENGLISH);
            dateFormat.setTimeZone(utc);

            return dateFormat.format(date);
        } catch (ParseException pe) {
            return "Date";
        }
    }

    private class ViewHolder {
        public TextView titleHolder, authorHolder, sectionHolder, dataHolder;

        public ViewHolder(View view) {
            //Find the TextView with view ID title
            titleHolder = view.findViewById(R.id.title);
            //Find the TextView with view ID author
            authorHolder = view.findViewById(R.id.author);
            //Find the TextView with view ID section
            sectionHolder = view.findViewById(section);
            //Find the TextView with view ID date
            dataHolder = view.findViewById(date);
        }
    }

}
