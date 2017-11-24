package com.floreaacosmin.app.drawer;

import com.floreaacosmin.notifier.R;

import java.util.ArrayList;

class AppDrawerItemsList extends ArrayList<AppDrawerItem> {

    AppDrawerItemsList() {

        final String ALL_NOTIFICATIONS = "all notifications";
        final String ABOUT = "about";

        add(size(), (new AppDrawerItem(ALL_NOTIFICATIONS, R.drawable.icon_full_square,
                R.color.transparent)));
        add(size(), (new AppDrawerItem(ABOUT, R.drawable.icon_empty_square,
                R.drawable.icon_about_symbol)));
        }
}