package com.floreaacosmin.app.drawer;

import com.floreaacosmin.notifier.R;

import java.util.ArrayList;

class AppDrawerItemsList extends ArrayList<AppDrawerItem> {

    public AppDrawerItemsList() {

        final String ALL_NOTIFICATIONS = "all notifications";
        final String FACILITY = "facility";
        final String ABOUT = "about";

        add(0, (new AppDrawerItem(ALL_NOTIFICATIONS, R.drawable.icon_all_articles,
                R.color.transparent)));
        add(1, (new AppDrawerItem(FACILITY, R.drawable.icon_selected_articles,
                R.drawable.icon_selected_symbol)));
        add(2, (new AppDrawerItem(ABOUT, R.drawable.icon_about,
                R.color.transparent)));
        }
}