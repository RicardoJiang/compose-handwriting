package com.zj.compose.handwriting;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

public class BitmapUtils {
    /**
     * 保存图片到图库
     *
     * @param bmp
     * @param bitName
     */
    public static String saveBitmapToGallery(Context context, Bitmap bmp, String bitName) {
        //插入到系统图库
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "菜单", bitName);
    }

}
