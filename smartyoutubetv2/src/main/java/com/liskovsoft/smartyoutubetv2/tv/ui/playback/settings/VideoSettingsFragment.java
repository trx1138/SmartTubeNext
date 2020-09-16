package com.liskovsoft.smartyoutubetv2.tv.ui.playback.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.leanback.preference.LeanbackPreferenceFragment;
import androidx.leanback.preference.LeanbackSettingsFragment;
import androidx.preference.DialogPreference;
import androidx.preference.DropDownPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.liskovsoft.sharedutils.helpers.Helpers;
import com.liskovsoft.smartyoutubetv2.common.app.models.playback.controller.OptionItem;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.VideoSettingsPresenter;
import com.liskovsoft.smartyoutubetv2.common.app.views.VideoSettingsView;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.liskovsoft.smartyoutubetv2.tv.ui.old.AuthenticationActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoSettingsFragment extends LeanbackSettingsFragment
        implements DialogPreference.TargetFragment, VideoSettingsView {
    private final static String PREFERENCE_RESOURCE_ID = "preferenceResource";
    private final static String PREFERENCE_ROOT = "root";
    private static final String OPTIONS = "options";
    private PreferenceFragment mPreferenceFragment;
    private VideoSettingsPresenter mSettingsPresenter;
    private List<OptionItem> mItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsPresenter = VideoSettingsPresenter.instance(getActivity());
        mSettingsPresenter.register(this);
        mSettingsPresenter.onInitDone(); // should run before onActivityCreated
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSettingsPresenter.unregister(this);
    }

    @Override
    public void onPreferenceStartInitialScreen() {
        mPreferenceFragment = buildPreferenceFragment(R.xml.settings, null);
        startPreferenceFragment(mPreferenceFragment);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment,
        Preference preference) {
        return false;
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragment preferenceFragment,
        PreferenceScreen preferenceScreen) {
        PreferenceFragment frag = buildPreferenceFragment(R.xml.settings,
            preferenceScreen.getKey());
        startPreferenceFragment(frag);
        return true;
    }

    @Override
    public Preference findPreference(CharSequence charSequence) {
        return mPreferenceFragment.findPreference(charSequence);
    }

    private PreferenceFragment buildPreferenceFragment(int preferenceResId, String root) {
        PreferenceFragment fragment = new PrefFragment();
        Bundle args = new Bundle();
        args.putInt(PREFERENCE_RESOURCE_ID, preferenceResId);
        args.putString(PREFERENCE_ROOT, root);
        args.putStringArrayList(OPTIONS, toArrayList(mItems));
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayList<String> toArrayList(List<OptionItem> items) {
        ArrayList<String> result = new ArrayList<>();

        for (OptionItem item : items) {
            result.add(item.getTitle());
        }

        return result;
    }

    @Override
    public void addCategory(String title, List<OptionItem> items) {
        if (items == null) {
            return;
        }

        mItems = items;
    }

    public static class PrefFragment extends LeanbackPreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            String root = getArguments().getString(PREFERENCE_ROOT, null);
            int prefResId = getArguments().getInt(PREFERENCE_RESOURCE_ID);
            ArrayList<String> options = getArguments().getStringArrayList(OPTIONS);

            // create dialog items form option items here

            //if (root == null) {
            //    addPreferencesFromResource(prefResId);
            //} else {
            //    setPreferencesFromResource(prefResId, root);
            //}

            Context styledContext = (Context) Helpers.getField(this, "mStyledContext");

            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(styledContext);

            //PreferenceCategory category = new PreferenceCategory(styledContext);
            //category.setTitle("Video formats");
            //category.setEnabled(true);
            //screen.addPreference(category);

            ListPreference pref = new ListPreference(styledContext);
            pref.setKey("video_formats");
            pref.setTitle("Video formats");
            pref.setEntries(options.toArray(new CharSequence[0]));
            pref.setEntryValues(options.toArray(new CharSequence[0]));
            screen.addPreference(pref);

            //DropDownPreference pref = new DropDownPreference(styledContext);
            //pref.setKey("video_formats");
            //pref.setTitle("Video formats");
            //pref.setEntries(options.toArray(new CharSequence[0]));
            //pref.setEntryValues(options.toArray(new CharSequence[0]));
            //screen.addPreference(pref);

            setPreferenceScreen(screen);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            if (preference.getKey().equals(getString(R.string.pref_key_login))) {
                // Open an AuthenticationActivity
                startActivity(new Intent(getActivity(), AuthenticationActivity.class));
            }
            return super.onPreferenceTreeClick(preference);
        }

        //private Context getStyledContext() {
        //    final TypedValue tv = new TypedValue();
        //    getActivity().getTheme().resolveAttribute(R.attr.preferenceTheme, tv, true);
        //    int theme = tv.resourceId;
        //    if (theme == 0) {
        //        // Fallback to default theme.
        //        theme = R.style.PreferenceThemeOverlay;
        //    }
        //
        //    return new ContextThemeWrapper(getActivity(), theme);
        //}
    }
}