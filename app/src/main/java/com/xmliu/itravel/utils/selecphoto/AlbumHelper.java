package com.xmliu.itravel.utils.selecphoto;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @date: 2013-12-23 上午9:52:07
 * 
 * @email: tchen@raipeng.com
 * 
 * @version: V1.0
 * 
 * @descontentResolveription:
 * 
 */
public class AlbumHelper {

	private Context context;
	private ContentResolver contentResolver;

	private HashMap<String, String> mThumbnailList = new HashMap<String, String>();
	private HashMap<String, ImageBucket> mBucketList = new HashMap<String, ImageBucket>();

	private static AlbumHelper instance;

	private AlbumHelper() {
	}

	public static AlbumHelper getHelper() {
		if (instance == null) {
			instance = new AlbumHelper();
		}
		return instance;
	}

	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
			this.contentResolver = context.getContentResolver();
		}
	}

	private void getThumbnail() {
		String[] projection = {
				Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA
		};
		Cursor cursor = contentResolver.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
		getThumbnailColumnData(cursor);
	}

	private void getThumbnailColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			int image_id;
			int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cur.getColumnIndex(Thumbnails.DATA);
			String image_path;

			do {
				image_id = cur.getInt(image_idColumn);
				image_path = cur.getString(dataColumn);

				mThumbnailList.put("" + image_id, image_path);
			} while (cur.moveToNext());
		}
	}

	boolean hasBuildImagesBucketList = false;

	private void buildImagesBucketList() {
		getThumbnail();

		String columns[] = new String[] {
				Media._ID, Media.BUCKET_ID, Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE, Media.SIZE,
				Media.BUCKET_DISPLAY_NAME
		};
		Cursor cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
		if (cursor.moveToFirst()) {
			int photoIDIndex = cursor.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cursor.getColumnIndexOrThrow(Media.DATA);
			int bucketDisplayNameIndex = cursor.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cursor.getColumnIndexOrThrow(Media.BUCKET_ID);

			do {
				String _id = cursor.getString(photoIDIndex);
				String path = cursor.getString(photoPathIndex);
				String bucketName = cursor.getString(bucketDisplayNameIndex);
				String bucketId = cursor.getString(bucketIdIndex);

				ImageBucket bucket = mBucketList.get(bucketId);
				if (bucket == null) {
					bucket = new ImageBucket();
					mBucketList.put(bucketId, bucket);
					bucket.bucketList = new ArrayList<ImageItem>();
					bucket.bucketName = bucketName;
				}
				bucket.count++;
				ImageItem imageItem = new ImageItem();
				imageItem.imageId = _id;
				imageItem.imagePath = path;
				imageItem.thumbnailPath = mThumbnailList.get(_id);
				bucket.bucketList.add(imageItem);

			} while (cursor.moveToNext());
		}
		hasBuildImagesBucketList = true;
	}

	public List<ImageBucket> getImagesBucketList(boolean refresh) {
		if (refresh || (!refresh && !hasBuildImagesBucketList)) {
			buildImagesBucketList();
		}
		List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
		Iterator<Entry<String, ImageBucket>> iterator = mBucketList.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) iterator.next();
			tmpList.add(entry.getValue());
		}
		return tmpList;
	}

}
