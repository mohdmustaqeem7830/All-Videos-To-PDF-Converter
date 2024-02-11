package com.allVideosTo.PdfConverter7830;


import android.content.Context;
import android.graphics.BitmapFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PdfGenerator {

    public static Boolean generatePDF(Context context, List<File> imageFiles, String pdfFileName, String folderPath) throws Exception {

        Document document = new Document();
        // Get the dimensions of the first image in the list

        int firstImageWidth = 0;
            int firstImageHeight = 0;
            if (!imageFiles.isEmpty()) {
                File firstImageFile = imageFiles.get(1);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(firstImageFile.getAbsolutePath(), options);
                firstImageWidth = options.outWidth;
                firstImageHeight = options.outHeight;
            }

            // Set the page size of the PDF to match the dimensions of the first image
            document.setPageSize(new Rectangle(firstImageWidth, firstImageHeight));


            String pdfPath = folderPath + File.separator + pdfFileName;
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));


            document.open();
            document.newPage();

            for (int i = 0; i < imageFiles.size(); i++) {
                document.newPage();
                // Convert File to Image and add it to the PDF
                Image img = Image.getInstance(imageFiles.get(i).toURI().toURL());
//            img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                float pageWidth = document.getPageSize().getWidth();
                float pageHeight = document.getPageSize().getHeight();

                float imageWidth = img.getWidth();
                float imageHeight = img.getHeight();


                float widthScale = pageWidth / imageWidth;
                float heightScale = pageHeight / imageHeight;

// Use the minimum of the two scaling factors to maintain the aspect ratio
                float scale = Math.min(widthScale, heightScale);

// Scale the image
                float scaledWidth = imageWidth * scale;
                float scaledHeight = imageHeight * scale;
                img.scaleAbsolute(scaledWidth, scaledHeight);

// Calculate the X-coordinate position to center the image horizontally
                float xPosition = (pageWidth - scaledWidth) / 2;
                img.setAbsolutePosition(xPosition, 0); // The second parameter is the Y-coordinate; you can adjust it as needed


                img.scaleToFit(firstImageWidth, firstImageHeight);
                img.setAlignment(Element.ALIGN_CENTER);
//            img.setAbsolutePosition(firstImageWidth,firstImageHeight);

                document.add(img);
            }

            document.close();
            writer.close();

            // Now, you can safely delete the image files
            for (File imageFile : imageFiles) {
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
            return null;
        }

}
