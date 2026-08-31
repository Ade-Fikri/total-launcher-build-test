package com.ss.launcher2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public abstract class ThemeProvider extends ContentProvider {
	
	public static final String FOLDER_IMAGES = "images";
	public static final String FOLDER_DYNAMIC_IMAGES = "dynamicImages";
	public static final String FOLDER_SHAPES = "shapes";
	public static final String FOLDER_SHADOWS = "shadows";
	public static final String FOLDER_FONTS = "fonts";
	public static final String FOLDER_SOUNDS = "sounds";
	public static final String FOLDER_PAGES = "pages";
	public static final String FOLDER_WINDOWS = "wnds";
	
	private static final String EXTRA_PIRATED = "com.ss.launcher.ThemeProvider.EXTRA_PIRATED";
	private static final String EXTRA_THEME_IDS = "com.ss.launcher.ThemeProvider.EXTRA_THEME_IDS";
	private static final String EXTRA_THEME_NAME = "com.ss.launcher.ThemeProvider.EXTRA_THEME_NAME";
	private static final String EXTRA_THEME_URI = "com.ss.launcher.ThemeProvider.EXTRA_THEME_URI";
	private static final String EXTRA_HAS_RESOURCES = "com.ss.launcher.ThemeProvider.EXTRA_HAS_RESOURCES";
	private static final String EXTRA_RESOURCES = "com.ss.launcher.ThemeProvider.EXTRA_RESOURCES";
	private static final String EXTRA_IN_ASSETS = "com.ss.launcher.ThemeProvider.EXTRA_IN_ASSETS";
	private static final String EXTRA_FONT_PATH = "com.ss.launcher.ThemeProvider.EXTRA_FONT_PATH";

	/**
	 * 
	 * @return Ids for themes supported by this package
	 */
	protected abstract String[] getThemeIds();

	/**
	 * 
	 * @param themeId
	 * @return Null must be returned when themeId is invalid no more.
	 */
	protected abstract String getThemeDisplayName(String themeId);
	
	/**
	 * 
	 * @param themeId
	 * @return A valid uri string to access to the theme for the given themeId. This will be used like "[theme uri]/[folder]" to access the resources.
	 */
	protected abstract String getThemeUriString(String themeId);

	/**
	 * This must return the result very quickly. Do not call AssetManager.list(String) to check if the folder is empty!
	 * 
	 * @param themeId
	 * @param folder - FOLDER_IMAGES, FOLDER_DYNAMIC_IMAGES, FOLDER_SHAPES or FOLDER_FONTS
	 * @return True if the resources folder is not empty.
	 */
	protected abstract boolean hasResources(String themeId, String folder);

	/**
	 * 
	 * @param themeId
	 * @param folder - FOLDER_IMAGES, FOLDER_DYNAMIC_IMAGES, FOLDER_SHAPES or FOLDER_FONTS
	 * @return All filenames in the resource folder.
	 */
	protected abstract String[] getResources(String themeId, String folder);

	/**
	 * This is called to check the font files is in assets. If the returned value is false, you must override getFontPath(String, String) to support correct full path of the font file.
	 * 
	 * @param themeId
	 * @return True if the font file is in assets.
	 */
	protected abstract boolean isFontInAssets(String themeId, String fontName);

	/**
	 * 
	 * @param themeId
	 * @param fontName
	 * @return Sub path of the font file in assets when true is returned by isFontInAssets(), or full path when the font is not in assets.
	 */
	protected String getFontPath(String themeId, String fontName) {
		return new StringBuffer(themeId).append("/fonts/").append(fontName).toString();
	}

	@Override
	public Bundle call(String method, String arg, Bundle extras) {
		Bundle bundle = new Bundle();
		if (this.isPiracyFound()) {
			bundle.putBoolean(EXTRA_PIRATED, true);
			return bundle;
		}
		if (method.equals("getThemeIds")) {
			bundle.putStringArray(EXTRA_THEME_IDS, this.getThemeIds());
		} else if (method.equals("getThemeDisplayName")) {
			bundle.putString(EXTRA_THEME_NAME, this.getThemeDisplayName(arg));
		} else if (method.equals("getThemeUriString")) {
			bundle.putString(EXTRA_THEME_URI, this.getThemeUriString(arg));
		} else if (method.equals("hasResources")) {
			bundle.putBoolean(EXTRA_HAS_RESOURCES, this.hasResources(arg, extras.getString("folder")));
		} else if (method.equals("getResources")) {
			bundle.putStringArray(EXTRA_RESOURCES, this.getResources(arg, extras.getString("folder")));
		} else if (method.equals("isFontInAssets")) {
			bundle.putBoolean(EXTRA_IN_ASSETS, this.isFontInAssets(arg, extras.getString("filename")));
		} else if (method.equals("getFontPath")) {
			bundle.putString(EXTRA_FONT_PATH, this.getFontPath(arg, extras.getString("filename")));
		}
		return bundle;
	}

	private boolean isPiracyFound() {
		return false;
	}
	
	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
