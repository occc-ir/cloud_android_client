/*
Yaaic - Yet Another Android IRC Client

Copyright 2009-2013 Sebastian Kaspari

This file is part of Yaaic.

Yaaic is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Yaaic is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Yaaic.  If not, see <http://www.gnu.org/licenses/>.
 */
package ir.occc.android.irc.activity;

import ir.occc.android.R;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * Settings
 *
 * @author Sebastian Kaspari <sebastian@yaaic.org>
 */
public class SettingsActivity extends SherlockPreferenceActivity
{
    /**
     * On create
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.preferences);
    }

    /**
     * On menu item selected.
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        if (item.getItemId() ==  android.R.id.home) {
            finish();
        }

        return true;
    }
}
