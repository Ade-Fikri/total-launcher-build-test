/*
 * Copyright (C) 2015 ChYK
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.theme.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.ss.launcher2.ThemeProvider;

public class MyThemeProvider extends ThemeProvider {
	
	/**
	 * The list of theme ids in this package.
	 */
	public static final String[] THEME_IDS = { "sample" };

	/**
	 * 
	 * @return Ids for themes supported by this package
	 */
	@Override
	protected String[] getThemeIds() {
		return THEME_IDS;
	}

	/**
	 * 
	 * @param themeId
	 * @return Null must be returned when themeId is invalid no more.
	 */
	@Override
	protected String getThemeDisplayName(String themeId) {
		return this.getContext().getString(R.string.label);
	}

	/**
	 * This is called to get the base URI for a themeId. You can return different URIs for one themeId according to locale or screen resolutions.
	 *  
	 * @param themeId
	 * @return A valid uri string to access to the theme for the given themeId. This will be used like "[theme uri]/[folder]" to access the resources.
	 */
	@Override
	protected String getThemeUriString(String themeId) {
		return this.getBaseUri() + themeId;
	}
	
	private String getBaseUri() {
		return "content://" + this.getContext().getPackageName() + "/";
	}

	/**
	 * This must return the result very quickly. Do not call AssetManager.list(String) to check if the folder is empty!
	 * 
	 * @param themeId
	 * @param folder - FOLDER_IMAGES, FOLDER_DYNAMIC_IMAGES, FOLDER_SHAPES, FOLDER_SHADOWS, FOLDER_FONTS or FOLDER_SOUNDS
	 * @return True if the resources folder is not empty.
	 */
	@Override
	protected boolean hasResources(String themeId, String folder) {
		if (folder.equals(FOLDER_DYNAMIC_IMAGES) ||
				folder.equals(FOLDER_FONTS) || folder.equals(FOLDER_SOUNDS)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param themeId
	 * @param folder - FOLDER_IMAGES, FOLDER_DYNAMIC_IMAGES, FOLDER_SHAPES, FOLDER_SHADOWS, FOLDER_FONTS or FOLDER_SOUNDS
	 * @return All filenames in the resource folder.
	 */
	@Override
	protected String[] getResources(String themeId, String folder) {
		try {
			return this.getContext().getAssets().list(themeId + "/" + folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This is called to check the font files is in assets. If the returned value is false, you must override getFontPath(String, String) to support correct full path of the font file.
	 * 
	 * @param themeId
	 * @return True if the font file is in assets.
	 */
	@Override
	protected boolean isFontInAssets(String themeId, String fontName) {
		return true;
	}

	/**
	 * 
	 * @param themeId
	 * @param fontName
	 * @return Sub path of the font file in assets when true is returned by isFontInAssets(), or full path when the font is not in assets.
	 */
	@Override
	protected String getFontPath(String themeId, String fontName) {
		return super.getFontPath(themeId, fontName);
	}

	@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode)
			throws FileNotFoundException {
		String assetPath = uri.toString().substring(this.getBaseUri().length());
		try {
			return this.getContext().getAssets().openFd(assetPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			final long lastUpdateTime = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).lastUpdateTime;
			File cacheFile = new File(getContext().getCacheDir() + File.separator + assetPath);
			if (!cacheFile.exists() || cacheFile.lastModified() < lastUpdateTime) {
				cacheFile.getParentFile().mkdirs();
				this.copyToCacheFile(assetPath, cacheFile);
				if (cacheFile.exists()) {
					cacheFile.setLastModified(System.currentTimeMillis());
				}
			}
			return new AssetFileDescriptor(ParcelFileDescriptor.open(cacheFile, ParcelFileDescriptor.MODE_READ_ONLY), 0, -1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return super.openAssetFile(uri, mode);
	}
	
	private void copyToCacheFile(final String assetPath, final File cacheFile)
			throws IOException {
		final InputStream inputStream = getContext().getAssets().open(assetPath, AssetManager.ACCESS_BUFFER);
		final FileOutputStream fileOutputStream = new FileOutputStream(cacheFile, false);
		try {
			copy(inputStream, fileOutputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void copy(InputStream in, OutputStream out) throws Exception {
		try {
			byte[] buf = new byte[1024];
			int size;
			while ((size = in.read(buf)) > -1) {
				out.write(buf, 0, size);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}
	}

}
