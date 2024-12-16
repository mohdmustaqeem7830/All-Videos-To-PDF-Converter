package com.allVideosTo.PdfConverter7830;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PdfGenerator {

    public static Boolean generatePDF(Context context, List<File> imageFiles, String pdfFileName) throws Exception {

        Document document = new Document();
        int firstImageWidth = 0;
        int firstImageHeight = 0;

        // Get dimensions of the first image
        if (!imageFiles.isEmpty()) {
            File firstImageFile = imageFiles.get(0);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(firstImageFile.getAbsolutePath(), options);
            firstImageWidth = options.outWidth;
            firstImageHeight = options.outHeight;
        }

        // Set the page size of the PDF to match the first image dimensions
        document.setPageSize(new Rectangle(firstImageWidth, firstImageHeight));

        // Save PDF in the app's external files directory
        File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PDFs");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String pdfPath = new File(folder, pdfFileName).getAbsolutePath();

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

        document.open();

        for (File imageFile : imageFiles) {
            document.newPage();
            Image img = Image.getInstance(imageFile.toURI().toURL());

            // Scale and center the image in the PDF
            float pageWidth = document.getPageSize().getWidth();
            float pageHeight = document.getPageSize().getHeight();

            float imageWidth = img.getWidth();
            float imageHeight = img.getHeight();

            float widthScale = pageWidth / imageWidth;
            float heightScale = pageHeight / imageHeight;
            float scale = Math.min(widthScale, heightScale);

            float scaledWidth = imageWidth * scale;
            float scaledHeight = imageHeight * scale;

            float xPosition = (pageWidth - scaledWidth) / 2;
            img.scaleAbsolute(scaledWidth, scaledHeight);
            img.setAbsolutePosition(xPosition, (pageHeight - scaledHeight) / 2);

            document.add(img);
        }

        document.close();
        writer.close();

        // Optionally delete the image files
        for (File imageFile : imageFiles) {
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }

        return true;
    }
}