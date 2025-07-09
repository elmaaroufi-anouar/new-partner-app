package com.done.partner.data.services.print.newpas.util;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;

public class BitMapUtil {

    private static final String TAG = "BitMapUtil";
	private static PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    /**
     * 图片去色,返回灰度图片
     *
     * @param bmpOriginal 传入的图片
     * @return 去色后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal, int progress) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        System.out.println("Bitmap " + height);

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        //设置饱和度
        cm.setSaturation((float) (progress / 100.0));
        // 改变亮度
        int brightness = progress - 127;
        cm.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, 0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        //改变对比度
        float contrast = (float) ((progress + 64) / 128.0);
        cm.set(new float[]{contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0, 0, 0, 1, 0});
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * 重画bmp
     */
    public static @Nullable Bitmap remapSizeAndtoGrayscale(Bitmap bitmapOrg, int imgWidth) {
        // 获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        Bitmap bitmap = convertToBlackWhite(bitmapOrg);
        if (bitmap == null) {
            return null;
        }
        saveBitmapforPDF(bitmap);
		Bitmap tempGray = toGrayscale(bitmapOrg,100);
		//bitmapOrg.recycle();
		//计算左右边和底部的空白
		int leftSpaceW = 0;
		int rightSpaceW = 0;
		int bottomSpaceH = 0;
		int pixelColor = 0;

		for(int w = 0; w < (width - 1); w++){
			for(int h = 0;h < (height - 1); h++){
				pixelColor = tempGray.getPixel(w, h);
				if(pixelColor != -1)
				{
					break;
				}
			}
			if(pixelColor != -1)
			{
				pixelColor = 0;
				break;
			}
			else
			{
				leftSpaceW++;
			}
		}

		for(int w = (width - 1); w > 0; w--){
			for(int h = (height - 1);h > 0; h--){
				pixelColor = tempGray.getPixel(w, h);
				if(pixelColor != -1)
				{
					break;
				}
			}
			if(pixelColor != -1)
			{
				pixelColor = 0;
				break;
			}
			else
			{
				rightSpaceW++;
			}
		}

		for(int h = (height - 1);h > 0; h--){
			for(int w = (width - 1); w > 0; w--){
				pixelColor = tempGray.getPixel(w,h);
				if(pixelColor != -1)
				{
					break;
				}
			}
			if(pixelColor != -1)
			{
				pixelColor = 0;
				break;
			}
			else
			{
				bottomSpaceH++;
			}
		}
		//左右边各保留一个单位
        rightSpaceW = rightSpaceW > 0 ? rightSpaceW - 1 : rightSpaceW;
        leftSpaceW = leftSpaceW > 0 ? leftSpaceW - 1 : leftSpaceW;
		//裁剪图片的左右边和底部的空白
		int adjustW = 10;
		int cropWidth = width - rightSpaceW - leftSpaceW;
		if((imgWidth - cropWidth) >= adjustW){
			cropWidth += adjustW;
			if(leftSpaceW >= (adjustW/2)){
				leftSpaceW -= (adjustW/2);
			}
		}

		int cropHeight = height - bottomSpaceH;
        Bitmap cropBmp = Bitmap.createBitmap(tempGray,leftSpaceW,0,cropWidth,cropHeight,null,false);
		tempGray.recycle();
		//Log.e(TAG,"imgWidth:"+imgWidth + "  cropWidth:" + cropWidth +  "   width:" + width);
		//Log.e(TAG,"leftSpaceW:"+leftSpaceW + "  cropHeight:" + cropHeight +  "   height:" + height);
        float scaleWidth = ((float)imgWidth) / cropWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
//		matrix.postScale((float)1.0, (float)1.0);

        // 定义预转换成的图片的宽度和高度
       tempGray = sharpenImageAmeliorate1(Bitmap.createBitmap(cropBmp, 0, 0, cropWidth, cropHeight, matrix, true));//toGrayscale(Bitmap.createBitmap(cropBmp, 0, 0, cropWidth, cropHeight, matrix, true),100);
        Bitmap targetBmp = Bitmap.createBitmap(tempGray,1,1,tempGray.getWidth()-2,tempGray.getHeight()-2,null,false);
//	   Bitmap targetBmp = scaleImageCavans(1.0f, cropBmp, cropWidth, cropHeight);
//        saveBitmapforPDF(sharpenImageAmeliorate(targetBmp));
        tempGray.recycle();
	   cropBmp.recycle();
       return targetBmp;
    }

