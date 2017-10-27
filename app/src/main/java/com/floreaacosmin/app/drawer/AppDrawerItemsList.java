package com.floreaacosmin.app.drawer;

import com.floreaacosmin.notifier.R;

import java.util.ArrayList;

class AppDrawerItemsList extends ArrayList<AppDrawerItem> {

    AppDrawerItemsList() {

        final String ALL_NOTIFICATIONS = "all notifications";
        final String FACILITY = "facility";
        final String ABOUT = "about";

        add(0, (new AppDrawerItem(ALL_NOTIFICATIONS, R.drawable.icon_full_square,
                R.color.transparent)));
        add(1, (new AppDrawerItem(ABOUT, R.drawable.icon_empty_square,
                R.drawable.icon_selected_symbol)));
        }
}