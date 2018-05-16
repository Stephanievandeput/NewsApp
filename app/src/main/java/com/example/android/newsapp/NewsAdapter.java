package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/* NewsAdapter is an ArrayAdapter that can provide the layout for each list item
 * based on a data source, which is here a list of NewsArticle objects
 */
public class NewsAdapter extends ArrayAdapter<NewsArticle> {

    //String Separator to split the time from the date of publication of the news article
    private static final String LOCATION_SEPARATOR = "T";

    /*
     * Create a new NewsAdapter object
     */
    public NewsAdapter(Context context, ArrayList<NewsArticle> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        //Get the NewsArticle object located at this position in the list
        NewsArticle currentNewsArticle = getItem(position);

        //Find the TextView in the list_item.xml layout with the ID title_text_view
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);

        //Get the title of the currentNewsArticle object and set this text in the titleTextView
        titleTextView.setText(currentNewsArticle.getTitle());

        //Find the TextView in the list_item.xml layout with the ID section_text_view
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_text_view);

        //Get the section of the currentNewsArticle object and set this text in the sectionTextView
        sectionTextView.setText(currentNewsArticle.getSection());

        //Get the original String with the date + time of publication
        String originalDate = currentNewsArticle.getDate();

        //Split the String originalDate in two parts
        String[] parts = originalDate.split(LOCATION_SEPARATOR);

        //String to store just the date of publication in
        String date = parts[0];

        //Find the TextView in the list_item.xml layout with the ID date_text_view
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);

        //Set the date of publication in the dateTextView
        dateTextView.setText(date);

        //Find the TextView in the list_item.xml layout with the ID author_text_view
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_text_view);

        //Get the author of the currentNewsArticle object and set this text in the authorTextView
        authorTextView.setText(currentNewsArticle.getAuthor());

        return listItemView;
    }
}