	public synchronized static Bitmap scaleImageCavans(float iconScaleRadio, Bitmap bm, int newWidth, int newHeight) {
        Canvas mCanvas = new Canvas();
        if(iconScaleRadio!=1f){
            newWidth= (int) (newWidth*iconScaleRadio);
            newHeight= (int) (newHeight*iconScaleRadio);
        }
        if (bm == null) {
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Bitmap newbm = Bitmap.createBitmap(newWidth, newHeight,
                Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(newbm);
        mCanvas.save();
        mCanvas.scale(scaleWidth, scaleHeight);
        //保证图标不失真
        mCanvas.setDrawFilter(pfd);
        mCanvas.drawBitmap(bm,0,0,null);
        mCanvas.restore();
        if (bm != null & !bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }
        return newbm;
    }

    /**
     *  获取位图点阵数据
     * @param bmp     位图
     * @param width   位图宽 0-384
     * @param bmpMode
     * @return
     */
    public static @Nullable byte[] getBitmapPrintData(Bitmap bmp , int width, int bmpMode){
        bmp = remapSizeAndtoGrayscale(bmp,width);
        if (bmp == null) {
            return null;
        }
        int bmpNewWidth = bmp.getWidth();
        int bmpNewHeight = bmp.getHeight();

        byte[] printBMPPackageHead = ESCUtil.bmpCmdHead(bmpMode,bmpNewWidth);

        int bmpBlockHeight = 0;
        int bmpBlockNums =0;
        if((bmpMode == 0) || (bmpMode ==1))
        {
            bmpBlockHeight = 8;
        }
        else if((bmpMode == 32) || (bmpMode ==33))
        {
            bmpBlockHeight = 24;
        }
        else
        {
            Log.d(TAG,"****bmpMode set error!!*****");
            return (new byte[1]);
        }
        bmpBlockNums = ((bmpNewHeight % bmpBlockHeight) == 0)? (bmpNewHeight/bmpBlockHeight) : (bmpNewHeight/bmpBlockHeight +1);
        int bmpBlockCMDSize = printBMPPackageHead.length + bmpNewWidth*bmpBlockHeight/8;
        byte[] bmpPrintData = new byte[bmpBlockNums*bmpBlockCMDSize];
        for(int n = 0; n < bmpBlockNums; n++)
        {
            byte[] bmpBlockPxBytes = getBitmapBlockData(n,bmpNewWidth,bmpBlockHeight,bmp);

            byte[][] bmpBlockPrintData = {printBMPPackageHead,bmpBlockPxBytes};
                System.arraycopy(ESCUtil.byteMerger(bmpBlockPrintData),0,bmpPrintData,n*bmpBlockCMDSize,bmpBlockCMDSize);
        }

        if (bmp != null && !bmp.isRecycled())
        {
            bmp.recycle();
        }

        return bmpPrintData;
    }

    public static void saveBitmapforPDF(Bitmap bitmap) {
//        Bitmap dstBitmap = remapSizeAndtoGrayscale(bitmap, 380);
        Bitmap dstBitmap = bitmap;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_pdf.png";
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(new File(path));
            dstBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.getMessage();
        } finally {
            if(dstBitmap != null && !dstBitmap.isRecycled()){
                dstBitmap.recycle();
                dstBitmap = null;
            }
        }
    }

    /**
     * 将彩色图转换为黑白图
     *
     * @param
     * @return 返回转换好的位图
     */
    public static @Nullable Bitmap convertToBlackWhite(Bitmap bmp) {
        try {
            int width = bmp.getWidth(); // 获取位图的宽
            int height = bmp.getHeight(); // 获取位图的高
            int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            int alpha = 0xFF << 24;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int grey = pixels[width * i + j];

                    int red = ((grey & 0x00FF0000) >> 16);
                    int green = ((grey & 0x0000FF00) >> 8);
                    int blue = (grey & 0x000000FF);

                    grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                    grey = alpha | (grey << 16) | (grey << 8) | grey;
                    pixels[width * i + j] = grey;
                }
            }
            Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
            return newBmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 图片锐化（拉普拉斯变换）
     *
     * @return
     */
    public static Bitmap sharpenImageAmeliorate1(Bitmap bmp)
    {
        long start = System.currentTimeMillis();
        // 拉普拉斯矩阵
        int[] laplacian = new int[]{-1, -1, -1, -1, 9, -1, -1, -1, -1};
        //        int[] laplacian = new int[]{0, -1, 0, -1, 5, -1, 0, -1, 0};
        //        int[] laplacian = new int[]{1, -2, 1, -2, 5, -2, 1, -2, 1};
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int idx = 0;
        float alpha = 1F;
        //原图像素点数组
        int[] pixels = new int[width*height];
        //创建一个新数据保存锐化后的像素点
        int[] pixels_1 = new int[width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i = 1, length = height-1; i<length; i++)
        {
            for(int k = 1, len = width-1; k<len; k++)
            {
                idx = 0;
                for(int m = -1; m<=1; m++)
                {
                    for(int n = -1; n<=1; n++)
                    {
                        pixColor = pixels[( i+n )*width+k+m];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR+(int)( pixR*laplacian[idx]*alpha );
                        newG = newG+(int)( pixG*laplacian[idx]*alpha );
                        newB = newB+(int)( pixB*laplacian[idx]*alpha );
                        idx++;
                    }
                }

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels_1[i*width+k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        bitmap.setPixels(pixels_1, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        Log.d("may", "used time="+( end-start ));
        return bitmap;
    }

    /**
     * 图片锐化（拉普拉斯变换）
     * @param bmp
     * @return
     */
    public static Bitmap sharpenImageAmeliorate(Bitmap bmp)
    {
        long start = System.currentTimeMillis();
        // 拉普拉斯矩阵
        int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };
//                int[] laplacian = new int[]{0, -1, 0, -1, 5, -1, 0, -1, 0};
        //        int[] laplacian = new int[]{1, -2, 1, -2, 5, -2, 1, -2, 1};

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int idx = 0;
        float alpha = 1F;   //图片透明度
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++)
        {
            for (int k = 1, len = width - 1; k < len; k++)
            {
                idx = 0;
                for (int m = -1; m <= 1; m++)
                {
                    for (int n = -1; n <= 1; n++)
                    {
                        pixColor = pixels[(i + n) * width + k + m];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR + (int) (pixR * laplacian[idx] * alpha);
                        newG = newG + (int) (pixG * laplacian[idx] * alpha);
                        newB = newB + (int) (pixB * laplacian[idx] * alpha);
                        idx++;
                    }
                }

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        Log.d("may", "used time="+(end - start));
        return bitmap;
    }

    // 图片素描效果
    public static Bitmap sketch(Bitmap bmp) {
        // 创建新Bitmap
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height]; // 存储变换图像
        int[] linpix = new int[width * height]; // 存储灰度图像
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int pixColor = 0;
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        // 灰度图像
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {  // 拉普拉斯算子模板 { 0, -1, 0, -1, -5, -1, 0, -1, 0
                // 获取前一个像素颜色
                pixColor = pixels[width * j + i];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                // 灰度图像
                int gray = (int) (0.3 * pixR + 0.59 * pixG + 0.11 * pixB);
                linpix[width * j + i] = Color.argb(255, gray, gray, gray);
                // 图像反向
                gray = 255 - gray;
                pixels[width * j + i] = Color.argb(255, gray, gray, gray);
            }
        }
        int radius = Math.min(width / 2, height / 2);
        int[] copixels = gaussBlur(pixels, width, height, 10, 10 / 3); // 高斯模糊
        // 采用半径10
        int[] result = colorDodge(linpix, copixels); // 素描图像 颜色减淡
        bitmap.setPixels(result, 0, width, 0, 0, width, height);
        return bitmap;
    }

    // 高斯模糊
    public static int[] gaussBlur(int[] data, int width, int height, int radius, float sigma) {
        float pa = (float) (1 / (Math.sqrt(2 * Math.PI) * sigma));
        float pb = -1.0f / (2 * sigma * sigma);

        // generate the Gauss Matrix
        float[] gaussMatrix = new float[radius * 2 + 1];
        float gaussSum = 0f;
        for (int i = 0, x = -radius; x <= radius; ++x, ++i) {
            float g = (float) (pa * Math.exp(pb * x * x));
            gaussMatrix[i] = g;
            gaussSum += g;
        }
        for (int i = 0, length = gaussMatrix.length; i < length; ++i) {
            gaussMatrix[i] /= gaussSum;
        }

        // x direction
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                float r = 0, g = 0, b = 0;
                gaussSum = 0;
                for (int j = -radius; j <= radius; ++j) {
                    int k = x + j;
                    if (k >= 0 && k < width) {
                        int index = y * width + k;
                        int color = data[index];
                        int cr = (color & 0x00ff0000) >> 16;
                        int cg = (color & 0x0000ff00) >> 8;
                        int cb = (color & 0x000000ff);

                        r += cr * gaussMatrix[j + radius];
                        g += cg * gaussMatrix[j + radius];
                        b += cb * gaussMatrix[j + radius];
                        gaussSum += gaussMatrix[j + radius];
                    }
                }

                int index = y * width + x;
                int cr = (int) (r / gaussSum);
                int cg = (int) (g / gaussSum);
                int cb = (int) (b / gaussSum);
                data[index] = cr << 16 | cg << 8 | cb | 0xff000000;
            }
        }

        // y direction
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                float r = 0, g = 0, b = 0;
                gaussSum = 0;
                for (int j = -radius; j <= radius; ++j) {
                    int k = y + j;
                    if (k >= 0 && k < height) {
                        int index = k * width + x;
                        int color = data[index];
                        int cr = (color & 0x00ff0000) >> 16;
                        int cg = (color & 0x0000ff00) >> 8;
                        int cb = (color & 0x000000ff);

                        r += cr * gaussMatrix[j + radius];
                        g += cg * gaussMatrix[j + radius];
                        b += cb * gaussMatrix[j + radius];
                        gaussSum += gaussMatrix[j + radius];
                    }
                }

                int index = y * width + x;
                int cr = (int) (r / gaussSum);
                int cg = (int) (g / gaussSum);
                int cb = (int) (b / gaussSum);
                data[index] = cr << 16 | cg << 8 | cb | 0xff000000;
            }
        }
        return data;
    }

    // 颜色减淡
    public static int[] colorDodge(int[] baseColor, int[] mixColor) {
        for (int i = 0, length = baseColor.length; i < length; ++i) {
            int bColor = baseColor[i];
            int br = (bColor & 0x00ff0000) >> 16;
            int bg = (bColor & 0x0000ff00) >> 8;
            int bb = (bColor & 0x000000ff);

            int mColor = mixColor[i];
            int mr = (mColor & 0x00ff0000) >> 16;
            int mg = (mColor & 0x0000ff00) >> 8;
            int mb = (mColor & 0x000000ff);

            int nr = colorDodgeFormular(br, mr);
            int ng = colorDodgeFormular(bg, mg);
            int nb = colorDodgeFormular(bb, mb);
            baseColor[i] = nr << 16 | ng << 8 | nb | 0xff000000;
        }
        return baseColor;
    }

    private static int colorDodgeFormular(int base, int mix) {
        int result = base + (base * mix) / (255 - mix);
        result = result > 255 ? 255 : result;
        return result;
    }

    /**
     * 获取块数据
     */
    public static byte[] getBitmapBlockData(int blocknum,int bmpWidth,int bmpBlockHeight,Bitmap bmp)
    {
        int blockHeightBytes = bmpBlockHeight/8;
        byte[] blockData = new byte[bmpWidth*blockHeightBytes];
        for (int i = 0;i < bmpWidth; i++)
        {
            for(int j = 0;j < blockHeightBytes;j++)
            {
                for(int p = 0; p < 8; p++)
                {
                    byte px = px2Byte(i,blocknum * bmpBlockHeight+j*8+p,bmp);
                    blockData[i*blockHeightBytes+j] |= (px << (7-p));
                }
            }
        }
        return  blockData;
    }

    /**
     * 图片二值化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param bmp 位图
     * @return
     */
    private static byte px2Byte(int x, int y, Bitmap bmp) {
        if (x < bmp.getWidth() && y < bmp.getHeight()) {
            byte b;
            int pixelColor = bmp.getPixel(x, y);
            if (pixelColor != -1) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     */
    private static int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // 灰度转化公式
        return gray;
    }
}

