package edu.rit.csh.agargiulo.Gatekeeper;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GatekeeperActivity extends FragmentActivity
{

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment
	{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment ()
		{
		}

		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return textView;
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter (FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public int getCount ()
		{
			// Show 3 total pages.
			return 3;
		}

		@Override
		public Fragment getItem (int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle (int position)
		{
			switch(position)
			{
			case 0:
				return getString(R.string.title_section1)
						.toUpperCase(Locale.US);
			case 1:
				return getString(R.string.title_section2)
						.toUpperCase(Locale.US);
			case 2:
				return getString(R.string.title_section3)
						.toUpperCase(Locale.US);
			}
			return null;
		}
	}

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private boolean loggedin = false;

	private void displayAbout ()
	{
		// TODO Auto-generated method stub

	}

	private void login ()
	{
		startActivity(new Intent(this, LoginActivity.class));
		loggedin = true;
	}

	private void logout ()
	{
		loggedin = true;
	}

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.menu_login:
			login();
			return true;
		case R.id.menu_logout:
			logout();
			return true;
		case R.id.menu_about:
			displayAbout();
			return true;
		case R.id.menu_settings:
			openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu)
	{
		boolean result = super.onPrepareOptionsMenu(menu);
		if(loggedin)
		{
			menu.findItem(R.id.menu_login).setVisible(false);
			menu.findItem(R.id.menu_logout).setVisible(true);
		} else
		{
			menu.findItem(R.id.menu_logout).setVisible(false);
			menu.findItem(R.id.menu_login).setVisible(true);
		}
		return result;
	}

	private void openSettings ()
	{
		// TODO Auto-generated method stub

	}

}
